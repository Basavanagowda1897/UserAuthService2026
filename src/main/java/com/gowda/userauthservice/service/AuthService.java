package com.gowda.userauthservice.service;

import com.gowda.userauthservice.exceptions.IncorrectPasswordException;
import com.gowda.userauthservice.models.Role;
import com.gowda.userauthservice.models.State;
import com.gowda.userauthservice.models.User;
import com.gowda.userauthservice.repositories.RoleRepo;
import com.gowda.userauthservice.repositories.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AuthService implements IAuthService{

    @Autowired
    private UserRepo userRepo;
    @Autowired
    private RoleRepo roleRepo;

    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public User singUp(String name, String email, String password) {
        Optional<User> OptionalUser = userRepo.findByEmail(email);

        if(OptionalUser.isPresent()) {
            throw new RuntimeException("User with email " + email + " already exists");
        }

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(bCryptPasswordEncoder.encode(password));
        //user.setPassword(password);
        user.setCreatedAt(System.currentTimeMillis());
        user.setUpdatedAt(System.currentTimeMillis());
        user.setState(State.ACTIVE);
        Role role = null;
        Optional<Role> OptionalRole = roleRepo.findByValue("USER");

        if(OptionalUser.isEmpty()) {
            role = new Role();
            role.setValue("USER");
            role.setCreatedAt(System.currentTimeMillis());
            role.setUpdatedAt(System.currentTimeMillis());
            role.setState(State.ACTIVE);
            roleRepo.save(role);
        }else{
            role = OptionalRole.get();
        }
        List<Role> roles = new ArrayList<>();
        roles.add(role);
        user.setRoles(roles);
        return userRepo.save(user);
    }

    @Override
    public User login(String email, String password) {
        Optional<User> OptionalUser = userRepo.findByEmail(email);

        if(OptionalUser.isEmpty()) {
            throw new RuntimeException("User with email " + email + " does not exist");
        }

        User user = OptionalUser.get();

        if(!bCryptPasswordEncoder.matches(password, user.getPassword())) {
            throw new IncorrectPasswordException("Invalid password");
        }

        return user;
    }
}

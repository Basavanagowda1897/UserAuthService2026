package com.gowda.userauthservice.service;

import com.gowda.userauthservice.dtos.UserToken;
import com.gowda.userauthservice.exceptions.IncorrectPasswordException;
import com.gowda.userauthservice.models.Role;
import com.gowda.userauthservice.models.Session;
import com.gowda.userauthservice.models.State;
import com.gowda.userauthservice.models.User;
import com.gowda.userauthservice.repositories.RoleRepo;
import com.gowda.userauthservice.repositories.SessionRepo;
import com.gowda.userauthservice.repositories.UserRepo;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.MacAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.*;

@Service
public class AuthService implements IAuthService{

    @Autowired
    private UserRepo userRepo;
    @Autowired
    private RoleRepo roleRepo;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private SessionRepo sessionRepo;
    @Autowired
    private SecretKey secretKey;



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
    public UserToken login(String email, String password) {
        Optional<User> OptionalUser = userRepo.findByEmail(email);

        if(OptionalUser.isEmpty()) {
            throw new RuntimeException("User with email " + email + " does not exist");
        }

        User user = OptionalUser.get();

        if(!bCryptPasswordEncoder.matches(password, user.getPassword())) {
            throw new IncorrectPasswordException("Invalid password");
        }


        Map<String,Object> payload = new HashMap<>();
        Long nowInMillis = System.currentTimeMillis(); // gets us timestamp in epoch
        System.out.println("nowInMillis = " + nowInMillis);
        payload.put("iat",nowInMillis); //Jan 16 58392
        payload.put("exp",nowInMillis + 100000); //100 seconds
        payload.put("userId",user.getId());
        payload.put("iss","scaler");
        payload.put("scope",user.getRoles());

        MacAlgorithm macAlgorithm = Jwts.SIG.HS256;
        SecretKey secretKey = macAlgorithm.key().build();

        String token = Jwts.builder()
                .setClaims(payload)
                .signWith(secretKey)
                .compact();

        Session session = new Session();
        session.setToken(token);
        session.setUser(user);
        session.setState(State.ACTIVE);
        sessionRepo.save(session);

        return new UserToken(user, token);
    }

    public Boolean validateToken(String token) {
        /*
        Optional step to check if token is in the db
         */

        Optional<Session> optionalSession = sessionRepo.findByToken(token);

        if(optionalSession.isEmpty()){
            System.out.println("Token not found in the database");
            return false;
        }

        /*
        Parsing : If a signature is tampered with, the parser will throw an exception

        token = a.b.c was generated using a secret key
        algo(a,b,secret key) = c (token) prevToken
        Parse this token
        If you want to generate the token again, will you need the same secret key?
        extract a, b, c (originalToken)
        algo(a,b,secret key) = c1 (token) generatedNow
        if match , then return true
         */


        JwtParser jwtParser = Jwts.parser().verifyWith(secretKey).build();
        Claims claims = jwtParser.parseSignedClaims(token).getPayload();

        Long tokenExpiry = (Long) claims.get("exp");
        Long currentTime = System.currentTimeMillis();


        System.out.println("tokenExpiry = " + tokenExpiry);


        if(currentTime > tokenExpiry){
            return false;
        }else{
            return true;
        }
    }
}
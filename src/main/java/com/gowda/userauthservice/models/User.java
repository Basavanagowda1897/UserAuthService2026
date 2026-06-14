package com.gowda.userauthservice.models;

import com.gowda.userauthservice.dtos.UserDto;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;

import java.util.List;

@Entity
public class User extends BaseModel {
    private String name;
    private String email;
    private String password;
    private String phone;
    @ManyToMany
    private List<Role> roles;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }
    public UserDto toUserDto(){
        UserDto userDto = new UserDto();
        userDto.setId(this.getId());
        userDto.setName(this.getName());
        userDto.setEmail(this.getEmail());
       // userDto.setPhone(this.getPhone());
        userDto.setRoles(this.getRoles());
        return userDto;
    }
}

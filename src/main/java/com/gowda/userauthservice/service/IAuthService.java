package com.gowda.userauthservice.service;

import com.gowda.userauthservice.models.User;

public interface IAuthService {
    User singUp(String name, String email, String password);
    User login(String email, String password);
}

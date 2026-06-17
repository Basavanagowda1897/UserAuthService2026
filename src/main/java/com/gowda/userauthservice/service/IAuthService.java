package com.gowda.userauthservice.service;

import com.gowda.userauthservice.dtos.UserToken;
import com.gowda.userauthservice.models.User;
import org.springframework.data.util.Pair;

public interface IAuthService {
    User singUp(String name, String email, String password);
     UserToken login(String email, String password);

     Boolean validateToken(String token);
}

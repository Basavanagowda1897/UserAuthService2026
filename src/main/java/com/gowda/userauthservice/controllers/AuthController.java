package com.gowda.userauthservice.controllers;

import com.gowda.userauthservice.dtos.LoginRequestDto;
import com.gowda.userauthservice.dtos.SignUpRequestDto;
import com.gowda.userauthservice.dtos.UserDto;
import com.gowda.userauthservice.models.User;
import com.gowda.userauthservice.service.IAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private IAuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<UserDto> signup(@RequestBody SignUpRequestDto signUpRequestDto){
        try{
            User user =authService.singUp(signUpRequestDto.getName(), signUpRequestDto.getEmail(), signUpRequestDto.getPassword());
            UserDto userDto = user.toUserDto();
            return new ResponseEntity<>(userDto, HttpStatus.OK);
        } catch (Exception e) {
            throw e;
        }
    }

    @PostMapping("/login")
    public ResponseEntity<UserDto> login(@RequestBody LoginRequestDto loginRequestDto) {
        try {
            User user = authService.login(loginRequestDto.getEmail(), loginRequestDto.getPassword());
            UserDto userDto = user.toUserDto();
            return new ResponseEntity<>(userDto, HttpStatus.OK);
        } catch (Exception e) {
            throw e;
        }
    }
}

package com.gowda.userauthservice.controllers;

import com.gowda.userauthservice.dtos.LoginRequestDto;
import com.gowda.userauthservice.dtos.SignUpRequestDto;
import com.gowda.userauthservice.dtos.UserDto;
import com.gowda.userauthservice.dtos.UserToken;
import com.gowda.userauthservice.exceptions.UnauthorizedException;
import com.gowda.userauthservice.models.User;
import com.gowda.userauthservice.service.IAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
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
            UserToken userToken = authService.login(loginRequestDto.getEmail(), loginRequestDto.getPassword());
            UserDto userDto = userToken.getUser().toUserDto();
            String token = userToken.getToken();

            MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
            headers.add(HttpHeaders.SET_COOKIE, token);

            HttpHeaders httpHeaders = new HttpHeaders(headers);

            return new ResponseEntity<>(userDto, httpHeaders, HttpStatus.OK);
        } catch (Exception e) {
            throw e;
        }
    }

    @PostMapping("/validate-token")
    public void validateToken(@RequestBody String token){
        Boolean res = authService.validateToken(token);
        if(!res){
            throw new UnauthorizedException("Invalid token");
        }
    }
}

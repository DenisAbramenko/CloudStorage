package com.project.cloudstorage.controller;

import com.project.cloudstorage.dto.JwtAuthenticationResponse;
import com.project.cloudstorage.dto.SignInRequest;
import com.project.cloudstorage.dto.SignUpRequest;
import com.project.cloudstorage.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
@Tag(name = "Аутентификация")
public class UserController {
    private final AuthenticationService authenticationService;

    @Operation(summary = "Регистрация пользователя")
    @PostMapping("/sign-up")
    public JwtAuthenticationResponse signUp(@RequestBody @Valid SignUpRequest request) {
        return authenticationService.signUp(request);
    }

//    @Operation(summary = "Авторизация пользователя")
//    @PostMapping("/login")
//    public JwtAuthenticationResponse signIn(@RequestBody @Valid SignInRequest request) {
//        return authenticationService.signIn(request);
//    }

    @Operation(summary = "Авторизация пользователя")
    @PostMapping("/login")
    public ResponseEntity<?> signIn(@RequestBody @Valid SignInRequest request) {
        String token = String.valueOf(authenticationService.signIn(request));
        return ResponseEntity.ok(new JwtAuthenticationResponse(token));
    }


    @Operation(summary = "Удаление пользователя из системы")
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("auth-token") String token) {
        authenticationService.logout(token);
        return ResponseEntity.ok(HttpStatus.OK);
    }

}

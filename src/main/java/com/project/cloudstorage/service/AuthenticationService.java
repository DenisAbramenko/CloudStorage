package com.project.cloudstorage.service;

import com.project.cloudstorage.dto.JwtAuthenticationResponse;
import com.project.cloudstorage.dto.SignInRequest;
import com.project.cloudstorage.dto.SignUpRequest;
import com.project.cloudstorage.entity.Role;
import com.project.cloudstorage.entity.Token;
import com.project.cloudstorage.entity.User;
import com.project.cloudstorage.repository.TokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserService userService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final TokenRepository tokenRepository;

    /**
     * Регистрация пользователя
     *
     * @param request данные пользователя
     * @return токен
     */
    public JwtAuthenticationResponse signUp(SignUpRequest request) {

        User user = User.builder()
                .login(request.getLogin())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.ROLE_USER)
                .build();

        userService.create(user);

        String jwt = jwtService.generateToken(user);
        saveUserToken(user);
        return new JwtAuthenticationResponse(jwt);
    }

    private void saveUserToken(User user) {
        Token token = new Token();
        token.setLoggedOut(false);
        token.setUser(user);
        tokenRepository.save(token);
    }

    /**
     * Аутентификация пользователя
     *
     * @param request данные пользователя
     * @return токен
     */
    public JwtAuthenticationResponse signIn(SignInRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                request.getLogin(),
                request.getPassword()
        ));

        UserDetails user = userService
                .userDetailsService()
                .loadUserByUsername(request.getLogin());

        String jwt = jwtService.generateToken(user);
        return new JwtAuthenticationResponse(jwt);
    }

    /**
     * Выход пользователя из системы
     */
    public void logout(String token) {
        Optional<Token> optionalToken = tokenRepository.findByTokenAndLoggedOutEquals(token, false);
        if (optionalToken.isEmpty()) {
            return;
        }
        optionalToken.get().setLoggedOut(true);
        tokenRepository.save(optionalToken.get());
    }
}

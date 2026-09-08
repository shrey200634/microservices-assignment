package com.assignment.user_service.service;

import com.assignment.user_service.dto.AuthResponse;
import com.assignment.user_service.dto.LoginRequest;
import com.assignment.user_service.dto.RegisterRequest;
import com.assignment.user_service.dto.UserResponse;
import com.assignment.user_service.entity.User;
import com.assignment.user_service.exception.EmailAlreadyExistsException;
import com.assignment.user_service.exception.InvalidCredentialsException;
import com.assignment.user_service.mapper.UserMapper;
import com.assignment.user_service.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserMapper userMapper;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService,
                       UserMapper userMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.userMapper = userMapper;
    }

    @Transactional
    public UserResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException(request.getEmail());
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .build();

        User saved = userRepository.save(user);

        // NOTE: publish `user.registered` to NATS here once the JetStream
        // publisher is wired in (see design doc §3.2 — publish after commit).

        return userMapper.toResponse(saved);
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(InvalidCredentialsException::new);

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }

        String token = jwtService.generateToken(user.getId(), user.getEmail());

        AuthResponse response = new AuthResponse();
        response.setToken(token);
        response.setUser(userMapper.toResponse(user));
        return response;
    }
}
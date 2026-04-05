package com.ccp.WorkBridge.services.auth;

import com.ccp.WorkBridge.dto.AuthResponse;
import com.ccp.WorkBridge.dto.CustomUserDetails;
import com.ccp.WorkBridge.dto.LoginRequest;
import com.ccp.WorkBridge.dto.RegisterRequest;
import com.ccp.WorkBridge.exceptions.DataAlreadyExists;
import com.ccp.WorkBridge.models.User;
import com.ccp.WorkBridge.repos.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.ZoneId;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new DataAlreadyExists("User", "email", request.email());
        }
        User user = User.builder()
                .fullName(request.fullName())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .phoneNumber("")
                .isVerified(false)
                .priorityCoefficient(1.0)
                .timeZone(ZoneId.systemDefault())
                .build();
        userRepository.save(user);
        String token = jwtService.generateToken(user);

        return new AuthResponse(user.getId(), user.getEmail(), token);
    }

    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );
        CustomUserDetails userDetails =
                (CustomUserDetails) authentication.getPrincipal();

        User user = userDetails.user();
        String token = jwtService.generateToken(user);

        return new AuthResponse(user.getId(), user.getEmail(), token);
    }
}

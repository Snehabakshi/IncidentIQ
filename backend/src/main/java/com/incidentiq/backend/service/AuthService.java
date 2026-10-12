package com.incidentiq.backend.service;

import com.incidentiq.backend.dto.LoginRequest;
import com.incidentiq.backend.dto.TokenResponse;
import com.incidentiq.backend.entity.User;
import com.incidentiq.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.incidentiq.backend.exception.InvalidCredentialsException;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Locale;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtEncoder jwtEncoder;
    private final long expirationMinutes;
    
    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtEncoder jwtEncoder, @Value("${app.jwt.expiration-minutes}") long expirationMinutes){
        this.userRepository=userRepository;
        this.passwordEncoder=passwordEncoder;
        this.jwtEncoder=jwtEncoder;
        this.expirationMinutes=expirationMinutes;
    }
    @Transactional(readOnly = true)
    public TokenResponse login(LoginRequest request){
        String email=request.email().trim().toLowerCase(Locale.ROOT);
        User user=userRepository.findByEmail(email).orElseThrow(InvalidCredentialsException::new);
        if(!passwordEncoder.matches(request.password(), user.getPasswordHash())){
            throw new InvalidCredentialsException();
        }
        Instant now=Instant.now();
        Instant expiresAt=now.plus(expirationMinutes, ChronoUnit.MINUTES);
        JwtClaimsSet claims=JwtClaimsSet.builder()
                .issuer("incidentiq")
                .issuedAt(now)
                .expiresAt(expiresAt)
                .subject(user.getId().toString())
                .claim("tenantId",user.getTenantId().toString())
                .claim("role",user.getRole().name())
                .build();
        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        String token=jwtEncoder.encode(JwtEncoderParameters.from(header,claims)).getTokenValue();
        return new TokenResponse(token , "Bearer",expirationMinutes*60);


    }
}

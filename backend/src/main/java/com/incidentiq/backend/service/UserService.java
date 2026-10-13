package com.incidentiq.backend.service;

import com.incidentiq.backend.dto.CreateUserRequest;
import com.incidentiq.backend.dto.UserResponse;
import com.incidentiq.backend.entity.User;
import com.incidentiq.backend.exception.EmailAlreadyExistsException;
import com.incidentiq.backend.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.UUID;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,PasswordEncoder passwordEncoder){
        this.userRepository=userRepository;
        this.passwordEncoder=passwordEncoder;
    }

    @Transactional
    public UserResponse createUser(UUID tenantId, CreateUserRequest request){
        String email=request.email().trim().toLowerCase(Locale.ROOT);
        if(userRepository.existsByEmail(email)){
            throw new EmailAlreadyExistsException(email);
        }
        String passwordHash= passwordEncoder.encode(request.password());
        User saved=userRepository.save(new User(email,passwordHash,request.role(),tenantId));
        return UserResponse.from(saved);
    }
}

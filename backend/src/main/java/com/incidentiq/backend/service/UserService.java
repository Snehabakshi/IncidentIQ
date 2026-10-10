package com.incidentiq.backend.service;

import com.incidentiq.backend.dto.RegisterRequest;
import com.incidentiq.backend.dto.UserResponse;
import com.incidentiq.backend.entity.Role;
import com.incidentiq.backend.entity.User;
import com.incidentiq.backend.exception.EmailAlreadyExistsException;
import com.incidentiq.backend.exception.TenantNotFoundException;
import com.incidentiq.backend.repository.TenantRepository;
import com.incidentiq.backend.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final TenantRepository tenantRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,TenantRepository tenantRepository,PasswordEncoder passwordEncoder){
        this.userRepository=userRepository;
        this.tenantRepository=tenantRepository;
        this.passwordEncoder=passwordEncoder;
    }
    @Transactional
    public UserResponse register(RegisterRequest request){
        String email=request.email().trim().toLowerCase(Locale.ROOT);
        if(!tenantRepository.existsById(request.tenantId())){
            throw new TenantNotFoundException(request.tenantId());
        }
        if(userRepository.existsByEmail(email)){
            throw new EmailAlreadyExistsException(email);
        }
        String passwordHash=passwordEncoder.encode(request.password());
        User saved=userRepository.save(new User(email,passwordHash, Role.ENGINEER,request.tenantId()));
        return UserResponse.from(saved);
    }
}

package com.incidentiq.backend.service;

import com.incidentiq.backend.dto.SignUpRequest;
import com.incidentiq.backend.dto.SignUpResponse;
import com.incidentiq.backend.dto.TenantResponse;
import com.incidentiq.backend.dto.UserResponse;
import com.incidentiq.backend.entity.Role;
import com.incidentiq.backend.entity.Tenant;
import com.incidentiq.backend.entity.User;
import com.incidentiq.backend.exception.EmailAlreadyExistsException;
import com.incidentiq.backend.exception.TenantAlreadyExistsException;
import com.incidentiq.backend.repository.TenantRepository;
import com.incidentiq.backend.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
public class SignUpService {
    private final TenantRepository tenantRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    public SignUpService(TenantRepository tenantRepository,UserRepository userRepository, PasswordEncoder passwordEncoder){
        this.passwordEncoder=passwordEncoder;
        this.userRepository=userRepository;
        this.tenantRepository=tenantRepository;
    }

    @Transactional
    public SignUpResponse signup(SignUpRequest request){
        String companyName=request.companyName().trim();
        String email=request.email().trim().toLowerCase(Locale.ROOT);
        if(tenantRepository.existsByName(companyName)){
            throw new TenantAlreadyExistsException(companyName);
        }
        if(userRepository.existsByEmail(email)){
            throw new EmailAlreadyExistsException(email);
        }
        Tenant tenant=tenantRepository.save(new Tenant(companyName));
        User admin=userRepository.save(new User(email, passwordEncoder.encode(request.password()), Role.ADMIN,tenant.getId()));
        return new SignUpResponse(TenantResponse.from(tenant), UserResponse.from(admin));
    }
}

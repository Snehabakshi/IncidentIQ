package com.incidentiq.backend.service;


import com.incidentiq.backend.dto.RegisterRequest;
import com.incidentiq.backend.dto.UserResponse;
import com.incidentiq.backend.entity.Role;
import com.incidentiq.backend.entity.User;
import com.incidentiq.backend.exception.EmailAlreadyExistsException;
import com.incidentiq.backend.exception.TenantNotFoundException;
import com.incidentiq.backend.repository.TenantRepository;
import com.incidentiq.backend.repository.UserRepository;
import org.hibernate.validator.internal.constraintvalidators.bv.EmailValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;



import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;



@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private TenantRepository tenantRepository;

    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private UserService userService;
    private final UUID tenantId=UUID.randomUUID();

    @Test
    void register_savesUserWithHashedPasswordAndEngineerRole(){

        when(tenantRepository.existsById(tenantId)).thenReturn(true);
        when(userRepository.existsByEmail("sneha@acme.com")).thenReturn(false);
        when(passwordEncoder.encode("mySecret123")).thenReturn("hashed-value");
        when(userRepository.save(any(User.class))).thenAnswer(invocation ->invocation.getArgument(0));
        userService.register(new RegisterRequest(tenantId,"sneha@acme.com","mySecret123"));
        ArgumentCaptor<User>captor=ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        User saved=captor.getValue();
        assertEquals("hashed-value", saved.getPasswordHash());
        assertNotEquals("mySecret123",saved.getPasswordHash());
        assertEquals(Role.ENGINEER,saved.getRole());
        assertEquals(tenantId,saved.getTenantId());

    }
    @Test
    void register_trimsAndLowerCaseEmail(){
        when(tenantRepository.existsById(tenantId)).thenReturn(true);
        when(userRepository.existsByEmail("sneha@acme.com")).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("hashed-value");
        when(userRepository.save(any(User.class))).thenAnswer(invocation->invocation.getArgument(0));

        UserResponse response=userService.register(new RegisterRequest(tenantId,"  Sneha@Acme.com  ","mySecret123"));
        assertEquals("sneha@acme.com",response.email());
    }
    @Test
    void register_throwsWhenTenantDoesNotExist(){
        when(tenantRepository.existsById(tenantId)).thenReturn(false);
        assertThrows(TenantNotFoundException.class,()->userService.register(new RegisterRequest(tenantId,"sneha@acme.com","mySecret123")));
        verify(userRepository ,never()).save(any(User.class));
    }
    @Test
    void register_throwsWhenEmailAlreadyExists(){
        when(tenantRepository.existsById(tenantId)).thenReturn(true);
        when(userRepository.existsByEmail("sneha@acme.com")).thenReturn(true);
        assertThrows(EmailAlreadyExistsException.class,()->userService.register(new RegisterRequest(tenantId,"sneha@acme.com","mySecret123")));
        verify(passwordEncoder,never()).encode(anyString());
        verify(userRepository,never()).save(any(User.class));
    }
}

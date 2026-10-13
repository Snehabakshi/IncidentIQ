package com.incidentiq.backend.service;


import com.incidentiq.backend.dto.CreateUserRequest;
import com.incidentiq.backend.dto.UserResponse;
import com.incidentiq.backend.entity.Role;
import com.incidentiq.backend.entity.User;
import com.incidentiq.backend.exception.EmailAlreadyExistsException;

import com.incidentiq.backend.repository.UserRepository;
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




@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;
    private final UUID tenantId=UUID.randomUUID();

    @Test
    void createUser_savesUserWithHashedPasswordAndEngineerRole(){


        when(userRepository.existsByEmail("dev@acme.com")).thenReturn(false);
        when(passwordEncoder.encode("devPassword1")).thenReturn("hashed-value");
        when(userRepository.save(any(User.class))).thenAnswer(invocation ->invocation.getArgument(0));
        userService.createUser(tenantId,new CreateUserRequest("dev@acme.com", "devPassword1", Role.ENGINEER));
        ArgumentCaptor<User>captor=ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        User saved=captor.getValue();
        assertEquals("hashed-value", saved.getPasswordHash());
        assertEquals(Role.ENGINEER,saved.getRole());
        assertEquals(tenantId,saved.getTenantId());

    }
    @Test
    void createUser_trimsAndLowerCaseEmail(){
        when(userRepository.existsByEmail("dev@acme.com")).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("hashed-value");
        when(userRepository.save(any(User.class))).thenAnswer(invocation->invocation.getArgument(0));

        UserResponse response=userService.createUser(tenantId,new CreateUserRequest("  Dev@Acme.com  ", "devPassword1", Role.ENGINEER));
        assertEquals("dev@acme.com",response.email());
    }

    @Test
    void createUser_throwsWhenEmailAlreadyExists(){

        when(userRepository.existsByEmail("dev@acme.com")).thenReturn(true);
        assertThrows(EmailAlreadyExistsException.class,
                () -> userService.createUser(tenantId,
                        new CreateUserRequest("dev@acme.com", "devPassword1", Role.ENGINEER)));
        verify(passwordEncoder,never()).encode(anyString());
        verify(userRepository,never()).save(any(User.class));
    }
}

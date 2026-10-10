package com.incidentiq.backend.service;

import com.incidentiq.backend.dto.CreateTenantRequest;
import com.incidentiq.backend.dto.TenantResponse;
import com.incidentiq.backend.entity.Tenant;
import com.incidentiq.backend.exception.TenantAlreadyExistsException;
import com.incidentiq.backend.repository.TenantRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;


@ExtendWith(MockitoExtension.class)
 class TenantServiceTest {
    @Mock
    private TenantRepository tenantRepository;
    @InjectMocks
    private TenantService tenantService;

    @Test
    void createTenant_savesAndReturnTenant(){
        when(tenantRepository.existsByName("Acme")).thenReturn(false);
        when(tenantRepository.save(any(Tenant.class))).thenAnswer(invocation->invocation.getArgument(0));

        TenantResponse response=tenantService.createTenant(new CreateTenantRequest("Acme"));
        assertEquals("Acme",response.name());
        verify(tenantRepository).save(any(Tenant.class));

    }

    @Test
    void createTenant_trimWhiteSpaceFromName(){
        when(tenantRepository.existsByName("Acme")).thenReturn(false);
        when(tenantRepository.save(any(Tenant.class))).thenAnswer(invocation->invocation.getArgument(0));
        TenantResponse response=tenantService.createTenant(new CreateTenantRequest(" Acme "));
        assertEquals("Acme",response.name());
    }

    @Test
    void createTenant_throwsWhenNameAlreadyExists(){
        when(tenantRepository.existsByName("Acme")).thenReturn(true);
        assertThrows(TenantAlreadyExistsException.class,()->tenantService.createTenant(new CreateTenantRequest("Acme")));
        verify(tenantRepository,never()).save(any(Tenant.class));
    }
}

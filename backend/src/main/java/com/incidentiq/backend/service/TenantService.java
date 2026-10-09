package com.incidentiq.backend.service;

import com.incidentiq.backend.dto.CreateTenantRequest;
import com.incidentiq.backend.dto.TenantResponse;
import com.incidentiq.backend.entity.Tenant;
import com.incidentiq.backend.exception.TenantAlreadyExistsException;
import com.incidentiq.backend.repository.TenantRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TenantService {
   private final TenantRepository tenantRepository;

   public TenantService(TenantRepository tenantRepository){
       this.tenantRepository=tenantRepository;
   }

   @Transactional
    public TenantResponse createTenant (CreateTenantRequest request){
       String name=request.name().trim();
       if(tenantRepository.existsByName(name)){
           throw new TenantAlreadyExistsException(name);
       }
       Tenant saved= tenantRepository.save(new Tenant (name));
       return TenantResponse.from(saved);
   }
   @Transactional(readOnly = true)
    public List<TenantResponse> listTenants(){
       return tenantRepository.findAll()
               .stream()
               .map(TenantResponse:: from)
               .toList();
   }

}

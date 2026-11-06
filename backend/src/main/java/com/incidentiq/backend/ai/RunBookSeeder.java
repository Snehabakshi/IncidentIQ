package com.incidentiq.backend.ai;

import com.incidentiq.backend.entity.Tenant;
import com.incidentiq.backend.repository.TenantRepository;
import com.incidentiq.backend.service.KnowledgeService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class RunBookSeeder implements ApplicationRunner {
    private final TenantRepository tenantRepository;
    private final KnowledgeService knowledgeService;
    public RunBookSeeder(TenantRepository tenantRepository,KnowledgeService knowledgeService){
        this.tenantRepository=tenantRepository;
        this.knowledgeService=knowledgeService;
    }
    @Override
    public void run(ApplicationArguments args) throws Exception {
      for(Tenant tenant: tenantRepository.findAll()){
          knowledgeService.seedRunbooksIfMissing(tenant.getId());
      }
    }
}

package com.incidentiq.backend.service;

import com.incidentiq.backend.dto.ApiKeyCreatedResponse;
import com.incidentiq.backend.dto.ApiKeyResponse;
import com.incidentiq.backend.dto.CreateApiKeyrequest;
import com.incidentiq.backend.entity.ApiKey;
import com.incidentiq.backend.exception.ApiKeyNotFoundException;
import com.incidentiq.backend.repository.ApiKeyRepository;
import com.incidentiq.backend.security.ApiKeyUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
@Service
public class ApiKeyService {
    private final ApiKeyRepository apiKeyRepository;
    public ApiKeyService(ApiKeyRepository apiKeyRepository){
        this.apiKeyRepository=apiKeyRepository;
    }
    @Transactional
    public ApiKeyCreatedResponse create(UUID tenantId, CreateApiKeyrequest request){
        String fullKey= ApiKeyUtils.generateKey();
        ApiKey saved=apiKeyRepository.save(new ApiKey(
                tenantId,
                request.name().trim(),
                ApiKeyUtils.displayPrefix(fullKey),
                ApiKeyUtils.hash(fullKey)
        ));
        return new ApiKeyCreatedResponse(saved.getId(),saved.getName(),saved.getKeyPrefix(),fullKey,saved.getCreatedAt());
    }
    @Transactional(readOnly = true)
    public List<ApiKeyResponse>list(UUID tenantId){
       return apiKeyRepository.findByTenantIdOrderByCreatedAtDesc(tenantId)
               .stream()
               .map(ApiKeyResponse:: from)
               .toList();
    }
    @Transactional
    public void revoke(UUID tenantId,UUID keyId){
        ApiKey key=apiKeyRepository.findByIdAndTenantId(keyId,tenantId).orElseThrow(()->new ApiKeyNotFoundException(keyId));
        key.revoke();
    }
}


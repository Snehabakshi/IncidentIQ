package com.incidentiq.backend.security;

import com.incidentiq.backend.entity.ApiKey;
import com.incidentiq.backend.repository.ApiKeyRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

public class ApiKeyAuthFilter extends OncePerRequestFilter {

    private static final String HEADER="X-API-KEY";
    private final ApiKeyRepository apiKeyRepository;
    public ApiKeyAuthFilter(ApiKeyRepository apiKeyRepository){
        this.apiKeyRepository=apiKeyRepository;
    }
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
       String rawkey=request.getHeader(HEADER);
       if(rawkey!=null && !rawkey.isBlank()){
           Optional<ApiKey> found=apiKeyRepository.findByKeyHash(ApiKeyUtils.hash(rawkey.trim()));
           if(found.isPresent() && !found.get().isRevoked()){
               ApiKey key=found.get();
               var authentication=new UsernamePasswordAuthenticationToken(new ApiKeyPrincipal(key.getTenantId(),key.getId()),null, List.of(new SimpleGrantedAuthority("ROLE_SERVICE")));
               SecurityContextHolder.getContext().setAuthentication(authentication);
               try{
                   Instant now=Instant.now();
                   apiKeyRepository.touch(key.getId(),now,now.minusSeconds(60));
               }
               catch(Exception e){
                   logger.warn("Could not update last_used_at for key " + key.getId(), e);
               }
           }
       }
       chain.doFilter(request,response);
    }
}

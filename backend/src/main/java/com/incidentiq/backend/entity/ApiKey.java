package com.incidentiq.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name="api_keys")
public class ApiKey {
    @Id
    @GeneratedValue(strategy= GenerationType.UUID)
    private UUID id;

    @Column(name="tenant_id", nullable=false)
    private UUID tenantId;

    @Column(nullable = false)
    private String name;

    @Column(name="key_prefix" , nullable = false)
    private String keyPrefix;

    @Column(name="key_hash", nullable=false ,unique=true)
    private String keyHash;

    @Column(name="created_at",nullable=false)
    private Instant createdAt;

    @Column(name="last_used_at")
    private Instant lastUsedAt;

    @Column(name="revoked_at")
    private Instant revokedAt;

    protected ApiKey(){

    }
    public ApiKey(UUID tenantId,String name,String KeyPrefix,String keyHash){
        this.tenantId=tenantId;
        this.name=name;
        this.keyPrefix=keyPrefix;
        this.keyHash=keyHash;
        this.createdAt=Instant.now();
    }
    public boolean isRevoked(){
        return revokedAt!=null;
    }
    public void revoke(){
        if(revokedAt==null){
            revokedAt=Instant.now();
        }
    }
    public void markUsed(){
        this.lastUsedAt=Instant.now();
    }

    public UUID getId() { return id; }
    public UUID getTenantId() { return tenantId; }
    public String getName() { return name; }
    public String getKeyPrefix() { return keyPrefix; }
    public String getKeyHash() { return keyHash; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getLastUsedAt() { return lastUsedAt; }
    public Instant getRevokedAt() { return revokedAt; }
}

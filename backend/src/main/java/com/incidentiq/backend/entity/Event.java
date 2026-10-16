package com.incidentiq.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name="events")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name="tenant_id",nullable = false)
    private UUID tenantId;

    @Column(name="api_key_id", nullable=false)
    private UUID apiKeyId;

    @Column(nullable=false)
    private String service;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Severity severity;

    @Column(nullable=false,columnDefinition = "text")
    private String message;

    @Column(name="occurred_at",nullable=false)
    private Instant occurredAt;

    @Column(name = "received_at", nullable = false)
    private Instant receivedAt;

    @Column(name="signature")
    private String signature;

    @Column(name="external_id")
    private String externalId;

    @Column(name="incident_id")
    private UUID incidentId;

    protected Event(){

    }
    public Event(UUID tenantId, UUID apiKeyId, String service, Severity severity,
                 String message, Instant occurredAt, String externalId) {
        this.tenantId = tenantId;
        this.apiKeyId = apiKeyId;
        this.service = service;
        this.severity = severity;
        this.message = message;
        this.occurredAt = occurredAt;
        this.externalId = externalId;
        this.receivedAt = Instant.now();
    }
    public UUID getId() { return id; }
    public UUID getTenantId() { return tenantId; }
    public UUID getApiKeyId() { return apiKeyId; }
    public String getService() { return service; }
    public Severity getSeverity() { return severity; }
    public String getMessage() { return message; }
    public Instant getOccurredAt() { return occurredAt; }
    public Instant getReceivedAt() { return receivedAt; }
    public String getSignature() { return signature; }
    public String getExternalId() { return externalId; }
    public UUID getIncidentId() { return incidentId; }

}

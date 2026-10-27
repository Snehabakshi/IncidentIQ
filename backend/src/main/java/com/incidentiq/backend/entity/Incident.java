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
@Table(name="incidents")
public class Incident {
    @Id
    @GeneratedValue(strategy= GenerationType.UUID)
    private UUID id;

    @Column(name="tenant_id",nullable=false)
    private UUID tenantId;

    @Column(nullable=false)
    private String service;

    @Column(nullable = false)
    private String signature;

    @Column(nullable = false,columnDefinition = "text")
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Severity severity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private IncidentStatus status;

    @Column(name="event_count",nullable = false)
    private int eventCount;

    @Column(name="first_seen_at",nullable=false)
    private Instant firstSeenAt;

    @Column(name="last_seen_at",nullable=false)
    private Instant lastSeenAt;

    @Column(name="resolved_at")
    private Instant resolvedAt;

    @Column(name="resolved_by")
    private UUID resolvedBy;

    @Column(name="resolution_note",columnDefinition = "text")
    private String resolutionNote;

    @Column(name="created_at",nullable = false)
    private Instant createdAt;

    protected Incident(){

    }
    public Incident(UUID tenantId, String service, String signature,
                    String title, Severity severity, Instant seenAt){
        this.tenantId=tenantId;
        this.service=service;
        this.signature=signature;
        this.title=title;
        this.severity=severity;
        this.status=IncidentStatus.OPEN;
        this.eventCount=0;
        this.firstSeenAt=seenAt;
        this.lastSeenAt=seenAt;
        this.createdAt=Instant.now();
    }

    public boolean isOpen(){
        return status== IncidentStatus.OPEN;
    }
    public void resolve(UUID userId,String note){
        if (status == IncidentStatus.RESOLVED) {
            return;
        }
        this.status=IncidentStatus.RESOLVED;
        this.resolvedAt=Instant.now();
        this.resolvedBy=userId;
        this.resolutionNote=note;
    }

    public UUID getId() { return id; }
    public UUID getTenantId() { return tenantId; }
    public String getService() { return service; }
    public String getSignature() { return signature; }
    public String getTitle() { return title; }
    public Severity getSeverity() { return severity; }
    public IncidentStatus getStatus() { return status; }
    public int getEventCount() { return eventCount; }
    public Instant getFirstSeenAt() { return firstSeenAt; }
    public Instant getLastSeenAt() { return lastSeenAt; }
    public Instant getResolvedAt() { return resolvedAt; }
    public UUID getResolvedBy() { return resolvedBy; }
    public String getResolutionNote() { return resolutionNote; }
    public Instant getCreatedAt() { return createdAt; }



}
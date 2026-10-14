DROP INDEX uq_events_tenant_external_id;

CREATE UNIQUE INDEX uq_events_tenant_service_external_id
    ON events (tenant_id, service, external_id)
    WHERE external_id IS NOT NULL;


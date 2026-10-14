CREATE TABLE api_keys(
 id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
 tenant_id UUID NOT NULL REFERENCES tenants(id),
 name VARCHAR(100) NOT NULL,
 key_prefix VARCHAR(32) NOT NULL,
 key_hash VARCHAR(64) NOT NULL UNIQUE,
 created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
 last_used_at TIMESTAMPTZ,
 revoked_at TIMESTAMPTZ
);
CREATE INDEX idx_api_keys_tenant_id ON api_keys(tenant_id);
CREATE TABLE events(
 id  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
 tenant_id UUID NOT NULL REFERENCES tenants(id),
 api_key_id UUID NOT NULL REFERENCES api_keys(id),
 service VARCHAR(100) NOT NULL,
 severity VARCHAR(20) NOT NULL CHECK (severity IN ('INFO', 'WARN', 'ERROR', 'CRITICAL')),
 message TEXT NOT NULL,
 occured_at TIMESTAMPTZ NOT NULL,
 received_at TIMESTAMPTZ NOT NULL DEFAULT now(),
 signature VARCHAR(64),
 external_id VARCHAR(100),
 incident_id UUID
);
CREATE INDEX dx_events_tenant_received ON events(tenant_id , received_at DESC);
CREATE UNIQUE INDEX uq_events_tenant_external_id
ON events (tenant_id, external_id)
WHERE external_id IS NOT NULL;

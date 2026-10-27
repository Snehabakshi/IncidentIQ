CREATE TABLE incidents(
  id   UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  tenant_id  UUID NOT NULL REFERENCES tenants(id),
  service  VARCHAR(100) NOT NULL,
  signature VARCHAR(64) NOT NULL,
  title    TEXT NOT NULL,
  severity VARCHAR(20) NOT NULL CHECK (severity IN ('ERROR','CRITICAL')),
  status   VARCHAR(20) NOT NULL DEFAULT 'OPEN' CHECK (status IN ('OPEN','RESOLVED')),
  event_count  INTEGER NOT NULL DEFAULT 0,
  first_seen_at TIMESTAMPTZ NOT NULL,
  last_seen_at  TIMESTAMPTZ NOT NULL,
  resolved_at   TIMESTAMPTZ,
  resolved_by  UUID REFERENCES users(id),
  resolution_note TEXT,
  created_at    TIMESTAMPTZ NOT NULL DEFAULT now()

);
CREATE UNIQUE INDEX uq_incidents_open
    ON incidents(tenant_id,service,signature)
    WHERE status='OPEN';

CREATE INDEX idx_incidents_tenant_status_last_seen
    ON incidents(tenant_id,status,last_seen_at DESC);

ALTER TABLE events
   ADD CONSTRAINT fk_events_incident
   FOREIGN KEY(incident_id) REFERENCES incidents(id);

CREATE INDEX idx_events_incident_id ON events(incident_id);
ALTER TABLE events RENAME COLUMN occured_at TO occurred_at;
ALTER INDEX dx_events_tenant_received RENAME TO idx_events_tenant_received;
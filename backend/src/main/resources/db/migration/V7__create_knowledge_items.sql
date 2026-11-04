CREATE EXTENSION IF NOT EXISTS vector;

CREATE TABLE knowledge_items(
  id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  tenant_id   UUID NOT NULL REFERENCES tenants(id),
  source_type VARCHAR(20) NOT NULL CHECK (source_type IN ('INCIDENT','RUNBOOK')),
  source_id   UUID,
  title       VARCHAR(200) NOT NULL,
  content     TEXT NOT NULL,
  embedding   vector(64) NOT NULL,
  created_at  TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_knowledge_items_tenant ON knowledge_items(tenant_id);
CREATE UNIQUE INDEX uq_knowledge_items_source
       ON knowledge_items(tenant_id,source_type,source_id)
       WHERE source_id IS NOT NULL ;

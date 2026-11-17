CREATE TABLE suggestions(
 id       UUID PRIMARY KEY DEFAULT gen_random_uuid(),
 tenant_id  UUID NOT NULL REFERENCES tenants(id),
 incident_id  UUID NOT NULL REFERENCES incidents(id),
 answer       TEXT NOT NULL,
 sources      JSONB NOT NULL DEFAULT '[]'::jsonb,
 created_at   TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_suggestions_incident_created
    ON suggestions(incident_id , created_at DESC);

CREATE TABLE suggestion_feedback(
 id     UUID PRIMARY KEY DEFAULT gen_random_uuid(),
 tenant_id  UUID NOT NULL REFERENCES tenants(id),
 suggestion_id  UUID NOT NULL REFERENCES suggestions(id),
 user_id      UUID NOT NULL REFERENCES users(id),
 rating      VARCHAR(200) NOT NULL CHECK (rating IN ('HELPFUL','WRONG')),
 comment     VARCHAR(1000),
 created_at   TIMESTAMPTZ NOT NULL DEFAULT now(),
 updated_at    TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE UNIQUE INDEX uq_feedback_user_suggestion
    ON suggestion_feedback(suggestion_id,user_id);

CREATE INDEX idx_feedback_tenant ON suggestion_feedback(tenant_id);

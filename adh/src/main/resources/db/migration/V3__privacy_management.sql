CREATE TABLE privacy_rules (
    id UUID PRIMARY KEY,

    owner_user_id UUID NOT NULL
        REFERENCES users(id)
        ON DELETE CASCADE,

    resource_type VARCHAR(50) NOT NULL,
    resource_id UUID NOT NULL,
    field_key VARCHAR(100) NOT NULL,

    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,

    CONSTRAINT uq_privacy_rule
        UNIQUE (
            owner_user_id,
            resource_type,
            resource_id,
            field_key
        )
);

CREATE TABLE privacy_rule_statuses (
    privacy_rule_id UUID NOT NULL
        REFERENCES privacy_rules(id)
        ON DELETE CASCADE,

    membership_status VARCHAR(50) NOT NULL,

    PRIMARY KEY (
        privacy_rule_id,
        membership_status
    )
);

CREATE TABLE privacy_rule_users (
    privacy_rule_id UUID NOT NULL
        REFERENCES privacy_rules(id)
        ON DELETE CASCADE,

    allowed_user_id UUID NOT NULL
        REFERENCES users(id)
        ON DELETE CASCADE,

    PRIMARY KEY (
        privacy_rule_id,
        allowed_user_id
    )
);

CREATE INDEX idx_privacy_rules_owner
    ON privacy_rules(owner_user_id);

CREATE INDEX idx_privacy_rules_resource
    ON privacy_rules(
        owner_user_id,
        resource_type,
        resource_id
    );

CREATE INDEX idx_privacy_rule_users_allowed
    ON privacy_rule_users(allowed_user_id);
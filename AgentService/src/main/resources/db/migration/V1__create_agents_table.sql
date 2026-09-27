CREATE TABLE agents (
    id CHAR(36) NOT NULL,
    user_id CHAR(36) NOT NULL,
    license_number VARCHAR(128) NOT NULL,
    agency_name VARCHAR(255) NOT NULL,
    agency_registration_number VARCHAR(128) NULL,
    office_phone VARCHAR(64) NULL,
    status VARCHAR(32) NOT NULL,
    availability_status VARCHAR(32) NOT NULL,
    created_at TIMESTAMP(6) NOT NULL,
    updated_at TIMESTAMP(6) NOT NULL,
    version BIGINT NOT NULL,
    CONSTRAINT pk_agents PRIMARY KEY (id),
    CONSTRAINT uk_agents_user_id UNIQUE (user_id),
    CONSTRAINT uk_agents_license_number UNIQUE (license_number)
) ENGINE=InnoDB;

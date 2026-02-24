CREATE TABLE companies (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    cnpj VARCHAR(14) NOT NULL UNIQUE,
    cep VARCHAR(8),
    address_number INTEGER,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP
);

CREATE TABLE company_settings (
    id BIGSERIAL PRIMARY KEY,
    company_id BIGINT NOT NULL UNIQUE,
    everynet_access_token TEXT,
    mqtt_host VARCHAR(255),
    mqtt_port INTEGER,
    mqtt_username VARCHAR(255),
    mqtt_password TEXT,
    subscribe_topic VARCHAR(255),
    publish_topic VARCHAR(255),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP,
    CONSTRAINT fk_company_settings_company FOREIGN KEY (company_id) REFERENCES companies (id)
);

CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    company_id BIGINT NOT NULL,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    username VARCHAR(255) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    is_first_access BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP,
    CONSTRAINT fk_users_company FOREIGN KEY (company_id) REFERENCES companies (id)
);

CREATE TABLE api_keys (
    id BIGSERIAL PRIMARY KEY,
    company_id BIGINT NOT NULL,
    api_key VARCHAR(255) NOT NULL UNIQUE,
    description VARCHAR(255) NOT NULL,
    authorities VARCHAR(255) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    CONSTRAINT fk_api_keys_company FOREIGN KEY (company_id) REFERENCES companies (id)
);

CREATE TABLE device (
    id BIGSERIAL PRIMARY KEY,
    dev_eui VARCHAR(255) NOT NULL UNIQUE,
    dev_addr VARCHAR(255) NOT NULL,
    app_eui VARCHAR(255) NOT NULL,
    nwks_key VARCHAR(255) NOT NULL,
    apps_key VARCHAR(255) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP
);

CREATE TABLE tags (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP
);

CREATE TABLE device_tags (
    device_id BIGINT NOT NULL,
    tag_id BIGINT NOT NULL,
    CONSTRAINT fk_device_tags_device FOREIGN KEY (device_id) REFERENCES device (id),
    CONSTRAINT fk_device_tags_tag FOREIGN KEY (tag_id) REFERENCES tags (id)
);

CREATE TABLE user_device (
    user_id BIGINT NOT NULL,
    device_id BIGINT NOT NULL,
    name VARCHAR(255),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP,
    PRIMARY KEY (user_id, device_id),
    CONSTRAINT fk_user_device_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_user_device_device FOREIGN KEY (device_id) REFERENCES device (id)
);

CREATE TABLE device_feature (
    id BIGSERIAL PRIMARY KEY,
    device_id BIGINT NOT NULL,
    name VARCHAR(255),
    value VARCHAR(255),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP,
    CONSTRAINT fk_device_feature_device FOREIGN KEY (device_id) REFERENCES device (id)
);

CREATE TABLE device_position (
    id BIGSERIAL PRIMARY KEY,
    device_id BIGINT NOT NULL,
    latitude DOUBLE PRECISION NOT NULL,
    longitude DOUBLE PRECISION NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP,
    CONSTRAINT fk_device_position_device FOREIGN KEY (device_id) REFERENCES device (id)
);

CREATE TABLE device_telemetry (
    id BIGSERIAL PRIMARY KEY,
    device_id BIGINT NOT NULL,
    source VARCHAR(50) NOT NULL,
    type VARCHAR(50),
    payload JSONB,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP,
    CONSTRAINT fk_telemetry_device FOREIGN KEY (device_id) REFERENCES device(id)
);


CREATE TABLE ble_devices (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    hashed_public_key VARCHAR(255) NOT NULL UNIQUE,
    private_key_base64 VARCHAR(500),
    company_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT fk_findmy_device_company FOREIGN KEY (company_id) REFERENCES companies (id)
);

CREATE TABLE ble_locations (
    id BIGSERIAL PRIMARY KEY,
    ble_device_id BIGINT NOT NULL,
    latitude DOUBLE PRECISION NOT NULL,
    longitude DOUBLE PRECISION NOT NULL,
    accuracy INTEGER,
    confidence INTEGER,
    battery_status VARCHAR(50),
    timestamp TIMESTAMP NOT NULL,
    movement_status INTEGER,
    published_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT fk_ble_location_device FOREIGN KEY (ble_device_id) REFERENCES ble_devices (id) ON DELETE CASCADE
);
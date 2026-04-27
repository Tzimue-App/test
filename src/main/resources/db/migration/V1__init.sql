-- V1 : Initial schema

CREATE TABLE users (
    id          BIGSERIAL PRIMARY KEY,
    email       VARCHAR(255) NOT NULL UNIQUE,
    password    VARCHAR(255),
    name        VARCHAR(255) NOT NULL,
    role        VARCHAR(50)  NOT NULL DEFAULT 'ROLE_USER',
    provider    VARCHAR(50)  NOT NULL DEFAULT 'LOCAL',
    provider_id VARCHAR(255),
    created_at  TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE TABLE categories (
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    created_at  TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE TABLE items (
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(100)   NOT NULL,
    description VARCHAR(500),
    price       NUMERIC(10, 2),
    category_id BIGINT         NOT NULL REFERENCES categories(id) ON DELETE CASCADE,
    created_at  TIMESTAMP      NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_items_category_id ON items(category_id);
CREATE INDEX idx_users_email       ON users(email);

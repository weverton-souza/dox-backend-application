CREATE TABLE admin_users (
    id              UUID         PRIMARY KEY DEFAULT uuid_generate_v4(),
    email           VARCHAR(255) NOT NULL UNIQUE,
    password_hash   VARCHAR(255) NOT NULL,
    name            VARCHAR(255) NOT NULL,
    role            VARCHAR(20)  NOT NULL DEFAULT 'ADMIN'
                                 CHECK (role IN ('ADMIN','SUPER_ADMIN')),
    last_login_at   TIMESTAMP,
    created_at      TIMESTAMP    NOT NULL DEFAULT NOW(),
    deactivated_at  TIMESTAMP
);

CREATE INDEX idx_admin_users_email ON admin_users(email) WHERE deactivated_at IS NULL;

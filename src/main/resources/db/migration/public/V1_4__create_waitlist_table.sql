CREATE TABLE waitlist (
    id         UUID         PRIMARY KEY DEFAULT uuid_generate_v4(),
    name       VARCHAR(255) NOT NULL,
    email      VARCHAR(255) NOT NULL UNIQUE,
    profession VARCHAR(100) NOT NULL,
    city       VARCHAR(255),
    created_at TIMESTAMP    NOT NULL DEFAULT NOW()
);

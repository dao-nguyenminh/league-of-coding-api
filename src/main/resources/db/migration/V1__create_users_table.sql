-- ========================================
-- League Of Coding - Database Migration V1
-- Create users table
-- ========================================

CREATE TABLE users
(
    id            BIGSERIAL PRIMARY KEY,
    username      VARCHAR(50)  NOT NULL UNIQUE,
    email         VARCHAR(100) NOT NULL UNIQUE,
    password      VARCHAR(255) NOT NULL,
    rating        INTEGER      NOT NULL DEFAULT 1000,
    total_matches INTEGER      NOT NULL DEFAULT 0,
    wins          INTEGER      NOT NULL DEFAULT 0,
    losses        INTEGER      NOT NULL DEFAULT 0,
    created_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes
CREATE INDEX idx_users_username ON users (username);
CREATE INDEX idx_users_email ON users (email);
CREATE INDEX idx_users_rating ON users (rating DESC);

COMMENT
ON TABLE users IS 'User accounts and battle statistics';
COMMENT
ON COLUMN users.rating IS 'ELO rating for matchmaking (default: 1000)';
-- ========================================
-- League Of Coding - Database Migration V1
-- Create users table with indexes
-- ========================================

-- Create users table
CREATE TABLE users
(
    id            BIGSERIAL PRIMARY KEY,
    username      VARCHAR(50)  NOT NULL UNIQUE,
    email         VARCHAR(255) NOT NULL UNIQUE,
    password      VARCHAR(255) NOT NULL,
    rating        INT          NOT NULL DEFAULT 1000,
    total_matches INT          NOT NULL DEFAULT 0,
    wins          INT          NOT NULL DEFAULT 0,
    losses        INT          NOT NULL DEFAULT 0,
    created_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes for performance optimization
CREATE INDEX idx_users_username ON users (username);
CREATE INDEX idx_users_email ON users (email);
CREATE INDEX idx_users_rating ON users (rating DESC);

-- Add table and column comments for documentation
COMMENT ON TABLE users IS 'User accounts for League Of Coding platform';
COMMENT ON COLUMN users.id IS 'Primary key, auto-increment';
COMMENT ON COLUMN users.username IS 'Unique username for login and display';
COMMENT ON COLUMN users.email IS 'Unique email for login and communication';
COMMENT ON COLUMN users.password IS 'BCrypt hashed password (never store plain text)';
COMMENT ON COLUMN users.rating IS 'User skill rating (ELO-like), starts at 1000 (Silver tier)';
COMMENT ON COLUMN users.total_matches IS 'Total number of matches played';
COMMENT ON COLUMN users.wins IS 'Number of matches won';
COMMENT ON COLUMN users.losses IS 'Number of matches lost';
COMMENT ON COLUMN users.created_at IS 'Account creation timestamp';
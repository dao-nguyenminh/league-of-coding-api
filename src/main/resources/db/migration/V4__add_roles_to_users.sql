-- ========================================
-- League Of Coding - Database Migration V4
-- Add roles column to users table
-- ========================================

-- Add roles column
ALTER TABLE users
    ADD COLUMN roles VARCHAR(255) NOT NULL DEFAULT 'ROLE_USER';

-- Create index
CREATE INDEX idx_users_roles ON users (roles);

COMMENT ON COLUMN users.roles IS 'User roles: ROLE_USER, ROLE_ADMIN';

-- Update existing users
UPDATE users
SET roles = 'ROLE_USER'
WHERE roles ISNULL
   OR roles = '';

-- Create admin user (password: admin123)
INSERT INTO users (username, email, password, roles, rating, total_matches, wins, losses, created_at, updated_at)
VALUES ('admin',
        'admin@leagueofcoding.com',
        '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
        'ROLE_ADMIN',
        1000,
        0,
        0,
        0,
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP)
ON CONFLICT (username) DO NOTHING;
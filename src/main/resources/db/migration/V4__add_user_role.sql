-- ========================================
-- League Of Coding - Database Migration V4
-- Add role column to users table
-- ========================================

-- Add role column to users table
ALTER TABLE users
    ADD COLUMN role VARCHAR(20) NOT NULL DEFAULT 'USER';

-- Add constraint for role values
ALTER TABLE users
    ADD CONSTRAINT chk_role
        CHECK (role IN ('USER', 'ADMIN'));

-- Add index for role column (for performance)
CREATE INDEX idx_users_role ON users (role);

-- Set existing users as USER (optional since default is already USER)
UPDATE users
SET role = 'USER'
WHERE role ISNULL;

-- Create default admin account (optional - can be done via DataInitializer)
INSERT INTO users (username, email, password, role, rating, total_matches, wins, losses, created_at)
VALUES ('admin',
        'admin@leagueofcoding.com',
        '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9P8jskrjPmfXtLa', -- password: admin123
        'ADMIN',
        1000,
        0,
        0,
        0,
        CURRENT_TIMESTAMP)
ON CONFLICT (username) DO NOTHING;

-- Comment on the role column
COMMENT ON COLUMN users.role IS 'User role: USER or ADMIN';
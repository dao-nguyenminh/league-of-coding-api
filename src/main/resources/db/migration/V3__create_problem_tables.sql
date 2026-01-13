-- V3__create_problem_tables.sql
-- ========================================
-- League Of Coding - Database Migration V3
-- Create problem library tables
-- ========================================

-- ========== Categories Table ==========
CREATE TABLE categories
(
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(100) NOT NULL UNIQUE,
    slug        VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_categories_slug ON categories (slug);

COMMENT ON TABLE categories IS 'Problem categories (Arrays, Strings, Dynamic Programming, etc.)';
COMMENT ON COLUMN categories.slug IS 'URL-friendly identifier (e.g., dynamic-programming)';

-- ========== Problems Table ==========
CREATE TABLE problems
(
    id              BIGSERIAL PRIMARY KEY,
    title           VARCHAR(255) NOT NULL,
    slug            VARCHAR(255) NOT NULL UNIQUE,
    description     TEXT         NOT NULL,
    difficulty      VARCHAR(20)  NOT NULL CHECK (difficulty IN ('EASY', 'MEDIUM', 'HARD')),
    time_limit_ms   INTEGER      NOT NULL DEFAULT 2000,
    memory_limit_mb INTEGER      NOT NULL DEFAULT 256,
    category_id     BIGINT       NOT NULL,
    created_by      BIGINT       NOT NULL,
    is_active       BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_problem_category FOREIGN KEY (category_id)
        REFERENCES categories (id) ON DELETE RESTRICT,
    CONSTRAINT fk_problem_creator FOREIGN KEY (created_by)
        REFERENCES users (id) ON DELETE RESTRICT
);

CREATE INDEX idx_problems_slug ON problems (slug);
CREATE INDEX idx_problems_difficulty ON problems (difficulty);
CREATE INDEX idx_problems_category_id ON problems (category_id);
CREATE INDEX idx_problems_is_active ON problems (is_active);
CREATE INDEX idx_problems_created_at ON problems (created_at DESC);

COMMENT ON TABLE problems IS 'Coding problems for battles';
COMMENT ON COLUMN problems.slug IS 'SEO-friendly URL identifier (e.g., two-sum)';
COMMENT ON COLUMN problems.time_limit_ms IS 'Execution time limit in milliseconds';
COMMENT ON COLUMN problems.memory_limit_mb IS 'Memory limit in megabytes';
COMMENT ON COLUMN problems.is_active IS 'Soft delete flag - false means archived';

-- ========== Test Cases Table ==========
CREATE TABLE test_cases
(
    id              BIGSERIAL PRIMARY KEY,
    problem_id      BIGINT    NOT NULL,
    input           TEXT      NOT NULL,
    expected_output TEXT      NOT NULL,
    is_sample       BOOLEAN   NOT NULL DEFAULT FALSE,
    order_index     INTEGER   NOT NULL DEFAULT 0,
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_test_case_problem FOREIGN KEY (problem_id)
        REFERENCES problems (id) ON DELETE CASCADE
);

CREATE INDEX idx_test_cases_problem_id ON test_cases (problem_id);
CREATE INDEX idx_test_cases_is_sample ON test_cases (is_sample);

COMMENT ON TABLE test_cases IS 'Test cases for problem validation';
COMMENT ON COLUMN test_cases.is_sample IS 'True if visible to users (example), false if hidden (validation only)';
COMMENT ON COLUMN test_cases.order_index IS 'Display order for sample test cases';

-- ========== Seed Data: Categories ==========
INSERT INTO categories (name, slug, description)
VALUES ('Arrays', 'arrays', 'Problems involving array manipulation and algorithms'),
       ('Strings', 'strings', 'String processing and pattern matching problems'),
       ('Dynamic Programming', 'dynamic-programming', 'Problems using dynamic programming techniques'),
       ('Graphs', 'graphs', 'Graph traversal and shortest path problems'),
       ('Trees', 'trees', 'Binary trees, BST, and tree-based algorithms'),
       ('Sorting', 'sorting', 'Sorting algorithms and related problems'),
       ('Searching', 'searching', 'Binary search and search optimization'),
       ('Math', 'math', 'Mathematical and number theory problems'),
       ('Greedy', 'greedy', 'Greedy algorithm problems'),
       ('Backtracking', 'backtracking', 'Problems requiring backtracking approach');
-- V5: Create matchmaking tables

-- User ratings for ELO system
CREATE TABLE user_ratings
(
    id             BIGSERIAL PRIMARY KEY,
    user_id        BIGINT    NOT NULL UNIQUE,
    elo_rating     INTEGER   NOT NULL DEFAULT 1200,
    matches_played INTEGER   NOT NULL DEFAULT 0,
    wins           INTEGER   NOT NULL DEFAULT 0,
    losses         INTEGER   NOT NULL DEFAULT 0,
    created_at     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_user_ratings_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

-- Index for faster lookups
CREATE INDEX idx_user_ratings_user_id ON user_ratings (user_id);
CREATE INDEX idx_user_ratings_elo ON user_ratings (elo_rating);

-- Matches table
CREATE TABLE matches
(
    id         BIGSERIAL PRIMARY KEY,
    player1_id BIGINT      NOT NULL,
    player2_id BIGINT      NOT NULL,
    problem_id BIGINT      NOT NULL,
    status     VARCHAR(20) NOT NULL DEFAULT 'WAITING',
    winner_id  BIGINT,
    started_at TIMESTAMP,
    ended_at   TIMESTAMP,
    created_at TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_matches_player1 FOREIGN KEY (player1_id) REFERENCES users (id),
    CONSTRAINT fk_matches_player2 FOREIGN KEY (player2_id) REFERENCES users (id),
    CONSTRAINT fk_matches_problem FOREIGN KEY (problem_id) REFERENCES problems (id),
    CONSTRAINT fk_matches_winner FOREIGN KEY (winner_id) REFERENCES users (id),

    -- Ensure different players
    CONSTRAINT chk_different_players CHECK (player1_id != player2_id
        )
);

-- Indexes for performance
CREATE INDEX idx_matches_player1 ON matches (player1_id);
CREATE INDEX idx_matches_player2 ON matches (player2_id);
CREATE INDEX idx_matches_status ON matches (status);
CREATE INDEX idx_matches_created_at ON matches (created_at DESC);

-- Match submissions (code submissions during battle)
CREATE TABLE match_submissions
(
    id                BIGSERIAL PRIMARY KEY,
    match_id          BIGINT      NOT NULL,
    user_id           BIGINT      NOT NULL,
    code              TEXT        NOT NULL,
    language          VARCHAR(20) NOT NULL,
    status            VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    execution_time_ms INTEGER,
    memory_used_kb    INTEGER,
    test_cases_passed INTEGER,
    test_cases_total  INTEGER,
    submitted_at      TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    judged_at         TIMESTAMP,

    CONSTRAINT fk_submissions_match FOREIGN KEY (match_id) REFERENCES matches (id) ON DELETE CASCADE,
    CONSTRAINT fk_submissions_user FOREIGN KEY (user_id) REFERENCES users (id),

    -- User can only have one submission per match
    CONSTRAINT uk_match_user UNIQUE (match_id, user_id)
);

-- Indexes
CREATE INDEX idx_submissions_match ON match_submissions (match_id);
CREATE INDEX idx_submissions_user ON match_submissions (user_id);
CREATE INDEX idx_submissions_status ON match_submissions (status);

-- Comments
COMMENT
    ON TABLE user_ratings IS 'User ELO ratings for matchmaking';
COMMENT
    ON TABLE matches IS 'Battle matches between users';
COMMENT
    ON TABLE match_submissions IS 'Code submissions during matches';

COMMENT
    ON COLUMN matches.status IS 'WAITING, IN_PROGRESS, COMPLETED, CANCELLED';
COMMENT
    ON COLUMN match_submissions.status IS 'PENDING, PASSED, FAILED, ERROR';
COMMENT
    ON COLUMN match_submissions.language IS 'JAVA, PYTHON, CPP, JAVASCRIPT';
CREATE TABLE IF NOT EXISTS users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(40) NOT NULL,
    student_id VARCHAR(32) NOT NULL UNIQUE,
    phone VARCHAR(20) NOT NULL UNIQUE,
    email VARCHAR(120),
    password_hash VARCHAR(100) NOT NULL,
    role VARCHAR(10) NOT NULL,
    verified BOOLEAN NOT NULL DEFAULT FALSE,
    avatar_url VARCHAR(600),
    avatar_object_key VARCHAR(300),
    created_at TIMESTAMP(6) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

ALTER TABLE users ADD COLUMN IF NOT EXISTS avatar_url VARCHAR(600);
ALTER TABLE users ADD COLUMN IF NOT EXISTS avatar_object_key VARCHAR(300);

CREATE TABLE IF NOT EXISTS auth_tokens (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    token_hash VARCHAR(64) NOT NULL UNIQUE,
    expires_at TIMESTAMP(6) NOT NULL,
    created_at TIMESTAMP(6) NOT NULL,
    INDEX idx_auth_tokens_user_id (user_id),
    CONSTRAINT fk_auth_tokens_user FOREIGN KEY (user_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS item_posts (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    type VARCHAR(10) NOT NULL,
    title VARCHAR(30) NOT NULL,
    category VARCHAR(30) NOT NULL,
    event_date DATE NOT NULL,
    location VARCHAR(120) NOT NULL,
    description VARCHAR(300) NOT NULL,
    hidden_feature VARCHAR(300),
    status VARCHAR(20) NOT NULL DEFAULT 'PUBLISHED',
    created_at TIMESTAMP(6) NOT NULL,
    INDEX idx_item_posts_created_at (created_at),
    CONSTRAINT fk_item_posts_user FOREIGN KEY (user_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS item_images (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    item_id BIGINT NOT NULL,
    object_key VARCHAR(300) NOT NULL,
    url VARCHAR(600) NOT NULL,
    sort_order INT NOT NULL,
    INDEX idx_item_images_item_id (item_id),
    CONSTRAINT fk_item_images_item FOREIGN KEY (item_id) REFERENCES item_posts(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS item_favorites (
    user_id BIGINT NOT NULL,
    item_id BIGINT NOT NULL,
    created_at TIMESTAMP(6) NOT NULL,
    PRIMARY KEY (user_id, item_id),
    INDEX idx_item_favorites_item_id (item_id),
    CONSTRAINT fk_item_favorites_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_item_favorites_item FOREIGN KEY (item_id) REFERENCES item_posts(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS item_drafts (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    type VARCHAR(10) NOT NULL,
    title VARCHAR(30),
    category VARCHAR(30),
    event_date DATE,
    location VARCHAR(120),
    description VARCHAR(300),
    hidden_feature VARCHAR(300),
    images_json TEXT,
    created_at TIMESTAMP(6) NOT NULL,
    updated_at TIMESTAMP(6) NOT NULL,
    INDEX idx_item_drafts_user_id (user_id),
    CONSTRAINT fk_item_drafts_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

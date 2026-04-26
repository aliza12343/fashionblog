-- ============================================================
-- Fashion Blog – Database Schema
-- Run against a fresh MySQL 8 instance:
--   mysql -u root -p < src/main/resources/db/schema.sql
-- ============================================================

CREATE DATABASE IF NOT EXISTS fashion_db
    CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE fashion_db;

-- ------------------------------------------------------------
-- Table: users
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS users (
    id       BIGINT       NOT NULL AUTO_INCREMENT,
    username VARCHAR(50)  NOT NULL,
    password VARCHAR(255) NOT NULL,
    email    VARCHAR(100) NOT NULL,
    role     ENUM('USER','ADMIN') NOT NULL DEFAULT 'USER',

    CONSTRAINT pk_users          PRIMARY KEY (id),
    CONSTRAINT uq_users_username UNIQUE      (username),
    CONSTRAINT uq_users_email    UNIQUE      (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ------------------------------------------------------------
-- Table: posts
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS posts (
    id             BIGINT        NOT NULL AUTO_INCREMENT,
    title          VARCHAR(200)  NOT NULL,
    content        TEXT          NOT NULL,
    image_url      VARCHAR(500),
    price          DOUBLE,
    category       VARCHAR(100)  NOT NULL,
    stock_quantity INT                    DEFAULT 0,
    created_at     DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    user_id        BIGINT        NOT NULL,

    CONSTRAINT pk_posts      PRIMARY KEY (id),
    CONSTRAINT fk_posts_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ------------------------------------------------------------
-- Indexes for frequently queried columns
-- ------------------------------------------------------------
CREATE INDEX idx_posts_category   ON posts (category);
CREATE INDEX idx_posts_created_at ON posts (created_at DESC);
CREATE INDEX idx_posts_user_id    ON posts (user_id);
CREATE INDEX idx_users_email      ON users (email);

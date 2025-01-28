CREATE SCHEMA forum_schema;


DROP TABLE IF EXISTS forum_schema.users CASCADE;
DROP TABLE IF EXISTS forum_schema.posts CASCADE;
DROP TABLE IF EXISTS forum_schema.comments CASCADE;
DROP TABLE IF EXISTS forum_schema.roles CASCADE;
DROP TABLE IF EXISTS forum_schema.user_roles CASCADE;
DROP TABLE IF EXISTS forum_schema.admin_details CASCADE;
DROP TABLE IF EXISTS forum_schema.likes CASCADE;
DROP TABLE IF EXISTS forum_schema.post_tags CASCADE;
DROP TABLE IF EXISTS forum_schema.tags CASCADE;

CREATE TABLE forum_schema.users
(
    user_id       INT AUTO_INCREMENT PRIMARY KEY,
    first_name    VARCHAR(50)          NOT NULL,
    last_name     VARCHAR(50)          NOT NULL,
    email_address VARCHAR(100)         NOT NULL UNIQUE,
    username      VARCHAR(50)          NOT NULL,
    password      VARCHAR(50)          NOT NULL,
    blocked       TINYINT(1) DEFAULT 0 NOT NULL
);


CREATE TABLE forum_schema.posts
(
    post_id INT AUTO_INCREMENT PRIMARY KEY,
    title   VARCHAR(70) NOT NULL,
    content TEXT        NOT NULL,
    user_id INT         NOT NULL,
    FOREIGN KEY (user_id) REFERENCES forum_schema.users (user_id) ON DELETE CASCADE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);


CREATE TABLE forum_schema.comments
(
    comment_id INT AUTO_INCREMENT PRIMARY KEY,
    content    TEXT NOT NULL,
    post_id    INT  NOT NULL,
    user_id    INT  NOT NULL,
    FOREIGN KEY (post_id) references forum_schema.posts (post_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) references forum_schema.users (user_id) ON DELETE CASCADE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE TABLE forum_schema.roles
(
    role_id INT AUTO_INCREMENT PRIMARY KEY,
    name    VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE forum_schema.user_roles
(
    user_id INT NOT NULL,
    role_id INT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES forum_schema.users (user_id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES forum_schema.roles (role_id) ON DELETE CASCADE
);

CREATE TABLE forum_schema.admin_details
(
    id           INT AUTO_INCREMENT PRIMARY KEY,
    user_id      INT         NOT NULL UNIQUE,
    phone_number VARCHAR(20) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES forum_schema.users (user_id) ON DELETE CASCADE
);

CREATE TABLE forum_schema.likes
(
    like_id  INT AUTO_INCREMENT PRIMARY KEY,
    user_id  INT     NOT NULL,
    post_id  INT     NOT NULL,
    reaction TINYINT NOT NULL,
    UNIQUE (user_id, post_id),
    FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE,
    FOREIGN KEY (post_id) REFERENCES posts (post_id) ON DELETE CASCADE
);

CREATE TABLE forum_schema.tags
(
    tag_id INT AUTO_INCREMENT PRIMARY KEY,
    name     VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE forum_schema.post_tags
(
    post_id  INT NOT NULL,
    tag_id INT NOT NULL,
    PRIMARY KEY (post_id, tag_id),
    FOREIGN KEY (post_id) REFERENCES forum_schema.posts (post_id) ON DELETE CASCADE,
    FOREIGN KEY (tag_id) REFERENCES forum_schema.tags (tag_id) ON DELETE CASCADE
);



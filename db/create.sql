CREATE SCHEMA forum_schema;


DROP TABLE IF EXISTS forum_schema.users CASCADE;
DROP TABLE IF EXISTS forum_schema.admins CASCADE;
DROP TABLE IF EXISTS forum_schema.posts CASCADE;
DROP TABLE IF EXISTS forum_schema.comments CASCADE;


CREATE TABLE forum_schema.users
(
    user_id       INT AUTO_INCREMENT PRIMARY KEY,
    first_name    VARCHAR(50)  NOT NULL,
    last_name     VARCHAR(50)  NOT NULL,
    email_address VARCHAR(100) NOT NULL UNIQUE,
    username      VARCHAR(50)  NOT NULL,
    password      VARCHAR(50)  NOT NULL
);

CREATE TABLE forum_schema.admins
(
    admin_id     INT AUTO_INCREMENT PRIMARY KEY,
    user_id      INT NOT NULL,
    phone_number VARCHAR(50),
    FOREIGN KEY (user_id) REFERENCES forum_schema.users (user_id) ON DELETE CASCADE
);

CREATE TABLE forum_schema.posts(
    post_id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(70) NOT NULL,
    content TEXT NOT NULL,
    likes INT NOT NULL,
    user_id INT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES forum_schema.users(user_id) ON DELETE CASCADE
);


CREATE TABLE forum_schema.comments
(
    comment_id INT AUTO_INCREMENT PRIMARY KEY,
    content    TEXT NOT NULL,
    post_id    INT  NOT NULL,
    user_id    INT  NOT NULL,
    FOREIGN KEY (post_id) references forum_schema.posts (post_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) references forum_schema.users (user_id) ON DELETE CASCADE
);



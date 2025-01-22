INSERT INTO forum_schema.users (first_name, last_name, email_address, username, password)
VALUES
    ('John', 'Doe', 'john.doe@example.com', 'johndoe', 'password123'),
    ('Jane', 'Smith', 'jane.smith@example.com', 'janesmith', 'securepass'),
    ('Admin', 'User', 'admin.user@example.com', 'adminuser', 'adminpass');

INSERT INTO forum_schema.admins (user_id, phone_number)
VALUES
    (3, '123-456-7890');

INSERT INTO forum_schema.posts (title, content, likes, user_id)
VALUES
    ('Welcome Post', 'This is the first post in the forum!', 10, 1),
    ('Second Post', 'Another interesting post in the forum.', 5, 2);

INSERT INTO forum_schema.comments (content, post_id)
VALUES
    ('This is a comment on the first post.', 1),
    ('Another comment on the first post.', 1),
    ('Comment on the second post.', 2);
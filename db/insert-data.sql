INSERT INTO forum_schema.users (first_name, last_name, email_address, username, password)
VALUES ('John', 'Doe', 'john.doe@example.com', 'johndoe', 'password123'),
       ('Jane', 'Smith', 'jane.smith@example.com', 'janesmith', 'securepass'),
       ('Admin', 'User', 'admin.user@example.com', 'adminuser', 'adminpass'),
       ('Nophone', 'Andy', 'nophone.andy@example.com', 'nophoneadmin', 'adminpass');

INSERT INTO forum_schema.posts (title, content, user_id)
VALUES ('Welcome Post', 'This is the first post in the forum!', 1),
       ('Second Post', 'Another interesting post in the forum.', 2);

INSERT INTO forum_schema.comments (content, post_id, user_id)
VALUES ('This is a comment on the first post.', 1, 3),
       ('Another comment on the first post.', 1, 2),
       ('Comment on the second post.', 2, 4);

INSERT INTO forum_schema.roles(name)
VALUES('USER'),('ADMIN');

INSERT INTO forum_schema.user_roles(user_id, role_id)
VALUES(1,1),
      (2,1),
      (3,1),
      (3,2),
      (4,1),
      (4,2);

INSERT INTO forum_schema.admin_details(user_id, phone_number)
VALUES (3, '123-456-7890');



INSERT INTO library_user (id, email, first_name, last_name, password)
VALUES (1, 'admin@example.com', 'Admin', 'User', '$2a$10$HqEj3CU.FwUWtNtjI31h4OO7YzcIQDBbk5G96r1VuhihOInaAetD.');

INSERT INTO authority (authority_id, name, user_id)
VALUES (1, 'ROLE_ADMIN', 1);

/*Inserting ROLE_ADMIN*/
INSERT INTO role (id, name)
SELECT * FROM (SELECT 1, 'ROLE_ADMIN') AS tmp
WHERE NOT EXISTS (
        SELECT id, name FROM role WHERE name = 'ROLE_ADMIN'
    ) LIMIT 1;

/*Inserting ROLE_STUDENT*/
INSERT INTO role (id, name)
SELECT * FROM (SELECT 2, 'ROLE_STUDENT') AS tmp
WHERE NOT EXISTS (
        SELECT id, name FROM role WHERE name = 'ROLE_STUDENT'
    ) LIMIT 1;

/*Inserting ROLE_PROFESSOR*/
INSERT INTO role (id, name)
SELECT * FROM (SELECT 3, 'ROLE_PROFESSOR') AS tmp
WHERE NOT EXISTS (
        SELECT id, name FROM role WHERE name = 'ROLE_PROFESSOR'
    ) LIMIT 1;

/*Inserting Admin user*/
INSERT INTO user (dtype, id, first_name, name, password)
SELECT * FROM (SELECT 'User', 'admin', 'simone', 'magnani', '$2a$04$aQE3mnZpUmjlWB2gAj41CeacwqXMm4T2nGeLGeTjlmSyanx8t7H6O') AS tmp
WHERE NOT EXISTS (
        SELECT * FROM user WHERE id = 'admin'
    ) LIMIT 1;

/*Associating Admin user-role*/
INSERT INTO user_role (user_id, role_id)
SELECT * FROM (SELECT 'admin', (SELECT id from role where name='ROLE_ADMIN')) AS tmp
WHERE NOT EXISTS (
        SELECT user_id, role_id FROM user_role WHERE user_id = 'admin' and role_id = (SELECT id from role where name='ROLE_ADMIN')
    ) LIMIT 1;
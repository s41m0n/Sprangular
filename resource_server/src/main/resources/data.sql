/*Inserting ROLE_STUDENT*/
INSERT INTO role (id, name)
SELECT * FROM (SELECT 1, 'ROLE_STUDENT') AS tmp
WHERE NOT EXISTS (
        SELECT id, name FROM role WHERE name = 'ROLE_STUDENT'
    ) LIMIT 1;

/*Inserting ROLE_PROFESSOR*/
INSERT INTO role (id, name)
SELECT * FROM (SELECT 2, 'ROLE_PROFESSOR') AS tmp
WHERE NOT EXISTS (
        SELECT id, name FROM role WHERE name = 'ROLE_PROFESSOR'
    ) LIMIT 1;

/*Inserting Student user*/
INSERT INTO user (dtype, id, password, email, name, surname, verified) /* password = toortoor */
SELECT * FROM (SELECT 'Student', 's264970', '$2a$10$pfxXEkF4jy81bjRu/ZlPGOKFnxf1t.tboqcaRzOs/ykaYOAK1IWKW', 's264970@studenti.polito.it', 'Francesco', 'Pavan', true) AS tmp
WHERE NOT EXISTS (
        SELECT * FROM user WHERE id = 's264970'
    ) LIMIT 1;

/*Associating Student user-role*/
INSERT INTO user_role (user_id, role_id)
SELECT * FROM (SELECT 's264970', (SELECT id from role where name='ROLE_STUDENT')) AS tmp
WHERE NOT EXISTS (
        SELECT user_id, role_id FROM user_role WHERE user_id = 's264970' and role_id = (SELECT id from role where name='ROLE_STUDENT')
    ) LIMIT 1;

/*Inserting Student user*/
INSERT INTO user (dtype, id, password, email, name, surname, verified)
SELECT * FROM (SELECT 'Student', 's264971', '$2a$10$pfxXEkF4jy81bjRu/ZlPGOKFnxf1t.tboqcaRzOs/ykaYOAK1IWKW', 's264971@studenti.polito.it', 'Simone', 'Magnani', true) AS tmp
WHERE NOT EXISTS (
        SELECT * FROM user WHERE id = 's264971'
    ) LIMIT 1;

/*Associating Student user-role*/
INSERT INTO user_role (user_id, role_id)
SELECT * FROM (SELECT 's264971', (SELECT id from role where name='ROLE_STUDENT')) AS tmp
WHERE NOT EXISTS (
        SELECT user_id, role_id FROM user_role WHERE user_id = 's264971' and role_id = (SELECT id from role where name='ROLE_STUDENT')
    ) LIMIT 1;

/*Inserting Student user*/
INSERT INTO user (dtype, id, password, email, name, surname, verified)
SELECT * FROM (SELECT 'Student', 's264972', '$2a$10$pfxXEkF4jy81bjRu/ZlPGOKFnxf1t.tboqcaRzOs/ykaYOAK1IWKW', 's264972@studenti.polito.it', 'Riccardo', 'Marchi', true) AS tmp
WHERE NOT EXISTS (
        SELECT * FROM user WHERE id = 's264972'
    ) LIMIT 1;

/*Associating Student user-role*/
INSERT INTO user_role (user_id, role_id)
SELECT * FROM (SELECT 's264972', (SELECT id from role where name='ROLE_STUDENT')) AS tmp
WHERE NOT EXISTS (
        SELECT user_id, role_id FROM user_role WHERE user_id = 's264972' and role_id = (SELECT id from role where name='ROLE_STUDENT')
    ) LIMIT 1;

/*Inserting Student user*/
INSERT INTO user (dtype, id, password, email, name, surname, verified)
SELECT * FROM (SELECT 'Student', 's264973', '$2a$10$pfxXEkF4jy81bjRu/ZlPGOKFnxf1t.tboqcaRzOs/ykaYOAK1IWKW', 's264973@studenti.polito.it', 'Daniele', 'Palumbo', true) AS tmp
WHERE NOT EXISTS (
        SELECT * FROM user WHERE id = 's264973'
    ) LIMIT 1;

/*Associating Student user-role*/
INSERT INTO user_role (user_id, role_id)
SELECT * FROM (SELECT 's264973', (SELECT id from role where name='ROLE_STUDENT')) AS tmp
WHERE NOT EXISTS (
        SELECT user_id, role_id FROM user_role WHERE user_id = 's264973' and role_id = (SELECT id from role where name='ROLE_STUDENT')
    ) LIMIT 1;

/*Inserting Student user*/
INSERT INTO user (dtype, id, password, email, name, surname, verified)
SELECT * FROM (SELECT 'Student', 's264974', '$2a$10$pfxXEkF4jy81bjRu/ZlPGOKFnxf1t.tboqcaRzOs/ykaYOAK1IWKW', 's264974@studenti.polito.it', 'Enrico', 'Postolov', true) AS tmp
WHERE NOT EXISTS (
        SELECT * FROM user WHERE id = 's264974'
    ) LIMIT 1;

/*Associating Student user-role*/
INSERT INTO user_role (user_id, role_id)
SELECT * FROM (SELECT 's264974', (SELECT id from role where name='ROLE_STUDENT')) AS tmp
WHERE NOT EXISTS (
        SELECT user_id, role_id FROM user_role WHERE user_id = 's264974' and role_id = (SELECT id from role where name='ROLE_STUDENT')
    ) LIMIT 1;

/*Inserting Student user*/
INSERT INTO user (dtype, id, password, email, name, surname, verified)
SELECT * FROM (SELECT 'Student', 's264975', '$2a$10$pfxXEkF4jy81bjRu/ZlPGOKFnxf1t.tboqcaRzOs/ykaYOAK1IWKW', 's264975@studenti.polito.it', 'Giulia', 'Milan', true) AS tmp
WHERE NOT EXISTS (
        SELECT * FROM user WHERE id = 's264975'
    ) LIMIT 1;

/*Associating Student user-role*/
INSERT INTO user_role (user_id, role_id)
SELECT * FROM (SELECT 's264975', (SELECT id from role where name='ROLE_STUDENT')) AS tmp
WHERE NOT EXISTS (
        SELECT user_id, role_id FROM user_role WHERE user_id = 's264975' and role_id = (SELECT id from role where name='ROLE_STUDENT')
    ) LIMIT 1;

/*Inserting Student user*/
INSERT INTO user (dtype, id, password, email, name, surname, verified)
SELECT * FROM (SELECT 'Student', 's264976', '$2a$10$pfxXEkF4jy81bjRu/ZlPGOKFnxf1t.tboqcaRzOs/ykaYOAK1IWKW', 's264976@studenti.polito.it', 'Ilaria', 'Di Domenico', true) AS tmp
WHERE NOT EXISTS (
        SELECT * FROM user WHERE id = 's264976'
    ) LIMIT 1;

/*Associating Student user-role*/
INSERT INTO user_role (user_id, role_id)
SELECT * FROM (SELECT 's264976', (SELECT id from role where name='ROLE_STUDENT')) AS tmp
WHERE NOT EXISTS (
        SELECT user_id, role_id FROM user_role WHERE user_id = 's264976' and role_id = (SELECT id from role where name='ROLE_STUDENT')
    ) LIMIT 1;

/*Inserting Professor user*/
INSERT INTO user (dtype, id, password, email, name, surname, verified)
SELECT * FROM (SELECT 'Professor', 'd264970', '$2a$10$pfxXEkF4jy81bjRu/ZlPGOKFnxf1t.tboqcaRzOs/ykaYOAK1IWKW', 'd264970@polito.it', 'Giovanni', 'Malnati', true) AS tmp
WHERE NOT EXISTS (
        SELECT * FROM user WHERE id = 'd264970'
    ) LIMIT 1;

/*Associating Professor user-role*/
INSERT INTO user_role (user_id, role_id)
SELECT * FROM (SELECT 'd264970', (SELECT id from role where name='ROLE_PROFESSOR')) AS tmp
WHERE NOT EXISTS (
        SELECT user_id, role_id FROM user_role WHERE user_id = 'd264970' and role_id = (SELECT id from role where name='ROLE_PROFESSOR')
    ) LIMIT 1;

/*Inserting Professor user*/
INSERT INTO user (dtype, id, password, email, name, surname, verified)
SELECT * FROM (SELECT 'Professor', 'd264971', '$2a$10$pfxXEkF4jy81bjRu/ZlPGOKFnxf1t.tboqcaRzOs/ykaYOAK1IWKW', 'd264971@polito.it', 'Antonio', 'Servetti', true) AS tmp
WHERE NOT EXISTS (
        SELECT * FROM user WHERE id = 'd264971'
    ) LIMIT 1;

/*Associating Professor user-role*/
INSERT INTO user_role (user_id, role_id)
SELECT * FROM (SELECT 'd264971', (SELECT id from role where name='ROLE_PROFESSOR')) AS tmp
WHERE NOT EXISTS (
        SELECT user_id, role_id FROM user_role WHERE user_id = 'd264971' and role_id = (SELECT id from role where name='ROLE_PROFESSOR')
    ) LIMIT 1;

/*Inserting Professor user*/
INSERT INTO user (dtype, id, password, email, name, surname, verified)
SELECT * FROM (SELECT 'Professor', 'd264972', '$2a$10$pfxXEkF4jy81bjRu/ZlPGOKFnxf1t.tboqcaRzOs/ykaYOAK1IWKW', 'd264972@polito.it', 'Fulvio', 'Risso', true) AS tmp
WHERE NOT EXISTS (
        SELECT * FROM user WHERE id = 'd264972'
    ) LIMIT 1;

/*Associating Professor user-role*/
INSERT INTO user_role (user_id, role_id)
SELECT * FROM (SELECT 'd264972', (SELECT id from role where name='ROLE_PROFESSOR')) AS tmp
WHERE NOT EXISTS (
        SELECT user_id, role_id FROM user_role WHERE user_id = 'd264972' and role_id = (SELECT id from role where name='ROLE_PROFESSOR')
    ) LIMIT 1;

/*Inserting Professor user*/
INSERT INTO user (dtype, id, password, email, name, surname, verified)
SELECT * FROM (SELECT 'Professor', 'd264973', '$2a$10$pfxXEkF4jy81bjRu/ZlPGOKFnxf1t.tboqcaRzOs/ykaYOAK1IWKW', 'd264973@polito.it', 'Riccardo', 'Sisto', true) AS tmp
WHERE NOT EXISTS (
        SELECT * FROM user WHERE id = 'd264973'
    ) LIMIT 1;

/*Associating Professor user-role*/
INSERT INTO user_role (user_id, role_id)
SELECT * FROM (SELECT 'd264973', (SELECT id from role where name='ROLE_PROFESSOR')) AS tmp
WHERE NOT EXISTS (
        SELECT user_id, role_id FROM user_role WHERE user_id = 'd264972' and role_id = (SELECT id from role where name='ROLE_PROFESSOR')
    ) LIMIT 1;
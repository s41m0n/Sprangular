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

/*Inserting Course*/
INSERT INTO course (acronym, name, team_min_size, team_max_size, enabled)
SELECT * FROM (SELECT 'dp2', 'Distributed Programming II', 2, 5, true) AS tmp
WHERE NOT EXISTS (
        SELECT * FROM course WHERE acronym = 'dp2'
    ) LIMIT 1;

/*Inserting Course*/
INSERT INTO course (acronym, name, team_min_size, team_max_size, enabled)
SELECT * FROM (SELECT 'ai', 'Applicazioni Internet', 2, 5, true) AS tmp
WHERE NOT EXISTS (
        SELECT * FROM course WHERE acronym = 'ai'
    ) LIMIT 1;


/*Inserting Course*/
INSERT INTO course (acronym, name, team_min_size, team_max_size, enabled)
SELECT * FROM (SELECT 'dp1', 'Distributed Programming I', 2, 5, true) AS tmp
WHERE NOT EXISTS (
        SELECT * FROM course WHERE acronym = 'dp1'
    ) LIMIT 1;

/*Associating professor to course*/
INSERT INTO professor_course(course_acronym, professor_id)
SELECT * FROM (SELECT 'dp1', 'd264970') AS tmp
WHERE NOT EXISTS (
        SELECT course_acronym, professor_id FROM professor_course WHERE course_acronym = 'dp1' and professor_id = 'd264970'
    ) LIMIT 1;

/*Associating professor to course*/
INSERT INTO professor_course(course_acronym, professor_id)
SELECT * FROM (SELECT 'dp1', 'd264971') AS tmp
WHERE NOT EXISTS (
        SELECT course_acronym, professor_id FROM professor_course WHERE course_acronym = 'dp1' and professor_id = 'd264971'
    ) LIMIT 1;

/*Associating professor to course*/
INSERT INTO professor_course(course_acronym, professor_id)
SELECT * FROM (SELECT 'dp2', 'd264972') AS tmp
WHERE NOT EXISTS (
        SELECT course_acronym, professor_id FROM professor_course WHERE course_acronym = 'dp2' and professor_id = 'd264972'
    ) LIMIT 1;

/*Associating professor to course*/
INSERT INTO professor_course(course_acronym, professor_id)
SELECT * FROM (SELECT 'ai', 'd264973') AS tmp
WHERE NOT EXISTS (
        SELECT course_acronym, professor_id FROM professor_course WHERE course_acronym = 'ai' and professor_id = 'd264973'
    ) LIMIT 1;

/*Associating student to course*/
INSERT INTO course_student(course_acronym, student_id)
SELECT * FROM (SELECT 'dp1', 's264970') AS tmp
WHERE NOT EXISTS (
        SELECT course_acronym, student_id FROM course_student WHERE course_acronym = 'dp1' and student_id = 's264970'
    ) LIMIT 1;

/*Associating student to course*/
INSERT INTO course_student(course_acronym, student_id)
SELECT * FROM (SELECT 'dp1', 's264971') AS tmp
WHERE NOT EXISTS (
        SELECT course_acronym, student_id FROM course_student WHERE course_acronym = 'dp1' and student_id = 's264971'
    ) LIMIT 1;

/*Associating student to course*/
INSERT INTO course_student(course_acronym, student_id)
SELECT * FROM (SELECT 'dp1', 's264972') AS tmp
WHERE NOT EXISTS (
        SELECT course_acronym, student_id FROM course_student WHERE course_acronym = 'dp1' and student_id = 's264972'
    ) LIMIT 1;


/*Associating student to course*/
INSERT INTO course_student(course_acronym, student_id)
SELECT * FROM (SELECT 'dp2', 's264973') AS tmp
WHERE NOT EXISTS (
        SELECT course_acronym, student_id FROM course_student WHERE course_acronym = 'dp2' and student_id = 's264973'
    ) LIMIT 1;

/*Associating student to course*/
INSERT INTO course_student(course_acronym, student_id)
SELECT * FROM (SELECT 'dp2', 's264974') AS tmp
WHERE NOT EXISTS (
        SELECT course_acronym, student_id FROM course_student WHERE course_acronym = 'dp2' and student_id = 's264974'
    ) LIMIT 1;

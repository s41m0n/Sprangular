# Sprangular

Progetto finale di Applicazioni Internet - sito web creato con Spring Boot e Angular

## Come avviare un database sfruttando un container mariadDB collegato

Eseguire il seguente comando sostituendo a _\<local_path\>_ il path desiderato per salvare i dati del database.

`$ docker run --name mariadb -v <local path>:/var/lib/mysql -e MYSQL_ROOT_PASSWORD=root -p 3306:3306 -d mariadb`

## Come creare un nuovo database utilizzando il plugin di IntelliJ IDEA

- Creare una data source `@localhost`;
- Aprire il source editor collegato;
- Digitare ed eseguire il comando `CREATE DATABASE sprangular` nel source editor;
- Creare ora una data source `sprangular@localhost`.

## Credenziali accesso al database:

| Username | Password |
| -------- | -------- |
| root     | root     |

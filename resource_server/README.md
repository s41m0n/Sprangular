# Sprangular

## AI University project

### How to start a persistent MariaDB docker container

Command to execute from CLI (replace `<local path>` with the path to the directory where you want to store the DB):

`docker run --name mariadb -v <local path>:/var/lib/mysql -e MYSQL_ROOT_PASSWORD=root -p 3306:3306 -d mariadb`

DB access credentials:

```
User: root
Psw:  root
```
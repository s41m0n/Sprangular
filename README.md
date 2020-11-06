# Sprangular

Progetto finale di Applicazioni Internet - sito web creato con Spring Boot e Angular

## Istruzioni per compilazione e deploy con docker

Verificare che nel file *applications.properties* sia settata la seguente datasource:
```spring.datasource.url=jdbc:mysql://mariadb_compose/sprangular```

Dalla cartella del server ./resource_server generare il .jar dell'applicazione Spring Boot con il seguente comando: 
```$ mvn clean package -DskipTests``` 
dove -DskipTests è necessario per evitare che il packaging fallisca a causa del mancato db che verrà deployato da docker;

Dalla cartella del server ./resource_server generare l'immagine docker per l'applicazione Spring Boot usando il dockerfile presente con il comando:
```$ docker build -t sprangular_server .```

Dalla cartella del client ./webapp generare l'immagine docker per l'applicazione Angular usando il dockerfile presente con il comando:
```$ docker build -t sprangular_client .```

Dalla cartella root del progetto deployare tutto il sistema usando il docker-compose presente con il comando:
```$ docker-compose up --force-recreate```

## Avviare l'applicazione

Per collegarsi all'applicazione è sufficiente aprire un browser al seguente link: https://localhost:4200/home

## Utenti già presenti

### Studenti:

| Id | Password |
| --- | ----------- |
| s264970 | toortoor |
| s264971 | toortoor |
| s264972 | toortoor |
| s264973 | toortoor |
| s264974 | toortoor |
| s264975 | toortoor |
| s264976 | toortoor |

### Docenti:

| Id | Password |
| --- | ----------- |
| d264970 | toortoor |
| d264971 | toortoor |
| d264972 | toortoor |
| d264973 | toortoor |

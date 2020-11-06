# Sprangular

Progetto finale di Applicazioni Internet - sito web creato con Spring Boot e Angular

## Istruzioni per compilazione e deploy con docker

Verificare che nel file *applications.properties* del server Spring sia settata la seguente datasource:
```spring.datasource.url=jdbc:mysql://mariadb_compose/sprangular```

Verificare che nel file *proxy.conf.json* del client Angular sia settato il seguente target:
```"target": "http://spring-jdbc:8080"```

Dalla cartella del server ./resource_server generare il .jar dell'applicazione Spring Boot con il seguente comando: 
```$ mvn clean package -DskipTests``` 
dove -DskipTests è necessario per evitare che il packaging fallisca a causa del mancato db che verrà deployato da docker;

Dalla cartella del server ./resource_server generare l'immagine docker per l'applicazione Spring Boot usando il dockerfile presente con il comando:
```$ docker build -t sprangular_server .```

Dalla cartella del client ./webapp generare l'immagine docker per l'applicazione Angular usando il dockerfile presente con il comando:
```$ docker build -t sprangular_client .```

Dalla cartella root del progetto deployare tutto il sistema usando il docker-compose presente con il comando:
```$ docker-compose up --force-recreate```

Il server potrebbe riavviarsi più volte durante il primo avvio, questo è dovuto al fatto che mariadb impiega un determinato lasso di tempo per creare il database e fino a che il server non riesce a connettersi ritorna un messaggio di errore, che causa il riavvio automatico. 

## Avviare l'applicazione

Per collegarsi all'applicazione è sufficiente aprire un browser al seguente link: https://localhost:4200/home

## Gestione del database

Il database viene gestito tramite un container mariadb che utilizza come volume una carella creata in automatico: ```/home/$USER/db```

## Utenti già presenti nel database

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

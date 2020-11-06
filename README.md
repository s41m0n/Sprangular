# Sprangular

Progetto finale di Applicazioni Internet - sito web creato con Spring Boot e Angular

## Istruzioni per compilazione e deploy con Docker

Nel file _applications.properties_ del server Spring settare la seguente datasource:
`spring.datasource.url=jdbc:mysql://mariadb_compose/sprangular`

Nel file _proxy.conf.json_ del client Angular settare il seguente target:
`"target": "http://spring-jdbc:8080"`

Dalla cartella del server ./resource_server generare il .jar dell'applicazione Spring Boot con il seguente comando:
`$ mvn clean package -DskipTests`
dove -DskipTests è necessario per evitare che il packaging fallisca a causa del mancato db che verrà deployato da docker;

Dalla cartella del server ./resource_server generare l'immagine docker per l'applicazione Spring Boot usando il dockerfile presente con il comando:
`$ docker build -t sprangular_server .`

Dalla cartella del client ./webapp generare l'immagine docker per l'applicazione Angular usando il dockerfile presente con il comando:
`$ docker build -t sprangular_client .`

Dalla cartella root del progetto deployare tutto il sistema usando il docker-compose presente con il comando:
`$ docker-compose up`

Il server potrebbe riavviarsi più volte durante il primo avvio, questo è dovuto al fatto che mariadb impiega un determinato lasso di tempo per creare il database e fino a che il server non riesce a connettersi ritorna un messaggio di errore, che causa il riavvio automatico.

## Avviare l'applicazione

Per collegarsi all'applicazione è sufficiente, dopo averla deployata con Docker, aprire un browser al seguente link: https://localhost:4200/home

## Gestione del database

Il database viene gestito tramite un container mariadb che utilizza come volume una carella creata in automatico: `/home/$USER/db`

## Gestione dell'invio di e-mail

Per evitare di mandare email a utenti sconosciuti del Politecnico tutte le email dell'applicazione vengono inviate a noreply.sprangular@gmail.com. Sulla console di Spring viene comunque stampato un messaggio di log che indica il destinatario reale a cui andrebbe inviato il messaggio.

## Utenti già presenti nel database

### Studenti:

| Id      | Password |
| ------- | -------- |
| s264970 | toortoor |
| s264971 | toortoor |
| s264972 | toortoor |
| s264973 | toortoor |
| s264974 | toortoor |
| s264975 | toortoor |
| s264976 | toortoor |

### Docenti:

| Id      | Password |
| ------- | -------- |
| d264970 | toortoor |
| d264971 | toortoor |
| d264972 | toortoor |
| d264973 | toortoor |

## Modifiche ai file per il development in locale senza bisogno di usare Docker

Queste modifiche permettono di svilppare codice in locale senza avere un deployment di Docker per client e server. Per il database rimane necessario utilizzare Docker.
Le istruzioni per configurare correttamente un database con mariaDB in docker e per come utilizzare il plugin di IntelliJ IDEA per la gesione dei database sono presenti nel file _README.md_ all'interno della cartella del server ./resource_server.

Nel file _applications.properties_ del server Spring settare la seguente datasource:
`spring.datasource.url=jdbc:mysql://localhost:3306/sprangular`

Nel file _proxy.conf.json_ del client Angular settare il seguente target:
`"target": "http://localhost:8080"`

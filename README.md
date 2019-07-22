# Banking  app to debit, credit accounts and do money transfers
1. Package structure is Domain driven rather than layered
2. Application runs on Embedded Jetty server
3. Jax-rs implementation is done in Jersey
3. HK2 is used for Dependency Injection
3. JPA is used for data access (Hibernate)

# Database tables

1. savings_account
2. transaction

# Things to improve if more time is given:

1. Tests have been written to check only debit credit and transfers(Supporting code is not tested)
2. Constants in app could have been moved to prop files
3. Data access is the part of the service which could have been further abstracted to a separate class


# Build

mvn clean install -> creates an uber jar(banking-app.jar), inside the target folder

# Run

java -jar banking-app.jar   
App is started on port 8080 (can be changed using an environment variable BANKING_APP_PORT)  
Project has a postman collection(revolut.postman_collection.json) please import it for testing  
You can also find the published collection -> https://documenter.getpostman.com/view/3426820/SVSPmRLf

# Endpoints

1. Account(endpoint for account management)
    localhost:8080/api/v1/accounts/savings
2. Transaction(endpoint to do transactions)
    localhost:8080/api/v1/accounts/savings/transactions

# Transaction Management

Programmatic transaction management using JPA EntityManager

# Concurrency

It is assumed that database is accessed only through the application running on a single server.  
Fine grained locking is done using ReadwriteLocks.  
In an attempt to improve parallelism locks are created for each account(guava cache is used to manage them).

# Deployed app

Sample app has been deployed at http://43.225.52.99  
endpoints it can be accessed through postman




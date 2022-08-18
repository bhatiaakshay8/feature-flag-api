# feature-flag-api
Feature Flag Api Service

### Assumptions taken
1. Each feature can have more than 2 dependency.
   Since it is not written I am considering dependency as array list
   instead of 2 separate variables which I would have taken in case of a classic Tree (with 2 branches at each point)
2. Clearing previous Feature Flags before inserting new (that is why this is a PUT request)
3. Sort and Store both done as part of one.
   Not using additional DS to store sorted order. But if needed we can split the steps and create a list

### Module structure
- `feature-flag-api-db-management` - wraps Liquibase and Liquibase changelogs for DB versioning
- `feature-flag-api-service` - application entry point application context and rest controllers defined here
- `mssql` - Containerised MS SQL instance


### Build and Deploy

- Run `./deploy-all.sh` from root folder of this repo. It will 

- You can access the service at:
`localhost:${PORT}` PORT (currently 8071) is defined in `feature-flag-api-service/src/main/docker/env.list`
Swagger Documentation which can be used to test the api at:
`http://localhost:${PORT}/swagger-ui/index.html`

Authentication also implemented with credentials in `feature-flag-api-service/src/main/docker/env.list`:

`username`: admin
`password`: 123

### Stop the docker container 

run `docker compose stop`

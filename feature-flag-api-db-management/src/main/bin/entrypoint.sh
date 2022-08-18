#!/usr/bin/env bash

export PATH=$PATH:/liquibase

DATABASE_URL="jdbc:sqlserver://${DATABASE_HOST}:${DATABASE_PORT};databaseName=${DATABASE_NAME};trustServerCertificate=true"

echo "WAITING FOR MSSQL initialization ..."
sleep 15

# --classpath is used for pointing to both drivers and changelogs
# Some later Liquibase versions may use an additional --searchPath CLI option to specify changelogs

COMMAND="liquibase --logLevel=${LIQUIBASE_LOG_LEVEL}
          --classpath=${LIQUIBASE_CLASSPATH}
          --changeLogFile=${LIQUIBASE_CHANGELOG_NAME}
          --url=${DATABASE_URL} --username=${DATABASE_USERNAME} --password=${DATABASE_PASSWORD}
          --defaultSchemaName=${DATABASE_DEFAULT_SCHEMA_NAME} ${LIQUIBASE_COMMAND}"

echo "Running the following command: ${COMMAND}"
${COMMAND}

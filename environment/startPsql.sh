#!/bin/bash
source ../.env
docker run -ti --network environment_default -v $(pwd):/home \
  -e POSTGRES_USER=$POSTGRESQL_USER \
  -e POSTGRES_PWD=$POSTGRESQL_PWD \
  -e POSTGRES_DB=$POSTGRESQL_DBNAME \
  -e POSTGRES_HOST=$POSTGRESQL_HOST \
  postgres bash 

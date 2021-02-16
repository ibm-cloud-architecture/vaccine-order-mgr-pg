#!/bin/bash

source ./setEnv.sh
export  SERVICE_ACCOUNT_NAME=postgres-sa
export  DEPLOYMENT_NAME=postgres
export  SERVICE_NAME=postgres
export  DOCKER_IMAGE=docker.io/postgres:11.6-alpine

oc project $PROJECT_NAME 
echo "------------------ Deploy Postgresql-------------------"

oc create serviceaccount ${SERVICE_ACCOUNT_NAME}
oc adm policy add-scc-to-user anyuid -n ${PROJECT_NAME} -z ${SERVICE_ACCOUNT_NAME}
oc create deployment ${DEPLOYMENT_NAME} --image=${DOCKER_IMAGE}
oc set serviceaccount deployment/${DEPLOYMENT_NAME} ${SERVICE_ACCOUNT_NAME}
oc patch deployment ${DEPLOYMENT_NAME} --type="json" -p='[{"op":"add", "path":"/spec/template/spec/containers/0/args", "value":[]},{"op":"add", "path":"/spec/template/spec/containers/0/args/-", "value":"-c"},{"op":"add", "path":"/spec/template/spec/containers/0/args/-", "value":"wal_level=logical"} ]'
oc set env deployment ${DEPLOYMENT_NAME} POSTGRES_PASSWORD=${POSTGRES_PASSWORD}
oc expose deployment ${DEPLOYMENT_NAME} --port 5432 --name ${SERVICE_NAME}

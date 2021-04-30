#!/bin/bash

source ../.env-remote


oc project $PROJECT_NAME 


echo "------------------ Get EventStreams / Kafka Credentials -------------------"
export KAFKA_BROKERS=$(oc get route -n ${EVENTSTREAMS_NS} ${CLUSTER_NAME}-kafka-bootstrap -o jsonpath="{.status.ingress[0].host}:443")

oc get secret ${KAFKA_USER} -n ${EVENTSTREAMS_NS} -o json | jq -r '.metadata.namespace="'${PROJECT_NAME}'"' | oc apply -f -

oc get secret ${CLUSTER_NAME}-cluster-ca-cert -n ${EVENTSTREAMS_NS} -o json | jq -r '.metadata.name="kafka-cluster-ca-cert"' |jq -r '.metadata.namespace="'${PROJECT_NAME}'"' | oc apply -f -


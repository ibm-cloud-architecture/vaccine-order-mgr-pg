#!/bin/bash
scriptDir=$(dirname $0)

IMAGE_NAME=quay.io/ibmcase/vaccineorderms
./mvnw clean package -Dui.deps -Dui.dev -DskipTests -Dquarkus.profile=prod
docker build -f src/main/docker/Dockerfile.jvm -t ${IMAGE_NAME} .
docker push ${IMAGE_NAME}

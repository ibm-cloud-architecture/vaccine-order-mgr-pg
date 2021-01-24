# Vaccine Order Manager event-driven microservice

This service is responsible to manage the Vaccine Order entity. It is done with Smallrye microprofile and reactive messaging with Kafka, hibernate ORM with panache for DB2 database, Quarkus stack and Tekton pipeline.

Visit detail implementation approach, design and different deployment model, read explanations of this service in [the main solution documentation](https://ibm-cloud-architecture.github.io/vaccine-solution-main/solution/orderms/).

The goals of this project are:

* Quarkus app with [Debezium outbox](https://debezium.io/documentation/reference/integrations/outbox.html) extension
* Reactive REST APP with Mutiny
* JPA with Hibernate and Panache with Postgresql database
* Debezium Postgres [Change Data Capture connector]() to publish OrderEvents to Kafka topic
* Consume ShipmentPlan from Kafka using reactive messaging


## Build deploy on OpenShift

Be sure to define the parameters in the configmap in `src/main/kubernetes/configmap.yaml` and secret in src/main/kubernetes/secrets.yaml (the strings in the secret are base64 encoded) then do

```shell
# Example encoding a user:
echo "app-scram" | base64 
oc apply -f src/main/kubernetes/configmap.yaml
oc apply -f src/main/kubernetes/secrets.yaml
```

The application uses Quarkus OpenShift extension to create yaml files for OpenShift and deploy the application using source to image:

```shell
./mvnw clean package -Dui.deps -Dui.dev -Dquarkus.kubernetes.deploy=true -DskipTests
```

The `-Dui.deps -Dui.dev` arguments are used to prepare and build the vue.js app from the `ui` folder. The packaging build a runner jar and push it to the private image registry in OpenShift.


## Build and run locally

### Run with remote services

It is possible to run the quarkus app to connect to the remote Postgres and Kafka both deployed on OpenShift.

Set the following environment variables in a `.env` file, and get the truststore.p12 file from the Kafka configuration

 ```shell
 export QUARKUS_DATASOURCE_USERNAME=postgres
 export QUARKUS_DATASOURCE_PASSWORD=<>
 export POSTGRESQL_DBNAME=postgres
 export QUARKUS_DATASOURCE_JDBC_URL=jdbc:postgresql://localhost:15432/postgres
 export KAFKA_USER=<user with scram>
 export KAFKA_PASSWORD=<user psw>
 export KAFKA_BOOTSTRAP_SERVERS=<url bootstratp>.us-east.containers.appdomain.cloud:443
 export KAFKA_SSL_TRUSTSTORE_LOCATION=${PWD}/truststore.p12
 export KAFKA_SSL_TRUSTSTORE_PASSWORD=<pwd of the truststore> 
 ```

 ```shell
 # be sure to have packaged the order app first with the following command which also builds the UI
 source .env
 ./mvnw package -Dui.deps -Dui.dev -DskipTests
 # If the UI does not need to be built again just do:
 ./mvnw quarkus:dev 
 ```

### Run with docker compose

As an alternate we have defined two docker compose files to run the docker image of the service or run maven to build and execute `quarkus:dev` continuously.

* From docker image:

```shell
./mvnw package -Dui.deps -Dui.dev -D -DskipTests
 # build the image
 docker build -f src/main/docker/Dockerfile.jvm -t ibmcase/vaccineorderms  .
 # Start local environment 
 cd environment
 docker-compose  up -d 
 ```

* With maven:

 If you want to start everything in development mode, the vaccine order service is executed via a maven container which starts `quarkus:dev`. Therefore the command is using another compose file: 

 ```shell
 cd environment
 docker-compose -f dev-docker-compose.yaml up -d
 ```

* Define Kafka topics

Some topics are created by the Kafka Connector.

```shell
# Under environment folder
./createTopic.sh
# validate topics created
./listTopics.sh

__consumer_offsets

vaccine_shipment_plans
```

## Debezium CDC connector

The [Debezium Postgres connector](https://debezium.io/documentation/reference/connectors/postgresql.html) is a Kafka Connector. So to deploy to Event Streams or Strimzi Connector we will use source to image approach.

* From the Event Streams UI > Toolbox > Kafka Connect menu, download the source to image yaml file.
* Modify the file to get connection to the Kafka brokers
* Add declaration to use Postgres connector
* Deploy with `oc apply -f environment/cdc/kafka-connect-s2i.yaml`


* Start a consumer on the CDC topic for the order events

 ```shell
 docker-compose exec kafka /opt/kafka/bin/kafka-console-consumer.sh     --bootstrap-server kafka:9092     --from-beginning     --property print.key=true     --topic vaccine_lot_db.DB2INST1.ORDEREVENTS
 ```

* Add new order from the user interface: http://localhost:8080/#/Orders, or...

* Post an order using the API: [http://localhost:8080/swagger-ui/#/default/post_orders](http://localhost:8080/swagger-ui/#/default/post_orders). Use the following JSON

 ```json
 {
    "deliveryDate": "2021-07-25",
    "deliveryLocation": "Milano",
    "askingOrganization": "Italy gov",
    "priority": 1,
    "quantity": 100,
    "type": "COVID-19"
 }
 ```

 * The expected result should have the following records in the Kafka topic:

 ```json
 {"ID":"lvz4gYs/Q+aSqKmWjVGMXg=="}	
 {"before":null,"after":{"ID":"lvz4gYs/Q+aSqKmWjVGMXg==","AGGREGATETYPE":"VaccineOrderEntity","AGGREGATEID":"21","TYPE":"OrderCreated","TIMESTAMP":1605304440331350,"PAYLOAD":"{\"orderID\":21,\"deliveryLocation\":\"London\",\"quantity\":150,\"priority\":2,\"deliveryDate\":\"2020-12-25\",\"askingOrganization\":\"UK Governement\",\"vaccineType\":\"COVID-19\",\"status\":\"OPEN\",\"creationDate\":\"13-Nov-2020 21:54:00\"}"},"source":{"version":"1.3.0.Final","connector":"db2","name":"vaccine_lot_db","ts_ms":1605304806596,"snapshot":"last","db":"TESTDB","schema":"DB2INST1","table":"ORDEREVENTS","change_lsn":null,"commit_lsn":"00000000:0000150f:0000000000048fca"},"op":"r","ts_ms":1605304806600,"transaction":null}
 ```

## Tests

Unit and integration tests are done with Junit 5 and Test Container when needed or mockito to avoid backend access for CI/CD.

For end to end testing the `e2e` folder includes some python scripts to test new order creation.

```shell
cd e2e
./post-order.sh
```

## UI development

For UI development start the components with `docker-compose  -f dev-docker-compose.yaml up -d`, then under the ui folder, do the following:

```
yarn install
yarn serve
```

Use the web browser and developer console to the address [http://localhost:4545](http://localhost:4545). The Vue app is configured to proxy to `localhost:8080`.


## Troubleshooting

### Logs:

```shell
# microservice logs:

```

### Connector operations

To delete the CDC connector:

```
curl -i -X DELETE  http://localhost:8083/connectors/orderdb-connector
```

### Errors

## Git Action

This repository includes a Github [workflow](https://github.com/ibm-cloud-architecture/vaccine-order-mgr/blob/master/.github/workflows/dockerbuild.yaml) to build the app and push a new docker image to public registry. To do that we need to define 4 secrets in the github repository:

* DOCKER_IMAGE_NAME the image name to build. Here it is `vaccineorderms`
* DOCKER_USERNANE: user to access docker hub
* DOCKER_PASSWORD: and its password.
* DOCKER_REPOSITORY for example the organization we use is `ibmcase`

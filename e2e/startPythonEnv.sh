docker run --network vaccine-order-mgr-pg_default  -v $(pwd):/home -e KAFKA_BROKERS=kafka:9092 \
      -e SCHEMA_REGISTRY_URL=http://apicurio:8090 \
      -ti quay.io/ibmcase/vaccine-order-optimizer  bash

# ibmcase/python37
echo "------------------ Deploy Order Manager Service -------------------"

source ./setenv.sh

oc apply -f - << EOF
apiVersion: v1
kind: Secret
metadata:
  name: vaccine-order-secrets
stringData:
  KAFKA_USER: ${KAFKA_USER}
  QUARKUS_DATASOURCE_PASSWORD: ${POSTGRES_PASSWORD}
  QUARKUS_DATASOURCE_USERNAME: postgres
EOF

oc apply -f ../kubernetes/configmap.yaml
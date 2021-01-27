import os

KAFKA_BROKERS = os.getenv('KAFKA_BROKERS','localhost:9092')
KAFKA_CERT_PATH = os.getenv('KAFKA_CERT_PATH','')
KAFKA_USER =  os.getenv('KAFKA_USER','')
KAFKA_PASSWORD =  os.getenv('KAFKA_PASSWORD','')
KAFKA_SASL_MECHANISM =  os.getenv('KAFKA_SASL_MECHANISM','SCRAM-SHA-512')
SCHEMA_REGISTRY_URL =os.getenv('SCHEMA_REGISTRY_URL','localhost:9092')
KAFKA_CERT = os.getenv('KAFKA_CERT','/app/certs/es-cert.pem')
CONSUMER_GROUP = os.getenv("CONSUMER_GROUP","vaccine-optimizer")
import json,os
from confluent_kafka import KafkaError

from confluent_kafka import SerializingProducer
from confluent_kafka.serialization import StringSerializer
from confluent_kafka.schema_registry import SchemaRegistryClient
from confluent_kafka.schema_registry.avro import AvroSerializer
import kafka.KafkaConfig as config

class KafkaAvroProducer:

    def __init__(self, producer_name, value_schema, groupID = 'KafkaAvroProducer',
                kafka_brokers = "", 
                kafka_user = "", 
                kafka_pwd = "", 
                kafka_cacert = "", 
                kafka_sasl_mechanism = "", 
                topic_name = ""):
        self.kafka_brokers = kafka_brokers
        self.kafka_user = kafka_user
        self.kafka_pwd = kafka_pwd
        self.kafka_sasl_mechanism = kafka_sasl_mechanism
        self.kafka_cacert = kafka_cacert
        self.topic_name = topic_name
        # Consumer name for logging purposes
        self.logging_prefix = '['+ producer_name + '][KafkaAvroProducer]'
        # Schema Registry configuration
        self.schema_registry_conf = {'url': config.SCHEMA_REGISTRY_URL}
        # Schema Registry Client
        self.schema_registry_client = SchemaRegistryClient(self.schema_registry_conf)

        # String Serializer for the key
        self.key_serializer = StringSerializer('utf_8')
        # Avro Serializer for the value
        print(value_schema)
        print(type(value_schema))
        value_schema=value_schema.strip()
        self.value_serializer = AvroSerializer(value_schema, self.schema_registry_client)
        
        # Get the producer configuration
        self.producer_conf = self.getProducerConfiguration(groupID,
                        self.key_serializer,
                        self.value_serializer)
        # Create the producer
        self.producer = SerializingProducer(self.producer_conf)



    def delivery_report(self,err, msg):
        """ Called once for each message produced to indicate delivery result. Triggered by poll() or flush(). """
        if err is not None:
            print('[KafkaAvroProducer] - [ERROR] - Message delivery failed: {}'.format(err))
        else:
            print('[KafkaAvroProducer] - Message delivered to {} [{}]'.format(msg.topic(), msg.partition()))

    def publishEvent(self, key, value, topicName = 'kafka-avro-producer'):
        # Produce the Avro message
        self.producer.produce(topic=topicName,value=value,key=key, on_delivery=self.delivery_report)
        # Flush
        self.producer.flush()


    def getProducerConfiguration(self,groupID,key_serializer,value_serializer):
        try:
            options ={
                    'bootstrap.servers': os.environ['KAFKA_BROKERS'],
                    'group.id': groupID,
                    'key.serializer': key_serializer,
                    'value.serializer': value_serializer
            }
            if (os.getenv('KAFKA_PASSWORD','') != ''):
                # Set security protocol common to ES on prem and on IBM Cloud
                options['security.protocol'] = 'SASL_SSL'
                # Depending on the Kafka User, we will know whether we are talking to ES on prem or on IBM Cloud
                # If we are connecting to ES on IBM Cloud, the SASL mechanism is plain
                if (os.getenv('KAFKA_USER','') == 'token'):
                    options['sasl.mechanisms'] = 'PLAIN'
                # If we are connecting to ES on OCP, the SASL mechanism is scram-sha-512
                else:
                    options['sasl.mechanisms'] = 'SCRAM-SHA-512'
                # Set the SASL username and password
                options['sasl.username'] = os.getenv('KAFKA_USER','')
                options['sasl.password'] = os.getenv('KAFKA_PASSWORD','')
            # If we are talking to ES on prem, it uses an SSL self-signed certificate.
            # Therefore, we need the CA public certificate for the SSL connection to happen.
            if (os.path.isfile(os.getenv('KAFKA_CERT','/certs/es-cert.pem'))):
                options['ssl.ca.location'] = os.getenv('KAFKA_CERT','/certs/es-cert.pem')
            return options

        except KeyError as error:
            print('[KafkaAvroProducer] - [ERROR] - A required environment variable does not exist: ' + error)
            return {}
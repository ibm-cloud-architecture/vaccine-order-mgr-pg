import logging, json, uuid
from datetime import datetime
import avro.schema
from kafka.KafkaAvroProducer import KafkaAvroProducer
import kafka.KafkaConfig as config



def getCloudEventSchema(schema_files_location = "avro/"):
    shipment = "shipment.avsc"
    shipmentPlan = "shipment_plan.avsc"
    cloudEvent = "cloudEvent.avsc"
    known_schemas = avro.schema.Names()
    shipment_schema = LoadAvsc(schema_files_location + shipment, known_schemas)
    shipment_plan_schema = LoadAvsc(schema_files_location + shipmentPlan, known_schemas)
    cloudEvent_schema = LoadAvsc(schema_files_location + cloudEvent, known_schemas)
    return cloudEvent_schema

def LoadAvsc(file_path, names=None):
  # Load avsc file
  # file_path: path to schema file
  # names(optional): avro.schema.Names object
  file_text = open(file_path).read()
  json_data = json.loads(file_text)
  schema = avro.schema.SchemaFromJSONData(json_data, names)
  return schema

'''
Product some shipment plan
'''
if __name__ == '__main__':
    print("Start Shipment plan Producer")
    logging.basicConfig(level=logging.INFO)
    cloudEvent_schema = getCloudEventSchema()
    strschema = json.dumps(cloudEvent_schema.to_json(),indent=2)
    producer = KafkaAvroProducer(
                producer_name = "ShipmentProducer",
                value_schema = strschema,
                kafka_brokers = config.KAFKA_BROKERS, 
                kafka_user = config.KAFKA_USER, 
                kafka_pwd = config.KAFKA_PASSWORD, 
                kafka_cacert = config.KAFKA_CERT_PATH, 
                kafka_sasl_mechanism=config.KAFKA_SASL_MECHANISM,
                topic_name = config.TOPIC_NAME)
    shipmentPlans = [{'planID': 'plan01','orderID': '1','from': "Beerse, Belgium",'departureDate': '2021-01-21', 'type': 'Delivery', 'to': 'Paris, France','arrivalDate': '2021-01-21','quantity': 150, 'reefers': 3, 'cost': 50}]
    cloudevent = {}  
    cloudevent['type'] = "ibm.gse.eda.vaccine.orderoptimizer.VaccineOrderCloudEvent"
    cloudevent['specversion'] = "1.0"
    cloudevent['source'] = "Vaccine Order Optimizer engine"
    cloudevent['id'] = str(uuid.uuid4())
    cloudevent['time'] = datetime.datetime.now(datetime.timezone.utc).isoformat()
    cloudevent['dataschema'] = "https://raw.githubusercontent.com/ibm-cloud-architecture/vaccine-order-optimizer/master/data/avro/schemas/shipment_plan.avsc"
    cloudevent['datacontenttype'] =	"application/json"
    cloudevent['data'] = { "Shipments": shipmentPlans}
    producer.publishEvent(cloudevent["id"],cloudevent,config.TOPIC_NAME)
    

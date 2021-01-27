import logging
from kafka.KafkaProducer import KafkaProducer
import kafka.eventStreamsConfig as config

'''
Product some shipment plan
'''
if __name__ == '__main__':
    print("Start Shipment plan Producer")
    logging.basicConfig(level=logging.INFO)
    producer = KafkaProducer(kafka_brokers = config.KAFKA_BROKERS, 
                kafka_user = config.KAFKA_USER, 
                kafka_pwd = config.KAFKA_PASSWORD, 
                kafka_cacert = config.KAFKA_CERT_PATH, 
                kafka_sasl_mechanism=config.KAFKA_SASL_MECHANISM,
                topic_name = "vaccine_shipment_plans")
    producer.prepare("ShipmentPlanProducer")
    shipmentPlan = {'planID': 'plan01','orderID': '1','from': "Beerse, Belgium",'departureDate': '2021-01-21', 'type': 'Delivery', 'to': 'Paris, France','arrivalDate': '2021-01-21','quantity': 150, 'reefers': 3, 'cost': 50}
             
    producer.publishEvent(shipmentPlan,"planID")
    
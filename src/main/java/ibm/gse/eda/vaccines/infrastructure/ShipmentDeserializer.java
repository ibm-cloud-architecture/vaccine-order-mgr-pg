package ibm.gse.eda.vaccines.infrastructure;
import ibm.gse.eda.vaccines.domain.events.CloudEvent;
import io.quarkus.kafka.client.serialization.JsonbDeserializer;

public class ShipmentDeserializer extends JsonbDeserializer<CloudEvent> {
    public ShipmentDeserializer(){
        // pass the class to the parent.
        super(CloudEvent.class);
    }
    
}

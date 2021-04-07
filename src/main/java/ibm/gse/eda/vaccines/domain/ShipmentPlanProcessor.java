package ibm.gse.eda.vaccines.domain;

import java.util.HashMap;

import javax.enterprise.context.ApplicationScoped;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.jboss.logging.Logger;

import ibm.gse.eda.vaccines.api.dto.ShipmentPlan;
import ibm.gse.eda.vaccines.domain.events.ShipmentPlansEvent;
import ibm.gse.eda.vaccines.domain.events.ShipmentPlanEvent;
import ibm.gse.eda.vaccines.domain.events.CloudEvent;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.smallrye.reactive.messaging.kafka.IncomingKafkaCloudEventMetadata;
import io.smallrye.reactive.messaging.ce.IncomingCloudEventMetadata;
import io.smallrye.reactive.messaging.kafka.IncomingKafkaRecord;
import io.smallrye.reactive.messaging.ce.CloudEventMetadata;

@ApplicationScoped
public class ShipmentPlanProcessor {
    private Logger logger = Logger.getLogger(ShipmentPlanProcessor.class);
    private Jsonb jsonb = JsonbBuilder.create();

    private HashMap<String,ShipmentPlan> plans = new HashMap<String,ShipmentPlan>();
    
    public ShipmentPlanProcessor(){
    }

    @Incoming("shipments")
    public Uni<Void> process(Message<ShipmentPlansEvent> evt){
        IncomingCloudEventMetadata<ShipmentPlansEvent> cloudEventMetadata = evt.getMetadata(IncomingCloudEventMetadata.class).orElseThrow(() -> new IllegalArgumentException("Expected a Cloud Event"));
        logger.infof("Received Cloud Events (spec-version: %s): source:  '%s', type: '%s', id: '%s' ",
                cloudEventMetadata.getSpecVersion(),
                cloudEventMetadata.getSource(),
                cloudEventMetadata.getType(),
                cloudEventMetadata.getId());
        // TO-DO: iterate through the Shipments, convert them to ShipmentPlan and add these to the plans variable.
        ShipmentPlansEvent spse = evt.getPayload();
        for (ShipmentPlanEvent spe : spse.getShipments()){
            plans.put(cloudEventMetadata.getId(),ShipmentPlan.from(spe));
        }
        //logger.info("Event received: " + jsonb.toJson(spe));
        // ShipmentPlanEvent planEvt = evt.getPayload();
        // plans.put(planEvt.planID,ShipmentPlan.from(planEvt));
        return Uni.createFrom().voidItem();
    }

    // // Use a Java class for deserializing rather than the mp messaging CloudEvent libraries
    // @Incoming("shipments")
    // public Uni<Void> process(CloudEvent ce){
    //     logger.info(ce.toString());
    //     new IllegalArgumentException("blahblahblahlbahblahblah");
    //     // ShipmentPlanEvent planEvt = evt.getPayload();
    //     // plans.put(planEvt.planID,ShipmentPlan.from(planEvt));
    //     return Uni.createFrom().voidItem();
    // }

    public Multi<ShipmentPlan> stream() {
        return Multi.createFrom().items(plans.values().stream());
    }
}

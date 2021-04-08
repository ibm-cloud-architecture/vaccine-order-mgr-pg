package ibm.gse.eda.vaccines.domain;

import java.util.HashMap;

import javax.enterprise.context.ApplicationScoped;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;

import org.apache.avro.generic.GenericRecord;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.jboss.logging.Logger;

import ibm.gse.eda.vaccines.api.dto.ShipmentPlan;
import ibm.gse.eda.vaccines.domain.events.CloudEvent;
import ibm.gse.eda.vaccines.domain.events.ShipmentPlanEvent;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;

@ApplicationScoped
public class ShipmentPlanProcessor {
    private Logger logger = Logger.getLogger(ShipmentPlanProcessor.class);
    private Jsonb jsonb = JsonbBuilder.create();

    private HashMap<String,ShipmentPlan> plans = new HashMap<String,ShipmentPlan>();
    
    public ShipmentPlanProcessor(){
    }

    @Incoming("shipments")
    public Uni<Void> process(Message<GenericRecord> evt){  
        GenericRecord spse = evt.getPayload();
        int idx = 0;
        logger.info(spse);
        CloudEvent ce = jsonb.fromJson(spse.toString(), CloudEvent.class);
        for (ShipmentPlanEvent spe : ce.data.Shipments) {
            logger.info("Event received: " + jsonb.toJson(spe));
            plans.put("SHIP_" + idx,ShipmentPlan.from(spe));
            idx++;
        }
        //
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

package ibm.gse.eda.vaccines.domain;

import java.util.HashMap;

import javax.enterprise.context.ApplicationScoped;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.jboss.logging.Logger;

import ibm.gse.eda.vaccines.api.dto.ShipmentPlan;
import ibm.gse.eda.vaccines.domain.events.ShipmentPlanEvent;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.smallrye.reactive.messaging.kafka.IncomingKafkaCloudEventMetadata;

@ApplicationScoped
public class ShipmentPlanProcessor {
    private Logger logger = Logger.getLogger(ShipmentPlanProcessor.class);
    private Jsonb jsonb = JsonbBuilder.create();

    private HashMap<String,ShipmentPlan> plans = new HashMap<String,ShipmentPlan>();
    
    public ShipmentPlanProcessor(){
    }

    @Incoming("shipments")
    public Uni<Void> process(Message<ShipmentPlanEvent> evt){
        IncomingKafkaCloudEventMetadata<String,ShipmentPlanEvent> cloudEventMetadata = evt.getMetadata(IncomingKafkaCloudEventMetadata.class).orElseThrow(() -> new IllegalArgumentException("Expected a Cloud Event"));
        logger.info("Event received: " + jsonb.toJson(evt));
        ShipmentPlanEvent planEvt = evt.getPayload();
        plans.put(planEvt.planID,ShipmentPlan.from(planEvt));
        return Uni.createFrom().voidItem();
    }

    public Multi<ShipmentPlan> stream() {
        return Multi.createFrom().items(plans.values().stream());
    }
}

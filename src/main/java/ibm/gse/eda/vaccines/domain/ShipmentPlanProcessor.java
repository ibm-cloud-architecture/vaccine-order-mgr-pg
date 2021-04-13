package ibm.gse.eda.vaccines.domain;

import java.util.ArrayList;
import java.util.HashMap;

import javax.enterprise.context.ApplicationScoped;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;

import org.apache.avro.generic.GenericRecord;
import org.eclipse.microprofile.reactive.messaging.Acknowledgment;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.eclipse.microprofile.reactive.messaging.Outgoing;
import org.jboss.logging.Logger;

import ibm.gse.eda.vaccines.api.dto.ShipmentPlans;
import ibm.gse.eda.vaccines.domain.events.CloudEvent;
import ibm.gse.eda.vaccines.domain.events.ShipmentPlanEvent;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.smallrye.reactive.messaging.annotations.Broadcast;

@ApplicationScoped
public class ShipmentPlanProcessor {
    private Logger logger = Logger.getLogger(ShipmentPlanProcessor.class);
    private Jsonb jsonb = JsonbBuilder.create();

    private HashMap<String,ShipmentPlan> plans = new HashMap<String,ShipmentPlan>();
    
    public ShipmentPlanProcessor(){
    }

    @Incoming("shipments")
    @Outgoing("internal-plan-stream")                             
    @Broadcast                                              
    @Acknowledgment(Acknowledgment.Strategy.POST_PROCESSING)
    public Uni<ShipmentPlans> process(GenericRecord spse){  
        //GenericRecord spse = evt.getPayload();
        int idx = 0;
        logger.info(spse);
        CloudEvent ce = jsonb.fromJson(spse.toString(), CloudEvent.class);
        String planID = ce.id;
        for (ShipmentPlanEvent spe : ce.data.Shipments) {
            ShipmentPlan plan = ShipmentPlan.from(spe);
            logger.info(plan.toString());
            plans.put("SHIP_" + idx,plan);
            idx++;
        }
        ShipmentPlans shipmentPlans = new ShipmentPlans();
        shipmentPlans.planID = planID;
        shipmentPlans.plans = new ArrayList<ShipmentPlan>(this.plans.values());
        return Uni.createFrom().item(shipmentPlans);
    }

    public Multi<ShipmentPlan> getAllPlans() {
        return Multi.createFrom().items(plans.values().stream());
    }
}

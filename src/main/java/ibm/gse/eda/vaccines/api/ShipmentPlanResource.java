package ibm.gse.eda.vaccines.api;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.jboss.resteasy.annotations.SseElementType;
import org.reactivestreams.Publisher;

import ibm.gse.eda.vaccines.api.dto.ShipmentPlans;
import ibm.gse.eda.vaccines.domain.ShipmentPlan;
import ibm.gse.eda.vaccines.domain.ShipmentPlanProcessor;
import io.smallrye.mutiny.Multi;


@Path("api/v1/shipments")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ShipmentPlanResource {
    
    @Channel("internal-plan-stream")
    Publisher<ShipmentPlans> plans;

    @Inject
    protected ShipmentPlanProcessor shipmentPlanProcessor;
   

    @GET
    @Path("/stream")
    @Produces(MediaType.SERVER_SENT_EVENTS)
    @SseElementType(MediaType.APPLICATION_JSON)
    public Publisher<ShipmentPlans> getAllPlansAsStream(){
        return plans;
    }

    @GET
    public Multi<ShipmentPlan> getAllPlans(){
        return shipmentPlanProcessor.getAllPlans();
    }


}

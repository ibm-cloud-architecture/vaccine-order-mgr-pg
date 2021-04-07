package ibm.gse.eda.vaccines.domain.events;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class ShipmentPlansEvent {

    public ShipmentPlanEvent[] Shipments;

    public ShipmentPlansEvent(){}

    public ShipmentPlanEvent[] getShipments(){
        return Shipments;
    }
}

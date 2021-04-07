package ibm.gse.eda.vaccines.domain.events;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class ShipmentPlanEvent {

    public String Type;
    public String From;
    public String DepartureDate;
    public String To;
    public String ArrivalDate;
    public int Qty;
    public int Reefers;
    public int Cost;

    public ShipmentPlanEvent(){}
}

package ibm.gse.eda.vaccines.api.dto;

import ibm.gse.eda.vaccines.domain.events.ShipmentPlanEvent;


public class ShipmentPlan {
    public String orderID;
    public String from;
    public String departureDate;
    public String to;
    public String arrivalDate;
    public double quantity;
    public int reefers;
    public double cost;
    public String type;


	public static ShipmentPlan from(ShipmentPlanEvent evt) {
        ShipmentPlan sp = new ShipmentPlan();
        sp.type = evt.Type;
        sp.from = evt.From;
        sp.departureDate = evt.DepartureDate;
        sp.to = evt.To;
        sp.arrivalDate = evt.ArrivalDate;
        sp.cost = evt.Cost;
        sp.quantity = evt.Qty;
        sp.reefers = evt.Reefers;
		return sp;
	}
}

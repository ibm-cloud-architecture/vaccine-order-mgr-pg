package ibm.gse.eda.vaccines.domain.events;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class CloudEvent {

    public String type;
    public String specversion;
    public String source;
    public String id;
    public String time;
    public String dataschema;
    public String datacontenttype;
    public ShipmentPlansEvent data;

    public CloudEvent(){}
}

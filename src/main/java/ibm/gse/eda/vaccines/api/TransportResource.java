package ibm.gse.eda.vaccines.api;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

import org.eclipse.microprofile.config.inject.ConfigProperty;

@Path("/api/v1/transportURL")
@ApplicationScoped
public class TransportResource {
    
    
    @ConfigProperty(name="app.transportationURL")
    public String transportationURL;

    @GET
    public String getTransportationURL(){
        return this.transportationURL;
    }

}

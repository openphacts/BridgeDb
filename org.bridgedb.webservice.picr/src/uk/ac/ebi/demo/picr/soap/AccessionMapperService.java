
package uk.ac.ebi.demo.picr.soap;

import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceFeature;


/**
 * This class was generated by the JAXWS SI.
 * JAX-WS RI 2.1-02/02/2007 09:55 AM(vivekp)-FCS
 * Generated source version: 2.1
 * 
 */
@WebServiceClient(name = "AccessionMapperService", targetNamespace = "http://www.ebi.ac.uk/picr/AccessionMappingService", wsdlLocation = "https://www.ebi.ac.uk/Tools/picr/service?wsdl")
public class AccessionMapperService
    extends Service
{

    private final static URL ACCESSIONMAPPERSERVICE_WSDL_LOCATION;

    static {
        URL url = null;
        try {
            url = new URL("https://www.ebi.ac.uk/Tools/picr/service?wsdl");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        ACCESSIONMAPPERSERVICE_WSDL_LOCATION = url;
    }

    public AccessionMapperService(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public AccessionMapperService() {
        super(ACCESSIONMAPPERSERVICE_WSDL_LOCATION, new QName("http://www.ebi.ac.uk/picr/AccessionMappingService", "AccessionMapperService"));
    }

    /**
     * 
     * @return
     *     returns AccessionMapperInterface
     */
    @WebEndpoint(name = "AccessionMapperPort")
    public AccessionMapperInterface getAccessionMapperPort() {
        return (AccessionMapperInterface)super.getPort(new QName("http://www.ebi.ac.uk/picr/AccessionMappingService", "AccessionMapperPort"), AccessionMapperInterface.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns AccessionMapperInterface
     */
    @WebEndpoint(name = "AccessionMapperPort")
    public AccessionMapperInterface getAccessionMapperPort(WebServiceFeature... features) {
        return (AccessionMapperInterface)super.getPort(new QName("http://www.ebi.ac.uk/picr/AccessionMappingService", "AccessionMapperPort"), AccessionMapperInterface.class, features);
    }

}

package fr.cnes.sitools.contact;

import org.restlet.data.MediaType;
import org.restlet.ext.jackson.JacksonRepresentation;
import org.restlet.ext.xstream.XstreamRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;

import com.thoughtworks.xstream.XStream;

import fr.cnes.sitools.common.SitoolsResource;
import fr.cnes.sitools.common.XStreamFactory;
import fr.cnes.sitools.common.model.Response;

public class AbstractContactResource extends SitoolsResource {

  @Override
  public void sitoolsDescribe() {
    // TODO Auto-generated method stub
    
  }

  @Override
  public Representation getRepresentation(Response response, Variant variant) {
    // TODO Auto-generated method stub
    return super.getRepresentation(response, variant);
  }
  
  

  public final Contact getObject(Representation representation, Variant variant) {
    Contact contact = null;
    if (MediaType.APPLICATION_XML.isCompatible(representation.getMediaType())) {
      // Parse the XML representation to get the Collection bean
      XstreamRepresentation<Contact> repXML = new XstreamRepresentation<Contact>(representation);
      XStream xstream = XStreamFactory.getInstance().getXStreamReader(MediaType.APPLICATION_XML);
      xstream.autodetectAnnotations(false);
      repXML.setXstream(xstream);
      contact = repXML.getObject();
    }
    else if (MediaType.APPLICATION_JSON.isCompatible(representation.getMediaType())) {
      // Parse the JSON representation to get the bean
      contact = new JacksonRepresentation<Contact>(representation, Contact.class).getObject();
    }
    return contact;
  }
}

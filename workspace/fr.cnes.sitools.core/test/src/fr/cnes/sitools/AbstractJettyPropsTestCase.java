package fr.cnes.sitools;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.logging.Logger;

import org.junit.Test;
import org.restlet.data.MediaType;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;

import com.thoughtworks.xstream.XStream;

import fr.cnes.sitools.common.SitoolsSettings;
import fr.cnes.sitools.common.SitoolsXStreamRepresentation;
import fr.cnes.sitools.common.XStreamFactory;
import fr.cnes.sitools.common.model.Resource;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.server.Consts;
import fr.cnes.sitools.util.Property;
import fr.cnes.sitools.util.RIAPUtils;

public abstract class AbstractJettyPropsTestCase extends AbstractSitoolsServerTestCase {

  
  protected String getBaseUrl() {
    return ( super.getBaseUrl() +  SitoolsSettings.getInstance().getString(Consts.APP_ADMINISTRATOR_URL) + "/jettyprops" );
  }
  
  @Test
  public void getApplicationsTest() {
    
    docAPI.setActive(false);

    ClientResource cr = new ClientResource(getBaseUrl());
    Representation result = cr.get(getMediaTest());
    assertNotNull(result);
    assertTrue(cr.getStatus().isSuccess());

    // FIXME in XML
    Response response = getResponse(getMediaTest(), result, Property.class, true);
    assertTrue(response.getSuccess());
    assertTrue(response.getTotal().intValue() >= 14);

    RIAPUtils.exhaust(result);
  }
  
  
  /**
   * Decodes Representation into standard Sitools Response object.
   * 
   * @param media
   *          MediaType (XML, JSON)
   * @param representation
   *          Representation
   * @param dataClass
   *          Class<?>
   * @param isArray
   *          boolean
   * @return Response
   */
  public static Response getResponse(MediaType media, Representation representation, Class<?> dataClass, boolean isArray) {
    try {

      if (!media.isCompatible(MediaType.APPLICATION_JSON) && !media.isCompatible(MediaType.APPLICATION_XML)) {
        Logger.getLogger(AbstractSitoolsTestCase.class.getName()).warning("Only JSON or XML supported in tests");
        return null;
      }

      XStream xstream = XStreamFactory.getInstance().getXStreamReader(media);
      configure(xstream);

      if (isArray) {
        if (media.isCompatible(MediaType.APPLICATION_JSON)) {
          xstream.addImplicitCollection(Response.class, "data", dataClass);
          xstream.alias("item", dataClass);
          xstream.alias("item", Object.class, dataClass);
        }
        else {
          // xstream.addImplicitCollection(Response.class, "data", "item", dataClass);
          xstream.alias("item", Object.class, dataClass);
        }
      }
      else {
        xstream.alias("item", dataClass);
        xstream.alias("item", Object.class, dataClass);

        if (dataClass == Property.class) {
          xstream.aliasField("application", Response.class, "item");
        }
      }

      xstream.aliasField("data", Response.class, "data");

      SitoolsXStreamRepresentation<Response> rep = new SitoolsXStreamRepresentation<Response>(representation);
      rep.setXstream(xstream);

      if (media.isCompatible(getMediaTest())) {
        Response response = rep.getObject("response");
        return response;
      }
      else {
        Logger.getLogger(AbstractSitoolsTestCase.class.getName()).warning("Only JSON or XML supported in tests");
        return null; // TODO complete test for XML, Object
      }

    }
    finally {
      RIAPUtils.exhaust(representation);
    }
  }
  

  /**
   * Configures XStream mapping for Response object with Project content.
   * 
   * @param xstream
   *          XStream
   */
  private static void configure(XStream xstream) {
    xstream.autodetectAnnotations(false);
    xstream.alias("response", Response.class);
    xstream.alias("resource", Resource.class);
    xstream.alias("application", Resource.class);
  }  
  
}

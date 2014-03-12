 /*******************************************************************************
 * Copyright 2010-2013 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
 *
 * This file is part of SITools2.
 *
 * SITools2 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SITools2 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with SITools2.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package fr.cnes.sitools;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.restlet.data.MediaType;
import org.restlet.engine.Engine;
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
        Engine.getLogger(AbstractSitoolsTestCase.class.getName()).warning("Only JSON or XML supported in tests");
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
        Engine.getLogger(AbstractSitoolsTestCase.class.getName()).warning("Only JSON or XML supported in tests");
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

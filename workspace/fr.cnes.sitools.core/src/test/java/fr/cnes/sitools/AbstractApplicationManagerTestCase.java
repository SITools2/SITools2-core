/*******************************************************************************
 * Copyright 2010-2014 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.restlet.data.MediaType;
import org.restlet.engine.Engine;
import org.restlet.ext.jackson.JacksonRepresentation;
import org.restlet.ext.xstream.XstreamRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;

import com.thoughtworks.xstream.XStream;

import fr.cnes.sitools.common.SitoolsXStreamRepresentation;
import fr.cnes.sitools.common.XStreamFactory;
import fr.cnes.sitools.common.model.Resource;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.project.model.Project;
import fr.cnes.sitools.server.Consts;
import fr.cnes.sitools.util.RIAPUtils;

/**
 * Test case of AppRegistry
 * 
 * 1- Démarrer le serveur Sitools au complet
 * 
 * 2- Lister les applications
 * 
 * @author jp.boignard (AKKA Technologies)
 */
@Ignore
public abstract class AbstractApplicationManagerTestCase extends AbstractSitoolsServerTestCase {

  /** Test title */
  protected static String title = "Applications API with JSON format";

  /**
   * absolute url for role management REST API
   * 
   * @return url
   */
  protected String getBaseUrl() {
    return super.getBaseUrl() + settings.getString(Consts.APP_APPLICATIONS_URL);
  }

  /**
   * Unit Test
   */
  @Test
  public void getApplicationsTest() {
    docAPI.setActive(false);
    List<Resource> apps = getApplications();
    getApplicationById(apps, 10);
    stopAndStartApplication(apps);
    stopAndStartApplicationWithApi(apps);
    deleteApplications(apps);
    createWadl(getBaseUrl(), "applications_registry");
  }

  /**
   * API documentation
   */
  @Test
  public void getApplications2docAPITest() {
    docAPI.setActive(true);
    docAPI.appendChapter(title);
    getApplications();
    docAPI.close();
  }

  /**
   * Invokes GET and asserts result response is a not empty array.
   * 
   * @return the list of applications
   */
  public List<Resource> getApplications() {
    List<Resource> apps = new ArrayList<Resource>();
    if (docAPI.isActive()) {
      retrieveDocAPI(getBaseUrl(), "", new LinkedHashMap<String, String>(), getBaseUrl());
    }
    else {
      ClientResource cr = new ClientResource(getBaseUrl());
      Representation result = cr.get(getMediaTest());
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());

      // FIXME in XML
      Response response = getResponse(getMediaTest(), result, Resource.class, true);
      assertTrue(response.getSuccess());
      assertTrue(response.getTotal().intValue() > 0);

      for (Object data : response.getData()) {
        apps.add((Resource) data);
      }

      RIAPUtils.exhaust(result);
    }
    return apps;
  }

  /**
   * Get a single application by ID
   * 
   * @param apps
   *          the list of applications
   * @param index
   *          the application index
   */
  public void getApplicationById(List<Resource> apps, int index) {
    Resource app = apps.get(index);
    ClientResource cr = new ClientResource(getBaseUrl() + "/" + app.getId());
    Representation result = cr.get(getMediaTest());
    assertNotNull(result);
    assertTrue(cr.getStatus().isSuccess());

    // FIXME in XML
    Response response = getResponse(getMediaTest(), result, Resource.class, false);
    assertTrue(response.getSuccess());
    Resource out = (Resource) response.getItem();
    assertTrue(out.getAuthor().equals(app.getAuthor()));
    assertTrue(out.getDescription().equals(app.getDescription()));
    RIAPUtils.exhaust(result);
  }

  /**
   * Modifies the description of the first application
   * 
   * @param apps
   *          the list of applications
   */
  private void stopAndStartApplication(List<Resource> apps) {
    String appId = apps.get(10).getId();

    // STOP
    ClientResource cr = new ClientResource(getBaseUrl() + "/" + appId + "?action=stop");
    Representation result = cr.put(getMediaTest());
    assertNotNull(result);
    assertTrue(cr.getStatus().isSuccess());
    Response response = getResponse(getMediaTest(), result, Resource.class, false);
    assertNotNull(response);
    assertTrue(response.getSuccess());
    assertTrue(response.getMessage().equals("APPLICATION_STOPPED"));
    RIAPUtils.exhaust(result);

    // START
    cr = new ClientResource(getBaseUrl() + "/" + appId + "?action=start");
    result = cr.put(getMediaTest());
    assertNotNull(result);
    assertTrue(cr.getStatus().isSuccess());
    response = getResponse(getMediaTest(), result, Resource.class, false);
    assertNotNull(response);
    assertTrue(response.getSuccess());
    assertTrue(response.getMessage().equals("APPLICATION_STARTED"));
    RIAPUtils.exhaust(result);

    // START AGAIN EXPECT AN ERROR
    cr = new ClientResource(getBaseUrl() + "/" + appId + "?action=start");
    result = cr.put(getMediaTest());
    assertNotNull(result);
    assertTrue(cr.getStatus().isSuccess());
    response = getResponse(getMediaTest(), result, Resource.class, false);
    assertNotNull(response);
    assertFalse(response.getSuccess());
    assertTrue(response.getMessage().equals("APPLICATION_ALREADY_STARTED"));
    RIAPUtils.exhaust(result);

    // Resource unknown
    cr = new ClientResource(getBaseUrl() + "/wqazsx");
    result = cr.put(getMediaTest());
    assertNotNull(result);
    assertTrue(cr.getStatus().isSuccess());
    response = getResponse(getMediaTest(), result, Resource.class, false);
    assertNotNull(response);
    assertTrue(!response.getSuccess());
    assertTrue(response.getMessage().equals("APPLICATION_INSTANCE_NOT_FOUND"));
    RIAPUtils.exhaust(result);

    // Restart
    cr = new ClientResource(getBaseUrl() + "/" + appId + "?action=restart");
    result = cr.put(getMediaTest());
    assertNotNull(result);
    assertTrue(cr.getStatus().isSuccess());
    response = getResponse(getMediaTest(), result, Resource.class, false);
    assertNotNull(response);
    assertTrue(response.getSuccess());
    assertTrue(response.getMessage().equals("APPLICATION_RESTARTED"));
    RIAPUtils.exhaust(result);

  }

  /**
   * Modifies the description of the first application
   * 
   * @param apps
   *          the list of applications
   */
  private void stopAndStartApplicationWithApi(List<Resource> apps) {
    String appId = apps.get(10).getId();

    // STOP
    ClientResource cr = new ClientResource(getBaseUrl() + "/" + appId + "/stop");
    Representation result = cr.put(getMediaTest());
    assertNotNull(result);
    assertTrue(cr.getStatus().isSuccess());
    Response response = getResponse(getMediaTest(), result, Resource.class, false);
    assertNotNull(response);
    assertTrue(response.getSuccess());
    assertTrue(response.getMessage().equals("APPLICATION_STOPPED"));
    RIAPUtils.exhaust(result);

    // START
    cr = new ClientResource(getBaseUrl() + "/" + appId + "/start");
    result = cr.put(getMediaTest());
    assertNotNull(result);
    assertTrue(cr.getStatus().isSuccess());
    response = getResponse(getMediaTest(), result, Resource.class, false);
    assertNotNull(response);
    assertTrue(response.getSuccess());
    assertTrue(response.getMessage().equals("APPLICATION_STARTED"));
    RIAPUtils.exhaust(result);

    // START AGAIN, EXPECT
    cr = new ClientResource(getBaseUrl() + "/" + appId + "/start");
    result = cr.put(getMediaTest());
    assertNotNull(result);
    assertTrue(cr.getStatus().isSuccess());
    response = getResponse(getMediaTest(), result, Resource.class, false);
    assertNotNull(response);
    assertFalse(response.getSuccess());
    assertTrue(response.getMessage().equals("APPLICATION_ALREADY_STARTED"));
    RIAPUtils.exhaust(result);

    // Restart
    cr = new ClientResource(getBaseUrl() + "/" + appId + "/restart");
    result = cr.put(getMediaTest());
    assertNotNull(result);
    assertTrue(cr.getStatus().isSuccess());
    response = getResponse(getMediaTest(), result, Resource.class, false);
    assertNotNull(response);
    assertTrue(response.getSuccess());
    assertTrue(response.getMessage().equals("APPLICATION_RESTARTED"));
    RIAPUtils.exhaust(result);

  }

  /**
   * Invoke Delete to unregister application with base URL then Application ID
   * 
   * @param apps
   *          the list of applications
   */
  public void deleteApplications(List<Resource> apps) {
    String appId = apps.get(10).getId();

    // DELETE with unknown ID
    ClientResource cr = new ClientResource(getBaseUrl() + "/saasdmmlpoookdjjj");
    Representation result = cr.delete(getMediaTest());
    assertNotNull(result);
    assertTrue(cr.getStatus().isSuccess());
    Response response = getResponse(getMediaTest(), result, Resource.class, false);
    assertNotNull(response);
    assertTrue(!response.getSuccess());
    assertTrue(response.getMessage().equals("RESOURCE_UNKNOWN"));
    RIAPUtils.exhaust(result);

    // DELETE
    cr = new ClientResource(getBaseUrl() + "/" + appId);
    result = cr.delete(getMediaTest());
    assertNotNull(result);
    assertTrue(cr.getStatus().isSuccess());
    response = getResponse(getMediaTest(), result, Resource.class, false);
    assertNotNull(response);
    assertTrue(response.getSuccess());
    assertTrue(response.getMessage().equals("RESOURCE_DELETED"));
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

        if (dataClass == Resource.class) {
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
   * Get a Project representation for the specified MediaType
   * 
   * @param item
   *          Project
   * @param media
   *          MediaType
   * @return Representation
   */
  public static Representation getRepresentation(Project item, MediaType media) {
    if (media.equals(MediaType.APPLICATION_JSON)) {
      return new JacksonRepresentation<Project>(item);
    }
    else if (media.equals(MediaType.APPLICATION_XML)) {
      XStream xstream = XStreamFactory.getInstance().getXStream(media, false);
      XstreamRepresentation<Project> rep = new XstreamRepresentation<Project>(media, item);
      configure(xstream);
      rep.setXstream(xstream);
      return rep;
    }
    else {
      Engine.getLogger(AbstractSitoolsTestCase.class.getName()).warning("Only JSON or XML supported in tests");
      return null; // TODO complete test with ObjectRepresentation
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

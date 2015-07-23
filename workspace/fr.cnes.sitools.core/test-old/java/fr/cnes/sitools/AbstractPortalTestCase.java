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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;
import org.restlet.data.MediaType;
import org.restlet.engine.Engine;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;

import com.thoughtworks.xstream.XStream;

import fr.cnes.sitools.common.SitoolsXStreamRepresentation;
import fr.cnes.sitools.common.XStreamFactory;
import fr.cnes.sitools.common.model.Resource;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.feeds.model.FeedAuthorModel;
import fr.cnes.sitools.feeds.model.FeedEntryModel;
import fr.cnes.sitools.feeds.model.FeedModel;
import fr.cnes.sitools.portal.model.Portal;
import fr.cnes.sitools.project.model.Project;
import fr.cnes.sitools.common.Consts;
import fr.cnes.sitools.util.RIAPUtils;

@Ignore
public class AbstractPortalTestCase extends SitoolsServerTestCase {
  /** The name of a project on the server */
  private String projectName = "premier";

  /** The name of a feed on the server */
  private String feedName = "a_feed";

  @Override
  protected String getBaseUrl() {
    return super.getBaseUrl() + settings.getString(Consts.APP_PORTAL_URL);
  }

  /**
   * Test
   */
  @Test
  public void test() {
    docAPI.setActive(false);
    try {
      // test avec projet contenant des objets
      getPortal();
      getProjects();
      getProject(projectName);
      getFeedsList();
      getFeed(feedName);

      // test avec projet vide
      createWadl(getBaseUrl(), "portal");
    }
    catch (IOException e) {

      e.printStackTrace();
    }
  }

  /**
   * Test
   */
  @Test
  public void testAPI() {
    docAPI.setActive(true);
    docAPI.appendChapter("Portal application API");
    try {
      docAPI.appendSubChapter("Get the portal object", "portal");
      // test avec projet contenant des objets
      getPortal();
      docAPI.appendSubChapter("Get the list of projects on the portal", "portal_projects");
      getProjects();
      docAPI.appendSubChapter("Get the detail of a given project", "portal_project_details");
      getProject(projectName);
      docAPI.appendSubChapter("Get the list of feeds on the portal", "portal_feeds");
      getFeedsList();
      docAPI.appendSubChapter("Get a feed", "portal_feed");
      getFeed(feedName);

      // test avec projet vide
      createWadl(getBaseUrl(), "portal");
    }
    catch (IOException e) {

      e.printStackTrace();
    }
  }

  /**
   * Get the portal description
   * 
   * 
   * @throws IOException
   *           Exception when copying configuration files from TEST to data/TESTS
   */
  private void getPortal() throws IOException {
    String url = getBaseUrl();
    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      String template = getBaseUrl();
      retrieveDocAPI(url, "", parameters, template);
    }
    else {
      ClientResource cr = new ClientResource(url);
      Representation result = cr.get(getMediaTest());
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());
      Response response = getResponse(getMediaTest(), result, Portal.class, true);
      assertTrue(response.getSuccess());
      assertEquals(new Integer(1), response.getTotal());
      RIAPUtils.exhaust(result);
    }

  }

  /**
   * Get the list of projects attached to a portal
   * 
   * @throws IOException
   *           Exception when copying configuration files from TEST to data/TESTS
   */
  private void getProjects() throws IOException {
    String url = getBaseUrl() + "/projects";
    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      String template = getBaseUrl() + "/projects";
      retrieveDocAPI(url, "", parameters, template);
    }
    else {
      ClientResource cr = new ClientResource(url);
      Representation result = cr.get(getMediaTest());
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());
      Response response = getResponse(getMediaTest(), result, Project.class, true);
      assertTrue(response.getSuccess());
      assertEquals(new Integer(2), response.getTotal());
      RIAPUtils.exhaust(result);
    }

  }

  /**
   * Get a project from the list by it's name
   * 
   * @param projectName
   *          the name of the project
   * @throws IOException
   *           Exception when copying configuration files from TEST to data/TESTS
   */
  private void getProject(String projectName) throws IOException {
    String url = getBaseUrl() + "/projects/" + projectName;
    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      parameters.put("projectId", "Project name");
      String template = getBaseUrl() + "/projects/{projectId}";
      retrieveDocAPI(url, "", parameters, template);
    }
    else {
      ClientResource cr = new ClientResource(url);
      Representation result = cr.get(getMediaTest());
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());
      Response response = getResponse(getMediaTest(), result, Project.class, false);
      assertTrue(response.getSuccess());
      assertNotNull(response.getItem());
      Project project = (Project) response.getItem();
      assertEquals(projectName, project.getName());
      RIAPUtils.exhaust(result);
    }

  }

  /**
   * Get the list of feeds attached to a portal
   * 
   * @throws IOException
   *           Exception when copying configuration files from TEST to data/TESTS
   */
  private void getFeedsList() throws IOException {
    String url = getBaseUrl() + "/idPortal/feeds";
    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      String template = getBaseUrl() + "/idPortal/feeds";
      retrieveDocAPI(url, "", parameters, template);
    }
    else {
      ClientResource cr = new ClientResource(url);
      Representation result = cr.get(getMediaTest());
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());
      Response response = getResponse(getMediaTest(), result, FeedModel.class, true);
      assertTrue(response.getSuccess());
      assertEquals(new Integer(2), response.getTotal());
      RIAPUtils.exhaust(result);
    }

  }

  /**
   * Get the list of feeds attached to a portal
   * 
   * @param feedId
   *          the feed name
   * 
   * @throws IOException
   *           Exception when copying configuration files from TEST to data/TESTS
   */
  private void getFeed(String feedId) throws IOException {
    String url = getBaseUrl() + "/idPortal/clientFeeds/" + feedId;
    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      parameters.put("feedId", "The feed identifier (the feed name)");
      String template = getBaseUrl() + "/idPortal/clientFeeds/{feedId}";
      retrieveDocAPI(url, "", parameters, template);
    }
    else {
      ClientResource cr = new ClientResource(url);
      Representation result = cr.get(MediaType.APPLICATION_ALL_XML);
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());
      RIAPUtils.exhaust(result);
    }

  }

  // ------------------------------------------------------------
  // RESPONSE REPRESENTATION WRAPPING

  /**
   * REST API Response wrapper for single item expected.
   * 
   * @param media
   *          MediaType expected
   * @param representation
   *          service response representation
   * @param dataClass
   *          class expected in the item property of the Response object
   * @return Response the response.
   */
  public static Response getResponse(MediaType media, Representation representation, Class<?> dataClass) {
    return getResponse(media, representation, dataClass, false);
  }

  /**
   * REST API Response Representation wrapper for single or multiple items expexted
   * 
   * @param media
   *          MediaType expected
   * @param representation
   *          service response representation
   * @param dataClass
   *          class expected for items of the Response object
   * @param isArray
   *          if true wrap the data property else wrap the item property
   * @return Response
   */
  public static Response getResponse(MediaType media, Representation representation, Class<?> dataClass, boolean isArray) {
    try {
      if (!media.isCompatible(getMediaTest()) && !media.isCompatible(MediaType.APPLICATION_XML)) {
        Engine.getLogger(AbstractSitoolsTestCase.class.getName()).warning("Only JSON or XML supported in tests");
        return null;
      }

      XStream xstream = XStreamFactory.getInstance().getXStreamReader(media);
      xstream.autodetectAnnotations(false);

      xstream.alias("response", Response.class);
      // for feeds
      xstream.alias("FeedModel", FeedModel.class);
      xstream.alias("FeedEntryModel", FeedEntryModel.class);
      xstream.alias("FeedAuthorModel", FeedAuthorModel.class);
      // for portal
      xstream.alias("portal", Portal.class);
      // for projects
      xstream.alias("project", Project.class);
      xstream.alias("dataset", Resource.class);

      if (isArray) {
        if (media.isCompatible(MediaType.APPLICATION_JSON)) {
          xstream.addImplicitCollection(Response.class, "data", dataClass);

          // for feeds
          xstream.addImplicitCollection(FeedModel.class, "entries", FeedEntryModel.class);

        }
        xstream.alias("item", Object.class, dataClass);
      }
      else {
        xstream.alias("item", Object.class, dataClass);

        if (media.equals(MediaType.APPLICATION_JSON)) {
          xstream.addImplicitCollection(Project.class, "dataSets", Resource.class);
          xstream.aliasField("dataSets", Project.class, "dataSets");
        }

        if (dataClass == Project.class) {
          xstream.aliasField("project", Response.class, "item");
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
        return null;
      }
    }
    finally {
      RIAPUtils.exhaust(representation);
    }
  }
}

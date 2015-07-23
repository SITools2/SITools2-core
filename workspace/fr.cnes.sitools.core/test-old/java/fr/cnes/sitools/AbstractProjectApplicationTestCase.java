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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;
import org.restlet.data.MediaType;
import org.restlet.engine.Engine;
import org.restlet.ext.jackson.JacksonRepresentation;
import org.restlet.ext.xstream.XstreamRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.ClientResource;

import com.thoughtworks.xstream.XStream;

import fr.cnes.sitools.common.SitoolsXStreamRepresentation;
import fr.cnes.sitools.common.XStreamFactory;
import fr.cnes.sitools.common.model.Resource;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.project.model.Project;
import fr.cnes.sitools.common.Consts;
import fr.cnes.sitools.util.RIAPUtils;

/**
 * Test for projects
 * 
 * 
 * @author AKKA Technologies
 */
@Ignore
public abstract class AbstractProjectApplicationTestCase extends AbstractSitoolsServerTestCase {

  /** projectId */
  private String projectId = "10000";

  /** project attachment for user */
  private String projectAttach = "/myProject";

  /**
   * absolute url for project management REST API
   * 
   * @return url
   */
  protected String getBaseUrl() {
    return super.getBaseUrl() + "/projects";
  }

  /**
   * absolute url for application management REST API
   * 
   * @return url
   */
  protected String getBaseAppUrl() {
    return super.getBaseUrl() + settings.getString(Consts.APP_APPLICATIONS_URL);
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.cnes.sitools.AbstractSitoolsServerTestCase#setUp()
   */
  @Override
  public void setUp() throws Exception {
    super.setUp();
  }

  /**
   * Test
   */
  @Test
  public void test() {
    docAPI.setActive(false);
    // create project
    Project proj = createObject(projectId);

    try {
      // add the project
      createProject(proj);
      // assertNoneApplication
      assertApplicationDontExists(proj.getId());
      // activate project
      activateProject(proj);
      proj.setStatus("ACTIVE");
      proj.setLastStatusUpdate(new Date());
      // check application is active
      assertApplicationActive(proj.getId());
      // activate project but get an error
      activateProjectFail(proj);
      // assertNoneApplication
      assertApplicationExists(proj.getId());
      // set project to maintenance
      startProjectMaintenance(proj.getId());
      // set project to maintenance
      startProjectMaintenanceFail(proj.getId());
      // set project not to maintenance anymore
      stopProjectMaintenance(proj.getId());
      // set project to maintenance
      stopProjectMaintenanceFail(proj.getId());
      // disactivate project
      stopProject(proj);
      // disactivate project but get an error
      stopProjectFail(proj);
      // assert application INACTIVE
      assertApplicationInactive(proj.getId());
      // delete project
      deleteProject(proj);
      // assertNoneApplication
      assertApplicationDontExists(proj.getId());
    }
    catch (IOException e) {

      e.printStackTrace();
    }

  }

  /**
   * Test
   */
  @Test
  public void testDeleteActiveProject() {
    docAPI.setActive(false);
    // create project
    Project proj = createObject(projectId);

    try {
      // add the project
      createProject(proj);
      // assertNoneApplication
      assertApplicationDontExists(proj.getId());
      // activate project
      activateProject(proj);
      // check application is active
      assertApplicationActive(proj.getId());
      // delete project
      deleteProject(proj);
      // assertNoneApplication
      assertApplicationDontExists(proj.getId());
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
    docAPI.appendChapter("Manipulating and Administrating Projects");

    docAPI.appendSubChapter("Create Project", "create");
    // create project
    Project proj = createObject(projectId);

    try {
      // add the project
      createProject(proj);
      docAPI.appendSubChapter("Check if the application is inactive", "appInactive");
      // assertNoneApplication
      assertApplicationDontExists(proj.getId());
      docAPI.appendSubChapter("Activate a project", "active");
      // activate project
      activateProject(proj);
      docAPI.appendSubChapter("Activate a project when already active", "activeFail");
      docAPI.appendComment("Return an error");
      // activate project
      activateProjectFail(proj);
      docAPI.appendSubChapter("Set a project under maintenance", "startmaintenance");
      // activate project
      startProjectMaintenance(proj.getId());
      docAPI.appendSubChapter("Set a project not under maintenance anymore", "stopMaintenance");
      // activate project
      stopProjectMaintenance(proj.getId());
      docAPI.appendSubChapter("Check if the application is active", "appActive");
      // assertApplicationExists
      assertApplicationExists(proj.getId());
      docAPI.appendSubChapter("Disactivate a project", "disactive");
      // disactivate project
      stopProject(proj);
      docAPI.appendSubChapter("Disctivate a project when already disactive", "disactiveFail");
      docAPI.appendComment("Return an error");
      // disactivate project but get an error
      stopProjectFail(proj);
      docAPI.appendSubChapter("Delete a project", "disactive");
      // delete project
      deleteProject(proj);
    }
    catch (IOException e) {

      e.printStackTrace();
    }

  }

  /**
   * Create a project object
   * 
   * @param projectId
   *          the projectId
   * @return a Project Object
   */
  private Project createObject(String projectId) {

    Project project = new Project();
    project.setName("projTest");
    project.setId(projectId);
    project.setDescription("test_Description");
    project.setSitoolsAttachementForUsers(this.projectAttach);
    return project;
  }

  /**
   * Create a Project on the server
   * 
   * @param proj
   *          The project to create
   * @throws IOException
   *           Exception when copying configuration files from TEST to data/TESTS
   */
  private void createProject(Project proj) throws IOException {
    Representation projRep = getRepresentation(proj, getMediaTest());
    String url = getBaseUrl();
    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      parameters.put("POST", "A <i>Project</i> object");
      postDocAPI(url, "", projRep, parameters, url);
    }
    else {
      ClientResource cr = new ClientResource(url);
      Representation result = cr.post(projRep, getMediaTest());
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());
      Response response = getResponse(getMediaTest(), result, Project.class);
      assertTrue(response.getSuccess());
      RIAPUtils.exhaust(result);
    }

  }

  /**
   * Activate a project
   * 
   * @param proj
   *          the project to activate
   * @throws IOException
   *           Exception when copying configuration files from TEST to data/TESTS
   */
  private void activateProject(Project proj) throws IOException {

    StringRepresentation projRep = new StringRepresentation("");
    String url = getBaseUrl() + "/" + proj.getId() + "/start";
    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      parameters.put("identifier", "project identifier");
      String template = getBaseUrl() + "/%identifier%/start";
      putDocAPI(url, "", projRep, parameters, template);
    }
    else {
      ClientResource cr = new ClientResource(url);
      Representation result = cr.put(projRep, getMediaTest());
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());
      Response response = getResponse(getMediaTest(), result, Project.class);
      assertTrue(response.getSuccess());
      
      Project projOut = (Project) response.getItem();
      assertEquals("ACTIVE", projOut.getStatus());
      assertNotNull(projOut.getLastStatusUpdate());
      RIAPUtils.exhaust(result);
    }
  }

  /**
   * Activate a project, assert error
   * 
   * @param proj
   *          the project to activate
   * @throws IOException
   *           Exception when copying configuration files from TEST to data/TESTS
   */
  private void activateProjectFail(Project proj) throws IOException {

    StringRepresentation projRep = new StringRepresentation("");
    String url = getBaseUrl() + "/" + proj.getId() + "/start";
    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      parameters.put("identifier", "project identifier");
      String template = getBaseUrl() + "/%identifier%/start";
      putDocAPI(url, "", projRep, parameters, template);
    }
    else {
      ClientResource cr = new ClientResource(url);
      Representation result = cr.put(projRep, getMediaTest());
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());
      Response response = getResponse(getMediaTest(), result, Project.class);
      assertTrue(response.getSuccess());
      assertEquals("project.update.blocked", response.getMessage());
      RIAPUtils.exhaust(result);
    }

  }

  /**
   * Stop a project
   * 
   * @param proj
   *          the project to stop
   * @throws IOException
   *           Exception when copying configuration files from TEST to data/TESTS
   */
  private void stopProject(Project proj) throws IOException {

    StringRepresentation projRep = new StringRepresentation("");
    String url = getBaseUrl() + "/" + proj.getId() + "/stop";
    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      parameters.put("identifier", "project identifier");
      String template = getBaseUrl() + "/%identifier%/stop";
      putDocAPI(url, "", projRep, parameters, template);
    }
    else {
      ClientResource cr = new ClientResource(url);
      Representation result = cr.put(projRep, getMediaTest());
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());
      Response response = getResponse(getMediaTest(), result, Project.class);
      assertTrue(response.getSuccess());
      Project projOut = (Project) response.getItem();
      assertEquals("INACTIVE", projOut.getStatus());
      assertTrue(proj.getLastStatusUpdate().before(projOut.getLastStatusUpdate()));
      RIAPUtils.exhaust(result);
    }
  }

  /**
   * Stop a project, assert error
   * 
   * @param proj
   *          the project to activate
   * @throws IOException
   *           Exception when copying configuration files from TEST to data/TESTS
   */
  private void stopProjectFail(Project proj) throws IOException {

    StringRepresentation projRep = new StringRepresentation("");
    String url = getBaseUrl() + "/" + proj.getId() + "/stop";
    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      parameters.put("identifier", "project identifier");
      String template = getBaseUrl() + "/%identifier%/stop";
      putDocAPI(url, "", projRep, parameters, template);
    }
    else {
      ClientResource cr = new ClientResource(url);
      Representation result = cr.put(projRep, getMediaTest());
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());
      Response response = getResponse(getMediaTest(), result, Project.class);
      assertTrue(response.getSuccess());
      assertEquals("project.stop.blocked", response.getMessage());
      RIAPUtils.exhaust(result);
    }

  }

  /**
   * Assert if there is an application with the given id is defined and INACTIVE
   * 
   * @param id
   *          the application id
   * @throws IOException
   *           Exception when copying configuration files from TEST to data/TESTS
   */
  private void assertApplicationInactive(String id) throws IOException {
    assertApplicationStatus(id, "INACTIVE");
  }

  /**
   * Assert if there is an application with the given id defined on the server
   * 
   * @param id
   *          the application id
   * @throws IOException
   *           Exception when copying configuration files from TEST to data/TESTS
   */
  private void assertApplicationActive(String id) throws IOException {
    assertApplicationStatus(id, "ACTIVE");
  }

  /**
   * Assert if there is an application with the given id defined on the server
   * 
   * @param id
   *          the application id
   * @throws IOException
   *           Exception when copying configuration files from TEST to data/TESTS
   */
  private void assertApplicationStatus(String id, String status) throws IOException {
    String url = getBaseAppUrl() + "/" + id;
    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      parameters.put("identifier", "The application id");
      String template = getBaseAppUrl() + "/%identifier";
      retrieveDocAPI(url, "", parameters, template);
    }
    else {
      ClientResource cr = new ClientResource(url);
      Representation result = cr.get(getMediaTest());
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());
      Response response = getResponse(getMediaTest(), result, Resource.class);
      assertTrue(response.getSuccess());
      Resource app = (Resource) response.getItem();
      assertEquals(status, app.getStatus());
      RIAPUtils.exhaust(result);
    }

  }

  /**
   * Assert if there is an application with the given id defined on the server
   * 
   * @param id
   *          the application id
   * @throws IOException
   *           Exception when copying configuration files from TEST to data/TESTS
   */
  private void assertApplicationDontExists(String id) throws IOException {
    String url = getBaseAppUrl() + "/" + id;
    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      parameters.put("identifier", "The application id");
      String template = getBaseAppUrl() + "/%identifier";
      retrieveDocAPI(url, "", parameters, template);
    }
    else {
      ClientResource cr = new ClientResource(url);
      Representation result = cr.get(getMediaTest());
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());
      Response response = getResponse(getMediaTest(), result, Resource.class);
      assertFalse(response.getSuccess());
      assertEquals("application.notfound", response.getMessage());
      RIAPUtils.exhaust(result);
    }

  }

  /**
   * Assert if there is an application exists with the given id defined on the server
   * 
   * @param id
   *          the application id
   * @throws IOException
   *           Exception when copying configuration files from TEST to data/TESTS
   */
  private void assertApplicationExists(String id) throws IOException {
    String url = getBaseAppUrl() + "/" + id;
    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      parameters.put("identifier", "The application id");
      String template = getBaseAppUrl() + "/%identifier";
      retrieveDocAPI(url, "", parameters, template);
    }
    else {
      ClientResource cr = new ClientResource(url);
      Representation result = cr.get(getMediaTest());
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());
      Response response = getResponse(getMediaTest(), result, Resource.class);
      Resource app = (Resource) response.getItem();
      assertEquals("ACTIVE", app.getStatus());
      RIAPUtils.exhaust(result);
    }

  }

  /**
   * Delete a project
   * 
   * @param proj
   *          the project to delete
   * @throws IOException
   *           Exception when copying configuration files from TEST to data/TESTS
   */
  private void deleteProject(Project proj) throws IOException {
    String url = getBaseUrl() + "/" + proj.getId();
    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      parameters.put("identifier", "project identifier");
      String template = getBaseUrl() + "/%identifier";
      deleteDocAPI(url, "", parameters, template);
    }
    else {
      ClientResource cr = new ClientResource(url);
      Representation result = cr.delete(getMediaTest());
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());
      Response response = getResponse(getMediaTest(), result, Project.class);
      assertTrue(response.getSuccess());
      RIAPUtils.exhaust(result);
    }

  }

  /**
   * Start the maintenance on a project specified by its identifier
   * 
   * @param projectId
   *          the identifier of the Project
   */
  private void startProjectMaintenance(String projectId) {
    StringRepresentation projRep = new StringRepresentation("");
    String url = getBaseUrl() + "/" + projectId + "/startmaintenance";
    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      parameters.put("identifier", "project identifier");
      String template = getBaseUrl() + "/%identifier%/startMaintenance";
      putDocAPI(url, "", projRep, parameters, template);
    }
    else {
      ClientResource cr = new ClientResource(url);
      Representation result = cr.put(projRep, getMediaTest());
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());
      Response response = getResponse(getMediaTest(), result, Project.class);
      assertTrue(response.getSuccess());

      Project projOut = (Project) response.getItem();
      assertTrue(projOut.isMaintenance());
      RIAPUtils.exhaust(result);
    }

  }

  /**
   * Stop the maintenance on a project specified by its identifier
   * 
   * @param projectId
   *          the identifier of the Project
   */
  private void stopProjectMaintenance(String projectId) {
    StringRepresentation projRep = new StringRepresentation("");
    String url = getBaseUrl() + "/" + projectId + "/stopmaintenance";
    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      parameters.put("identifier", "project identifier");
      String template = getBaseUrl() + "/%identifier%/stopMaintenance";
      putDocAPI(url, "", projRep, parameters, template);
    }
    else {
      ClientResource cr = new ClientResource(url);
      Representation result = cr.put(projRep, getMediaTest());
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());
      Response response = getResponse(getMediaTest(), result, Project.class);
      assertTrue(response.getSuccess());

      Project projOut = (Project) response.getItem();
      assertFalse(projOut.isMaintenance());
      RIAPUtils.exhaust(result);
    }

  }

  /**
   * Start the maintenance on a project specified by its identifier but expect an error because it is already in
   * maintenance state
   * 
   * @param projectId
   *          the identifier of the Project
   */
  private void startProjectMaintenanceFail(String projectId) {
    StringRepresentation projRep = new StringRepresentation("");
    String url = getBaseUrl() + "/" + projectId + "/startmaintenance";
    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      parameters.put("identifier", "project identifier");
      String template = getBaseUrl() + "/%identifier%/startMaintenance";
      putDocAPI(url, "", projRep, parameters, template);
    }
    else {
      ClientResource cr = new ClientResource(url);
      Representation result = cr.put(projRep, getMediaTest());
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());
      Response response = getResponse(getMediaTest(), result, Project.class);
      assertTrue(response.getSuccess());
      assertEquals("project.maintenance.on.blocked", response.getMessage());

      RIAPUtils.exhaust(result);
    }

  }

  /**
   * Start the maintenance on a project specified by its identifier but expect an error because it is not in maintenance
   * state
   * 
   * @param projectId
   *          the identifier of the Project
   */
  private void stopProjectMaintenanceFail(String projectId) {
    StringRepresentation projRep = new StringRepresentation("");
    String url = getBaseUrl() + "/" + projectId + "/stopmaintenance";
    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      parameters.put("identifier", "project identifier");
      String template = getBaseUrl() + "/%identifier%/stopMaintenance";
      putDocAPI(url, "", projRep, parameters, template);
    }
    else {
      ClientResource cr = new ClientResource(url);
      Representation result = cr.put(projRep, getMediaTest());
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());
      Response response = getResponse(getMediaTest(), result, Project.class);
      assertTrue(response.getSuccess());
      assertNull(response.getItem());
      assertEquals("project.maintenance.off.blocked", response.getMessage());

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
      xstream.alias("project", Project.class);
      xstream.alias("dataset", Resource.class);
      // xstream.alias("dataset", Resource.class);

      xstream.alias("application", Resource.class);

      if (isArray) {
        xstream.addImplicitCollection(Response.class, "data", dataClass);
      }
      else {
        xstream.alias("item", dataClass);
        xstream.alias("item", Object.class, dataClass);

        if (media.equals(MediaType.APPLICATION_JSON)) {
          xstream.addImplicitCollection(Project.class, "dataSets", Resource.class);
          xstream.aliasField("dataSets", Project.class, "dataSets");
        }

        if (dataClass == Project.class) {
          xstream.aliasField("project", Response.class, "item");
          // if (dataClass == DataSet.class)
          // xstream.aliasField("dataset", Response.class, "item");
        }

        if (dataClass == Resource.class) {
          xstream.aliasField("application", Response.class, "item");
          // if (dataClass == DataSet.class)
          // xstream.aliasField("dataset", Response.class, "item");
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

  /**
   * Builds XML or JSON Representation of Project for Create and Update methods.
   * 
   * @param item
   *          Project
   * @param media
   *          APPLICATION_XML or APPLICATION_JSON
   * @return XML or JSON Representation
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
      return null;
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
    xstream.alias("project", Project.class);
    xstream.alias("dataset", Resource.class);
    // xstream.addImplicitCollection(Project.class, "dataSets", "dataSets",
    // Resource.class);
    // xstream.aliasField("dataSets", Project.class, "dataSets");
    // xstream.alias("image", Resource.class);
    // xstream.addImplicitCollection(Project.class, "dataSets", Resource.class);
    // xstream.aliasField("dataSets", Project.class, "dataSets");
    // xstream.aliasField("image", Project.class, "image");
  }
}

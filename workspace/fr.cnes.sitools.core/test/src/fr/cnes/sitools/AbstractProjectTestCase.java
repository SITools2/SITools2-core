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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.restlet.Component;
import org.restlet.Context;
import org.restlet.data.Method;
import org.restlet.data.Protocol;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;

import fr.cnes.sitools.common.SitoolsSettings;
import fr.cnes.sitools.common.application.ContextAttributes;
import fr.cnes.sitools.common.model.Resource;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.project.ProjectAdministration;
import fr.cnes.sitools.project.ProjectStoreInterface;
import fr.cnes.sitools.project.ProjectStoreXMLMap;
import fr.cnes.sitools.project.graph.GraphStoreInterface;
import fr.cnes.sitools.project.graph.GraphStoreXMLMap;
import fr.cnes.sitools.project.model.MinimalProjectPriorityDTO;
import fr.cnes.sitools.project.model.Project;
import fr.cnes.sitools.project.model.ProjectModule;
import fr.cnes.sitools.project.model.ProjectPriorityDTO;
import fr.cnes.sitools.registry.AppRegistryApplication;
import fr.cnes.sitools.registry.ApplicationStoreInterface;
import fr.cnes.sitools.registry.ApplicationStoreXMLMap;
import fr.cnes.sitools.server.Consts;
import fr.cnes.sitools.util.RIAPUtils;
import fr.cnes.sitools.utils.GetRepresentationUtils;
import fr.cnes.sitools.utils.GetResponseUtils;

/**
 * Test CRUD Project Rest API
 * 
 * @since UserStory : ADM Projects, Sprint : 4
 * 
 * @author jp.boignard (AKKA Technologies)
 */
public abstract class AbstractProjectTestCase extends AbstractSitoolsTestCase {

  /**
   * static xml store instance for the test
   */
  private static ProjectStoreInterface store = null;

  /**
   * static xml store instance for the test
   */
  private static GraphStoreInterface storeGraph = null;

  /**
   * Restlet Component for server
   */
  private Component component = null;

  /**
   * absolute url for project management REST API
   * 
   * @return url
   */
  protected String getBaseUrl() {
    return super.getBaseUrl() + SitoolsSettings.getInstance().getString(Consts.APP_PROJECTS_URL);
  }

  /**
   * relative url for project management REST API
   * 
   * @return url
   */
  protected String getAttachUrl() {
    return SITOOLS_URL + SitoolsSettings.getInstance().getString(Consts.APP_PROJECTS_URL);
  }

  /**
   * Absolute path location for project store files
   * 
   * @return path
   */
  protected String getTestRepository() {
    return super.getTestRepository() + SitoolsSettings.getInstance().getString(Consts.APP_PROJECTS_STORE_DIR);
  }

  /**
   * Absolute path location for project store files
   * 
   * @return path
   */
  protected String getTestGraphRepository() {
    return super.getTestRepository() + SitoolsSettings.getInstance().getString(Consts.APP_GRAPHS_STORE_DIR);
  }

  @Before
  @Override
  /**
   * Init and Start a server with ProjectApplication
   * 
   * @throws java.lang.Exception
   */
  public void setUp() throws Exception {
    File storeDirectory = new File(getTestRepository());
    File storeGraphDirectory = new File(getTestGraphRepository());
    File appRegistry = new File(super.getTestRepository()
        + SitoolsSettings.getInstance().getString(Consts.APP_APPLICATIONS_STORE_DIR));

    if (this.component == null) {
      this.component = new Component();
      this.component.getServers().add(Protocol.HTTP, getTestPort());
      this.component.getClients().add(Protocol.HTTP);
      this.component.getClients().add(Protocol.FILE);
      this.component.getClients().add(Protocol.CLAP);

      // Context
      Context ctx = this.component.getContext().createChildContext();
      ctx.getAttributes().put(ContextAttributes.SETTINGS, SitoolsSettings.getInstance());

      cleanDirectory(storeDirectory);
      cleanMapDirectories(storeDirectory);
      storeGraph = new GraphStoreXMLMap(storeGraphDirectory, ctx);
      store = new ProjectStoreXMLMap(storeDirectory, ctx);

      Map<String, Object> stores = new ConcurrentHashMap<String, Object>();
      stores.put(Consts.APP_STORE_PROJECT, store);
      stores.put(Consts.APP_STORE_GRAPH, storeGraph);

      SitoolsSettings settings = SitoolsSettings.getInstance();
      settings.setStores(stores);

      ctx.getAttributes().put(ContextAttributes.APP_STORE, store);

      // ===========================================================================
      // ApplicationManager for application registering

      // Store
      ApplicationStoreInterface storeApp = new ApplicationStoreXMLMap(appRegistry, ctx);

      // Context
      Context appContext = component.getContext().createChildContext();
      String appReference = getBaseUrl() + settings.getString(Consts.APP_APPLICATIONS_URL);
      appContext.getAttributes().put(ContextAttributes.SETTINGS, settings);
      appContext.getAttributes().put(ContextAttributes.APP_ATTACH_REF, appReference);
      appContext.getAttributes().put(ContextAttributes.APP_STORE, storeApp);

      // Application
      AppRegistryApplication appManager = new AppRegistryApplication(appContext);

      // for applications whose attach / detach themselves other applications to
      // the virtualhost.
      settings.setAppRegistry(appManager);

      this.component.getDefaultHost().attach(getAttachUrl(),
          new ProjectAdministration(this.component.getDefaultHost(), ctx));
    }

    if (!this.component.isStarted()) {
      this.component.start();
    }
  }

  @After
  @Override
  /**
   * Stop server
   * @throws java.lang.Exception
   */
  public void tearDown() throws Exception {
    super.tearDown();
    this.component.stop();
    this.component = null;
  }

  /**
   * Test CRUD Project with JSon format exchanges.
   */
  @Test
  public void testCRUD() {
    docAPI.setActive(false);
    try {
      assertNone();
      Project item = createObject("new_project");
      create(item);
      retrieve(item);
      update(item);
      delete(item);
      assertNone();
      createWadl(getBaseUrl(), "projects_admin");
    }
    catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  /**
   * Test CRUD Project with JSon format exchanges.
   */
  @Test
  public void testCRUD2docAPI() {
    docAPI.setActive(true);
    docAPI.appendChapter("Manipulating Project Collection");

    try {
      assertNone();
      Project item = createObject("1000000");

      docAPI.appendSubChapter("Creating a new Project", "create");
      create(item);

      docAPI.appendChapter("Manipulating an existing Project resource");

      docAPI.appendSubChapter("Retrieving a Project", "retrieving");
      retrieve(item);

      docAPI.appendSubChapter("Updating a Project", "updating");
      update(item);

      docAPI.appendSubChapter("Deleting a Project", "deleting");
      delete(item);
      docAPI.close();
    }
    catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

  }

  /**
   * Test CRUD Project with JSon format exchanges.
   */
  @Test
  public void testPriorityAndCategory() {
    docAPI.setActive(false);
    try {
      assertNone();
      Project item = createObject("new_project");
      create(item);
      ProjectPriorityDTO projectDTO = createProjectPriorityDTO(item);
      updatePriority(projectDTO);
      retrieveProjectPriority(projectDTO, item);

      delete(item);
      assertNone();
      createWadl(getBaseUrl(), "projects_admin");
    }
    catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  private void retrieveProjectPriority(ProjectPriorityDTO projectDTO, Project item) {
    String url = getBaseUrl() + "/" + item.getId();
    ClientResource cr = new ClientResource(url);
    docAPI.appendRequest(Method.GET, cr);

    Representation result = cr.get(getMediaTest());
    if (!docAPI.appendResponse(result)) {
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());
      Response response = GetResponseUtils.getResponseProject(getMediaTest(), result, Project.class);
      assertTrue(response.getSuccess());
      Project prj = (Project) response.getItem();

      assertEquals(item.getName(), prj.getName());
      assertEquals(item.getDescription(), prj.getDescription());

      boolean checkPriority = false;
      for (MinimalProjectPriorityDTO minimalProject : projectDTO.getMinimalProjectPriorityList()) {
        if (minimalProject.getId().equals(item.getId())) {
          checkPriority = true;
          assertEquals(minimalProject.getPriority(), prj.getPriority());
          assertEquals(minimalProject.getCategoryProject(), prj.getCategoryProject());
        }
      }
      assertTrue(checkPriority);

      RIAPUtils.exhaust(result);
    }

  }

  private void updatePriority(ProjectPriorityDTO projectDTO) {
    Representation rep = GetRepresentationUtils.getRepresentationProjectPriorityDTO(projectDTO, getMediaTest());
    ClientResource cr = new ClientResource(getBaseUrl());
    docAPI.appendRequest(Method.PUT, cr, rep);

    Representation result = cr.put(rep, getMediaTest());
    if (!docAPI.appendResponse(result)) {
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());
      Response response = GetResponseUtils.getResponse(getMediaTest(), result, getMediaTest());
      assertTrue(response.getSuccess());

      RIAPUtils.exhaust(result);
    }

  }

  private ProjectPriorityDTO createProjectPriorityDTO(Project p) {
    List<MinimalProjectPriorityDTO> list = new ArrayList<MinimalProjectPriorityDTO>();
    MinimalProjectPriorityDTO minimalProject = new MinimalProjectPriorityDTO();

    minimalProject.setId(p.getId());
    minimalProject.setCategoryProject("MyCategory");
    minimalProject.setPriority(25);

    list.add(minimalProject);
    ProjectPriorityDTO projectDTO = new ProjectPriorityDTO();
    projectDTO.setMinimalProjectPriorityList(list);

    return projectDTO;
  }

  /**
   * Create an object for tests
   * 
   * @param id
   *          project id
   * @return Project
   */
  public Project createObject(String id) {
    Project item = new Project();
    item.setId(id);
    item.setName(id);
    item.setDescription("project description");
    Resource image = new Resource();
    image.setUrl("http://uneimage.png");
    item.setImage(image);
    Resource dataset1 = new Resource();
    dataset1.setId("9991");
    Resource dataset2 = new Resource();
    dataset2.setId("9992");
    ArrayList<Resource> datasets = new ArrayList<Resource>();
    datasets.add(dataset1);
    datasets.add(dataset2);
    item.setDataSets(datasets);

    ArrayList<ProjectModule> modules = new ArrayList<ProjectModule>();
    ProjectModule projectModule1 = new ProjectModule();
    projectModule1.setId("12345");
    projectModule1.setName("a new project");
    projectModule1.setDescription("a new project");
    modules.add(projectModule1);
    item.setModules(modules);
    item.setCategoryProject("MyCategory");
    item.setPriority(1);

    return item;
  }

  /**
   * Invoke POST
   * 
   * @param item
   *          Project
   * @throws IOException
   *           Exception when copying configuration files from TEST to data/TESTS
   */
  public void create(Project item) throws IOException {
    Representation rep = GetRepresentationUtils.getRepresentationProject(item, getMediaTest());
    ClientResource cr = new ClientResource(getBaseUrl());
    docAPI.appendRequest(Method.POST, cr, rep);

    Representation result = cr.post(rep, getMediaTest());
    assertNotNull(result);
    assertTrue(cr.getStatus().isSuccess());
    if (!docAPI.appendResponse(result)) {

      Response response = GetResponseUtils.getResponseProject(getMediaTest(), result, Project.class);
      assertTrue(response.getSuccess());
      Project prj = (Project) response.getItem();
      assertEquals(prj.getName(), item.getName());
      assertEquals(prj.getDescription(), item.getDescription());
      RIAPUtils.exhaust(result);
    }
  }

  /**
   * Invoke GET
   * 
   * @param item
   *          Project
   * @throws IOException
   *           Exception when copying configuration files from TEST to data/TESTS
   */
  public void retrieve(Project item) throws IOException {
    String url = getBaseUrl() + "/" + item.getId();
    ClientResource cr = new ClientResource(url);
    docAPI.appendRequest(Method.GET, cr);

    Representation result = cr.get(getMediaTest());
    if (!docAPI.appendResponse(result)) {
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());
      Response response = GetResponseUtils.getResponseProject(getMediaTest(), result, Project.class);
      assertTrue(response.getSuccess());
      Project prj = (Project) response.getItem();
      assertEquals(prj.getName(), item.getName());
      assertEquals(prj.getDescription(), item.getDescription());
      RIAPUtils.exhaust(result);
    }
  }

  /**
   * Invoke PUT
   * 
   * @param item
   *          Project
   * @throws IOException
   *           Exception when copying configuration files from TEST to data/TESTS
   */
  public void update(Project item) throws IOException {
    Representation rep = GetRepresentationUtils.getRepresentationProject(item, getMediaTest());
    ClientResource cr = new ClientResource(getBaseUrl() + "/" + item.getId());
    docAPI.appendRequest(Method.PUT, cr, rep);

    Representation result = cr.put(rep, getMediaTest());
    if (!docAPI.appendResponse(result)) {
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());
      Response response = GetResponseUtils.getResponseProject(getMediaTest(), result, Project.class);
      assertTrue(response.getSuccess());
      Project prj = (Project) response.getItem();
      assertEquals(prj.getName(), item.getName());
      assertEquals(prj.getDescription(), item.getDescription());
      RIAPUtils.exhaust(result);
    }
  }

  /**
   * Invoke DELETE
   * 
   * @param item
   *          Project
   * @throws IOException
   *           Exception when copying configuration files from TEST to data/TESTS
   */
  public void delete(Project item) throws IOException {
    String url = getBaseUrl() + "/" + item.getId();
    ClientResource cr = new ClientResource(url);
    docAPI.appendRequest(Method.DELETE, cr);

    Representation result = cr.delete(getMediaTest());
    if (!docAPI.appendResponse(result)) {
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());
      Response response = GetResponseUtils.getResponseProject(getMediaTest(), result, Project.class);
      assertTrue(response.getSuccess());
      RIAPUtils.exhaust(result);
    }
  }

  /**
   * Invokes GET and asserts result response is an empty array.
   * 
   * @throws IOException
   *           Exception when copying configuration files from TEST to data/TESTS
   */
  public void assertNone() throws IOException {
    ClientResource cr = new ClientResource(getBaseUrl());
    docAPI.appendRequest(Method.GET, cr);

    Representation result = cr.get(getMediaTest());
    if (!docAPI.appendResponse(result)) {
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());

      Response response = GetResponseUtils.getResponseProject(getMediaTest(), result, Project.class);
      assertTrue(response.getSuccess());
      assertEquals(0, response.getTotal().intValue());
      RIAPUtils.exhaust(result);
    }
  }

}

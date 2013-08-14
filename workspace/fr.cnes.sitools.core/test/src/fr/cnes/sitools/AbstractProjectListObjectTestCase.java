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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.junit.Test;
import org.restlet.data.ChallengeResponse;
import org.restlet.data.ChallengeScheme;
import org.restlet.data.MediaType;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;

import com.thoughtworks.xstream.XStream;

import fr.cnes.sitools.collections.model.Collection;
import fr.cnes.sitools.common.SitoolsXStreamRepresentation;
import fr.cnes.sitools.common.XStreamFactory;
import fr.cnes.sitools.common.model.Dependencies;
import fr.cnes.sitools.common.model.Resource;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.common.model.Url;
import fr.cnes.sitools.dataset.opensearch.model.Opensearch;
import fr.cnes.sitools.dataset.opensearch.model.OpensearchColumn;
import fr.cnes.sitools.dataset.view.model.DatasetView;
import fr.cnes.sitools.dictionary.model.Concept;
import fr.cnes.sitools.dictionary.model.ConceptTemplate;
import fr.cnes.sitools.dictionary.model.Dictionary;
import fr.cnes.sitools.feeds.model.FeedAuthorModel;
import fr.cnes.sitools.feeds.model.FeedEntryModel;
import fr.cnes.sitools.feeds.model.FeedModel;
import fr.cnes.sitools.form.dataset.dto.FormDTO;
import fr.cnes.sitools.form.dataset.dto.ParameterDTO;
import fr.cnes.sitools.form.dataset.dto.ValueDTO;
import fr.cnes.sitools.form.dataset.dto.ZoneDTO;
import fr.cnes.sitools.form.project.dto.FormProjectDTO;
import fr.cnes.sitools.form.project.model.FormParameter;
import fr.cnes.sitools.plugins.guiservices.declare.model.GuiServiceModel;
import fr.cnes.sitools.project.graph.model.Graph;
import fr.cnes.sitools.project.graph.model.GraphNodeComplete;
import fr.cnes.sitools.project.model.Project;
import fr.cnes.sitools.project.model.ProjectModule;
import fr.cnes.sitools.project.modules.model.ProjectModuleModel;
import fr.cnes.sitools.role.model.Role;
import fr.cnes.sitools.util.Property;
import fr.cnes.sitools.util.RIAPUtils;

/**
 * Test case testing access to Forms, datasets, datasetViews or opensearch list for a given project
 * 
 * @author m.gond (AKKA Technologies)
 * 
 * @version
 * 
 */
public abstract class AbstractProjectListObjectTestCase extends AbstractSitoolsServerTestCase {

  /** project attachment for user */
  private String projectAttach = "/proj/premier";

  /** project attachment for user */
  private String projectAttachEmpty = "/proj/second";

  /** The feed identifier (name) */
  private String feedId = "a_feed";

  /**
   * absolute url for project management REST API
   * 
   * @return url
   */
  protected String getBaseUrl() {
    return super.getHostUrl();
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.cnes.sitools.AbstractSitoolsServerTestCase#setUp()
   */
  @Override
  public void setUp() throws Exception {
    // TODO Auto-generated method stub
    super.setUp();
  }

  /**
   * Test
   */
  @Test
  public void test() {
    docAPI.setActive(false);
    try {
      // test avec projet contenant des objets
      getFormList(projectAttach);
      getDatasetList(projectAttach);
      getGraph(projectAttach);
      getOpensearchList(projectAttach);
      getFeedsList(projectAttach);
      getFeed(projectAttach, feedId);
      getFormProjectList(projectAttach);

      getProjectModulesList(projectAttach, "admin", "admin");
      getProjectModulesList(projectAttach, "", "");

      getProjectModulesDetailsList(projectAttach, "admin", "admin");
      getProjectModulesDetailsList(projectAttach, "", "");

      getDatasetViewsList(projectAttach);
      getGuiServicesList(projectAttach);

      // test avec projet vide
      getFormList(projectAttachEmpty);
      getDatasetList(projectAttachEmpty);
      getGraph(projectAttachEmpty);
      getOpensearchList(projectAttachEmpty);
      getFeedsList(projectAttachEmpty);
      getFormProjectList(projectAttachEmpty);
      createWadl(getBaseUrl() + projectAttach, "project_user");
    }
    catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  /**
   * Test
   */
  @Test
  public void testAPI() {
    docAPI.setActive(true);
    docAPI.appendChapter("Getting list of Object attached to the project");

    docAPI.appendSubChapter("Get forms", "forms");
    try {
      getFormList(projectAttach);
      docAPI.appendSubChapter("Get datasets", "datasets");
      getDatasetList(projectAttach);
      docAPI.appendSubChapter("Get the graph", "graph");
      getGraph(projectAttach);
      docAPI.appendSubChapter("Get opensearch", "opensearch");
      getOpensearchList(projectAttach);
      docAPI.appendSubChapter("Get feeds", "feeds");
      getFeedsList(projectAttach);
      docAPI.appendSubChapter("Get the FormProject (multidataset) list", "formProject");
      getFormProjectList(projectAttach);

      docAPI.appendSubChapter("Get forms, empty result", "formsEmpty");
      getFormList(projectAttachEmpty);
      docAPI.appendSubChapter("Get datasets, empty result", "datasetsEmpty");
      getDatasetList(projectAttachEmpty);
      docAPI.appendSubChapter("Get graph, empty result", "graphEmpty");
      getGraph(projectAttachEmpty);
      docAPI.appendSubChapter("Get opensearch, empty result", "opensearchEmpty");
      getOpensearchList(projectAttachEmpty);
      docAPI.appendSubChapter("Get feeds, empty result", "feedsEmpty");
      getFeedsList(projectAttachEmpty);
      docAPI.appendSubChapter("Get the FormProject (multidataset) list, empty result", "formProjectEmpty");
      getFormProjectList(projectAttachEmpty);

      docAPI.appendSubChapter("Get the project description", "projectModules");
      getProjectModulesList(projectAttach, "", "");

      docAPI.appendSubChapter("Get the list of project modules", "projectModulesList");
      getProjectModulesDetailsList(projectAttach, "", "");
    }
    catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    docAPI.close();
  }

  /**
   * Get the list of form of the project corresponding to the following attachment
   * 
   * @param projectAttach
   *          the attachment of the project
   * @throws IOException
   *           Exception when copying configuration files from TEST to data/TESTS
   */
  private void getFormList(String projectAttach) throws IOException {
    String url = getBaseUrl() + projectAttach + "/forms";
    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      String template = getBaseUrl() + projectAttach + "/forms";
      retrieveDocAPI(url, "", parameters, template);
    }
    else {
      ClientResource cr = new ClientResource(url);
      Representation result = cr.get(getMediaTest());
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());
      Response response = getResponse(getMediaTest(), result, FormDTO.class, true);
      assertTrue(response.getSuccess());
      RIAPUtils.exhaust(result);
    }

  }

  /**
   * Get the list of datasets of the project corresponding to the following attachment
   * 
   * @param projectAttach
   *          the attachment of the project
   * @throws IOException
   *           Exception when copying configuration files from TEST to data/TESTS
   */
  private void getDatasetList(String projectAttach) throws IOException {
    String url = getBaseUrl() + projectAttach + "/datasets";
    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      String template = getBaseUrl() + projectAttach + "/datasets";
      retrieveDocAPI(url, "", parameters, template);
    }
    else {
      ClientResource cr = new ClientResource(url);
      Representation result = cr.get(getMediaTest());
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());
      Response response = getResponse(getMediaTest(), result, Resource.class, true);
      assertTrue(response.getSuccess());
      RIAPUtils.exhaust(result);
    }

  }

  /**
   * Get the graph of the project corresponding to the following attachment
   * 
   * @param projectAttach
   *          the attachment of the project
   * @throws IOException
   *           Exception when copying configuration files from TEST to data/TESTS
   */
  private void getGraph(String projectAttach) throws IOException {
    String url = getBaseUrl() + projectAttach + "/graph";
    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      String template = getBaseUrl() + projectAttach + "/graph";
      retrieveDocAPI(url, "", parameters, template);
    }
    else {
      ClientResource cr = new ClientResource(url);
      Representation result = cr.get(getMediaTest());
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());
      Response response = getResponse(getMediaTest(), result, Graph.class, false);
      assertTrue(response.getSuccess());
      RIAPUtils.exhaust(result);
    }

  }

  /**
   * Get the list of opensearch of the project corresponding to the following attachment
   * 
   * @param projectAttach
   *          the attachment of the project
   * @throws IOException
   *           Exception when copying configuration files from TEST to data/TESTS
   */
  private void getOpensearchList(String projectAttach) throws IOException {
    String url = getBaseUrl() + projectAttach + "/opensearch";
    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      String template = getBaseUrl() + projectAttach + "/opensearch";
      retrieveDocAPI(url, "", parameters, template);
    }
    else {
      ClientResource cr = new ClientResource(url);
      Representation result = cr.get(getMediaTest());
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());
      Response response = getResponse(getMediaTest(), result, Opensearch.class, true);
      assertTrue(response.getSuccess());
      RIAPUtils.exhaust(result);
    }

  }

  /**
   * Get the list of opensearch of the project corresponding to the following attachment
   * 
   * @param projectAttach
   *          the attachment of the project
   * @throws IOException
   *           Exception when copying configuration files from TEST to data/TESTS
   */
  private void getFeedsList(String projectAttach) throws IOException {
    String url = getBaseUrl() + projectAttach + "/feeds";
    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      String template = getBaseUrl() + projectAttach + "/feeds";
      retrieveDocAPI(url, "", parameters, template);
    }
    else {
      ClientResource cr = new ClientResource(url);
      Representation result = cr.get(getMediaTest());
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());
      Response response = getResponse(getMediaTest(), result, FeedModel.class, true);
      assertTrue(response.getSuccess());
      RIAPUtils.exhaust(result);
    }

  }

  /**
   * Get the list of roles of the project modules of the project corresponding to the following attachment
   * 
   * @param projectAttach
   *          the attachment of the project
   * @param userId
   *          the user who wants to retrieve projectModules
   * @param password
   *          the user's password
   * @throws IOException
   *           Exception when copying configuration files from TEST to data/TESTS
   */
  private void getProjectModulesList(String projectAttach, String userId, String password) throws IOException {
    if (getMediaTest().isCompatible(MediaType.APPLICATION_ALL_XML)) {
      String url = getBaseUrl() + projectAttach;
      if (docAPI.isActive()) {
        Map<String, String> parameters = new LinkedHashMap<String, String>();
        String template = getBaseUrl() + projectAttach;
        retrieveDocAPI(url, "", parameters, template);
      }
      else {
        ClientResource cr = new ClientResource(url);
        ChallengeResponse chal = new ChallengeResponse(ChallengeScheme.HTTP_BASIC, userId, password);
        cr.setChallengeResponse(chal);
        Representation result = cr.get(getMediaTest());
        assertNotNull(result);
        assertTrue(cr.getStatus().isSuccess());

        Response response = getResponse(getMediaTest(), result, Project.class);
        assertNotNull(response.getItem());

        Project prj = (Project) response.getItem();
        assertNotNull(prj);
        assertNotNull(prj.getModules());
        if (userId.equals("admin")) {
          assertEquals(3, prj.getModules().size());
        }
        else {
          assertEquals(2, prj.getModules().size());
        }
        RIAPUtils.exhaust(result);
      }
    }
  }

  /**
   * Get the list of projectModules of the project corresponding to the following attachment
   * 
   * @param projectAttach
   *          the attachment of the project
   * @param userId
   *          the user who wants to retrieve projectModules
   * @param password
   *          the user's password
   * @throws IOException
   *           Exception when copying configuration files from TEST to data/TESTS
   */
  private void getProjectModulesDetailsList(String projectAttach, String userId, String password) throws IOException {
    String url = getBaseUrl() + projectAttach + "/projectModules";
    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      String template = getBaseUrl() + projectAttach;
      retrieveDocAPI(url, "", parameters, template);
    }
    else {
      ClientResource cr = new ClientResource(url);
      ChallengeResponse chal = new ChallengeResponse(ChallengeScheme.HTTP_BASIC, userId, password);
      cr.setChallengeResponse(chal);
      Representation result = cr.get(getMediaTest());
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());

      Response response = getResponse(getMediaTest(), result, ProjectModuleModel.class, true);
      assertNotNull(response.getData());

      List<Object> projectModules = response.getData();
      assertNotNull(projectModules);
      if (userId.equals("admin")) {
        assertEquals(3, projectModules.size());
      }
      else {
        assertEquals(2, projectModules.size());
      }
      RIAPUtils.exhaust(result);
    }

  }

  /**
   * Get the list of datasetViews of the project corresponding to the following attachment
   * 
   * @param projectAttach
   *          the attachment of the project
   * @param userId
   *          the user who wants to retrieve projectModules
   * @param password
   *          the user's password
   * @throws IOException
   *           Exception when copying configuration files from TEST to data/TESTS
   */
  private void getDatasetViewsList(String projectAttach) {
    String url = getBaseUrl() + projectAttach + "/datasetViews";
    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      String template = getBaseUrl() + projectAttach;
      retrieveDocAPI(url, "", parameters, template);
    }
    else {
      ClientResource cr = new ClientResource(url);
      Representation result = cr.get(getMediaTest());
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());

      Response response = getResponse(getMediaTest(), result, DatasetView.class, true);
      assertNotNull(response.getData());

      List<Object> datasetViews = response.getData();
      assertNotNull(datasetViews);

      RIAPUtils.exhaust(result);
    }
  }

  /**
   * Get the list of feeds attached to a portal
   * 
   * @param projectAttach
   *          the project attachment
   * 
   * @param feedId
   *          the feed name
   * @throws IOException
   *           Exception when copying configuration files from TEST to data/TESTS
   */
  private void getFeed(String projectAttach, String feedId) throws IOException {
    String url = getBaseUrl() + projectAttach + "/clientFeeds/" + feedId;
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

  private void getFormProjectList(String attach) {

    String url = getBaseUrl() + attach + "/formsProject";
    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      String template = getBaseUrl() + projectAttach + "/formsProject";
      retrieveDocAPI(url, "", parameters, template);
    }
    else {
      ClientResource cr = new ClientResource(url);
      Representation result = cr.get(getMediaTest());
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());
      Response response = getResponse(getMediaTest(), result, FormProjectDTO.class, true);
      assertTrue(response.getSuccess());
      RIAPUtils.exhaust(result);
    }
  }

  private void getGuiServicesList(String projectAttach) {
    String url = getBaseUrl() + projectAttach + "/guiServices";
    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      String template = getBaseUrl() + projectAttach;
      retrieveDocAPI(url, "", parameters, template);
    }
    else {
      ClientResource cr = new ClientResource(url);
      Representation result = cr.get(getMediaTest());
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());

      Response response = getResponse(getMediaTest(), result, GuiServiceModel.class, true);
      assertNotNull(response.getData());

      List<Object> guiServices = response.getData();
      assertNotNull(guiServices);

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
        Logger.getLogger(AbstractSitoolsTestCase.class.getName()).warning("Only JSON or XML supported in tests");
        return null;
      }

      XStream xstream = XStreamFactory.getInstance().getXStreamReader(media);
      xstream.autodetectAnnotations(false);

      // for the project exposition
      xstream.alias("project", Project.class);
      xstream.alias("dataset", Resource.class);

      xstream.alias("response", Response.class);
      // for forms
      xstream.alias("formDTO", FormDTO.class);
      // for datasets
      xstream.alias("item", dataClass);
      // for graphs
      xstream.alias("graphNodeComplete", GraphNodeComplete.class);
      // for opensearch
      xstream.alias("opensearchColumn", OpensearchColumn.class);
      // for feeds
      xstream.alias("FeedModel", FeedModel.class);
      xstream.alias("FeedEntryModel", FeedEntryModel.class);
      xstream.alias("FeedAuthorModel", FeedAuthorModel.class);
      // for formProject
      xstream.alias("formProject", FormProjectDTO.class);
      if (dataClass == FormProjectDTO.class && media.isCompatible(MediaType.APPLICATION_JSON)) {
        xstream.addImplicitCollection(Collection.class, "dataSets", Resource.class);
        xstream.addImplicitCollection(Dictionary.class, "concepts", Concept.class);
        xstream.addImplicitCollection(ConceptTemplate.class, "properties", Property.class);
        xstream.addImplicitCollection(Resource.class, "properties", Property.class);

      }

      xstream.alias("dictionary", Dictionary.class);
      xstream.alias("collection", Collection.class);
      xstream.alias("conceptTemplate", ConceptTemplate.class);

      // for projectModules
      xstream.alias("ProjectModuleModel", ProjectModuleModel.class);
      xstream.alias("dependencies", Dependencies.class);

      // for guiServices
      xstream.alias("guiService", GuiServiceModel.class);

      if (isArray) {
        if (media.isCompatible(MediaType.APPLICATION_JSON)) {

          // for forms, always lists
          xstream.addImplicitCollection(Response.class, "data", dataClass);

          // Sans []
          xstream.addImplicitCollection(FormDTO.class, "parameters", "parameters", ParameterDTO.class);
          xstream.addImplicitCollection(FormDTO.class, "zones", "zones", ZoneDTO.class);
          xstream.addImplicitCollection(ZoneDTO.class, "params", "params", ParameterDTO.class);

          xstream.addImplicitCollection(ParameterDTO.class, "values", "values", ValueDTO.class);
          xstream.addImplicitCollection(ParameterDTO.class, "defaultValues", "defaultValues", String.class);
          xstream.addImplicitCollection(ParameterDTO.class, "code", "code", String.class);

          // Avec []
          xstream.aliasField("parameters", FormDTO.class, "parameters");
          xstream.aliasField("values", ParameterDTO.class, "values");

          // end for forms

          // for opensearch
          xstream.addImplicitCollection(Opensearch.class, "indexedColumns", OpensearchColumn.class);
          xstream.addImplicitCollection(Opensearch.class, "keywordColumns", String.class);

          // for feeds
          xstream.addImplicitCollection(FeedModel.class, "entries", FeedEntryModel.class);

          // for FormProject
          xstream.addImplicitCollection(FormProjectDTO.class, "parameters", FormParameter.class);
          xstream.addImplicitCollection(FormProjectDTO.class, "properties", String.class);

          // for projectModules
          xstream.addImplicitCollection(ProjectModuleModel.class, "listRoles", Role.class);
          xstream.addImplicitCollection(Dependencies.class, "js", Url.class);
          xstream.addImplicitCollection(Dependencies.class, "css", Url.class);

        }
      }
      else {

        xstream.alias("item", Object.class, dataClass);

        if (dataClass == Graph.class) {
          xstream.aliasField("graph", Response.class, "item");
          if (media.equals(MediaType.APPLICATION_JSON)) {
            xstream.addImplicitCollection(Graph.class, "nodeList", GraphNodeComplete.class);
            xstream.addImplicitCollection(GraphNodeComplete.class, "children", GraphNodeComplete.class);
            xstream.aliasField("nodeList", Graph.class, "nodeList");
            xstream.aliasField("children", GraphNodeComplete.class, "children");
          }
          // if (dataClass == DataSet.class)
          // xstream.aliasField("dataset", Response.class, "item");
        }

        if (dataClass == Project.class) {
          if (media.equals(MediaType.APPLICATION_JSON)) {
            xstream.addImplicitCollection(Project.class, "dataSets", Resource.class);
            xstream.addImplicitCollection(Project.class, "modules", ProjectModule.class);
            xstream.addImplicitCollection(ProjectModule.class, "listRoles", Role.class);
            xstream.aliasField("listRoles", ProjectModule.class, "listRoles");

          }
          xstream.aliasField("project", Response.class, "item");
        }

        // feeds
        if (dataClass == FeedModel.class) {
          xstream.aliasField("FeedModel", Response.class, "item");
          if (media.equals(MediaType.APPLICATION_JSON)) {
            xstream.addImplicitCollection(FeedModel.class, "entries", FeedEntryModel.class);
          }
        }

        if (dataClass == FormProjectDTO.class) {
          xstream.aliasField("formProject", Response.class, "item");
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
        return null; // TODO complete test with ObjectRepresentation
      }
    }
    finally {
      RIAPUtils.exhaust(representation);
    }
  }
}

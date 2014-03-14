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

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.engine.Engine;
import org.restlet.ext.jackson.JacksonRepresentation;
import org.restlet.ext.xstream.XstreamRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;

import com.thoughtworks.xstream.XStream;

import fr.cnes.sitools.common.SitoolsSettings;
import fr.cnes.sitools.common.SitoolsXStreamRepresentation;
import fr.cnes.sitools.common.XStreamFactory;
import fr.cnes.sitools.common.model.ExtensionModel;
import fr.cnes.sitools.common.model.Resource;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.common.store.SitoolsStore;
import fr.cnes.sitools.form.dataset.dto.ParameterDTO;
import fr.cnes.sitools.form.project.dto.FormProjectAdminDTO;
import fr.cnes.sitools.form.project.dto.FormPropertyParameterDTO;
import fr.cnes.sitools.form.project.model.FormParameter;
import fr.cnes.sitools.form.project.model.FormProject;
import fr.cnes.sitools.plugins.resources.dto.ResourceModelDTO;
import fr.cnes.sitools.plugins.resources.dto.ResourcePluginDescriptionDTO;
import fr.cnes.sitools.plugins.resources.model.ResourceParameter;
import fr.cnes.sitools.server.Consts;
import fr.cnes.sitools.util.RIAPUtils;

public class AbstractFormProjectTestCase extends AbstractSitoolsServerTestCase {
  /**
   * static xml store instance for the test
   */
  private static SitoolsStore<FormProject> store = null;

  /** The settings */
  private SitoolsSettings settings = SitoolsSettings.getInstance();

  /** projectId */
  private String projectId = "premier";

  /** The url of the project */
  private String projectUrl = "/proj/premier";

  /**
   * absolute url for dataset management REST API
   * 
   * @return url
   */
  protected String getBaseUrl() {
    return super.getBaseUrl() + settings.getString(Consts.APP_PROJECTS_URL) + "/" + projectId
      + settings.getString(Consts.APP_FORMPROJECT_URL);
  }

  /**
   * Absolute path location for project store files
   * 
   * @return path
   */
  protected String getTestRepository() {
    return settings.getStoreDIR(Consts.APP_FORMPROJECT_STORE_DIR);
  }

  @Before
  @Override
  /**
   * Init and Start a server with GraphApplication
   * 
   * @throws java.lang.Exception
   */
  public void setUp() throws Exception {
    if (store == null) {
      File storeDirectory = new File(getTestRepository());
      cleanDirectory(storeDirectory);
    }
  }

  protected String getResourceBaseUrl() {
    return super.getBaseUrl() + settings.getString(Consts.APP_PROJECTS_URL) + "/" + projectId
      + settings.getString(Consts.APP_RESOURCES_URL);
  }

  /**
   * Test FormProject
   */
  @Test
  public void test() {

    docAPI.setActive(false);
    assertNone();
    // create a new formProject
    FormProjectAdminDTO formProject = createFormProjectObject("100000");
    // add it to the server
    create(formProject);

    retrieve(formProject);

    retrieveByName(formProject);

    update(formProject);

    delete(formProject);

    assertNone();

  }

  /**
   * Test CRUD formProject with API
   */
  @Test
  public void testAPI() {

    docAPI.setActive(true);
    docAPI.appendChapter("Manipulating FormProject Collection");
    assertNone();
    // create a new formProject
    FormProjectAdminDTO formProject = createFormProjectObject("100000");
    // add it to the server
    docAPI.appendSubChapter("Create a new FormProject", "create");
    create(formProject);
    docAPI.appendSubChapter("retrieve a FormProject", "retrieve");
    retrieve(formProject);
    docAPI.appendSubChapter("Update a FormProject", "update");
    update(formProject);
    docAPI.appendSubChapter("Delete new FormProject", "delete");
    delete(formProject);

    docAPI.close();

  }

  /**
   * Invokes GET and asserts result response is an empty array.
   */
  public void assertNone() {
    String url = getBaseUrl();
    ClientResource cr = new ClientResource(url);
    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      retrieveDocAPI(url, "", parameters, url);
    }
    else {
      Representation result = cr.get(getMediaTest());
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());

      Response response = getResponse(getMediaTest(), result, FormProjectAdminDTO.class, true);
      assertTrue(response.getSuccess());
      assertEquals(new Integer(0), response.getTotal());
      RIAPUtils.exhaust(result);
    }

  }

  private FormProjectAdminDTO createFormProjectObject(String id) {
    FormProjectAdminDTO formProject = new FormProjectAdminDTO();
    formProject.setName("A form project");
    formProject.setId(id);
    formProject.setDescription("A form project description");

    formProject.setUrlServiceDatasetSearch("/search/datasets");
    formProject.setUrlServicePropertiesSearch("/search/properties");

    Resource collection = new Resource();
    collection.setId("collectionId");
    collection.setUrl(settings.getString(Consts.APP_COLLECTIONS_URL) + "/" + collection.getId());
    formProject.setCollection(collection);

    Resource dictionnary = new Resource();
    dictionnary.setId("dictionnaryId");
    dictionnary.setUrl(settings.getString(Consts.APP_DICTIONARIES_URL) + "/" + dictionnary.getId());
    formProject.setDictionary(dictionnary);

    formProject.setNbDatasetsMax(10);

    formProject.setUrlServiceDatasetSearch("/searchOnDataset");
    formProject.setUrlServicePropertiesSearch("/searchOnProperties");

    List<FormPropertyParameterDTO> properties = new ArrayList<FormPropertyParameterDTO>();
    properties.add(new FormPropertyParameterDTO("prop1", "textField"));
    properties.add(new FormPropertyParameterDTO("prop2", "textField"));
    formProject.setProperties(properties);

    // parameters
    List<ParameterDTO> parameters = new ArrayList<ParameterDTO>();
    ParameterDTO param1 = new ParameterDTO();
    param1.setLabel("param1");
    parameters.add(param1);
    ParameterDTO param2 = new ParameterDTO();
    param2.setLabel("param2");
    parameters.add(param2);

    
    formProject.setParameters(parameters);

    return formProject;
  }

  private void create(FormProjectAdminDTO formProject) {
    String url = getBaseUrl();
    Representation rep = getRepresentation(formProject, getMediaTest());
    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      parameters.put("identifier", "project identifier");
      parameters.put("POST", "A <i>FormProject</i> object");
      postDocAPI(url, "", rep, parameters, String.format(getBaseUrl(), "%identifier%"));
    }
    else {
      ClientResource cr = new ClientResource(getBaseUrl());
      Representation result = cr.post(rep, getMediaTest());
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());
      Response response = getResponse(getMediaTest(), result, FormProjectAdminDTO.class);
      assertTrue(response.getSuccess());
      FormProjectAdminDTO formProjectOut = (FormProjectAdminDTO) response.getItem();
      assertEquals(formProject.getName(), formProjectOut.getName());

      RIAPUtils.exhaust(result);
    }

  }

  private void retrieve(FormProjectAdminDTO formProject) {
    String url = getBaseUrl() + "/" + formProject.getId();
    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      parameters.put("identifier", "FormProject identifier");
      retrieveDocAPI(url, "", parameters, getBaseUrl() + "/%identifier%");
    }
    else {
      ClientResource cr = new ClientResource(url);
      Representation result = cr.get(getMediaTest());
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());
      Response response = getResponse(getMediaTest(), result, FormProjectAdminDTO.class);
      assertTrue(response.getSuccess());
      FormProjectAdminDTO formProjectOut = (FormProjectAdminDTO) response.getItem();
      assertEquals(formProject.getName(), formProjectOut.getName());

      assertServices(formProjectOut);

      RIAPUtils.exhaust(result);
    }
  }

  private void retrieveByName(FormProjectAdminDTO formProject) {
    String url = getBaseUrl();
    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      parameters.put("identifier", "FormProject identifier");
      retrieveDocAPI(url, "", parameters, getBaseUrl() + "/%identifier%");
    }
    else {
      Reference ref = new Reference(url);
      ref.addQueryParameter("query", "A form project");
      ClientResource cr = new ClientResource(ref);

      Representation result = cr.get(getMediaTest());

      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());
      Response response = getResponse(getMediaTest(), result, FormProjectAdminDTO.class, true);
      assertTrue(response.getSuccess());
      assertEquals(new Integer(1), response.getTotal());

      RIAPUtils.exhaust(result);
    }
  }

  private void update(FormProjectAdminDTO formProject) {
    String url = getBaseUrl() + "/" + formProject.getId();
    formProject.setName(formProject.getName() + "_updated");
    Representation rep = getRepresentation(formProject, getMediaTest());
    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      parameters.put("identifier", "project identifier");
      parameters.put("POST", "A <i>FormProject</i> object");
      putDocAPI(url, "", rep, parameters, String.format(getBaseUrl(), "%identifier%"));
    }
    else {
      ClientResource cr = new ClientResource(url);
      Representation result = cr.put(rep, getMediaTest());
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());
      Response response = getResponse(getMediaTest(), result, FormProjectAdminDTO.class);
      assertTrue(response.getSuccess());
      FormProjectAdminDTO formProjectOut = (FormProjectAdminDTO) response.getItem();
      assertEquals(formProject.getName(), formProjectOut.getName());
      assertServices(formProjectOut);

      RIAPUtils.exhaust(result);
    }
  }

  private void delete(FormProjectAdminDTO formProject) {
    String url = getBaseUrl() + "/" + formProject.getId();
    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      parameters.put("identifier", "collection identifier");
      deleteDocAPI(url, "", parameters, getBaseUrl() + "/%identifier%");
    }
    else {
      ClientResource cr = new ClientResource(url);
      Representation result = cr.delete(getMediaTest());
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());
      Response response = getResponse(getMediaTest(), result, FormProjectAdminDTO.class);
      assertTrue(response.getSuccess());
      RIAPUtils.exhaust(result);
    }
  }

  /**
   * Assert that the different services has been created, attached and that the parameters value are correct
   * 
   * @param formProjectOut
   *          the FormProject
   */
  private void assertServices(FormProjectAdminDTO formProjectOut) {
    // SEARCH SERVICE
    String idServiceSearch = formProjectOut.getIdServiceDatasetSearch();
    ResourceModelDTO dtoServiceSearch = getResourceModelDTO(idServiceSearch);
    assertNotNull(dtoServiceSearch);
    assertNotNull(dtoServiceSearch.getParameters());

    assertParameterValue(dtoServiceSearch.getParameters(), "url", formProjectOut.getUrlServiceDatasetSearch());
    assertParameterValue(dtoServiceSearch.getParameters(), "dictionary", formProjectOut.getDictionary().getId());
    assertParameterValue(dtoServiceSearch.getParameters(), "collection", formProjectOut.getCollection().getId());
    assertParameterValue(dtoServiceSearch.getParameters(), "nbThreads",
      settings.getString(Consts.DEFAULT_THREAD_POOL_SIZE));
    assertParameterValue(dtoServiceSearch.getParameters(), "nbDatasetsMax", formProjectOut.getNbDatasetsMax()
      .toString());

    // PROPERTIES SERVICE
    String idServiceProperties = formProjectOut.getIdServicePropertiesSearch();
    ResourceModelDTO dtoServiceProperties = getResourceModelDTO(idServiceProperties);
    assertNotNull(dtoServiceProperties);
    assertNotNull(dtoServiceProperties.getParameters());

    assertParameterValue(dtoServiceProperties.getParameters(), "url", formProjectOut.getUrlServicePropertiesSearch());
    assertParameterValue(dtoServiceProperties.getParameters(), "dictionary", formProjectOut.getDictionary().getId());
    assertParameterValue(dtoServiceProperties.getParameters(), "collection", formProjectOut.getCollection().getId());

  }

  /**
   * Assert that there is a {@link ResourceParameter} with the name paramName and the value expectedParamValue in the
   * {@link List} parameters
   * 
   * @param parameters
   *          the List
   * @param paramName
   *          the name of the {@link ResourceParameter}
   * @param expectedParamValue
   *          the value of the {@link ResourceParameter}
   */
  private void assertParameterValue(List<ResourceParameter> parameters, String paramName, String expectedParamValue) {
    boolean found = false;
    for (Iterator<ResourceParameter> iterator = parameters.iterator(); iterator.hasNext() && !found;) {
      ResourceParameter param = iterator.next();
      if (paramName.equals(param.getName())) {
        found = true;
        assertEquals(expectedParamValue, param.getValue());
      }
    }
    assertTrue(found);
  }

  /**
   * Get a {@link ResourceModelDTO} from its id
   * 
   * @param resourceId
   *          the id
   * @return a {@link ResourceModelDTO} with the specified id
   */
  private ResourceModelDTO getResourceModelDTO(String resourceId) {
    String url = getResourceBaseUrl() + "/" + resourceId;

    ClientResource cr = new ClientResource(url);
    Representation result = cr.get(getMediaTest());
    assertNotNull(result);
    assertTrue(cr.getStatus().isSuccess());
    Response response = getResponseResourceModelDTO(getMediaTest(), result, ResourceModelDTO.class, false);
    assertTrue(response.getSuccess());
    assertNotNull(response.getItem());
    return (ResourceModelDTO) response.getItem();
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
      xstream.alias("response", Response.class);
      xstream.alias("formProject", FormProjectAdminDTO.class);

      if (media.equals(MediaType.APPLICATION_JSON)) {
        xstream.addImplicitCollection(FormProjectAdminDTO.class, "parameters", FormParameter.class);
        xstream.addImplicitCollection(FormProjectAdminDTO.class, "properties", String.class);

      }

      if (isArray) {
        xstream.alias("item", dataClass);
        xstream.alias("item", Object.class, dataClass);
        if (media.equals(MediaType.APPLICATION_JSON)) {
          xstream.addImplicitCollection(Response.class, "data", dataClass);
        }
      }
      else {
        xstream.alias("item", dataClass);
        xstream.alias("item", Object.class, dataClass);
        xstream.aliasField("formProject", Response.class, "item");

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
        return null; // TODO complete test with ObjectRepresentation
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
  public static Representation getRepresentation(FormProjectAdminDTO item, MediaType media) {
    if (media.equals(MediaType.APPLICATION_JSON)) {
      return new JacksonRepresentation<FormProjectAdminDTO>(item);
    }
    else if (media.equals(MediaType.APPLICATION_XML)) {
      XStream xstream = XStreamFactory.getInstance().getXStream(media, false);
      XstreamRepresentation<FormProjectAdminDTO> rep = new XstreamRepresentation<FormProjectAdminDTO>(media, item);
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
   * Configures XStream mapping of Response object with FormProject content.
   * 
   * @param xstream
   *          XStream
   */
  private static void configure(XStream xstream) {
    xstream.autodetectAnnotations(false);
    xstream.alias("response", Response.class);

    // Parce que les annotations ne sont apparemment prises en compte
    xstream.omitField(Response.class, "itemName");
    xstream.omitField(Response.class, "itemClass");
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
  public static Response getResponseResourceModelDTO(MediaType media, Representation representation,
    Class<?> dataClass, boolean isArray) {
    try {
      if (!media.isCompatible(MediaType.APPLICATION_JSON) && !media.isCompatible(MediaType.APPLICATION_XML)) {
        Engine.getLogger(AbstractSitoolsTestCase.class.getName()).warning("Only JSON or XML supported in tests");
        return null;
      }

      XStream xstream = XStreamFactory.getInstance().getXStreamReader(media);
      xstream.autodetectAnnotations(false);
      xstream.alias("response", Response.class);
      xstream.alias("resourcePlugin", ResourceModelDTO.class);
      xstream.alias("resourceParameter", ResourceParameter.class);
      xstream.alias("ResourcePluginDescriptionDTO", ResourcePluginDescriptionDTO.class);

      xstream.omitField(ExtensionModel.class, "parametersMap");

      if (media.isCompatible(MediaType.APPLICATION_JSON)) {
        xstream.addImplicitCollection(ResourceModelDTO.class, "parameters", ResourceParameter.class);
      }

      if (isArray) {
        if (media.isCompatible(MediaType.APPLICATION_JSON)) {
          xstream.addImplicitCollection(Response.class, "data", dataClass);
          if (dataClass == ResourceModelDTO.class) {
            xstream.addImplicitCollection(ResourceModelDTO.class, "parameters", ResourceParameter.class);
          }
        }
        else {
          xstream.alias("resourcePlugin", Object.class, ResourceModelDTO.class);
        }
      }
      else {
        xstream.aliasField("resourcePlugin", Response.class, "item");
        xstream.alias("resourcePlugin", Object.class, ResourceModelDTO.class);
      }

      SitoolsXStreamRepresentation<Response> rep = new SitoolsXStreamRepresentation<Response>(representation);
      rep.setXstream(xstream);

      if (media.isCompatible(getMediaTest())) {
        Response response = rep.getObject("response");

        return response;
      }
      else {
        Engine.getLogger(AbstractSitoolsTestCase.class.getName()).warning("Only JSON or XML supported in tests");
        return null;
        // TODO complete test with ObjectRepresentation
      }
    }
    finally {
      RIAPUtils.exhaust(representation);
    }
  }

}

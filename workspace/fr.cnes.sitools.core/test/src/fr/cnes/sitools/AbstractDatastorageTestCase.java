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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.restlet.Client;
import org.restlet.Request;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Protocol;
import org.restlet.data.Status;
import org.restlet.engine.Engine;
import org.restlet.ext.jackson.JacksonRepresentation;
import org.restlet.ext.xstream.XstreamRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.ClientResource;

import com.thoughtworks.xstream.XStream;

import fr.cnes.sitools.common.SitoolsSettings;
import fr.cnes.sitools.common.SitoolsXStreamRepresentation;
import fr.cnes.sitools.common.XStreamFactory;
import fr.cnes.sitools.common.model.ExtensionModel;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.plugins.filters.dto.FilterModelDTO;
import fr.cnes.sitools.plugins.filters.model.FilterParameter;
import fr.cnes.sitools.plugins.filters.model.FilterParameterType;
import fr.cnes.sitools.server.Consts;
import fr.cnes.sitools.service.storage.model.StorageDirectory;
import fr.cnes.sitools.util.RIAPUtils;

/**
 * Tests the Datastorage application and ressources Also tests the security filter on that datastorage
 * 
 * 
 * @author m.gond
 */
public abstract class AbstractDatastorageTestCase extends SitoolsServerTestCase {
  /** Initial number of datastorages at startup */
  private static final int NB_INITIAL_DATASTORAGE = 2;
  /** The storage local path */
  private String storagePath = "file:///${ROOT_DIRECTORY}/data/TESTS";
  /** The url attachment */
  private String urlAttach = "/datatests";
  /** The storage id */
  private String storageId = "4568714657986";
  /** The url of the file to query */
  private String fileToQuery = "/files_for_orders/file_for_orders.txt";
  /** The filter model class */
  private String storageCustomModelFilterClass = "fr.cnes.sitools.filter.authorizer.DataStorageAuthorizerModel";
  /** The filter class */
  private String storageCustomFilterClass = "fr.cnes.sitools.filter.authorizer.DataStorageAuthorizer";
  /** The attachment of the datastorage with a filter already attached */
  private String storageWithFilterId = "storage_test_with_security_filter";

  /**
   * Get the admin datastorage url
   * 
   * @return the admin datastorage url
   */
  protected String getAdminDataStorageBaseUrl() {
    return super.getBaseUrl() + SitoolsSettings.getInstance().getString(Consts.APP_DATASTORAGE_ADMIN_URL) + "/directories";
  }

  /**
   * Get the user datastorage url
   * 
   * @return the user datastorage url
   */
  protected String getUserDataStorageBaseUrl() {
    return super.getBaseUrl() + SitoolsSettings.getInstance().getString(Consts.APP_DATASTORAGE_URL);
  }

  /**
   * Test CRUD -> Create a datastorage -> Change the status -> add a filter to block all requests -> add a filter to
   * block no request -> delete the filter -> delete the storage
   */
  @Test
  public void testCRUD() {
    docAPI.setActive(false);
    assertNone();
    StorageDirectory storage = createObject(storageId, storagePath, urlAttach);
    create(storage);
    retrieveStorageDirectory(storage.getId());
    queryStorage(storage, fileToQuery);
    changeStatus(storage, "stop");
    FilterModelDTO filter = createFilterModelDTO(storage, storageCustomModelFilterClass, storageCustomFilterClass, false);
    actOnCustomFilter(storage, filter, "POST");
    retrieveCustomFilter(storage, filter);
    changeStatus(storage, "start");
    queryStorageForbiden(storage, fileToQuery);
    changeStatus(storage, "stop");
    filter = createFilterModelDTO(storage, storageCustomModelFilterClass, storageCustomFilterClass, true);
    actOnCustomFilter(storage, filter, "PUT");
    changeStatus(storage, "start");
    queryStorage(storage, fileToQuery);
    deleteCustomFilter(storage);
    delete(storage);
  }

  /**
   * Test that a datastorage is correctly restarted at server startup even if it has a filter attached to it
   */
  @Test
  public void testDatastorageWithFilterAfterServerRestart() {
    docAPI.setActive(false);
    StorageDirectory storage = retrieveStorageDirectory(storageWithFilterId);
    assertEquals("STARTED", storage.getStatus());
    queryStorage(storage, fileToQuery);
  }

  /**
   * Assert that no storage are on the server
   */
  private void assertNone() {
    String url = getAdminDataStorageBaseUrl();
    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      parameters.put("id", "Dataset identifier");
      String template = getBaseUrl() + "/%id/mappings";
      retrieveDocAPI(url, "", parameters, template);
    }
    else {
      ClientResource cr = new ClientResource(url);
      Representation result = cr.get(getMediaTest());
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());
      Response response = getResponse(getMediaTest(), result, StorageDirectory.class, true);
      assertNotNull(response);
      assertTrue(response.getSuccess());
      assertEquals(new Integer(NB_INITIAL_DATASTORAGE), response.getTotal());

      // RIAPUtils.exhaust(result);
    }
  }

  /**
   * Create a new StorageDirectory Object
   * 
   * @param storageId
   *          the identifier
   * @param storagePath
   *          the storage local path
   * @param attachUrl
   *          the attach url
   * @return the new StorageDirectory Object
   */
  private StorageDirectory createObject(String storageId, String storagePath, String attachUrl) {
    StorageDirectory storage = new StorageDirectory();
    storage.setId(storageId);
    storage.setName("datastorage");
    storage.setDescription("datastorage description");
    storage.setLocalPath(storagePath);

    storage.setAttachUrl(attachUrl);
    storage.setDeeplyAccessible(true);
    storage.setListingAllowed(true);
    storage.setModifiable(true);

    return storage;
  }

  /**
   * Add the given DirectoryStorage to the server
   * 
   * @param storage
   *          the DirectoryServer
   */
  private void create(StorageDirectory storage) {
    String url = getAdminDataStorageBaseUrl();
    Representation repr = getRepresentation(storage, getMediaTest());
    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      parameters.put("POST", "A <i>StorageDirectory</i> object");
      String template = getBaseUrl();
      postDocAPI(url, "", repr, parameters, template);
    }
    else {
      ClientResource cr = new ClientResource(url);
      Representation result = cr.post(repr, getMediaTest());
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());
      Response response = getResponse(getMediaTest(), result, StorageDirectory.class, false);
      assertNotNull(response);
      assertTrue(response.getSuccess());
      assertNotNull(response.getItem());

      StorageDirectory storageOutput = (StorageDirectory) response.getItem();

      assertEquals(storage.getName(), storageOutput.getName());
      assertEquals(storage.getLocalPath(), storageOutput.getLocalPath());
      assertEquals(storage.getAttachUrl(), storageOutput.getAttachUrl());
      assertEquals("STARTED", storageOutput.getStatus());
    }
  }

  private StorageDirectory retrieveStorageDirectory(String id) {
    String url = getAdminDataStorageBaseUrl() + "/" + id;
    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      parameters.put("id", "Datastorage identifier");
      String template = getBaseUrl() + "/{id}";
      retrieveDocAPI(url, "", parameters, template);
      return null;
    }
    else {
      ClientResource cr = new ClientResource(url);
      Representation result = cr.get(getMediaTest());
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());
      Response response = getResponse(getMediaTest(), result, StorageDirectory.class, false);
      assertNotNull(response);
      assertTrue(response.getSuccess());
      assertNotNull(response.getItem());

      StorageDirectory storageOutput = (StorageDirectory) response.getItem();
      return storageOutput;
    }

  }

  /**
   * Change the status of the given StorageDirectory
   * 
   * @param storage
   *          the storage
   * @param action
   *          the action to perform (start or stop)
   */
  private void changeStatus(StorageDirectory storage, String action) {
    String url = getAdminDataStorageBaseUrl() + "/" + storage.getId() + "?action=" + action;
    Representation repr = new StringRepresentation("");
    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      parameters.put("id", "Datastorage identifier");
      parameters.put("action", "The action to perform ( start or stop ) ");
      String template = getBaseUrl() + "/{id}?action=" + action;
      putDocAPI(url, "", repr, parameters, template);
    }
    else {
      ClientResource cr = new ClientResource(url);
      Representation result = cr.put(repr, getMediaTest());
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());
      Response response = getResponse(getMediaTest(), result, StorageDirectory.class, false);
      assertNotNull(response);
      assertTrue(response.getSuccess());
      assertNotNull(response.getItem());

      StorageDirectory storageOutput = (StorageDirectory) response.getItem();
      if ("start".equals(action)) {
        assertEquals("STARTED", storageOutput.getStatus());
      }
      else {
        assertEquals("STOPPED", storageOutput.getStatus());
      }

    }

  }

  /**
   * Query the storage at the url of the file to query
   * 
   * @param storage
   *          the storage to query
   * @param fileToQuery
   *          the url of the file to query
   */
  private void queryStorage(StorageDirectory storage, String fileToQuery) {
    String url = getUserDataStorageBaseUrl() + storage.getAttachUrl() + fileToQuery;
    ClientResource cr = new ClientResource(url);
    Representation result = cr.get();
    assertNotNull(result);
    assertTrue(cr.getStatus().isSuccess());
  }

  /**
   * Query the storage at the specified url and assert that it is forbiden
   * 
   * @param storage
   *          the storage to query
   * @param fileToQuery
   *          the url of the file to query
   */
  private void queryStorageForbiden(StorageDirectory storage, String fileToQuery) {
    String url = getUserDataStorageBaseUrl() + storage.getAttachUrl() + fileToQuery;
    final Client client = new Client(Protocol.HTTP);
    Request request = new Request(Method.GET, url);
    org.restlet.Response response = client.handle(request);
    try {
      assertNotNull(response);
      assertTrue(response.getStatus().isError());
      assertEquals(Status.CLIENT_ERROR_FORBIDDEN, response.getStatus());
    }
    finally {
      RIAPUtils.exhaust(response);
    }
  }

  /**
   * Create a new FilterModelDTO object
   * 
   * @param storage
   *          the storage
   * @param className
   *          the model class name
   * @param filterClassName
   *          the filter class name
   * @param authorized
   *          set the filter as authorized or not
   * @return a new FilterModelDTO
   */
  private FilterModelDTO createFilterModelDTO(StorageDirectory storage, String className, String filterClassName, boolean authorized) {
    FilterModelDTO filterModel = new FilterModelDTO();

    filterModel.setClassName(className);
    filterModel.setName("DataStorageAuthorizer");
    filterModel.setFilterClassName(filterClassName);
    filterModel.setId(storage.getId());
    List<FilterParameter> parameters = new ArrayList<FilterParameter>();

    FilterParameter paramAuthorized = new FilterParameter();
    paramAuthorized.setName("authorize");
    paramAuthorized.setDescription("Authorize true|false");
    paramAuthorized.setValue(new Boolean(authorized).toString());
    paramAuthorized.setValueType("xs:boolean");
    paramAuthorized.setType(FilterParameterType.PARAMETER_INTERN);

    parameters.add(paramAuthorized);

    FilterParameter paramLogDir = new FilterParameter();
    paramLogDir.setName("logdir");
    paramLogDir.setDescription("Storage logging directory");
    paramLogDir.setValue("false");
    paramLogDir.setValueType("xs:boolean");
    paramLogDir.setType(FilterParameterType.PARAMETER_INTERN);

    parameters.add(paramLogDir);

    filterModel.setParameters(parameters);

    return filterModel;
  }

  /**
   * Add or modify a given FilterModelDTO
   * 
   * @param storage
   *          the storage where to modify the FilterModelDTO
   * @param filterModel
   *          the FilterModelDTO
   * @param method
   *          the method to perform (POST or PUT)
   */
  private void actOnCustomFilter(StorageDirectory storage, FilterModelDTO filterModel, String method) {

    String url = getAdminDataStorageBaseUrl() + "/" + storage.getId() + "/filter";
    Representation repr = getRepresentationFilterModelDTO(filterModel, getMediaTest());
    if (docAPI.isActive()) {
      Map<String, String> parametersURI = new LinkedHashMap<String, String>();
      parametersURI.put("id", "Datastorage identifier");
      parametersURI.put(method, "A <i>FilterModelDTO</i> object");
      String template = getAdminDataStorageBaseUrl() + "/{id}/filter";
      postDocAPI(url, "", repr, parametersURI, template);
    }
    else {
      ClientResource cr = new ClientResource(url);
      Representation result;
      if ("POST".equals(method)) {
        result = cr.post(repr, getMediaTest());
      }
      else {
        result = cr.put(repr, getMediaTest());
      }
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());
      Response response = getResponse(getMediaTest(), result, FilterModelDTO.class, false);
      assertNotNull(response);
      assertTrue(response.getSuccess());
      assertNotNull(response.getItem());

      FilterModelDTO filterModelOutput = (FilterModelDTO) response.getItem();
      assertEquals(filterModel.getClassName(), filterModelOutput.getClassName());

    }

  }

  /**
   * Retrieve a Custom filter for the given storage
   * 
   * @param storage
   *          the storage
   * @param filter
   *          the filter
   */
  private void retrieveCustomFilter(StorageDirectory storage, FilterModelDTO filter) {
    String url = getAdminDataStorageBaseUrl() + "/" + storage.getId() + "/filter";
    if (docAPI.isActive()) {
      Map<String, String> parametersURI = new LinkedHashMap<String, String>();
      parametersURI.put("id", "Datastorage identifier");
      parametersURI.put("idFilter", "Filter identifier");
      String template = getAdminDataStorageBaseUrl() + "/{id}/filter/{idFilter}";
      retrieveDocAPI(url, "", parametersURI, template);
    }
    else {
      ClientResource cr = new ClientResource(url);
      Representation result;
      result = cr.get(getMediaTest());
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());
      Response response = getResponse(getMediaTest(), result, FilterModelDTO.class, false);
      assertNotNull(response);
      assertTrue(response.getSuccess());
      assertNotNull(response.getItem());

      FilterModelDTO filterModelOutput = (FilterModelDTO) response.getItem();
      assertEquals(filter.getClassName(), filterModelOutput.getClassName());
    }
  }

  /**
   * Delete a FilterModelDTO
   * 
   * @param storage
   *          the storage where the filter is attached
   */
  private void deleteCustomFilter(StorageDirectory storage) {

    String url = getAdminDataStorageBaseUrl() + "/" + storage.getId() + "/filter";
    Representation repr = new StringRepresentation("");
    if (docAPI.isActive()) {
      Map<String, String> parametersURI = new LinkedHashMap<String, String>();
      parametersURI.put("id", "Datastorage identifier");
      String template = getAdminDataStorageBaseUrl() + "/{id}/filter";
      postDocAPI(url, "", repr, parametersURI, template);
    }
    else {
      ClientResource cr = new ClientResource(url);
      Representation result;
      result = cr.delete(getMediaTest());
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());
      Response response = getResponse(getMediaTest(), result, FilterModelDTO.class, false);
      assertNotNull(response);
      assertTrue(response.getSuccess());
    }

  }

  /**
   * Delete the given StorageDirectory
   * 
   * @param storage
   *          the StorageDirectory
   */
  private void delete(StorageDirectory storage) {
    String url = getAdminDataStorageBaseUrl() + "/" + storage.getId();

    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      parameters.put("id", "datastorage identifier");
      String template = getBaseUrl() + "/{id}";
      deleteDocAPI(url, "", parameters, template);
    }
    else {
      ClientResource cr = new ClientResource(url);
      Representation result = cr.delete(getMediaTest());
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());
      Response response = getResponse(getMediaTest(), result, StorageDirectory.class, false);
      assertNotNull(response);
      assertTrue(response.getSuccess());
    }
  }

  /**
   * REST API Response Representation wrapper for single or multiple items expected
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
      if (!media.isCompatible(MediaType.APPLICATION_JSON) && !media.isCompatible(MediaType.APPLICATION_XML)) {
        Engine.getLogger(AbstractSitoolsTestCase.class.getName()).warning("Only JSON or XML supported in tests");
        return null;
      }

      XStream xstream = XStreamFactory.getInstance().getXStreamReader(media);
      configure(xstream);
      // for filterPlugin
      xstream.alias("filterPlugin", FilterModelDTO.class);
      xstream.alias("filterParameter", FilterParameter.class);
      xstream.omitField(ExtensionModel.class, "parametersMap");

      xstream.alias("directory", StorageDirectory.class);
      xstream.alias("item", dataClass);
      xstream.alias("item", Object.class, dataClass);

      if (media.isCompatible(MediaType.APPLICATION_JSON)) {
        xstream.addImplicitCollection(FilterModelDTO.class, "parameters", FilterParameter.class);
      }

      if (isArray) {
        if (media.isCompatible(MediaType.APPLICATION_JSON)) {
          xstream.addImplicitCollection(Response.class, "data", dataClass);
          if (dataClass == FilterModelDTO.class) {
            xstream.addImplicitCollection(FilterModelDTO.class, "parameters", FilterParameter.class);
            xstream.alias("filterPlugin", Object.class, FilterModelDTO.class);
          }
        }
      }
      else {
        if (media.isCompatible(MediaType.APPLICATION_JSON)) {

        }

        if (dataClass == StorageDirectory.class) {
          xstream.aliasField("directory", Response.class, "item");
        }
        if (dataClass == FilterModelDTO.class) {
          xstream.aliasField("filterPlugin", Response.class, "item");
          xstream.alias("filterPlugin", Object.class, FilterModelDTO.class);
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
   * Builds XML or JSON Representation of StorageDirectory for Create and Update methods.
   * 
   * @param item
   *          Dictionary
   * @param media
   *          APPLICATION_XML or APPLICATION_JSON
   * @return XML or JSON Representation
   */
  public static Representation getRepresentation(StorageDirectory item, MediaType media) {
    if (media.equals(MediaType.APPLICATION_JSON)) {
      return new JacksonRepresentation<StorageDirectory>(item);
    }
    else if (media.equals(MediaType.APPLICATION_XML)) {
      XStream xstream = XStreamFactory.getInstance().getXStream(media, false);
      XstreamRepresentation<StorageDirectory> rep = new XstreamRepresentation<StorageDirectory>(media, item);
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
   * Configures XStream mapping of Response object with Dictionary content.
   * 
   * @param xstream
   *          XStream
   */
  private static void configure(XStream xstream) {
    xstream.autodetectAnnotations(false);
    xstream.alias("response", Response.class);

  }

  /**
   * Builds XML or JSON Representation of StorageDirectory for Create and Update methods.
   * 
   * @param item
   *          Dictionary
   * @param media
   *          APPLICATION_XML or APPLICATION_JSON
   * @return XML or JSON Representation
   */
  public static Representation getRepresentationFilterModelDTO(FilterModelDTO item, MediaType media) {
    if (media.equals(MediaType.APPLICATION_JSON)) {
      return new JacksonRepresentation<FilterModelDTO>(item);
    }
    else if (media.equals(MediaType.APPLICATION_XML)) {
      XStream xstream = XStreamFactory.getInstance().getXStream(media, false);
      XstreamRepresentation<FilterModelDTO> rep = new XstreamRepresentation<FilterModelDTO>(media, item);
      rep.setXstream(xstream);
      return rep;
    }
    else {
      Engine.getLogger(AbstractSitoolsTestCase.class.getName()).warning("Only JSON or XML supported in tests");
      return null; // TODO complete test with ObjectRepresentation
    }
  }

}

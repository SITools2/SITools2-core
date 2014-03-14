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
import java.util.HashMap;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.restlet.data.LocalReference;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Reference;
import org.restlet.engine.Engine;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.ext.xstream.XstreamRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;

import com.thoughtworks.xstream.XStream;

import fr.cnes.sitools.common.SitoolsSettings;
import fr.cnes.sitools.common.SitoolsXStreamRepresentation;
import fr.cnes.sitools.common.XStreamFactory;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.server.Consts;
import fr.cnes.sitools.service.storage.model.StorageDirectory;
import fr.cnes.sitools.util.RIAPUtils;

/**
 * Test class for Storage Directory administration and API
 * 
 * @author m.marseille (AKKA Technologies)
 * 
 */
public abstract class AbstractStorageDirectoryTestCase extends AbstractSitoolsServerTestCase {

  /** The initial number of directory before startup */
  private static final int INITIAL_NB_DIRECTORY = 2;

  /** Directory ID for tests */
  private static final String DIRECTORY_ID = "testdir";

  /** Storage directory available for all methods */
  private static StorageDirectory testdir = null;

  @Override
  public String getBaseUrl() {
    return super.getBaseUrl() + SitoolsSettings.getInstance().getString(Consts.APP_DATASTORAGE_ADMIN_URL);
  }

  @Before
  @Override
  public void setUp() throws Exception {
    super.setUp();
  }

  @After
  @Override
  public void tearDown() throws Exception {
    super.tearDown();
  }

  /**
   * Testing CRUD of directories
   */
  @Test
  public void testCRUD() {
    docAPI.setActive(false);
    createDirectory();
    retrieveAllDirectories();
    retrieveSingleDirectory();
    modifyDirectory();
    startDirectory();
    stopDirectory();
    deleteDirectory();
    createWadl(getBaseUrl(), "storage_directory");
  }

  /**
   * Creating doc
   */
  @Test
  public void testCRUD2docAPI() {
    docAPI.setActive(true);
    docAPI.appendChapter("Storage directory administration API");

    docAPI.appendSubChapter("Creating a directory with a POST", "create");

    createDirectory();
    docAPI.appendSubChapter("Retrieve all directory descriptions with GET", "retrieveall");
    retrieveAllDirectories();

    docAPI.appendSubChapter("Retrieve single directory description with GET from its ID", "retrieveall");
    retrieveSingleDirectory();

    docAPI.appendSubChapter("Modify directory settings with PUT", "modify");
    modifyDirectory();

    docAPI.appendSubChapter("Stop directory resource status with GET", "stop");
    HashMap<String, String> params = new HashMap<String, String>();
    params.put("action", "stop");
    docAPI.appendParameters(params);
    stopDirectory();

    docAPI.appendSubChapter("Start directory resource status with GET", "start");
    HashMap<String, String> param = new HashMap<String, String>();
    param.put("action", "start");
    docAPI.appendParameters(params);
    startDirectory();

    docAPI.appendSubChapter("Delete a directory with DELETE", "modify");
    deleteDirectory();

  }

  /**
   * POST Create a directory and attach it
   */
  public void createDirectory() {
    // Initiate the directory object
    testdir = new StorageDirectory();
    testdir.setAttachUrl("/testurl");
    testdir.setDeeplyAccessible(true);
    testdir.setDescription("directory for tests");
    testdir.setId(DIRECTORY_ID);
    testdir.setListingAllowed(true);
    
    Reference storageReference = LocalReference.createFileReference(SitoolsSettings.getInstance().getString("Starter.ROOT_DIRECTORY"));
    String localPath = storageReference.toString();
    if (localPath.endsWith("/")) {
      testdir.setLocalPath(localPath);
    }
    else {
      testdir.setLocalPath(localPath + "/");
    }
    testdir.setModifiable(true);
    testdir.setName("testdir");
    testdir.setPublicUrl(getHostUrl() + testdir.getAttachUrl());

    // Now post it
    ClientResource cr = new ClientResource(getBaseUrl() + "/directories");
    Representation repr = cr.post(getRepresentation(testdir, getMediaTest()), getMediaTest());
    if (docAPI.isActive()) {
      docAPI.appendRequest(Method.POST, cr);
      docAPI.appendResponse(repr);
    }
    else {
      assertNotNull(repr);
      Response response = getResponse(getMediaTest(), repr, StorageDirectory.class);
      assertNotNull(response);
      assertTrue(response.getSuccess());
      StorageDirectory dir = (StorageDirectory) response.getItem();
      testdir.setStatus("STARTED");
      assertEqualDirectories(testdir, dir);
    }
    RIAPUtils.exhaust(repr);
  }

  /**
   * Retrieve the list of all directories
   */
  public void retrieveAllDirectories() {
    ClientResource cr = new ClientResource(getBaseUrl() + "/directories");
    Representation repr = cr.get(getMediaTest());
    if (docAPI.isActive()) {
      docAPI.appendRequest(Method.GET, cr);
      docAPI.appendResponse(repr);
    }
    else {
      assertNotNull(repr);
      Response response = getResponse(getMediaTest(), repr, StorageDirectory.class, true);
      assertNotNull(response);
      assertTrue(response.getSuccess());
      ArrayList<StorageDirectory> dirList = new ArrayList<StorageDirectory>();
      for (Object obj : response.getData()) {
        assertNotNull(obj);
        StorageDirectory dir = (StorageDirectory) obj;
        dirList.add(dir);
      }
      // 2 storages are already defined in the test data, so at this point there are 2 datastorages defined
      assertEquals(INITIAL_NB_DIRECTORY + 1, dirList.size());
      // assertTrue(dirList.size() == 2);
    }
    RIAPUtils.exhaust(repr);
  }

  /**
   * GET to retrieve a single Storage from ID
   */
  public void retrieveSingleDirectory() {
    ClientResource cr = new ClientResource(getBaseUrl() + "/directories/" + DIRECTORY_ID);
    Representation repr = cr.get(getMediaTest());
    if (docAPI.isActive()) {
      docAPI.appendRequest(Method.GET, cr);
      docAPI.appendResponse(repr);
    }
    else {
      assertNotNull(repr);
      Response response = getResponse(getMediaTest(), repr, StorageDirectory.class);
      assertNotNull(response);
      assertTrue(response.getSuccess());
      StorageDirectory dir = (StorageDirectory) response.getItem();
      assertEqualDirectories(testdir, dir);
    }
    RIAPUtils.exhaust(repr);
  }

  /**
   * PUT to modify directory setup
   */
  public void modifyDirectory() {
    ClientResource cr = new ClientResource(getBaseUrl() + "/directories/" + DIRECTORY_ID);
    testdir.setDescription("testdirmodified");
    testdir.setName("modifiedname");
    testdir.setLocalPath(testdir.getLocalPath() + "../");
    Representation repr = cr.put(getRepresentation(testdir, getMediaTest()), getMediaTest());
    if (docAPI.isActive()) {
      docAPI.appendRequest(Method.PUT, cr);
      docAPI.appendResponse(repr);
    }
    else {
      assertNotNull(repr);
      Response response = getResponse(getMediaTest(), repr, StorageDirectory.class);
      assertNotNull(response);
      assertTrue(response.getSuccess());
      StorageDirectory dir = (StorageDirectory) response.getItem();
      // The test directory should be now stopped
      testdir.setStatus("STOPPED");
      assertEqualDirectories(testdir, dir);
    }
    RIAPUtils.exhaust(repr);
  }

  /**
   * DELETE to delete a directory
   */
  public void deleteDirectory() {
    ClientResource cr = new ClientResource(getBaseUrl() + "/directories/" + DIRECTORY_ID);
    Representation repr = cr.delete(getMediaTest());
    if (docAPI.isActive()) {
      docAPI.appendRequest(Method.DELETE, cr);
      docAPI.appendResponse(repr);
    }
    else {
      assertNotNull(repr);
      Response response = getResponse(getMediaTest(), repr, StorageDirectory.class);
      assertNotNull(response);
      assertTrue(response.getSuccess());
    }
    RIAPUtils.exhaust(repr);
  }

  /**
   * Stop a directory with form parameters
   */
  public void stopDirectory() {
    ClientResource cr = new ClientResource(getBaseUrl() + "/directories/" + DIRECTORY_ID);
    cr.getRequest().getResourceRef().addQueryParameter("action", "stop");
    Representation repr = cr.put(getMediaTest(), getMediaTest());
    if (docAPI.isActive()) {
      docAPI.appendRequest(Method.GET, cr);
      docAPI.appendResponse(repr);
    }
    else {
      assertNotNull(repr);
      Response response = getResponse(getMediaTest(), repr, StorageDirectory.class);
      assertNotNull(response);
      assertTrue(response.getSuccess());
      // Now status status should be STOPPED
      StorageDirectory dir = (StorageDirectory) response.getItem();
      testdir.setStatus("STOPPED");
      assertEqualDirectories(testdir, dir);
    }
    RIAPUtils.exhaust(repr);
  }

  /**
   * Start a directory with form parameters
   */
  public void startDirectory() {
    ClientResource cr = new ClientResource(getBaseUrl() + "/directories/" + DIRECTORY_ID);
    cr.getRequest().getResourceRef().addQueryParameter("action", "start");
    Representation repr = cr.put(null, getMediaTest());
    if (docAPI.isActive()) {
      docAPI.appendRequest(Method.GET, cr);
      docAPI.appendResponse(repr);
    }
    else {
      assertNotNull(repr);
      Response response = getResponse(getMediaTest(), repr, StorageDirectory.class);
      assertNotNull(response);
      assertTrue(response.getSuccess());
      // Now status status should be STARTED
      StorageDirectory dir = (StorageDirectory) response.getItem();
      testdir.setStatus("STARTED");
      assertEqualDirectories(testdir, dir);
    }
    RIAPUtils.exhaust(repr);
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
      if (!media.isCompatible(MediaType.APPLICATION_JSON) && !media.isCompatible(MediaType.APPLICATION_XML)) {
        Engine.getLogger(AbstractSitoolsTestCase.class.getName()).warning("Only JSON or XML supported in tests");
        return null;
      }

      XStream xstream = XStreamFactory.getInstance().getXStreamReader(media);
      xstream.autodetectAnnotations(false);
      xstream.alias("response", Response.class);
      xstream.alias("directory", StorageDirectory.class);

      if (isArray && media.isCompatible(MediaType.APPLICATION_JSON)) {
        xstream.addImplicitCollection(Response.class, "data", dataClass);
      }
      xstream.alias("item", dataClass);
      xstream.alias("item", Object.class, dataClass);
      if (dataClass == StorageDirectory.class) {
        xstream.aliasField("directory", Response.class, "item");
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
   * Builds XML or JSON Representation of Role for Create and Update methods.
   * 
   * @param item
   *          Role
   * @param media
   *          APPLICATION_XML or APPLICATION_JSON
   * @return XML or JSON Representation
   */
  public static Representation getRepresentation(StorageDirectory item, MediaType media) {
    if (media.equals(MediaType.APPLICATION_JSON)) {
      return new JsonRepresentation(item);
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
   * Configure XStream mapping of a Response object
   * 
   * @param xstream
   *          XStream
   */
  private static void configure(XStream xstream) {
    xstream.autodetectAnnotations(false);
  }

  /**
   * Test to compare two directories
   * 
   * @param dirtest
   *          the directory of reference
   * @param dir
   *          the directory to test
   */
  private void assertEqualDirectories(StorageDirectory dirtest, StorageDirectory dir) {
    assertEquals(dirtest.getAttachUrl(), dir.getAttachUrl());
    assertEquals(dirtest.getDescription(), dir.getDescription());
    assertEquals(dirtest.getId(), dir.getId());
    assertEquals(dirtest.getLocalPath(), dir.getLocalPath());
    assertEquals(dirtest.getName(), dir.getName());
    assertEquals(dirtest.getPublicUrl(), dir.getPublicUrl());
    assertEquals(dirtest.getStatus(), dir.getStatus());
  }

}

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
import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.restlet.Component;
import org.restlet.Context;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Protocol;
import org.restlet.engine.Engine;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.ext.xstream.XstreamRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;

import com.thoughtworks.xstream.XStream;

import fr.cnes.sitools.common.SitoolsSettings;
import fr.cnes.sitools.common.SitoolsXStreamRepresentation;
import fr.cnes.sitools.common.XStreamFactory;
import fr.cnes.sitools.common.application.ContextAttributes;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.dataset.view.DatasetViewApplication;
import fr.cnes.sitools.dataset.view.DatasetViewStoreXML;
import fr.cnes.sitools.dataset.view.model.DatasetView;
import fr.cnes.sitools.server.Consts;
import fr.cnes.sitools.util.RIAPUtils;

/**
 * Tests for form components API
 * 
 * @author d.arpin (AKKA Technologies)
 * 
 */
public abstract class AbstractDatasetViewTestCase extends AbstractSitoolsTestCase {

  /**
   * static xml store instance for the test
   */
  private static DatasetViewStoreXML store = null;

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
    return super.getBaseUrl() + SitoolsSettings.getInstance().getString(Consts.APP_FORMCOMPONENTS_URL);
  }

  /**
   * relative url for project management REST API
   * 
   * @return url
   */
  protected String getAttachUrl() {
    return SITOOLS_URL + SitoolsSettings.getInstance().getString(Consts.APP_FORMCOMPONENTS_URL);
  }

  /**
   * Absolute path location for authorizations store files
   * 
   * @return path
   */
  protected String getTestRepository() {
    return super.getTestRepository() + SitoolsSettings.getInstance().getString(Consts.APP_FORMCOMPONENTS_STORE_DIR);
  }

  @Before
  @Override
  /**
   * Init and Start a server with ProjectApplication
   * 
   * @throws java.lang.Exception
   */
  public void setUp() throws Exception {

    if (this.component == null) {
      this.component = new Component();
      this.component.getServers().add(Protocol.HTTP, getTestPort());
      this.component.getClients().add(Protocol.HTTP);
      this.component.getClients().add(Protocol.FILE);
      this.component.getClients().add(Protocol.CLAP);

      // Context
      Context ctx = this.component.getContext().createChildContext();
      ctx.getAttributes().put(ContextAttributes.SETTINGS, SitoolsSettings.getInstance());
      
      File storeDirectory = new File(getTestRepository());
      cleanDirectory(storeDirectory);
      if (store == null) {
        store = new DatasetViewStoreXML(storeDirectory, ctx);
      }
      
      ctx.getAttributes().put(ContextAttributes.APP_STORE, store);

      this.component.getDefaultHost().attach(getAttachUrl(), new DatasetViewApplication(ctx));
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
      DatasetView item = createObject();
      create(item);
      modify(item);
      // retrieveAll();
      retrieve(item);
      delete(item);
      assertNone();
      createWadl(getBaseUrl(), "form_components");
    }
    catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

  }

  /**
   * Create an object for tests
   * 
   * @return DatasetView
   */
  public DatasetView createObject() {
    DatasetView item = new DatasetView();
    item.setId("anId");
    item.setName("aName");
    item.setJsObject("AnObject");
    item.setDescription("a description");
    item.setFileUrl("aFile");
    return item;
  }

  /**
   * Invoke Get to check that nobody's in there
   * 
   * @throws IOException
   *           Exception when copying configuration files from TEST to data/TESTS
   * 
   */
  public void assertNone() throws IOException {
    ClientResource cr = new ClientResource(getBaseUrl());

    Representation result = cr.get(getMediaTest());
    assertNotNull(result);
    assertTrue(cr.getStatus().isSuccess());
    if (!docAPI.appendResponse(result)) {
      Response response = getResponse(getMediaTest(), result, DatasetView.class, true);
      assertTrue(response.getSuccess());
      assertEquals(0, response.getTotal().intValue());
    }
    RIAPUtils.exhaust(result);
  }

  /**
   * Invoke POST for creation
   * 
   * @param item
   *          Form Component
   * @throws IOException
   *           Exception when copying configuration files from TEST to data/TESTS
   */
  public void create(DatasetView item) throws IOException {
    Representation rep = getRepresentation(item, getMediaTest());
    ClientResource cr = new ClientResource(getBaseUrl());
    docAPI.appendRequest(Method.POST, cr, rep);

    Representation result = cr.post(rep, getMediaTest());
    assertNotNull(result);
    assertTrue(cr.getStatus().isSuccess());
    if (!docAPI.appendResponse(result)) {
      Response response = getResponse(getMediaTest(), result, DatasetView.class, false);
      assertTrue(response.getSuccess());
      DatasetView prj = (DatasetView) response.getItem();
      assertEquals(prj.getId(), item.getId());
      assertEquals(prj.getName(), item.getName());
      assertEquals(prj.getDescription(), item.getDescription());
      assertEquals(prj.getJsObject(), item.getJsObject());
      assertEquals(prj.getFileUrl(), item.getFileUrl());
    }
    RIAPUtils.exhaust(result);
  }

  /**
   * Invoke PUT for modification
   * 
   * @param item
   *          Form Component
   * @throws IOException
   *           Exception when ... TODO
   */
  public void modify(DatasetView item) throws IOException {
    item.setName("anotherName");
    Representation rep = getRepresentation(item, getMediaTest());
    ClientResource cr = new ClientResource(getBaseUrl() + "/" + item.getId());
    docAPI.appendRequest(Method.PUT, cr, rep);

    Representation result = cr.put(rep, getMediaTest());
    assertNotNull(result);
    assertTrue(cr.getStatus().isSuccess());
    if (!docAPI.appendResponse(result)) {
      Response response = getResponse(getMediaTest(), result, DatasetView.class, false);
      assertTrue(response.getSuccess());
      DatasetView prj = (DatasetView) response.getItem();
      assertEquals(prj.getName(), item.getName());
    }
    RIAPUtils.exhaust(result);
  }

  /**
   * Invoke GET for all components
   * 
   * @throws IOException
   *           Exception when copying configuration files from TEST to data/TESTS
   */
  public void retrieveAll() throws IOException {
    ClientResource cr = new ClientResource(getBaseUrl());
    Representation result = cr.get(getMediaTest());
    assertNotNull(result);
    assertTrue(cr.getStatus().isSuccess());
    if (!docAPI.appendResponse(result)) {
      Response response = getResponse(getMediaTest(), result, DatasetView.class, true);
      assertTrue(response.getSuccess());
    }
    RIAPUtils.exhaust(result);
  }

  /**
   * Invoke GET for one component
   * 
   * @param item
   *          the item to get
   * @throws IOException
   *           Exception when copying configuration files from TEST to data/TESTS
   */
  public void retrieve(DatasetView item) throws IOException {
    ClientResource cr = new ClientResource(getBaseUrl() + "/" + item.getId());
    Representation result = cr.get(getMediaTest());
    assertNotNull(result);
    assertTrue(cr.getStatus().isSuccess());
    if (!docAPI.appendResponse(result)) {
      Response response = getResponse(getMediaTest(), result, DatasetView.class, false);
      assertTrue(response.getSuccess());
      DatasetView prj = (DatasetView) response.getItem();
      assertEquals(prj.getId(), item.getId());
      assertEquals(prj.getName(), item.getName());
      assertEquals(prj.getFileUrl(), item.getFileUrl());
      assertEquals(prj.getDescription(), item.getDescription());
      assertEquals(prj.getJsObject(), item.getJsObject());
    }
    RIAPUtils.exhaust(result);
  }

  /**
   * Invoke DELETE for one component
   * 
   * @param item
   *          the item to delete
   * @throws IOException
   *           Exception when copying configuration files from TEST to data/TESTS
   */
  public void delete(DatasetView item) throws IOException {
    ClientResource cr = new ClientResource(getBaseUrl() + "/" + item.getId());
    Representation result = cr.delete(getMediaTest());
    assertNotNull(result);
    assertTrue(cr.getStatus().isSuccess());
    RIAPUtils.exhaust(result);
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
      xstream.alias("datasetView", DatasetView.class);

      // Parce que les annotations ne sont apparemment prises en compte
      xstream.omitField(Response.class, "itemName");
      xstream.omitField(Response.class, "itemClass");

      if (isArray) {
        xstream.addImplicitCollection(Response.class, "data", dataClass);
      }
      else {
        xstream.alias("item", dataClass);
        xstream.alias("item", Object.class, dataClass);
      }
      xstream.aliasField("datasetView", Response.class, "item");

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
  public static Representation getRepresentation(DatasetView item, MediaType media) {
    if (media.equals(MediaType.APPLICATION_JSON)) {
      return new JsonRepresentation(item);
    }
    else if (media.equals(MediaType.APPLICATION_XML)) {
      XStream xstream = XStreamFactory.getInstance().getXStream(media, false);
      XstreamRepresentation<DatasetView> rep = new XstreamRepresentation<DatasetView>(media, item);
      configure(xstream);
      rep.setXstream(xstream);
      return rep;
    }
    else {
      Engine.getLogger(DatasetView.class.getName()).warning("Only JSON or XML supported in tests");
      return null; // TODO complete test with ObjectRepresentation
    }
  }

  /**
   * Configure Xstream for XML
   * 
   * @param xstream
   *          the xstream to configure
   */
  public static void configure(XStream xstream) {
    xstream.autodetectAnnotations(false);
    xstream.alias("response", Response.class);

    // Parce que les annotations ne sont apparemment prises en compte
    xstream.omitField(Response.class, "itemName");
    xstream.omitField(Response.class, "itemClass");
  }

}

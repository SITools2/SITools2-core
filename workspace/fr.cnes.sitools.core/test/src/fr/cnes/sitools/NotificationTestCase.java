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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.restlet.Component;
import org.restlet.Context;
import org.restlet.data.MediaType;
import org.restlet.data.Protocol;
import org.restlet.engine.Engine;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;

import com.thoughtworks.xstream.XStream;

import fr.cnes.sitools.common.SitoolsSettings;
import fr.cnes.sitools.common.SitoolsXStreamRepresentation;
import fr.cnes.sitools.common.XStreamFactory;
import fr.cnes.sitools.common.application.ContextAttributes;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.notification.NotificationApplication;
import fr.cnes.sitools.notification.model.RestletObservable;
import fr.cnes.sitools.notification.model.RestletObserver;
import fr.cnes.sitools.notification.store.NotificationStoreXML;
import fr.cnes.sitools.server.Consts;
import fr.cnes.sitools.util.RIAPUtils;

/**
 * TestCase Notification
 * 
 * @author jp.boignard (AKKA Technologies)
 */
public class NotificationTestCase extends AbstractSitoolsTestCase {
  /**
   * static xml store instance for the test
   */
  private static NotificationStoreXML store = null;

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
    return super.getBaseUrl() + SitoolsSettings.getInstance().getString(Consts.APP_NOTIFICATIONS_URL);
  }

  /**
   * relative url for project management REST API
   * 
   * @return url
   */
  protected String getAttachUrl() {
    return SITOOLS_URL + SitoolsSettings.getInstance().getString(Consts.APP_NOTIFICATIONS_URL);
  }

  /**
   * Absolute path location for inscription store files
   * 
   * @return path
   */
  protected String getTestRepository() {
    return super.getTestRepository() + SitoolsSettings.getInstance().getString(Consts.APP_NOTIFICATIONS_STORE_DIR);
  }

  @Before
  @Override
  /**
   * Create component, store and application and start server
   * @throws java.lang.Exception
   */
  public void setUp() throws Exception {
    

    if (this.component == null) {
      this.component = new Component();
      this.component.getServers().add(Protocol.HTTP, getTestPort());
      this.component.getClients().add(Protocol.HTTP);
      this.component.getClients().add(Protocol.FILE);
      this.component.getClients().add(Protocol.CLAP);
      this.component.getClients().add(Protocol.RIAP);

      SitoolsSettings settings = SitoolsSettings.getInstance();

      // ===================================
      // RESOURCES NOTIFICATIONS APPLICATION

      // Context
      Context appContext = this.component.getContext().createChildContext();
      appContext.getAttributes().put(ContextAttributes.SETTINGS, settings);
      
      if (store == null) {
        File storeDirectory = new File(getTestRepository());
        cleanDirectory(storeDirectory);
        store = new NotificationStoreXML(storeDirectory, appContext);
      }
      
      appContext.getAttributes().put(ContextAttributes.APP_ATTACH_REF, getAttachUrl());
      appContext.getAttributes().put(ContextAttributes.APP_REGISTER, false);
      appContext.getAttributes().put(ContextAttributes.APP_STORE, store);

      // Application
      NotificationApplication notificationApplication = new NotificationApplication(appContext);

      // Attachment
      this.component.getDefaultHost().attach(getAttachUrl(), notificationApplication);

      component.getInternalRouter()
          .attach(settings.getString(Consts.APP_NOTIFICATIONS_URL), notificationApplication);

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

    try {
      this.component.stop();
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    finally {
      this.component = null;
    }
  }

  /**
   * Scenario de test du mécanisme Observer Observable REST.
   * 
   */
  @Test
  public void testScenarioComplet() {

    // // CREATE OBSERVABLE
    // RestletObservable observable = createObservable(1);
    // ClientResource cr = new ClientResource(this.getBaseUrl() + "/notifications/observables" );
    // Representation result = cr.post(observable,MediaType.APPLICATION_JSON));
    // assertTrue(cr.getStatus().isSuccess());
    // assertNotNull(result);
    //
    // Response response = getResponse(MediaType.APPLICATION_JSON, result, RestletObservable.class, true);
    // assertNotNull(response);
    // LOGGER.info(response.toString());
    // assertTrue(response.getSuccess());
    //
    // // CREATE OBSERVER
    // RestletObserver observer = createObserver(1);
    // ClientResource crr = new ClientResource(this.getBaseUrl() + "/notifications/observers/" );
    // Representation resultr = crr.post(MediaType.APPLICATION_JSON, observer);
    // assertTrue(crr.getStatus().isSuccess());
    // assertNotNull(resultr);
    //
    // Response responser = getResponse(MediaType.APPLICATION_JSON, resultr, RestletObserver.class, true);
    // assertNotNull(responser);
    // LOGGER.info(responser.toString());
    // assertTrue(responser.getSuccess());
    //
    // // DELETE OBSERVER
    // ClientResource crd = new ClientResource(this.getBaseUrl() + "/notifications/observers/" );
    // Representation resultd = crr.delete(MediaType.APPLICATION_JSON);
    // assertTrue(crd.getStatus().isSuccess());
    // assertNotNull(resultd);
    //
    // // DELETE OBSERVABLE
    // ClientResource cra = new ClientResource(this.getBaseUrl() + "/notifications/observables/" + observable.getUri()
    // );
    // Representation resulta = crr.delete(MediaType.APPLICATION_JSON);
    // assertTrue(cra.getStatus().isSuccess());
    // assertNotNull(resulta);

    // assertNone();

    // getObservables();

    // observableCreate();
    //
    // getObservers();
    //
    // observableDelete();

    // assertNone();
  }

  /**
   * Creates a new RestletObservable object
   * 
   * @param no
   *          sequence used in the object id
   * @return RestletObservable
   */
  public RestletObservable createObservable(int no) {
    RestletObservable oa = new RestletObservable();
    oa.setUri("observable_test_" + no);
    return oa;
  }

  /**
   * Creates a new RestletObserver object
   * 
   * @param no
   *          sequence used in the object id
   * @return RestletObserver
   */
  public RestletObserver createObserver(int no) {
    RestletObserver oe = new RestletObserver();
    oe.setUuid("observer_test_" + no);
    return oe;
  }

  /**
   * Invoke GET
   */
  public void getObservables() {
    ClientResource cr = new ClientResource(this.getBaseUrl() + "/notifications/observables");
    Representation result = cr.get(MediaType.APPLICATION_JSON);
    assertTrue(cr.getStatus().isSuccess());
    assertNotNull(result);

    Response response = getResponse(MediaType.APPLICATION_JSON, result, RestletObservable.class, true);
    assertNotNull(response);
    LOGGER.info(response.toString());
    assertTrue(response.getSuccess());

    // un tableau est attendu dans la reponse
    assertNull(response.getItem());
    assertNotNull(response.getData());
  }

  /**
   * Invoke GET
   * 
   * @param observableId
   *          String
   */
  public void getObservers(String observableId) {
    ClientResource cr = new ClientResource(this.getBaseUrl() + "/observables/" + observableId + "/observers");
    Representation result = cr.get(MediaType.APPLICATION_JSON);
    assertTrue(cr.getStatus().isSuccess());
    assertNotNull(result);

    Response response = getResponse(MediaType.APPLICATION_JSON, result, RestletObserver.class, true);
    assertNotNull(response);
    LOGGER.info(response.toString());
    assertTrue(response.getSuccess());

    // un tableau est attendu dans la reponse
    assertNull(response.getItem());
    assertNotNull(response.getData());
  }

  /**
   * Invokes GET and asserts result response is an empty array.
   */
  public void assertNone() {
    ClientResource cr = new ClientResource(this.getBaseUrl() + "/observables");
    Representation result = cr.get(MediaType.APPLICATION_JSON);
    assertNotNull(result);
    assertTrue(cr.getStatus().isSuccess());

    Response response = getResponse(MediaType.APPLICATION_JSON, result, RestletObservable.class, true);
    assertNotNull(response);
    LOGGER.info(response.toString());
    assertTrue(response.getSuccess());
    assertNull(response.getItem());
    assertEquals(0, response.getTotal().intValue());
  }

  /**
   * Invoke POST
   * 
   * @param item
   *          Observable
   */
  public void observableCreate(RestletObservable item) {
    Representation rep = new JsonRepresentation(item);
    ClientResource cr = new ClientResource(this.getBaseUrl());
    Representation result = cr.post(rep, MediaType.APPLICATION_JSON);
    assertTrue(cr.getStatus().isSuccess());
    assertNotNull(result);

    Response response = getResponse(MediaType.APPLICATION_JSON, result, RestletObservable.class);
    assertNotNull(response);
    LOGGER.info(response.toString());
    assertTrue(response.getSuccess());
    assertNotNull(response.getItem());
  }

  /**
   * Invoke DELETE assert true if inscription exist and param "exists" is true. assert true if inscription doesnt exist
   * and param "exists" is false.
   * 
   * @param item
   *          Observable
   * @param exists
   *          boolean (true if item should exist before delete.)
   */
  public void observableDelete(RestletObservable item, boolean exists) {
    ClientResource cr = new ClientResource(this.getBaseUrl() + "/" + item.getUri());
    Representation result = cr.delete(MediaType.APPLICATION_JSON);
    assertNotNull(result);
    assertTrue(cr.getStatus().isSuccess());

    Response response = getResponse(MediaType.APPLICATION_JSON, result, Response.class);
    assertNotNull(response);
    LOGGER.info(response.toString());
    assertEquals(response.getSuccess().booleanValue(), exists);
  }

  // ------------------------------------------------------------
  // RESPONSE REPRESENTATION

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
      xstream.alias("inscription", RestletObservable.class);

      if (isArray) {
        xstream.addImplicitCollection(Response.class, "data", dataClass);
      }
      else {
        xstream.alias("item", dataClass);
        xstream.alias("item", Object.class, dataClass);

        if (dataClass == RestletObservable.class) {
          xstream.aliasField("resource", Response.class, "item");
        }
      }
      xstream.aliasField("data", Response.class, "data");

      SitoolsXStreamRepresentation<Response> rep = new SitoolsXStreamRepresentation<Response>(representation);
      rep.setXstream(xstream);

      if (media.isCompatible(MediaType.APPLICATION_JSON)) {
        Response response = rep.getObject("response");
        // Response response = rep.getObject();

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
}

/*******************************************************************************
 * Copyright 2011, 2012 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.junit.Test;
import org.restlet.Context;
import org.restlet.data.MediaType;
import org.restlet.ext.xstream.XstreamRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;

import com.thoughtworks.xstream.XStream;

import fr.cnes.sitools.common.SitoolsSettings;
import fr.cnes.sitools.common.SitoolsXStreamRepresentation;
import fr.cnes.sitools.common.XStreamFactory;
import fr.cnes.sitools.common.application.ContextAttributes;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.notification.model.RestletObservable;
import fr.cnes.sitools.notification.model.RestletObserver;
import fr.cnes.sitools.notification.store.NotificationStore;
import fr.cnes.sitools.notification.store.NotificationStoreXML;
import fr.cnes.sitools.server.Consts;
import fr.cnes.sitools.util.RIAPUtils;

/**
 * Class to test the notification API
 * 
 * @author m.marseille (AKKA Technologies)
 */
public abstract class AbstractNotificationAPITestCase extends AbstractSitoolsServerTestCase {

  /**
   * UUID of the observable
   */
  private static final String OBSERVABLE_UUID = "bf77955a-2cec-4fc3-b95d-7397025fb299";

  /**
   * absolute url for role management REST API
   * 
   * @return url
   */
  protected String getBaseUrl() {
    return super.getBaseUrl() + settings.getString(Consts.APP_NOTIFICATIONS_URL);
  }

  /**
   * Unit Test
   */
  @Test
  public void testNotifications() {
    docAPI.setActive(false);
    RestletObservable observable = createObservable();
    createObservable(observable);
    deleteObservable(observable);
    createWadl(getBaseUrl(), "notifications");
  }

  /**
   * Invoke post to create an observable
   * 
   * @param observable
   *          the observable to create
   */
  private void createObservable(RestletObservable observable) {
    Representation rep = getRepresentation(observable, getMediaTest());
    ClientResource cr = new ClientResource(getBaseUrl() + "/observables/" + OBSERVABLE_UUID);
    Representation result = cr.post(rep, getMediaTest());
    assertNotNull(result);
    assertTrue(cr.getStatus().isSuccess());
    Response response = getResponse(getMediaTest(), result, Response.class, false);
    assertTrue(response.getSuccess());
    assertTrue("observable.added".equals(response.getMessage()));
    RIAPUtils.exhaust(result);
  }

  /**
   * Invoke post to create an observable
   * 
   * @param observable
   *          the observable to create
   */
  private void deleteObservable(RestletObservable observable) {
    ClientResource cr = new ClientResource(getBaseUrl() + "/observables/" + OBSERVABLE_UUID);
    Representation result = cr.delete(getMediaTest());
    assertNotNull(result);
    assertTrue(cr.getStatus().isSuccess());
    Response response = getResponse(getMediaTest(), result, Response.class, false);
    assertTrue(response.getSuccess());
    assertTrue("observable.deleted".equals(response.getMessage()));
    RIAPUtils.exhaust(result);
  }

  /**
   * Create an observable
   * 
   * @return the observable created
   */
  private RestletObservable createObservable() {
    RestletObservable observable = new RestletObservable();
    List<RestletObserver> observers = new ArrayList<RestletObserver>();
    Context ctx = new Context();
    ctx.getAttributes().put(ContextAttributes.SETTINGS, SitoolsSettings.getInstance());
    NotificationStore store = new NotificationStoreXML(ctx);
    observable.setStore(store);
    observable.setObservers(observers);
    observable.setUri("sitools/test");
    return observable;
  }

  /**
   * Response to Representation
   * 
   * @param observable
   *          the observable
   * @param media
   *          MediaType
   * @return Representation
   */
  public static Representation getRepresentation(RestletObservable observable, MediaType media) {
    if (media.equals(MediaType.APPLICATION_JSON) || media.equals(MediaType.APPLICATION_XML)) {
      XStream xstream = XStreamFactory.getInstance().getXStream(media);
      configure(xstream);
      XstreamRepresentation<RestletObservable> rep = new XstreamRepresentation<RestletObservable>(media, observable);
      rep.setXstream(xstream);
      return rep;
    }
    else {
      Logger.getLogger(AbstractSitoolsTestCase.class.getName()).warning("Only JSON or XML supported in tests");
      return null;
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
    xstream.alias("response", Response.class);
    xstream.omitField(RestletObservable.class, "store");
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
  private static Response getResponse(MediaType media, Representation representation, Class<?> dataClass,
      boolean isArray) {
    try {
      if (!media.isCompatible(MediaType.APPLICATION_JSON) && !media.isCompatible(MediaType.APPLICATION_XML)) {
        Logger.getLogger(AbstractSitoolsTestCase.class.getName()).warning("Only JSON or XML supported in tests");
        return null;
      }

      XStream xstream = XStreamFactory.getInstance().getXStreamReader(media);
      xstream.autodetectAnnotations(false);
      xstream.alias("response", Response.class);

      xstream.aliasField("data", Response.class, "data");

      SitoolsXStreamRepresentation<Response> rep = new SitoolsXStreamRepresentation<Response>(representation);
      rep.setXstream(xstream);

      if (media.isCompatible(getMediaTest())) {
        Response response = rep.getObject("response");

        return response;
      }
      else {
        Logger.getLogger(AbstractSitoolsTestCase.class.getName()).warning("Only JSON or XML supported in tests");
        return null;
        // TODO complete test with ObjectRepresentation
      }
    }
    finally {
      RIAPUtils.exhaust(representation);
    }
  }

}

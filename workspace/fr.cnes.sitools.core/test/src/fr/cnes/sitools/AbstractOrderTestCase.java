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
import java.util.ArrayList;
import java.util.Date;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.restlet.Component;
import org.restlet.Context;
import org.restlet.data.ClientInfo;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Protocol;
import org.restlet.engine.Engine;
import org.restlet.ext.xstream.XstreamRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;
import org.restlet.security.User;

import com.thoughtworks.xstream.XStream;

import fr.cnes.sitools.common.SitoolsSettings;
import fr.cnes.sitools.common.SitoolsXStreamRepresentation;
import fr.cnes.sitools.common.XStreamFactory;
import fr.cnes.sitools.common.application.ContextAttributes;
import fr.cnes.sitools.common.model.Event;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.feeds.model.SitoolsFeedDateConverter;
import fr.cnes.sitools.order.OrderAdministration;
import fr.cnes.sitools.order.OrderStoreXML;
import fr.cnes.sitools.order.UserOrderApplication;
import fr.cnes.sitools.order.model.Order;
import fr.cnes.sitools.server.Consts;
import fr.cnes.sitools.util.RIAPUtils;

/**
 * Test du CRUD des commandes côté user.
 * 
 * @author M Marseille
 * 
 */
public abstract class AbstractOrderTestCase extends AbstractSitoolsTestCase {

  /**
   * static xml store instance for the test
   */
  private static OrderStoreXML store = null;

  /**
   * Restlet Component for server. One for admin, one for user
   */
  private Component component = null;

  /**
   * absolute url for order user REST API
   * 
   * @return url
   */
  protected String getBaseUrl() {
    return super.getBaseUrl() + SitoolsSettings.getInstance().getString(Consts.APP_ORDERS_USER_URL);
  }

  /**
   * absolute URL for admin view of orders
   * 
   * @return url
   */
  protected String getBaseAdminUrl() {
    return super.getBaseUrl() + SitoolsSettings.getInstance().getString(Consts.APP_ORDERS_ADMIN_URL);
  }

  /**
   * relative url for role management REST API - user side
   * 
   * @return url
   */
  protected String getAttachUrl() {
    return SITOOLS_URL + SitoolsSettings.getInstance().getString(Consts.APP_ORDERS_USER_URL);
  }

  /**
   * relative url for role management REST API - admin side
   * 
   * @return url
   */
  protected String getAttachAdminUrl() {
    return SITOOLS_URL + SitoolsSettings.getInstance().getString(Consts.APP_ORDERS_ADMIN_URL);
  }

  /**
   * Absolute path location for order store files
   * 
   * @return path
   */
  protected String getTestRepository() {
    return super.getTestRepository() + SitoolsSettings.getInstance().getString(Consts.APP_ORDERS_STORE_DIR);
  }

  @Before
  @Override
  /**
   * Init and Start a server with OrderApplication
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

      if (store == null) {
        File storeDirectory = new File(getTestRepository());
        cleanDirectory(storeDirectory);
        store = new OrderStoreXML(storeDirectory, ctx);
      }
      
      ctx.getAttributes().put(ContextAttributes.APP_STORE, store);

      this.component.getDefaultHost().attach(getAttachAdminUrl(), new OrderAdministration(ctx));
      this.component.getDefaultHost().attach(getAttachUrl(), new UserOrderApplication(ctx));
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
    if (this.component != null) {
      if (this.component.isStarted()) {
        this.component.stop();
        this.component = null;
      }
    }
  }

  /**
   * Test CRUD Role with JSon format exchanges.
   */
  @Test
  public void testCRUD() {
    User user = createUser();
    Order item = createObject("1000000");
    Order item2 = createObject("1100000");
    Order item3 = createObject("1010000");
    assertNone(user);
    // Create orders
    adminCreateOrder(item);
    adminCreateOrderWithUserAPI(user, item2);
    userCreateOrder(user, item3);
    // Get orders
    adminGetOrders();
    adminGetOrdersWithUserAPI(user);
    adminGetOrderById(item);
    adminGetOrderByIdWithUserAPI(user, item);
    // Modify orders
    item.setDescription("New description");
    item2.setDescription("Other new description");
    adminUpdateOrder(item);
    adminUpdateOrderWithUserAPI(user, item);
    // Actions
    adminActsOn(item, "TREATED");
    adminActsOnWithUserAPI(user, item2, "TREATED");
    adminDeleteOrders(user);
    assertNone(user);
    createWadl(getBaseAdminUrl(), "order_admin");
    createWadl(getBaseUrl(), "order_user");
  }

  /**
   * Create a restlet user
   * 
   * @return a restlet user
   */
  public User createUser() {
    User out = new User("admin", "admin");
    return out;
  }

  /**
   * Create an order
   * 
   * @param id
   *          order ID
   * @return the order itself
   */
  public Order createObject(String id) {
    Order item = new Order();
    item.setId(id);
    item.setDescription("test");
    item.setStatus("NEW");
    Date date = new Date(System.currentTimeMillis());
    item.setDateOrder(date);
    Event e = new Event();
    e.setAuthor("me");
    e.setDescription("an event");
    e.setMessage("youki");
    e.setNotify(false);
    ArrayList<Event> le = new ArrayList<Event>();
    le.add(e);
    item.setEvents(le);
    return item;
  }

  /**
   * Invoke GET to be sure that nobody's there
   * 
   * @param us
   *          User
   */
  public void assertNone(User us) {
    ClientResource cr = new ClientResource(getBaseAdminUrl());
    ClientInfo clientInfo = new ClientInfo(getMediaTest());
    clientInfo.setUser(us);
    cr.setClientInfo(clientInfo);
    docAPI.appendRequest(Method.GET, cr);

    Representation result = cr.get(getMediaTest());
    if (!docAPI.appendResponse(result)) {
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());

      Response response = getResponse(getMediaTest(), result, Order.class);
      assertTrue(response.getSuccess());
      assertEquals(response.getTotal().intValue(), 0);
    }
    RIAPUtils.exhaust(result);
  }

  /**
   * Invokes POST method to add a an order
   * 
   * @param item
   *          Order
   */
  public void adminCreateOrder(Order item) {
    Representation rep = getRepresentation(item, getMediaTest());
    ClientResource cr = new ClientResource(getBaseAdminUrl());
    docAPI.appendRequest(Method.POST, cr, rep);
    Representation result = cr.post(rep, getMediaTest());
    if (!docAPI.appendResponse(result)) {
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());

      Response response = getResponse(getMediaTest(), result, Order.class);
      assertTrue(response.getSuccess());
      Order prj = (Order) response.getItem();
      assertEquals(prj.getDescription(), item.getDescription());
      assertEquals(prj.getId(), item.getId());
      assertEquals(prj.getStatus(), item.getStatus());
    }
    RIAPUtils.exhaust(result);
  }

  /**
   * Invokes POST method to add a an order
   * 
   * @param us
   *          User
   * @param item
   *          Order
   */
  public void adminCreateOrderWithUserAPI(User us, Order item) {
    Representation rep = getRepresentation(item, getMediaTest());
    ClientResource cr = new ClientResource(getBaseAdminUrl() + "/users/" + us.getIdentifier());
    docAPI.appendRequest(Method.POST, cr, rep);
    Representation result = cr.post(rep, getMediaTest());
    if (!docAPI.appendResponse(result)) {
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());

      Response response = getResponse(getMediaTest(), result, Order.class);
      assertTrue(response.getSuccess());
      Order prj = (Order) response.getItem();
      assertEquals(prj.getDescription(), item.getDescription());
      assertEquals(prj.getId(), item.getId());
      assertEquals(prj.getStatus(), item.getStatus());
    }
    RIAPUtils.exhaust(result);
  }

  /**
   * Invokes POST method to add a an order
   * 
   * @param us
   *          User
   * @param item
   *          Order
   */
  public void adminGetOrdersByIdWithUserAPI(User us, Order item) {
    Representation rep = getRepresentation(item, getMediaTest());
    ClientResource cr = new ClientResource(getBaseAdminUrl() + "/users/" + us.getIdentifier());
    docAPI.appendRequest(Method.POST, cr, rep);
    Representation result = cr.post(rep, getMediaTest());
    if (!docAPI.appendResponse(result)) {
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());

      Response response = getResponse(getMediaTest(), result, Order.class);
      assertTrue(response.getSuccess());
      Order prj = (Order) response.getItem();
      assertEquals(prj.getDescription(), item.getDescription());
      assertEquals(prj.getId(), item.getId());
      assertEquals(prj.getStatus(), item.getStatus());
    }
    RIAPUtils.exhaust(result);
  }

  /**
   * Invokes POST method to add a an order
   * 
   * @param us
   *          User
   * @param item
   *          Order
   */
  public void userCreateOrder(User us, Order item) {
    Representation rep = getRepresentation(item, getMediaTest());
    ClientResource cr = new ClientResource(getBaseUrl());
    docAPI.appendRequest(Method.POST, cr, rep);
    Representation result = cr.post(rep, getMediaTest());
    if (!docAPI.appendResponse(result)) {
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());

      Response response = getResponse(getMediaTest(), result, Order.class);
      assertTrue(response.getSuccess());
      Order prj = (Order) response.getItem();
      assertEquals(prj.getDescription(), item.getDescription());
      assertEquals(prj.getId(), item.getId());
      assertEquals(prj.getStatus(), item.getStatus());
    }
    RIAPUtils.exhaust(result);
  }

  /**
   * Invoke GET on the admin side to have the list of orders
   */
  public void adminGetOrders() {
    ClientResource cr = new ClientResource(getBaseAdminUrl());
    docAPI.appendRequest(Method.GET, cr);
    Representation result = cr.get(getMediaTest());
    if (!docAPI.appendResponse(result)) {

      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());

      Response response = getResponse(getMediaTest(), result, Order.class, true);
      assertTrue(response.getSuccess());
      assertEquals(3, response.getTotal().intValue());
    }
    RIAPUtils.exhaust(result);
  }

  /**
   * Invoke GET on the admin side to have the list of orders
   * 
   * @param us
   *          the user
   */
  public void adminGetOrdersWithUserAPI(User us) {
    ClientResource cr = new ClientResource(getBaseAdminUrl() + "/users/" + us.getIdentifier());
    docAPI.appendRequest(Method.GET, cr);
    Representation result = cr.get(getMediaTest());
    if (!docAPI.appendResponse(result)) {

      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());

      Response response = getResponse(getMediaTest(), result, Order.class, true);
      assertTrue(response.getSuccess());
      assertEquals(3, response.getTotal().intValue());
    }
    RIAPUtils.exhaust(result);
  }

  /**
   * Invoke GET with a specific ID to obtain a particular order
   * 
   * @param item
   *          the order to check
   */
  public void adminGetOrderById(Order item) {
    ClientResource cr = new ClientResource(getBaseAdminUrl() + "/" + item.getId());
    docAPI.appendRequest(Method.GET, cr);
    Representation result = cr.get(getMediaTest());
    if (!docAPI.appendResponse(result)) {
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());

      Response response = getResponse(getMediaTest(), result, Order.class);
      assertTrue(response.getSuccess());
      Order ord = (Order) response.getItem();
      // assertEquals(ord.getDateOrder(), item.getDateOrder());
      assertEquals(ord.getDescription(), item.getDescription());
      assertEquals(ord.getStatus(), item.getStatus());
      assertEquals(ord.getUserId(), item.getUserId());
      assertEquals(ord.getResourceDescriptor(), item.getResourceDescriptor());
      for (int i = 0; i < item.getEvents().size(); i++) {
        Event ordere = ord.getEvents().get(i);
        Event iteme = item.getEvents().get(i);
        assertEquals(ordere.getAuthor(), iteme.getAuthor());
        assertEquals(ordere.getDescription(), iteme.getDescription());
        assertEquals(ordere.getMessage(), iteme.getMessage());
      }
    }
    RIAPUtils.exhaust(result);
  }

  /**
   * Invoke GET with a specific ID to obtain a particular order
   * 
   * @param us
   *          the user
   * @param item
   *          the order to check
   */
  public void adminGetOrderByIdWithUserAPI(User us, Order item) {
    ClientResource cr = new ClientResource(getBaseAdminUrl() + "/users/" + us.getIdentifier() + "/" + item.getId());
    docAPI.appendRequest(Method.GET, cr);
    Representation result = cr.get(getMediaTest());
    if (!docAPI.appendResponse(result)) {

      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());

      Response response = getResponse(getMediaTest(), result, Order.class, false);
      assertTrue(response.getSuccess());
      Order ord = (Order) response.getItem();
      // assertEquals(ord.getDateOrder(), item.getDateOrder());
      assertEquals(ord.getDescription(), item.getDescription());
      assertEquals(ord.getStatus(), item.getStatus());
      assertEquals(ord.getUserId(), item.getUserId());
      assertEquals(ord.getResourceDescriptor(), item.getResourceDescriptor());
      for (int i = 0; i < item.getEvents().size(); i++) {
        Event ordere = ord.getEvents().get(i);
        Event iteme = item.getEvents().get(i);
        assertEquals(ordere.getAuthor(), iteme.getAuthor());
        assertEquals(ordere.getDescription(), iteme.getDescription());
        assertEquals(ordere.getMessage(), iteme.getMessage());
      }
    }
    RIAPUtils.exhaust(result);
  }

  /**
   * Invoke PUT
   * 
   * @param item
   *          the order
   */
  public void adminUpdateOrder(Order item) {
    Representation rep = getRepresentation(item, getMediaTest());
    ClientResource cr = new ClientResource(getBaseAdminUrl() + "/" + item.getId());
    docAPI.appendRequest(Method.PUT, cr);
    Representation result = cr.put(rep, getMediaTest());
    if (!docAPI.appendResponse(result)) {

      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());

      Response response = getResponse(getMediaTest(), result, Order.class, false);
      assertTrue(response.getSuccess());
      Order ord = (Order) response.getItem();
      // assertEquals(ord.getDateOrder(), item.getDateOrder());
      assertEquals(ord.getDescription(), item.getDescription());
      assertEquals(ord.getStatus(), item.getStatus());
      assertEquals(ord.getUserId(), item.getUserId());
      assertEquals(ord.getResourceDescriptor(), item.getResourceDescriptor());
      for (int i = 0; i < item.getEvents().size(); i++) {
        Event ordere = ord.getEvents().get(i);
        Event iteme = item.getEvents().get(i);
        assertEquals(ordere.getAuthor(), iteme.getAuthor());
        assertEquals(ordere.getDescription(), iteme.getDescription());
        assertEquals(ordere.getMessage(), iteme.getMessage());
      }
    }
    RIAPUtils.exhaust(result);
  }

  /**
   * Invoke PUT
   * 
   * @param item
   *          the order
   * @param us
   *          the user
   */
  public void adminUpdateOrderWithUserAPI(User us, Order item) {
    Representation rep = getRepresentation(item, getMediaTest());
    ClientResource cr = new ClientResource(getBaseAdminUrl() + "/users/" + us.getIdentifier() + "/" + item.getId());
    docAPI.appendRequest(Method.PUT, cr);
    Representation result = cr.put(rep, getMediaTest());
    if (!docAPI.appendResponse(result)) {

      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());

      Response response = getResponse(getMediaTest(), result, Order.class, false);
      assertTrue(response.getSuccess());
      Order ord = (Order) response.getItem();
      // assertEquals(ord.getDateOrder(), item.getDateOrder());
      assertEquals(ord.getDescription(), item.getDescription());
      assertEquals(ord.getStatus(), item.getStatus());
      assertEquals(ord.getUserId(), item.getUserId());
      assertEquals(ord.getResourceDescriptor(), item.getResourceDescriptor());
      for (int i = 0; i < item.getEvents().size(); i++) {
        Event ordere = ord.getEvents().get(i);
        Event iteme = item.getEvents().get(i);
        assertEquals(ordere.getAuthor(), iteme.getAuthor());
        assertEquals(ordere.getDescription(), iteme.getDescription());
        assertEquals(ordere.getMessage(), iteme.getMessage());
      }
    }
    RIAPUtils.exhaust(result);
  }

  /**
   * Invoke PUT on admin side to set the status up
   * 
   * @param item
   *          the order the admin acts on
   * @param act
   *          the new status
   */
  public void adminActsOn(Order item, String act) {
    ClientResource cr = new ClientResource(getBaseAdminUrl() + "/" + item.getId() + "/" + act);
    Event event = new Event();
    event.setDescription(act);
    event.setMessage(act);
    Representation rep = getEventRepresentation(event, getMediaTest());

    docAPI.appendRequest(Method.PUT, cr);
    Representation result = cr.put(rep, getMediaTest());
    if (!docAPI.appendResponse(result)) {
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());

      Response response = getResponse(getMediaTest(), result, Order.class);
      assertTrue(response.getSuccess());
      Order ord = (Order) response.getItem();
      assertEquals(ord.getStatus(), act);
    }
    RIAPUtils.exhaust(result);
  }

  /**
   * Invoke PUT on admin side to set the status up
   * 
   * @param us
   *          the user
   * @param item
   *          the order the admin acts on
   * @param act
   *          the new status
   */
  public void adminActsOnWithUserAPI(User us, Order item, String act) {
    ClientResource cr = new ClientResource(getBaseAdminUrl() + "/users/" + us.getIdentifier() + "/" + item.getId()
        + "/" + act);
    Event event = new Event();
    event.setDescription(act);
    event.setMessage(act);
    Representation rep = getEventRepresentation(event, getMediaTest());

    docAPI.appendRequest(Method.PUT, cr);
    Representation result = cr.put(rep, getMediaTest());
    if (!docAPI.appendResponse(result)) {
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());

      Response response = getResponse(getMediaTest(), result, Order.class);
      assertTrue(response.getSuccess());
      Order ord = (Order) response.getItem();
      assertEquals(ord.getStatus(), act);
    }
    RIAPUtils.exhaust(result);
  }

  /**
   * Delete order test
   * 
   * @param us
   *          the user
   */
  public void adminDeleteOrders(User us) {
    ClientResource cr = new ClientResource(getBaseAdminUrl());
    docAPI.appendRequest(Method.GET, cr);
    Representation result = cr.get(getMediaTest());
    if (!docAPI.appendResponse(result)) {
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());

      Response response = getResponse(getMediaTest(), result, Order.class, true);
      assertTrue(response.getSuccess());
      assertEquals(3, response.getTotal().intValue());

      ArrayList<Object> orders = response.getData();
      assertEquals(orders.size(), 3);

      Order ord = (Order) orders.get(0);
      cr = new ClientResource(getBaseAdminUrl() + "/" + ord.getId());
      result = cr.delete(getMediaTest());
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());

      ord = (Order) orders.get(1);
      cr = new ClientResource(getBaseAdminUrl() + "/" + ord.getId());
      result = cr.delete(getMediaTest());
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());

      ord = (Order) orders.get(2);
      cr = new ClientResource(getBaseAdminUrl() + "/users/" + us.getIdentifier() + "/" + ord.getId());
      result = cr.delete(getMediaTest());
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());

    }
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
      if (!media.isCompatible(MediaType.APPLICATION_JSON) && !media.isCompatible(MediaType.APPLICATION_XML)) {
        Engine.getLogger(AbstractSitoolsTestCase.class.getName()).warning("Only JSON or XML supported in tests");
        return null;
      }

      XStream xstream = XStreamFactory.getInstance().getXStreamReader(media);
      xstream.autodetectAnnotations(false);
      xstream.alias("response", Response.class);
      xstream.alias("order", Order.class);

      if (isArray) {
        xstream.alias("item", dataClass);
        xstream.alias("item", Object.class, dataClass);
        if (media.isCompatible(MediaType.APPLICATION_JSON)) {
          xstream.addImplicitCollection(Response.class, "data", dataClass);
          xstream.addImplicitCollection(Order.class, "events", Event.class);
        }
      }
      else {
        xstream.alias("item", dataClass);
        xstream.alias("item", Object.class, dataClass);

        if (dataClass == Order.class) {
          xstream.aliasField("order", Response.class, "item");
        }
        if (media.isCompatible(MediaType.APPLICATION_JSON)) {
          xstream.addImplicitCollection(Order.class, "events", Event.class);
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
   * Response to Representation
   * 
   * @param order
   *          Order
   * @param media
   *          MediaType
   * @return Representation
   */
  public static Representation getRepresentation(Order order, MediaType media) {
    if (media.equals(MediaType.APPLICATION_JSON) || media.equals(MediaType.APPLICATION_XML)) {
      XStream xstream = XStreamFactory.getInstance().getXStream(media);
      configure(xstream);

      if (media.equals(MediaType.APPLICATION_JSON)) {
        // Convertisseur Date / TimeStamp
        xstream.registerConverter(new SitoolsFeedDateConverter());
      }

      XstreamRepresentation<Order> rep = new XstreamRepresentation<Order>(media, order);
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
    xstream.alias("response", Response.class);
  }

  /**
   * Item to Representation
   * 
   * @param item
   *          Event
   * @param media
   *          MediaType
   * @return Representation
   */
  private Representation getEventRepresentation(Event item, MediaType media) {
    if (media.equals(MediaType.APPLICATION_JSON) || media.equals(MediaType.APPLICATION_XML)) {
      XStream xstream = XStreamFactory.getInstance().getXStream(media);
      configure(xstream);

      XstreamRepresentation<Event> rep = new XstreamRepresentation<Event>(media, item);
      rep.setXstream(xstream);
      return rep;
    }
    else {
      Engine.getLogger(AbstractSitoolsTestCase.class.getName()).warning("Only JSON or XML supported in tests");
      return null; // TODO complete test with ObjectRepresentation

    }
  }
}

/*******************************************************************************
 * Copyright 2010-2016 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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
package fr.cnes.sitools.ext.test.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.restlet.Component;
import org.restlet.Context;
import org.restlet.data.ClientInfo;
import org.restlet.data.Protocol;
import org.restlet.security.User;

import fr.cnes.sitools.common.SitoolsSettings;
import fr.cnes.sitools.common.application.ContextAttributes;
import fr.cnes.sitools.common.exception.SitoolsException;
import fr.cnes.sitools.ext.test.common.AbstractExtSitoolsTestCase;
import fr.cnes.sitools.order.OrderAdministration;
import fr.cnes.sitools.order.OrderStoreInterface;
import fr.cnes.sitools.order.OrderStoreXMLMap;
import fr.cnes.sitools.order.model.Order;
import fr.cnes.sitools.resources.order.utils.OrderAPI;
import fr.cnes.sitools.server.Consts;
import fr.cnes.sitools.tasks.TaskUtils;

public class OrderAPITestCase extends AbstractExtSitoolsTestCase {

  /**
   * Restlet Component for server
   */
  private Component component = null;

  private ClientInfo clientInfo;

  private Context context;

  private User user;

  private OrderStoreInterface store;

  private OrderAdministration orderApp;

  /**
   * relative url for dataset management REST API
   * 
   * @return url
   */
  protected String getAppUrl() {
    return SitoolsSettings.getInstance().getString(Consts.APP_ORDERS_ADMIN_URL);
  }

  /**
   * relative url for dataset management REST API
   * 
   * @return url
   */
  protected String getAttachUrl() {
    return SITOOLS_URL + getAppUrl();
  }

  @Before
  @Override
  /**
   * Create component, store and application and start server  
   * @throws java.lang.Exception
   */
  public void setUp() throws Exception {

    // Directory
    String tempAppDIR = SitoolsSettings.getInstance().getStoreDIR(Consts.APP_TMP_FOLDER_DIR);

    if (this.component == null) {
      this.component = new Component();
      this.component.getServers().add(Protocol.HTTP, getTestPort());
      this.component.getClients().add(Protocol.HTTP);
      this.component.getClients().add(Protocol.FILE);
      this.component.getClients().add(Protocol.CLAP);

      File tmpDirectory = new File(tempAppDIR);
      tmpDirectory.mkdirs();
      cleanDirectory(tmpDirectory);

      // Context
      Context ctx = this.component.getContext().createChildContext();
      ctx.getAttributes().put(ContextAttributes.SETTINGS, SitoolsSettings.getInstance());

      if (store == null) {
        File storeDirectory = new File(getTestRepository() + settings.getString(Consts.APP_ORDERS_STORE_DIR));
        storeDirectory.mkdirs();
        cleanDirectory(storeDirectory);
        store = new OrderStoreXMLMap(storeDirectory, ctx);
      }

      ctx.getAttributes().put(ContextAttributes.APP_ATTACH_REF, getAttachUrl());
      ctx.getAttributes().put(ContextAttributes.APP_STORE, store);
      orderApp = new OrderAdministration(ctx);
      this.component.getDefaultHost().attach(getAttachUrl(), orderApp);
      component.getInternalRouter().attach(SitoolsSettings.getInstance().getString(Consts.APP_ORDERS_ADMIN_URL),
          orderApp);

    }

    if (!this.component.isStarted()) {
      this.component.start();
    }

    user = new User("admin", "admin");
    clientInfo = new ClientInfo(getMediaTest());
    clientInfo.setUser(user);

    context = orderApp.getContext().createChildContext();
    context.getAttributes().put(TaskUtils.PARENT_APPLICATION, orderApp);

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
   * Test CRUD Graph with JSon format exchanges.
   * 
   * @throws SitoolsException
   */
  @Test
  public void testCRUD() throws SitoolsException {
    String orderDescription = "Order description";
    Order order = OrderAPI.createOrder("admin", context, orderDescription);

    assertNotNull(order);
    assertEquals(orderDescription, order.getDescription());
    assertEquals(1, order.getEvents().size());

    order = OrderAPI.activateOrder(order, context);
    assertEquals("active", order.getStatus());
    assertEquals(2, order.getEvents().size());

    order.setDescription(orderDescription + "_modified");
    order = OrderAPI.updateOrder(order, context);
    assertEquals(orderDescription + "_modified", order.getDescription());

    order = OrderAPI.createEvent(order, context, "Order activated and modified");
    // 2 events are already in the order, 1 for creation, the other for
    // activation
    assertEquals(3, order.getEvents().size());

    order = OrderAPI.terminateOrder(order, context);
    assertEquals("done", order.getStatus());
    assertEquals(4, order.getEvents().size());

    order = OrderAPI.orderFailed(order, context, "Order failed");
    assertEquals("failed", order.getStatus());

  }
}

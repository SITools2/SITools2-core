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
package fr.cnes.sitools.order;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.logging.Level;

import org.restlet.data.Status;
import org.restlet.ext.wadl.MethodInfo;
import org.restlet.ext.wadl.ParameterInfo;
import org.restlet.ext.wadl.ParameterStyle;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ResourceException;

import fr.cnes.sitools.common.model.Event;
import fr.cnes.sitools.common.model.ResourceCollectionFilter;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.order.model.Order;

/**
 * Class Resource for managing Order Collection (GET, POST)
 * 
 * @author jp.boignard (AKKA Technologies)
 * 
 */
public final class OrderCollectionResource extends AbstractOrderResource {

  @Override
  public void sitoolsDescribe() {
    setName("OrderCollectionResource");
    setDescription("Resource for managing order collection");
    setNegotiated(false);
  }

  /**
   * Update / Validate existing order
   * 
   * @param representation
   *          Order representation
   * @param variant
   *          client preferred media type
   * @return Representation
   */
  @Post
  public Representation newOrder(Representation representation, Variant variant) {
    if (representation == null) {
      trace(Level.INFO, "Cannot add new order");
      throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "ORDER_REPRESENTATION_REQUIRED");
    }
    try {
      Order orderInput = null;
      orderInput = getObject(representation);

      // Business service
      orderInput.setDateOrder(new Date(new GregorianCalendar().getTime().getTime()));

      // Add the event of creation in the list of events
      List<Event> events = orderInput.getEvents();
      if (events == null) {
        events = new ArrayList<Event>();
      }
      Event newEvent = new Event();
      newEvent.setAuthor(getUserId());
      newEvent.setDescription("Order creation");
      newEvent.setMessage("Order creation");
      newEvent.setEventDate(new Date());
      events.add(newEvent);
      orderInput.setEvents(events);

      Order orderOutput = getStore().create(orderInput);

      trace(Level.INFO, "Add new order");

      // Response
      Response response = new Response(true, orderOutput, Order.class, "order");
      return getRepresentation(response, variant);

    }
    catch (ResourceException e) {
      trace(Level.INFO, "Cannot add new order");
      getLogger().log(Level.INFO, null, e);
      throw e;
    }
    catch (Exception e) {
      trace(Level.INFO, "Cannot add new order");
      getLogger().log(Level.WARNING, null, e);
      throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
    }
  }

  @Override
  public void describePost(MethodInfo info) {
    info.setDocumentation("Method to create a new order by sending its representation.");
    this.addStandardPostOrPutRequestInfo(info);
    ParameterInfo param = new ParameterInfo("userId", false, "xs:string", ParameterStyle.TEMPLATE,
        "User identifier to determine who has done this order.");
    info.getRequest().getParameters().add(param);
    this.addStandardObjectResponseInfo(info);
    this.addStandardInternalServerErrorInfo(info);
  }

  /**
   * get all orders
   * 
   * @param variant
   *          client preferred media type
   * @return Representation
   */
  @Get
  public Representation retrieveOrder(Variant variant) {
    try {

      if (getOrderId() != null) {
        Order order = getStore().retrieve(getOrderId());
        trace(Level.FINE, "View order - id: " + getOrderId());
        Response response = new Response(true, order, Order.class, "order");
        return getRepresentation(response, variant);
      }
      else {
        ResourceCollectionFilter filter = new ResourceCollectionFilter(this.getRequest());
        List<Order> orders = getStore().getList(filter);
        int total = orders.size();
        orders = getStore().getPage(filter, orders);
        trace(Level.FINE, "View orders");
        Response response = new Response(true, orders, Order.class, "orders");
        response.setTotal(total);
        return getRepresentation(response, variant);
      }
    }
    catch (ResourceException e) {
      trace(Level.INFO, "Cannot view orders");
      getLogger().log(Level.INFO, null, e);
      throw e;
    }
    catch (Exception e) {
      trace(Level.INFO, "Cannot view orders");
      getLogger().log(Level.WARNING, null, e);
      throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
    }
  }

  @Override
  public void describeGet(MethodInfo info, String path) {
    if (path.contains("{userId}")) {
      info.setDocumentation("GET : " + path + " : returns orders for a specific user.");
    }
    else {
      info.setDocumentation("GET : " + path + " : returns orders for all users.");
    }
    this.addStandardGetRequestInfo(info);
    ParameterInfo param = new ParameterInfo("orderId", false, "xs:string", ParameterStyle.TEMPLATE, "Order identifier.");
    info.getRequest().getParameters().add(param);
    this.addStandardObjectResponseInfo(info);
    this.addStandardInternalServerErrorInfo(info);
    this.addStandardResourceCollectionFilterInfo(info);
  }

}

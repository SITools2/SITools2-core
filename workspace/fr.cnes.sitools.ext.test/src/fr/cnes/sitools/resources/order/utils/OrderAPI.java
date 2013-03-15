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
package fr.cnes.sitools.resources.order.utils;

import java.io.IOException;
import java.util.ArrayList;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Preference;
import org.restlet.data.Status;
import org.restlet.representation.ObjectRepresentation;

import fr.cnes.sitools.common.exception.SitoolsException;
import fr.cnes.sitools.common.model.Event;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.order.model.Order;
import fr.cnes.sitools.util.RIAPUtils;

/**
 * Util class to simplify Order management
 * 
 * 
 * 
 * @author m.gond
 */
public final class OrderAPI {

  /**
   * Default constructor declared as private because OrderAPI is a Utility class
   */
  private OrderAPI() {
    super();
  }

  /**
   * Create an order to trace it into the Sitools order system
   * 
   * @param userIdentifier
   *          the user identifier of the order
   * @param context
   *          the context
   * @param orderDescription
   *          the description of the order
   * @throws SitoolsException
   *           if there is an error
   * @return the created Order
   */
  public static Order createOrder(String userIdentifier, Context context, String orderDescription)
    throws SitoolsException {

    Order order = new Order();
    order.setUserId(userIdentifier);
    order.setDescription(orderDescription);

    Request reqPOST = new Request(Method.POST, "riap://component/orders/admin", new ObjectRepresentation<Order>(order));

    ArrayList<Preference<MediaType>> objectMediaType = new ArrayList<Preference<MediaType>>();
    objectMediaType.add(new Preference<MediaType>(MediaType.APPLICATION_JAVA_OBJECT));
    reqPOST.getClientInfo().setAcceptedMediaTypes(objectMediaType);

    org.restlet.Response r = context.getClientDispatcher().handle(reqPOST);

    if (r == null || Status.isError(r.getStatus().getCode())) {
      throw new SitoolsException("ERROR CREATING ORDER OBJECT");
    }

    @SuppressWarnings("unchecked")
    ObjectRepresentation<Response> or = (ObjectRepresentation<Response>) r.getEntity();
    try {
      Response resp = or.getObject();
      // check if there is an object in the response, if not return null
      if (resp == null) {
        return null;
      }
      Order orderOut = (Order) resp.getItem();
      return orderOut;

    }
    catch (IOException e) { // marshalling error
      throw new SitoolsException("ERROR CREATING ORDER OBJECT");
    }
    finally {
      RIAPUtils.exhaust(r);
    }

  }

  /**
   * Update the order
   * 
   * @param order
   *          the order to update
   * @param context
   *          the context
   * @return Order the updated order
   * @throws SitoolsException
   *           if there is an error
   */
  public static Order updateOrder(Order order, Context context) throws SitoolsException {

    Request reqPOST = new Request(Method.PUT, "riap://component/orders/admin/" + order.getId(),
        new ObjectRepresentation<Order>(order));
    ArrayList<Preference<MediaType>> objectMediaType = new ArrayList<Preference<MediaType>>();
    objectMediaType.add(new Preference<MediaType>(MediaType.APPLICATION_JAVA_OBJECT));
    reqPOST.getClientInfo().setAcceptedMediaTypes(objectMediaType);

    org.restlet.Response r = context.getClientDispatcher().handle(reqPOST);
    try {
      if (r == null || Status.isError(r.getStatus().getCode())) {
        throw new SitoolsException("ERROR UPDATING ORDER OBJECT");
      }

      return getOrderFromResponse(r);
    }
    finally {
      RIAPUtils.exhaust(r);
    }
  }

  /**
   * Add a event to the order
   * 
   * @param order
   *          the order to update
   * @param context
   *          the context
   * @param msg
   *          the message of the event to create
   * @return Order the order with the new Event
   * @throws SitoolsException
   *           if there is an error
   */
  public static Order createEvent(Order order, Context context, String msg) throws SitoolsException {

    Event event = new Event();
    event.setDescription("RESOURCE_ORDER_NOTIFICATION");
    event.setMessage(msg);

    order.getEvents().add(event);

    Request reqPUT = new Request(Method.PUT, "riap://component/orders/admin/" + order.getId() + "/addEvent",
        new ObjectRepresentation<Event>(event));
    ArrayList<Preference<MediaType>> objectMediaType = new ArrayList<Preference<MediaType>>();
    objectMediaType.add(new Preference<MediaType>(MediaType.APPLICATION_JAVA_OBJECT));
    reqPUT.getClientInfo().setAcceptedMediaTypes(objectMediaType);
    org.restlet.Response r = context.getClientDispatcher().handle(reqPUT);

    try {

      if (r == null || Status.isError(r.getStatus().getCode())) {
        throw new SitoolsException("ERROR CREATING ORDER OBJECT");
      }

      return getOrderFromResponse(r);
    }
    finally {
      RIAPUtils.exhaust(r);
    }
  }

  /**
   * Activate the given order
   * 
   * @param order
   *          the order
   * @param context
   *          the context
   * @return Order the order activated
   * @throws SitoolsException
   *           if there is an error
   */
  public static Order activateOrder(Order order, Context context) throws SitoolsException {
    Event event = new Event();
    event.setDescription("RESOURCE_ORDER_NOTIFICATION");
    event.setMessage("ACTIVE ORDER");

    Request reqPOST = new Request(Method.PUT, "riap://component/orders/admin/" + order.getId() + "/active",
        new ObjectRepresentation<Event>(event));
    ArrayList<Preference<MediaType>> objectMediaType = new ArrayList<Preference<MediaType>>();
    objectMediaType.add(new Preference<MediaType>(MediaType.APPLICATION_JAVA_OBJECT));
    reqPOST.getClientInfo().setAcceptedMediaTypes(objectMediaType);
    org.restlet.Response r = context.getClientDispatcher().handle(reqPOST);
    try {
      if (r == null || Status.isError(r.getStatus().getCode())) {
        throw new SitoolsException("ERROR ACTIVATING ORDER OBJECT");
      }

      return getOrderFromResponse(r);
    }
    finally {
      RIAPUtils.exhaust(r);
    }
  }

  /**
   * Set to done the given order
   * 
   * @param order
   *          the order
   * @param context
   *          the context
   * @return Order the order activated
   * @throws SitoolsException
   *           if there is an error
   */
  public static Order terminateOrder(Order order, Context context) throws SitoolsException {
    Event event = new Event();
    event.setDescription("RESOURCE_ORDER_NOTIFICATION");
    event.setMessage("TERMINATE ORDER");

    Request reqPOST = new Request(Method.PUT, "riap://component/orders/admin/" + order.getId() + "/done",
        new ObjectRepresentation<Event>(event));
    ArrayList<Preference<MediaType>> objectMediaType = new ArrayList<Preference<MediaType>>();
    objectMediaType.add(new Preference<MediaType>(MediaType.APPLICATION_JAVA_OBJECT));
    reqPOST.getClientInfo().setAcceptedMediaTypes(objectMediaType);

    org.restlet.Response r = context.getClientDispatcher().handle(reqPOST);
    try {
      if (r == null || Status.isError(r.getStatus().getCode())) {
        throw new SitoolsException("ERROR TERMINATING ORDER OBJECT");
      }

      return getOrderFromResponse(r);
    }
    finally {
      RIAPUtils.exhaust(r);
    }

  }

  /**
   * Set the order to fail
   * 
   * @param order
   *          the order
   * @param context
   *          the context
   * @param localizedMessage
   *          the message to set
   * @return the failed order
   * @throws SitoolsException
   *           if something goes wrong
   */
  public static Order orderFailed(Order order, Context context, String localizedMessage) throws SitoolsException {

    Event event = new Event();
    event.setDescription("RESOURCE_ORDER_NOTIFICATION");
    event.setMessage(localizedMessage);

    // TODO Order RiapUtils... Consts.URL
    Request reqPUT = new Request(Method.PUT, "riap://component/orders/admin/" + order.getId() + "/failed",
        new ObjectRepresentation<Event>(event));
    ArrayList<Preference<MediaType>> objectMediaType = new ArrayList<Preference<MediaType>>();
    objectMediaType.add(new Preference<MediaType>(MediaType.APPLICATION_JAVA_OBJECT));
    reqPUT.getClientInfo().setAcceptedMediaTypes(objectMediaType);
    org.restlet.Response r = context.getClientDispatcher().handle(reqPUT);
    try {
      if (r == null || Status.isError(r.getStatus().getCode())) {
        throw new SitoolsException("ERROR WHILE ORDER FAILURE");
      }

      return getOrderFromResponse(r);
    }
    finally {
      RIAPUtils.exhaust(r);
    }
  }

  /**
   * Return An order Object from a Response containing an Order object
   * 
   * @param response
   *          the response Containing a Response object
   * @return An Order Object
   * @throws SitoolsException
   *           when an error append
   */
  private static Order getOrderFromResponse(org.restlet.Response response) throws SitoolsException {
    @SuppressWarnings("unchecked")
    ObjectRepresentation<Response> or = (ObjectRepresentation<Response>) response.getEntity();
    try {
      Response resp = or.getObject();
      // check if there is an object in the response, if not return null
      if (resp == null) {
        return null;
      }
      Order order = (Order) resp.getItem();
      return order;
    }
    catch (IOException e) { // marshalling error
      throw new SitoolsException("ERROR GETTING ORDER FROM RESPONSE");
    }
  }

}

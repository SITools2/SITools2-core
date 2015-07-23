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
package fr.cnes.sitools.order;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;

import org.restlet.Request;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Status;
import org.restlet.engine.util.DateUtils;
import org.restlet.ext.jackson.JacksonRepresentation;
import org.restlet.ext.wadl.MethodInfo;
import org.restlet.ext.wadl.ParameterInfo;
import org.restlet.ext.wadl.ParameterStyle;
import org.restlet.ext.xstream.XstreamRepresentation;
import org.restlet.representation.FileRepresentation;
import org.restlet.representation.ObjectRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ResourceException;

import com.thoughtworks.xstream.XStream;

import fr.cnes.sitools.common.SitoolsSettings;
import fr.cnes.sitools.common.exception.SitoolsException;
import fr.cnes.sitools.common.model.Event;
import fr.cnes.sitools.common.model.ResourceCollectionFilter;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.order.model.ConstsOrder;
import fr.cnes.sitools.order.model.Order;
import fr.cnes.sitools.common.Consts;
import fr.cnes.sitools.util.RIAPUtils;

/**
 * Resource for POSTing new orders
 * 
 * @author jp.boignard (AKKA Technologies)
 * 
 */
public final class UserOrderResource extends AbstractOrderResource {

  /** Application Settings */
  private SitoolsSettings settings = ((UserOrderApplication) getApplication()).getSettings();

  @Override
  public void sitoolsDescribe() {
    setName("UserOrderResource");
    setDescription("Resource for posting orders");
  }

  /**
   * Create a file from the following Representation at the given urlDest. urlDest must be relative url using RIAP. It
   * must contains the file name as well
   * 
   * @param repr
   *          the representation
   * @param urlDest
   *          the destination url
   * @return The location of the new File
   * @throws SitoolsException
   *           if there is an error while creating the file
   */
  public String addFile(Representation repr, String urlDest) throws SitoolsException {

    getLogger().warning("ADD FILE TO : " + urlDest);

    Request reqPOST = new Request(Method.PUT, RIAPUtils.getRiapBase() + urlDest, repr);

    org.restlet.Response r = getContext().getClientDispatcher().handle(reqPOST);
    try {
      if (r == null || Status.isError(r.getStatus().getCode())) {
        throw new SitoolsException("ERROR ADDING FILE : " + urlDest);
      }
      return urlDest;
    }
    finally {
      RIAPUtils.exhaust(r);
    }
  }

  /**
   * Copy a file from <code>fileUrl</code> to <code>destUrl</code> destUrl must be pointing to a folder
   * 
   * @param fileUrl
   *          the file to copy
   * @param destUrl
   *          the destination folder url
   * @return the url of the created file
   * @throws SitoolsException
   *           if the copy is unsuccessful
   */
  public String copyFile(String fileUrl, String destUrl) throws SitoolsException {
    getLogger().warning("COPY FILE FROM " + fileUrl + " to " + destUrl);

    String urlAttach = settings.getString(Consts.APP_DATASTORAGE_URL);
    if (fileUrl.contains(urlAttach)) {
      fileUrl = fileUrl.substring(fileUrl.lastIndexOf(urlAttach));
    }

    FileRepresentation repr = (FileRepresentation) getFile(fileUrl);
    String fileName = repr.getFile().getName();

    String newFileUrl = this.addFile(repr, destUrl + "/" + fileName);

    return newFileUrl;
  }

  /**
   * Gets the representation of a File
   * 
   * @param fileUrl
   *          the url of the file
   * @return the Representation of a File
   * @throws SitoolsException
   *           if there is an error while getting the file
   */
  public Representation getFile(String fileUrl) throws SitoolsException {

    String appUrl = settings.getString(Consts.APP_URL);
    if (fileUrl.startsWith("http")) {
      fileUrl = fileUrl.substring(fileUrl.indexOf(appUrl) + appUrl.length());
    }
    Request reqGET = new Request(Method.GET, RIAPUtils.getRiapBase() + fileUrl);

    org.restlet.Response r = getContext().getClientDispatcher().handle(reqGET);

    if (r == null || Status.isError(r.getStatus().getCode())) {
      throw new SitoolsException("ERROR GETTING FILE : " + fileUrl);
    }
    else if (Status.CLIENT_ERROR_FORBIDDEN.equals(r.getStatus())) {
      throw new SitoolsException("CLIENT_ERROR_FORBIDDEN : " + fileUrl);
    }
    else if (Status.CLIENT_ERROR_UNAUTHORIZED.equals(r.getStatus())) {
      throw new SitoolsException("CLIENT_ERROR_UNAUTHORIZED : " + fileUrl);
    }

    return r.getEntity();

  }

  /**
   * Post a new order
   * 
   * @param representation
   *          Order representation
   * @param variant
   *          client preferred media type
   * @return Representation
   */
  @SuppressWarnings("unchecked")
  @Post
  public Representation newOrder(Representation representation, Variant variant) {
    if (representation == null) {
      throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "ORDER_REPRESENTATION_REQUIRED");
    }
    try {
      Order orderInput = null;
      if (MediaType.APPLICATION_JSON.isCompatible(representation.getMediaType())) {
        // Parse the JSON representation to get the bean
        orderInput = new JacksonRepresentation<Order>(representation, Order.class).getObject();
      }
      else if (representation.getMediaType().isCompatible(MediaType.APPLICATION_JAVA_OBJECT)) {
        ObjectRepresentation<Order> obj = (ObjectRepresentation<Order>) representation;
        orderInput = obj.getObject();
      }

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

      List<String> adminResourceCollection = new ArrayList<String>();
      List<String> resourceCollection = orderInput.getResourceCollection();
      if (resourceCollection != null) {
        UserOrderApplication application = (UserOrderApplication) getApplication();
        String orderFolder = "ORDER-"
            + DateUtils.format(new Date(), settings.getString("Starter.orderTimestampPattern"));
        for (int i = 0; i < resourceCollection.size(); i++) {

          // Copy resource to AdminStorage.

          String destination = application.getSettings().getString("Starter.PUBLIC_HOST_DOMAIN")
              + "/"
              + copyFile(resourceCollection.get(i),
                  application.getSettings().getString(Consts.APP_ADMINSTORAGE_ORDERS_URL));
          adminResourceCollection.add(destination);
          // Copy resource to OrdersUserStorage
          destination = settings.getString("Starter.PUBLIC_HOST_DOMAIN")
              + settings.getString(Consts.APP_URL)
              + copyFile(resourceCollection.get(i),
                  settings.getString(Consts.APP_USERSTORAGE_USER_URL).replace("{identifier}", orderInput.getUserId())
                      + "/orders/" + orderFolder);
          resourceCollection.set(i, destination);

          // TODO : delete dataSelectionFile ?
        }
        orderInput.setResourceCollection(resourceCollection);
        orderInput.setAdminResourceCollection(adminResourceCollection);
      }

      // Business service
      Order orderOutput = getStore().create(orderInput);

      // Response
      Response response = new Response(true, orderOutput, Order.class, "order");
      return getRepresentation(response, variant);

    }
    catch (ResourceException e) {
      getLogger().log(Level.INFO, null, e);
      throw e;
    }
    catch (Exception e) {
      getLogger().log(Level.SEVERE, null, e);
      throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
    }
  }

  @Override
  public void describePost(MethodInfo info) {
    info.setDocumentation("Method used by a user to create a new order.");
    this.addStandardPostOrPutRequestInfo(info);
    ParameterInfo paramUserId = new ParameterInfo("userId", true, "xs:string", ParameterStyle.TEMPLATE,
        "Identifier of the user creating the order");
    info.getRequest().getParameters().add(paramUserId);
    this.addStandardObjectResponseInfo(info);
    this.addStandardInternalServerErrorInfo(info);
  }

  /**
   * Invokes GET method to retrieve Order(s)
   * 
   * @param variant
   *          Variant
   * @return Representation
   */
  @Get
  public Representation retrieveOrder(Variant variant) {
    try {
      if (getRequest().getClientInfo().getUser() == null) {
        throw new ResourceException(Status.CLIENT_ERROR_FORBIDDEN);
      }
      if (getOrderId() != null) {
        Order order = getStore().retrieve(getOrderId());
        Response response = new Response(true, order, Order.class, "order");
        return getRepresentation(response, variant);
      }
      else {

        setUserId(getRequest().getClientInfo().getUser().getIdentifier());

        ResourceCollectionFilter filter = new ResourceCollectionFilter(this.getRequest());
        filter.setQuery(getUserId());
        filter.setFilterMode(ConstsOrder.STATUS_NOT_DELETED);
        List<Order> orders = getStore().getList(filter);

        int total = orders.size();
        orders = getStore().getPage(filter, orders);
        Response response = new Response(true, orders, Order.class, "orders");
        response.setTotal(total);
        return getRepresentation(response, variant);
      }
    }
    catch (ResourceException e) {
      getLogger().log(Level.INFO, null, e);
      throw e;
    }
    catch (Exception e) {
      getLogger().log(Level.SEVERE, null, e);
      throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
    }
  }

  @Override
  public void describeGet(MethodInfo info) {
    info.setDocumentation("Method used by a user to retrieve its orders, or a single one by ID.");
    this.addStandardGetRequestInfo(info);
    ParameterInfo paramUserId = new ParameterInfo("userId", true, "xs:string", ParameterStyle.TEMPLATE,
        "Identifier of the user creating the order");
    ParameterInfo paramOrderId = new ParameterInfo("orderId", false, "xs:string", ParameterStyle.TEMPLATE,
        "Identifier of the order to retrieve");
    info.getRequest().getParameters().add(paramUserId);
    info.getRequest().getParameters().add(paramOrderId);
    this.addStandardObjectResponseInfo(info);
    this.addStandardInternalServerErrorInfo(info);
    this.addStandardResourceCollectionFilterInfo(info);
  }

  /**
   * Delete Order
   * 
   * @param variant
   *          client preferred media type
   * @return Representation
   */
  @Delete
  public Representation deleteOrder(Variant variant) {
    try {

      Order input = getStore().retrieve(getOrderId());
      Response response = null;

      if (input != null) {

        input.setStatus("deleted");
        getStore().update(input);
        response = new Response(true, "order.delete.success");

      }
      else {
        response = new Response(false, "order.delete.notfound");

      }
      return getRepresentation(response, variant);

    }
    catch (ResourceException e) {
      getLogger().log(Level.INFO, null, e);
      throw e;
    }
    catch (Exception e) {
      getLogger().log(Level.SEVERE, null, e);
      throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
    }
  }

  @Override
  public void describeDelete(MethodInfo info) {
    info.setDocumentation("Method used by a user to delete an order by ID.");
    this.addStandardGetRequestInfo(info);
    ParameterInfo paramUserId = new ParameterInfo("userId", true, "xs:string", ParameterStyle.TEMPLATE,
        "Identifier of the user creating the order");
    ParameterInfo paramOrderId = new ParameterInfo("orderId", true, "xs:string", ParameterStyle.TEMPLATE,
        "Identifier of the order to retrieve");
    info.getRequest().getParameters().add(paramUserId);
    info.getRequest().getParameters().add(paramOrderId);
    this.addStandardSimpleResponseInfo(info);
    this.addStandardInternalServerErrorInfo(info);
  }

  /**
   * Configure the XStream
   * 
   * @param xstream
   *          the XStream to treat
   * @param response
   *          the response used
   */
  public void configure(XStream xstream, Response response) {
    super.configure(xstream, response);
    xstream.omitField(Order.class, "adminResourceCollection");

  }
}

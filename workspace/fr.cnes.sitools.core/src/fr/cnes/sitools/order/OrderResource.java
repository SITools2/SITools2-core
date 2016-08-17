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

import java.util.logging.Level;

import org.restlet.data.Status;
import org.restlet.ext.wadl.MethodInfo;
import org.restlet.ext.wadl.ParameterInfo;
import org.restlet.ext.wadl.ParameterStyle;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.Put;
import org.restlet.resource.ResourceException;

import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.order.model.Order;

/**
 * OrderResource for whose can manage (get/update/delete) single user Order
 * 
 * @author jp.boignard (AKKA Technologies)
 * 
 */
public final class OrderResource extends AbstractOrderResource {

  @Override
  public void sitoolsDescribe() {
    setName("OrderResource");
    setDescription("Resource for managing an identified order");
    setNegotiated(false);
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
    if (getOrderId() != null) {
      Order order = getStore().retrieve(getOrderId());
      trace(Level.INFO, "View order - id: " + getOrderId());
      Response response = new Response(true, order, Order.class, "order");
      return getRepresentation(response, variant);
    }
    else {
      Order[] orders = getStore().getArray();
      trace(Level.INFO, "View orders");
      Response response = new Response(true, orders);
      return getRepresentation(response, variant);
    }
  }

  @Override
  public void describeGet(MethodInfo info, String path) {
    if (path.contains("{userId}")) {
      info.setDocumentation("GET : " + path + " : returns a specific order for a specific user.");
    }
    else {
      info.setDocumentation("GET : " + path + " : returns a specific order.");
    }

    this.addStandardGetRequestInfo(info);
    ParameterInfo param = new ParameterInfo("orderId", false, "xs:string", ParameterStyle.TEMPLATE,
        "Identifier of the order to get.");
    info.getRequest().getParameters().add(param);
    this.addStandardObjectResponseInfo(info);
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
  @Put
  public Representation updateOrder(Representation representation, Variant variant) {
    Order orderOutput = null;
    try {
      Response response;
      if (representation != null) {
        // Parse object representation
        Order orderInput = getObject(representation, variant);

        // Business service
        orderOutput = getStore().update(orderInput);
        trace(Level.INFO, "Update order - id: " + getOrderId());
        response = new Response(true, orderOutput, Order.class, "order");
      }
      else {
        trace(Level.INFO, "Update order - id: " + getOrderId());
        response = new Response(false, "cannot update order");

      }

      return getRepresentation(response, variant);

    }
    catch (ResourceException e) {
      trace(Level.INFO, "Cannot update order - id: " + getOrderId());
      getLogger().log(Level.INFO, null, e);
      throw e;
    }
    catch (Exception e) {
      trace(Level.INFO, "Cannot update order - id: " + getOrderId());
      getLogger().log(Level.WARNING, null, e);
      throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
    }
  }

  @Override
  public void describePut(MethodInfo info) {
    info.setDocumentation("Modify an order sending its new representation.");
    this.addStandardPostOrPutRequestInfo(info);
    this.addStandardObjectResponseInfo(info);
    this.addStandardInternalServerErrorInfo(info);
  }

  /**
   * Delete order
   * 
   * @param variant
   *          client preferred media type
   * @return Representation
   */
  @Delete
  public Representation deleteOrder(Variant variant) {
    try {
      Order order = getStore().retrieve(getOrderId());
      Response response;
      if (order != null) {
        // Business service
        getStore().delete(getOrderId());
        trace(Level.INFO, "Delete order - id: " + getOrderId());
        response = new Response(true, "order.delete.success");
      }
      else {
        trace(Level.INFO, "Cannot delete order - id: " + getOrderId());
        response = new Response(false, "order.delete.failure");
      }
      return getRepresentation(response, variant);

    }
    catch (ResourceException e) {
      trace(Level.INFO, "Cannot delete order - id: " + getOrderId());
      getLogger().log(Level.INFO, null, e);
      throw e;
    }
    catch (Exception e) {
      trace(Level.INFO, "Cannot delete order - id: " + getOrderId());
      getLogger().log(Level.WARNING, null, e);
      throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
    }
  }

  @Override
  public void describeDelete(MethodInfo info) {
    info.setDocumentation("Method to delete the order identified with its ID.");
    this.addStandardGetRequestInfo(info);
    ParameterInfo param = new ParameterInfo("orderId", false, "xs:string", ParameterStyle.TEMPLATE,
        "Identifier of the order to get.");
    info.getRequest().getParameters().add(param);
    this.addStandardSimpleResponseInfo(info);
    this.addStandardInternalServerErrorInfo(info);
  }

}

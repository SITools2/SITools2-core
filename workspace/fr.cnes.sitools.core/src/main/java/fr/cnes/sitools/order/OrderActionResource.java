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

import org.restlet.data.Status;
import org.restlet.ext.wadl.MethodInfo;
import org.restlet.ext.wadl.ParameterInfo;
import org.restlet.ext.wadl.ParameterStyle;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Put;
import org.restlet.resource.ResourceException;

import fr.cnes.sitools.common.model.Event;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.order.model.Order;

/**
 * Resource for order actions
 * 
 * @author jp.boignard (AKKA Technologies)
 * 
 */
public final class OrderActionResource extends AbstractOrderResource {

  @Override
  public void sitoolsDescribe() {
    setName("OrderResource");
    setDescription("Resource for managing an identified order");
    setNegotiated(false);
  }

  /**
   * Do action
   * 
   * @param representation
   *          Event representation
   * @param variant
   *          client preferred media type
   * @return Representation of the Order
   */
  @Put
  public Representation adminActionOrder(Representation representation, Variant variant) {
    Order orderOutput = null;
    try {

      if (representation != null) {
        // Parse object representation
        Event actionEvent = getEvent(representation, variant);

        Order orderInput = getStore().retrieve(getOrderId());

        List<Event> events = orderInput.getEvents();
        if (events == null) {
          events = new ArrayList<Event>();
        }

        if (actionEvent == null) {
          // Create event related to order treatment on admin side
          actionEvent = new Event();
        }

        actionEvent.setAuthor(getUserId());
        actionEvent.setEventDate(new Date());

        if (getActionId().toString().compareTo("addEvent") != 0) {
          orderInput.setStatus(getActionId());
        }

        events.add(actionEvent);
        orderInput.setEvents(events);
        // Business service
        orderOutput = getStore().update(orderInput);
      }

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
  public void describePut(MethodInfo info) {
    info.setDocumentation("Modify the order status according to the action identifier.");
    this.addStandardPostOrPutRequestInfo(info);
    ParameterInfo paramOrderId = new ParameterInfo("orderId", true, "xs:string", ParameterStyle.TEMPLATE,
        "Identifier of the order to modify.");
    ParameterInfo paramUserId = new ParameterInfo("userId", false, "xs:string", ParameterStyle.TEMPLATE,
        "Identifier of the user to which belongs the order.");
    ParameterInfo paramActionId = new ParameterInfo("actionId", true, "xs:string", ParameterStyle.TEMPLATE,
        "Identifier of the action to apply to the order (new event).");
    paramActionId.setDefaultValue("addEvent");
    info.getRequest().getParameters().add(paramOrderId);
    info.getRequest().getParameters().add(paramUserId);
    info.getRequest().getParameters().add(paramActionId);
    this.addStandardObjectResponseInfo(info);
    this.addStandardInternalServerErrorInfo(info);
  }

}

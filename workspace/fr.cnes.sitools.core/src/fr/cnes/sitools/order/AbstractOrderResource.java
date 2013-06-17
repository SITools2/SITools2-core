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
package fr.cnes.sitools.order;

import java.io.IOException;

import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.ext.jackson.JacksonRepresentation;
import org.restlet.ext.xstream.XstreamRepresentation;
import org.restlet.representation.ObjectRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

import com.thoughtworks.xstream.XStream;

import fr.cnes.sitools.common.SitoolsResource;
import fr.cnes.sitools.common.XStreamFactory;
import fr.cnes.sitools.common.model.Event;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.common.store.SitoolsStore;
import fr.cnes.sitools.order.model.Order;

/**
 * Abstract order resource
 * 
 * @author m.marseille (AKKA Technologies)
 * 
 */
public abstract class AbstractOrderResource extends SitoolsResource {

  /**
   * Application associated
   */
  private AbstractOrderApplication application = null;

  /**
   * Store associated
   */
  private SitoolsStore<Order> store = null;

  /**
   * Id of the order
   */
  private String orderId = null;

  /**
   * User that made the order
   */
  private String userId = null;

  /**
   * Actions
   */
  private String actionId = null;

  @Override
  public void doInit() {
    super.doInit();

    // Declares the two variants supported
    getVariants().add(new Variant(MediaType.APPLICATION_XML));
    getVariants().add(new Variant(MediaType.APPLICATION_JSON));

    application = (AbstractOrderApplication) getApplication();
    store = application.getStore();

    orderId = (String) this.getRequest().getAttributes().get("orderId");
    userId = (String) this.getRequest().getAttributes().get("userId");
    actionId = (String) this.getRequest().getAttributes().get("actionId");
  }

  /**
   * Gets representation according to the specified mediaType.
   * 
   * @param response
   *          the response to treat
   * @param media
   *          the media to deal with
   * @return Representation
   */
  public final Representation getRepresentation(Response response, MediaType media) {
    getLogger().info(media.toString());
    if (media.isCompatible(MediaType.APPLICATION_JAVA_OBJECT)) {
      return new ObjectRepresentation<Response>(response);
    }

    XStream xstream = XStreamFactory.getInstance().getXStream(media, getContext());
    xstream.alias("order", Order.class);
    configure(xstream, response);
    
    XstreamRepresentation<Response> rep = new XstreamRepresentation<Response>(media, response);
    rep.setXstream(xstream);
    return rep;
  }

  /**
   * Get the order using representation and variant
   * 
   * @param representation
   *          the representation to use
   * @param variant
   *          the variant to use
   * @return the corresponding order
   */
  @SuppressWarnings("unchecked")
  public final Order getObject(Representation representation, Variant variant) {
    Order orderInput = null;
    if (MediaType.APPLICATION_XML.isCompatible(representation.getMediaType())) {
      // Parse the XML representation to get the order bean
      // Default parsing
      XstreamRepresentation<Order> repXML = new XstreamRepresentation<Order>(representation);
      XStream xstream = XStreamFactory.getInstance().getXStreamReader(MediaType.APPLICATION_XML);
      xstream.autodetectAnnotations(false);
      xstream.alias("order", Order.class);
      xstream.alias("event", Event.class);
      repXML.setXstream(xstream);
      orderInput = repXML.getObject();
    }
    else if (MediaType.APPLICATION_JSON.isCompatible(representation.getMediaType())) {
      // Parse the JSON representation to get the mail bean
      orderInput = new JacksonRepresentation<Order>(representation, Order.class).getObject();
    }
    else if (representation.getMediaType().isCompatible(MediaType.APPLICATION_JAVA_OBJECT)) {
      ObjectRepresentation<Order> obj = (ObjectRepresentation<Order>) representation;
      try {
        orderInput = obj.getObject();
      }
      catch (IOException e) {
        throw new ResourceException(Status.SERVER_ERROR_INTERNAL, null, e);
      }
    }
    return orderInput;
  }

  /**
   * Get the event using representation and variant
   * 
   * @param representation
   *          the representation to use
   * @param variant
   *          the variant to use
   * @return the corresponding event
   */
  @SuppressWarnings("unchecked")
  public final Event getEvent(Representation representation, Variant variant) {
    Event event = null;
    try {
      if (MediaType.APPLICATION_XML.isCompatible(representation.getMediaType())) {
        // Parse the XML representation to get the bean
        // Default parsing
        XstreamRepresentation<Event> repXML = new XstreamRepresentation<Event>(representation);
        XStream xstream = XStreamFactory.getInstance().getXStreamReader(MediaType.APPLICATION_XML);
        xstream.autodetectAnnotations(false);
        xstream.alias("event", Event.class);
        repXML.setXstream(xstream);
        event = repXML.getObject();
      }
      else if (MediaType.APPLICATION_JSON.isCompatible(representation.getMediaType())) {
        // Parse the JSON representation to get the bean
        event = new JacksonRepresentation<Event>(representation, Event.class).getObject();
      }
      else if (representation.getMediaType().isCompatible(MediaType.APPLICATION_JAVA_OBJECT)) {
        ObjectRepresentation<Event> obj = (ObjectRepresentation<Event>) representation;
        event = obj.getObject();

      }
      return event;
    }
    catch (IOException e) {
      throw new ResourceException(Status.SERVER_ERROR_INTERNAL, null, e);
    }
  }
  
  /**
   * Get the store attached to the application
   * @return the order store
   */
  public final SitoolsStore<Order> getStore() {
    return this.store;
  }
  
  /**
   * Get the order identifier for resource treatments
   * @return the order identifier
   */
  public final String getOrderId() {
    return this.orderId;
  }
  
  /**
   * Get the order identifier for resource treatments
   * @return the user identifier
   */
  public final String getUserId() {
    return this.userId;
  }
  
  /**
   * Set the order identifier for resource treatments
   * @param uid the user identifier to set
   */
  public final void setUserId(String uid) {
    this.userId = uid;
  }
  
  /**
   * Get the action identifier for resource treatments
   * @return the action identifier
   */
  public final String getActionId() {
    return this.actionId;
  }
  
}

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
package fr.cnes.sitools.resources.order.cart;

import org.restlet.data.MediaType;
import org.restlet.ext.jackson.JacksonRepresentation;
import org.restlet.ext.wadl.MethodInfo;
import org.restlet.ext.xstream.XstreamRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Get;
import org.restlet.resource.Post;

import com.thoughtworks.xstream.XStream;

import fr.cnes.sitools.cart.model.CartSelections;
import fr.cnes.sitools.common.XStreamFactory;
import fr.cnes.sitools.common.resource.SitoolsParameterizedResource;
import fr.cnes.sitools.resources.order.OrderResourceFacade;
import fr.cnes.sitools.tasks.TaskUtils;

/**
 * Facade for OrderResource
 * 
 * 
 * @author m.gond
 */
public class CartOrderResourceFacade extends OrderResourceFacade {
  /**
   * Description de la ressource
   */
  @Override
  public void sitoolsDescribe() {
    setName("OrderResourceFacade");
    setDescription("Resource to order data");
  }
  
  

  /**
   * Description WADL de la methode POST
   * 
   * @param info
   *          The method description to update.
   */
  @Override
  public void describePost(MethodInfo info) {
    info.setDocumentation("Method to order data from a dataset");
    info.setIdentifier("order");
    addStandardPostOrPutRequestInfo(info);
    addStandardResponseInfo(info);
    addStandardInternalServerErrorInfo(info);
    this.addInfo(info);
  }

  /**
   * Create the order
   * 
   * @param represent
   *          the {@link Representation} entity
   * @param variant
   *          The {@link Variant} needed
   * @return a representation
   */
  @Post
  public Representation orderPost(Representation represent, Variant variant) {
    processBody(variant);
    return TaskUtils.execute(this, variant);
  }

  /**
   * Create the order
   * 
   * @param variant
   *          The {@link Variant} needed
   * @return a representation
   */
  @Get
  public Representation orderGet(Variant variant) {
    return TaskUtils.execute(this, variant);
  }

  /**
   * process the body and save the request entity {@link Representation}
   */
  public void processBody(Variant variant) {
    Representation body = this.getRequest().getEntity();
    if (body != null && body.isAvailable() && body.getSize() > 0) {
       getContext().getAttributes().put(TaskUtils.BODY_CONTENT, getObject(body, variant));
    }
    else {
      getContext().getAttributes().remove(TaskUtils.BODY_CONTENT);
    }
  }
  
  
  public final CartSelections getObject(Representation representation, Variant variant) {

    CartSelections selections = null;

    if (MediaType.APPLICATION_XML.isCompatible(representation.getMediaType())) {
      XstreamRepresentation<CartSelections> repXML = new XstreamRepresentation<CartSelections>(representation);
      XStream xstream = XStreamFactory.getInstance().getXStreamReader(MediaType.APPLICATION_XML);
      repXML.setXstream(xstream);
      selections = repXML.getObject();

    }
    else if (MediaType.APPLICATION_JSON.isCompatible(representation.getMediaType())) {
      // Parse the JSON representation to get the bean
      selections = new JacksonRepresentation<CartSelections>(representation, CartSelections.class).getObject();
    }

    return selections;
  }

}

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

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.ext.wadl.ApplicationInfo;
import org.restlet.ext.wadl.DocumentationInfo;
import org.restlet.routing.Router;

import fr.cnes.sitools.common.model.Category;

/**
 * Application for managing User orders.
 * @author jp.boignard (AKKA Technologies)
 * 
 */
public final class OrderAdministration extends AbstractOrderApplication {

  /**
   * Constructor
   * 
   * @param context
   *          Restlet Context
   */
  public OrderAdministration(Context context) {
    super(context);
  }

  @Override
  public void sitoolsDescribe() {
    setCategory(Category.ADMIN);
    setName("OrderAdministration");
    setDescription("Management of off-line or asynchronous orders");
  }

  @Override
  public Restlet createInboundRoot() {

    Router router = new Router(getContext());

    router.attachDefault(OrderCollectionResource.class);

    router.attach("/users/{userId}", OrderCollectionResource.class);
    router.attach("/users/{userId}/{orderId}", OrderResource.class);
    router.attach("/users/{userId}/{orderId}/{actionId}", OrderActionResource.class);

    router.attach("/config", OrderConfigurationResource.class);
    
    router.attach("/{orderId}", OrderResource.class);
    router.attach("/{orderId}/{actionId}", OrderActionResource.class);

    return router;
  }
  
  @Override
  public ApplicationInfo getApplicationInfo(Request request, Response response) {
    ApplicationInfo result = super.getApplicationInfo(request, response);
    DocumentationInfo docInfo = new DocumentationInfo("Order application for SITools2.");
    docInfo.setTitle("API documentation.");
    result.getDocumentations().add(docInfo);
    return result;
  }

}

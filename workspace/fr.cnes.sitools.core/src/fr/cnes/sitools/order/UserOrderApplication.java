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

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.ext.wadl.ApplicationInfo;
import org.restlet.ext.wadl.DocumentationInfo;
import org.restlet.routing.Router;

import fr.cnes.sitools.common.model.Category;

/**
 * API for User Orders
 * 
 * @author JP BOIGNARD
 * 
 */
public final class UserOrderApplication extends AbstractOrderApplication {

  /**
   * Users post their orders to this application.
   * 
   * @param context
   *          Restlet application Context
   */
  public UserOrderApplication(Context context) {
    super(context);
  }

  @Override
  public void sitoolsDescribe() {
    setCategory(Category.USER);
    setName("UserOrderApplication");
    setDescription("Application allowing users to follow their orders.\n"
        + "-> Administrator must have all authorizations"
        + "-> Public user must have GET/POST/DELETE authorizations to retrieve, create or delete its orders");
  }

  @Override
  public Restlet createInboundRoot() {
    Router router = new Router(getContext());
    router.attachDefault(UserOrderResource.class);
    router.attach("/{orderId}", UserOrderResource.class);
    return router;
  }

  @Override
  public ApplicationInfo getApplicationInfo(Request request, Response response) {
    ApplicationInfo result = super.getApplicationInfo(request, response);
    DocumentationInfo docInfo = new DocumentationInfo("Order application on client side for SITools2.");
    docInfo.setTitle("API documentation.");
    result.getDocumentations().add(docInfo);
    return result;
  }

}

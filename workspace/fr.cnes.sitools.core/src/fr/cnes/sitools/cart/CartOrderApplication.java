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
package fr.cnes.sitools.cart;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.ext.wadl.ApplicationInfo;
import org.restlet.ext.wadl.DocumentationInfo;
import org.restlet.routing.Router;

import fr.cnes.sitools.common.model.Category;
import fr.cnes.sitools.order.AbstractOrderApplication;

/**
 * API for User Orders
 * 
 * @author JP BOIGNARD
 * 
 */
public final class CartOrderApplication extends AbstractCartOrderApplication {

  /** Root directory for user accounts (home) */
  private String rootDirectory;
  
  /**
   * Users post their orders to this application.
   * 
   * @param context
   *          Restlet application Context
   */
  public CartOrderApplication(Context context) {
    super(context);
    this.rootDirectory = (String) context.getAttributes().get("USER_STORAGE_ROOT");
  }

  @Override
  public void sitoolsDescribe() {
    setCategory(Category.USER);
    setName("CartOrderApplication");
    setDescription("Application parsing json order to metadata.xml.\n"
        + "-> Administrator must have all authorizations"
        + "-> Public user must have GET/POST/DELETE authorizations to retrieve, create or delete its orders");
  }

  @Override
  public Restlet createInboundRoot() {
    Router router = new Router(getContext());
    router.attachDefault(CartOrderResource.class);
    router.attach("/", CartOrderResource.class);
    return router;
  }

  @Override
  public ApplicationInfo getApplicationInfo(Request request, Response response) {
    ApplicationInfo result = super.getApplicationInfo(request, response);
    DocumentationInfo docInfo = new DocumentationInfo("Cart order application on client side for SITools2.");
    docInfo.setTitle("API documentation.");
    result.getDocumentations().add(docInfo);
    return result;
  }

}

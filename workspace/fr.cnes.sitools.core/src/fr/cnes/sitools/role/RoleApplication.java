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
package fr.cnes.sitools.role;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.ext.wadl.ApplicationInfo;
import org.restlet.ext.wadl.DocumentationInfo;
import org.restlet.routing.Router;

import fr.cnes.sitools.common.application.ContextAttributes;
import fr.cnes.sitools.common.application.SitoolsApplication;
import fr.cnes.sitools.common.model.Category;
import fr.cnes.sitools.common.store.SitoolsStore;
import fr.cnes.sitools.notification.business.NotifierFilter;
import fr.cnes.sitools.role.model.Role;

/**
 * Application for managing role
 * 
 * Dependencies : DataSets columns references Role notions.
 * 
 * @author AKKA Technologies
 * 
 */
public final class RoleApplication extends SitoolsApplication {

  /** Store */
  private RoleStoreInterface store = null;

  /**
   * Constructor
   * 
   * @param context
   *          Restlet Host Context
   */
  @SuppressWarnings("unchecked")
  public RoleApplication(Context context) {
    super(context);
    this.store = (RoleStoreInterface) context.getAttributes().get(ContextAttributes.APP_STORE);
  }

  @Override
  public void sitoolsDescribe() {
    setCategory(Category.ADMIN);
    setName("RoleApplication");
    setDescription("Roles management");
  }

  @Override
  public Restlet createInboundRoot() {

    Router router = new Router(getContext());

    // Attach the resources to the router
    router.attachDefault(RoleCollectionResource.class);
    router.attach("/{roleId}", RoleResource.class);
    router.attach("/{roleId}/users", RoleUsersResource.class);
    router.attach("/{roleId}/groups", RoleGroupsResource.class);

    router.attach("/users/notify/{userId}", RoleNotifyUserResource.class);
    router.attach("/groups/notify/{groupId}", RoleNotifyGroupResource.class);

    NotifierFilter filter = new NotifierFilter(getContext());
    filter.setNext(router);
    return filter;
  }

  /**
   * Gets the store value
   * 
   * @return the store
   */
  public RoleStoreInterface getStore() {
    return store;
  }

  @Override
  public ApplicationInfo getApplicationInfo(Request request, Response response) {
    ApplicationInfo result = super.getApplicationInfo(request, response);
    DocumentationInfo docInfo = new DocumentationInfo("Role application to handle roles in Sitools2.");
    docInfo.setTitle("API documentation.");
    result.getDocumentations().add(docInfo);
    return result;
  }

}

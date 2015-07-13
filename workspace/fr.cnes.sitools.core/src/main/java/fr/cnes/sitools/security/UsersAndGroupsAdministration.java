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
package fr.cnes.sitools.security;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.ext.wadl.ApplicationInfo;
import org.restlet.ext.wadl.DocumentationInfo;
import org.restlet.routing.Filter;
import org.restlet.routing.Router;

import fr.cnes.sitools.common.application.ContextAttributes;
import fr.cnes.sitools.common.application.SitoolsApplication;
import fr.cnes.sitools.common.model.Category;
import fr.cnes.sitools.notification.business.NotifierFilter;

/**
 * Class Application for managing users and groups
 * 
 * @author jp.boignard (AKKA Technologies)
 */
public final class UsersAndGroupsAdministration extends SitoolsApplication {

  /** Store */
  private UsersAndGroupsStore store = null;

  /**
   * Constructor
   * 
   * @param context
   *          Restlet Host Context
   */
  public UsersAndGroupsAdministration(Context context) {
    super(context);
    this.store = (UsersAndGroupsStore) context.getAttributes().get(ContextAttributes.APP_STORE);

    // TODO Constructeur vide -
    // store => datasource jdbc dans fichier de properties
    // sécurité - roles => ICI ?
    // this.getRoles().add(new Role("ADMIN", "Administrateur"));
  }

  @Override
  public void sitoolsDescribe() {
    setCategory(Category.ADMIN);
    setName("UsersAndGroupsAdministration");
    setDescription("Users and groups management");
  }

  @Override
  public Restlet createInboundRoot() {
    // Create a router
    Router router = new Router(getContext());

    // Attach the resources to the router
    router.attach("/groups", GroupsResource.class);
    router.attach("/groups/{group}", GroupResource.class);
    router.attach("/groups/{group}/users", UsersResource.class);

    router.attach("/users", UsersResource.class);
    router.attach("/users/{user}", UserResource.class);
    router.attach("/users/{user}/findRoles", UserResource.class);
    router.attach("/users/{user}/groups", GroupsResource.class);

    Filter filter = new NotifierFilter(getContext());
    filter.setNext(router);
    return filter;
  }

 /**
   * Gets the store value
   * 
   * @return the store
   */
  public UsersAndGroupsStore getStore() {
    return store;
  }
  
  @Override
  public ApplicationInfo getApplicationInfo(Request request, Response response) {
    ApplicationInfo result = super.getApplicationInfo(request, response);
    DocumentationInfo docInfo = new DocumentationInfo("Application handling users and groups in Sitools2.");
    docInfo.setTitle("API documentation.");
    result.getDocumentations().add(docInfo);
    return result;
  }

}

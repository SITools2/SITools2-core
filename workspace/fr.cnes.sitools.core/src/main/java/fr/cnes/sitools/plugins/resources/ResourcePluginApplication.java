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
package fr.cnes.sitools.plugins.resources;

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

/**
 * Application handling request of dynamic resources attachment
 * 
 * @author m.marseille (AKKA Technologies)
 */
public final class ResourcePluginApplication extends SitoolsApplication {

  /** Store */
  private ResourcePluginStoreInterface store = null;

  /**
   * Application associated to a converter
   * 
   * @param context
   *          application context
   */
  public ResourcePluginApplication(Context context) {
    super(context);
    this.store = (ResourcePluginStoreInterface) context.getAttributes().get(ContextAttributes.APP_STORE);
  }

  @Override
  public void sitoolsDescribe() {
    setName("ResourcePluginApplication");
    setDescription("Application handling request of dynamic resources attachment");
    setCategory(Category.ADMIN);
  }

  @Override
  public Restlet createInboundRoot() {
    Router router = new Router(getContext());

    router.attachDefault(ResourcePluginResource.class);

    // // GET : gets the list of registered resources
    // router.attach("/list", ResourceClassPluginCollectionResource.class);
    //
    // // GET : gets the representation of a registered class of application
    // router.attach("/list/{resourceClass}", ResourceClassPluginResource.class);

    router.attach("/{resourcePluginId}", ResourcePluginResource.class);
    router.attach("/{resourcePluginId}/notify", ResourcePluginNotificationResource.class);

    return router;
  }

  /**
   * Gets the store value
   * 
   * @return the store
   */
  public ResourcePluginStoreInterface getStore() {
    return store;
  }

  @Override
  public ApplicationInfo getApplicationInfo(Request request, Response response) {
    ApplicationInfo result = super.getApplicationInfo(request, response);
    DocumentationInfo docInfo = new DocumentationInfo("Resource plugins management.");
    docInfo.setTitle("API documentation.");
    result.getDocumentations().add(docInfo);
    return result;
  }

}

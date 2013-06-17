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
package fr.cnes.sitools.plugins.filters;

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
import fr.cnes.sitools.plugins.filters.model.FilterModel;

/**
 * Application handling request of dynamic filters attachment
 * 
 * @author m.marseille (AKKA Technologies)
 */
public final class FilterPluginApplication extends SitoolsApplication {

  /** Store */
  private SitoolsStore<FilterModel> store = null;

  /**
   * Application associated to a converter
   * 
   * @param context
   *          application context
   */
  @SuppressWarnings("unchecked")
  public FilterPluginApplication(Context context) {
    super(context);
    this.store = (SitoolsStore<FilterModel>) context.getAttributes().get(ContextAttributes.APP_STORE);
  }

  @Override
  public void sitoolsDescribe() {
    setName("ApplicationFilterPluginApplication");
    setDescription("Application handling request of dynamic plugins attachment");
    setCategory(Category.ADMIN);
  }

  @Override
  public Restlet createInboundRoot() {
    Router router = new Router(getContext());

    router.attachDefault(FilterPluginResource.class);
    router.attach("/{pluginId}", FilterPluginResource.class);
    router.attach("/{pluginId}/notify", FilterPluginNotificationResource.class);

    return router;
  }

  /**
   * Gets the store value
   * 
   * @return the store
   */
  public SitoolsStore<FilterModel> getStore() {
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

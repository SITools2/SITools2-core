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
package fr.cnes.sitools.dataset.filter;

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
import fr.cnes.sitools.dataset.filter.model.FilterChainedModel;

/**
 * Exposition of Filters
 * 
 * @author AKKA
 */
public final class FilterApplication extends SitoolsApplication {

  /** Store */
  private SitoolsStore<FilterChainedModel> store = null;

  /**
   * Application associated to a filter
   * 
   * @param context
   *          application context
   */
  @SuppressWarnings("unchecked")
  public FilterApplication(Context context) {
    super(context);
    this.store = (SitoolsStore<FilterChainedModel>) context.getAttributes().get(ContextAttributes.APP_STORE);
  }

  @Override
  public void sitoolsDescribe() {
    setCategory(Category.ADMIN);
    setName("FilterApplication");
    setDescription("Act on filters defined for datasets.");
  }

  @Override
  public Restlet createInboundRoot() {
    Router router = new Router(getContext());

    // router.attachDefault(FilterChainResource.class);
    // router.attach("/{filterChainedId}", FilterResource.class);
    // router.attach("/{filterChainedId}/notify", FilterNotificationResource.class);
    //
    // add a new filter to the filter list, get the list, delete the list
    router.attachDefault(FilterChainResource.class);
    router.attach("/notify", FilterNotificationResource.class);
    // get, modify or delete a filter in the list
    router.attach("/{filterId}", FilterResource.class);
    // Change the Filter status
    router.attach("/{filterId}/start", FilterActivationResource.class);
    router.attach("/{filterId}/stop", FilterActivationResource.class);

    return router;
  }

  /**
   * Gets the store value
   * 
   * @return the store
   */
  public SitoolsStore<FilterChainedModel> getStore() {
    return store;
  }

  @Override
  public ApplicationInfo getApplicationInfo(Request request, Response response) {
    ApplicationInfo result = super.getApplicationInfo(request, response);
    DocumentationInfo docInfo = new DocumentationInfo("Filters management for all datasets.");
    docInfo.setTitle("API documentation.");
    result.getDocumentations().add(docInfo);
    return result;
  }
}

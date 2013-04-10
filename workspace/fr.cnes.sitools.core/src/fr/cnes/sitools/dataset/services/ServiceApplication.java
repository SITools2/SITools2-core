/*******************************************************************************
 * Copyright 2011 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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
package fr.cnes.sitools.dataset.services;

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
import fr.cnes.sitools.common.store.SitoolsStore;
import fr.cnes.sitools.dataset.services.model.ServiceCollectionModel;
import fr.cnes.sitools.notification.business.NotifierFilter;

/**
 * Application used to manage GuiServices on datasets
 * 
 * 
 * @author m.gond
 */
public class ServiceApplication extends SitoolsApplication {

  /** Store */
  private SitoolsStore<ServiceCollectionModel> store = null;

  /**
   * Constructor
   * 
   * @param context
   *          Restlet Host Context
   */
  @SuppressWarnings("unchecked")
  public ServiceApplication(Context context) {
    super(context);
    this.store = (SitoolsStore<ServiceCollectionModel>) context.getAttributes().get(ContextAttributes.APP_STORE);
  }

  @Override
  public void sitoolsDescribe() {
    setCategory(Category.ADMIN);
    setName("ServiceApplication");
    setDescription("Management of services on a dataset");
  }

  @Override
  public Restlet createInboundRoot() {

    Router router = new Router(getContext());

    // Complete collection of gui services
    router.attachDefault(ServiceCollectionResource.class);
    router.attach("/server", ServerServiceCollectionResource.class);
    router.attach("/server/{resourcePluginId}", ServerServiceResource.class);

    Filter filter = new NotifierFilter(getContext());
    filter.setNext(router);
    return filter;
  }

  /**
   * Gets the store value
   * 
   * @return the store
   */
  public SitoolsStore<ServiceCollectionModel> getStore() {
    return store;
  }

  @Override
  public ApplicationInfo getApplicationInfo(Request request, Response response) {
    ApplicationInfo result = super.getApplicationInfo(request, response);
    DocumentationInfo docInfo = new DocumentationInfo("Application for services on a dataset management in SITools2.");
    docInfo.setTitle("API documentation.");
    result.getDocumentations().add(docInfo);
    return result;
  }

}

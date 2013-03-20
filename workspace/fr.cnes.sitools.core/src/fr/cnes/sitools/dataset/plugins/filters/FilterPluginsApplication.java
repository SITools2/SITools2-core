/*******************************************************************************
 * Copyright 2011, 2012 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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
package fr.cnes.sitools.dataset.plugins.filters;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.ext.wadl.ApplicationInfo;
import org.restlet.ext.wadl.DocumentationInfo;
import org.restlet.routing.Router;

import fr.cnes.sitools.common.application.SitoolsApplication;
import fr.cnes.sitools.common.model.Category;

/**
 * Exposition of filters
 * 
 * @author AKKA
 * 
 */
public final class FilterPluginsApplication extends SitoolsApplication {

  /**
   * Plug-in Application of filters
   * 
   * @param context
   *          a restlet context
   */
  public FilterPluginsApplication(Context context) {
    super(context);
  }

  @Override
  public void sitoolsDescribe() {
    setCategory(Category.ADMIN);
    setName("FilterPluginsApplication");
    setDescription("Expose the classes of search filter plugins");
  }

  @Override
  public Restlet createInboundRoot() {

    Router router = new Router(getContext());

    router.attachDefault(FilterPluginsCollectionResource.class);
    router.attach("/{filterClass}", FilterPluginsResource.class);
    router.attach("/{filterClass}/{datasetId}", FilterPluginsResource.class);

    return router;
  }

  @Override
  public ApplicationInfo getApplicationInfo(Request request, Response response) {
    ApplicationInfo result = super.getApplicationInfo(request, response);
    DocumentationInfo docInfo = new DocumentationInfo("Filter plug-in application documentation in SITools2.");
    docInfo.setTitle("API documentation.");
    result.getDocumentations().add(docInfo);
    return result;
  }

}

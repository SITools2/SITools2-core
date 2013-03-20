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
package fr.cnes.sitools.dataset.plugins.converters;

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
 * COnverters exposition
 * 
 * @author AKKA Technologies
 */
public final class ConverterPluginsApplication extends SitoolsApplication {

  /**
   * Converter plug-in application
   * 
   * @param context
   *          RESTlet {@code Context}
   */
  public ConverterPluginsApplication(Context context) {
    super(context);
  }

  @Override
  public void sitoolsDescribe() {
    setCategory(Category.ADMIN);
    setName("ConverterPluginsApplication");
    setDescription("Exposition of converters classes");
  }

  @Override
  public Restlet createInboundRoot() {
    Router router = new Router(getContext());

    router.attachDefault(ConverterPluginsCollectionResource.class);
    router.attach("/{converterClass}", ConverterPluginsResource.class);
    router.attach("/{converterClass}/{datasetId}", ConverterPluginsResource.class);
    return router;
  }
  
  @Override
  public ApplicationInfo getApplicationInfo(Request request, Response response) {
    ApplicationInfo result = super.getApplicationInfo(request, response);
    DocumentationInfo docInfo = new DocumentationInfo("Converters plug-in management.");
    docInfo.setTitle("API documentation.");
    result.getDocumentations().add(docInfo);
    return result;
  }

}

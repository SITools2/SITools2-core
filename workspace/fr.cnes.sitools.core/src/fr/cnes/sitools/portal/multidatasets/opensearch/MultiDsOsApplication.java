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
package fr.cnes.sitools.portal.multidatasets.opensearch;

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
 * Application for managing MultiDatasets opensearch
 * 
 * @author AKKA Technologies
 * 
 * @version
 * 
 */
public class MultiDsOsApplication extends SitoolsApplication {
  /**
   * Default constructor
   * 
   * @param context
   *          The context parameter
   */
  public MultiDsOsApplication(Context context) {
    super(context);
  }

  @Override
  public void sitoolsDescribe() {
    setName("MultiDatasetsOpensearchApplication");
    setDescription("Application used to query over multiple opensearch indexes");
    setCategory(Category.USER);
  }

  @Override
  public Restlet createInboundRoot() {

    Router router = new Router(getContext());

    router.attachDefault(MutliDsOsCollectionResource.class);
    router.attach("/list", MutliDsOsCollectionResource.class);
    router.attach("/search", MutliDsOsResource.class);
    router.attach("/suggest", MutliDsOsResource.class);

    return router;
  }
  
  @Override
  public ApplicationInfo getApplicationInfo(Request request, Response response) {
    ApplicationInfo result = super.getApplicationInfo(request, response);
    DocumentationInfo docInfo = new DocumentationInfo("Multi-dataset opensearch application for Sitools2");
    docInfo.setTitle("API documentation.");
    result.getDocumentations().add(docInfo);
    return result;
  }

}

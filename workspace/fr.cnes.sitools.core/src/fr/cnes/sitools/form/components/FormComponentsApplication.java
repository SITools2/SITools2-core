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
package fr.cnes.sitools.form.components;

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
 * Application for managing FormComponent
 * 
 * Dependencies : DataSets columns references FormComponent notions.
 * 
 * @author AKKA
 * 
 */
public final class FormComponentsApplication extends SitoolsApplication {

  /** Store */
  private FormComponentsStoreInterface store = null;

  /**
   * Constructor
   * 
   * @param context
   *          Restlet Host Context
   */
  @SuppressWarnings("unchecked")
  public FormComponentsApplication(Context context) {
    super(context);
    this.store = (FormComponentsStoreInterface) context.getAttributes().get(ContextAttributes.APP_STORE);
  }

  @Override
  public void sitoolsDescribe() {
    setCategory(Category.USER);
    setName("FormComponentApplication");
    setDescription("Management of GUI components for forms."
        + "-> Administrator user should have all rights on this application"
        + "-> public user must have GET authorization in order to get the list of form components in the client-user interface");
  }

  @Override
  public Restlet createInboundRoot() {

    Router router = new Router(getContext());

    router.attachDefault(FormComponentsCollectionResource.class);
    router.attach("/{formComponentId}", FormComponentsResource.class);

    return router;
  }

  /**
   * Gets the store value
   * 
   * @return the store
   */
  public FormComponentsStoreInterface getStore() {
    return store;
  }

  @Override
  public ApplicationInfo getApplicationInfo(Request request, Response response) {
    ApplicationInfo result = super.getApplicationInfo(request, response);
    DocumentationInfo docInfo = new DocumentationInfo("Feeds application for RSS feeds in SITools2.");
    docInfo.setTitle("API documentation.");
    result.getDocumentations().add(docInfo);
    return result;
  }

}

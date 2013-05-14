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
package fr.cnes.sitools.dataset.converter;

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
import fr.cnes.sitools.dataset.converter.model.ConverterChainedModel;

/**
 * Exposition of converters
 * 
 * @author AKKA Technologies
 */
public final class ConverterApplication extends SitoolsApplication {

  /** Store */
  private SitoolsStore<ConverterChainedModel> store = null;

  /**
   * Application associated to a converter
   * 
   * @param context
   *          application context
   */
  @SuppressWarnings("unchecked")
  public ConverterApplication(Context context) {
    super(context);
    this.store = (SitoolsStore<ConverterChainedModel>) context.getAttributes().get(ContextAttributes.APP_STORE);
  }

  @Override
  public void sitoolsDescribe() {
    setCategory(Category.ADMIN);
    setName("ConverterApplication");
    setDescription("Act on converters defined for a dataset.");
  }

  @Override
  public Restlet createInboundRoot() {
    Router router = new Router(getContext());

    // add a new converter to the converter list, get the list, delete the list
    router.attachDefault(ConverterChainedResource.class);
    router.attach("/notify", ConverterNotificationResource.class);
    // get, modify or delete a converter in the list
    router.attach("/{converterId}", ConverterResource.class);

    // Change the Converter status
    router.attach("/{converterId}/start", ConverterActivationResource.class);
    router.attach("/{converterId}/stop", ConverterActivationResource.class);

    return router;
  }

  /**
   * Gets the store value
   * 
   * @return the store
   */
  public SitoolsStore<ConverterChainedModel> getStore() {
    return store;
  }

  @Override
  public ApplicationInfo getApplicationInfo(Request request, Response response) {
    ApplicationInfo result = super.getApplicationInfo(request, response);
    DocumentationInfo docInfo = new DocumentationInfo("Converters chained management.");
    docInfo.setTitle("API documentation.");
    result.getDocumentations().add(docInfo);
    return result;
  }

}

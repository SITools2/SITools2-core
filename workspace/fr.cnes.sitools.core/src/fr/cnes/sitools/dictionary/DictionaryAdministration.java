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
package fr.cnes.sitools.dictionary;

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
import fr.cnes.sitools.dictionary.model.Dictionary;
import fr.cnes.sitools.notification.business.NotifierFilter;

/**
 * Application for managing dictionary
 * 
 * Dependencies : DataSets columns references Dictionary notions.
 * 
 * @author AKKA
 * 
 */
public final class DictionaryAdministration extends SitoolsApplication {

  /** Store */
  private SitoolsStore<Dictionary> store = null;

  /**
   * Constructor
   * 
   * @param context
   *          Restlet Host context
   */
  @SuppressWarnings("unchecked")
  public DictionaryAdministration(Context context) {
    super(context);
    this.store = (SitoolsStore<Dictionary>) context.getAttributes().get(ContextAttributes.APP_STORE);
  }

  @Override
  public void sitoolsDescribe() {
    setCategory(Category.ADMIN);
    setName("DictionaryApplication");
    setDescription("Dictionary management");
  }

  @Override
  public Restlet createInboundRoot() {

    Router router = new Router(getContext());

    router.attachDefault(DictionaryCollectionResource.class);
    router.attach("/{dictionaryId}", DictionaryResource.class);
    router.attach("/{dictionaryId}/concepts/{conceptId}", ConceptResource.class);

    Filter filter = new NotifierFilter(getContext());
    filter.setNext(router);
    return filter;
  }

  /**
   * Gets the store value
   * 
   * @return the store
   */
  public SitoolsStore<Dictionary> getStore() {
    return store;
  }

  @Override
  public ApplicationInfo getApplicationInfo(Request request, Response response) {
    ApplicationInfo result = super.getApplicationInfo(request, response);
    DocumentationInfo docInfo = new DocumentationInfo("Dictionary application documentation in SITools2.");
    docInfo.setTitle("API documentation.");
    result.getDocumentations().add(docInfo);
    return result;
  }

}

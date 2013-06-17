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
package fr.cnes.sitools.dataset.opensearch;

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
import fr.cnes.sitools.dataset.opensearch.model.Opensearch;
import fr.cnes.sitools.feeds.FeedsStoreXML;

/**
 * Application for managing OpenSearch configuration
 * 
 * Dependencies : DataSets, SolR
 * 
 * @author AKKA
 */
public final class OpenSearchApplication extends SitoolsApplication {

  /** Store */
  private SitoolsStore<Opensearch> store = null;

  /** Other store needed for feeds definition */
  private FeedsStoreXML storeFeeds = null;
  
  /** If the indexation has been cancelled */
  private boolean cancelled = false;

  /**
   * Constructor
   * 
   * @param context
   *          RESTlet Host Context
   */
  @SuppressWarnings("unchecked")
  public OpenSearchApplication(Context context) {
    super(context);
    this.store = (SitoolsStore<Opensearch>) context.getAttributes().get(ContextAttributes.APP_STORE);
    this.storeFeeds = (FeedsStoreXML) context.getAttributes().get("APP_STORE_FEEDS");
  }

  @Override
  public void sitoolsDescribe() {
    setCategory(Category.ADMIN);
    setName("OpensearchApplication");
    setDescription("Opensearch configuration");
  }

  @Override
  public Restlet createInboundRoot() {

    Router router = new Router(getContext());

    router.attachDefault(OpenSearchResource.class);
    router.attach("/start", OpenSearchActionResource.class);
    router.attach("/stop", OpenSearchActionResource.class);
    router.attach("/refresh", OpenSearchActionResource.class);
    router.attach("/cancel", OpenSearchActionResource.class);
    
    
    router.attach("/notify", OpensearchNotificationResource.class);
    

    // for riap - TODO modifier l'attachement des clients riap (observer)
    router.attach("/{opensearchId}", OpenSearchResource.class);
    return router;
  }

  /**
   * Gets the store value
   * 
   * @return the store
   */
  public SitoolsStore<Opensearch> getStore() {
    return store;
  }

  /**
   * Gets the StoreFeeds value
   * 
   * @return the storeFeeds
   */
  public FeedsStoreXML getStoreFeed() {
    return this.storeFeeds;
  }
  
  
  @Override
  public ApplicationInfo getApplicationInfo(Request request, Response response) {
    ApplicationInfo result = super.getApplicationInfo(request, response);
    DocumentationInfo docInfo = new DocumentationInfo("OpenSearch application management.");
    docInfo.setTitle("API documentation.");
    result.getDocumentations().add(docInfo);
    return result;
  }

  /**
   * Sets the value of cancelled
   * @param cancelled the cancelled to set
   */
  public void setCancelled(boolean cancelled) {
    this.cancelled = cancelled;
  }

  /**
   * Gets the cancelled value
   * @return the cancelled
   */
  public boolean isCancelled() {
    return cancelled;
  }

}

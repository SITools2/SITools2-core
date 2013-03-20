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
package fr.cnes.sitools.feeds;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.ext.wadl.ApplicationInfo;
import org.restlet.ext.wadl.DocumentationInfo;
import org.restlet.routing.Router;

import fr.cnes.sitools.common.application.SitoolsApplication;
import fr.cnes.sitools.common.model.Category;
import fr.cnes.sitools.common.store.SitoolsStore;
import fr.cnes.sitools.feeds.model.FeedModel;

/**
 * Application for managing projects Dependencies : DataSets
 * 
 * TODO Constructor with all generic security configuration (Authenticator informations) Configure security application
 * by spring or from server main
 * 
 * @author AKKA
 * 
 */
public class FeedsApplication extends SitoolsApplication {

  /** Store */
  private SitoolsStore<FeedModel> store = null;

  /**
   * Constructor
   * 
   * @param context
   *          Restlet Host Context
   * @param store
   *          FeedsStore
   * 
   */
  public FeedsApplication(Context context, SitoolsStore<FeedModel> store) {
    super(context);
    this.store = store;
    // Description de cette instance dataSetId d'application.
    sitoolsDescribe();
  }

  @Override
  public void sitoolsDescribe() {
    setCategory(Category.ADMIN);
    setName("FeedsApplication");
    setDescription("Feeds management.");
  }

  @Override
  public final Restlet createInboundRoot() {

    Router router = new Router(getContext());

    router.attachDefault(FeedsCollectionResource.class);
    router.attach("/{feedsId}", FeedsAdminResource.class);
    router.attach("/{feedsId}/notify", FeedsNotificationResource.class);

    return router;
  }

  @Override
  public final ApplicationInfo getApplicationInfo(Request request, Response response) {
    ApplicationInfo result = super.getApplicationInfo(request, response);
    DocumentationInfo docInfo = new DocumentationInfo("Feeds application for RSS feeds in SITools2.");
    docInfo.setTitle("API documentation.");
    result.getDocumentations().add(docInfo);
    return result;
  }

  /**
   * Gets the store value
   * 
   * @return the store
   */
  public final SitoolsStore<FeedModel> getStore() {
    return store;
  }

  /**
   * Sets the value of store
   * 
   * @param store
   *          the store to set
   */
  public final void setStore(SitoolsStore<FeedModel> store) {
    this.store = store;
  }

}

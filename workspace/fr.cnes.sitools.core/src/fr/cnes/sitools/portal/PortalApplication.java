    /*******************************************************************************
 * Copyright 2010-2016 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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
package fr.cnes.sitools.portal;

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
import fr.cnes.sitools.feeds.FeedsClientResource;

/**
 * Application for managing Portal
 * 
 * Dependencies : DataSets columns references Portal notions.
 * 
 * @author AKKA
 * 
 */
public final class PortalApplication extends SitoolsApplication {

  /** Store */
  private PortalStoreInterface store = null;

  /**
   * Constructor
   * 
   * @param context
   *          the restlet context must contain a <code>PortalStore</code> attribute for APP_STORE key
   */
  public PortalApplication(Context context) {
    super(context);
    this.store = (PortalStoreInterface) context.getAttributes().get(ContextAttributes.APP_STORE);

    if (this.store == null) {
      getLogger().warning("Missing APP_STORE in PortalApplication context");
    }

  }

  @Override
  public void sitoolsDescribe() {
    setCategory(Category.USER);
    setName("PortalApplication");
    setDescription("Portal definition application");
  }

  @Override
  public Restlet createInboundRoot() {

    Router router = new Router(getContext());

    // Attach the resources to the router
    router.attachDefault(PortalResource.class);

    router.attach("/projects", PortalProjectsCollectionResource.class, Router.MODE_FIRST_MATCH);
    router.attach("/projects/{projectId}", PortalProjectResource.class);

    router.attach("/{portalId}/clientFeeds/{feedsId}", FeedsClientResource.class);
    router.attach("/{portalId}/listFeeds", PortalListFeedsResource.class);

    return router;
  }

  /**
   * Gets the store value

   * @return the store
   */
  public PortalStoreInterface getStore() {
    return store;
  }

  @Override
  public ApplicationInfo getApplicationInfo(Request request, Response response) {
    ApplicationInfo result = super.getApplicationInfo(request, response);
    DocumentationInfo docInfo = new DocumentationInfo("Portal application to get portal, project and feeds definitions");
    docInfo.setTitle("API documentation.");
    result.getDocumentations().add(docInfo);
    return result;
  }

}

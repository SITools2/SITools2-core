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
package fr.cnes.sitools.applications;

import java.io.File;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.ext.wadl.ApplicationInfo;
import org.restlet.resource.Directory;
import org.restlet.routing.Router;

import fr.cnes.sitools.client.ClientSiteMapResource;
import fr.cnes.sitools.client.ProjectIndex;
import fr.cnes.sitools.common.application.StaticWebApplication;
import fr.cnes.sitools.common.model.Category;
import fr.cnes.sitools.project.ProjectStoreInterface;
import fr.cnes.sitools.proxy.DirectoryProxy;
import fr.cnes.sitools.server.Consts;

/**
 * Application Web Client User.
 * 
 * @author jp.boignard (AKKA Technologies)
 * 
 */
public final class ClientUserApplication extends StaticWebApplication {

  /**
   * Project Store
   */
  private ProjectStoreInterface store = null;

  /**
   * ProjectIndex URL
   */
  private String projectIndexUrl;

  /**
   * Constructor with folder of exposed files
   * 
   * @param context
   *          Restlet {@code Context}
   * @param appPath
   *          Directory
   * @param baseUrl
   *          public domain name when list files.
   */
  public ClientUserApplication(final Context context, final String appPath, final String baseUrl) {
    super(context, appPath, baseUrl);
    projectIndexUrl = getSettings().getRootDirectory() + getSettings().getString(Consts.TEMPLATE_DIR)
        + getSettings().getString("Starter.client-user.projectIndex");
    File projectIndexFile = new File(projectIndexUrl);
    if (projectIndexFile == null || !projectIndexFile.exists()) {
      getLogger().severe("Template file for Project/index.html file not found :" + projectIndexUrl);
    }
    setUserAuthenticationNeeded(false);

  }

  /**
   * Gets the store value.
   * 
   * @return the store
   */
  public ProjectStoreInterface getStore() {
    return this.store;
  }

  /**
   * Sets the value of store.
   * 
   * @param stor
   *          the store to set
   */
  public void setStore(final ProjectStoreInterface stor) {
    this.store = stor;
  }

  @Override
  public void sitoolsDescribe() {
    setCategory(Category.USER);
    setName("client-user");
    // setDescription("Web client application for SITools users");
    setDescription("This application is used by users" + "-> to access the Web client application"
        + "-> to access the portal page" + "-> to access the desktop page");
  }

  @Override
  public Restlet createInboundRoot() {

    // Create a router
    Router router = new Router(getContext());
    
    // attach the portal resource to client-user
//    router.attach("/", ProjectIndex.class);
    
    router.attach("/index.html", ProjectIndex.class);

//    String portalTarget = getSettings().getPublicHostDomain() + getSettings().getString(Consts.APP_URL) + getSettings().getString(Consts.APP_CLIENT_PORTAL_URL)
//        + "/index.html";
//    Redirector redirector = new Redirector(getContext(), portalTarget, Redirector.MODE_CLIENT_PERMANENT);
//    // redirect to the portal to have the same behavior as version 2.x  
//    router.attach("/", redirector).setMatchingMode(Template.MODE_EQUALS);
//    // redirect to the portal to have the same behavior as version 2.x
//    router.attach("/index.html", redirector).getTemplate().setMatchingMode(Template.MODE_EQUALS);
    
    router.attach("/siteMap", ClientSiteMapResource.class);

    Directory directory = new DirectoryProxy(getContext(), "file:///" + getAppPath(), getAttachementRef());
    directory.setDeeplyAccessible(true);
    directory.setListingAllowed(true);
    directory.setModifiable(false);
    directory.setName("Client-user directory");
    directory.setDescription("Exposes all the client user files");
    router.attach("/", directory);

    return router;
  }

  /**
   * Gets the projectIndexUrl value
   * 
   * @return the projectIndexUrl
   */
  public String getProjectIndexUrl() {
    return projectIndexUrl;
  }

  @Override
  public ApplicationInfo getApplicationInfo(Request request, Response response) {
    ApplicationInfo appInfo = super.getApplicationInfo(request, response);
    appInfo.setDocumentation("Web client application for SITools2 users.");
    return appInfo;
  }

}

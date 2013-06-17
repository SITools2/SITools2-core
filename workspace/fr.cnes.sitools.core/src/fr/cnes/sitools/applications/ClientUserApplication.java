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
package fr.cnes.sitools.applications;

import java.io.File;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.ext.wadl.ApplicationInfo;
import org.restlet.resource.Directory;
import org.restlet.routing.Router;
import org.restlet.routing.Template;

import fr.cnes.sitools.client.ClientSiteMapResource;
import fr.cnes.sitools.client.PortalIndex;
import fr.cnes.sitools.client.ProjectIndex;
import fr.cnes.sitools.common.application.StaticWebApplication;
import fr.cnes.sitools.common.model.Category;
import fr.cnes.sitools.common.store.SitoolsStore;
import fr.cnes.sitools.project.model.Project;
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
  private SitoolsStore<Project> store = null;

  /**
   * portalIndex URL
   */
  private String portalIndexUrl;

  /**
   * ProjectIndex URL
   */
  private String projectIndexUrl;

  /**
   * Identifier of the portal
   */
  private String portalId;

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
    // application settings
    portalId = getSettings().getString("Portal.id");

    portalIndexUrl = getSettings().getRootDirectory() + getSettings().getString(Consts.TEMPLATE_DIR)
        + getSettings().getString("Starter.client-user.portalIndex");

    File portalIndexFile = new File(portalIndexUrl);
    if (portalIndexFile == null || !portalIndexFile.exists()) {
      getLogger().severe("Template file for Portal/index.html not found :" + portalIndexUrl);
    }
    projectIndexUrl = getSettings().getRootDirectory() + getSettings().getString(Consts.TEMPLATE_DIR)
        + getSettings().getString("Starter.client-user.projectIndex");
    File projectIndexFile = new File(projectIndexUrl);
    if (projectIndexFile == null || !projectIndexFile.exists()) {
      getLogger().severe("Template file for Project/index.html file not found :" + projectIndexUrl);
    }

  }

  /**
   * Gets the store value.
   * 
   * @return the store
   */
  public SitoolsStore<Project> getStore() {
    return this.store;
  }

  /**
   * Sets the value of store.
   * 
   * @param stor
   *          the store to set
   */
  public void setStore(final SitoolsStore<Project> stor) {
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
    router.attach("/", PortalIndex.class);

    // Redirections/Raccourcis sur certains répertoires virtuels ...
    router.attach("/index.html", PortalIndex.class).getTemplate().setMatchingMode(Template.MODE_EQUALS);

    router.attach("/{projectName}/project-index.html", ProjectIndex.class);
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
   * Gets the portalIndexUrl value
   * 
   * @return the portalIndexUrl
   */
  public String getPortalIndexUrl() {
    return portalIndexUrl;
  }

  /**
   * Gets the projectIndexUrl value
   * 
   * @return the projectIndexUrl
   */
  public String getProjectIndexUrl() {
    return projectIndexUrl;
  }

  /**
   * Get the portal identifier
   * 
   * @return the portal identifier
   */
  public String getPortalId() {
    return portalId;
  }

  @Override
  public ApplicationInfo getApplicationInfo(Request request, Response response) {
    ApplicationInfo appInfo = super.getApplicationInfo(request, response);
    appInfo.setDocumentation("Web client application for SITools2 users.");
    return appInfo;
  }

}

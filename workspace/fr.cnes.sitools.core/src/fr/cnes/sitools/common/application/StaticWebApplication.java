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
package fr.cnes.sitools.common.application;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

import org.restlet.Context;
import org.restlet.Restlet;
import org.restlet.data.MediaType;
import org.restlet.resource.Directory;
import org.restlet.routing.Router;
import org.restlet.service.MetadataService;

import fr.cnes.sitools.proxy.DirectoryProxy;

/**
 * A static web application consists in publishing a directory content on the web.
 * 
 * @author jp.boignard (AKKA Technologies)
 * 
 */
public abstract class StaticWebApplication extends SitoolsApplication {
  /**
   * Directory.
   */
  private String appPath = "";

  /**
   * public domain used when listing files of Directory.
   */
  private String baseUrl = "";

  /**
   * Constructor with folder of exposed files.
   * 
   * @param context
   *          RESTlet server context
   * @param appliPath
   *          Directory
   * @param baseURL
   *          public domain name when list files.
   * @param description
   *          description for the application
   */
  public StaticWebApplication(Context context, String appliPath, String baseURL, String description) {
    super(context);
    try {
      File appDir = new File(appliPath);
      this.appPath = appDir.getCanonicalPath().replace("\\", "/");
    }
    catch (IOException e) {
      Context.getCurrentLogger().severe("Chemin d'application incorrect");
      getLogger().log(Level.INFO, null, e);
    }
    this.baseUrl = baseURL;
    setDescription(description);
    
    MetadataService ms = getMetadataService();
    ms.addCommonExtensions();
    ms.addExtension("properties", MediaType.TEXT_PLAIN);
    ms.setEnabled(true);
    try {
      ms.start();
    }
    catch (Exception e) {
      getLogger().warning(e.getMessage());
    }
  }

  /**
   * Constructor.
   * 
   * @param context
   *          context
   * @param appliPath
   *          application path
   * @param baseURL
   *          base URL
   */
  public StaticWebApplication(Context context, String appliPath, String baseURL) {
    super(context);
    try {
      File appDir = new File(appliPath);
      this.appPath = appDir.getCanonicalPath().replace("\\", "/");
    }
    catch (IOException e) {
      Context.getCurrentLogger().severe("Chemin d'application incorrect");
      getLogger().log(Level.INFO, null, e);
    }
    this.baseUrl = baseURL;
    
    MetadataService ms = getMetadataService();
    ms.addCommonExtensions();
    ms.addExtension("properties", MediaType.TEXT_PLAIN);
    ms.setEnabled(true);
    try {
      ms.start();
    }
    catch (Exception e) {
      getLogger().warning(e.getMessage());
    }

    getLogger().info(this.getName() + " URL:" + this.getBaseUrl() + "PATH:" + appPath);
  }

  /**
   * Gets the directory path.
   * 
   * @return String
   */
  public String getAppPath() {
    return appPath;
  }

  /**
   * Gets the public base URL (domain name).
   * 
   * @return String
   */
  public String getBaseUrl() {
    return baseUrl;
  }

  @Override
  public Restlet createInboundRoot() {

    // Create a router
    Router router = new Router(getContext());

    // ----------------------------------------------------------------
    // FILES
    Directory directory = new DirectoryProxy(getContext(), "file:///" + appPath, baseUrl);
    directory.setDeeplyAccessible(true);
    directory.setListingAllowed(true);
    directory.setModifiable(false);
    directory.setName(getName() + " Directory");

    router.attachDefault(directory);

    // ----------------------------------------------------------------
    // Return the root router
    return router;

  }

}

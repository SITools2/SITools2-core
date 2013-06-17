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
import org.restlet.data.LocalReference;
import org.restlet.ext.wadl.ApplicationInfo;
import org.restlet.routing.Router;

import fr.cnes.sitools.common.application.SitoolsApplication;
import fr.cnes.sitools.common.model.Category;
import fr.cnes.sitools.proxy.DirectoryProxy;

/**
 * Application Web Developer to expose Javadoc and code audit.
 * 
 * @author jp.boignard (AKKA Technologies)
 * 
 */
public final class TemporaryFolderApplication extends SitoolsApplication {

  /** directory path for upload files */
  private String appPath;

  /**
   * Constructor with folder of files exposed
   * 
   * @param context
   *          Restlet {@code Context}
   * @param appPath
   *          Directory
   */
  public TemporaryFolderApplication(Context context, String appPath) {
    super(context);
    this.appPath = appPath;

    // creation du dossier temporaire
    File file = new File(appPath);
    if (!file.exists()) {
      file.setWritable(true);
      file.setReadable(true);
      file.mkdir();
    }
  }

  @Override
  public void sitoolsDescribe() {
    setCategory(Category.USER);
    setName("TemporaryFolderApplication");
    setDescription("Application to access a temporary folder on the server\n"
        + "-> administrator user can have GET/POST/PUT/DELETE authorizations to access, add or delete files or folder\n"
        + "-> public can have GET authorization to access the folders\n"
        + "In order to download files using the ExtJs client, the HTTP-BASIC credentials can be sended in the cookie");
  }

  @Override
  public Restlet createInboundRoot() {
    Router router = new Router(getContext());

    DirectoryProxy dp = new DirectoryProxy(getContext(), LocalReference.createFileReference(appPath),
        getAttachementRef());
    dp.setNegotiatingContent(true);
    this.getMetadataService().addCommonExtensions();
    dp.setDeeplyAccessible(true);
    dp.setListingAllowed(true);
    dp.setModifiable(true);
    this.getTunnelService().setEnabled(true);
    this.getTunnelService().setMethodTunnel(true);
    this.getTunnelService().setMethodParameter("method");
    router.attachDefault(dp);
    return router;
  }

  @Override
  public ApplicationInfo getApplicationInfo(Request request, Response response) {
    ApplicationInfo appInfo = super.getApplicationInfo(request, response);
    appInfo.setDocumentation("Application to access a temporary folder on the server.");
    return appInfo;
  }

}

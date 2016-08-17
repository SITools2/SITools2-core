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
package fr.cnes.sitools.applications;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.ext.wadl.ApplicationInfo;
import org.restlet.resource.Directory;
import org.restlet.routing.Router;

import fr.cnes.sitools.common.application.StaticWebApplication;
import fr.cnes.sitools.common.exception.SitoolsException;
import fr.cnes.sitools.common.model.Category;
import fr.cnes.sitools.proxy.DirectoryProxy;

/**
 * PublicApplication to expose client extensions files.
 * 
 * @author m.gond (AKKA Technologies)
 * 
 */
public final class ClientExtensionApplication extends StaticWebApplication {

  /**
   * Constructor.
   * 
   * @param context
   *          Restlet {@code Context}
   * @param appPath
   *          location of Directory to be exposed
   * @param baseUrl
   *          public URL for listing files of Directory
   * @throws SitoolsException
   *           if the challengeToken is null
   */
  public ClientExtensionApplication(Context context, String appPath, String baseUrl) throws SitoolsException {
    super(context, appPath, baseUrl);
  }

  @Override
  public void sitoolsDescribe() {
    setCategory(Category.PUBLIC);
    setName("client-extension");
    setDescription("web client application for extensions resources used by client user and admin application "
        + "-> Administrator must have all authorizations on this application\n"
        + "-> Public user must have at least GETauthorizations on this application");
  }

  @Override
  public Restlet createInboundRoot() {

    Router router = (Router) super.createInboundRoot();

    // Create sub - restlets / applications

    Directory directory = new DirectoryProxy(getContext(), "file:///" + getAppPath(), getAttachementRef());
    directory.setDeeplyAccessible(true);
    directory.setListingAllowed(true);
    directory.setModifiable(false);
    directory.setName("Client-extensions directory");
    directory.setDescription("Exposes all the client extensions files");
    router.attach("/", directory);
    
    return router;
  }

  @Override
  public ApplicationInfo getApplicationInfo(Request request, Response response) {
    ApplicationInfo appInfo = super.getApplicationInfo(request, response);
    appInfo.setDocumentation("Web client application for public resources used by other SITools2 client applications.");
    return appInfo;
  }

}

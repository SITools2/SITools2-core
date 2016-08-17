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

import java.io.File;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.engine.Engine;
import org.restlet.ext.wadl.ApplicationInfo;
import org.restlet.resource.Directory;
import org.restlet.routing.Router;

import fr.cnes.sitools.common.application.StaticWebApplication;
import fr.cnes.sitools.common.exception.SitoolsException;
import fr.cnes.sitools.common.model.Category;
import fr.cnes.sitools.proxy.DirectoryProxy;
import fr.cnes.sitools.server.Consts;

/**
 * PublicApplication to expose commons files and cots.
 * 
 * @author jp.boignard (AKKA Technologies)
 * 
 */
public final class ClientPublicApplication extends StaticWebApplication {

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
  public ClientPublicApplication(Context context, String appPath, String baseUrl) throws SitoolsException {
    super(context, appPath, baseUrl);
  }

  @Override
  public void sitoolsDescribe() {
    setCategory(Category.PUBLIC);
    setName("client-public");
    setDescription("web client application for public resources used by other sitools client applications "
        + "-> Administrator must have all authorizations on this application\n"
        + "-> Public user must have at least GET and PUT authorizations on this application\n"
        + "PUT authorization is used to reset User password");
  }

  @Override
  public Restlet createInboundRoot() {

    Router router = (Router) super.createInboundRoot();

    // Create sub - restlets / applications

    // ----------------------------------------------------------------
    // FILES
    String commonPath = new File(getAppPath() + getSettings().getString(Consts.APP_CLIENT_PUBLIC_COMMON_PATH))
        .getAbsolutePath().replace("\\", "/");
    Engine.getLogger(this.getClass().getName()).info(Consts.APP_CLIENT_PUBLIC_COMMON_PATH + ":" + commonPath);
    
    Directory commonDir = new DirectoryProxy(getContext(), "file:///" + commonPath, getBaseUrl()
        + getSettings().getString(Consts.APP_CLIENT_PUBLIC_COMMON_URL));
    commonDir.setDeeplyAccessible(true);
    commonDir.setListingAllowed(true);
    commonDir.setModifiable(false);
    commonDir.setName("Client-public directoryProxy");
    commonDir.setDescription("Exposes all the client public files");
    router.attach(getSettings().getString(Consts.APP_CLIENT_PUBLIC_COMMON_URL), commonDir); // .setMatchingMode(Router.MODE_FIRST_MATCH);

    
    
    String cotsPath = new File(getAppPath() + getSettings().getString(Consts.APP_CLIENT_PUBLIC_COTS_PATH))
        .getAbsolutePath().replace("\\", "/");
    Engine.getLogger(this.getClass().getName()).info(Consts.APP_CLIENT_PUBLIC_COTS_PATH + ":" + cotsPath);
    Directory cotsDir = new DirectoryProxy(getContext(), "file:///" + cotsPath, getBaseUrl()
        + getSettings().getString(Consts.APP_CLIENT_PUBLIC_COTS_URL));

    cotsDir.setDeeplyAccessible(true);
    cotsDir.setListingAllowed(true);
    cotsDir.setModifiable(false);
    cotsDir.setName("Cots directoryProxy");
    cotsDir.setDescription("Exposes all the cots files");
    router.attach(getSettings().getString(Consts.APP_CLIENT_PUBLIC_COTS_URL), cotsDir); // .setMatchingMode(Router.MODE_FIRST_MATCH);

    return router;
  }

  @Override
  public ApplicationInfo getApplicationInfo(Request request, Response response) {
    ApplicationInfo appInfo = super.getApplicationInfo(request, response);
    appInfo.setDocumentation("Web client application for public resources used by other SITools2 client applications.");
    return appInfo;
  }

}

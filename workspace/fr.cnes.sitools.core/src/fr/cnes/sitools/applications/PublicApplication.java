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
import java.util.logging.Logger;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.ext.wadl.ApplicationInfo;
import org.restlet.resource.Directory;
import org.restlet.routing.Extractor;
import org.restlet.routing.Redirector;
import org.restlet.routing.Router;
import org.restlet.security.Authenticator;

import fr.cnes.sitools.client.ProxyRestlet;
import fr.cnes.sitools.client.SitoolsVersionResource;
import fr.cnes.sitools.common.application.StaticWebApplication;
import fr.cnes.sitools.common.model.Category;
import fr.cnes.sitools.login.LoginDetailsResource;
import fr.cnes.sitools.login.LoginResource;
import fr.cnes.sitools.login.ResetPasswordResource;
import fr.cnes.sitools.proxy.DirectoryProxy;
import fr.cnes.sitools.security.EditUserProfileResource;
import fr.cnes.sitools.security.FindRoleResource;
import fr.cnes.sitools.security.authentication.AuthenticatorFactory;
import fr.cnes.sitools.server.Consts;

/**
 * PublicApplication to expose commons files and cots.
 * 
 * @author jp.boignard (AKKA Technologies)
 * 
 */
public final class PublicApplication extends StaticWebApplication {

  /**
   * Constructor.
   * 
   * @param context
   *          Restlet {@code Context}
   * @param appPath
   *          location of Directory to be exposed
   * @param baseUrl
   *          public URL for listing files of Directory
   */
  public PublicApplication(Context context, String appPath, String baseUrl) {
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
    Logger.getLogger(this.getName()).info(Consts.APP_CLIENT_PUBLIC_COMMON_PATH + ":" + commonPath);
    Directory commonDir = new DirectoryProxy(getContext().createChildContext(), "file:///" + commonPath, getBaseUrl()
        + getSettings().getString(Consts.APP_CLIENT_PUBLIC_COMMON_URL));
    commonDir.setDeeplyAccessible(true);
    commonDir.setListingAllowed(true);
    commonDir.setModifiable(false);
    commonDir.setName("Client-public directoryProxy");
    commonDir.setDescription("Exposes all the client public files");
    router.attach(getSettings().getString(Consts.APP_CLIENT_PUBLIC_COMMON_URL), commonDir); // .setMatchingMode(Router.MODE_FIRST_MATCH);

    String cotsPath = new File(getAppPath() + getSettings().getString(Consts.APP_CLIENT_PUBLIC_COTS_PATH))
        .getAbsolutePath().replace("\\", "/");
    Logger.getLogger(this.getName()).info(Consts.APP_CLIENT_PUBLIC_COTS_PATH + ":" + cotsPath);
    Directory cotsDir = new DirectoryProxy(getContext().createChildContext(), "file:///" + cotsPath, getBaseUrl()
        + getSettings().getString(Consts.APP_CLIENT_PUBLIC_COTS_URL));

    cotsDir.setDeeplyAccessible(true);
    cotsDir.setListingAllowed(true);
    cotsDir.setModifiable(false);
    cotsDir.setName("Cots directoryProxy");
    cotsDir.setDescription("Exposes all the cots files");
    router.attach(getSettings().getString(Consts.APP_CLIENT_PUBLIC_COTS_URL), cotsDir); // .setMatchingMode(Router.MODE_FIRST_MATCH);

    if (getAuthenticationRealm() != null) {
      // "Basic Public Login Test"
      Authenticator authenticator = AuthenticatorFactory.getAuthenticator(getContext(), true, getSettings()
          .getAuthenticationDOMAIN(), getAuthenticationRealm());
      authenticator.setNext(LoginResource.class);
      router.attach("/login", authenticator);

      // "Basic Public Login Test Mandatory to return credentials"
      Authenticator authenticatorMandatory = AuthenticatorFactory.getAuthenticator(getContext(), false, getSettings()
          .getAuthenticationDOMAIN(), getAuthenticationRealm());
      authenticatorMandatory.setNext(LoginResource.class);
      router.attach("/login-mandatory", authenticatorMandatory);
    }

    else {
      router.attach("/login", LoginResource.class);
      router.attach("/login-mandatory", LoginResource.class);
      router.attach("/login", LoginResource.class);

    }

    router.attach("/login-details", LoginDetailsResource.class);

    String target = getBaseUrl() + "{keywords}";

    Redirector redirector = new Redirector(getContext(), target, Redirector.MODE_CLIENT_TEMPORARY);

    Extractor extractor = new Extractor(getContext(), redirector);
    extractor.extractFromQuery("keywords", "kwd", true);

    // Attach the extractor to the router
    router.attach("/login-redirect", extractor);

    // Attach the version resource to get the version of Sitools
    router.attach("/version", SitoolsVersionResource.class);

    // Attach the resetPasswordResource to reset an user password
    router.attach("/resetPassword", ResetPasswordResource.class);

    // Attach the EditUserProfileResource to modified an user properties
    router.attach("/editProfile/{user}", EditUserProfileResource.class);

    router.attach("/userRole", FindRoleResource.class);

    router.attach("/proxy", new ProxyRestlet(getContext()));

    return router;
  }

  @Override
  public ApplicationInfo getApplicationInfo(Request request, Response response) {
    ApplicationInfo appInfo = super.getApplicationInfo(request, response);
    appInfo.setDocumentation("Web client application for public resources used by other SITools2 client applications.");
    return appInfo;
  }
}

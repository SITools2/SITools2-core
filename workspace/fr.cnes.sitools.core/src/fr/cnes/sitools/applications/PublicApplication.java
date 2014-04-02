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
import org.restlet.engine.Engine;
import org.restlet.ext.wadl.ApplicationInfo;
import org.restlet.resource.Directory;
import org.restlet.routing.Extractor;
import org.restlet.routing.Redirector;
import org.restlet.routing.Router;
import org.restlet.routing.Template;

import fr.cnes.sitools.client.ProxyRestlet;
import fr.cnes.sitools.client.ResetPasswordIndex;
import fr.cnes.sitools.client.SitoolsVersionResource;
import fr.cnes.sitools.client.UnlockAccountIndex;
import fr.cnes.sitools.common.application.StaticWebApplication;
import fr.cnes.sitools.common.exception.SitoolsException;
import fr.cnes.sitools.common.model.Category;
import fr.cnes.sitools.login.LoginDetailsResource;
import fr.cnes.sitools.login.LostPasswordResource;
import fr.cnes.sitools.login.ResetPasswordResource;
import fr.cnes.sitools.login.UnblacklistUserResource;
import fr.cnes.sitools.login.UnlockAccountResource;
import fr.cnes.sitools.proxy.DirectoryProxy;
import fr.cnes.sitools.security.EditUserProfileResource;
import fr.cnes.sitools.security.FindRoleResource;
import fr.cnes.sitools.security.captcha.CaptchaFilter;
import fr.cnes.sitools.security.captcha.CaptchaResource;
import fr.cnes.sitools.security.challenge.ChallengeToken;
import fr.cnes.sitools.server.Consts;

/**
 * PublicApplication to expose commons files and cots.
 * 
 * @author jp.boignard (AKKA Technologies)
 * 
 */
public final class PublicApplication extends StaticWebApplication {

  /** The challengeToken. */
  private ChallengeToken challengeToken;

  /** The resetPassword index page url. */
  private String resetPasswordIndexUrl;

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
  public PublicApplication(Context context, String appPath, String baseUrl) throws SitoolsException {
    super(context, appPath, baseUrl);

    challengeToken = (ChallengeToken) context.getAttributes().get("Security.challenge.ChallengeTokenContainer");
    if (challengeToken == null) {
      throw new SitoolsException("ChallengeToken is null");
    }

    // Application settings

    resetPasswordIndexUrl = getSettings().getRootDirectory() + getSettings().getString(Consts.TEMPLATE_DIR)
        + getSettings().getString("Starter.resetPassword.resetPasswordIndex");

    File resetPasswordIndexFile = new File(resetPasswordIndexUrl);
    if (resetPasswordIndexFile == null || !resetPasswordIndexFile.exists()) {
      getLogger().warning("Template file for resetPassword/index.html not found :" + resetPasswordIndexUrl);
    }

  }

  @Override
  public void sitoolsDescribe() {
    setCategory(Category.USER);
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
    Engine.getLogger(this.getClass().getName()).info(Consts.APP_CLIENT_PUBLIC_COTS_PATH + ":" + cotsPath);
    Directory cotsDir = new DirectoryProxy(getContext().createChildContext(), "file:///" + cotsPath, getBaseUrl()
        + getSettings().getString(Consts.APP_CLIENT_PUBLIC_COTS_URL));

    cotsDir.setDeeplyAccessible(true);
    cotsDir.setListingAllowed(true);
    cotsDir.setModifiable(false);
    cotsDir.setName("Cots directoryProxy");
    cotsDir.setDescription("Exposes all the cots files");
    router.attach(getSettings().getString(Consts.APP_CLIENT_PUBLIC_COTS_URL), cotsDir); // .setMatchingMode(Router.MODE_FIRST_MATCH);

    router.attach("/login-details", LoginDetailsResource.class);

    String target = getBaseUrl() + "{keywords}";

    Redirector redirector = new Redirector(getContext(), target, Redirector.MODE_CLIENT_TEMPORARY);

    Extractor extractor = new Extractor(getContext(), redirector);
    extractor.extractFromQuery("keywords", "kwd", true);

    // Attach the extractor to the router
    router.attach("/login-redirect", extractor);

    // Attach the version resource to get the version of Sitools
    router.attach("/version", SitoolsVersionResource.class);

    // Attach the LostPasswordResource to ask for another password
    router.attach("/lostPassword", LostPasswordResource.class);

    // Attach the LostPasswordResource to ask for another password
    router.attach("/unblacklist", UnblacklistUserResource.class);

    // Attach the resetPasswordResource to reset an user password
    CaptchaFilter captchaFilterResetPwd = new CaptchaFilter(getContext());
    captchaFilterResetPwd.setNext(ResetPasswordResource.class);
    router.attach("/resetPassword", captchaFilterResetPwd);

    // Attach the resetPasswordResource to reset an user password
    CaptchaFilter captchaFilterUnBlacklist = new CaptchaFilter(getContext());
    captchaFilterUnBlacklist.setNext(UnlockAccountResource.class);
    router.attach("/unlockAccount", captchaFilterUnBlacklist);

    // Attach the EditUserProfileResource to modified an user properties
    router.attach("/editProfile/{user}", EditUserProfileResource.class);

    router.attach("/userRole", FindRoleResource.class);

    router.attach("/proxy", new ProxyRestlet(getContext()));

    // Attach index pages to reset password and to unlock account
    router.attach("/resetPassword/index.html", ResetPasswordIndex.class).getTemplate()
        .setMatchingMode(Template.MODE_EQUALS);

    router.attach("/unlockAccount/index.html", UnlockAccountIndex.class).getTemplate()
        .setMatchingMode(Template.MODE_EQUALS);

    // Captcha resource for unlock account and reset password resource
    router.attach("/captcha", CaptchaResource.class);

    return router;
  }

  @Override
  public ApplicationInfo getApplicationInfo(Request request, Response response) {
    ApplicationInfo appInfo = super.getApplicationInfo(request, response);
    appInfo.setDocumentation("Web client application for public resources used by other SITools2 client applications.");
    return appInfo;
  }

  /**
   * Gets the challengeToken value
   * 
   * @return the challengeToken
   */
  public ChallengeToken getChallengeToken() {
    return challengeToken;
  }

  /**
   * Gets the resetPasswordIndexUrl value
   * 
   * @return the resetPasswordIndexUrl
   */
  public String getResetPasswordIndexUrl() {
    return resetPasswordIndexUrl;
  }

}

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
package fr.cnes.sitools.login;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import org.restlet.Request;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Status;
import org.restlet.ext.jackson.JacksonRepresentation;
import org.restlet.ext.wadl.MethodInfo;
import org.restlet.ext.xstream.XstreamRepresentation;
import org.restlet.representation.ObjectRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Put;
import org.restlet.resource.ResourceException;

import fr.cnes.sitools.applications.PublicApplication;
import fr.cnes.sitools.common.SitoolsResource;
import fr.cnes.sitools.common.SitoolsSettings;
import fr.cnes.sitools.common.application.SitoolsApplication;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.mail.model.Mail;
import fr.cnes.sitools.security.model.User;
import fr.cnes.sitools.common.Consts;
import fr.cnes.sitools.util.MailUtils;
import fr.cnes.sitools.util.RIAPUtils;
import fr.cnes.sitools.util.TemplateUtils;
import fr.cnes.sitools.util.Util;

/**
 * Resource to reset and generate an new user password with a sent email
 * 
 * @author AKKA Technologies
 * 
 */
public class LostPasswordResource extends SitoolsResource {
  /** The application */
  private PublicApplication application;

  @Override
  public void sitoolsDescribe() {
    setName("ResetPasswordResource");
    setDescription("Resource for reset and generate a new user password");
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.cnes.sitools.common.SitoolsResource#doInit()
   */
  @Override
  protected void doInit() {
    super.doInit();

    application = (PublicApplication) getApplication();
  }

  /**
   * Reset an user Password
   * 
   * @param representation
   *          the response to use
   * @param variant
   *          client preference for response media type
   * @return Representation if success
   */
  @Put
  public Representation resetPass(Representation representation, Variant variant) {
    if (representation == null) {
      throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "USER_REPRESENTATION_REQUIRED");
    }
    try {
      User user = getObject(representation);
      Response response = null;
      String url = getSitoolsSetting(Consts.APP_SECURITY_URL) + "/users";
      User userDb = RIAPUtils.getObject(user.getIdentifier(), url, getContext());

      if (userDb != null) {
        if (userDb.getEmail().equals(user.getEmail())) {
          String resetPasswordUrl = getResetPasswordUrl(user);
          sendMailToUser(userDb, resetPasswordUrl);
          response = new Response(true, "Mail sent to : " + user.getEmail() + " to initialize new password");
        }
        else {
          response = new Response(false, "Invalid fields : email doesn't match the correct login ");
        }
      }
      else {
        response = new Response(false, "User not found. ");
      }

      return getRepresentation(response, variant);
    }
    catch (ResourceException e) {
      getLogger().log(Level.INFO, null, e);
      throw e;
    }
    catch (Exception e) {
      getLogger().log(Level.SEVERE, null, e);
      throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
    }
  }

  /**
   * Gets the url to call to reset the password.
   * 
   * @param user
   *          the user
   * @return the reset password url
   */
  private String getResetPasswordUrl(User user) {
    String token = application.getChallengeToken().getToken(user.getIdentifier());
    return getSitoolsSetting(Consts.APP_CLIENT_PUBLIC_URL) + "/resetPassword/index.html?cdChallengeMail=" + token;
  }

  @Override
  public void describePut(MethodInfo info) {
    info.setDocumentation("Method to reset the password of a user");
    info.setIdentifier("reset_password");
    addStandardPostOrPutRequestInfo(info);
    addStandardResponseInfo(info);
    addStandardInternalServerErrorInfo(info);
  }

  /**
   * Gets User object from Representation
   * 
   * @param representation
   *          of a User
   * @return DataSet
   * @throws IOException
   *           if there is an error while deserializing Java Object
   */
  private User getObject(Representation representation) throws IOException {
    User object = null;
    if (representation.getMediaType().isCompatible(MediaType.APPLICATION_JAVA_OBJECT)) {
      @SuppressWarnings("unchecked")
      ObjectRepresentation<User> obj = (ObjectRepresentation<User>) representation;
      object = obj.getObject();
    }
    if (MediaType.APPLICATION_JSON.isCompatible(representation.getMediaType())) {
      // Parse the JSON representation to get the bean
      object = new JacksonRepresentation<User>(representation, User.class).getObject();
    }

    return object;
  }

  /**
   * Send the new password by mail to an user
   * 
   * @param user
   *          the user to send mail
   * @param url
   *          the url that the user must call to change its password
   */
  private void sendMailToUser(User user, String url) {

    SitoolsSettings settings = ((SitoolsApplication) getApplication()).getSettings();

    String[] toList = new String[] {user.getEmail()};
    Mail mailToUser = new Mail();
    mailToUser.setToList(Arrays.asList(toList));

    // Object
    mailToUser.setSubject("SITools2 - Password lost");

    // Body
    mailToUser.setBody(user.getIdentifier() + ", click on <a href='" + url + "'>" + url
        + "</a> to initialize a new password");

    // use a freemarker template for email body with Mail object
    String templatePath = settings.getRootDirectory() + settings.getString(Consts.TEMPLATE_DIR)
        + "mail.password.lost.ftl";
    Map<String, Object> root = new HashMap<String, Object>();
    root.put("user", user);
    root.put("passwordLostUrl", getSettings().getPublicHostDomain() + settings.getString(Consts.APP_URL) + url);
    MailUtils.addDefaultParameters(root, getSettings(), mailToUser);
    
    TemplateUtils.describeObjectClassesForTemplate(templatePath, root);

    root.put("context", getContext());

    String body = TemplateUtils.toString(templatePath, root);
    if (Util.isNotEmpty(body)) {
      mailToUser.setBody(body);
    }

    org.restlet.Response sendMailResponse = null;
    try {
      // riap request to MailAdministration application
      Request request = new Request(Method.POST, RIAPUtils.getRiapBase()
          + settings.getString(Consts.APP_MAIL_ADMIN_URL), new ObjectRepresentation<Mail>(mailToUser));

      sendMailResponse = getContext().getClientDispatcher().handle(request);
    }
    catch (Exception e) {
      getApplication().getLogger().warning("Failed to post message to user");
      throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
    }
    if (sendMailResponse.getStatus().isError()) {
      throw new ResourceException(Status.SERVER_ERROR_INTERNAL, "Server Error sending email to user.");
    }
  }

}

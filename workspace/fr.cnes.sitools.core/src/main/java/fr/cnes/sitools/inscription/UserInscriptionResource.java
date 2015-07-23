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
package fr.cnes.sitools.inscription;

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
import org.restlet.resource.Post;
import org.restlet.resource.ResourceException;

import fr.cnes.sitools.common.SitoolsSettings;
import fr.cnes.sitools.common.application.SitoolsApplication;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.inscription.model.Inscription;
import fr.cnes.sitools.mail.model.Mail;
import fr.cnes.sitools.security.SecurityUtil;
import fr.cnes.sitools.common.Consts;
import fr.cnes.sitools.util.MailUtils;
import fr.cnes.sitools.util.RIAPUtils;
import fr.cnes.sitools.util.TemplateUtils;
import fr.cnes.sitools.util.Util;

/**
 * Resource for users to subscribe
 * 
 * @author jp.boignard (AKKA Technologies)
 */
public final class UserInscriptionResource extends InscriptionResource {

  @Override
  public void sitoolsDescribe() {
    setName("UserInscriptionResource");
    setDescription("Resource for user inscription (register)");
  }

  /**
   * Handle POST method for registering a new User Inscription.
   * 
   * @param representation
   *          Representation
   * @param variant
   *          client preferred media type
   * @return Inscription
   */
  @Override
  @Post
  public Representation post(Representation representation, Variant variant) {
    if (representation == null) {
      throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "INSCRIPTION_REPRESENTATION_REQUIRED");
    }
    try {
      Inscription inscriptionInput = null;
      if (MediaType.APPLICATION_XML.isCompatible(representation.getMediaType())) {
        // Parse the XML representation to get the inscription bean
        inscriptionInput = new XstreamRepresentation<Inscription>(representation).getObject();

      }
      else if (MediaType.APPLICATION_JSON.isCompatible(representation.getMediaType())) {
        // Parse the JSON representation to get the bean
        inscriptionInput = new JacksonRepresentation<Inscription>(representation, Inscription.class).getObject();
      }

      // Response
      Response response = null;

      String message = checkInscription(inscriptionInput);
      if (message != null) {
        response = new Response(false, message);
      }
      else {

        // CRYPTAGE DU MOT DE PASSE SI NECESSAIRE
        SecurityUtil.encodeUserInscriptionPassword(getSitoolsApplication().getSettings(), inscriptionInput);

        Inscription inscriptionOutput = getStore().create(inscriptionInput);

        sendMailToAdmin(inscriptionOutput);

        response = new Response(true, inscriptionOutput, Inscription.class, "inscription");
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
   * Send the new password by mail to an user
   * 
   * @param inscription
   *          the user inscription
   */
  private void sendMailToAdmin(Inscription inscription) {

    SitoolsSettings settings = ((SitoolsApplication) getApplication()).getSettings();
    String adminMail = settings.getString("Starter.StatusService.CONTACT_MAIL", null);

    if (adminMail == null) {
      getLogger().info("No email address for administrator, cannot send inscription email");
      return;
    }

    String[] toList = new String[] {adminMail};
    Mail mailToAdmin = new Mail();
    mailToAdmin.setToList(Arrays.asList(toList));

    // Object
    mailToAdmin.setSubject("SITools2 - New user registration");

    // Body
    mailToAdmin.setBody("A new user registered on SITools2, username : " + inscription.getIdentifier());

    // use a freemarker template for email body with Mail object
    String templatePath = settings.getRootDirectory() + settings.getString(Consts.TEMPLATE_DIR)
        + "mail.inscription.registered.ftl";
    Map<String, Object> root = new HashMap<String, Object>();
    root.put("inscription", inscription);
    MailUtils.addDefaultParameters(root, getSettings(), mailToAdmin);
    
    TemplateUtils.describeObjectClassesForTemplate(templatePath, root);

    root.put("context", getContext());

    String body = TemplateUtils.toString(templatePath, root);
    if (Util.isNotEmpty(body)) {
      mailToAdmin.setBody(body);
    }

    org.restlet.Response sendMailResponse = null;
    try {
      // riap request to MailAdministration application
      Request request = new Request(Method.POST, RIAPUtils.getRiapBase()
          + settings.getString(Consts.APP_MAIL_ADMIN_URL), new ObjectRepresentation<Mail>(mailToAdmin));

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

  @Override
  public void describePost(MethodInfo info) {
    info.setDocumentation("Method to create a new registration request on client side");
    this.addStandardPostOrPutRequestInfo(info);
    this.addStandardResponseInfo(info);
    this.addStandardInternalServerErrorInfo(info);
  }

  /**
   * Check that the inscription is correct corresponding to some rules defined on the properties file
   * 
   * @param inscription
   *          the inscription to check
   * 
   * @return a String corresponding to the error, or null if validation is ok
   */
  private String checkInscription(Inscription inscription) {
    SitoolsSettings settings = ((SitoolsApplication) getApplication()).getSettings();

    String userRegExp = settings.getString("STARTER.SECURITY.USER_LOGIN_REGEX", null);
    if (inscription.getIdentifier() != null && (userRegExp != null && !inscription.getIdentifier().matches(userRegExp))) {
      return "WRONG_USER_LOGIN";
    }

    String passwordRegExp = settings.getString("STARTER.SECURITY.USER_PASSWORD_REGEX", null);
    if (inscription.getPassword() != null
        && (passwordRegExp != null && !inscription.getPassword().matches(passwordRegExp))) {
      return "WRONG_USER_PASSWORD";
    }
    return null;

  }

}

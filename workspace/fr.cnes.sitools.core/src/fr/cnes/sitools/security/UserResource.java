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
package fr.cnes.sitools.security;

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
import org.restlet.ext.wadl.ParameterInfo;
import org.restlet.ext.wadl.ParameterStyle;
import org.restlet.ext.xstream.XstreamRepresentation;
import org.restlet.representation.ObjectRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Get;
import org.restlet.resource.Put;
import org.restlet.resource.ResourceException;

import fr.cnes.sitools.common.SitoolsSettings;
import fr.cnes.sitools.common.application.SitoolsApplication;
import fr.cnes.sitools.common.exception.SitoolsException;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.mail.model.Mail;
import fr.cnes.sitools.notification.model.Notification;
import fr.cnes.sitools.security.model.User;
import fr.cnes.sitools.server.Consts;
import fr.cnes.sitools.util.RIAPUtils;
import fr.cnes.sitools.util.TemplateUtils;
import fr.cnes.sitools.util.Util;

/**
 * Resource for managing single User
 * 
 * <a href="https://sourceforge.net/tracker/?func=detail&aid=3317773&group_id=531341&atid=2158259">[3317773]</a><br/>
 * 21/06/2011 m.gond {} <br/>
 * 
 * @author jp.boignard (AKKA Technologies)
 * 
 */
public final class UserResource extends UsersAndGroupsResource implements fr.cnes.sitools.security.api.UserResource {

  @Override
  public void sitoolsDescribe() {
    setName("UserResource");
    setDescription("Resource for managing a user - Retrieve Update Delete");
    setNegotiated(false);
  }

  @Override
  public Representation getJSON() {
    getLogger().info("UserResource.getJSON");
    Response response = getUserResponse();
    return getRepresentation(response, MediaType.APPLICATION_JSON);
  }

  @Override
  public Representation getXML() {
    getLogger().info("UserResource.getXML");
    Response response = getUserResponse();
    return getRepresentation(response, MediaType.APPLICATION_XML);
  }

  @Override
  public Representation getObject() {
    getLogger().info("UserResource.getObject");
    Response response = getUserResponse();
    return getRepresentation(response, MediaType.APPLICATION_JAVA_OBJECT);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.restlet.resource.ServerResource#get(org.restlet.representation.Variant)
   */
  @Get
  @Override
  public Representation get(Variant variant) {
    Response response = getUserResponse();
    return getRepresentation(response, variant);
  }

  @Override
  public void describeGet(MethodInfo info) {
    info.setDocumentation("Method to get the list of users, by group if group ID is given.");
    this.addStandardGetRequestInfo(info);
    ParameterInfo paramUserId = new ParameterInfo("group", false, "xs:string", ParameterStyle.TEMPLATE,
        "Group identifier.");
    info.getRequest().getParameters().add(paramUserId);
    this.addStandardObjectResponseInfo(info);
  }

  /**
   * Calls business layer
   * 
   * @return Sitools.model.Response
   */
  private Response getUserResponse() {
    Response response = null;
    try {
      User user = getStore().getUserById(getUserId());
      if (user != null) {
        response = new Response(true, user, User.class, "user");
        trace(Level.FINE, "View user information for the user " + getUserId());
      }
      else {
        response = new Response(false, "user " + getUserId() + " does not exists");
        trace(Level.INFO, "Cannot view user information for the user " + getUserId());
      }

    }
    catch (Exception e) {
      trace(Level.INFO, "Cannot view user information for the user " + getUserId());
      response = new Response(false, e.getMessage());
      throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
    }
    return response;
  }

  @Override
  @Put
  public Representation update(Representation representation, Variant variant) {
    try {
      User input = null;
      String password = "";

      if (MediaType.APPLICATION_XML.isCompatible(representation.getMediaType())) {
        // Parse the XML representation to get the bean
        input = new XstreamRepresentation<User>(representation).getObject();

      }
      else if (MediaType.APPLICATION_JSON.isCompatible(representation.getMediaType())) {
        // Parse the JSON representation to get the bean
        input = new JacksonRepresentation<User>(representation, User.class).getObject();
      }
      else if (representation.getMediaType().isCompatible(MediaType.APPLICATION_JAVA_OBJECT)) {
        @SuppressWarnings("unchecked")
        ObjectRepresentation<User> obj = (ObjectRepresentation<User>) representation;
        try {
          input = obj.getObject();
        }
        catch (IOException e) {
          trace(Level.INFO, "Cannot update user information for the user " + getUserId());
          throw new ResourceException(Status.SERVER_ERROR_INTERNAL, null, e);
        }
      }

      // Business service
      // TODO à faire sur toutes les modifs de ressources
      input.setIdentifier(getUserId());

      User initial = getStore().getUserById(getUserId());
      boolean loginHasChanged = !initial.getIdentifier().equals(input.getIdentifier());

      // CRYPTAGE DU MOT DE PASSE SI NECESSAIRE
      if (loginHasChanged) {
        if (variant.getMediaType().isCompatible(MediaType.APPLICATION_JSON)
            || variant.getMediaType().isCompatible(MediaType.APPLICATION_XML)) {
          Response object = new Response(false, "error.login.not.mofifiable");
          return getRepresentation(object, variant.getMediaType());
        }
        else {
          trace(Level.INFO, "Cannot update user information for the user " + getUserId());
          throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "error.login.not.mofifiable");
        }
      }

      if (input.getProperties() != null && !checkPropertiesName(input.getProperties())) {
        MediaType media = representation.getMediaType();
        trace(Level.INFO, "Cannot update user information for the user " + getUserId());
        Response response = new Response(false, "Duplicated Property Name");
        return getRepresentation(response, media);
      }

      if ((input.getSecret() != null) && !input.getSecret().equals("")) {
        password = input.getSecret();
        SecurityUtil.encodeUserPassword(getUsersAndGroupsAdministration().getSettings(), input);
      }
      else {
        input.setSecret(initial.getSecret());
      }

      User output = getStore().updateUser(input);

      // Si le user password a été changé, on envoi le nouveau à l'user
      if (!password.isEmpty()) {
        sendMailToUser(input, password);
      }

      // Notify observers
      User notifier = new User();
      notifier.setIdentifier(output.getIdentifier());
      notifier.setSecret(output.getSecret());
      notifier.setEmail(output.getEmail());
      Notification notification = new Notification();
      notification.setEvent("USER_UPDATED");
      notification.setObservable(notifier.getIdentifier());
      notification.setEventSource(notifier);
      getResponse().getAttributes().put(Notification.ATTRIBUTE, notification);

      trace(Level.INFO, "Update user information for the user " + getUserId());
      // Response
      Response response = new Response(true, output, User.class, "user");
      return getRepresentation(response, variant);

    }
    catch (SitoolsException e) {
      trace(Level.INFO, "Cannot update user information for the user " + getUserId());
      MediaType media = representation.getMediaType();
      Response response = new Response(false, e.getMessage());
      return getRepresentation(response, media);
    }
    catch (ResourceException e) {
      trace(Level.INFO, "Cannot update user information for the user " + getUserId());
      getLogger().log(Level.INFO, null, e);
      throw e;
    }
    catch (Exception e) {
      trace(Level.INFO, "Cannot update user information for the user " + getUserId());
      getLogger().log(Level.WARNING, null, e);
      throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
    }
  }

  @Override
  public void describePut(MethodInfo info) {
    info.setDocumentation("Method to modify a user.");
    this.addStandardPostOrPutRequestInfo(info);
    ParameterInfo paramUserId = new ParameterInfo("group", false, "xs:string", ParameterStyle.TEMPLATE,
        "Group identifier.");
    info.getRequest().getParameters().add(paramUserId);
    this.addStandardObjectResponseInfo(info);
  }

  @Override
  public Representation delete(Variant variant) {
    Response response = null;
    try {
      getStore().deleteUser(getUserId());

      response = new Response(true, "user.delete.success");

      // Notify observers
      Notification notification = new Notification();
      notification.setObservable(getUserId());
      notification.setEvent("USER_DELETED");
      notification.setMessage("user.delete.success");
      getResponse().getAttributes().put(Notification.ATTRIBUTE, notification);
      trace(Level.INFO, "Delete information for the user " + getUserId());
    }
    catch (Exception e) {
      trace(Level.INFO, "Cannot delete information for the user " + getUserId());
      response = new Response(false, e.getMessage());
      getLogger().log(Level.INFO, null, e);
    }
    return getRepresentation(response, variant);
  }

  /**
   * Send the new password by mail to an user
   * 
   * @param user
   *          the user to send mail
   * @param pass
   *          the user password to send
   */
  private void sendMailToUser(User user, String pass) {

    SitoolsSettings settings = ((SitoolsApplication) getApplication()).getSettings();

    String[] toList = new String[] {user.getEmail()};
    Mail mailToUser = new Mail();
    mailToUser.setToList(Arrays.asList(toList));

    // Object
    mailToUser.setSubject("SITOOLS - Your password has been changed");

    // Body
    mailToUser.setBody(user.getIdentifier() + ", your password has been changed. Your new password is : " + pass);

    // use a freemarker template for email body with Mail object
    String templatePath = settings.getRootDirectory() + settings.getString(Consts.TEMPLATE_DIR)
        + "mail.password.reset.ftl";
    Map<String, Object> root = new HashMap<String, Object>();
    root.put("mail", mailToUser);
    root.put("user", user);
    root.put(
        "sitoolsUrl",
        getSettings().getPublicHostDomain() + settings.getString(Consts.APP_URL)
            + settings.getString(Consts.APP_CLIENT_USER_URL) + "/");

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

  @Override
  public void describeDelete(MethodInfo info) {
    info.setDocumentation("Method to delete a user by ID.");
    this.addStandardGetRequestInfo(info);
    ParameterInfo paramUserId = new ParameterInfo("group", false, "xs:string", ParameterStyle.TEMPLATE,
        "Group identifier.");
    info.getRequest().getParameters().add(paramUserId);
    this.addStandardObjectResponseInfo(info);
  }

}

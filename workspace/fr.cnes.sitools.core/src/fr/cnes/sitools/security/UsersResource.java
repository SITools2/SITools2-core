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

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.restlet.Request;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Preference;
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
import org.restlet.resource.Post;
import org.restlet.resource.Put;
import org.restlet.resource.ResourceException;

import fr.cnes.sitools.common.SitoolsSettings;
import fr.cnes.sitools.common.application.SitoolsApplication;
import fr.cnes.sitools.common.exception.SitoolsException;
import fr.cnes.sitools.common.model.ResourceCollectionFilter;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.mail.model.Mail;
import fr.cnes.sitools.notification.model.Notification;
import fr.cnes.sitools.security.model.Group;
import fr.cnes.sitools.security.model.User;
import fr.cnes.sitools.server.Consts;
import fr.cnes.sitools.util.PasswordGenerator;
import fr.cnes.sitools.util.RIAPUtils;
import fr.cnes.sitools.util.TemplateUtils;
import fr.cnes.sitools.util.Util;

/**
 * Resource for users management
 * 
 * <a href="https://sourceforge.net/tracker/?func=detail&aid=3317773&group_id=531341&atid=2158259">[3317773]</a><br/>
 * 21/06/2011 m.gond {} <br/>
 * 
 * @author jp.boignard (AKKA Technologies)
 * 
 */
public final class UsersResource extends UsersAndGroupsResource {

  @Override
  public void sitoolsDescribe() {
    setName("UsersResource");
    setDescription("Resource for managing a user collection");
    setNegotiated(false);
  }

  /**
   * Get an XML representation
   * 
   * @param variant
   *          The variant needed
   * 
   * @return an XML representation of the user
   */
  @Get
  public Representation get(Variant variant) {
    getLogger().info("UsersResource.getXML");
    Response response = getUsersResponse();
    return getRepresentation(response, variant);
  }

  @Override
  public void describeGet(MethodInfo info, String path) {
    this.addStandardGetRequestInfo(info);
    if (path.endsWith("groups/{group}/users")) {
      info.setDocumentation("GET " + path + " : Gets the list of users for the specified group.");

      ParameterInfo paramUserId = new ParameterInfo("group", false, "xs:string", ParameterStyle.TEMPLATE,
          "Group identifier.");
      info.getRequest().getParameters().add(paramUserId);
    }
    else {
      info.setDocumentation("GET " + path + " : Gets the list of users.");
      this.addStandardResourceCollectionFilterInfo(info);
    }
    this.addStandardObjectResponseInfo(info);
  }

  /**
   * Get the response bounded to the user
   * 
   * @return a response representing the user
   */
  private Response getUsersResponse() {
    Response response = null;
    try {
      List<User> users = null;
      ResourceCollectionFilter filter = new ResourceCollectionFilter(this.getRequest());
      if (getGroupName() != null) {
        users = getStore().getUsersByGroup(getGroupName(), filter);
      }
      else {
        users = getStore().getUsers(filter);
      }
      response = new Response(true, users, User.class);
      response.setTotal((filter.getTotalCount() == null) ? users.size() : filter.getTotalCount());
    }
    catch (Exception e) {
      response = new Response(false, e.getMessage());
    }
    // debug
    response.setUrl(this.getReference().toString());
    return response;
  }

  /**
   * Update Group
   * 
   * @param representation
   *          input User representation
   * @param variant
   *          client preferred media type
   * @return Representation
   */
  @Put
  public Representation update(Representation representation, Variant variant) {
    try {
      Group input = getGroupObject(representation);

      // Business service
      input.checkUserUnicity();
      Group output = getStore().updateGroupUsers(input);

      // Notify observers
      Notification notification = new Notification();
      notification.setEvent("GROUP_UPDATED");
      notification.setObservable(output.getName());
      getResponse().getAttributes().put(Notification.ATTRIBUTE, notification);

      // Response
      Response response = new Response(true, output, Group.class, "group");
      return getRepresentation(response, variant);
    }
    catch (SitoolsException e) {
      getLogger().log(Level.SEVERE, null, e);
      Response response = new Response(false, e.getMessage());
      return getRepresentation(response, variant.getMediaType());
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

  @Override
  public void describePut(MethodInfo info) {
    info.setDocumentation("Method to modify the list of users, grouped by group if ID is given");
    this.addStandardPostOrPutRequestInfo(info);
    ParameterInfo paramUserId = new ParameterInfo("group", false, "xs:string", ParameterStyle.TEMPLATE,
        "Group identifier.");
    info.getRequest().getParameters().add(paramUserId);
    this.addStandardObjectResponseInfo(info);
    this.addStandardInternalServerErrorInfo(info);
  }

  /**
   * Create a new user from representation
   * 
   * @param representation
   *          the representation to use for creation
   * @return the representation of the answer to the creation
   */
  @SuppressWarnings("unchecked")
  @Post
  public Representation newUser(Representation representation) {
    if (representation == null) {
      throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "USER_REPRESENTATION_REQUIRED");
    }
    try {
      User input = null;
      if (MediaType.APPLICATION_XML.isCompatible(representation.getMediaType())) {
        // Parse the XML representation to get the bean
        input = new XstreamRepresentation<User>(representation).getObject();

      }
      else if (MediaType.APPLICATION_JSON.isCompatible(representation.getMediaType())) {
        // Parse the JSON representation to get the bean
        input = new JacksonRepresentation<User>(representation, User.class).getObject();

      }
      else if (MediaType.APPLICATION_JAVA_OBJECT.isCompatible(representation.getMediaType())) {
        input = ((ObjectRepresentation<User>) representation).getObject();
      }

      String passwordUnecoded = input.getSecret();
      if (passwordUnecoded == null) {
        passwordUnecoded = PasswordGenerator.generate(10);
        input.setSecret(passwordUnecoded);
      }
      checkUser(input);

      // CRYPTAGE DU MOT DE PASSE SI NECESSAIRE
      SecurityUtil.encodeUserPassword(getSitoolsApplication().getSettings(), input);

      if (input.getProperties() != null && !checkPropertiesName(input.getProperties())) {
        MediaType media = representation.getMediaType();
        Response response = new Response(false, "Duplicated Property Name");
        return getRepresentation(response, media);
      }

      // Business service
      User output = getStore().createUser(input);

      MediaType media = representation.getMediaType();
      if ((media == null)
          || !media.isConcrete()
          || (!media.isCompatible(MediaType.APPLICATION_XML) && media.isCompatible(MediaType.APPLICATION_JSON) && media
              .isCompatible(MediaType.APPLICATION_JAVA_OBJECT))) {

        List<Preference<MediaType>> preferences = this.getRequest().getClientInfo().getAcceptedMediaTypes();
        for (Iterator<Preference<MediaType>> iterator = preferences.iterator(); iterator.hasNext();) {
          Preference<MediaType> preference = (Preference<MediaType>) iterator.next();
          if (preference.getMetadata().isCompatible(MediaType.APPLICATION_XML)) {
            media = MediaType.APPLICATION_XML;
            break;
          }
          if (preference.getMetadata().isCompatible(MediaType.APPLICATION_JSON)) {
            media = MediaType.APPLICATION_JSON;
            break;
          }
          if (preference.getMetadata().isCompatible(MediaType.APPLICATION_JAVA_OBJECT)) {
            media = MediaType.APPLICATION_JAVA_OBJECT;
            break;
          }
        }
      }

      // creation d'un espace de stockage pour l'utilisateur
      // Notify observers
      Notification notification = new Notification();
      notification.setObservable(output.getIdentifier());
      notification.setEvent("USER_CREATED");
      notification.setMessage("user.create.success");
      getResponse().getAttributes().put(Notification.ATTRIBUTE, notification);

      // Si le user password a été changé, on envoi le nouveau à l'user
      sendMailToUser(input, passwordUnecoded);

      // Response
      Response response = new Response(true, output, User.class, "user");
      return getRepresentation(response, media);
    }
    catch (SitoolsException e) {
      MediaType media = representation.getMediaType();
      Response response = new Response(false, e.getMessage());
      return getRepresentation(response, media);
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
   * Check that the user is correct corresponding to some rules defined on the properties file
   * 
   * @param user
   *          the User to check
   * @throws SitoolsException
   *           if the user is unvalid
   */
  private void checkUser(User user) throws SitoolsException {
    SitoolsSettings settings = ((SitoolsApplication) getApplication()).getSettings();

    String userRegExp = settings.getString("STARTER.SECURITY.USER_LOGIN_REGEX", null);
    if (user.getIdentifier() != null && (userRegExp != null && !user.getIdentifier().matches(userRegExp))) {
      throw new SitoolsException("WRONG_USER_LOGIN");
    }

    String passwordRegExp = settings.getString("STARTER.SECURITY.USER_PASSWORD_REGEX", null);
    if (user.getSecret() != null && (passwordRegExp != null && !user.getSecret().matches(passwordRegExp))) {
      throw new SitoolsException("WRONG_USER_PASSWORD");
    }

  }

  @Override
  public void describePost(MethodInfo info) {
    info.setDocumentation("Method to create a user, in a certain group if group ID is given.");
    this.addStandardPostOrPutRequestInfo(info);
    ParameterInfo paramUserId = new ParameterInfo("group", false, "xs:string", ParameterStyle.TEMPLATE,
        "Group identifier.");
    info.getRequest().getParameters().add(paramUserId);
    this.addStandardObjectResponseInfo(info);
    this.addStandardInternalServerErrorInfo(info);
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
    if (user.getEmail() == null) {
      getLogger().info("User " + user.getIdentifier() + " has no email, cannot send confirmation email");
      return;
    }
    String[] toList = new String[] {user.getEmail()};
    Mail mailToUser = new Mail();
    mailToUser.setToList(Arrays.asList(toList));

    // Object
    mailToUser.setSubject("SITOOLS2 - Your new account");

    // Body
    mailToUser.setBody(user.getIdentifier() + ", your password has been changed. Your new password is : " + pass);

    // use a freemarker template for email body with Mail object
    String templatePath = settings.getRootDirectory() + settings.getString(Consts.TEMPLATE_DIR)
        + "mail.account.created.ftl";
    Map<String, Object> root = new HashMap<String, Object>();
    root.put("mail", mailToUser);
    root.put("user", user);
    if (!SecurityUtil.isEncoded(pass)) {
      root.put("pass", pass);
    }
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

}

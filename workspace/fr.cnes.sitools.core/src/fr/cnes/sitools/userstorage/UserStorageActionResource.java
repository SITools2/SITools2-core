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
package fr.cnes.sitools.userstorage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.restlet.Request;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Preference;
import org.restlet.data.Status;
import org.restlet.ext.wadl.MethodInfo;
import org.restlet.ext.wadl.ParameterInfo;
import org.restlet.ext.wadl.ParameterStyle;
import org.restlet.representation.EmptyRepresentation;
import org.restlet.representation.ObjectRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Put;
import org.restlet.resource.ResourceException;

import fr.cnes.sitools.common.SitoolsSettings;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.mail.model.Mail;
import fr.cnes.sitools.security.model.User;
import fr.cnes.sitools.server.Consts;
import fr.cnes.sitools.userstorage.business.UserStorageManager;
import fr.cnes.sitools.userstorage.model.Action;
import fr.cnes.sitools.userstorage.model.UserStorage;
import fr.cnes.sitools.userstorage.model.UserStorageStatus;
import fr.cnes.sitools.util.RIAPUtils;
import fr.cnes.sitools.util.TemplateUtils;
import fr.cnes.sitools.util.Util;

/**
 * Resource for managing single user disk space
 * 
 * @author jp.boignard (AKKA Technologies)
 */
public final class UserStorageActionResource extends AbstractUserStorageResource {

  @Override
  public void sitoolsDescribe() {
    setName("UserStorageActionResource");
    setDescription("Resource to perform several actions on user storage");
    setNegotiated(false);
  }

  /**
   * PUT treatment
   * 
   * @param representation
   *          the representation used
   * @param variant
   *          the variant used
   * @return Representation
   */
  @Put
  public Representation doPut(Representation representation, Variant variant) {
    SitoolsSettings settings = getSettings();

    // resource action settings
    // String mailServer = settings.getString("Starter.mail.send.server");

    if (Action.START.value().equals(getAction())) {
      getLogger().finest(Action.START.value());
      UserStorage storage = getStore().retrieve(getIdentifier());
      storage.setStatus(UserStorageStatus.ACTIVE);
      // create directory
      // String root = (String) getContext().getAttributes().get(ROOT);
      
      // Format UserStorage Path
      String path = storage.getStorage().getUserStoragePath();
      // String path = storage.getStorage().getUserStoragePath();
      if (path == null || path.equals("")) {
        path = getUserStorageManagement().getRootDirectory() + File.separator + storage.getUserId();
      }
      storage.getStorage().setUserStoragePath(path);
      
      path = settings.getFormattedString(storage.getStorage().getUserStoragePath());
      File cible = new File(path);
      if (cible.exists()) {
        cible.mkdir();
      }
      // refresh storage on start
      UserStorageManager.refresh(getContext(), storage);
      getStore().update(storage);

      getLogger().finest("status: " + storage.getStatus());
      Response response = new Response(true, "userstorage.activation.success");
      return getRepresentation(response, variant);
    }
    else if (Action.STOP.value().equals(getAction())) {
      getLogger().finest(Action.STOP.value());
      UserStorage storage = getStore().retrieve(getIdentifier());
      storage.setStatus(UserStorageStatus.DISACTIVE);
      getStore().update(storage);
      getLogger().finest("status: " + storage.getStatus());
      Response response = new Response(true, "userstorage.stop.success");
      return getRepresentation(response, variant);
    }
    else if (Action.REFRESH.value().equals(getAction())) {
      UserStorage storage = getStore().retrieve(getIdentifier());
      if (storage != null) {
        UserStorageManager.refresh(getContext(), storage);
        getStore().update(storage);
        Response response = new Response(true, "userstorage.refresh.success");
        return getRepresentation(response, variant);
      }
    }
    else if (Action.CLEAN.value().equals(getAction())) {
      UserStorage storage = getStore().retrieve(getIdentifier());
      if (storage != null) {
        UserStorageManager.clean(getContext(), storage);
        UserStorageManager.refresh(getContext(), storage);
        getStore().update(storage);
        Response response = new Response(true, "userstorage.clean.success");
        return getRepresentation(response, variant);
      }
    }
    else if (Action.NOTIFY.value().equals(getAction())) {
      // =================================================================================
      // to send email to user
      UserStorage storage = getStore().retrieve(getIdentifier());
      // check if user ever exists
      Response response = null;
      Request reqGET = new Request(Method.GET, RIAPUtils.getRiapBase() + settings.getString(Consts.APP_SECURITY_URL)
          + "/users/" + storage.getUserId());
      ArrayList<Preference<MediaType>> objectMediaType = new ArrayList<Preference<MediaType>>();
      objectMediaType.add(new Preference<MediaType>(MediaType.APPLICATION_JAVA_OBJECT));
      reqGET.getClientInfo().setAcceptedMediaTypes(objectMediaType);

      org.restlet.Response resp = getUserStorageManagement().getContext().getClientDispatcher().handle(reqGET);

      if (resp == null || Status.isError(resp.getStatus().getCode())) {
        // fail access User application
        throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
      }

      @SuppressWarnings("unchecked")
      ObjectRepresentation<Response> or = (ObjectRepresentation<Response>) resp.getEntity();
      Response myObj;
      try {
        myObj = or.getObject();
      }
      catch (IOException e) { // marshalling error
        throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
      }
      if (myObj.isSuccess() && myObj.getItem() != null) {
        User user = (User) myObj.getItem();
        if ((user.getEmail() == null) || "".equals(user.getEmail())) {
          response = new Response(false, "user.email.null");
          return getRepresentation(response, variant);
        }
        String[] toList = new String[] {user.getEmail()};

        if (storage.getStorage().getBusyUserSpace() > storage.getStorage().getQuota()) {
          org.restlet.Response sendMailResponse = sendMailQuotaExceeded(storage, toList);
          if (sendMailResponse.getStatus().isError()) {
            response = new Response(false, "userstorage.notify.error");
          }
          else {
            response = new Response(true, "userstorage.notify.success");
          }
          return getRepresentation(response, variant);
        }
        else {
          response = new Response(true, "userstorage.notify.unnecessary");
          return getRepresentation(response, variant);
        }
      }
      else {
        response = new Response(false, "user.null");
        return getRepresentation(response, variant);
      }
      // =================================================================================
    }
    else {
      getLogger().finest(this.getName() + " unknown requested action.");
      throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "action.unknown");
    }
    return new EmptyRepresentation();
  }

  /**
   * Send a mail to notify an user than is quota is exceeded
   * 
   * @param storage
   *        th user storage
   * @param toList
   *        th user mail
   * @return a {@link org.restlet.Response} mail 
   */
  private org.restlet.Response sendMailQuotaExceeded(UserStorage storage, String[] toList) {
    Mail mailToUser = new Mail();
    mailToUser.setToList(Arrays.asList(toList));
    // TODO EVOL : email subject should be a parameter
    mailToUser.setSubject("Sitools quota");

    // default body
    mailToUser.setBody("Your quota exceeded:" + storage.getStorage().getBusyUserSpace() + " used / "
        + storage.getStorage().getQuota() + " authorized");

    // use a freemarker template for email body with Mail object
    String templatePath = getSettings().getRootDirectory() + getSettings().getString(Consts.TEMPLATE_DIR)
        + "mail.quota.exceeded.ftl";
    Map<String, Object> root = new HashMap<String, Object>();
    root.put("mail", mailToUser);
    root.put("storage", storage);

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
          + getSettings().getString(Consts.APP_MAIL_ADMIN_URL), new ObjectRepresentation<Mail>(mailToUser));

      sendMailResponse = getContext().getClientDispatcher().handle(request);
    }
    catch (Exception e) {
      getApplication().getLogger().warning("Failed to post message to user");
      throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
    }
    return sendMailResponse;
  }

  @Override
  public void describePut(MethodInfo info) {
    info.setDocumentation("Method to perform several action on the userstorage of the specified user.");
    this.addStandardPostOrPutRequestInfo(info);
    ParameterInfo paramUserId = new ParameterInfo(IDENTIFIER_PARAM_NAME, true, "xs:string", ParameterStyle.TEMPLATE,
        "Identifier of the user to deal with.");
    info.getRequest().getParameters().add(paramUserId);
    ParameterInfo paramAction = new ParameterInfo("action", false, "xs:string", ParameterStyle.TEMPLATE,
        "(start|stop|notify|clean|refresh) : action on the user storage.");
    info.getRequest().getParameters().add(paramAction);
    this.addStandardResponseInfo(info);
  }

}

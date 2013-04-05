/*******************************************************************************
 * Copyright 2011, 2012 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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
import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.Put;
import org.restlet.resource.ResourceException;

import fr.cnes.sitools.common.SitoolsSettings;
import fr.cnes.sitools.common.application.SitoolsApplication;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.inscription.model.Inscription;
import fr.cnes.sitools.mail.model.Mail;
import fr.cnes.sitools.security.SecurityUtil;
import fr.cnes.sitools.security.model.User;
import fr.cnes.sitools.server.Consts;
import fr.cnes.sitools.util.RIAPUtils;
import fr.cnes.sitools.util.TemplateUtils;
import fr.cnes.sitools.util.Util;

/**
 * Resource for managing inscriptions (Role Administrator)
 * 
 * @author jp.boignard (AKKA Technologies)
 * 
 */
public final class AdminInscriptionResource extends InscriptionResource {

  @Override
  public void sitoolsDescribe() {
    setName("AdminInscriptionResource");
    setDescription("Resource for managing a single inscription (Retrieve Update Delete and Validate)");
  }

  /**
   * get all inscriptions
   * 
   * @param variant
   *          client preferred media type
   * @return Representation
   */
  @Get
  public Representation retrieveInscription(Variant variant) {
    try {
      Inscription inscription = getStore().retrieve(getInscriptionId());
      if (inscription == null) {
        throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND);
      }

      Response response = new Response(true, inscription, Inscription.class, "inscription");
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

  @Override
  public void describeGet(MethodInfo info) {
    info.setDocumentation("Method to retrieve one inscriptions.");
    this.addStandardGetRequestInfo(info);
    ParameterInfo param = new ParameterInfo("inscriptionId", false, "xs:string", ParameterStyle.TEMPLATE,
        "Identifier of the inscription to retrieve.");
    info.getRequest().getParameters().add(param);
    this.addStandardObjectResponseInfo(info);
    this.addStandardInternalServerErrorInfo(info);
  }

  /**
   * Update / Validate existing inscription
   * 
   * @param representation
   *          Inscription representation
   * @param variant
   *          client preferred media type
   * @return Representation
   */
  @Put
  public Representation putInscription(Representation representation, Variant variant) {
    SitoolsSettings settings = ((SitoolsApplication) getApplication()).getSettings();

    Response response = null;
    Inscription inscriptionOutput = null;
    Inscription inscriptionInput = null;

    boolean validating = false;

    if (getInscriptionId() == null) {
      throw new ResourceException(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED);
    }

    if (representation == null) {
      // On recharge l'inscription avec l'uid
      inscriptionInput = getStore().retrieve(getInscriptionId());
      if (inscriptionInput == null) {
        throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
      }
      validating = true;
    }
    else {
      if (MediaType.APPLICATION_XML.isCompatible(representation.getMediaType())) {
        // Parse the XML representation to get the inscription bean
        inscriptionInput = new XstreamRepresentation<Inscription>(representation).getObject();

      }
      else if (MediaType.APPLICATION_JSON.isCompatible(representation.getMediaType())) {
        // Parse the JSON representation to get the bean
        inscriptionInput = new JacksonRepresentation<Inscription>(representation, Inscription.class).getObject();
      }

      if (inscriptionInput == null) {
        throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
      }

      if (!inscriptionInput.getId().equals(getInscriptionId())) {
        throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
      }

      Inscription initial = getStore().retrieve(getInscriptionId());
      boolean loginHasChanged = !initial.getIdentifier().equals(inscriptionInput.getIdentifier());

      if (loginHasChanged && (inscriptionInput.getPassword() == null || inscriptionInput.getPassword().equals(""))) {
        if (variant.getMediaType().isCompatible(MediaType.APPLICATION_JSON)
            || variant.getMediaType().isCompatible(MediaType.APPLICATION_XML)) {
          Response object = new Response(false, "error.password.mandatory");
          return getRepresentation(object, variant.getMediaType());
        }
        else {
          throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "error.password.mandatory");
        }
      }
      // CRYPTAGE DU MOT DE PASSE SI NECESSAIRE
      if (loginHasChanged && (inscriptionInput.getPassword() != null) && !inscriptionInput.getPassword().equals("")) {
        SecurityUtil.encodeUserInscriptionPassword(getInscriptionApplication().getSettings(), inscriptionInput);
      }
      else {
        inscriptionInput.setPassword(initial.getPassword());
      }

      inscriptionOutput = getStore().update(inscriptionInput);
      if (inscriptionOutput == null) {
        throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
      }

      response = new Response(true, "INSCRIPTION.UPDATED User updated");
    }

    // ici on a une inscription modifiée / obtenue par son id à valider.

    // VALIDATION => CREATION UTILISATEUR
    if (validating) {

      // nouvel utilisateur
      User newUser = inscriptionInput.wrapToUser();

      // check si un utilisateur existe pas déjà
      Request reqGET = new Request(Method.GET, RIAPUtils.getRiapBase() + settings.getString(Consts.APP_SECURITY_URL)
          + "/users/" + inscriptionInput.getIdentifier());
      ArrayList<Preference<MediaType>> objectMediaType = new ArrayList<Preference<MediaType>>();
      objectMediaType.add(new Preference<MediaType>(MediaType.APPLICATION_JAVA_OBJECT));
      reqGET.getClientInfo().setAcceptedMediaTypes(objectMediaType);

      org.restlet.Response resp = getInscriptionApplication().getContext().getClientDispatcher().handle(reqGET);

      if (resp == null || Status.isError(resp.getStatus().getCode())) {
        // echec access User application
        throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
      }

      @SuppressWarnings("unchecked")
      ObjectRepresentation<Response> or = (ObjectRepresentation<Response>) resp.getEntity();
      Response myObj;
      try {
        myObj = or.getObject();
      }
      catch (IOException e) { // marshalling error
        throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
      }
      if (myObj.isSuccess() && myObj.getItem() != null) {
        // Il existe déjà un utilisateur avec ce même identifiant.
        // message d'erreur de reponse
        response = new Response(false, "KEY_USER_ERROR identifier ever exists");
      }
      else {
        // OK - Creation de l'utilisateur
        Request reqPOST = new Request(Method.POST, RIAPUtils.getRiapBase()
            + settings.getString(Consts.APP_SECURITY_URL) + "/users", new ObjectRepresentation<User>(newUser));
        reqPOST.getClientInfo().setAcceptedMediaTypes(objectMediaType);
        org.restlet.Response r = getInscriptionApplication().getContext().getClientDispatcher().handle(reqPOST);

        if (r == null || Status.isError(r.getStatus().getCode())) {
          // echec access User application
          throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
        }

        try {
          @SuppressWarnings("unchecked")
          Response repPOST = ((ObjectRepresentation<Response>) r.getEntity()).getObject();
          if (!repPOST.isSuccess()) {
            response = new Response(false, "CREATE_USER_ERROR " + repPOST.getMessage());
          }
        }
        catch (IOException e) { // marshalling error
          throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
        }

        // suppression de l'inscription
        getStore().delete(inscriptionInput.getId());

        // =================================================================================
        // to send email to user
        Mail mailToUser = new Mail();

        String[] toList = new String[] {inscriptionInput.getEmail()};
        mailToUser.setToList(Arrays.asList(toList));

        // TODO EVOL : email subject should be a parameter
        mailToUser.setSubject("Sitools registration");

        // default
        mailToUser.setBody("Your account is now activated.");

        // use a freemarker template for email body with Mail object
        String templatePath = settings.getRootDirectory() + settings.getString(Consts.TEMPLATE_DIR)
            + "mail.account.activated.ftl";
        Map<String, Object> root = new HashMap<String, Object>();
        root.put("mail", mailToUser);
        root.put("user", newUser);
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
        }
        if (sendMailResponse == null || sendMailResponse.getStatus().isError()) {
          response = new Response(true, "INSCRIPTION.VALIDATED User created, Inscription deleted but mail not sent.");
        }
        else {
          // construction de la reponse
          response = new Response(true, "INSCRIPTION.VALIDATED User created, Inscription deleted, Mail sent to user.");
        }

        // =================================================================================

        // url de la nouvelle resource en retour ?
        // response + redirection ?
      }
    } // end validation

    if (MediaType.APPLICATION_JAVA_OBJECT.isCompatible(variant.getMediaType())) {
      return new ObjectRepresentation<Response>(response);
    }

    if (MediaType.APPLICATION_XML.isCompatible(variant.getMediaType())
        || MediaType.APPLICATION_JSON.isCompatible(variant.getMediaType())) {
      // Response XML / JSON
      return getRepresentation(response, variant);
    }

    throw new ResourceException(Status.CLIENT_ERROR_UNSUPPORTED_MEDIA_TYPE);
  }

  @Override
  public void describePut(MethodInfo info) {
    info.setDocumentation("Method to validate/modify an inscription given by the ID.");
    this.addStandardGetRequestInfo(info);
    ParameterInfo param = new ParameterInfo("inscriptionId", true, "xs:string", ParameterStyle.TEMPLATE,
        "Identifier of the inscription to retrieve.");
    info.getRequest().getParameters().add(param);
    this.addStandardResponseInfo(info);
  }

  /**
   * Delete inscription
   * 
   * @param variant
   *          client preferred media type
   * @return Representation
   */
  @Delete
  public Representation deleteInscription(Variant variant) {
    try {
      // Business service
      boolean result = getStore().delete(getInscriptionId());

      Response response = result ? new Response(result, "INSCRIPTION.DELETED") : new Response(result,
          "INSCRIPTION.NOT_FOUND");

      // Response
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

  @Override
  public void describeDelete(MethodInfo info) {
    info.setDocumentation("Method to delete an inscription given by the ID.");
    this.addStandardGetRequestInfo(info);
    ParameterInfo param = new ParameterInfo("inscriptionId", true, "xs:string", ParameterStyle.TEMPLATE,
        "Identifier of the inscription to retrieve.");
    info.getRequest().getParameters().add(param);
    this.addStandardSimpleResponseInfo(info);
    this.addStandardInternalServerErrorInfo(info);
  }

}

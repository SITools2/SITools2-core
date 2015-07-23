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
package fr.cnes.sitools.mail;

import java.io.IOException;
import java.util.logging.Level;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.ChallengeResponse;
import org.restlet.data.ChallengeScheme;
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
import org.restlet.resource.Post;
import org.restlet.resource.ResourceException;

import com.thoughtworks.xstream.XStream;

import fr.cnes.sitools.common.SitoolsResource;
import fr.cnes.sitools.common.XStreamFactory;
import fr.cnes.sitools.common.task.RequestDispatcherTask;
import fr.cnes.sitools.mail.model.Mail;
import fr.cnes.sitools.mail.model.MailConfiguration;
import fr.cnes.sitools.proxy.ProxySettings;
import fr.cnes.sitools.util.Util;

/**
 * Resource to configure mail management.
 * 
 * @author jp.boignard (AKKA Technologies)
 * 
 */
public final class MailResource extends SitoolsResource {

  /** parent application */
  private MailAdministration application = null;

  @Override
  public void doInit() {
    super.doInit();
    application = (MailAdministration) getApplication();
  }

  @Override
  public void sitoolsDescribe() {
    setName("MailResource");
    setDescription("Resource for sending mails according to the MailAdministration configuration.");
  }

  /**
   * Envoi Mail
   * 
   * @param representation
   *          representation XML / JSON of a Mail object
   * @param variant
   *          XML / JSON for response
   * @return a representation of the mail sent
   */
  @Post
  public Representation sendMail(Representation representation, Variant variant) {

    // resource action settings
    String mailServer = application.getSettings().getString("Starter.mail.send.server");
    String mailAdmin = application.getSettings().getString("Starter.mail.send.admin");
    String adminIdentifier = application.getSettings().getString("Starter.mail.send.identifier");
    String adminSecret = application.getSettings().getString("Starter.mail.send.secret");

    fr.cnes.sitools.common.model.Response sendMailResponse = null;
    if (representation == null) {
      throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "mail.sendmail.error : entity is null");
    }

    try {
      Mail input = null;

      // Parse object representation
      input = getObject(representation, variant);

      if ((input.getFrom() == null) || input.getFrom().equals("")) {
        getApplication().getLogger().info("sending mail from default email address");
        input.setFrom(mailAdmin);
      }

      Request request = new Request(Method.POST, mailServer, new ObjectRepresentation<Mail>(input));

      if (ProxySettings.isWithProxy()) {
        ChallengeResponse challenge = ProxySettings.getProxyAuthentication();
        request.setProxyChallengeResponse(challenge);
      }

      if (Util.isNotEmpty(adminIdentifier)) {
        request.setChallengeResponse(new ChallengeResponse(ChallengeScheme.SMTP_PLAIN, adminIdentifier, adminSecret));
      }

      if (!Boolean.parseBoolean(request.getResourceRef().getQueryAsForm().getFirstValue("wait", true, "false"))) {
        // send mail in asynchronous mode
        sendMailAsynchrone(getContext(), request);
        // always supposed to succeed
        sendMailResponse = new fr.cnes.sitools.common.model.Response(true, "mail.send.success");
      }
      else {

        Response response = getContext().getClientDispatcher().handle(request);

        if (response.getStatus().isSuccess()) {
          sendMailResponse = new fr.cnes.sitools.common.model.Response(true, "mail.send.success");
        }
        else {
          sendMailResponse = new fr.cnes.sitools.common.model.Response(false, "mail.send.failed");
        }
      }

      return getRepresentation(sendMailResponse, variant.getMediaType());
    }
    catch (Exception e) {
      throw new ResourceException(Status.SERVER_ERROR_INTERNAL, "mail.sendmail.error : ", e);
    }
  }

  @Override
  public void describePost(MethodInfo info) {
    this.addStandardPostOrPutRequestInfo(info);

    info.setDocumentation("Method to send a new mail");

    ParameterInfo paramWait = new ParameterInfo("wait", false, "xs:boolean", ParameterStyle.TEMPLATE,
        "Synchronous if true, Asynchronous if false");
    paramWait.setDefaultValue("false");

    info.getRequest().getParameters().add(paramWait);

    this.addStandardSimpleResponseInfo(info);
    this.addStandardInternalServerErrorInfo(info);
  }

  /**
   * Get the mail from representation
   * 
   * @param representation
   *          Request entity
   * @param variant
   *          the variant used
   * @return a mail corresponding to the representation
   */
  public Mail getObject(Representation representation, Variant variant) {
    Mail input = null;
    if (MediaType.APPLICATION_JSON.isCompatible(representation.getMediaType())) {
      // Parse the JSON representation to get the mail bean
      try {
        input = new JacksonRepresentation<Mail>(representation, Mail.class).getObject();
      } catch (IOException e) {
        getContext().getLogger().severe(e.getMessage());
      }
    }
    else if (representation instanceof ObjectRepresentation<?>) {
      try {
        Object object = ((ObjectRepresentation<?>) representation).getObject();
        if (object instanceof Mail) {
          input = (Mail) object;
        }
      }
      catch (IOException e) {
        getLogger().log(Level.INFO, null, e);
      }
      if (input == null) {
        throw new RuntimeException("Only Mail object accepted for MailResource.POST( ObjectRepresentation)");
      }
    }
    return input;
  }

  /**
   * Response to Representation
   * 
   * @param response
   *          the response to transform
   * @param media
   *          the media type
   * @return Representation
   */
  public Representation getRepresentation(fr.cnes.sitools.common.model.Response response, MediaType media) {
    if (media.isCompatible(MediaType.APPLICATION_JAVA_OBJECT)) {
      return new ObjectRepresentation<fr.cnes.sitools.common.model.Response>(response);
    }

    XStream xstream = XStreamFactory.getInstance().getXStream(media, getContext());
    xstream.alias("response", fr.cnes.sitools.common.model.Response.class);
    xstream.alias("mailing", MailConfiguration.class);

    // Parce que les annotations ne sont apparemment prises en compte
    xstream.omitField(Response.class, "itemName");
    xstream.omitField(Response.class, "itemClass");

    // xstream.addImplicitCollection(Response.class, "data",Group.class);
    // pour supprimer @class sur data
    if (response.getItemClass() != null) {
      xstream.alias("item", Object.class, response.getItemClass());
    }
    if (response.getItemName() != null) {
      xstream.aliasField(response.getItemName(), fr.cnes.sitools.common.model.Response.class, "item");
    }

    XstreamRepresentation<fr.cnes.sitools.common.model.Response> rep = new XstreamRepresentation<fr.cnes.sitools.common.model.Response>(
        media, response);
    rep.setXstream(xstream);
    return rep;
  }

  /**
   * Send mail in asynchronous mode
   * 
   * @param context
   *          Context
   * @param request
   *          prepared request to mail server
   */
  public void sendMailAsynchrone(Context context, Request request) {

    getApplication().getTaskService().execute(new RequestDispatcherTask(context, request));

    // at the moment, no needs to getResponse nor result status for the task.

  }

}

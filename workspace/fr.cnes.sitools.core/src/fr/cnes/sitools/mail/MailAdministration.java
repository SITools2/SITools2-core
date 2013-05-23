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
package fr.cnes.sitools.mail;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

import org.restlet.Client;
import org.restlet.Component;
import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.data.Protocol;
import org.restlet.data.Status;
import org.restlet.ext.wadl.ApplicationInfo;
import org.restlet.ext.wadl.DocumentationInfo;
import org.restlet.resource.ResourceException;
import org.restlet.routing.Router;

import fr.cnes.sitools.common.SitoolsSettings;
import fr.cnes.sitools.common.application.SitoolsApplication;
import fr.cnes.sitools.common.model.Category;

/**
 * Application for managing mails (sending / receiving)
 * 
 * Aims at : 
 * 0. Sending mail service according to sitools.properties
 * 
 * 1. Administrator must be able to configure the mail settings (protocol, server) with
 * the administration GUI, and enable/disable this service. SMTP/SMTPS : the key file SSL must be present on the server.
 * 
 * 2. Administrator must be able to configure and manage the mail income for an applicative 
 * mail account (POP/POPS) : the key file SSL must be present on the server.
 * 
 * @author jp.boignard (AKKA Technologies)
 * 
 * @see org.restlet.test.engine.JavaMailTestCase.java
 * 
 */
public final class MailAdministration extends SitoolsApplication {

  /** Store for keys */
  private String trustStore = "certificats/sitools.keystore";

  /** Component */
  private Component component = null;

  /** Client list concerning mail management */
  private Map<String, Client> mailClients = new ConcurrentHashMap<String, Client>();
  

  /**
   * Constructor
   * 
   * @param context
   *          RESTlet context
   * @param server
   *          the server component
   */
  public MailAdministration(Context context, Component server) {
    super(context);
    this.component = server;

    // LOADING SSL KEYSTOREFILE
    // TODO _TRUSTSTORE à récupérer du contexte sinon valeur par défaut
    // TODO à mettre en place au niveau du composant ou de la sécurité ?
    // TODO voir si même chose que cryptage https.
    try {
      final File keyStoreFile = new File(trustStore);
      if (keyStoreFile.exists()) {
        System.setProperty("javax.net.ssl.trustStore", keyStoreFile.getCanonicalPath());
      }
    }
    catch (IOException e) {
      getLogger().warning("Setting javax.net.ssl.trustStore failed.");
      throw new ResourceException(Status.SERVER_ERROR_INTERNAL, null, e);
    }

    SitoolsSettings settings = getSettings();
    
    try {
      // Loading mail configuration for Sitools2 administrator account
      setupMailClient(Protocol.SMTP, settings.getString("Starter.mail.send.server"),
          Boolean.parseBoolean(settings.getString("Starter.mail.send.debug")),
          Boolean.parseBoolean(settings.getString("Starter.mail.send.tls")));
    }
    catch (Exception e) {
      getLogger().warning("MailAdministration initialization failed. Check your mail settings in sitools.properties and restart server.");
      getLogger().log(Level.INFO, null, e);
    }
    
    // register client protocol according to the configuration
  }

  @Override
  public void sitoolsDescribe() {
    setCategory(Category.SYSTEM);
    setName("MailAdministration");
    setDescription("Application used to send emails");
  }

  @Override
  public Restlet createInboundRoot() {

    Router router = new Router(getContext());

    // GET mails / POST mail
    router.attachDefault(MailResource.class);

    return router;
  }

  /**
   * Enregistrement d'un client pour le serveur / protocole mail en question avec / sans SSL
   * 
   * @param protocol
   *          protocol used
   * @param server
   *          the server used
   * @param debug
   *          debug mode on/off
   * @param startTls
   *          starts TLS
   * @throws Exception
   *           if setup fails
   */
  public void setupMailClient(Protocol protocol, String server, boolean debug, boolean startTls) throws Exception {
    Client client = mailClients.get(server);
    if (client != null) {
      mailClients.remove(server);
      client.stop();
      component.getClients().remove(client);
    }

    client = new Client(this.getContext(), protocol);
    client.getContext().getParameters().add("debug", Boolean.toString(debug));
    client.getContext().getParameters().add("startTls", Boolean.toString(startTls).toLowerCase());
    client.getContext().getParameters().add("representationMessageClass", SitoolsRepresentationMessage.class.getName());
    mailClients.put(server, client);
    component.getClients().add(client);
  }

  @Override
  public ApplicationInfo getApplicationInfo(Request request, Response response) {
    ApplicationInfo result = super.getApplicationInfo(request, response);
    DocumentationInfo docInfo = new DocumentationInfo("Mail administration for SITools2.");
    docInfo.setTitle("API documentation.");
    result.getDocumentations().add(docInfo);
    return result;
  }

}

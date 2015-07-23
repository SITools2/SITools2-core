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
package fr.cnes.sitools;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.restlet.Client;
import org.restlet.Component;
import org.restlet.Context;
import org.restlet.Request;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Preference;
import org.restlet.data.Protocol;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.engine.Engine;
import org.restlet.ext.jackson.JacksonRepresentation;
import org.restlet.ext.xstream.XstreamRepresentation;
import org.restlet.representation.Representation;

import com.thoughtworks.xstream.XStream;

import fr.cnes.sitools.applications.PublicApplication;
import fr.cnes.sitools.common.SitoolsSettings;
import fr.cnes.sitools.common.SitoolsXStreamRepresentation;
import fr.cnes.sitools.common.XStreamFactory;
import fr.cnes.sitools.common.application.ContextAttributes;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.datasource.jdbc.business.SitoolsSQLDataSource;
import fr.cnes.sitools.datasource.jdbc.business.SitoolsSQLDataSourceFactory;
import fr.cnes.sitools.mail.MailAdministration;
import fr.cnes.sitools.security.JDBCUsersAndGroupsStore;
import fr.cnes.sitools.security.UsersAndGroupsAdministration;
import fr.cnes.sitools.security.challenge.ChallengeToken;
import fr.cnes.sitools.security.challenge.ChallengeTokenContainer;
import fr.cnes.sitools.security.model.User;
import fr.cnes.sitools.common.Consts;
import fr.cnes.sitools.util.RIAPUtils;

/**
 * Test Reset an user Password
 * 
 * @since UserStory : Réinitialiser le mot de passe, Release 4 - Sprint : 3
 * 
 * @author b.fiorito (AKKA Technologies)
 * 
 */
@Ignore
public abstract class AbstractResetPasswordTestCase extends AbstractSitoolsTestCase {
  /** Name of the token param */
  private static final String TOKEN_PARAM_NAME = "cdChallengeMail";

  /**
   * Restlet Component for server
   */
  private Component component = null;
  private ChallengeToken challengeTokenContainer;

  private JDBCUsersAndGroupsStore ugstore;

  private SitoolsSQLDataSource ds;

  /**
   * relative url for inscription management REST API
   * 
   * @return url
   */
  protected String getAttachUrl() {
    return SITOOLS_URL + SitoolsSettings.getInstance().getString(Consts.APP_CLIENT_PUBLIC_URL);
  }

  /**
   * absolute url for inscription management REST API
   * 
   * @return url
   */
  protected String getBaseUrl() {
    return super.getBaseUrl() + SitoolsSettings.getInstance().getString(Consts.APP_CLIENT_PUBLIC_URL);
  }

  @Before
  @Override
  /**
   * Create component, store and application and start server
   * @throws java.lang.Exception
   */
  public void setUp() throws Exception {
    SitoolsSettings settings = SitoolsSettings.getInstance();

    if (this.component == null) {
      this.component = createTestComponent(settings);

      // ============================
      // MAIL INTERNAL APPLICATION
      Context ctxMail = this.component.getContext().createChildContext();
      ctxMail.getAttributes().put(ContextAttributes.SETTINGS, settings);

      // Application
      MailAdministration mailAdministration = new MailAdministration(ctxMail, component);

      component.getInternalRouter().attach(settings.getString(Consts.APP_MAIL_ADMIN_URL), mailAdministration);

      // USERS AND GROUPS
      // Context
      Context ctxUAG = this.component.getContext().createChildContext();
      ctxUAG.getAttributes().put(ContextAttributes.SETTINGS, settings);

      if (ds == null) {
        ds = SitoolsSQLDataSourceFactory.getInstance().setupDataSource(
            SitoolsSettings.getInstance().getString("Tests.PGSQL_DATABASE_DRIVER"),
            SitoolsSettings.getInstance().getString("Tests.PGSQL_DATABASE_URL"),
            SitoolsSettings.getInstance().getString("Tests.PGSQL_DATABASE_USER"),
            SitoolsSettings.getInstance().getString("Tests.PGSQL_DATABASE_PASSWORD"),
            SitoolsSettings.getInstance().getString("Tests.PGSQL_DATABASE_SCHEMA"));

      }

      if (ugstore == null) {
        ugstore = new JDBCUsersAndGroupsStore("SitoolsJDBCStore", ds, ctxUAG);
      }

      ctxUAG.getAttributes().put(ContextAttributes.APP_STORE, ugstore);

      UsersAndGroupsAdministration anApplication = new UsersAndGroupsAdministration(ctxUAG);

      // attach to the internatl router
      component.getInternalRouter().attach(settings.getString(Consts.APP_SECURITY_URL), anApplication);

      // USERS AND GROUPS
      // Context
      Context context = this.component.getContext().createChildContext();

      // Directory
      String publicAppPath = settings.getString(Consts.APP_PATH) + settings.getString(Consts.APP_CLIENT_PUBLIC_PATH);
      context.getAttributes().put(ContextAttributes.SETTINGS, settings);

      long cacheTime = settings.getLong("Security.challenge.cacheTime");
      long cacheSize = settings.getLong("Security.challenge.cacheSize");

      context.getAttributes().put("Security.filter.captcha.enabled", false);

      challengeTokenContainer = new ChallengeTokenContainer(cacheTime, cacheSize);
      context.getAttributes().put("Security.challenge.ChallengeTokenContainer", challengeTokenContainer);

      PublicApplication application = new PublicApplication(context, publicAppPath, getBaseUrl());

      this.component.getDefaultHost().attach(getAttachUrl(), application);

    }

    if (!this.component.isStarted()) {
      this.component.start();
    }
  }

  @After
  @Override
  /**
   * Stop server
   * @throws java.lang.Exception
   */
  public void tearDown() throws Exception {
    super.tearDown();
    this.component.stop();
    this.component = null;
    this.challengeTokenContainer = null;
  }

  /**
   * Test Reset the user password without API
   */
  @Test
  public void testResetPassword() {
    docAPI.setActive(false);
    User myUser = new User("identifier", "mynewPass%", "", "", "email@website.fr");

    updateUserWrongToken(myUser, "testToken");
    String token = challengeTokenContainer.getToken(myUser.getIdentifier());
    updateUser(myUser, token);
    updateUserWrongToken(myUser, token);

  }

  /**
   * Test Reset the user password with API activate
   */
  // @Test
  public void testResetPasswordAPI() {
    try {
      docAPI.setActive(true);
      docAPI.appendChapter("Reset User Password API");
      docAPI.appendSubChapter("Reset User Password", "reset");
      User myUser = new User("identifier", "mynewPass%", "", "", "email@website.fr");
      String token = challengeTokenContainer.getToken(myUser.getIdentifier());
      updateUser(myUser, token);
    }
    finally {
      docAPI.close();
    }

  }

  /**
   * Invoke PUT
   * 
   * @param user
   *          the user to update
   * @param token
   *          the token
   */
  public void updateUser(User user, String token) {
    Reference ref = new Reference(getBaseUrl() + "/resetPassword");
    ref.addQueryParameter(TOKEN_PARAM_NAME, token);

    Representation appRep = getRepresentation(user, getMediaTest());
    String url = ref.toString();
    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      parameters.put("PUT", "A <i>User</i> object with <b>identifier</b> and <b>email</b>");
      putDocAPI(url, "", appRep, parameters, url);
    }
    else {
      final Client client = new Client(Protocol.HTTP);
      Request request = new Request(Method.PUT, url);
      request.setEntity(appRep);

      org.restlet.Response response = null;
      try {

        ArrayList<Preference<MediaType>> objectMediaType = new ArrayList<Preference<MediaType>>();
        objectMediaType.add(new Preference<MediaType>(getMediaTest()));
        request.getClientInfo().setAcceptedMediaTypes(objectMediaType);

        response = client.handle(request);

        assertNotNull(response);
        assertTrue(response.getStatus().isSuccess());

        Response resp = getResponse(getMediaTest(), response.getEntity(), User.class, false);
        assertTrue(resp.getMessage(), resp.getSuccess());

      }
      finally {
        if (response != null) {
          RIAPUtils.exhaust(response);
        }
      }

    }
  }

  /**
   * Invoke PUT
   * 
   * @param user
   *          the user to update
   */
  public void updateUserWrongToken(User user, String token) {
    Reference ref = new Reference(getBaseUrl() + "/resetPassword");
    ref.addQueryParameter(TOKEN_PARAM_NAME, token);

    String url = ref.toString();
    if (docAPI.isActive()) {
      Representation appRep = getRepresentation(user, getMediaTest());
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      parameters.put("PUT", "A <i>User</i> object with <b>identifier</b> and <b>email</b>");
      putDocAPI(url, "", appRep, parameters, url);
    }
    else {
      Representation appRep = getRepresentation(user, getMediaTest());
      final Client client = new Client(Protocol.HTTP);
      Request request = new Request(Method.PUT, url);
      request.setEntity(appRep);

      org.restlet.Response response = null;
      try {
        response = client.handle(request);

        assertNotNull(response);
        assertTrue(response.getStatus().isError());
        assertEquals(Status.CLIENT_ERROR_GONE, response.getStatus());

      }
      finally {
        if (response != null) {
          RIAPUtils.exhaust(response);
        }
      }
    }
  }

  // ----------------------------------------------------------------
  // USER REPRESENTATIONS

  /**
   * Builds XML or JSON Representation of Project for Create and Update methods.
   * 
   * @param item
   *          Project
   * @param media
   *          APPLICATION_XML or APPLICATION_JSON
   * @return XML or JSON Representation
   */
  public static Representation getRepresentation(User item, MediaType media) {
    if (media.equals(MediaType.APPLICATION_JSON)) {
      return new JacksonRepresentation<User>(item);
    }
    else if (media.equals(MediaType.APPLICATION_XML)) {
      XStream xstream = XStreamFactory.getInstance().getXStream(media, false);
      XstreamRepresentation<User> rep = new XstreamRepresentation<User>(media, item);
      configure(xstream);
      rep.setXstream(xstream);
      return rep;
    }
    else {
      Engine.getLogger(AbstractSitoolsTestCase.class.getName()).warning("Only JSON or XML supported in tests");
      return null;
    }
  }

  /**
   * Configures XStream mapping of Response object with ConverterModel content.
   * 
   * @param xstream
   *          XStream
   */
  private static void configure(XStream xstream) {
    xstream.autodetectAnnotations(false);
    xstream.alias("response", Response.class);

    // Parce que les annotations ne sont apparemment prises en compte
    xstream.omitField(Response.class, "itemName");
    xstream.omitField(Response.class, "itemClass");
  }

  // ----------------------------------------------------------------
  // RESPONSE REPRESENTATION

  /**
   * Get response
   * 
   * @param media
   *          the media type
   * @param representation
   *          the representation to use
   * @param dataClass
   *          the class name
   * @param isArray
   *          true if there are many objects
   * @return response
   */
  public static Response getResponse(MediaType media, Representation representation, Class<?> dataClass, boolean isArray) {
    try {
      if (!media.isCompatible(MediaType.APPLICATION_JSON) && !media.isCompatible(MediaType.APPLICATION_XML)) {
        Engine.getLogger(AbstractSitoolsTestCase.class.getName()).warning("Only JSON or XML supported in tests");
        return null;
      }

      XStream xstream = XStreamFactory.getInstance().getXStreamReader(media);
      xstream.autodetectAnnotations(false);
      xstream.alias("response", Response.class);

      if (isArray) {
        xstream.addImplicitCollection(Response.class, "data", dataClass);
      }
      else {
        xstream.alias("item", dataClass);
        xstream.alias("item", Object.class, dataClass);

      }
      xstream.aliasField("data", Response.class, "data");

      SitoolsXStreamRepresentation<Response> rep = new SitoolsXStreamRepresentation<Response>(representation);
      rep.setXstream(xstream);

      if (media.isCompatible(getMediaTest())) {
        Response response = rep.getObject("response");

        return response;
      }
      else {
        Engine.getLogger(AbstractSitoolsTestCase.class.getName()).warning("Only JSON or XML supported in tests");
        return null; // TODO complete test for XML, Object
      }
    }
    finally {
      RIAPUtils.exhaust(representation);
    }
  }

}

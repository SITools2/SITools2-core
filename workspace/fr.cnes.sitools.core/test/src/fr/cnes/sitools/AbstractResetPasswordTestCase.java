 /*******************************************************************************
 * Copyright 2010-2013 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Test;
import org.restlet.data.MediaType;
import org.restlet.engine.Engine;
import org.restlet.ext.jackson.JacksonRepresentation;
import org.restlet.ext.xstream.XstreamRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;

import com.thoughtworks.xstream.XStream;

import fr.cnes.sitools.common.SitoolsXStreamRepresentation;
import fr.cnes.sitools.common.XStreamFactory;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.security.model.User;
import fr.cnes.sitools.util.RIAPUtils;

/**
 * Test Reset an user Password
 * 
 * @since UserStory : Réinitialiser le mot de passe, Release 4 - Sprint : 3
 * 
 * @author b.fiorito (AKKA Technologies)
 * 
 */
public abstract class AbstractResetPasswordTestCase extends AbstractSitoolsServerTestCase {

  /**
   * Test Reset the user password without API
   */
  @Test
  public void testResetPassword() {
    docAPI.setActive(false);
    User myUser = new User("identifier", "", "", "", "email@website.fr");
    updateUser(myUser);

  }

  /**
   * Test Reset the user password with API activate
   */
  @Test
  public void testResetPasswordAPI() {
    docAPI.setActive(true);
    docAPI.appendChapter("Reset User Password API");
    docAPI.appendSubChapter("Reset User Password", "reset");
    User myUser = new User("identifier", "", "", "", "email@website.fr");
    updateUser(myUser);

  }

  /**
   * Invoke PUT
   * 
   * @param user
   *          the user to update
   */
  public void updateUser(User user) {
    if (docAPI.isActive()) {
      Representation appRep = getRepresentation(user, getMediaTest());
      String url = getBaseUrl() + "/resetPassword";
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      parameters.put("PUT", "A <i>User</i> object with <b>identifier</b> and <b>email</b>");
      putDocAPI(url, "", appRep, parameters, url);
    }
    else {
      ClientResource crUsers = new ClientResource(getBaseUrl() + "/resetPassword");

      Representation result = crUsers.put(getRepresentation(user, getMediaTest()), getMediaTest());
      assertNotNull(result);
      assertTrue(crUsers.getStatus().isSuccess());

      Response response = getResponse(getMediaTest(), result, User.class, false);
      assertTrue(response.getSuccess());
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
      return null; // TODO complete test with ObjectRepresentation
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

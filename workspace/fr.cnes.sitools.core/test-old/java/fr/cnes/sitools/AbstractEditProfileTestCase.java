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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Ignore;
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
import fr.cnes.sitools.util.Property;
import fr.cnes.sitools.util.RIAPUtils;

/**
 * Test Editing user profile
 * 
 * @since UserStory : Edition profil utilisateur - Release 4 - Sprint : 3
 * 
 * @author b.fiorito (AKKA Technologies)
 */
@Ignore
public class AbstractEditProfileTestCase extends AbstractSitoolsServerTestCase {

  /**
   * Test editing the user profile without API
   */
  @Test
  public void testEditProfile() {
    docAPI.setActive(false);
    String login = "identifier";
    User user = getUser(login);

    modifyUserProperties(user);

    checkUserProperties(user);
    updateUser(user, true);

    deleteUserProperties(user);
    updateUser(user, false);

  }

  /**
   * Invoke GET
   * 
   * @param identifier
   *          the user login
   * @return user
   */
  public User getUser(String identifier) {
    ClientResource crUsers = new ClientResource(getBaseUrl() + "/editProfile/" + identifier);
    Representation result = crUsers.get(getMediaTest());
    assertNotNull(result);
    assertTrue(crUsers.getStatus().isSuccess());

    Response resp = getResponse(getMediaTest(), result, User.class);
    assertNotNull(resp);
    assertTrue(resp.getSuccess());
    assertNotNull(resp.getItem());
    User usr = (User) resp.getItem();
    assertNotNull(usr);

    return usr;

  }

  /**
   * Invoke PUT
   * 
   * @param user
   *          the user to update
   * @param modif
   *          if properties have been modified
   */
  public void updateUser(User user, boolean modif) {
    if (modif) {
      ClientResource crUsers = new ClientResource(getBaseUrl() + "/editProfile/" + user.getIdentifier());
      Representation result = crUsers.put(getRepresentation(user, getMediaTest()), getMediaTest());
      assertNotNull(result);
      assertTrue(crUsers.getStatus().isSuccess());
      Response response = getResponse(getMediaTest(), result, User.class, false);
      assertFalse(response.getSuccess());
    }
    else {
      ClientResource crUsers = new ClientResource(getBaseUrl() + "/editProfile/" + user.getIdentifier());
      Representation result = crUsers.put(getRepresentation(user, getMediaTest()), getMediaTest());
      assertNotNull(result);
      assertTrue(crUsers.getStatus().isSuccess());
      Response response = getResponse(getMediaTest(), result, User.class, false);
      assertTrue(response.getSuccess());
    }
  }

  /**
   * Modify ONLY Editable user properties
   * 
   * @param user
   *          the user to add properties
   */
  public void modifyUserProperties(User user) {
    List<Property> listProp = user.getProperties();
    for (Property prop : listProp) {
      if (prop.getScope().equals("Editable")) {
        prop.setValue("modified");
      }
    }
    user.setProperties(listProp);
  }

  /**
   * Add user properties
   * 
   * @param user
   *          the user to add properties
   */
  public void addUserProperties(User user) {
    List<Property> listProp = user.getProperties();
    listProp.add(new Property("test-add", "added", "Hidden"));
    user.setProperties(listProp);
  }

  /**
   * Delete user properties
   * 
   * @param user
   *          the user to delete properties
   */
  public void deleteUserProperties(User user) {
    List<Property> listProp = user.getProperties();
    listProp.remove(new Property("test-add", "added", "Hidden"));
    user.setProperties(listProp);
  }

  /**
   * Check user properties
   * 
   * @param user
   *          the user to update
   */
  public void checkUserProperties(User user) {
    User userDb = getUser(user.getIdentifier());

    for (Property propDb : userDb.getProperties()) {
      for (Property prop : user.getProperties()) {
        if (propDb.getScope().equals("ReadOnly") && (propDb.getScope().equals("ReadOnly"))
            && (propDb.getName().equals(prop.getName()))) {
          assertEquals(propDb, prop);
        }
        assertNotSame(prop.getScope(), "Hidden");
      }
      if (propDb.getScope().equals("Hidden")) {
        userDb.getProperties().remove(propDb);
      }
    }
    assertEquals(userDb.getProperties().size(), user.getProperties().size());

    addUserProperties(user);
    assertNotSame(userDb.getProperties().size(), user.getProperties().size());

  }

  // ----------------------------------------------------------------
  // USER REPRESENTATIONS

  /**
   * Builds XML or JSON Representation of user
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

  // ------------------------------------------------------------
  // RESPONSE REPRESENTATION

  /**
   * REST API Response Representation wrapper for single or multiple items expexted
   * 
   * @param media
   *          MediaType expected
   * @param representation
   *          service response representation
   * @param dataClass
   *          class expected for items of the Response object
   * @return Response
   */
  public static Response getResponse(MediaType media, Representation representation, Class<?> dataClass) {
    return getResponse(media, representation, dataClass, false);
  }

  /**
   * REST API Response Representation wrapper for single or multiple items expexted
   * 
   * @param media
   *          MediaType expected
   * @param representation
   *          service response representation
   * @param dataClass
   *          class expected for items of the Response object
   * @param isArray
   *          if true wrap the data property else wrap the item property
   * @return Response
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
      xstream.alias("user", User.class);

      if (isArray) {
        xstream.addImplicitCollection(Response.class, "data", dataClass);
      }
      else {
        xstream.alias("item", dataClass);
        xstream.alias("item", Object.class, dataClass);

        if (dataClass == User.class) {
          xstream.aliasField("user", Response.class, "item");
          if (media.equals(MediaType.APPLICATION_JSON)) {
            xstream.addImplicitCollection(User.class, "properties", Property.class);
          }
        }

      }
      xstream.aliasField("data", Response.class, "data");

      SitoolsXStreamRepresentation<Response> rep = new SitoolsXStreamRepresentation<Response>(representation);
      rep.setXstream(xstream);

      if (media.isCompatible(getMediaTest())) {
        Response response = rep.getObject("response");

        return response;
      }
      else {
        Engine.getLogger(AbstractSitoolsTestCase.class.getName()).warning("Only JSON and XML are supported in tests");
        return null;
      }
    }
    finally {
      RIAPUtils.exhaust(representation);
    }
  }
}

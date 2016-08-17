/*******************************************************************************
 * Copyright 2010-2016 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import fr.cnes.sitools.security.authorization.client.ResourceAuthorization;
import fr.cnes.sitools.security.authorization.client.RoleAndMethodsAuthorization;
import fr.cnes.sitools.server.Consts;
import fr.cnes.sitools.utils.GetRepresentationUtils;
import fr.cnes.sitools.utils.GetResponseUtils;
import org.junit.Test;
import org.restlet.data.ChallengeResponse;
import org.restlet.data.ChallengeScheme;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.engine.Engine;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;

import com.thoughtworks.xstream.XStream;

import fr.cnes.sitools.common.SitoolsSettings;
import fr.cnes.sitools.common.SitoolsXStreamRepresentation;
import fr.cnes.sitools.common.XStreamFactory;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.role.model.Role;
import fr.cnes.sitools.security.model.UserRole;
import fr.cnes.sitools.util.Property;
import fr.cnes.sitools.util.RIAPUtils;

public class AbstractUserRoleTestCase extends AbstractSitoolsServerTestCase {

  private String username = "admin";

  private String userpwd = "admin";

  private String appName = "";

  private SitoolsSettings settings = SitoolsSettings.getInstance();

  /**
   *
   */
  @Test
  public void testUserRole() {
    docAPI.setActive(false);
    List<Role> expectedRoles = new ArrayList<Role>();
    Role adminRole = new Role();
    adminRole.setName("Administrator");
    expectedRoles.add(adminRole);
    Role publicRole = new Role();
    publicRole.setName("public");
    expectedRoles.add(publicRole);

    getUserRole(username, userpwd, expectedRoles);

    getUserRoleNoUserError();
  }

  /**
   *
   */
  @Test
  public void testUserRoleDocAPI() {
    docAPI.setActive(true);
    List<Role> expectedRoles = new ArrayList<Role>();
    Role adminRole = new Role();
    adminRole.setName("Administrator");
    expectedRoles.add(adminRole);
    Role publicRole = new Role();
    publicRole.setName("public");
    expectedRoles.add(publicRole);

    docAPI.appendSubChapter("Get user role", "userRole");
    getUserRole(username, userpwd, expectedRoles);

    docAPI.close();
  }

  /**
   *
   */
  @Test
  public void testUserRoleCreateThenDelete() {
    docAPI.setActive(false);
    List<Role> expectedRoles = new ArrayList<Role>();
    Role adminRole = new Role();
    adminRole.setName("Administrator");
    expectedRoles.add(adminRole);
    Role publicRole = new Role();
    publicRole.setName("public");
    expectedRoles.add(publicRole);

    getUserRole(username, userpwd, expectedRoles);

    getUserRoleNoUserError();
  }

  /**
   * Create a role
   * Add the role to an authorization on an application
   * Delete the role => Should fail
   * Remove all authorization for this role
   * Delete the role => Should be OK
   */
//  @Test
  //TODO: FIX FOR XML
  public void testUserRoleCreateAndDeleteWhenUsedOnApplication() throws IOException {

    Role role = new Role();
    role.setId("role_tmp");
    role.setName("role_tmp");
    role.setDescription("A temporary role for tests");
    persistRole(role);

    ResourceAuthorization auth = createAuthorizationObject(role.getName());
    persistAuthorization(auth);

    deleteRole(role, false);

    deleteAuthorization(auth);

    deleteRole(role, true);

  }

  /**
   *
   * @param name
   * @param pwd
   * @param expectedRoles
   */
  public void getUserRole(String name, String pwd, List<Role> expectedRoles) {
    String url = getBaseUrl() + "/userRole";
    ChallengeResponse chal = new ChallengeResponse(ChallengeScheme.HTTP_BASIC, name, pwd);

    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      retrieveDocAPI(url, "", parameters, url, chal);
    }
    else {
      ClientResource cr = new ClientResource(url);
      cr.setChallengeResponse(chal);
      Representation result = cr.get(getMediaTest());
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());
      Response response = getResponse(getMediaTest(), result, UserRole.class);
      assertTrue(response.getSuccess());
      assertNotNull(response.getItem());

      UserRole userRole = (UserRole) response.getItem();
      assertNotNull(userRole.getRoles());

      assertEquals(name, userRole.getIdentifier());
      assertRoles(expectedRoles, userRole.getRoles());

      RIAPUtils.exhaust(result);

    }
  }

  private void getUserRoleNoUserError() {
    String url = getBaseUrl() + "/userRole";
    ClientResource cr = new ClientResource(url);
    Representation result = cr.get(getMediaTest());
    try {
      assertNotNull(result);
      Response response = getResponse(getMediaTest(), result, UserRole.class);
      assertFalse(response.getSuccess());
    }
    finally {
      RIAPUtils.exhaust(result);
    }

  }

  /**
   * Assert that the 2 list of roles are the same
   *
   * @param expectedRoles
   *          the expected list of roles
   * @param roles
   *          the actual list of roles
   */
  private void assertRoles(List<Role> expectedRoles, List<Role> roles) {
    if (expectedRoles != null && roles != null) {
      assertEquals(expectedRoles.size(), roles.size());
      for (Iterator<Role> iterator = expectedRoles.iterator(); iterator.hasNext(); ) {
        Role expectedRole = (Role) iterator.next();
        String roleName = expectedRole.getName();
        boolean found = false;
        for (Iterator<Role> iterator2 = roles.iterator(); iterator2.hasNext() && !found; ) {
          Role role = (Role) iterator2.next();
          if (role.getName().equals(roleName)) {
            found = true;
            iterator2.remove();
          }
        }
        if (found) {
          iterator.remove();
        }
        else {
          fail(roleName + " not found in the given list of roles");
        }
      }
    }

  }

  private String getBaseUrlRoles() {
    return getBaseUrl() + settings.getString(Consts.APP_ROLES_URL);
  }

  /**
   * Invokes POST method to create a new Role
   *
   * @param item
   *          Role
   */
  public void persistRole(Role item) {
    Representation rep = GetRepresentationUtils.getRepresentationRole(item, getMediaTest());
    ClientResource cr = new ClientResource(getBaseUrlRoles());
    docAPI.appendRequest(Method.POST, cr, rep);
    Representation result = cr.post(rep, getMediaTest());
    if (!docAPI.appendResponse(result)) {
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());

      Response response = GetResponseUtils.getResponseRole(getMediaTest(), result, Role.class);
      assertTrue(response.getSuccess());
      Role role = (Role) response.getItem();
      assertEquals(role.getName(), item.getName());
      assertEquals(role.getDescription(), item.getDescription());
    }
    RIAPUtils.exhaust(result);
  }


  /**
   * Invokes DELETE method for deleting Role with specified id.
   *
   * @param item
   *          Role
   */
  public void deleteRole(Role item, boolean expectSuccess) {
    String url = getBaseUrlRoles() + "/" + item.getId();
    ClientResource cr = new ClientResource(url);
    docAPI.appendRequest(Method.DELETE, cr);
    Representation result = cr.delete(getMediaTest());
    if (!docAPI.appendResponse(result)) {
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());
      if (expectSuccess) {
        Response response = GetResponseUtils.getResponseRole(getMediaTest(), result, Role.class);
        assertTrue(response.getMessage(), response.getSuccess());
      }
      else {
        Response response = GetResponseUtils
            .getResponseResourceAuthorization(getMediaTest(), result, ResourceAuthorization.class, true);
        assertFalse(response.getMessage(), response.getSuccess());
      }
    }
    RIAPUtils.exhaust(result);
  }

  private String getBaseUrlAuthorizations() {
    return getBaseUrl() + settings.getString(Consts.APP_AUTHORIZATIONS_URL);
  }

  /**
   * Create an object for tests
   *
   * @return ResourceAuthorization
   */
  private ResourceAuthorization createAuthorizationObject(String roleName) {
    ResourceAuthorization item = new ResourceAuthorization();
    item.setId("urn:uuid:SolrApplication:type:fr.cnes.sitools.solr.SolrApplication");
    item.setName("SolrApplication");
    item.setDescription("Sample Solr integration");
    item.setUrl("http://localhost:8182/sitools/solr");
    ArrayList<RoleAndMethodsAuthorization> authorizations = new ArrayList<RoleAndMethodsAuthorization>();
    RoleAndMethodsAuthorization aut = new RoleAndMethodsAuthorization();
    aut.setRole(roleName);
    aut.setAllMethod(true);
    aut.setGetMethod(true);
    authorizations.add(aut);
    item.setAuthorizations(authorizations);
    return item;
  }

  /**
   * Invoke POST
   *
   * @param item
   *          RsourceAuthorization
   * @throws IOException
   *           Exception when copying configuration files from TEST to data/TESTS
   */
  public void persistAuthorization(ResourceAuthorization item) throws IOException {
    Representation rep = GetRepresentationUtils.getRepresentationResourceAuthorization(item, getMediaTest());
    String url = getBaseUrlAuthorizations();
    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      putDocAPI(url, "", rep, parameters, url);
    }
    else {
      ClientResource cr = new ClientResource(url);
      Representation result = cr.post(rep, getMediaTest());
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());
      Response response = GetResponseUtils
          .getResponseResourceAuthorization(getMediaTest(), result, ResourceAuthorization.class, false);
      assertTrue(response.getSuccess());
      ResourceAuthorization authorization = (ResourceAuthorization) response.getItem();
      assertEquals(authorization.getName(), item.getName());
      assertEquals(authorization.getDescription(), item.getDescription());
      RIAPUtils.exhaust(result);
    }
  }

  /**
   * Invoke Delete
   *
   * @param item
   *          ResourceAuthorization
   * @throws IOException
   *           Exception when copying configuration files from TEST to data/TESTS
   */
  public void deleteAuthorization(ResourceAuthorization item) throws IOException {
    ClientResource cr = new ClientResource(getBaseUrlAuthorizations() + "/" + item.getId());
    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      parameters.put("identifier", "Identifier application");
      deleteDocAPI(getBaseUrlAuthorizations() + "/" + item.getId(), "", parameters,
          getBaseUrlAuthorizations() + "/%identifier%");
    }
    else {
      Representation result = cr.delete(getMediaTest());
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());
      Response response = getResponse(getMediaTest(), result, ResourceAuthorization.class, false);
      assertTrue(response.getSuccess());
      assertEquals(null, response.getTotal());
      RIAPUtils.exhaust(result);
    }
  }


  // ------------------------------------------------------------
  // RESPONSE REPRESENTATION WRAPPING

  /**
   * REST API Response wrapper for single item expected.
   *
   * @param media
   *          MediaType expected
   * @param representation
   *          service response representation
   * @param dataClass
   *          class expected in the item property of the Response object
   * @return Response the response.
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
  public static Response getResponse(MediaType media, Representation representation, Class<?> dataClass,
      boolean isArray) {
    try {
      if (!media.isCompatible(getMediaTest()) && !media.isCompatible(MediaType.APPLICATION_XML)) {
        Engine.getLogger(AbstractSitoolsTestCase.class.getName()).warning("Only JSON or XML supported in tests");
        return null;
      }

      XStream xstream = XStreamFactory.getInstance().getXStreamReader(media);
      xstream.alias("response", Response.class);
      xstream.alias("user", UserRole.class);
      xstream.aliasField("user", Response.class, "item");

      if (media.equals(MediaType.APPLICATION_JSON) && dataClass == UserRole.class) {
        xstream.addImplicitCollection(UserRole.class, "properties", Property.class);
        xstream.addImplicitCollection(UserRole.class, "roles", Role.class);

      }

      if (isArray) {
        if (media.equals(MediaType.APPLICATION_JSON)) {
          xstream.addImplicitCollection(Response.class, "data", dataClass);
        }
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
        return null; // TODO complete test with ObjectRepresentation
      }
    }
    finally {
      RIAPUtils.exhaust(representation);
    }
  }
}

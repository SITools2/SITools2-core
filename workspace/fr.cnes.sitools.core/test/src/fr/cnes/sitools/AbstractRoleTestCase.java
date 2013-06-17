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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.logging.Logger;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.restlet.Component;
import org.restlet.Context;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Protocol;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.ext.xstream.XstreamRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;

import com.thoughtworks.xstream.XStream;

import fr.cnes.sitools.common.SitoolsSettings;
import fr.cnes.sitools.common.SitoolsXStreamRepresentation;
import fr.cnes.sitools.common.XStreamFactory;
import fr.cnes.sitools.common.application.ContextAttributes;
import fr.cnes.sitools.common.model.Resource;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.common.store.SitoolsStore;
import fr.cnes.sitools.role.RoleApplication;
import fr.cnes.sitools.role.RoleStoreXML;
import fr.cnes.sitools.role.model.Role;
import fr.cnes.sitools.server.Consts;
import fr.cnes.sitools.util.RIAPUtils;

/**
 * Test du CRUD des roles
 * 
 * @author jp.boignard (AKKA Technologies)
 * 
 */
public abstract class AbstractRoleTestCase extends AbstractSitoolsTestCase {

  /**
   * static xml store instance for the test
   */
  private static SitoolsStore<Role> store = null;

  /**
   * Restlet Component for server
   */
  private Component component = null;

  /** A user id */
  private String userId = "admin";

  /** A user id */
  private String groupId = "administrateur";

  /**
   * absolute url for role management REST API
   * 
   * @return url
   */
  protected String getBaseUrl() {
    return super.getBaseUrl() + SitoolsSettings.getInstance().getString(Consts.APP_ROLES_URL);
  }

  /**
   * relative url for role management REST API
   * 
   * @return url
   */
  protected String getAttachUrl() {
    return SITOOLS_URL + SitoolsSettings.getInstance().getString(Consts.APP_ROLES_URL);
  }

  /**
   * Absolute path location for role store files
   * 
   * @return path
   */
  protected String getTestRepository() {
    return super.getTestRepository() + SitoolsSettings.getInstance().getString(Consts.APP_ROLES_STORE_DIR);
  }

  @Before
  @Override
  /**
   * Init and Start a server with RoleApplication
   * 
   * @throws java.lang.Exception
   */
  public void setUp() throws Exception {

    if (this.component == null) {
      this.component = new Component();
      this.component.getServers().add(Protocol.HTTP, getTestPort());
      this.component.getClients().add(Protocol.HTTP);
      this.component.getClients().add(Protocol.FILE);
      this.component.getClients().add(Protocol.CLAP);

      // Context
      Context ctx = this.component.getContext().createChildContext();
      ctx.getAttributes().put(ContextAttributes.SETTINGS, SitoolsSettings.getInstance());

      // clean directory prior to the test
      File storeDirectory = new File(getTestRepository());
      cleanDirectory(storeDirectory);
      if (store == null) {
        store = new RoleStoreXML(storeDirectory, ctx);
      }

      ctx.getAttributes().put(ContextAttributes.APP_STORE, store);

      this.component.getDefaultHost().attach(getAttachUrl(), new RoleApplication(ctx));
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
  }

  /**
   * Test CRUD Role with JSon format exchanges.
   */
  @Test
  public void testCRUD() {
    docAPI.setActive(false);

    assertNone();
    Role item = createObject("1000000");
    create(item);
    retrieve(item);
    retrieveByUsers(item);
    retrieveByGroups(item);
    update(item);
    // TODO implement methods
    // addUser(item, userId);
    // addGroup(item, groupId);
    delete(item);
    assertNone();
    createWadl(getBaseUrl(), "roles");

  }

  /**
   * Test CRUD Role with JSon format exchanges.
   */
  @Test
  public void testCRUD2docAPI() {
    docAPI.setActive(true);
    docAPI.appendChapter("Manipulating Role Collection");

    assertNone();
    Role item = createObject("1000000");
    docAPI.appendSubChapter("Creating a new Role", "create");
    create(item);

    docAPI.appendChapter("Manipulating an existing Role resource");

    docAPI.appendSubChapter("Retrieving a Role", "retrieving");
    retrieve(item);

    docAPI.appendSubChapter("Updating a Role", "updating");
    update(item);

    docAPI.appendSubChapter("Deleting a Role", "deleting");
    delete(item);
    docAPI.close();
    assertNone();

  }

  /**
   * Creates a new Role object with the specified identifier
   * 
   * @param id
   *          role identifier
   * @return Role
   */
  public Role createObject(String id) {
    Role item = new Role();
    item.setId(id);
    return item;
  }

  /**
   * Invokes POST method to create a new Role
   * 
   * @param item
   *          Role
   */
  public void create(Role item) {
    Representation rep = getRepresentation(item, getMediaTest());
    ClientResource cr = new ClientResource(getBaseUrl());
    docAPI.appendRequest(Method.POST, cr, rep);
    Representation result = cr.post(rep, getMediaTest());
    if (!docAPI.appendResponse(result)) {
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());

      Response response = getResponse(getMediaTest(), result, Role.class);
      assertTrue(response.getSuccess());
      Role prj = (Role) response.getItem();
      assertEquals(prj.getName(), item.getName());
      assertEquals(prj.getDescription(), item.getDescription());
    }
    RIAPUtils.exhaust(result);
  }

  /**
   * Invokes GET method for getting Role with specified id.
   * 
   * @param item
   *          Role
   */
  public void retrieve(Role item) {
    ClientResource cr = new ClientResource(getBaseUrl() + "/" + item.getId());
    docAPI.appendRequest(Method.GET, cr);

    Representation result = cr.get(getMediaTest());
    if (!docAPI.appendResponse(result)) {
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());

      Response response = getResponse(getMediaTest(), result, Role.class);
      assertTrue(response.getSuccess());
      Role prj = (Role) response.getItem();
      assertEquals(prj.getName(), item.getName());
      assertEquals(prj.getDescription(), item.getDescription());
    }
    RIAPUtils.exhaust(result);
  }

  /**
   * Invoke GET by users
   * 
   * @param item
   *          the role to get
   */
  public void retrieveByUsers(Role item) {
    ClientResource cr = new ClientResource(getBaseUrl() + "/" + item.getId() + "/users");
    docAPI.appendRequest(Method.GET, cr);

    Representation result = cr.get(getMediaTest());
    if (!docAPI.appendResponse(result)) {
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());

      Response response = getResponse(getMediaTest(), result, Role.class);
      assertTrue(response.getSuccess());
      assertTrue(response.getTotal() == 0);
    }
    RIAPUtils.exhaust(result);
  }

  /**
   * Invoke GET by users
   * 
   * @param item
   *          the role to get
   */
  public void retrieveByGroups(Role item) {
    ClientResource cr = new ClientResource(getBaseUrl() + "/" + item.getId() + "/groups");
    docAPI.appendRequest(Method.GET, cr);

    Representation result = cr.get(getMediaTest());
    if (!docAPI.appendResponse(result)) {
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());

      Response response = getResponse(getMediaTest(), result, Role.class);
      assertTrue(response.getSuccess());
      assertTrue(response.getTotal() == 0);
    }
    RIAPUtils.exhaust(result);
  }

  /**
   * Invokes PUT method for updating Role
   * 
   * @param item
   *          Role
   */
  public void update(Role item) {
    Representation rep = getRepresentation(item, getMediaTest());
    ClientResource cr = new ClientResource(getBaseUrl() + "/" + item.getId());
    docAPI.appendRequest(Method.PUT, cr, rep);
    Representation result = cr.put(rep, getMediaTest());
    if (!docAPI.appendResponse(result)) {
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());

      Response response = getResponse(getMediaTest(), result, Role.class);
      assertTrue(response.getSuccess());
      Role prj = (Role) response.getItem();
      assertEquals(prj.getName(), item.getName());
      assertEquals(prj.getDescription(), item.getDescription());
    }
    RIAPUtils.exhaust(result);
  }

  /**
   * Invokes DELETE method for deleting Role with specified id.
   * 
   * @param item
   *          Role
   */
  public void delete(Role item) {
    ClientResource cr = new ClientResource(getBaseUrl() + "/" + item.getId());
    docAPI.appendRequest(Method.DELETE, cr);
    Representation result = cr.delete(getMediaTest());
    if (!docAPI.appendResponse(result)) {
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());

      Response response = getResponse(getMediaTest(), result, Role.class);
      assertTrue(response.getSuccess());
    }
    RIAPUtils.exhaust(result);
  }

  /**
   * Invokes GET and asserts result response is an empty array.
   */
  public void assertNone() {
    ClientResource cr = new ClientResource(getBaseUrl());
    Representation result = cr.get(getMediaTest());
    assertNotNull(result);
    assertTrue(cr.getStatus().isSuccess());

    Response response = getResponse(getMediaTest(), result, Role.class);
    assertTrue(response.getSuccess());
    assertEquals(response.getTotal().intValue(), 0);
    RIAPUtils.exhaust(result);
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
  public static Response getResponse(MediaType media, Representation representation, Class<?> dataClass, boolean isArray) {
    try {
      if (!media.isCompatible(MediaType.APPLICATION_JSON) && !media.isCompatible(MediaType.APPLICATION_XML)) {
        Logger.getLogger(AbstractSitoolsTestCase.class.getName()).warning("Only JSON or XML supported in tests");
        return null;
      }

      XStream xstream = XStreamFactory.getInstance().getXStreamReader(media);
      xstream.autodetectAnnotations(false);
      xstream.alias("response", Response.class);
      xstream.alias("role", Role.class);
      // xstream.alias("dataset", Resource.class);

      if (isArray) {
        xstream.addImplicitCollection(Response.class, "data", dataClass);
      }
      else {
        xstream.alias("item", dataClass);
        xstream.alias("item", Object.class, dataClass);
        xstream.addImplicitCollection(Role.class, "users", Resource.class);
        // xstream.addImplicitCollection(Role.class, "groups", Resource.class);

        xstream.aliasField("users", Role.class, "users");
        // xstream.aliasField("groups", Role.class, "groups");

        if (dataClass == Role.class) {
          xstream.aliasField("role", Response.class, "item");
          // if (dataClass == DataSet.class)
          // xstream.aliasField("dataset", Response.class, "item");
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
        Logger.getLogger(AbstractSitoolsTestCase.class.getName()).warning("Only JSON or XML supported in tests");
        return null; // TODO complete test for XML, Object
      }
    }
    finally {
      RIAPUtils.exhaust(representation);
    }
  }

  /**
   * Builds XML or JSON Representation of Role for Create and Update methods.
   * 
   * @param item
   *          Role
   * @param media
   *          APPLICATION_XML or APPLICATION_JSON
   * @return XML or JSON Representation
   */
  public static Representation getRepresentation(Role item, MediaType media) {
    if (media.equals(MediaType.APPLICATION_JSON)) {
      return new JsonRepresentation(item);
    }
    else if (media.equals(MediaType.APPLICATION_XML)) {
      XStream xstream = XStreamFactory.getInstance().getXStream(media, false);
      XstreamRepresentation<Role> rep = new XstreamRepresentation<Role>(media, item);
      configure(xstream);
      rep.setXstream(xstream);
      return rep;
    }
    else {
      Logger.getLogger(AbstractSitoolsTestCase.class.getName()).warning("Only JSON or XML supported in tests");
      return null; // TODO complete test with ObjectRepresentation
    }
  }

  /**
   * Configure XStream mapping of a Response object
   * 
   * @param xstream
   *          XStream
   */
  private static void configure(XStream xstream) {
    xstream.autodetectAnnotations(false);
    xstream.alias("role", Role.class);
  }
}

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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.restlet.data.Method;
import org.restlet.ext.jackson.JacksonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;

import fr.cnes.sitools.common.SitoolsSettings;
import fr.cnes.sitools.server.Consts;
import fr.cnes.sitools.userstorage.model.DiskStorage;
import fr.cnes.sitools.userstorage.model.UserStorage;

/**
 * Test case for user storage management.
 * 
 * @author c.mozdzierz
 */
public abstract class AbstractUserStorageManagerServerTestCase extends AbstractSitoolsServerTestCase {

  /**
   * absolute url for role management REST API
   * 
   * @return url
   */
  protected String getBaseUrl() {
    return super.getBaseUrl() + SitoolsSettings.getInstance().getString(Consts.APP_USERSTORAGE_URL);
  }

  /**
   * Test of User storage management.
   */
  @Test
  public void adminUserStorageTest() {
    docAPI.setActive(false);
    getUserStorage();
    createUserStorage();
    createWadl(getBaseUrl(), "admin_storage");
  }

  /**
   * Produces documentation of user storage management API
   */
  @Test
  public void adminUserStorage2docAPITest() {
    docAPI.setActive(true);
    getUserStorage();
    createUserStorage();
    docAPI.close();
  }

  /**
   * Calls POST method for creating a new UserStorage
   */
  private void createUserStorage() {
    UserStorage us = new UserStorage();
    us.setUserId("show");
    us.setStatus(null);
    DiskStorage uds = new DiskStorage();
    uds.setUserStoragePath("D:/CNES-ULISSE/data/storage/show");
    us.setStorage(uds);

    JacksonRepresentation<UserStorage> rep = new JacksonRepresentation<UserStorage>(us);
    ClientResource cr = new ClientResource(getBaseUrl() + "/users");
    docAPI.appendRequest(Method.POST, cr, rep);

    Representation result = cr.post(rep);
    if (!docAPI.appendResponse(result)) {
      assertNotNull(result);
      // TODO TEST ASSERTION
    }

  }

  /**
   * Invokes GET and asserts result response is an empty array.
   */
  public void getUserStorage() {
    ClientResource cr = new ClientResource(getBaseUrl() + "/users");
    docAPI.appendRequest(Method.GET, cr);

    Representation result = cr.get(getMediaTest());
    docAPI.appendResponse(result);
    if (!docAPI.isActive()) {

      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());

      // TODO TEST ASSERTION
      // Response response = getResponse(getMediaTest(), result, Resource.class, true);
      // assertTrue(response.getSuccess());
      // assertEquals(response.getTotal().intValue(), 0);
    }
  }

}

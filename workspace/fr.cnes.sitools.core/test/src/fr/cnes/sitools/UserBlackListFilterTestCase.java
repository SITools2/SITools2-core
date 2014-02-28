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

import org.junit.Test;
import org.restlet.Client;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.ChallengeResponse;
import org.restlet.data.ChallengeScheme;
import org.restlet.data.Method;
import org.restlet.data.Protocol;
import org.restlet.data.Status;

import fr.cnes.sitools.util.RIAPUtils;

/**
 * Test the UserBlackListFilter
 * 
 * 
 * @author m.gond
 */
public class UserBlackListFilterTestCase extends AbstractSitoolsServerTestCase {
  /** The url to query */
  private static final String URL_ADMIN = getHostUrl() + "/sitools/datasets";
  /** The url to query */
  private static final String URL_USER_STORAGE = getHostUrl() + "/sitools/userstorage/admin/files/";
  /** NB_ALLOWED_REQ_BEFORE_BLACKLIST */
  private static final int NB_ALLOWED_REQ_BEFORE_BLACKLIST = settings.getInt("Starter.NB_ALLOWED_REQ_BEFORE_BLACKLIST");

  /**
   * Test
   */
  @Test
  public void testAdmin() {
    testBlocking(URL_ADMIN, NB_ALLOWED_REQ_BEFORE_BLACKLIST, "admin1", "admin1");
  }

  /**
   * Test
   */
  @Test
  public void testSecurityUser() {
    testBlocking(URL_USER_STORAGE, NB_ALLOWED_REQ_BEFORE_BLACKLIST, "admin2", "admin2");

  }

  /**
   * Test
   */
  @Test
  public void testBadAuthenticationThenGoodAuthentication() {
    String user = "admin";
    String password = "admin";
    String badPassword = "admin1";

    int nbRequests = NB_ALLOWED_REQ_BEFORE_BLACKLIST - 2;

    // Try 3 bad requests
    for (int i = 0; i < nbRequests; i++) {
      request(URL_ADMIN, Status.CLIENT_ERROR_UNAUTHORIZED, user, badPassword);
    }

    // Then do a good request
    request(URL_ADMIN, Status.SUCCESS_OK, user, password);

    // Try 3 bad requests, No blacklist because counter was set to 0 after good request
    for (int i = 0; i < nbRequests; i++) {
      request(URL_ADMIN, Status.CLIENT_ERROR_UNAUTHORIZED, user, badPassword);
    }
  }

  /**
   * Test
   */
  @Test
  public void testBlackListThenGoodAuthentication() {
    String user = "akka";
    String password = "akka";
    String badPassword = "akka1";

    // Try 3 bad requests
    for (int i = 0; i < NB_ALLOWED_REQ_BEFORE_BLACKLIST; i++) {
      request(URL_ADMIN, Status.CLIENT_ERROR_UNAUTHORIZED, user, badPassword);
    }

    // Then do a good request, user is blacklisted
    request(URL_ADMIN, Status.CLIENT_ERROR_FORBIDDEN, user, password);

  }

  /**
   * Test with bad authentication.
   * 
   * @param url
   *          the url to query
   * @param nbRequestsBeforeBlocking
   *          the number of bad calls needed before blacklisting the user
   * @param user
   *          the user
   * @param password
   *          the password
   */
  private void testBlocking(String url, int nbRequestsBeforeBlocking, String user, String password) {
    for (int i = 0; i < nbRequestsBeforeBlocking; i++) {
      request(url, Status.CLIENT_ERROR_UNAUTHORIZED, user, password);
    }

    request(url, Status.CLIENT_ERROR_FORBIDDEN, user, password);
  }

  /**
   * Request the provided url with the user and password. Expect the following status
   * 
   * @param url
   *          the url
   * @param status
   *          the status
   * @param user
   *          the user
   * @param password
   *          the password
   */
  private void request(String url, Status status, String user, String password) {
    final Client client = new Client(Protocol.HTTP);
    Request request = new Request(Method.GET, url);
    Response response = null;
    try {
      ChallengeResponse cr = new ChallengeResponse(ChallengeScheme.HTTP_BASIC, user, password);
      request.setChallengeResponse(cr);
      response = client.handle(request);

      assertNotNull(response);
      assertEquals(status.isError(), response.getStatus().isError());
      assertEquals(status.isSuccess(), response.getStatus().isSuccess());
      assertEquals(status, response.getStatus());

    }
    finally {
      if (response != null) {
        RIAPUtils.exhaust(response);
      }
    }
  }

}

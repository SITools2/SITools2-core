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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

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
 * Test the authorization mecanism of Sitools
 * 
 * 
 * @author m.gond
 */
public class SecurityAuthorizationTestCase extends SitoolsServerTestCase {

  private static String URL_ATTACH = "/application_test_security";

  @Test
  public void testWithAdmin() {
    String url = getHostUrl() + URL_ATTACH;
    final Client client = new Client(Protocol.HTTP);
    Request request = new Request(Method.GET, url);

    String user = "admin";
    String password = "admin";

    ChallengeResponse challenge = new ChallengeResponse(ChallengeScheme.HTTP_BASIC, user, password);
    request.setChallengeResponse(challenge);

    Response response = null;
    try {
      response = client.handle(request);

      assertNotNull(response);
      assertTrue(response.getStatus().isSuccess());
      assertEquals(Status.SUCCESS_OK, response.getStatus());

    }
    finally {
      if (response != null) {
        RIAPUtils.exhaust(response);
      }
    }
  }

  @Test
  public void testWithPublic() {
    String url = getHostUrl() + URL_ATTACH;
    final Client client = new Client(Protocol.HTTP);
    Request request = new Request(Method.GET, url);

    Response response = null;
    try {
      response = client.handle(request);

      assertNotNull(response);
      assertTrue("Status returned : "+ response.getStatus(), response.getStatus().isError());
      assertEquals("Status returned : "+ response.getStatus(),Status.CLIENT_ERROR_FORBIDDEN, response.getStatus());

    }
    finally {
      if (response != null) {
        RIAPUtils.exhaust(response);
      }
    }
  }

}

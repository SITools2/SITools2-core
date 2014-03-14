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

import java.util.Iterator;

import org.junit.Test;
import org.restlet.Client;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Method;
import org.restlet.data.Protocol;
import org.restlet.data.Status;

import fr.cnes.sitools.common.SitoolsSettings;
import fr.cnes.sitools.server.Consts;
import fr.cnes.sitools.util.RIAPUtils;

/**
 * Tests the security filter on an application It will test on 2 different applications -- Client-admin which is
 * configured in the intranet * With an ip address from the intranet => Will succeed * With an ip address out of the
 * intranet => Will fail -- Client-user which is configured in the extranet * With an ip address from the intranet =>
 * Will succeed * With an ip address out of the intranet => Will succeed
 * 
 * @author m.gond
 */
public class ApplicationFilterSecurityTestCase extends SitoolsServerTestCase {

  /** Client admin url, Client admin is an application configured for intranet */
  private static final String CLIENT_ADMIN = SitoolsSettings.getInstance().getString(Consts.APP_CLIENT_ADMIN_URL)
      + "/";

  /** Client user url, Client user is an application configured for extranet */
  private static final String CLIENT_USER = SitoolsSettings.getInstance().getString(Consts.APP_CLIENT_USER_URL)
      + "/";

  /**
   * Test
   */
  @Test
  public void test() {
    // test on Intranet application with Intranet Ip => SUCCESS
    retrieveAndSucceed(getBaseUrl() + CLIENT_ADMIN);

    // remove the loopback address, to simulate a call from extranet
    boolean found = false;
    for (Iterator<String> iterator = settings.getIntranetAddresses().iterator(); iterator.hasNext() && !found;) {
      String add = (String) iterator.next();
      if ("127.0.0.0".equals(add)) {
        iterator.remove();
        found = true;
      }
    }
    // test on Intranet application with a not Intranet Ip => FAILURE
    retrieveAndFail(getBaseUrl() + CLIENT_ADMIN);
    // test on Extranet application with not Intranet Ip => SUCCESS
    retrieveAndSucceed(getBaseUrl() + CLIENT_USER);

    // add the remove addresses to test with a intranet address
    settings.getIntranetAddresses().add("127.0.0.0");
    // test on Extranet application with Intranet Ip => SUCCESS
    retrieveAndSucceed(getBaseUrl() + CLIENT_USER);

  }

  /**
   * Invoke GET on the given url, with the given ipAddress, the call must succeed
   * 
   * @param url
   *          the url of the application
   * 
   * 
   */
  public void retrieveAndSucceed(String url) {
    final Client client = new Client(Protocol.HTTP);
    Request request = new Request(Method.GET, url);
    Response response = client.handle(request);
    try {
      assertNotNull(response);
      assertTrue(response.getStatus().isSuccess());
    }
    finally {
      RIAPUtils.exhaust(response);
    }
  }

  /**
   * Invoke GET on the given url, with the given ipAddress, the call must fail
   * 
   * @param url
   *          the url of the application
   * 
   * 
   */
  public void retrieveAndFail(String url) {
    final Client client = new Client(Protocol.HTTP);
    Request request = new Request(Method.GET, url);
    Response response = client.handle(request);
    try {
      assertNotNull(response);
      assertTrue(response.getStatus().isError());
      assertEquals(Status.CLIENT_ERROR_FORBIDDEN, response.getStatus());

    }
    finally {
      RIAPUtils.exhaust(response);
    }
  }

}

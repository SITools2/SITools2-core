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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.restlet.Application;
import org.restlet.data.ChallengeResponse;
import org.restlet.data.ChallengeScheme;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;

import fr.cnes.sitools.common.SitoolsSettings;
import fr.cnes.sitools.server.Consts;
import fr.cnes.sitools.server.Starter;

/**
 * SitoolsServerTestCase
 * 
 * @author jp.boignard (AKKA Technologies)
 */
public class SitoolsServerTestCase extends AbstractSitoolsServerTestCase {

  /**
   * Done before
   */
  @Before
  public void setUp() throws Exception {
    try {
      super.setUp();
    }
    catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  /**
   * Done after
   */
  @After
  public void tearDown() {
    try {
      super.tearDown();
    }
    catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  /**
   * test applications running TODO affiner la description du VirtualHost. Peut-être pas une application.
   */
  @Test
  public void testServerProperties() {
    // Authorizations
    Application app = Starter.getApplication(0);
    assertNotNull(app);
    assertNotNull(app.getAuthor());
    assertNotNull(app.getContext());
    assertNotNull(app.getDescription());
    assertNotNull(app.getRoles());
    assertNotNull(app.getStatusService());
    assertTrue(app.getStatusService().isStarted());
    assertEquals(app.getName(), "AuthorizationApplication");
  }

  /**
   * test applications running
   */
  @Test
  public void testGetApplications() {
    Representation result = null;
    ClientResource cr = null;

    // ACCES SANS AUTHENTIFICATION

    // Public
    Reference publicRef = new Reference(getBaseUrl()
        + SitoolsSettings.getInstance().getString(Consts.APP_CLIENT_PUBLIC_URL) + "/");
    cr = new ClientResource(publicRef);
    result = cr.get();
    assertEquals(Status.SUCCESS_OK.getCode(), cr.getStatus().getCode());
    assertNotNull(result);

    publicRef = new Reference(getBaseUrl() + SitoolsSettings.getInstance().getString(Consts.APP_CLIENT_PUBLIC_URL)
        + "/res/images/");
    cr = new ClientResource(publicRef);
    result = cr.get(MediaType.APPLICATION_JSON);
    assertEquals(Status.SUCCESS_OK.getCode(), cr.getStatus().getCode());
    assertNotNull(result);

    result = cr.get(MediaType.TEXT_URI_LIST);
    assertEquals(Status.SUCCESS_OK.getCode(), cr.getStatus().getCode());
    assertNotNull(result);

    // ClientAdmin
    Reference clientAdminRef = new Reference(getBaseUrl()
        + SitoolsSettings.getInstance().getString(Consts.APP_CLIENT_ADMIN_URL));
    cr = new ClientResource(clientAdminRef);
    result = cr.get();
    assertEquals(Status.SUCCESS_OK.getCode(), cr.getStatus().getCode());
    assertNotNull(result);

    // ClientPortal
    Reference clientPortalRef = new Reference(getBaseUrl()
        + SitoolsSettings.getInstance().getString(Consts.APP_CLIENT_PORTAL_URL) + "/index.html");
    cr = new ClientResource(clientPortalRef);
    result = cr.get();
    assertEquals(Status.SUCCESS_OK.getCode(), cr.getStatus().getCode());
    assertNotNull(result);
    
    

  }

  /**
   * test applications running
   */
  @Test
  public void testGetApplicationsWithAuthentication() {
    Representation result = null;
    ClientResource cr = null;

    // ACCES AVEC AUTHENTIFICATION
    ChallengeResponse challenge = new ChallengeResponse(ChallengeScheme.HTTP_BASIC, "admin", "admin");

    // ClientAdmin
    Reference clientAdminRef = new Reference(getBaseUrl()
        + SitoolsSettings.getInstance().getString(Consts.APP_CLIENT_ADMIN_URL));
    cr = new ClientResource(clientAdminRef);
    cr.setChallengeResponse(challenge);
    result = cr.get();
    assertEquals(Status.SUCCESS_OK.getCode(), cr.getStatus().getCode());
    assertNotNull(result);

    // ClientPortal
    Reference clientPortalRef = new Reference(getBaseUrl()
        + SitoolsSettings.getInstance().getString(Consts.APP_CLIENT_PORTAL_URL) + "/index.html");
    cr = new ClientResource(clientPortalRef);
    cr.setChallengeResponse(challenge);
    result = cr.get();
    assertEquals(Status.SUCCESS_OK.getCode(), cr.getStatus().getCode());
    assertNotNull(result);

    // Public
    Reference publicRef = new Reference(getBaseUrl()
        + SitoolsSettings.getInstance().getString(Consts.APP_CLIENT_PUBLIC_URL));
    cr = new ClientResource(publicRef);
    cr.setChallengeResponse(challenge);
    result = cr.get();
    assertEquals(Status.SUCCESS_OK.getCode(), cr.getStatus().getCode());
    assertNotNull(result);

  }

}

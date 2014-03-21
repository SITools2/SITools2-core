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
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;

import fr.cnes.sitools.common.SitoolsSettings;
import fr.cnes.sitools.server.Consts;
import fr.cnes.sitools.util.RIAPUtils;

/**
 * Test to reach the client user application
 * 
 * @author m.marseille (AKKA Technologies)
 */
public class ClientUserTestCase extends AbstractSitoolsServerTestCase {

  /** Dataset identifier for postgres tests */
  protected static final String CLIENT_USER = SitoolsSettings.getInstance().getString(Consts.APP_CLIENT_USER_PATH);
  /** The name of the project to get the desktop from */
  private static final String PROJECT_NAME = "premier";

  /**
   * Test to reach the client admin application
   */
  @Test
  public void testGet() {
    retrievePortal();
    retrieveDesktop();
    createWadl(getBaseUrl() + CLIENT_USER, "client-user");
  }

  /**
   * Invoke GET
   */
  public void retrievePortal() {
    ClientResource cr = new ClientResource(getBaseUrl() + CLIENT_USER + "/");
    Representation result = cr.get();
    assertTrue(cr.getStatus().isSuccess());
    assertNotNull(result);
    RIAPUtils.exhaust(result);
    cr.release();
  }

  /**
   * Invoke GET
   */
  public void retrieveDesktop() {
    ClientResource cr = new ClientResource(getBaseUrl() + CLIENT_USER + "/" + PROJECT_NAME + "/project-index.html");
    Representation result = cr.get();
    assertTrue(cr.getStatus().isSuccess());
    assertNotNull(result);
    RIAPUtils.exhaust(result);
    cr.release();
  }

}

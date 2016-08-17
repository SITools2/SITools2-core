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

import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.restlet.Component;
import org.restlet.Context;
import org.restlet.data.Protocol;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;

import fr.cnes.sitools.applications.ClientAdminApplication;
import fr.cnes.sitools.common.SitoolsSettings;
import fr.cnes.sitools.common.application.ContextAttributes;
import fr.cnes.sitools.server.Consts;

/**
 * Test CRUD Authorization with XML AUthorizationStore persistence
 * 
 * @since UserStory : ADM AUthorizations - Sprint : 4
 * @author b.fiorito (AKKA Technologies)
 */
public class ClientAdminSiteMapTestCase extends AbstractSitoolsTestCase {

  /**
   * Restlet Component for server
   */
  private Component component = null;

  @Before
  @Override
  /**
   * Init and Start a server with TaskApplication
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
      Context appContext = this.component.getContext().createChildContext();
      appContext.getAttributes().put(ContextAttributes.SETTINGS, SitoolsSettings.getInstance());

      String appPath = SitoolsSettings.getInstance().getString(Consts.APP_PATH);
      String adminAppPath = appPath + SitoolsSettings.getInstance().getString(Consts.APP_CLIENT_ADMIN_PATH);
      ClientAdminApplication app = new ClientAdminApplication(appContext, adminAppPath, SitoolsSettings.getInstance()
          .getString(Consts.APP_CLIENT_ADMIN_URL));

      this.component.getDefaultHost().attach(
          SITOOLS_URL + SitoolsSettings.getInstance().getString(Consts.APP_CLIENT_ADMIN_URL), app);
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
   * Test Generating XML File from ClientAdminSiteMapResource
   * 
   * @throws java.lang.IOException
   * 
   */
  @Test
  public void testSiteMap() throws IOException {
    ClientResource resSiteMap = new ClientResource(getBaseUrl()
        + SitoolsSettings.getInstance().getString(Consts.APP_CLIENT_ADMIN_URL) + "/siteMap");

    Representation result = resSiteMap.get(getMediaTest());

    assertNotNull(result);

    String siteMap = result.getText();
    assertNotNull(siteMap);
  }

}

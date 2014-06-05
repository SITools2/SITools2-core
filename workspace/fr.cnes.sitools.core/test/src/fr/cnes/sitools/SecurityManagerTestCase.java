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

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.restlet.Component;
import org.restlet.Context;
import org.restlet.data.Protocol;

import fr.cnes.sitools.common.SitoolsSettings;
import fr.cnes.sitools.common.application.ContextAttributes;
import fr.cnes.sitools.role.RoleApplication;
import fr.cnes.sitools.role.RoleStoreInterface;
import fr.cnes.sitools.role.RoleStoreMapXML;
import fr.cnes.sitools.server.Consts;

/**
 * TODO Should be implemented
 * 
 * @author jp.boignard (AKKA Technologies)
 */
public class SecurityManagerTestCase extends AbstractSitoolsTestCase {

  /**
   * static xml store instance for the test
   */
  private static RoleStoreInterface store = null;

  /**
   * Restlet Component for server
   */
  private Component component = null;

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
    return super.getTestRepository() + SitoolsSettings.getInstance().getString(Consts.APP_ROLES_STORE_DIR) + "/map";
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

      if (store == null) {
        File storeDirectory = new File(getTestRepository());
        storeDirectory.mkdirs();
        cleanDirectory(storeDirectory);
        store = new RoleStoreMapXML(storeDirectory, ctx);
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

}

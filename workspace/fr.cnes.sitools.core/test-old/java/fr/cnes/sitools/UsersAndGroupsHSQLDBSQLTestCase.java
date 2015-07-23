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

import org.junit.After;
import org.junit.Before;
import org.restlet.Context;

import fr.cnes.sitools.common.application.ContextAttributes;
import fr.cnes.sitools.datasource.jdbc.business.SitoolsSQLDataSourceFactory;
import fr.cnes.sitools.mail.MailAdministration;
import fr.cnes.sitools.security.JDBCUsersAndGroupsStore;
import fr.cnes.sitools.security.UsersAndGroupsAdministration;
import fr.cnes.sitools.common.Consts;

/**
 * Test CRUD Users and Groups Rest API
 * 
 * @since UserStory : ADM-security, Release 1 - Sprint : 4
 * 
 * @author jp.boignard (AKKA Technologies)
 * 
 */
public class UsersAndGroupsHSQLDBSQLTestCase extends AbstractUsersAndGroupsTestCase {
  @Before
  @Override
  /**
   * Init and Start a server with InscriptionApplication
   * La datasource et le store sont créés une seule fois pour le test. 
   * Le composant est arrêté et recréé à chaque opération
   * @throws java.lang.Exception
   */
  public void setUp() throws Exception {

    if (this.component == null) {
      this.component = createTestComponent(settings);

      Context ctx = this.component.getContext().createChildContext();
      ctx.getAttributes().put(ContextAttributes.SETTINGS, settings);
      if (ds == null) {
        ds = SitoolsSQLDataSourceFactory
            .getInstance()
            .setupDataSource(
                settings.getString("Tests.HSQLDB_DATABASE_DRIVER"), settings.getString("Tests.HSQLDB_DATABASE_URL"), settings.getString("Tests.HSQLDB_DATABASE_USER"), settings.getString("Tests.HSQLDB_DATABASE_PASSWORD"), settings.getString("Tests.HSQLDB_DATABASE_SCHEMA")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
      }

      if (store == null) {
        store = new JDBCUsersAndGroupsStore("SitoolsJDBCStore", ds, ctx);
      }

      // Context
      ctx.getAttributes().put(ContextAttributes.APP_STORE, store);

      this.component.getDefaultHost().attach(getAttachUrl(), new UsersAndGroupsAdministration(ctx));

      // Attachement de l'application de MAIL
      Context mailCtx = this.component.getContext().createChildContext();
      mailCtx.getAttributes().put(ContextAttributes.SETTINGS, settings);

      // Application
      MailAdministration mailAdministration = new MailAdministration(mailCtx, component);

      component.getInternalRouter().attach(settings.getString(Consts.APP_MAIL_ADMIN_URL), mailAdministration);

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

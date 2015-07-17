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
package fr.cnes.sitools.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.restlet.Component;
import org.restlet.Context;
import org.restlet.data.ClientInfo;
import org.restlet.data.Protocol;
import org.restlet.data.Reference;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;
import org.restlet.security.User;

import fr.cnes.sitools.applications.OrdersFilesApplication;
import fr.cnes.sitools.applications.TemporaryFolderApplication;
import fr.cnes.sitools.common.SitoolsSettings;
import fr.cnes.sitools.common.application.ContextAttributes;
import fr.cnes.sitools.common.exception.SitoolsException;
import fr.cnes.sitools.common.ext.AbstractExtSitoolsTestCase;
import fr.cnes.sitools.resources.order.utils.ListReferencesAPI;
import fr.cnes.sitools.server.Consts;
import fr.cnes.sitools.util.RIAPUtils;

public class ListReferenceAPITestCase extends AbstractExtSitoolsTestCase {

  /**
   * Restlet Component for server
   */
  private Component component = null;
  /** The Context */
  private Context context;
  /** The ClientInfo */
  private ClientInfo clientInfo;
  /** The user */
  private User user;

  /**
   * Get the host URL
   * 
   * @return the host url
   */
  private String getHostUrl() {
    return "http://localhost:" + getTestPort();
  }

  /**
   * relative url for dataset management REST API
   * 
   * @return url
   */
  protected String getAppUrl() {
    return SitoolsSettings.getInstance().getString(Consts.APP_ADMINSTORAGE_ORDERS_URL);
  }

  /**
   * relative url for dataset management REST API
   * 
   * @return url
   */
  protected String getAppTmpUrl() {
    return SitoolsSettings.getInstance().getString(Consts.APP_TMP_FOLDER_URL);
  }

  /**
   * relative url for dataset management REST API
   * 
   * @return url
   */
  protected String getAttachUrl() {
    return SITOOLS_URL + getAppUrl();
  }

  /**
   * relative url for dataset management REST API
   * 
   * @return url
   */
  protected String getAttachTmpUrl() {
    return SITOOLS_URL + getAppTmpUrl();
  }

  @Before
  @Override
  /**
   * Create component, store and application and start server  
   * @throws Exception
   */
  public void setUp() throws Exception {

    // Directory
    String orderFileAppDir = SitoolsSettings.getInstance().getStoreDIR(Consts.ADMINSTORAGE_ORDERS_DIR);
    String tempAppDIR = SitoolsSettings.getInstance().getStoreDIR(Consts.APP_TMP_FOLDER_DIR);

    if (this.component == null) {
      this.component = new Component();
      this.component.getServers().add(Protocol.HTTP, getTestPort());
      this.component.getClients().add(Protocol.HTTP);
      this.component.getClients().add(Protocol.FILE);
      this.component.getClients().add(Protocol.CLAP);

      File tmpDirectory = new File(orderFileAppDir);
      tmpDirectory.mkdirs();
      cleanDirectory(tmpDirectory);

      // Context
      context = this.component.getContext().createChildContext();
      context.getAttributes().put(ContextAttributes.SETTINGS, SitoolsSettings.getInstance());

      context.getAttributes().put(ContextAttributes.APP_ATTACH_REF, getAttachUrl());
      OrdersFilesApplication app = new OrdersFilesApplication(context, orderFileAppDir);
      this.component.getDefaultHost().attach(getAttachUrl(), app);
      component.getInternalRouter().attach(SitoolsSettings.getInstance().getString(Consts.APP_ADMINSTORAGE_ORDERS_URL),
          app);

      // Context
      Context ctx = this.component.getContext().createChildContext();
      ctx.getAttributes().put(ContextAttributes.SETTINGS, SitoolsSettings.getInstance());

      ctx.getAttributes().put(ContextAttributes.APP_ATTACH_REF, getAttachUrl());
      TemporaryFolderApplication tmpFolderApp = new TemporaryFolderApplication(ctx, tempAppDIR);
      this.component.getDefaultHost().attach(getAttachTmpUrl(), tmpFolderApp);
      component.getInternalRouter().attach(SitoolsSettings.getInstance().getString(Consts.APP_TMP_FOLDER_URL),
          tmpFolderApp);

    }

    if (!this.component.isStarted()) {
      this.component.start();
    }

    user = new User("admin", "admin");
    clientInfo = new ClientInfo(getMediaTest());
    clientInfo.setUser(user);

  }

  @After
  @Override
  /**
   * Stop server
   * @throws Exception
   */
  public void tearDown() throws Exception {
    super.tearDown();
    this.component.stop();
    this.component = null;
  }

  @Test
  public void testListReference() throws SitoolsException, IOException {
    SitoolsSettings settings = SitoolsSettings.getInstance();
    ListReferencesAPI listRefAPI = new ListReferencesAPI(settings.getPublicHostDomain());

    assertNotNull(listRefAPI);

    listRefAPI.addReferenceSource(new Reference("/test.html"));
    listRefAPI.addReferenceSource(new Reference("/test2.html"));

    List<Reference> listSource = listRefAPI.getReferencesSource();
    assertNotNull(listSource);
    assertEquals(2, listSource.size());

    String folderName = "/testList/folderName";

    String fileName = "fileName.txt";

    Reference ref = listRefAPI.copyToAdminStorage(context, folderName, fileName, clientInfo);

    ClientResource cr = new ClientResource(getHostUrl() + SITOOLS_URL + ref);
    Representation repr = cr.get();

    assertNotNull(repr);
    assertTrue(cr.getStatus().isSuccess());

    // TODO assert the file content

    listRefAPI.clearReferencesSource();
    listSource = listRefAPI.getReferencesSource();
    assertNotNull(listSource);
    assertEquals(0, listSource.size());

    listRefAPI.addReferenceDest(new Reference("/testDest.html"));
    listRefAPI.addReferenceDest(new Reference("/test2Dest.html"));

    List<Reference> listDest = listRefAPI.getReferencesDest();
    assertNotNull(listDest);
    assertEquals(2, listDest.size());

    Reference rootRef = new Reference(RIAPUtils.getRiapBase() + settings.getString(Consts.APP_TMP_FOLDER_URL)
        + "/testListRef/index.txt");

    Reference indexRef = listRefAPI.copyToUserStorage(rootRef, context, clientInfo);

    indexRef.setHostPort(getTestPort());

    cr = new ClientResource(indexRef);
    repr = cr.get();

    assertNotNull(repr);
    assertTrue(cr.getStatus().isSuccess());

    listRefAPI.clearReferencesDest();
    listDest = listRefAPI.getReferencesDest();
    assertNotNull(listDest);
    assertEquals(0, listDest.size());

  }
}

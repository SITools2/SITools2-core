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
package fr.cnes.sitools.ext.test.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.restlet.Component;
import org.restlet.Context;
import org.restlet.data.ClientInfo;
import org.restlet.data.MediaType;
import org.restlet.data.Protocol;
import org.restlet.data.Reference;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.security.User;

import fr.cnes.sitools.applications.TemporaryFolderApplication;
import fr.cnes.sitools.common.SitoolsSettings;
import fr.cnes.sitools.common.application.ContextAttributes;
import fr.cnes.sitools.common.exception.SitoolsException;
import fr.cnes.sitools.ext.test.common.AbstractExtSitoolsTestCase;
import fr.cnes.sitools.resources.order.utils.OrderResourceUtils;
import fr.cnes.sitools.server.Consts;
import fr.cnes.sitools.tasks.TaskUtils;
import fr.cnes.sitools.util.RIAPUtils;

public class OrderResourceUtilsTestCase extends AbstractExtSitoolsTestCase {

  /**
   * Restlet Component for server
   */
  private Component component = null;

  private TemporaryFolderApplication tmpFolderApp;

  private ClientInfo clientInfo;

  private Context context;

  private User user;

  private String fileText = "OrderResourceUtilsTestCase file test case";

  /**
   * relative url for dataset management REST API
   * 
   * @return url
   */
  protected String getAppUrl() {
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

  @Before
  @Override
  /**
   * Create component, store and application and start server  
   * @throws java.lang.Exception
   */
  public void setUp() throws Exception {

    // Directory
    String tempAppDIR = SitoolsSettings.getInstance().getStoreDIR(Consts.APP_TMP_FOLDER_DIR);

    if (this.component == null) {
      this.component = new Component();
      this.component.getServers().add(Protocol.HTTP, getTestPort());
      this.component.getClients().add(Protocol.HTTP);
      this.component.getClients().add(Protocol.FILE);
      this.component.getClients().add(Protocol.CLAP);
      this.component.getClients().add(Protocol.ZIP);

      File tmpDirectory = new File(tempAppDIR);
      tmpDirectory.mkdirs();
      cleanDirectory(tmpDirectory);

      // Context
      Context ctx = this.component.getContext().createChildContext();
      ctx.getAttributes().put(ContextAttributes.SETTINGS, SitoolsSettings.getInstance());

      ctx.getAttributes().put(ContextAttributes.APP_ATTACH_REF, getAttachUrl());
      tmpFolderApp = new TemporaryFolderApplication(ctx, tempAppDIR);
      this.component.getDefaultHost().attach(getAttachUrl(), tmpFolderApp);
      component.getInternalRouter().attach(SitoolsSettings.getInstance().getString(Consts.APP_TMP_FOLDER_URL),
          tmpFolderApp);

    }

    if (!this.component.isStarted()) {
      this.component.start();
    }

    user = new User("admin", "admin");
    clientInfo = new ClientInfo(getMediaTest());
    clientInfo.setUser(user);

    context = tmpFolderApp.getContext().createChildContext();
    context.getAttributes().put(TaskUtils.PARENT_APPLICATION, tmpFolderApp);

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
   * Test CRUD Graph with JSon format exchanges.
   * 
   * @throws SitoolsException
   * @throws IOException
   */
  @Test
  public void testCRUD() throws SitoolsException, IOException {

    Reference ref = addFile();

    getFile(ref);

    Reference refCopy = new Reference(ref);
    refCopy.setLastSegment("test_copy.txt");

    copyFile(ref, refCopy);

    tarFiles(ref, refCopy);
    tarGzFiles(ref, refCopy);

    // ?????
    zipFiles(ref, refCopy);

    deleteFile(ref);
    deleteFile(refCopy);

    getAvailableUserPath();

  }

  private Reference addFile() throws SitoolsException {
    StringRepresentation repr = new StringRepresentation(fileText, MediaType.TEXT_PLAIN);
    Reference refDestination = new Reference(RIAPUtils.getRiapBase() + getAppUrl());
    refDestination.addSegment("test.txt");

    Reference ref = OrderResourceUtils.addFile(repr, refDestination, clientInfo, context);

    assertNotNull(ref);

    return ref;

  }

  private void getFile(Reference ref) throws SitoolsException, IOException {

    Representation repr = OrderResourceUtils.getFile(ref, clientInfo, context);
    assertNotNull(repr);
    String txt = repr.getText();
    assertNotNull(txt);
    assertEquals(fileText, txt);

  }

  private void copyFile(Reference refSrc, Reference refOut) throws SitoolsException, IOException {

    Reference ref = OrderResourceUtils.copyFile(refSrc, refOut, clientInfo, context);
    assertNotNull(ref);
    getFile(ref);

  }

  private void deleteFile(Reference ref) throws SitoolsException {
    try {
      OrderResourceUtils.deleteFile(ref, clientInfo, context);
      getFileExpectError(ref);
    }
    catch (SitoolsException e) {
      fail("File " + ref + " canno't be deleted");
      throw e;
    }
  }

  private void getAvailableUserPath() {
    String folderName = "folderName";
    Reference ref = OrderResourceUtils.getUserAvailableFolderPath(user, folderName, context);
    boolean contains = ref.toString().contains(folderName);
    if (!contains) {
      fail(ref + " doesn't contains " + folderName);
    }

  }

  private void tarFiles(Reference ref1, Reference ref2) throws SitoolsException {
    List<Reference> refs = new ArrayList<Reference>();
    refs.add(ref1);
    refs.add(ref2);

    String destFilePath = SitoolsSettings.getInstance().getStoreDIR(Consts.APP_TMP_FOLDER_DIR);
    String destFileName = "testZip.tar";

    OrderResourceUtils.tarFiles(refs, destFilePath, destFileName, clientInfo, context, false);

    Reference ref = new Reference(RIAPUtils.getRiapBase() + getAppUrl() + "/" + destFileName);
    Representation repr = OrderResourceUtils.getFile(ref, clientInfo, context);
    assertNotNull(repr);

    OrderResourceUtils.deleteFile(ref, clientInfo, context);

  }
  
  private void tarGzFiles(Reference ref1, Reference ref2) throws SitoolsException {
    List<Reference> refs = new ArrayList<Reference>();
    refs.add(ref1);
    refs.add(ref2);

    String destFilePath = SitoolsSettings.getInstance().getStoreDIR(Consts.APP_TMP_FOLDER_DIR);
    String destFileName = "testZip.tar.gz";

    OrderResourceUtils.tarFiles(refs, destFilePath, destFileName, clientInfo, context, true);

    Reference ref = new Reference(RIAPUtils.getRiapBase() + getAppUrl() + "/" + destFileName);
    Representation repr = OrderResourceUtils.getFile(ref, clientInfo, context);
    assertNotNull(repr);

    OrderResourceUtils.deleteFile(ref, clientInfo, context);

  }

  private void zipFiles(Reference ref1, Reference ref2) throws SitoolsException {
    List<Reference> refs = new ArrayList<Reference>();
    refs.add(ref1);
    refs.add(ref2);

    String destFilePath = SitoolsSettings.getInstance().getStoreDIR(Consts.APP_TMP_FOLDER_DIR);
    String destFileName = "testArchive.zip";

    OrderResourceUtils.zipFiles(refs, destFilePath + "/" + destFileName, clientInfo, context);

    Reference ref = new Reference(RIAPUtils.getRiapBase() + getAppUrl() + "/" + destFileName);
    Representation repr = OrderResourceUtils.getFile(ref, clientInfo, context);
    assertNotNull(repr);

    OrderResourceUtils.deleteFile(ref, clientInfo, context);

  }

  private void getFileExpectError(Reference ref) {
    Representation repr = null;
    try {
      repr = OrderResourceUtils.getFile(ref, clientInfo, context);
      fail("File " + ref + " exists");
    }
    catch (SitoolsException e) {
      assertNull(repr);
    }

  }

}

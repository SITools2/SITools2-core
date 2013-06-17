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
import static org.junit.Assert.fail;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.restlet.Component;
import org.restlet.Context;
import org.restlet.data.MediaType;
import org.restlet.data.Protocol;
import org.restlet.data.Status;
import org.restlet.engine.io.BioUtils;
import org.restlet.representation.FileRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.ClientResource;

import fr.cnes.sitools.applications.UploadApplication;
import fr.cnes.sitools.common.SitoolsSettings;
import fr.cnes.sitools.common.application.ContextAttributes;
import fr.cnes.sitools.server.Consts;

/**
 * TestCase of Upload function
 * 
 * @author jp.boignard (AKKA Technologies)
 */
public class UploadTestCase extends AbstractSitoolsTestCase {

  /**
   * Restlet Component for server
   */
  private Component component = null;

  /**
   * relative url for upload images management REST API
   * 
   * @return url
   */
  protected String getAttachUrl() {
    return SITOOLS_URL + SitoolsSettings.getInstance().getString(Consts.APP_UPLOAD_URL);
  }

  /**
   * absolute url for upload images management REST API
   * 
   * @return url
   */
  protected String getBaseUrl() {
    return super.getBaseUrl() + SitoolsSettings.getInstance().getString(Consts.APP_UPLOAD_URL);
  }

  /**
   * Absolute path location for inscription store files
   * 
   * @return path
   */
  protected String getTestRepository() {
    return super.getTestRepository() + SitoolsSettings.getInstance().getString(Consts.APP_UPLOAD_DIR);
  }

  @Before
  @Override
  /**
   * Create component, store and application and start server
   * @throws java.lang.Exception
   */
  public void setUp() throws Exception {

    if (this.component == null) {
      this.component = new Component();
      this.component.getServers().add(Protocol.HTTP, getTestPort());
      this.component.getClients().add(Protocol.HTTP);
      this.component.getClients().add(Protocol.FILE);
      this.component.getClients().add(Protocol.CLAP);

      // Directory
      String uploadAppDIR = TEST_FILES_REPOSITORY + SitoolsSettings.getInstance().getString(Consts.APP_UPLOAD_DIR);

      // Context
      Context ctx = this.component.getContext().createChildContext();
      ctx.getAttributes().put(ContextAttributes.SETTINGS, SitoolsSettings.getInstance());
      ctx.getAttributes().put(ContextAttributes.APP_ATTACH_REF, getAttachUrl());
      ctx.getAttributes().put(ContextAttributes.APP_REGISTER, false);

      UploadApplication uploadApp = new UploadApplication(ctx, uploadAppDIR);

      this.component.getDefaultHost().attach(getAttachUrl(), uploadApp);
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

    try {
      this.component.stop();
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    finally {
      this.component = null;
    }
  }

  /**
   * Test of uploading file to server with a PUT call.
   */
  @Test
  public void upload() {
    try {
      BioUtils.delete(new File(getTestRepository(), "testupload1.png"));

      File toUpload = new File(super.getTestRepository(), "testupload.png");
      if (!toUpload.exists()) {
        fail("FILE MUST EXIST FOR TEST :" + toUpload.getName());
      }
      ClientResource res = new ClientResource(getBaseUrl() + "/" + "testupload1.png");
      FileRepresentation rep = new FileRepresentation(toUpload, MediaType.IMAGE_PNG);
      Representation result = res.put(rep);
      assertEquals(Status.SUCCESS_CREATED.getCode(), res.getStatus().getCode());
      assertNotNull(result);
    }
    catch (Exception e) {
      e.printStackTrace();
    }

  }

  /**
   * Test of uploading file to server with POST call and PUT tunneling
   */
  @Test
  public void uploadPOST() {
    try {
      BioUtils.delete(new File(getTestRepository(), "testupload2.png"));

      File toUpload = new File(super.getTestRepository(), "testupload.png");
      if (!toUpload.exists()) {
        fail("FILE MUST EXIST FOR TEST :" + toUpload.getName());
      }
      ClientResource res = new ClientResource(getBaseUrl() + "/" + "testupload2.png?method=PUT");
      FileRepresentation rep = new FileRepresentation(toUpload, MediaType.IMAGE_PNG);
      Representation result = res.post(rep);
      assertEquals(Status.SUCCESS_CREATED.getCode(), res.getStatus().getCode());
      assertNotNull(result);
    }
    catch (Exception e) {
      e.printStackTrace();
    }

  }
  
  /**
   * Test of updating a server text file with PUT method
   */
  @Test
  public void updatePUTTXT() {
    String fileName = "testupdate.txt";
    try {
      BioUtils.delete(new File(getTestRepository(), fileName));

      File toUpdate = new File(super.getTestRepository(), fileName);
      if (!toUpdate.exists()) {
        fail("FILE MUST EXIST FOR TEST :" + toUpdate.getName());
      }
      
      ClientResource res = new ClientResource(getBaseUrl() + "/" + fileName);
      
      MediaType media = MediaType.TEXT_PLAIN;
      
      FileRepresentation rep = new FileRepresentation(toUpdate, media);
      Representation result = res.put(rep);
      assertEquals(Status.SUCCESS_CREATED.getCode(), res.getStatus().getCode());
      assertNotNull(result);
      
      // update content
      ClientResource resourceUpdate = new ClientResource(getBaseUrl() + "/" + fileName);
      Representation resultUpdate = resourceUpdate.put(new StringRepresentation("nouveau contenu"), media);
      assertEquals(Status.SUCCESS_OK.getCode(), resourceUpdate.getStatus().getCode());
      assertNotNull(resultUpdate);
    }
    catch (Exception e) {
      e.printStackTrace();
    }

  }
  
// POUR FONCTIONNER LES 2 TESTS SUIVANTS NECESSITENT LA CREATION DE 2 MEDIATYPES
// qui surchargent includes pour permettre la compatiblité de l'entité reçue (octet) 
// et donc l'écriture du fichier.

//  /**
//   * Test of updating a server text file with PUT method
//   */
//  @Test
//  public void updatePUTFTL() {
//    String fileName = "testupdateftl.ftl";
//    try {
//      BioUtils.delete(new File(getTestRepository(), fileName));
//
//      File toUpdate = new File(super.getTestRepository(), fileName);
//      if (!toUpdate.exists()) {
//        fail("FILE MUST EXIST FOR TEST :" + toUpdate.getName());
//      }
//      
//      ClientResource res = new ClientResource(getBaseUrl() + "/" + fileName);
//
//      MediaType media = new MediaType("fremmarker", "Freemarker templates");//  new MediaType(M,  Encoding.FREEMARKER.getName());
//      
//      FileRepresentation rep = new FileRepresentation(toUpdate, media);
//      Representation result = res.put(rep);
//      assertEquals(Status.SUCCESS_CREATED.getCode(), res.getStatus().getCode());
//      assertNotNull(result);
//      
//      // update content
//      ClientResource resourceUpdate = new ClientResource(getBaseUrl() + "/" + fileName);
//      Representation resultUpdate = resourceUpdate.put(new StringRepresentation("nouveau contenu"), media);
//      assertEquals(Status.SUCCESS_OK.getCode(), resourceUpdate.getStatus().getCode());
//      assertNotNull(resultUpdate);
//    }
//    catch (Exception e) {
//      e.printStackTrace();
//    }
//
//  }
//  
//  /**
//   * Test of updating a server text file with PUT method
//   */
//  @Test
//  public void updatePUTCSS() {
//    String fileName = "testupdatecss.css";
//    try {
//      BioUtils.delete(new File(getTestRepository(), fileName));
//
//      File toUpdate = new File(super.getTestRepository(), fileName);
//      if (!toUpdate.exists()) {
//        fail("FILE MUST EXIST FOR TEST :" + toUpdate.getName());
//      }
//      
//      ClientResource res = new ClientResource(getBaseUrl() + "/" + fileName);
//      
//      //MediaType media = MediaType.TEXT_CSS;
//      MediaType media = new MediaType("css", "Common Style Cheet");
//      
//      FileRepresentation rep = new FileRepresentation(toUpdate, media);
//      Representation result = res.put(rep);
//      assertEquals(Status.SUCCESS_CREATED.getCode(), res.getStatus().getCode());
//      assertNotNull(result);
//      
//      // update content
//      ClientResource resourceUpdate = new ClientResource(getBaseUrl() + "/" + fileName);
//      Representation resultUpdate = resourceUpdate.put(new StringRepresentation("nouveau contenu"), media);
//      assertEquals(Status.SUCCESS_OK.getCode(), resourceUpdate.getStatus().getCode());
//      assertNotNull(resultUpdate);
//    }
//    catch (Exception e) {
//      e.printStackTrace();
//    }
//
//  }
  
}

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
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.restlet.data.MediaType;
import org.restlet.engine.Engine;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;

import com.thoughtworks.xstream.XStream;

import fr.cnes.sitools.client.model.VersionBuildDateDTO;
import fr.cnes.sitools.common.SitoolsSettings;
import fr.cnes.sitools.common.SitoolsXStreamRepresentation;
import fr.cnes.sitools.common.XStreamFactory;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.common.Consts;
import fr.cnes.sitools.util.RIAPUtils;

/**
 * Test case to create the WADL for public application, temporary folder app, upload and documentation
 * 
 * @author m.marseille (AKKA Technologies)
 */
public class PublicApplicationTestCase extends AbstractSitoolsServerTestCase {

  /** Temporary file URL */
  private static final String TMP_URL = SitoolsSettings.getInstance().getString(Consts.APP_TMP_FOLDER_URL);

  /** Upload URL */
  private static final String UPLOAD_URL = SitoolsSettings.getInstance().getString(Consts.APP_UPLOAD_DIR);

  /** Order Folder URL */
  private static final String SVA_ORDER_URL = SitoolsSettings.getInstance().getString(
    Consts.APP_ADMINSTORAGE_ORDERS_URL);

  /**
   * Test to reach the public application
   */
  @Test
  public void testGet() {
    getPublic();
    getTmp();
    getUpload();
    getOrderFolder();
    setMediaTest(MediaType.APPLICATION_XML);
    getVersion();
    setMediaTest(MediaType.APPLICATION_JSON);
    getVersion();
    createWadl(getBaseUrl(), "public");
    createWadl(getBaseUrl() + TMP_URL, "temporary-folder");
    createWadl(getBaseUrl() + UPLOAD_URL, "upload");
  }

  /**
   * Invoke GET on public (/sitools)
   */
  public void getPublic() {
    ClientResource cr = new ClientResource(getBaseUrl());
    Representation result = cr.get();
    assertTrue(cr.getStatus().isSuccess());
    assertNotNull(result);

    RIAPUtils.exhaust(result);
  }

  /**
   * Invoke GET on temporary folder (/sitools/tmp)
   */
  public void getTmp() {
    ClientResource cr = new ClientResource(getBaseUrl() + TMP_URL);
    Representation result = cr.get();
    assertTrue(cr.getStatus().isSuccess());
    assertNotNull(result);

    RIAPUtils.exhaust(result);
  }

  /**
   * Invoke GET on upload application (/sitools/upload)
   */
  public void getUpload() {
    ClientResource cr = new ClientResource(getBaseUrl() + UPLOAD_URL);
    Representation result = cr.get();
    assertTrue(cr.getStatus().isSuccess());
    assertNotNull(result);

    RIAPUtils.exhaust(result);
  }

  /**
   * Invoke GET on orderFolder application (/adminData)
   */
  private void getOrderFolder() {
    ClientResource cr = new ClientResource(getBaseUrl() + SVA_ORDER_URL);
    Representation result = cr.get();
    assertTrue(cr.getStatus().isSuccess());
    assertNotNull(result);

    RIAPUtils.exhaust(result);

  }

  /**
   * Get the version of Sitools
   */
  private void getVersion() {
    ClientResource cr = new ClientResource(getBaseUrl() + "/version");
    Representation result = cr.get(getMediaTest());
    assertTrue(cr.getStatus().isSuccess());
    assertNotNull(result);

    Response resp = getResponse(getMediaTest(), result, VersionBuildDateDTO.class);
    assertNotNull(resp);
    assertTrue(resp.getSuccess());
    assertNotNull(resp.getItem());

    VersionBuildDateDTO info = (VersionBuildDateDTO) resp.getItem();
    String version = info.getVersion();
    assertNotSame("", version);

    RIAPUtils.exhaust(result);

  }

  // ------------------------------------------------------------
  // RESPONSE REPRESENTATION WRAPPING

  /**
   * REST API Response wrapper for single item expected.
   * 
   * @param media
   *          MediaType expected
   * @param representation
   *          service response representation
   * @param dataClass
   *          class expected in the item property of the Response object
   * @return Response the response.
   */
  public static Response getResponse(MediaType media, Representation representation, Class<?> dataClass) {
    return getResponse(media, representation, dataClass, false);
  }

  /**
   * REST API Response Representation wrapper for single or multiple items expexted
   * 
   * @param media
   *          MediaType expected
   * @param representation
   *          service response representation
   * @param dataClass
   *          class expected for items of the Response object
   * @param isArray
   *          if true wrap the data property else wrap the item property
   * @return Response
   */
  public static Response getResponse(MediaType media, Representation representation, Class<?> dataClass, boolean isArray) {
    try {
      if (!media.isCompatible(MediaType.APPLICATION_JSON) && !media.isCompatible(MediaType.APPLICATION_XML)) {
        Engine.getLogger(AbstractSitoolsTestCase.class.getName()).warning("Only JSON or XML supported in tests");
        return null;
      }

      XStream xstream = XStreamFactory.getInstance().getXStreamReader(media);
      xstream.autodetectAnnotations(false);
      xstream.alias("response", Response.class);
      xstream.alias("info", VersionBuildDateDTO.class);

      if (isArray) {
        xstream.addImplicitCollection(Response.class, "data", dataClass);
      }
      else {
        xstream.alias("item", dataClass);
        xstream.alias("item", Object.class, dataClass);

        if (dataClass == VersionBuildDateDTO.class) {
          xstream.aliasField("info", Response.class, "item");
        }
      }
      xstream.aliasField("data", Response.class, "data");

      SitoolsXStreamRepresentation<Response> rep = new SitoolsXStreamRepresentation<Response>(representation);
      rep.setXstream(xstream);

      if (media.isCompatible(getMediaTest())) {
        Response response = rep.getObject("response");
        return response;
      }
      else {
        Engine.getLogger(AbstractSitoolsTestCase.class.getName()).warning("Only JSON is supported in tests");
        return null; // TODO complete test for XML, Object representation
      }
    }
    finally {
      RIAPUtils.exhaust(representation);

    }
  }

}

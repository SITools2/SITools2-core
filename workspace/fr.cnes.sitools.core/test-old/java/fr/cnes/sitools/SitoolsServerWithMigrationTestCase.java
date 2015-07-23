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

import fr.cnes.sitools.util.FileUtils;
import junit.framework.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.restlet.data.MediaType;
import org.restlet.engine.Engine;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;

import com.thoughtworks.xstream.XStream;

import fr.cnes.sitools.common.SitoolsXStreamRepresentation;
import fr.cnes.sitools.common.XStreamFactory;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.common.model.Url;
import fr.cnes.sitools.dataset.view.model.DatasetView;
import fr.cnes.sitools.common.Consts;
import fr.cnes.sitools.util.FileCopyUtils;
import fr.cnes.sitools.util.RIAPUtils;

import java.io.File;
import java.io.IOException;

 public class SitoolsServerWithMigrationTestCase extends AbstractSitoolsServerTestCase {

  private String dataviewId = "0d0f9ad1-2088-4f5e-a229-c89dd002d446";

  /**
   * Executed once before all test methods
   */
  @BeforeClass
  public static void before() {
    setupSpecific();
    start();
  }

  /** Setup tests variables before starting server */
  private static void setupSpecific() {
    AbstractSitoolsServerTestCase.setup();
    settings.setStartWithMigration(true);
    String filePath = settings.getRootDirectory() + settings.getStoreDIR() + "/bad_datasets_views_for_test/int@0.xml";
    //clear dataset_views directory to simulate migration
    try {
      FileUtils.cleanDirectory(new File(settings.getStoreDIR(Consts.APP_DATASETS_VIEWS_STORE_DIR) + "/map"), true);
    } catch (IOException e) {
      Assert.fail("Cannot clear dataset_views directory exception: " + e.getMessage());
    }
    FileCopyUtils.copyAFile(filePath, settings.getStoreDIR(Consts.APP_DATASETS_VIEWS_STORE_DIR) + "/int@0.xml");

  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.cnes.sitools.SitoolsServerTestCase#setUp()
   */
  @Override
  public void setUp() throws Exception {
    super.setUp();
    setMediaTest(MediaType.APPLICATION_JSON);
  }

  /**
   * Trace global parameters for the test.
   */
  @Test
  public void test() {
    // Test that the server is accessible meaning that the migration was succesfull
    String url = getBaseUrl() + settings.getString(Consts.APP_DATASETS_VIEWS_URL) + "/" + dataviewId;
    ClientResource cr = new ClientResource(url);
    Representation result = cr.get(getMediaTest());
    assertNotNull(result);
    assertTrue(cr.getStatus().isSuccess());
    Response response = getResponse(getMediaTest(), result, DatasetView.class, false);
    assertTrue(response.getSuccess());
    assertNotNull(response.getItem());
    DatasetView dataview = (DatasetView) response.getItem();
    assertEquals(dataview.getId(), dataviewId);
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
      if (!media.isCompatible(getMediaTest()) && !media.isCompatible(MediaType.APPLICATION_XML)) {
        Engine.getLogger(AbstractSitoolsTestCase.class.getName()).warning("Only JSON or XML supported in tests");
        return null;
      }

      XStream xstream = XStreamFactory.getInstance().getXStreamReader(media);
      xstream.autodetectAnnotations(false);
      xstream.alias("response", Response.class);
      xstream.alias("datasetView", DatasetView.class);
      xstream.alias("url", Url.class);
      

      // Parce que les annotations ne sont apparemment prises en compte
      xstream.omitField(Response.class, "itemName");
      xstream.omitField(Response.class, "itemClass");

      if (isArray) {
        xstream.addImplicitCollection(Response.class, "data", dataClass);
      }
      else {
        xstream.alias("item", dataClass);
        xstream.alias("item", Object.class, dataClass);
      }
      xstream.aliasField("datasetView", Response.class, "item");

      SitoolsXStreamRepresentation<Response> rep = new SitoolsXStreamRepresentation<Response>(representation);
      rep.setXstream(xstream);

      if (media.isCompatible(getMediaTest())) {
        Response response = rep.getObject("response");
        return response;
      }
      else {
        Engine.getLogger(AbstractSitoolsTestCase.class.getName()).warning("Only JSON or XML supported in tests");
        return null;
      }
    }
    finally {
      RIAPUtils.exhaust(representation);
    }
  }
}

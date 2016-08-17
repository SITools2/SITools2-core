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

import org.junit.BeforeClass;
import org.junit.Test;
import org.restlet.Client;
import org.restlet.Request;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Protocol;
import org.restlet.data.Status;
import org.restlet.engine.Engine;
import org.restlet.representation.Representation;

import com.thoughtworks.xstream.XStream;

import fr.cnes.sitools.common.SitoolsXStreamRepresentation;
import fr.cnes.sitools.common.XStreamFactory;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.dataset.view.model.DatasetView;
import fr.cnes.sitools.server.Consts;
import fr.cnes.sitools.util.FileCopyUtils;
import fr.cnes.sitools.util.RIAPUtils;

public class SitoolsServerWithMigrationErrorTestCase extends AbstractSitoolsServerTestCase {

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
    settings.setStartWithMigration(false);
    String filePath = settings.getRootDirectory() + settings.getStoreDIR() + "/bad_datasets_views_for_test/int@0.xml";
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
    // Test that the server is not accessible meaning that the migration wasn't successful
    String url = getBaseUrl() + settings.getString(Consts.APP_DATASETS_VIEWS_URL) + "/" + dataviewId;
    final Client client = new Client(Protocol.HTTP);
    Request request = new Request(Method.GET, url);
    org.restlet.Response response = null;
    try {
      response = client.handle(request);
      assertNotNull(response);
      assertTrue(response.getStatus().isError());
      assertEquals(Status.SERVER_ERROR_INTERNAL, response.getStatus());

    }
    finally {
      if (response != null) {
        RIAPUtils.exhaust(response);
      }
    }
  }
}

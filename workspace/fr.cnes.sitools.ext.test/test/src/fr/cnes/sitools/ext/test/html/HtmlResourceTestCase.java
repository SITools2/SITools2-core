/*******************************************************************************
 * Copyright 2011, 2012 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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
package fr.cnes.sitools.ext.test.html;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;
import org.restlet.data.MediaType;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;

import fr.cnes.sitools.common.SitoolsSettings;
import fr.cnes.sitools.ext.test.tasks.AbstractTaskResourceTestCase;
import fr.cnes.sitools.plugins.resources.model.ResourceModel;
import fr.cnes.sitools.server.Consts;
import fr.cnes.sitools.util.RIAPUtils;

/**
 * Tests the HTML resource
 * 
 * 
 * @author m.gond
 */
public class HtmlResourceTestCase extends AbstractTaskResourceTestCase {
  /**
   * The if of the dataset
   */
  private static final String DATASET_ID = "bf77955a-2cec-4fc3-b95d-7397025fb299";
  /**
   * The url of the dataset
   */
  private static final String DATASET_URL = "/mondataset";

  /**
   * The class name of the resourceModel
   */
  private String htmlResourceModelClassName = "fr.cnes.sitools.resources.html.HtmlResourceModel";

  /**
   * The url attachment for the resource model
   */
  private String urlAttach = "/html";

  /**
   * absolute url for dataset management REST API
   * 
   * @return url
   */
  public final String getBaseDatasetUrl() {
    return super.getBaseUrl() + SitoolsSettings.getInstance().getString(Consts.APP_DATASETS_URL) + "/" + DATASET_ID;
  }

  /**
   * Test the HTML Resource
   * 
   * @throws ClassNotFoundException
   *           if the class cannot be found
   * @throws InstantiationException
   *           if there is an error while instantiating the resource
   * @throws IllegalAccessException
   *           if there is an error while instantiating the resource
   * @throws IOException
   *           if the response cannot be read
   */
  @Test
  public void testHtmlResource() throws ClassNotFoundException, InstantiationException, IllegalAccessException,
    IOException {
    ResourceModel htmlResource = createResourceModel(htmlResourceModelClassName, "1000qsdqs", urlAttach);
    htmlResource.getParameterByName("title").setValue("HTML title");
    create(htmlResource, getBaseDatasetUrl());
    queryResourceHTML();
    delete(htmlResource, getBaseDatasetUrl());
  }

  /**
   * Query the Resoure
   * 
   * @throws IOException
   *           if the response cannot be read
   */
  private void queryResourceHTML() throws IOException {
    String url = getHostUrl() + DATASET_URL + urlAttach + "?start=0&limit=5";
    ClientResource cr = new ClientResource(url);
    Representation result = cr.get(MediaType.TEXT_HTML);
    assertNotNull(result);
    assertTrue(cr.getStatus().isSuccess());

    // print the Result to the console
    String html = result.getText();
    LOGGER.info(html);

    RIAPUtils.exhaust(result);
  }

}

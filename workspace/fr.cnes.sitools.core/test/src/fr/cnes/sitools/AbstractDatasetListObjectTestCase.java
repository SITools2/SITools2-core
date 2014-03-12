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
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Test;
import org.restlet.data.MediaType;
import org.restlet.engine.Engine;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;

import com.thoughtworks.xstream.XStream;

import fr.cnes.sitools.common.SitoolsXStreamRepresentation;
import fr.cnes.sitools.common.XStreamFactory;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.feeds.model.FeedAuthorModel;
import fr.cnes.sitools.feeds.model.FeedEntryModel;
import fr.cnes.sitools.feeds.model.FeedModel;
import fr.cnes.sitools.form.dataset.dto.FormDTO;
import fr.cnes.sitools.form.dataset.dto.ParameterDTO;
import fr.cnes.sitools.form.dataset.dto.ValueDTO;
import fr.cnes.sitools.util.RIAPUtils;

/**
 * Test case testing access to Forms, datasets or opensearch list for a given dataset
 * 
 * @author m.gond (AKKA Technologies)
 * 
 * @version
 * 
 */
public abstract class AbstractDatasetListObjectTestCase extends AbstractSitoolsServerTestCase {

  /** dataset attachment for user */
  private String datasetAttach = "/mondataset";

  /**
   * absolute url for dataset management REST API
   * 
   * @return url
   */
  protected String getBaseUrl() {
    return super.getHostUrl();
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.cnes.sitools.AbstractSitoolsServerTestCase#setUp()
   */
  @Override
  public void setUp() throws Exception {
    // TODO Auto-generated method stub
    super.setUp();
  }

  /**
   * Test
   */
  @Test
  public void test() {
    docAPI.setActive(false);
    try {
      // test avec projet contenant des objets
      getFormList(datasetAttach);
      getFeedsList(datasetAttach);

      // test avec projet vide
      createWadl(getBaseUrl() + datasetAttach, "dataset_user");
    }
    catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  /**
   * Test
   */
  @Test
  public void testAPI() {
    docAPI.setActive(true);
    docAPI.appendChapter("Getting list of Object attached to the dataset");

    docAPI.appendSubChapter("Get forms", "forms");
    try {
      getFormList(datasetAttach);
      docAPI.appendSubChapter("Get feeds", "feeds");
      getFeedsList(datasetAttach);
    }
    catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    docAPI.close();
  }

  /**
   * Get the list of form of the dataset corresponding to the following attachment
   * 
   * @param datasetAttach
   *          the attachment of the dataset
   * @throws IOException
   *           Exception when copying configuration files from TEST to data/TESTS
   */
  private void getFormList(String datasetAttach) throws IOException {
    String url = getBaseUrl() + datasetAttach + "/forms";
    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      String template = getBaseUrl() + datasetAttach + "/forms";
      retrieveDocAPI(url, "", parameters, template);
    }
    else {
      ClientResource cr = new ClientResource(url);
      Representation result = cr.get(getMediaTest());
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());
      Response response = getResponse(getMediaTest(), result, FormDTO.class, true);
      assertTrue(response.getSuccess());
      assertEquals(new Integer(2), response.getTotal());
      RIAPUtils.exhaust(result);
    }

  }

  /**
   * Get the list of opensearch of the dataset corresponding to the following attachment
   * 
   * @param datasetAttach
   *          the attachment of the dataset
   * @throws IOException
   *           Exception when copying configuration files from TEST to data/TESTS
   */
  private void getFeedsList(String datasetAttach) throws IOException {
    String url = getBaseUrl() + datasetAttach + "/feeds";
    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      String template = getBaseUrl() + datasetAttach + "/feeds";
      retrieveDocAPI(url, "", parameters, template);
    }
    else {
      ClientResource cr = new ClientResource(url);
      Representation result = cr.get(getMediaTest());
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());
      Response response = getResponse(getMediaTest(), result, FeedModel.class, true);
      assertTrue(response.getSuccess());
      assertEquals(new Integer(1), response.getTotal());
      RIAPUtils.exhaust(result);
    }

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
      // for forms
      xstream.alias("formDTO", FormDTO.class);
      // for feeds
      xstream.alias("FeedModel", FeedModel.class);
      xstream.alias("FeedEntryModel", FeedEntryModel.class);
      xstream.alias("FeedAuthorModel", FeedAuthorModel.class);

      if (isArray) {
        if (media.isCompatible(MediaType.APPLICATION_JSON)) {

          // for forms, always lists
          xstream.addImplicitCollection(Response.class, "data", dataClass);

          // Sans []
          xstream.addImplicitCollection(FormDTO.class, "parameters", "parameters", ParameterDTO.class);
          xstream.addImplicitCollection(ParameterDTO.class, "values", "values", ValueDTO.class);
          xstream.addImplicitCollection(ParameterDTO.class, "defaultValues", String.class);

          // Avec []
          xstream.aliasField("parameters", FormDTO.class, "parameters");
          xstream.aliasField("values", ParameterDTO.class, "values");

          // end for forms

          // for feeds
          xstream.addImplicitCollection(FeedModel.class, "entries", FeedEntryModel.class);

        }
        xstream.alias("item", Object.class, dataClass);
      }
      else {
        xstream.alias("item", Object.class, dataClass);
      }
      xstream.aliasField("data", Response.class, "data");

      SitoolsXStreamRepresentation<Response> rep = new SitoolsXStreamRepresentation<Response>(representation);
      rep.setXstream(xstream);

      if (media.isCompatible(getMediaTest())) {
        Response response = rep.getObject("response");

        return response;
      }
      else {
        Engine.getLogger(AbstractSitoolsTestCase.class.getName()).warning("Only JSON or XML supported in tests");
        return null; // TODO complete test with ObjectRepresentation
      }
    }
    finally {
      RIAPUtils.exhaust(representation);
    }
  }

}

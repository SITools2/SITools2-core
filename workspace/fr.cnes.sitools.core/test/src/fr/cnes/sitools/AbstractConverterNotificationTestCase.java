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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.restlet.data.ChallengeResponse;
import org.restlet.data.ChallengeScheme;
import org.restlet.data.MediaType;
import org.restlet.ext.jackson.JacksonRepresentation;
import org.restlet.ext.xstream.XstreamRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;

import com.thoughtworks.xstream.XStream;

import fr.cnes.sitools.api.DocWadl;
import fr.cnes.sitools.common.SitoolsSettings;
import fr.cnes.sitools.common.SitoolsXStreamRepresentation;
import fr.cnes.sitools.common.XStreamFactory;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.dataset.converter.dto.ConverterModelDTO;
import fr.cnes.sitools.dataset.converter.model.ConverterChainedModel;
import fr.cnes.sitools.dataset.converter.model.ConverterParameter;
import fr.cnes.sitools.dataset.converter.model.ConverterParameterType;
import fr.cnes.sitools.server.Consts;
import fr.cnes.sitools.util.RIAPUtils;

/**
 * Test case of converters.
 * 
 * @author AKKA Technologies
 */
public abstract class AbstractConverterNotificationTestCase extends AbstractSitoolsServerTestCase {

  /**
   * datasetId to attach the application
   */
  private String datasetId = "bf77955a-2cec-4fc3-b95d-7397025fb299";

  /**
   * converterId
   */
  private String converterId = "100000";

  /**
   * relative url for dataset management REST API
   * 
   * @return url
   */
  protected String getAttachUrl() {
    return SITOOLS_URL + "/datasets/{datasetId}"
        + SitoolsSettings.getInstance().getString(Consts.APP_DATASETS_CONVERTERS_URL);
  }

  /**
   * absolute url for dataset management REST API
   * 
   * @return url
   */
  protected String getBaseUrl() {
    return super.getBaseUrl() + "/datasets/%s"
        + SitoolsSettings.getInstance().getString(Consts.APP_DATASETS_CONVERTERS_URL);
  }

  /**
   * absolute url for dataset management REST API
   * 
   * @return url
   */
  protected String getBaseRefUrl() {
    return super.getBaseUrl() + "/datasets/{datasetId}"
        + SitoolsSettings.getInstance().getString(Consts.APP_DATASETS_CONVERTERS_URL);
  }

  /**
   * Absolute path location for project store files
   * 
   * @return path
   */
  protected String getTestRepository() {
    return super.getTestRepository()
        + SitoolsSettings.getInstance().getString(Consts.APP_DATASETS_CONVERTERS_STORE_DIR);
  }

  /**
   * Get the dataset URL
   * 
   * @return the dataset URL
   */
  protected String getBaseDatasetUrl() {
    return super.getBaseUrl() + "/datasets/" + datasetId;
  }

  /**
   * Get the attachment for dataset
   * 
   * @return the attachment URL for dataset
   */
  protected String getAttachDatasetUrl() {
    return SITOOLS_URL + "/datasets/" + datasetId;
  }

  /**
   * Absolute path location for project store files
   * 
   * @return path
   */
  protected String getDatasetTestRepository() {
    return super.getTestRepository() + SitoolsSettings.getInstance().getString(Consts.APP_DATASETS_STORE_DIR);
  }

  @Before
  @Override
  /**
   * Init and Start a server
   * 
   * @throws java.lang.Exception
   */
  public void setUp() throws Exception {
    super.setUp();
  }

  @After
  @Override
  /**
   * Stop server
   * @throws java.lang.Exception
   */
  public void tearDown() throws Exception {
    super.tearDown();
  }

  /**
   * Test CRUD Graph with JSon format exchanges.
   */
  @Test
  public void testCRUD() {
    docAPI.setActive(false);
    try {
      assertNone();
      ConverterModelDTO item = createConverterObject("description_conv", converterId);
      create(item);
      checknotify();
      assertNone();
    }
    catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Check the notification API by deleting the dataset and check that the converter is deleted too.
   * 
   * @throws IOException
   *           if problem
   */
  public void checknotify() throws IOException {
    // Delete the dataset
    ClientResource cr = new ClientResource(getBaseDatasetUrl());
    ChallengeResponse chr = new ChallengeResponse(ChallengeScheme.HTTP_BASIC, "admin", "admin");
    cr.setChallengeResponse(chr);
    Representation repr = cr.delete(getMediaTest());
    cr.release();
    assertNotNull(repr);
    Response response = getResponse(getMediaTest(), repr, Response.class);
    RIAPUtils.exhaust(repr);
    assertNotNull(response);
    assertTrue(response.getSuccess());
    try {
      Thread.sleep(2000);
    }
    catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  /**
   * Create WADL
   */
  public void createWadl() {
    ClientResource cr = new ClientResource(String.format(getBaseUrl(), datasetId));
    DocWadl dw = new DocWadl("dataset_converters");
    try {
      cr.options().write(dw.getWadlPrintStream());
      cr.options(MediaType.TEXT_HTML).write(dw.getHtmlPrintStream());
      cr.release();
    }
    catch (ResourceException e) {
      e.printStackTrace();
    }
    catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Create a ConverterModelDTO object with the specified description and identifier
   * 
   * @param description
   *          the description
   * @param id
   *          the ConverterModelDTO identifier
   * @return the created ConverterModelDTO
   */
  public ConverterModelDTO createConverterObject(String description, String id) {
    ConverterModelDTO conv = new ConverterModelDTO();

    conv.setClassName("fr.cnes.sitools.converter.tests.ConverterValidatorTest");
    conv.setDescriptionAction(description);
    conv.setName("TestConverter");
    conv.setClassAuthor("AKKA/CNES");
    conv.setClassVersion("1.0");
    conv.setDescription("FOR TEST PURPOSE ONLY, DON'T USE IT, IT DOESN'T DO ANYTHING");
    conv.setId(id);

    ConverterParameter param1 = new ConverterParameter("1", "1", ConverterParameterType.CONVERTER_PARAMETER_INTERN);
    param1.setValue("param1_value");
    ConverterParameter param2 = new ConverterParameter("2", "2", ConverterParameterType.CONVERTER_PARAMETER_INTERN);
    param2.setValue("param2_value");

    conv.getParameters().add(param1);
    conv.getParameters().add(param2);

    return conv;

  }

  /**
   * Add a converter to a Dataset
   * 
   * @param item
   *          ConverterModelDTO
   */
  public void create(ConverterModelDTO item) {
    Representation rep = getRepresentation(item, getMediaTest());
    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      parameters.put("identifier", "dataset identifier");
      parameters.put("POST", "A <i>ConverterChainedModel</i> object");
      postDocAPI(String.format(getBaseUrl(), datasetId), "", rep, parameters,
          String.format(getBaseUrl(), "%identifier%"));
    }
    else {
      ClientResource cr = new ClientResource(String.format(getBaseUrl(), datasetId));
      Representation result = cr.post(rep, getMediaTest());
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());
      Response response = getResponse(getMediaTest(), result, ConverterModelDTO.class);
      assertTrue(response.getSuccess());
      ConverterModelDTO conv = (ConverterModelDTO) response.getItem();
      assertEquals(item.getId(), conv.getId());
      assertEquals(item.getDescriptionAction(), conv.getDescriptionAction());
      RIAPUtils.exhaust(result);
    }

  }

  /**
   * Invokes GET and asserts result response item is null.
   * 
   * @return true if no converters are found
   * @param skipAssert
   *          to skip assertions
   * @throws IOException
   *           if problem
   */
  public boolean assertNone(boolean skipAssert) throws IOException {
    ClientResource crn = new ClientResource(String.format(getBaseUrl(), datasetId));
    Representation result = crn.get(getMediaTest());
    assertNotNull(result);
    assertTrue(crn.getStatus().isSuccess());
    Response response = getResponse(getMediaTest(), result, ConverterChainedModel.class);
    assertTrue(response.getSuccess());
    if (!skipAssert) {
      // if no converter defined, then item is null
      assertNull(response.getItem());
    }
    result.getText();
    result.exhaust();
    result.release();
    return response.getItem() == null;
  }

  /**
   * Default assertNone
   * 
   * @return true if no object found
   * @throws IOException
   *           if problem
   */
  public boolean assertNone() throws IOException {
    return assertNone(false);
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
        Logger.getLogger(AbstractSitoolsTestCase.class.getName()).warning("Only JSON or XML supported in tests");
        return null;
      }

      XStream xstream = XStreamFactory.getInstance().getXStreamReader(media);
      xstream.alias("response", Response.class);
      if (dataClass == ConverterChainedModel.class) {
        xstream.alias("converterChainedModel", ConverterChainedModel.class);
        xstream.alias("converterModel", ConverterModelDTO.class);
      }
      xstream.alias("converterParameter", ConverterParameter.class);

      if (isArray) {
        xstream.addImplicitCollection(ConverterChainedModel.class, "data", dataClass);
      }
      else {
        xstream.alias("item", dataClass);
        xstream.alias("item", Object.class, dataClass);

        if (media.equals(MediaType.APPLICATION_JSON)) {
          if (dataClass == ConverterChainedModel.class) {
            xstream.addImplicitCollection(ConverterChainedModel.class, "converters", ConverterModelDTO.class);
          }
          xstream.addImplicitCollection(ConverterModelDTO.class, "parameters", ConverterParameter.class);
        }

        if (dataClass == ConverterChainedModel.class) {
          xstream.aliasField("converterChainedModel", Response.class, "item");
        }
        if (dataClass == ConverterModelDTO.class) {
          xstream.aliasField("converter", Response.class, "item");
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
        Logger.getLogger(AbstractSitoolsTestCase.class.getName()).warning("Only JSON or XML supported in tests");
        return null; // TODO complete test with ObjectRepresentation
      }
    }
    finally {
      RIAPUtils.exhaust(representation);
    }
  }

  /**
   * Builds XML or JSON Representation of Project for Create and Update methods.
   * 
   * @param item
   *          Project
   * @param media
   *          APPLICATION_XML or APPLICATION_JSON
   * @return XML or JSON Representation
   */
  public static Representation getRepresentation(ConverterModelDTO item, MediaType media) {
    if (media.equals(MediaType.APPLICATION_JSON)) {
      return new JacksonRepresentation<ConverterModelDTO>(item);
    }
    else if (media.equals(MediaType.APPLICATION_XML)) {
      XStream xstream = XStreamFactory.getInstance().getXStream(media, false);
      XstreamRepresentation<ConverterModelDTO> rep = new XstreamRepresentation<ConverterModelDTO>(media, item);
      configure(xstream);
      rep.setXstream(xstream);
      return rep;
    }
    else {
      Logger.getLogger(AbstractSitoolsTestCase.class.getName()).warning("Only JSON or XML supported in tests");
      return null; // TODO complete test with ObjectRepresentation
    }
  }

  /**
   * Configures XStream mapping of Response object with ConverterModelDTO content.
   * 
   * @param xstream
   *          XStream
   */
  private static void configure(XStream xstream) {
    xstream.autodetectAnnotations(false);
    xstream.alias("response", Response.class);

    xstream.omitField(Response.class, "itemName");
    xstream.omitField(Response.class, "itemClass");

  }

}

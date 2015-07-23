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

import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;
import org.restlet.data.MediaType;
import org.restlet.engine.Engine;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;

import com.thoughtworks.xstream.XStream;

import fr.cnes.sitools.common.SitoolsSettings;
import fr.cnes.sitools.common.SitoolsXStreamRepresentation;
import fr.cnes.sitools.common.XStreamFactory;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.dataset.converter.dto.ConverterModelDTO;
import fr.cnes.sitools.dataset.converter.model.ConverterParameter;
import fr.cnes.sitools.dataset.plugins.converters.model.ConverterPluginsDescriptionDTO;
import fr.cnes.sitools.common.Consts;
import fr.cnes.sitools.util.RIAPUtils;

/**
 * Tests converters
 * 
 * @author AKKA Technologies
 * 
 */
@Ignore
public abstract class AbstractConverterPluginTestCase extends AbstractSitoolsServerTestCase {

  /** class name of a converter for test purpose. */
  private String className = "fr.cnes.sitools.mock.converter.ConverterValidatorTest";

  /** dataset id for test purpose. */
  private String datasetId = "bf77955a-2cec-4fc3-b95d-7397025fb299";

  /** dataset id not existing for test purpose. */
  private String datasetIdError = "456321";

  /** number of columns of the dataset */
  private int nbColumnDataset = 7;

  /** number of parameters of the Converter by default */
  private int nbParamConverter = 5;

  /**
   * relative url for dataset management REST API
   * 
   * @return url
   */
  protected String getAttachUrl() {
    return SITOOLS_URL + SitoolsSettings.getInstance().getString(Consts.APP_DATASETS_CONVERTERS_PLUGINS_URL);
  }

  /**
   * absolute url for dataset management REST API
   * 
   * @return url
   */
  protected String getBaseUrl() {
    return super.getBaseUrl() + SitoolsSettings.getInstance().getString(Consts.APP_DATASETS_CONVERTERS_PLUGINS_URL);
  }

  /**
   * Test getting converter list.
   */
  @Test
  public void test() {
    docAPI.setActive(false);
    getConverterList();
    getConverterDetailsDefault();
    getConverterDetailsWithDataset();
    getConverterDetailsWithDatasetNotFound();
    // getConverterDetailsWithNoDsConstructor();

    createWadl(getBaseUrl(), "converters_plugins");
  }

  /**
   * Produces DOC API for converters
   */
  @Test
  public void testAPI() {
    docAPI.setActive(true);
    docAPI.appendChapter("Getting Converter plugin list and details");

    docAPI.appendSubChapter("Getting converter list", "list");
    getConverterList();
    docAPI.appendSubChapter("Getting converter details from the default constructor", "details");
    getConverterDetailsDefault();
    docAPI.appendSubChapter("Getting converter details from the constructor with dataset constructor", "detailsDs");
    getConverterDetailsWithDataset();

    docAPI.close();

  }

  /**
   * getConverterList Calls GET on converter list URL.
   */
  private void getConverterList() {
    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      retrieveDocAPI(getBaseUrl(), "", parameters, getBaseUrl());
    }
    else {
      ClientResource cr = new ClientResource(getBaseUrl());
      Representation result = cr.get(getMediaTest());
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());

      Response response = getResponse(getMediaTest(), result, ConverterPluginsDescriptionDTO.class, true);
      assertTrue(response.getSuccess());
      RIAPUtils.exhaust(result);
    }
  }

  /**
   * Test converter detail properties.
   */
  private void getConverterDetailsDefault() {
    String url = getBaseUrl() + "/" + className;

    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      parameters.put("className", "Converter plugin className");
      String template = getBaseUrl() + "/%className";
      retrieveDocAPI(url, "", parameters, template);
    }
    else {
      ClientResource cr = new ClientResource(url);
      Representation result = cr.get(getMediaTest());

      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());

      Response response = getResponse(getMediaTest(), result, ConverterModelDTO.class);
      assertTrue(response.getSuccess());
      ConverterModelDTO converterPlugin = (ConverterModelDTO) response.getItem();
      assertEquals(className, converterPlugin.getClassName());
      assertEquals(nbParamConverter, converterPlugin.getParameters().size());
      RIAPUtils.exhaust(result);
    }
  }

  /**
   * Test converter detail properties with a given DataSetId. The dataset exists, the Converter is returned with 2
   * parameters + a parameter for each column of the dataset
   */
  private void getConverterDetailsWithDataset() {
    String url = getBaseUrl() + "/" + className + "/" + datasetId;

    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      parameters.put("className", "Converter plugin className");
      parameters.put("datasetId", "DataSet identifier");
      String template = getBaseUrl() + "/%className/%datasetId";
      retrieveDocAPI(url, "", parameters, template);
    }
    else {
      ClientResource cr = new ClientResource(url);
      Representation result = cr.get(getMediaTest());

      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());

      Response response = getResponse(getMediaTest(), result, ConverterModelDTO.class);
      assertTrue(response.getSuccess());
      ConverterModelDTO converterPlugin = (ConverterModelDTO) response.getItem();
      assertEquals(className, converterPlugin.getClassName());
      assertEquals(nbParamConverter + nbColumnDataset, converterPlugin.getParameters().size());
      RIAPUtils.exhaust(result);
    }
  }

  /**
   * Test converter detail properties with a given DataSetId. The dataset does not exists, the Converter returned is the
   * same as without DataSet parameter, with 2 parameters
   */
  private void getConverterDetailsWithDatasetNotFound() {
    String url = getBaseUrl() + "/" + className + "/" + datasetIdError;

    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      parameters.put("className", "Converter plugin className");
      parameters.put("datasetId", "DataSet identifier");
      String template = getBaseUrl() + "/%className/%datasetId";
      retrieveDocAPI(url, "", parameters, template);
    }
    else {
      ClientResource cr = new ClientResource(url);
      Representation result = cr.get(getMediaTest());

      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());

      Response response = getResponse(getMediaTest(), result, ConverterModelDTO.class);
      assertTrue(response.getSuccess());
      ConverterModelDTO converterPlugin = (ConverterModelDTO) response.getItem();
      assertEquals(className, converterPlugin.getClassName());
      assertEquals(nbParamConverter, converterPlugin.getParameters().size());
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
      if (!media.isCompatible(MediaType.APPLICATION_JSON) && !media.isCompatible(MediaType.APPLICATION_XML)) {
        Engine.getLogger(AbstractSitoolsTestCase.class.getName()).warning("Only JSON or XML supported in tests");
        return null;
      }

      XStream xstream = XStreamFactory.getInstance().getXStreamReader(media);
      xstream.autodetectAnnotations(false);
      xstream.alias("response", Response.class);
      xstream.alias("converter", ConverterModelDTO.class);
      xstream.alias("converterParameter", ConverterParameter.class);
      xstream.alias("ConverterPluginsDescriptionDTO", ConverterPluginsDescriptionDTO.class);

      if (isArray) {
        xstream.alias("item", dataClass);
        xstream.alias("item", Object.class, dataClass);
        // xstream.omitField(Response.class, "data");
        if (media.isCompatible(MediaType.APPLICATION_JSON)) {
          xstream.addImplicitCollection(Response.class, "data", dataClass);
          xstream.addImplicitCollection(ConverterModelDTO.class, "parameters", ConverterParameter.class);

        }

      }
      else {
        xstream.alias("item", dataClass);
        xstream.alias("item", Object.class, dataClass);

        if (dataClass == ConverterPluginsDescriptionDTO.class) {
          xstream.aliasField("ConverterPluginsDescriptionDTO", Response.class, "item");
        }
        if (dataClass == ConverterModelDTO.class) {
          xstream.aliasField("converter", Response.class, "item");
          if (media.isCompatible(MediaType.APPLICATION_JSON)) {
            xstream.addImplicitCollection(ConverterModelDTO.class, "parameters", ConverterParameter.class);
          }
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
        Engine.getLogger(AbstractSitoolsTestCase.class.getName()).warning("Only JSON or XML supported in tests");
        return null; // TODO complete test with ObjectRepresentation
      }
    }
    finally {
      RIAPUtils.exhaust(representation);
    }
  }

}

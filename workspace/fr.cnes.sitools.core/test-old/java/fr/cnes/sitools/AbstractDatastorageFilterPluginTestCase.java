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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
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
import fr.cnes.sitools.plugins.filters.dto.FilterModelDTO;
import fr.cnes.sitools.plugins.filters.model.FilterParameter;
import fr.cnes.sitools.common.Consts;
import fr.cnes.sitools.util.RIAPUtils;

/**
 * Tests filters
 * 
 * @author AKKA Technologies
 * 
 */
@Ignore
public abstract class AbstractDatastorageFilterPluginTestCase extends AbstractSitoolsServerTestCase {

  /** class name of a filter for test purpose. */
  private String className = "fr.cnes.sitools.mock.authorizer.DataStorageAuthorizerModel";

  /** number of parameters of the Filter by default */
  private int nbParamFilter = 2;

  /**
   * absolute url for dataset management REST API
   * 
   * @return url
   */
  protected String getBaseUrl() {
    return super.getBaseUrl() + SitoolsSettings.getInstance().getString(Consts.APP_PLUGINS_FILTERS_CLASSES_URL);
  }

  /**
   * Test getting filter list.
   */
  @Test
  public void test() {
    docAPI.setActive(false);
    try {
      getFilterList();
      getFilterDetailsDefault();
      getFilterDetailsError();
    }
    catch (IOException e) {

      e.printStackTrace();
    }
    createWadl(getBaseUrl(), "filters_plugins");
  }

  /**
   * Produces DOC API for filters
   */
  @Test
  public void testAPI() {
    docAPI.setActive(true);
    docAPI.appendChapter("Getting Filter plugin list and details");
    try {
      docAPI.appendSubChapter("Getting filter list", "list");
      getFilterList();
      docAPI.appendSubChapter("Getting filter details from the default constructor", "details");
      getFilterDetailsDefault();
      docAPI.appendSubChapter("Getting filter details with a unknown class name, expect an error", "details_error");
      getFilterDetailsError();

    }
    catch (IOException e) {

      e.printStackTrace();
    }

    docAPI.close();

  }

  /**
   * getFilterList Calls GET on filter list URL.
   * 
   * @throws IOException
   *           Exception when copying configuration files from TEST to data/TESTS
   */
  private void getFilterList() throws IOException {
    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      retrieveDocAPI(getBaseUrl(), "", parameters, getBaseUrl());
    }
    else {
      ClientResource cr = new ClientResource(getBaseUrl());
      Representation result = cr.get(getMediaTest());
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());

      Response response = getResponse(getMediaTest(), result, FilterModelDTO.class, true);
      assertTrue(response.getSuccess());
      RIAPUtils.exhaust(result);
    }
  }

  /**
   * Test filter detail properties.
   * 
   * @throws IOException
   *           Exception when copying configuration files from TEST to data/TESTS
   */
  private void getFilterDetailsDefault() throws IOException {
    String url = getBaseUrl() + "/" + className;

    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      parameters.put("className", "Filter plugin className");
      String template = getBaseUrl() + "/%className";
      retrieveDocAPI(url, "", parameters, template);
    }
    else {
      ClientResource cr = new ClientResource(url);
      Representation result = cr.get(getMediaTest());

      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());

      Response response = getResponse(getMediaTest(), result, FilterModelDTO.class);
      assertTrue(response.getSuccess());
      FilterModelDTO filterPlugin = (FilterModelDTO) response.getItem();
      assertEquals(className, filterPlugin.getClassName());
      assertEquals(nbParamFilter, filterPlugin.getParameters().size());
      RIAPUtils.exhaust(result);
    }
  }

  private void getFilterDetailsError() {
    String url = getBaseUrl() + "/unkownclassname";

    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      parameters.put("className", "Filter plugin className");
      String template = getBaseUrl() + "/%className";
      retrieveDocAPI(url, "", parameters, template);
    }
    else {
      ClientResource cr = new ClientResource(url);
      Representation result = cr.get(getMediaTest());

      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());

      Response response = getResponse(getMediaTest(), result, FilterModelDTO.class);
      assertFalse(response.getSuccess());
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
      xstream.alias("filterPlugin", FilterModelDTO.class);
      xstream.alias("filterParameter", FilterParameter.class);

      if (isArray) {
        xstream.alias("item", dataClass);
        xstream.alias("item", Object.class, dataClass);
        // xstream.omitField(Response.class, "data");
        if (media.isCompatible(MediaType.APPLICATION_JSON)) {
          xstream.addImplicitCollection(Response.class, "data", dataClass);
          xstream.addImplicitCollection(FilterModelDTO.class, "parameters", FilterParameter.class);

        }

      }
      else {
        xstream.alias("item", dataClass);
        xstream.alias("item", Object.class, dataClass);

        if (dataClass == FilterModelDTO.class) {
          xstream.aliasField("filterPlugin", Response.class, "item");
          if (media.isCompatible(MediaType.APPLICATION_JSON)) {
            xstream.addImplicitCollection(FilterModelDTO.class, "parameters", FilterParameter.class);
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
        return null;
      }
    }
    finally {
      RIAPUtils.exhaust(representation);
    }
  }

}

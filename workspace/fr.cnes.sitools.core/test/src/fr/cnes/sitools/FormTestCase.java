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

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.restlet.Component;
import org.restlet.Context;
import org.restlet.data.MediaType;
import org.restlet.data.Protocol;
import org.restlet.engine.Engine;
import org.restlet.ext.jackson.JacksonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;

import com.thoughtworks.xstream.XStream;

import fr.cnes.sitools.common.SitoolsSettings;
import fr.cnes.sitools.common.SitoolsXStreamRepresentation;
import fr.cnes.sitools.common.XStreamFactory;
import fr.cnes.sitools.common.application.ContextAttributes;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.form.dataset.FormApplication;
import fr.cnes.sitools.form.dataset.FormStoreInterface;
import fr.cnes.sitools.form.dataset.FormStoreXMLMap;
import fr.cnes.sitools.form.dataset.dto.FormDTO;
import fr.cnes.sitools.form.dataset.dto.ParameterDTO;
import fr.cnes.sitools.server.Consts;
import fr.cnes.sitools.util.RIAPUtils;

/**
 * 
 * Test CRUD Form Rest API
 * 
 * @since UserStory : ADM Forms, Sprint : 4
 * 
 * @author jp.boignard (AKKA Technologies)
 */
public class FormTestCase extends AbstractSitoolsTestCase {
  /**
   * static xml store instance for the test
   */
  private static FormStoreInterface store = null;

  /**
   * Restlet Component for server
   */
  private Component component = null;

  /**
   * absolute url for form management REST API
   * 
   * @return url
   */
  protected String getBaseUrl() {
    return super.getBaseUrl() + SitoolsSettings.getInstance().getString(Consts.APP_FORMS_URL);
  }

  /**
   * relative url for form management REST API
   * 
   * @return url
   */
  protected String getAttachUrl() {
    return SITOOLS_URL + SitoolsSettings.getInstance().getString(Consts.APP_FORMS_URL);
  }

  /**
   * Absolute path location for form store files
   * 
   * @return path
   */
  protected String getTestRepository() {
    return super.getTestRepository() + SitoolsSettings.getInstance().getString(Consts.APP_FORMS_STORE_DIR) + "/map";
  }

  @Before
  @Override
  /**
   * Init and Start a server with FormApplication
   * 
   * @throws java.lang.Exception
   */
  public void setUp() throws Exception {

    if (this.component == null) {
      this.component = new Component();
      this.component.getServers().add(Protocol.HTTP, getTestPort());
      this.component.getClients().add(Protocol.HTTP);
      this.component.getClients().add(Protocol.FILE);
      this.component.getClients().add(Protocol.CLAP);

      // Context
      Context ctx = this.component.getContext().createChildContext();
      ctx.getAttributes().put(ContextAttributes.SETTINGS, SitoolsSettings.getInstance());
      if (store == null) {
        File storeDirectory = new File(getTestRepository());
        storeDirectory.mkdirs();
        cleanDirectory(storeDirectory);cleanMapDirectories(storeDirectory);
        store = new FormStoreXMLMap(storeDirectory, ctx);
      }
      ctx.getAttributes().put(ContextAttributes.APP_STORE, store);

      this.component.getDefaultHost().attach(getAttachUrl(), new FormApplication(ctx));
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
    this.component.stop();
    this.component = null;
  }

  /**
   * Test CRUD Form with JSon format exchanges.
   */
  @Test
  public void tstCRUD() {
    assertNone();
    create();
    retrieve();
    update();
    delete();
    assertNone();
  }

  /**
   * Invoke POST
   */
  public void create() {
    FormDTO item = new FormDTO();
    item.setId("1000000");
    Representation rep = new JacksonRepresentation<FormDTO>(item);
    ClientResource cr = new ClientResource(getBaseUrl());
    Representation result = cr.post(rep, MediaType.APPLICATION_JSON);
    assertNotNull(result);
    assertTrue(cr.getStatus().isSuccess());

    Response response = getResponse(MediaType.APPLICATION_JSON, result, FormDTO.class);
    assertTrue(response.getSuccess());
    FormDTO form = (FormDTO) response.getItem();
    assertEquals(form.getName(), item.getName());
    assertEquals(form.getDescription(), item.getDescription());
  }

  /**
   * Invoke GET
   */
  public void retrieve() {
    FormDTO item = new FormDTO();
    item.setId("1000000");
    ClientResource cr = new ClientResource(getBaseUrl() + "/" + item.getId());
    Representation result = cr.get(MediaType.APPLICATION_JSON);
    assertNotNull(result);
    assertTrue(cr.getStatus().isSuccess());

    Response response = getResponse(MediaType.APPLICATION_JSON, result, FormDTO.class);
    assertTrue(response.getSuccess());
    FormDTO form = (FormDTO) response.getItem();
    assertEquals(form.getName(), item.getName());
    assertEquals(form.getDescription(), item.getDescription());
  }

  /**
   * Invoke PUT
   */
  public void update() {
    FormDTO item = new FormDTO();
    item.setId("1000000");
    Representation rep = new JacksonRepresentation<FormDTO>(item);
    ClientResource cr = new ClientResource(getBaseUrl() + "/" + item.getId());
    Representation result = cr.put(rep, MediaType.APPLICATION_JSON);
    assertNotNull(result);
    assertTrue(cr.getStatus().isSuccess());

    Response response = getResponse(MediaType.APPLICATION_JSON, result, FormDTO.class);
    assertTrue(response.getSuccess());
    FormDTO form = (FormDTO) response.getItem();
    assertEquals(form.getName(), item.getName());
    assertEquals(form.getDescription(), item.getDescription());
  }

  /**
   * Invoke DELETE
   */
  public void delete() {
    FormDTO item = new FormDTO();
    item.setId("1000000");
    ClientResource cr = new ClientResource(getBaseUrl() + "/" + item.getId());
    Representation result = cr.delete(MediaType.APPLICATION_JSON);
    assertNotNull(result);
    assertTrue(cr.getStatus().isSuccess());

    Response response = getResponse(MediaType.APPLICATION_JSON, result, FormDTO.class);
    assertTrue(response.getSuccess());
  }

  /**
   * Invokes GET and asserts result response is an empty array.
   */
  public void assertNone() {
    ClientResource cr = new ClientResource(getBaseUrl());
    Representation result = cr.get(MediaType.APPLICATION_JSON);
    assertNotNull(result);
    assertTrue(cr.getStatus().isSuccess());

    Response response = getResponse(MediaType.APPLICATION_JSON, result, FormDTO.class);
    assertTrue(response.getMessage(), response.getSuccess());
    assertEquals(response.getTotal().intValue(), 0);
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
      xstream.alias("form", FormDTO.class);
      // xstream.alias("dataset", Resource.class);

      if (isArray) {
        xstream.addImplicitCollection(Response.class, "data", dataClass);
      }
      else {
        xstream.alias("item", dataClass);
        xstream.alias("item", Object.class, dataClass);
        xstream.addImplicitCollection(FormDTO.class, "parameters", ParameterDTO.class);
        xstream.aliasField("parameters", FormDTO.class, "parameters");

        if (dataClass == FormDTO.class) {
          xstream.aliasField("form", Response.class, "item");
          // if (dataClass == DataSet.class)
          // xstream.aliasField("dataset", Response.class, "item");
        }
      }
      xstream.aliasField("data", Response.class, "data");

      SitoolsXStreamRepresentation<Response> rep = new SitoolsXStreamRepresentation<Response>(representation);
      rep.setXstream(xstream);

      if (media.isCompatible(MediaType.APPLICATION_JSON)) {
        Response response = rep.getObject("response");

        return response;
      }
      else {
        Engine.getLogger(AbstractSitoolsTestCase.class.getName()).warning("Only JSON or XML supported in tests");
        return null; // TODO complete test for XML, Object
      }
    }
    finally {
      RIAPUtils.exhaust(representation);
    }
  }
}

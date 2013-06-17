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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Logger;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.restlet.Component;
import org.restlet.Context;
import org.restlet.data.MediaType;
import org.restlet.data.Protocol;
import org.restlet.data.Reference;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;

import com.thoughtworks.xstream.XStream;

import fr.cnes.sitools.common.SitoolsSettings;
import fr.cnes.sitools.common.SitoolsXStreamRepresentation;
import fr.cnes.sitools.common.XStreamFactory;
import fr.cnes.sitools.common.application.ContextAttributes;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.common.store.SitoolsStore;
import fr.cnes.sitools.form.dataset.FormApplication;
import fr.cnes.sitools.form.dataset.FormStoreXML;
import fr.cnes.sitools.form.dataset.dto.FormDTO;
import fr.cnes.sitools.form.dataset.dto.ParameterDTO;
import fr.cnes.sitools.form.dataset.dto.ValueDTO;
import fr.cnes.sitools.form.dataset.model.Form;
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
public class FormDTOTestCase extends AbstractSitoolsTestCase {
  /**
   * static xml store instance for the test
   */
  private static SitoolsStore<Form> store = null;

  /**
   * Restlet Component for server
   */
  private Component component = null;

  /**
   * DataSet id for tests
   */
  private String dataSetId = "999";

  /**
   * absolute url for form management REST API
   * 
   * @return url
   */
  protected String getBaseUrl() {
    return super.getBaseUrl() + "/%s" + SitoolsSettings.getInstance().getString(Consts.APP_FORMS_URL);
  }

  /**
   * relative url for form management REST API
   * 
   * @return url
   */
  protected String getAttachUrl() {
    return SITOOLS_URL + "/{datasetId}" + SitoolsSettings.getInstance().getString(Consts.APP_FORMS_URL);
  }

  /**
   * Absolute path location for form store files
   * 
   * @return path
   */
  protected String getTestRepository() {
    return super.getTestRepository() + SitoolsSettings.getInstance().getString(Consts.APP_FORMS_STORE_DIR);
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
        cleanDirectory(storeDirectory);
        store = new FormStoreXML(storeDirectory, ctx);
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
  public void testCRUD() {
    FormDTO form = createObject("1000000");
    assertNone(form);
    create(form);
    retrieve(form);
    retrieveByName(form);
    update(form);
    delete(form);
    assertNone(form);
    createWadl(String.format(getBaseUrl(), dataSetId), "dataset_forms");
  }

  /**
   * Invoke POST
   * 
   * @param id
   *          form identifier
   * @return FormDTO
   */
  public FormDTO createObject(String id) {
    FormDTO item = new FormDTO();

    item.setId(id);
    item.setName("name");
    item.setDescription("description");

    ArrayList<ParameterDTO> params = new ArrayList<ParameterDTO>();
    /** Checkbox */
    ParameterDTO param = new ParameterDTO();
    ArrayList<String> code = new ArrayList<String>();
    code.add("id1");
    param.setCode(code);
    param.setType("CHECKBOX");
    ValueDTO v1 = new ValueDTO();
    v1.setCode("code");
    v1.setSelected(true);
    v1.setValue("valeur");
    ValueDTO v2 = new ValueDTO();
    v2.setCode("code");
    v2.setSelected(true);
    v2.setValue("valeur");
    ArrayList<ValueDTO> av = new ArrayList<ValueDTO>();
    av.add(v1);
    av.add(v2);
    param.setValues(av);
    params.add(param);

    param = new ParameterDTO();
    code.remove("id1");
    code.add("id2");
    /**Radio */
    param.setCode(code);
    param.setType("RADIO");
    params.add(param);
    ValueDTO v3 = new ValueDTO();
    v3.setCode("code");
    v3.setSelected(true);
    v3.setValue("valeur");
    ValueDTO v4 = new ValueDTO();
    v4.setCode("code");
    v4.setSelected(true);
    v4.setValue("valeur");
    ArrayList<ValueDTO> av2 = new ArrayList<ValueDTO>();
    av2.add(v1);
    av2.add(v2);
    param.setValues(av2);
    params.add(param);
    
    
    /** NoSelection */
    
    
    item.setParameters(params);
    return item;
  }

  /**
   * Invoke POST
   * 
   * @param item
   *          FormDTO
   */
  public void create(FormDTO item) {

    Representation rep = new JsonRepresentation(item);
    ClientResource cr = new ClientResource(String.format(getBaseUrl(), dataSetId));

    Representation result = cr.post(rep, MediaType.APPLICATION_JSON);

    assertNotNull(result);
    assertTrue(cr.getStatus().isSuccess());

    Response response = getResponse(MediaType.APPLICATION_JSON, result, FormDTO.class);
    assertTrue(response.getSuccess());
    FormDTO form = (FormDTO) response.getItem();
    assertEquals(form.getName(), item.getName());
    assertEquals(form.getDescription(), item.getDescription());
    RIAPUtils.exhaust(result);
    cr.release();

  }

  /**
   * Invoke GET
   * 
   * @param item
   *          FormDTO
   */
  public void retrieve(FormDTO item) {
    ClientResource cr = new ClientResource(String.format(getBaseUrl(), dataSetId) + "/" + item.getId());
    Representation result = cr.get(MediaType.APPLICATION_JSON);
    assertNotNull(result);
    assertTrue(cr.getStatus().isSuccess());

    Response response = getResponse(MediaType.APPLICATION_JSON, result, FormDTO.class);
    assertTrue(response.getSuccess());
    FormDTO form = (FormDTO) response.getItem();
    assertEquals(form.getName(), item.getName());
    assertEquals(form.getDescription(), item.getDescription());
    RIAPUtils.exhaust(result);
    cr.release();
  }
  
  /**
   * Invoke GET
   * 
   * @param item
   *          FormDTO
   */
  public void retrieveByName(FormDTO item) {
    
    Reference ref = new Reference(String.format(getBaseUrl(), dataSetId));
    ref.addQueryParameter("query", "name");
    
    ClientResource cr = new ClientResource(ref);
    
    Representation result = cr.get(MediaType.APPLICATION_JSON);
       
    assertNotNull(result);
    assertTrue(cr.getStatus().isSuccess());

    Response response = getResponse(MediaType.APPLICATION_JSON, result, FormDTO.class, true);
    assertTrue(response.getSuccess());
    assertEquals(new Integer(1), response.getTotal());
    
    RIAPUtils.exhaust(result);
    cr.release();

  }

  /**
   * Invoke PUT
   * 
   * @param item
   *          FormDTO
   */
  public void update(FormDTO item) {
    Representation rep = new JsonRepresentation(item);
    ClientResource cr = new ClientResource(String.format(getBaseUrl(), dataSetId) + "/" + item.getId());
    Representation result = cr.put(rep, MediaType.APPLICATION_JSON);
    assertNotNull(result);
    assertTrue(cr.getStatus().isSuccess());

    Response response = getResponse(MediaType.APPLICATION_JSON, result, FormDTO.class);
    assertTrue(response.getSuccess());
    FormDTO form = (FormDTO) response.getItem();
    assertEquals(form.getName(), item.getName());
    assertEquals(form.getDescription(), item.getDescription());

    RIAPUtils.exhaust(result);
    cr.release();
  }

  /**
   * Invoke DELETE
   * 
   * @param item
   *          FormDTO
   */
  public void delete(FormDTO item) {
    ClientResource cr = new ClientResource(String.format(getBaseUrl(), dataSetId) + "/" + item.getId());
    Representation result = cr.delete(MediaType.APPLICATION_JSON);
    assertNotNull(result);
    assertTrue(cr.getStatus().isSuccess());

    Response response = getResponse(MediaType.APPLICATION_JSON, result, FormDTO.class);
    assertTrue(response.getSuccess());

    RIAPUtils.exhaust(result);
    cr.release();
  }

  /**
   * Invokes GET and asserts result response is an empty array.
   * 
   * @param item
   *          FormDTO
   */
  public void assertNone(FormDTO item) {
    ClientResource cr = new ClientResource(String.format(getBaseUrl(), dataSetId));
    Representation result = cr.get(MediaType.APPLICATION_JSON);
    assertNotNull(result);
    assertTrue(cr.getStatus().isSuccess());

    Response response = getResponse(MediaType.APPLICATION_JSON, result, FormDTO.class);
    assertTrue(response.getSuccess());
    assertEquals(response.getTotal().intValue(), 0);

    RIAPUtils.exhaust(result);
    cr.release();
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
        Logger.getLogger(AbstractSitoolsTestCase.class.getName()).warning("Only JSON or XML supported in tests");
        return null;
      }

      XStream xstream = XStreamFactory.getInstance().getXStreamReader(media);
      xstream.autodetectAnnotations(false);
      xstream.alias("response", Response.class);

      if (isArray) {
        xstream.addImplicitCollection(Response.class, "data", dataClass);
        xstream.addImplicitCollection(FormDTO.class, "parameters", "parameters", ParameterDTO.class);
        xstream.addImplicitCollection(ParameterDTO.class, "values", "values", ValueDTO.class);
      }
      else {
        xstream.alias("item", dataClass);
        xstream.alias("item", Object.class, dataClass);

        // Sans []
        xstream.addImplicitCollection(FormDTO.class, "parameters", "parameters", ParameterDTO.class);
        xstream.addImplicitCollection(ParameterDTO.class, "values", "values", ValueDTO.class);

        // Avec []
        xstream.aliasField("parameters", FormDTO.class, "parameters");
        xstream.aliasField("values", ParameterDTO.class, "values");

        xstream.alias("parameters", ParameterDTO.class);
        xstream.alias("values", ValueDTO.class);

        // xstream.aliasField("paramItems", FormDTO.class, "parameters");
        // xstream.aliasField("valueItems", ParameterDTO.class, "values");
        //
        // xstream.alias("paramItems", ParameterDTO.class);
        // xstream.alias("valueItems", ValueDTO.class);

        if (dataClass == FormDTO.class) {
          xstream.alias("form", FormDTO.class);
          xstream.aliasField("form", Response.class, "item");

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
        Logger.getLogger(AbstractSitoolsTestCase.class.getName()).warning("Only JSON or XML supported in tests");
        return null; // TODO complete test for XML, Object
      }
    }
    finally {
      RIAPUtils.exhaust(representation);
    }

  }
}

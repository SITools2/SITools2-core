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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
import fr.cnes.sitools.common.model.Resource;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.dataset.opensearch.OpenSearchApplication;
import fr.cnes.sitools.dataset.opensearch.OpenSearchStoreInterface;
import fr.cnes.sitools.dataset.opensearch.OpenSearchStoreXMLMap;
import fr.cnes.sitools.dataset.opensearch.model.Opensearch;
import fr.cnes.sitools.dataset.opensearch.model.OpensearchColumn;
import fr.cnes.sitools.common.Consts;
import fr.cnes.sitools.util.RIAPUtils;

/**
 * 
 * Test CRUD DataSet Rest API
 * 
 * @since UserStory : ADM DataSets, Sprint : 4
 * 
 * @author jp.boignard (AKKA Technologies)
 */
public class OpenSearchTestCase extends AbstractSitoolsTestCase {
  /**
   * static xml store instance for the test
   */
  private static OpenSearchStoreInterface store = null;

  /**
   * Restlet Component for server
   */
  private Component component = null;

  /**
   * relative url for dataset management REST API
   * 
   * @return url
   */
  protected String getAttachUrl() {
    return SITOOLS_URL + "/{datasetId}" + SitoolsSettings.getInstance().getString(Consts.APP_OPENSEARCH_URL);
  }

  /**
   * absolute url for dataset management REST API
   * 
   * @return url
   */
  protected String getBaseUrl() {
    return super.getBaseUrl() + "/%s" + SitoolsSettings.getInstance().getString(Consts.APP_OPENSEARCH_URL);
  }

  /**
   * absolute url for dataset management REST API
   * 
   * @return url
   */
  protected String getBaseRefUrl() {
    return super.getBaseUrl() + "/{datasetId}" + SitoolsSettings.getInstance().getString(Consts.APP_OPENSEARCH_URL);
  }

  /**
   * Absolute path location for data set store files
   * 
   * @return path
   */
  protected String getTestRepository() {
    return super.getTestRepository() + SitoolsSettings.getInstance().getString(Consts.APP_OPENSEARCH_STORE_DIR)
        + "/map";
  }

  @Before
  @Override
  /**
   * Create component, store and application and start server
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
        store = new OpenSearchStoreXMLMap(storeDirectory, ctx);
      }

      ctx.getAttributes().put(ContextAttributes.APP_STORE, store);
      ctx.getAttributes().put(ContextAttributes.APP_ATTACH_REF, getAttachUrl());
      this.component.getDefaultHost().attach(getAttachUrl(), new OpenSearchApplication(ctx));
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
   * Test CRUD DataSet with JSon format exchanges.
   */
  @Test
  public void testCRUDJson() {
    Opensearch item = createObject("1000000");

    assertNone(item);

    create(item);
    retrieve(item);

    item.setName("name_modified");
    item.setDescription("description_modified");

    ArrayList<OpensearchColumn> indexedColumn = new ArrayList<OpensearchColumn>();
    OpensearchColumn col1 = new OpensearchColumn();
    col1.setIdColumn("453216557854621");
    col1.setType("text");
    OpensearchColumn col2 = new OpensearchColumn();
    col2.setIdColumn("fdsjklmfcv;,cxklvjx");
    col2.setType("text");
    OpensearchColumn col3 = new OpensearchColumn();
    col3.setIdColumn("column indexed");
    col3.setType("text");
    OpensearchColumn col4 = new OpensearchColumn();
    col4.setIdColumn("");
    col4.setType("text");

    indexedColumn.add(col1);
    indexedColumn.add(col2);
    indexedColumn.add(col3);
    indexedColumn.add(col4);

    item.setIndexedColumns(indexedColumn);

    item.setIndexPath("/mfjdksl/fkdslm/fjkdslm");
    item.setGuidField("guidField");
    item.setLinkField("linkField");
    item.setTitleField("titleField");
    item.setDescriptionField("descriptionField");
    item.setPubDateField("pubDateField");

    item.setUniqueKey("453216557854621");
    item.setDefaultSearchField("453216557854621");

    Resource image = item.getImage();
    image.setUrl("http://fldsmfkslmdfk");
    image.setType("Image");
    image.setMediaType("Image");

    // TODO update other properties

    update(item);
    delete(item);
    assertNone(item);
    createWadl(String.format(getBaseUrl(), item.getId()), "dataset_opensearch");
  }

  /**
   * Invokes GET and asserts result response is an empty array.
   * 
   * @param item
   *          Opensearch needed to set datasetId (= item.id) in the url
   */
  public void assertNone(Opensearch item) {
    ClientResource cr = new ClientResource(String.format(getBaseUrl(), item.getId()));
    Representation result = cr.get(MediaType.APPLICATION_JSON);
    assertNotNull(result);
    assertTrue(cr.getStatus().isSuccess());

    Response response = getResponse(MediaType.APPLICATION_JSON, result, Opensearch.class, true);
    assertTrue(response.getSuccess());
  }

  /**
   * Invoke POST
   * 
   * @param item
   *          DataSet
   */
  public void create(Opensearch item) {
    Representation rep = getRepresentation(item);
    ClientResource cr = new ClientResource(String.format(getBaseUrl(), item.getId()));
    Representation result = cr.post(rep, MediaType.APPLICATION_JSON);
    assertTrue(cr.getStatus().isSuccess());
    assertNotNull(result);

    Response response = getResponse(MediaType.APPLICATION_JSON, result, Opensearch.class);
    assertTrue(response.getSuccess());
    assertNotNull(response.getItem());

    Opensearch rs = (Opensearch) response.getItem();
    assertEqualsOpensearch(rs, item);
  }

  /**
   * Create object
   * 
   * @param id
   *          new instance identifier
   * @return a DataSet instance for tests
   */
  public Opensearch createObject(String id) {
    Opensearch item = new Opensearch();
    item.setId(id);
    item.setParent(id);
    item.setName("test_Name");
    item.setDescription("test_Description");

    ArrayList<OpensearchColumn> indexedColumn = new ArrayList<OpensearchColumn>();
    OpensearchColumn col1 = new OpensearchColumn();
    col1.setIdColumn("453216557854621");
    col1.setType("text");
    OpensearchColumn col2 = new OpensearchColumn();
    col2.setIdColumn("fdsjklmfcv;,cxklvjx");
    col2.setType("text");
    OpensearchColumn col3 = new OpensearchColumn();
    col3.setIdColumn("column indexed");
    col3.setType("text");
    OpensearchColumn col4 = new OpensearchColumn();
    col4.setIdColumn("");
    col4.setType("text");

    indexedColumn.add(col1);
    indexedColumn.add(col2);
    indexedColumn.add(col3);
    indexedColumn.add(col4);

    item.setIndexedColumns(indexedColumn);

    item.setIndexPath("/mfjdksl/fkdslm/fjkdslm");
    item.setGuidField("guidField");
    item.setLinkField("linkField");
    item.setTitleField("titleField");
    item.setDescriptionField("descriptionField");
    item.setPubDateField("pubDateField");

    Resource image = new Resource();
    image.setUrl("http://fldsmfkslmdfk");
    image.setType("Image");
    image.setMediaType("Image");
    item.setImage(image);

    item.setUniqueKey("453216557854621");
    item.setDefaultSearchField("453216557854621");

    return item;
  }

  /**
   * Invoke GET
   * 
   * @param item
   *          DataSet
   */
  public void retrieve(Opensearch item) {
    ClientResource cr = new ClientResource(String.format(getBaseUrl(), item.getId()));
    Representation result = cr.get(MediaType.APPLICATION_JSON);
    assertTrue(cr.getStatus().isSuccess());
    assertNotNull(result);
    assertTrue(cr.getStatus().isSuccess());

    Response response = getResponse(MediaType.APPLICATION_JSON, result, Opensearch.class);
    assertTrue(response.getSuccess());
    assertNotNull(response.getItem());
    Opensearch rs = (Opensearch) response.getItem();
    assertEqualsOpensearch(rs, item);
  }

  /**
   * Invoke PUT
   * 
   * @param item
   *          DataSet
   */
  public void update(Opensearch item) {
    Representation rep = getRepresentation(item);

    ClientResource cr = new ClientResource(String.format(getBaseUrl(), item.getId())); // +
                                                                                       // "/"
                                                                                       // +
                                                                                       // item.getId());
    Representation result = cr.put(rep, MediaType.APPLICATION_JSON);
    assertTrue(cr.getStatus().isSuccess());
    assertNotNull(result);

    Response response = getResponse(MediaType.APPLICATION_JSON, result, Opensearch.class);
    assertTrue(response.getSuccess());
    assertNotNull(response.getItem());
    Opensearch rs = (Opensearch) response.getItem();
    assertEqualsOpensearch(rs, item);
  }

  /**
   * Get the Json representation of the Opensearch object
   * 
   * @param item
   *          the opensearch object
   * @return the json representation
   */
  private Representation getRepresentation(Opensearch item) {
    Representation rep = new JacksonRepresentation<Opensearch>(item);
    return rep;
  }

  /**
   * Assert 2 opensearches are identicals
   * 
   * @param item
   *          expected
   * @param rs
   *          new opensearch
   */
  public void assertEqualsOpensearch(Opensearch item, Opensearch rs) {
    assertEquals(rs.getName(), item.getName());
    assertEquals(rs.getDescription(), item.getDescription());

    assertEquals(rs.getDescriptionField(), item.getDescriptionField());
    assertEquals(rs.getGuidField(), item.getGuidField());
    assertEquals(rs.getId(), item.getId());
    assertEquals(rs.getIndexPath(), item.getIndexPath());
    assertEquals(rs.getLinkField(), item.getLinkField());
    assertEquals(rs.getName(), item.getName());
    assertEquals(rs.getParent(), item.getParent());
    assertEquals(rs.getPubDateField(), item.getPubDateField());
    assertEquals(rs.getStatus(), item.getStatus());
    assertEquals(rs.getTitleField(), item.getTitleField());
    assertEqualsColumns(rs.getIndexedColumns(), item.getIndexedColumns());

    // assertEquals(rs.getImage(),item.getImage());

    // TODO assert all properties
  }

  /**
   * AssertEqualsColumns
   * 
   * @param indexedColumns
   *          a list of indexed columns
   * @param indexedColumns2
   *          another list of indexed columns
   */
  private void assertEqualsColumns(List<OpensearchColumn> indexedColumns, List<OpensearchColumn> indexedColumns2) {

    assertEquals(indexedColumns2.size(), indexedColumns.size());

    Iterator<OpensearchColumn> iterator1 = indexedColumns.iterator();
    Iterator<OpensearchColumn> iterator2 = indexedColumns2.iterator();
    while (iterator1.hasNext() && iterator2.hasNext()) {
      OpensearchColumn opensearchColumn1 = iterator1.next();
      OpensearchColumn opensearchColumn2 = iterator2.next();

      assertEquals(opensearchColumn1.getIdColumn(), opensearchColumn2.getIdColumn());
      assertEquals(opensearchColumn1.getType(), opensearchColumn2.getType());

    }
  }

  /**
   * Invoque DELETE
   * 
   * @param item
   *          DataSet
   */
  public void delete(Opensearch item) {
    ClientResource cr = new ClientResource(String.format(getBaseUrl(), item.getId())); // +
                                                                                       // "/"
                                                                                       // +
                                                                                       // item.getId());
    Representation result = cr.delete(MediaType.APPLICATION_JSON);
    assertTrue(cr.getStatus().isSuccess());
    assertNotNull(result);
    assertTrue(cr.getStatus().isSuccess());

    Response response = getResponse(MediaType.APPLICATION_JSON, result, Opensearch.class);
    assertTrue(response.getSuccess());
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
      xstream.alias("opensearch", Opensearch.class);
      xstream.alias("opensearchColumn", OpensearchColumn.class);

      // xstream.aliasField("structures", DataSet.class, "structures");
      // xstream.aliasField("columnModel", DataSet.class, "columnModel");

      // xstream.addImplicitCollection(DataSet.class, "columnModel",
      // "columnModel", Column.class);

      if (isArray) {
        xstream.addImplicitCollection(Response.class, "data", dataClass);
      }
      else {
        xstream.alias("item", dataClass);
        xstream.alias("item", Object.class, dataClass);
        xstream.addImplicitCollection(Opensearch.class, "indexedColumns", OpensearchColumn.class);
        // xstream.aliasField("indexedColumns", Opensearch.class, "indexedColumns");

        if (dataClass == Opensearch.class) {
          xstream.aliasField("opensearch", Response.class, "item");
        }
      }
      xstream.aliasField("data", Response.class, "data");

      SitoolsXStreamRepresentation<Response> rep = new SitoolsXStreamRepresentation<Response>(representation);
      rep.setXstream(xstream);

      if (media.isCompatible(MediaType.APPLICATION_JSON)) {
        Response response = rep.getObject("response");
        // Response response = rep.getObject();

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

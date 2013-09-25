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

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.ext.jackson.JacksonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;

import fr.cnes.sitools.common.SitoolsSettings;
import fr.cnes.sitools.common.model.Resource;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.dataset.dto.DataSetExpositionDTO;
import fr.cnes.sitools.dataset.model.Column;
import fr.cnes.sitools.dataset.model.DataSet;
import fr.cnes.sitools.dataset.model.SpecificColumnType;
import fr.cnes.sitools.dataset.model.structure.SitoolsStructure;
import fr.cnes.sitools.datasource.jdbc.model.Structure;
import fr.cnes.sitools.datasource.jdbc.model.Table;
import fr.cnes.sitools.properties.model.SitoolsProperty;
import fr.cnes.sitools.properties.model.SitoolsPropertyType;
import fr.cnes.sitools.server.Consts;
import fr.cnes.sitools.util.RIAPUtils;
import fr.cnes.sitools.utils.GetResponseUtils;

/**
 * 
 * Test CRUD DataSet Rest API
 * 
 * @since UserStory : ADM DataSets, Sprint : 4
 * 
 * @author jp.boignard (AKKA Technologies)
 */
public class MongoDBDatasetTestCase extends AbstractSitoolsServerTestCase {

  /**
   * relative url for dataset management REST API
   * 
   * @return url
   */
  protected String getAttachUrl() {
    return SITOOLS_URL + SitoolsSettings.getInstance().getString(Consts.APP_DATASETS_URL);
  }

  /**
   * absolute url for dataset management REST API
   * 
   * @return url
   */
  protected String getBaseDatasetUrl() {
    return super.getBaseUrl() + SitoolsSettings.getInstance().getString(Consts.APP_DATASETS_URL);
  }

  /**
   * Absolute path location for data set store files
   * 
   * @return path     
   */
  protected String getTestRepository() {
    return super.getTestRepository() + SitoolsSettings.getInstance().getString(Consts.APP_DATASETS_STORE_DIR);
  }

  @Before
  @Override
  /**
   * Create component, store and application and start server
   * @throws java.lang.Exception
   */
  public void setUp() throws Exception {
    super.setUp();

    setMediaTest(MediaType.APPLICATION_JSON);
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
   * Test CRUD DataSet with JSon format exchanges.
   */
  @Test
  public void testCRUDJson() {
    assertNone();
    DataSet item = createObject("10000012345648");
    create(item);
    retrieve(item);
    item.setName("name_modified");
    item.setDescription("description_modified");
    update(item);
    startDs();
    item.setStatus("ACTIVE");
    getExpositionDs(item);
    getSql();
    refreshDs();
    checkProperties();
    stopDs();
    delete(item);
    assertNone();
    createWadl(getBaseDatasetUrl(), "datasets_admin");
  }

  /**
   * Invokes GET and asserts result response is an empty array.
   */
  public void assertNone() {
    ClientResource cr = new ClientResource(getBaseDatasetUrl());
    Representation result = cr.get(MediaType.APPLICATION_JSON);
    assertNotNull(result);
    assertTrue(cr.getStatus().isSuccess());

    Response response = GetResponseUtils.getResponseDataset(MediaType.APPLICATION_JSON, result, DataSet.class, true);
    assertTrue(response.getSuccess());
    assertEquals(11, response.getTotal().intValue());
    RIAPUtils.exhaust(result);
    cr.release();
  }

  /**
   * Invoke POST
   * 
   * @param item
   *          DataSet
   */
  public void create(DataSet item) {
    Representation rep = new JacksonRepresentation<DataSet>(item);
    ClientResource cr = new ClientResource(getBaseDatasetUrl());
    Representation result = cr.post(rep, MediaType.APPLICATION_JSON);
    assertTrue(cr.getStatus().isSuccess());
    assertNotNull(result);

    Response response = GetResponseUtils.getResponseDataset(MediaType.APPLICATION_JSON, result, DataSet.class);
    assertTrue(response.getSuccess());
    assertNotNull(response.getItem());

    DataSet rs = (DataSet) response.getItem();
    assertEqualsDataSet(rs, item);
    assertEquals("NEW", rs.getStatus());
    RIAPUtils.exhaust(result);
    cr.release();
  }

  /**
   * Create object
   * 
   * @param id
   *          new instance identifier
   * @return a DataSet instance for tests
   */
  public DataSet createObject(String id) {
    DataSet item = new DataSet();
    item.setId(id);
    item.setName("test_Name");
    item.setDescription("test_Description");

    item.setSitoolsAttachementForUsers("/ds/test");

    Structure a = new Structure();
    a.setName("Metadata");
    a.setType("collection");

    List<Structure> structures = new ArrayList<Structure>();
    structures.add(a);
    item.setStructures(structures);

    SitoolsStructure ss = new SitoolsStructure();
    Table t = new Table("Metadata");
    ss.setMainTable(t);
    item.setStructure(ss);

    // List<StructureNodeComplete> nodeList = new ArrayList<StructureNodeComplete>();

    Column c1 = new Column("_id", "_id", "_id", 100, true, true, "varchar");
    c1.setColumnAlias("_id");
    c1.setSpecificColumnType(SpecificColumnType.DATABASE);
    c1.setTableName("Metadata");
    c1.setPrimaryKey(true);

    item.addColumn(c1);

    Column c2 = new Column("className", "className", "className", 100, true, true, "varchar");
    c2.setColumnAlias("classname");
    c2.setSpecificColumnType(SpecificColumnType.DATABASE);
    c2.setTableName("Metadata");

    item.addColumn(c2);

    Column c3 = new Column("satVersion", "satVersion", "satVersion", 100, true, true, "int");
    c3.setColumnAlias("satversion");
    c3.setSpecificColumnType(SpecificColumnType.DATABASE);
    c3.setTableName("Metadata");

    item.addColumn(c3);

    // item.addColumn(new Column("3", "title", "title_header", 100, true, true,
    // "varchar"));

    item.setQueryType("W");
    Resource datasource = new Resource();
    datasource.setId("ac5c5a8b-0edc-4e5d-83ac-d8476a5e69cb");
    datasource.setType("datasource");
    datasource.setMediaType("datasource/mongodb");
    item.setDatasource(datasource);
    // item.setSqlQuery("SELECT fuse_prg_id.prop_id FROM fuse_prg_id");

    List<SitoolsProperty> properties = new ArrayList<SitoolsProperty>();
    properties.add(new SitoolsProperty("prop1", "prop1_value", null, SitoolsPropertyType.String));
    properties.add(new SitoolsProperty("prop2", "prop2_value", null, SitoolsPropertyType.String));
    properties.add(new SitoolsProperty("prop3", "prop3_value", null, SitoolsPropertyType.String));

    item.setProperties(properties);

    return item;
  }

  /**
   * Invoke GET
   * 
   * @param item
   *          DataSet
   */
  public void retrieve(DataSet item) {
    ClientResource cr = new ClientResource(getBaseDatasetUrl() + "/" + item.getId());
    Representation result = cr.get(MediaType.APPLICATION_JSON);
    assertTrue(cr.getStatus().isSuccess());
    assertNotNull(result);
    assertNotNull(result);
    assertTrue(cr.getStatus().isSuccess());

    Response response = GetResponseUtils.getResponseDataset(MediaType.APPLICATION_JSON, result, DataSet.class);
    assertTrue(response.getSuccess());
    assertNotNull(response.getItem());
    DataSet rs = (DataSet) response.getItem();
    assertEqualsDataSet(rs, item);
    RIAPUtils.exhaust(result);
    cr.release();
  }

  /**
   * Get the exposed dataset for users
   * 
   * @param item
   *          the dataset to get
   */
  public void getExpositionDs(DataSet item) {
    ClientResource cr = new ClientResource(getHostUrl() + "/ds/test");
    Representation result = cr.get(MediaType.APPLICATION_JSON);
    assertTrue(cr.getStatus().isSuccess());
    assertNotNull(result);
    assertTrue(cr.getStatus().isSuccess());

    Response response = GetResponseUtils.getResponseDataset(MediaType.APPLICATION_JSON, result,
        DataSetExpositionDTO.class);
    assertTrue(response.getSuccess());
    assertNotNull(response.getItem());
    DataSetExpositionDTO rs = (DataSetExpositionDTO) response.getItem();
    assertEqualsDataSetDTO(item, rs);
    RIAPUtils.exhaust(result);
    cr.release();
  }

  /**
   * Invoke PUT
   * 
   * @param item
   *          DataSet
   */
  public void update(DataSet item) {
    Representation rep = new JacksonRepresentation<DataSet>(item);
    ClientResource cr = new ClientResource(getBaseDatasetUrl() + "/" + item.getId());
    Representation result = cr.put(rep, MediaType.APPLICATION_JSON);
    assertTrue(cr.getStatus().isSuccess());
    assertNotNull(result);

    Response response = GetResponseUtils.getResponseDataset(MediaType.APPLICATION_JSON, result, DataSet.class);
    assertTrue(response.getSuccess());
    assertNotNull(response.getItem());
    DataSet rs = (DataSet) response.getItem();
    assertEqualsDataSet(rs, item);
    RIAPUtils.exhaust(result);
    cr.release();
  }

  /**
   * Invoke PUT
   * 
   */
  public void stopDs() {
    ClientResource cr = new ClientResource(getBaseDatasetUrl() + "/10000012345648/stop");
    Representation result = cr.put(null, MediaType.APPLICATION_JSON);
    assertTrue(cr.getStatus().isSuccess());
    assertNotNull(result);

    Response response = GetResponseUtils.getResponseDataset(MediaType.APPLICATION_JSON, result, DataSet.class);
    assertNotNull(response);
    assertTrue(response.getSuccess());
    assertNotNull(response.getItem());
    DataSet ds = (DataSet) response.getItem();
    assertEquals("INACTIVE", ds.getStatus());
    RIAPUtils.exhaust(result);
    cr.release();
  }

  /**
   * Invoke PUT
   */
  public void startDs() {
    ClientResource cr = new ClientResource(getBaseDatasetUrl() + "/10000012345648/start");
    Representation result = cr.put(null, MediaType.APPLICATION_JSON);
    assertTrue(cr.getStatus().isSuccess());
    assertNotNull(result);

    Response response = GetResponseUtils.getResponseDataset(MediaType.APPLICATION_JSON, result, DataSet.class);
    assertNotNull(response);
    assertTrue(response.getSuccess());
    assertNotNull(response.getItem());
    DataSet ds = (DataSet) response.getItem();
    assertEquals("ACTIVE", ds.getStatus());
    RIAPUtils.exhaust(result);
    cr.release();
  }

  /**
   * Invoke PUT
   */
  public void getSql() {
    ClientResource cr = new ClientResource(getBaseDatasetUrl() + "/10000012345648/getSqlString");
    Representation result = cr.put(null, MediaType.APPLICATION_JSON);
    assertTrue(cr.getStatus().isSuccess());
    assertNotNull(result);

    Response response = GetResponseUtils.getResponseDataset(MediaType.APPLICATION_JSON, result, DataSet.class);
    assertTrue(response.getSuccess());
    RIAPUtils.exhaust(result);
    cr.release();
  }

  /**
   * Invoke PUT
   */
  public void refreshDs() {
    ClientResource cr = new ClientResource(getBaseDatasetUrl() + "/10000012345648/refresh");
    Representation result = cr.put(null, MediaType.APPLICATION_JSON);
    assertTrue(cr.getStatus().isSuccess());
    assertNotNull(result);

    Response response = GetResponseUtils.getResponseDataset(MediaType.APPLICATION_JSON, result, DataSet.class);
    assertTrue(response.getSuccess());
    assertEquals("dataset.refresh.success", response.getMessage());
    RIAPUtils.exhaust(result);
    cr.release();
  }

  /**
   * Check the properties on the dataset
   */
  private void checkProperties() {
    // check properties ok
    checkProperties(true, "prop1", "prop1_value");
    // check properties !ok, incorrect value
    checkProperties(false, "prop1", "prop2_value");
    // check properties !ok, unknown value
    checkProperties(false, "prop4", "prop4_value");
  }

  /**
   * Check a particular property on a dataset. If ok == true, it expect a success, otherwise a failure
   * 
   * @param ok
   *          whether or not to expect for a successful result
   * @param propertyName
   *          the name of the property
   * @param propertyValues
   *          the values of the property
   */
  private void checkProperties(boolean ok, String propertyName, String propertyValues) {
    Reference ref = new Reference(getHostUrl() + "/ds/test/checkProperties");
    ref.addQueryParameter("k[0]", "TEXTFIELD|" + propertyName + "|" + propertyValues);
    ClientResource cr = new ClientResource(ref);
    Representation result = cr.get(MediaType.APPLICATION_JSON);
    assertTrue(cr.getStatus().isSuccess());
    assertNotNull(result);
    assertTrue(cr.getStatus().isSuccess());

    Response response = GetResponseUtils.getResponseDataset(MediaType.APPLICATION_JSON, result, Response.class);
    assertEquals(ok, response.getSuccess());
    RIAPUtils.exhaust(result);
    cr.release();
  }

  /**
   * Assert 2 datasets are identicals
   * 
   * @param item
   *          expected
   * @param rs
   *          new dataset
   */
  public void assertEqualsDataSet(DataSet item, DataSet rs) {
    assertEquals(rs.getName(), item.getName());
    assertEquals(rs.getDescription(), item.getDescription());
    assertEquals(rs.getId(), item.getId());

    assertEquals(rs.getSitoolsAttachementForUsers(), item.getSitoolsAttachementForUsers());

    // TODO assert all properties

    assertEquals(3, rs.getProperties().size());
  }

  /**
   * Assert a DataSetExpositionDTO has the same properties as a DataSet
   * 
   * @param item
   *          expected DataSet
   * @param rs
   *          new DataSetExpositionDTO
   */
  public void assertEqualsDataSetDTO(DataSet rs, DataSetExpositionDTO item) {
    assertEquals(rs.getName(), item.getName());
    assertEquals(rs.getId(), item.getId());
    assertEquals(rs.getSitoolsAttachementForUsers(), item.getSitoolsAttachementForUsers());
    assertEquals(rs.getStatus(), item.getStatus());
  }

  /**
   * Invoque DELETE
   * 
   * @param item
   *          DataSet
   */
  public void delete(DataSet item) {
    ClientResource cr = new ClientResource(getBaseDatasetUrl() + "/" + item.getId());
    Representation result = cr.delete(MediaType.APPLICATION_JSON);
    assertTrue(cr.getStatus().isSuccess());
    assertNotNull(result);
    assertTrue(cr.getStatus().isSuccess());

    Response response = GetResponseUtils.getResponseDataset(MediaType.APPLICATION_JSON, result, DataSet.class);
    assertTrue(response.getSuccess());
    RIAPUtils.exhaust(result);
    cr.release();
  }

}

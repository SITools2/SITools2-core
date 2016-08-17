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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.junit.Test;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Status;
import org.restlet.ext.xstream.XstreamRepresentation;
import org.restlet.representation.ObjectRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;

import fr.cnes.sitools.api.DocAPI;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.datasource.jdbc.model.JDBCDataSource;
import fr.cnes.sitools.server.Consts;
import fr.cnes.sitools.solr.model.DBConfigDTO;
import fr.cnes.sitools.solr.model.EntityDTO;
import fr.cnes.sitools.solr.model.FieldDTO;
import fr.cnes.sitools.solr.model.RssXSLTDTO;
import fr.cnes.sitools.solr.model.SchemaConfigDTO;
import fr.cnes.sitools.solr.model.SchemaFieldDTO;
import fr.cnes.sitools.solr.model.SolRConfigDTO;
import fr.cnes.sitools.util.RIAPUtils;

/**
 * TestCase Solr - Lucene
 * 
 * @author m.gond (AKKA Technologies)
 * 
 */
public class SolrTestCase extends AbstractSitoolsServerTestCase {

  static {
    setMediaTest(MediaType.APPLICATION_XML);
    docAPI = new DocAPI(SolrTestCase.class, "Solr Administration API with XML format");
    docAPI.setMediaTest(MediaType.APPLICATION_XML);
  }

  /**
   * Identifiant de la datasource declaree pour les tests. dans /data/datasources/int...
   */
  private String datasourceId = "8e00fe38-6f95-4338-a81c-ed2ab1db9340";
  /**
   * The index name
   */
  private String indexName = "indexName";
  /**
   * The index name
   */
  private String indexName2 = "indexName2";
  /**
   * SolrDirectory
   */
  private String solrDirectory = settings.getStoreDIR(Consts.APP_SOLR_STORE_DIR) + "/config";

  /*
   * (non-Javadoc)
   * 
   * @see fr.cnes.sitools.AbstractSitoolsServerTestCase#setUp()
   */
  @Override
  public void setUp() throws Exception {
    // TODO Auto-generated method stub
    super.setUp();
    /*
     * String filePath = super.TEST_FILES_REPOSITORY + "/solr/config/" + this.indexName; FileUtils.cleanDirectory(new
     * File(filePath));
     */
  }

  /**
   * Test CRUD Solr configuration with JSon format exchanges.
   * 
   * @throws InterruptedException
   */
  @Test
  public void testCRUD() throws InterruptedException {
    docAPI.setActive(false);
    String query = "fuse";
    SolRConfigDTO solrConf = getSolRConfig(indexName);
    create(solrConf);

    query(indexName, query);

    refresh(indexName);

    query(indexName, query);

    delete(indexName);

    // TODO clean and cancel
  }

  /**
   * Test CRUD DataSource with JSon format exchanges.
   */
  @Test
  public void testCRUD2docAPI() {
    docAPI.setActive(true);
    String query = "fuse";
    docAPI.appendChapter("Manipulating Solr indexes");

    docAPI.appendSubChapter("Create solr index", "create");
    SolRConfigDTO solrConf = getSolRConfig(indexName2);
    create(solrConf);

    docAPI.appendSubChapter("Query solr index", "query");
    query(indexName2, query);

    docAPI.appendSubChapter("Refresh solr index", "refresh");
    refresh(indexName2);
    docAPI.appendSubChapter("Delete solr index", "delete");
    delete(indexName2);
  }

  /**
   * Create configuration
   * 
   * @param solrConf
   *          SolRConfigDTO
   */
  private void create(SolRConfigDTO solrConf) {
    ObjectRepresentation<SolRConfigDTO> obj = new ObjectRepresentation<SolRConfigDTO>(solrConf);
    ClientResource cr = new ClientResource("riap://component/solr/create");
    docAPI.appendRequest(Method.POST, cr, obj);

    Representation result = cr.post(obj, getMediaTest());
    if (!docAPI.appendResponse(result)) {
      assertNotNull(result);
      assertSuccess(result);
    }
  }

  /**
   * Refresh configuration
   * 
   * @param indexName
   *          String
   */
  private void refresh(String indexName) {
    // TODO Auto-generated method stub
    ClientResource cr = new ClientResource("riap://component/solr/" + indexName + "/refresh");
    docAPI.appendRequest(Method.POST, cr);

    Representation result = cr.post(null, getMediaTest());
    if (!docAPI.appendResponse(result)) {
      assertNotNull(result);
      assertSuccess(result);
    }
  }

  /**
   * Delete configuration
   * 
   * @param indexName
   *          String
   */
  private void delete(String indexName) {
    ClientResource cr = new ClientResource("riap://component/solr/" + indexName + "/delete");
    docAPI.appendRequest(Method.POST, cr);

    Representation result = cr.post(null, getMediaTest());
    if (!docAPI.appendResponse(result)) {
      assertNotNull(result);
      assertSuccess(result);
    }
  }

  /**
   * Search request
   * 
   * @param indexName
   *          String
   * @param query
   *          String
   */
  private void query(String indexName, String query) {
    ClientResource cr = new ClientResource("riap://component/solr/" + indexName + "/execute?q=" + query);
    docAPI.appendRequest(Method.GET, cr);
    
    Representation result = cr.get(getMediaTest());
    if (!docAPI.appendResponse(result)) {
      assertNotNull(result);
      RIAPUtils.exhaust(result);
      // assertSuccess(result);
    }
  }

  /**
   * Clean the directory of the solr Index
   * 
   * @param indexName
   *          the indexName
   */
  private void clean(String indexName) {
    ClientResource cr = new ClientResource("riap://component/solr/" + indexName + "/clean");
    docAPI.appendRequest(Method.POST, cr);

    docAPI.appendRequest(Method.POST, cr);

    Representation result = cr.post(null, getMediaTest());
    if (!docAPI.appendResponse(result)) {
      assertNotNull(result);
      assertSuccess(result);

      assertDirDeleted(indexName);
    }
  }

  /**
   * Controls that the file has been removed on server
   * 
   * @param indexName
   *          file name
   */
  private void assertDirDeleted(String indexName) {
    File fileTest = new File(solrDirectory + "/" + indexName);
    assertFalse(fileTest.exists());
  }

  /**
   * Assert result representation is a Sitools response object
   * 
   * @param result
   *          <code>Representation</code>
   */
  private void assertSuccess(Representation result) {
    @SuppressWarnings("unchecked")
    XstreamRepresentation<Response> repr = (XstreamRepresentation<Response>) result;
    Response resp = repr.getObject();
    assertTrue(resp.getSuccess());
  }

  /**
   * Gets solr configuration of an index (dataset)
   * 
   * @param indexName
   *          String
   * @return <code>SolRConfigDTO</code>
   */
  private SolRConfigDTO getSolRConfig(String indexName) {
    SolRConfigDTO solrConf = new SolRConfigDTO();
    solrConf.setIndexName(indexName);
    // creation Dataconfig DTO
    DBConfigDTO dcDTO = getDBConfigDTO();

    solrConf.setDataConfigDTO(dcDTO);

    // Creation SchemaConfigDTO
    SchemaConfigDTO scDTO = new SchemaConfigDTO();
    scDTO.setDefaultSearchField("title");
    scDTO.setDocument("document");
    scDTO.setUniqueKey("prop_id");

    ArrayList<SchemaFieldDTO> fields = getSchemaFields();
    scDTO.setFields(fields);

    solrConf.setSchemaConfigDTO(scDTO);

    // creation Returned Field
    RssXSLTDTO returned = new RssXSLTDTO();

    returned.setDescription(fields.get(0).getName());
    returned.setGuid(fields.get(1).getName());
    returned.setLink(fields.get(2).getName());
    returned.setPubDate(fields.get(3).getName());
    returned.setTitle(fields.get(4).getName());

    solrConf.setRssXSLTDTO(returned);

    return solrConf;
  }

  /**
   * Creates schema fields description based on fuse_prg_id table attributes.
   * 
   * @return ArrayList<SchemaFieldDTO>
   */
  private ArrayList<SchemaFieldDTO> getSchemaFields() {
    // creation SchemaFieldDTO
    ArrayList<SchemaFieldDTO> fields = new ArrayList<SchemaFieldDTO>();

    SchemaFieldDTO field = new SchemaFieldDTO();
    field.setCompressed(true);
    field.setDefaultValue("prop_id");
    field.setIdCol("1235424354");
    field.setIndexed(true);
    field.setName("prop_id");
    field.setStored(true);
    field.setType("string");

    SchemaFieldDTO field2 = new SchemaFieldDTO();
    field2.setCompressed(true);
    field2.setDefaultValue("default");
    field2.setIdCol("1235424354fds");
    field2.setIndexed(true);
    field2.setName("cycle");
    field2.setStored(true);
    field2.setType("string");

    SchemaFieldDTO field3 = new SchemaFieldDTO();
    field3.setCompressed(true);
    field3.setDefaultValue("default");
    field3.setIdCol("1235424354");
    field3.setIndexed(true);
    field3.setName("title");
    field3.setStored(true);
    field3.setType("text");

    SchemaFieldDTO field4 = new SchemaFieldDTO();
    field4.setCompressed(true);
    field4.setDefaultValue("default");
    field4.setIdCol("1235424354");
    field4.setIndexed(true);
    field4.setName("institution");
    field4.setStored(true);
    field4.setType("text");

    SchemaFieldDTO field5 = new SchemaFieldDTO();
    field5.setCompressed(true);
    field5.setDefaultValue("default");
    field5.setIdCol("1235424354");
    field5.setIndexed(true);
    field5.setName("date");
    field5.setStored(true);
    field5.setType("rss_date");

    fields.add(field);
    fields.add(field2);
    fields.add(field3);
    fields.add(field4);
    fields.add(field5);

    return fields;
  }

  /**
   * Creates a new DataConfigDTO for test purpose on table fuse_prg_id.
   * 
   * @return DataConfigDTO
   */
  private DBConfigDTO getDBConfigDTO() {
    // creation Dataconfig DTO
    DBConfigDTO dcDTO = new DBConfigDTO();
    // creation JDBCDatasource
    JDBCDataSource ds = getJDBCDataSource(datasourceId);

    // creation EntityDTO
    ArrayList<EntityDTO> entities = new ArrayList<EntityDTO>();
    EntityDTO entity = new EntityDTO();
    entity.setName("fuse_prg_id");
    entity.setQuery("Select prop_id, cycle, title, institution, now() as date from fuse.fuse_prg_id");

    entities.add(entity);

    // creation Field
    ArrayList<FieldDTO> fields = new ArrayList<FieldDTO>();
    FieldDTO field1 = new FieldDTO();
    field1.setColumn("prop_id");
    field1.setName("prop_id");
    FieldDTO field2 = new FieldDTO();
    field2.setColumn("cycle");
    field2.setName("cycle");
    FieldDTO field3 = new FieldDTO();
    field3.setColumn("title");
    field3.setName("title");
    FieldDTO field4 = new FieldDTO();
    field4.setColumn("institution");
    field4.setName("institution");
    FieldDTO field5 = new FieldDTO();
    field5.setColumn("date");
    field5.setName("date");

    fields.add(field1);
    fields.add(field2);
    fields.add(field3);
    fields.add(field4);
    fields.add(field5);

    entity.setFields(fields);
    // set every attributes to the Dataconfig DTO
    dcDTO.setDatasource(ds);
    dcDTO.setEntities(entities);
    dcDTO.setDocument("document");

    return dcDTO;
  }

  /**
   * Get the JDBCDatasource model object using RIAP
   * 
   * @param id
   *          : the JDBCDatasource model object id
   * @return a JDBCDatasource model objet corresponding to the given id null if there is no JDBCDatasource object
   *         corresponding to the given id
   * 
   */
  private JDBCDataSource getJDBCDataSource(String id) {
    ClientResource cr = new ClientResource("riap://component" + settings.getString(Consts.APP_DATASOURCES_URL) + "/"
        + id);

    Representation result = cr.get(MediaType.APPLICATION_JAVA_OBJECT);

    @SuppressWarnings("unchecked")
    ObjectRepresentation<Response> or = (ObjectRepresentation<Response>) result;

    try {
      Response resp = or.getObject();
      // check if there is an object in the response, if not return null
      if (resp == null) {
        return null;
      }
      JDBCDataSource datasource = (JDBCDataSource) resp.getItem();
      return datasource;
    }
    catch (IOException e) { // marshalling error
      throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
    }
  }

}

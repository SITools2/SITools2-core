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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;

import org.junit.Ignore;
import org.junit.Test;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.ext.xstream.XstreamRepresentation;
import org.restlet.representation.ObjectRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;

import fr.cnes.sitools.api.DocAPI;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.server.Consts;
import fr.cnes.sitools.solr.directory.DirectoryConfigDTO;
import fr.cnes.sitools.solr.model.RssXSLTDTO;
import fr.cnes.sitools.solr.model.SchemaConfigDTO;
import fr.cnes.sitools.solr.model.SchemaFieldDTO;
import fr.cnes.sitools.solr.model.SolRConfigDTO;

/**
 * TestCase Solr - Lucene
 * 
 * @author m.gond (AKKA Technologies)
 * 
 */
@Ignore
public class SolrDirectoryTestCase extends AbstractSitoolsServerTestCase {

  static {
    setMediaTest(MediaType.APPLICATION_XML);
    docAPI = new DocAPI(SolrDirectoryTestCase.class, "Solr Administration API with XML format");
    docAPI.setMediaTest(MediaType.APPLICATION_XML);
  }

  /**
   * The index name
   */
  private String indexName = "directory-postel";
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
  }

  /**
   * Test CRUD Solr configuration with JSon format exchanges.
   */
  @Test
  public void testCRUD() {
    docAPI.setActive(false);
    String query = "postel";
    SolRConfigDTO solrConf = getSolRConfig(indexName);

    try {
      create(solrConf);

      query(indexName, query);

      refresh(indexName);

      query(indexName, query);
    }
    finally {
      delete(indexName);
    }

    // TODO clean and cancel
  }

  /**
   * Test CRUD DataSource with JSon format exchanges.
   */
  @Test
  public void testCRUD2docAPI() {
    docAPI.setActive(true);
    String query = "postel";
    docAPI.appendChapter("Manipulating Solr indexes");

    docAPI.appendSubChapter("Create solr index", "create");
    SolRConfigDTO solrConf = getSolRConfig(indexName);
    create(solrConf);

    docAPI.appendSubChapter("Query solr index", "query");
    query(indexName, query);

    docAPI.appendSubChapter("Refresh solr index", "refresh");
    refresh(indexName);
    docAPI.appendSubChapter("Delete solr index", "delete");
    delete(indexName);
  }

  /**
   * Create configuration
   * 
   * @param solrConf
   *          SolRConfigDTO
   */
  private void create(SolRConfigDTO solrConf) {
    ObjectRepresentation<SolRConfigDTO> obj = new ObjectRepresentation<SolRConfigDTO>(solrConf);
    ClientResource cr = new ClientResource("riap://component/solr/directory/create");
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
    ClientResource cr = new ClientResource("riap://component/solr/directory/" + indexName + "/refresh");
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
    ClientResource cr = new ClientResource("riap://component/solr/directory/" + indexName + "/delete");
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
    ClientResource cr = new ClientResource("riap://component/solr/directory/" + indexName + "/execute?q=" + query);
    docAPI.appendRequest(Method.GET, cr);

    Representation result = cr.get(getMediaTest());
    if (!docAPI.appendResponse(result)) {
      assertNotNull(result);
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
    ClientResource cr = new ClientResource("riap://component/solr/directory/" + indexName + "/clean");
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
    DirectoryConfigDTO dcDTO = getDirectoryConfigDTO();

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
  private DirectoryConfigDTO getDirectoryConfigDTO() {
    // creation Dataconfig DTO
    DirectoryConfigDTO dcDTO = new DirectoryConfigDTO();

    // dcDTO.setBaseDir( getTestRepository() );
    dcDTO.setBaseDir(settings.getRootDirectory() + settings.getStoreDIR() + "/test-solr-tika");

    dcDTO.setDocument("document");

    String fileName = ".*\\.(DOC)|(PDF)|(pdf)|(doc)|(docx)|(ppt)";
    dcDTO.setFileName(fileName);

    String newerThan = "'NOW-1095DAYS'";
    dcDTO.setNewerThan(newerThan);

    return dcDTO;
  }

}

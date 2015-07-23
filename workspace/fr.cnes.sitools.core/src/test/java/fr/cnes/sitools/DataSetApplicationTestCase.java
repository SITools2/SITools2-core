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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.restlet.Component;
import org.restlet.Context;
import org.restlet.data.MediaType;
import org.restlet.data.Protocol;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;

import fr.cnes.sitools.common.SitoolsSettings;
import fr.cnes.sitools.common.application.ContextAttributes;
import fr.cnes.sitools.common.model.Resource;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.dataset.DataSetAdministration;
import fr.cnes.sitools.dataset.DataSetStoreInterface;
import fr.cnes.sitools.dataset.DataSetStoreXMLMap;
import fr.cnes.sitools.dataset.model.DataSet;
import fr.cnes.sitools.common.Consts;
import fr.cnes.sitools.util.RIAPUtils;
import fr.cnes.sitools.utils.GetRepresentationUtils;
import fr.cnes.sitools.utils.GetResponseUtils;

/**
 * 
 * Test DataSetApplication Rest API
 * 
 * @since UserStory : ADM DataSets, Sprint : 4
 * 
 * @author jp.boignard (AKKA Technologies)
 */

public class DataSetApplicationTestCase extends AbstractSitoolsTestCase {
  /**
   * static xml store instance for the test
   */
  private static DataSetStoreInterface store = null;

  /**
   * dataSourceId for the test
   */
  private String dataSourceId = "8e00fe38-6f95-4338-a81c-ed2ab1db9340";

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
    return SITOOLS_URL + SitoolsSettings.getInstance().getString(Consts.APP_DATASETS_URL);
  }

  /**
   * absolute url for dataset management REST API
   * 
   * @return url
   */
  protected String getBaseUrl() {
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
        File storeDirectory = new File(getTestRepository() + "/map");
        storeDirectory.mkdirs();
        cleanDirectory(storeDirectory);
        cleanMapDirectories(storeDirectory);
        store = new DataSetStoreXMLMap(storeDirectory, ctx);
      }
      ctx.getAttributes().put(ContextAttributes.APP_STORE, store);

      Map<String, Object> stores = new ConcurrentHashMap<String, Object>();
      stores.put(Consts.APP_STORE_DATASET, store);

      SitoolsSettings.getInstance().setStores(stores);

      // Create dataset
      Resource res = new Resource();
      res.setId(dataSourceId);
      res.setType("datasource");
      res.setMediaType("datasource");

      DataSet dataset = new DataSet();
      dataset.setId("DATASET_FOR_TESTS");
      dataset.setName("DATASET_FOR_TESTS");
      dataset.setDescription("dataset for tests");
      // dataset.addColumn(new Column());

      dataset.setDatasource(res);
      dataset.setSitoolsAttachementForUsers("/sitools/DATASET_FOR_TESTS");
      store.create(dataset);

      this.component.getDefaultHost().attach(getAttachUrl(),
          new DataSetAdministration(this.component.getDefaultHost(), ctx));
    }

    if (!this.component.isStarted()) {
      this.component.start();
    }

  }

  /**
   * Invoke POST
   * 
   * @param item
   *          DataSet
   */
  public void create(DataSet item) {
    Representation rep = GetRepresentationUtils.getRepresentationDataset(item, MediaType.APPLICATION_JSON);
    ClientResource cr = new ClientResource(getBaseUrl());
    Representation result = cr.post(rep, MediaType.APPLICATION_JSON);
    assertTrue(cr.getStatus().isSuccess());
    assertNotNull(result);

    Response response = getResponse(MediaType.APPLICATION_JSON, result, DataSet.class);
    assertTrue(response.getSuccess());
    assertNotNull(response.getItem());

    DataSet rs = (DataSet) response.getItem();
    assertEquals(rs, item);
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
  public void assertEquals(DataSet item, DataSet rs) {
    assertSame(rs.getName(), item.getName());
    assertSame(rs.getDescription(), item.getDescription());
    assertSame(rs.getColumnModel(), item.getColumnModel());
    assertSame(rs.getDatasource(), item.getDatasource());
    assertSame(rs.getDirty(), item.getDirty());
    assertSame(rs, item);
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
   * Test DataSet Activation And Desactivation. SPRINT 5 : Activation d'un dataset engendre la création d'une
   * DatasetApplication les autres fonctionnalités Forms / OpenSearch rattachées
   */
  @Test
  public void testDataSetActivationAndDesactivation() {

  }

  /**
   * Test DataSet Exploration. TODO SPRINT 6
   */
  @Test
  public void testDataSetExploration() {

  }

  /**
   * Test DataSet Metadatas. TODO SPRINT 6
   */
  @Test
  public void testDataSetAdminQuerying() {
  }

  /**
   * Test DataSet Metadatas. TODO SPRINT 6
   * 
   */
  @Test
  public void testDataSetGridQuerying() {
    // TODO Sprint 6 fail("test not implemented");
  }

  /**
   * Test DataSet Metadatas. TODO Sprint 6
   */
  @Test
  public void testDataSetFormQuerying() {
    // TODO Sprint 6 fail("test not implemented");
  }

  /**
   * Test DataSet Metadatas. TODO Sprint 6
   */
  @Test
  public void testDataSetOpenSearchQuerying() {
    // TODO Sprint 6 fail("test not implemented");
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
    return GetResponseUtils.getResponseDataset(media, representation, dataClass, isArray);
  }
}

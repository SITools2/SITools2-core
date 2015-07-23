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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.restlet.data.MediaType;

import fr.cnes.sitools.api.DocAPI;
import fr.cnes.sitools.common.SitoolsSettings;
import fr.cnes.sitools.dataset.model.DataSet;
import fr.cnes.sitools.dataset.model.structure.TypeJointure;
import fr.cnes.sitools.json.GraphTestCase;
import fr.cnes.sitools.common.Consts;
import fr.cnes.sitools.utils.CreateDatasetUtil;

/**
 * 
 * Test DataSetApplication Rest API
 * 
 * @since UserStory : ADM DataSets, Sprint : 4
 * 
 * @author jp.boignard (AKKA Technologies)
 */
public class DatasetQueryingJointureTestCase extends AbstractDataSetManagerTestCase {

  /** Test title */
  protected static final String TITLE = "Dataset Filter API with JSON format";

  /** url attachment of the dataset with MySQL datasource with join */
  private static String urlAttachJoin = "/dataset/tests/joinTest";

  /**
   * Mysql Dataset Id
   */
  private String datasetId = "datasetJoinId";

  /**
   * absolute url for dataset management REST API
   * 
   * @return url
   */
  protected String getBaseUrlFilter() {
    return super.getBaseUrl() + SitoolsSettings.getInstance().getString(Consts.APP_DATASETS_URL) + "/%s"
        + SitoolsSettings.getInstance().getString(Consts.APP_DATASETS_FILTERS_URL);
  }

  static {
    setMediaTest(MediaType.APPLICATION_JSON);

    docAPI = new DocAPI(GraphTestCase.class, TITLE);
    docAPI.setActive(false);
    docAPI.setMediaTest(MediaType.APPLICATION_JSON);

  }

  @Before
  @Override
  /**
   * Create component, store and application and start server
   * @throws java.lang.Exception
   */
  public void setUp() throws Exception {
    super.setUp();
    docAPI.setActive(false);

  }

  @After
  @Override
  /**
   * Stop server
   * @throws java.lang.Exception
   */
  public void tearDown() throws Exception {
    super.tearDown();
    deleteDataset(datasetId);
  }

  /**
   * Create and activate a Dataset for Mysql datasource with join. This dataset is created on the HEADERS, IAPDATASET
   * and OBJECT_CLASS table
   * 
   * @param typeJointure
   *          the join
   * @param urlAttach
   *          the url Attachement
   * @return the DataSet created
   * @throws InterruptedException
   *           if something is wrong
   */
  private DataSet createDatasetHeadersJoinMySQL(TypeJointure typeJointure, String urlAttach)
      throws InterruptedException {
    DataSet item = CreateDatasetUtil.createDatasetHeadersJoinMySQL(datasetId, urlAttach, typeJointure);

    persistDataset(item);
    changeStatus(item.getId(), "/start");

    return item;
  }

  /**
   * Create and activate a Dataset for Pg datasource with join. This dataset is created on the HEADERS, IAPDATASET and
   * OBJECT_CLASS table
   * 
   * @param typeJointure
   *          the join
   * @param urlAttach
   *          the url Attachement
   * @return the DataSet created
   * @throws InterruptedException
   *           if something is wrong
   */
  private DataSet createDatasetHeadersJoinPG(TypeJointure typeJointure, String urlAttach) throws InterruptedException {
    DataSet item = CreateDatasetUtil.createDatasetHeadersJoinPG(datasetId, urlAttach, typeJointure);

    persistDataset(item);
    changeStatus(item.getId(), "/start");

    return item;
  }

  /**
   * Test DataSet on Inner Join
   * 
   * @throws InterruptedException
   */
  @Test
  public void testDataSetInnerJoin() throws InterruptedException {
    createDatasetHeadersJoinMySQL(TypeJointure.INNER_JOIN, urlAttachJoin);
    String params = "/records?start=0&limit=10000&media=json";

    queryDatasetRequestUrl(urlAttachJoin, params, 5046);

  }

  /**
   * Test DataSet on Join
   * 
   * @throws InterruptedException
   */
  @Test
  public void testDataSetLeftJoin() throws InterruptedException {
    createDatasetHeadersJoinMySQL(TypeJointure.LEFT_JOIN, urlAttachJoin);
    String params = "/records?start=0&limit=10000&media=json";

    queryDatasetRequestUrl(urlAttachJoin, params, 5062);

  }

  /**
   * Test DataSet on left Join
   * 
   * @throws InterruptedException
   */
  @Test
  public void testDataSetRightJoin() throws InterruptedException {
    createDatasetHeadersJoinMySQL(TypeJointure.RIGHT_JOIN, urlAttachJoin);
    String params = "/records?start=0&limit=10000&media=json";

    queryDatasetRequestUrl(urlAttachJoin, params, 5064);

  }

  /**
   * Test DataSet on left outer Join
   * 
   * @throws InterruptedException
   */
  @Test
  public void testDataSetleftOuterJoin() throws InterruptedException {
    createDatasetHeadersJoinMySQL(TypeJointure.LEFT_OUTER_JOIN, urlAttachJoin);
    String params = "/records?start=0&limit=10000&media=json";

    queryDatasetRequestUrl(urlAttachJoin, params, 5062);

  }

  /**
   * Test DataSet on right outer Join
   * 
   * @throws InterruptedException
   */
  @Test
  public void testDataSetrightOuterJoin() throws InterruptedException {
    createDatasetHeadersJoinMySQL(TypeJointure.RIGHT_OUTER_JOIN, urlAttachJoin);
    String params = "/records?start=0&limit=10000&media=json";

    queryDatasetRequestUrl(urlAttachJoin, params, 5064);

  }

  /**
   * Test DataSet on inner Join
   * 
   * @throws InterruptedException
   */
  @Test
  public void testDataSetInnerJoinPg() throws InterruptedException {
    createDatasetHeadersJoinPG(TypeJointure.INNER_JOIN, urlAttachJoin);
    String params = "/records?start=0&limit=10000&media=json";

    queryDatasetRequestUrl(urlAttachJoin, params, 4689);

  }

  /**
   * Test DataSet on right outer Join
   * 
   * @throws InterruptedException
   */
  @Test
  public void testDataSetRightOuterJoinPg() throws InterruptedException {
    createDatasetHeadersJoinPG(TypeJointure.RIGHT_OUTER_JOIN, urlAttachJoin);
    String params = "/records?start=0&limit=10000&media=json";

    queryDatasetRequestUrl(urlAttachJoin, params, 4707);

  }

  /**
   * Test DataSet on right Join
   * 
   * @throws InterruptedException
   */
  @Test
  public void testDataSetRightJoinPg() throws InterruptedException {
    createDatasetHeadersJoinPG(TypeJointure.RIGHT_JOIN, urlAttachJoin);
    String params = "/records?start=0&limit=10000&media=json";

    queryDatasetRequestUrl(urlAttachJoin, params, 4707);

  }

  /**
   * Test DataSet on left Join
   * 
   * @throws InterruptedException
   */
  @Test
  public void testDataSetLeftJoinPg() throws InterruptedException {
    createDatasetHeadersJoinPG(TypeJointure.LEFT_JOIN, urlAttachJoin);
    String params = "/records?start=0&limit=10000&media=json";

    queryDatasetRequestUrl(urlAttachJoin, params, 5062);

  }

  /**
   * Test DataSet on left outer Join
   * 
   * @throws InterruptedException
   */
  @Test
  public void testDataSetLeftOuterJoinPg() throws InterruptedException {
    createDatasetHeadersJoinPG(TypeJointure.LEFT_OUTER_JOIN, urlAttachJoin);
    String params = "/records?start=0&limit=10000&media=json";

    queryDatasetRequestUrl(urlAttachJoin, params, 5062);

  }

}

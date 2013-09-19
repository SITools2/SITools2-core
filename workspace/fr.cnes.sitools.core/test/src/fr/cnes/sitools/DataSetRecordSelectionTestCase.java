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
/**
 * 
 */
package fr.cnes.sitools;

import org.junit.Before;
import org.junit.Test;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.resource.ClientResource;

import fr.cnes.sitools.api.DocAPI;
import fr.cnes.sitools.common.exception.SitoolsException;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.dataset.model.DataSet;
import fr.cnes.sitools.util.RIAPUtils;
import fr.cnes.sitools.utils.CreateDatasetUtil;

/**
 * Test case for querying a range of records 
 * via RIAP and JAVA_OBJECT media type 
 * 
 * @author tx.chevallier
 * 
 * @project fr.cnes.sitools.core
 * @version 
 *
 */
public class DataSetRecordSelectionTestCase extends AbstractDataSetManagerTestCase {
  
  private String urlAttachDatasetHeaders = "/dataset/headers";
  
  private static DataSet datasetHeaders = null;
  
  static {
    setMediaTest(MediaType.APPLICATION_JSON);
    docAPI = new DocAPI(DataSetRecordSelectionTestCase.class, "record selection");
    docAPI.setMediaTest(MediaType.APPLICATION_JAVA_OBJECT);
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
  
  /**
   * getRecordsCount
   * @throws InterruptedException
   * @throws SitoolsException
   */
  @Test
  public void getRecordsCount() throws InterruptedException, SitoolsException {

    String id = "4889218643";

    setDatasetHeaders(createDatasetHeaders(id));

    String colurl = "dataset, targname";
    
    String params = "/records" + "?ranges=[[1,1000]]"  + "&colModel=\"" + colurl
        + "\"&start=0&limit=500";

    //String uri = getHostUrl() + urlAttachDatasetHeaders + params;
    String url = urlAttachDatasetHeaders + params;
    
    ClientResource cr = new ClientResource(url);
    
    Response response = RIAPUtils.handleParseResponse(url,
        Method.GET, MediaType.APPLICATION_JAVA_OBJECT, cr.getContext());

    int nbTotalResult = response.getTotal();
    
    if (nbTotalResult!=1000)
      throw new SitoolsException("wrong number of results");

    deleteDataset(id);

    
  }
  
  /**
   * createDatasetHeaders
   * @param id
   * @return
   * @throws InterruptedException
   */
  private DataSet createDatasetHeaders(String id) throws InterruptedException {
    
    DataSet item = CreateDatasetUtil.createDatasetHeadersSimplePG(id, urlAttachDatasetHeaders);
    persistDataset(item);
    changeStatus(item.getId(), "/start");
    
    return item;
  
  
  }

  public static DataSet getDatasetHeaders() {
    return datasetHeaders;
  }

  public static void setDatasetHeaders(DataSet datasetHeaders) {
    DataSetRecordSelectionTestCase.datasetHeaders = datasetHeaders;
  }

}

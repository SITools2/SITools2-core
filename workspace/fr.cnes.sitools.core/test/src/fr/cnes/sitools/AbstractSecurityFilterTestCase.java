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

import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Test;
import org.restlet.Client;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Form;
import org.restlet.data.Method;
import org.restlet.data.Protocol;
import org.restlet.data.Status;
import org.restlet.representation.Representation;

import fr.cnes.sitools.util.RIAPUtils;

/**
 * Tests the filter for security Try to access the record API with a Security Filter attached to it The Filter is
 * configured to block every access, so every request will fail.
 * 
 * This test first tests the Record API via the standard GET API Then it tests it using a SVA.
 * 
 * @author m.gond (AKKA Technologies)
 */
public abstract class AbstractSecurityFilterTestCase extends AbstractSitoolsServerTestCase {

  /** Dataset attach url */
  private static String datasetUrl = "/dataset/test/filter";

  /** The id of a record in the dataset */
  private static String recordId = "A001";

  /**
   * Test
   */
  @Test
  public void test() {
    docAPI.setActive(false);
    // query all the dataset, but only for a few records
    queryAllDataset(datasetUrl);
    // query the dataset for a particular record
    queryDataset(datasetUrl, recordId);

  }

  /**
   * Query the dataset for 10 records
   * 
   * @param dataseturl2
   *          the url of the dataset
   */
  private void queryAllDataset(String dataseturl2) {
    String url = getHostUrl() + datasetUrl + "/records?start=0&limit=10";

    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      retrieveDocAPI(url, "", parameters, url);
    }
    else {
      final Client client = new Client(Protocol.HTTP);
      Request request = new Request(Method.GET, url);
      Response response = null;
      try {
        response = client.handle(request);

        assertNotNull(response);
        assertTrue(response.getStatus().isError());
        assertEquals(Status.CLIENT_ERROR_BAD_REQUEST, response.getStatus());

      }
      finally {
        if (response != null) {
          RIAPUtils.exhaust(response);
        }
      }
    }
  }

  /**
   * Query the dataset for a particular record
   * 
   * @param dataseturl
   *          the datasetUrl
   * @param id
   *          the record id
   */
  private void queryDataset(String dataseturl, String id) {
    String url = getHostUrl() + datasetUrl + "/records/" + id;

    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      retrieveDocAPI(url, "", parameters, url);
    }
    else {
      final Client client = new Client(Protocol.HTTP);
      Request request = new Request(Method.GET, url);
      Response response = client.handle(request);
      try {
        assertNotNull(response);
        assertTrue(response.getStatus().isError());
        assertEquals(Status.CLIENT_ERROR_BAD_REQUEST, response.getStatus());

      }
      finally {
        RIAPUtils.exhaust(response);
      }
    }

  }

  /**
   * Query the dataset for a particular record
   * 
   * @param dataseturl
   *          the datasetUrl
   * @param svaId
   *          the sva id
   */
  private void queryDatasetUsingSVASync(String dataseturl, String svaId) {
    String url = getHostUrl() + datasetUrl + "/sva/" + svaId + "/tasks?run=sync";

    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      retrieveDocAPI(url, "", parameters, url);
    }
    else {
      final Client client = new Client(Protocol.HTTP);
      Request request = new Request(Method.POST, url);
      Response response = client.handle(request);
      try {
        assertNotNull(response);
        assertTrue(response.getStatus().isError());
        assertEquals(Status.CLIENT_ERROR_BAD_REQUEST, response.getStatus());
      }
      finally {
        RIAPUtils.exhaust(response);
      }
    }
  }

  /**
   * Query the dataset for a particular record
   * 
   * @param dataseturl
   *          the datasetUrl
   * @param svaId
   *          the sva id
   */
  private void queryDatasetUsingSVAAsync(String dataseturl, String svaId) {
    String url = getHostUrl() + datasetUrl + "/sva/" + svaId + "/tasks?run=async";

    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      retrieveDocAPI(url, "", parameters, url);
    }
    else {
      final Client client = new Client(Protocol.HTTP);
      Request request = new Request(Method.POST, url);
      Response response = client.handle(request);
      try {
        assertNotNull(response);
        assertTrue(response.getStatus().isError());
        assertEquals(Status.CLIENT_ERROR_BAD_REQUEST, response.getStatus());
      }
      finally {
        RIAPUtils.exhaust(response);
      }
    }
  }

  /**
   * Test a SVA with POST method and a url to a file of resources list.
   * 
   * @param dataseturl
   *          String
   * @param svaId
   *          String
   * @param urlFile
   *          String
   */
  private void queryDatasetUsingSVAPostFileList(String dataseturl, String svaId, String urlFile) {
    String url = getHostUrl() + datasetUrl + "/sva/" + svaId + "/tasks?run=async";
    Form form = new Form();
    form.set("urlFile", urlFile);
    Representation repr = form.getWebRepresentation();
    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      retrieveDocAPI(url, "", parameters, url);
    }
    else {
      final Client client = new Client(Protocol.HTTP);
      Request request = new Request(Method.POST, url, repr);
      Response response = client.handle(request);
      try {
        assertNotNull(response);
        assertTrue(response.getStatus().isError());
        assertEquals(Status.CLIENT_ERROR_BAD_REQUEST, response.getStatus());
      }
      finally {
        RIAPUtils.exhaust(response);
      }
    }
  }

}

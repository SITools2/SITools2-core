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

import org.junit.Test;
import org.restlet.Client;
import org.restlet.Request;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Protocol;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.engine.Engine;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;

import com.thoughtworks.xstream.XStream;

import fr.cnes.sitools.common.SitoolsXStreamRepresentation;
import fr.cnes.sitools.common.XStreamFactory;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.util.RIAPUtils;

public class AbstractDataSetPropertyTestCase extends SitoolsServerTestCase {

  public String datasetId = "6a5a7a2a-f4c9-4c97-add6-eb60bd0b1c69";

  public String urlAttach = "/fuse_ds";

  @Test
  public void testTextField() {
    // test with a real property and correct value => OK
    String parameters = "?k[0]=TEXTFIELD|galaxy|m31";
    checkProperties(true, parameters);
    // test with a real property and incorrect value => NOK
    parameters = "?k[0]=TEXTFIELD|galaxy|m32";
    checkProperties(false, parameters);
    // test with a fake property => NOK
    parameters = "?k[0]=TEXTFIELD|test|m31";
    checkProperties(false, parameters);
    // test with 2 real properties and correct values => OK
    parameters = "?k[0]=TEXTFIELD|satellite|fuse1&k[1]=TEXTFIELD|satellite|fuse2";
    checkProperties(true, parameters);
    // test with real property but missing value => NOK with Exception
    parameters = "?k[0]=TEXTFIELD|satellite|";
    checkPropertiesException(Status.CLIENT_ERROR_BAD_REQUEST, parameters);
  }

  @Test
  public void testNumericBetweenField() {
    // test with a real property and correct value => OK
    String parameters = "?k[0]=NUMERIC_BETWEEN|dec|50|150";
    checkProperties(true, parameters);
    // test with a real property and incorrect value => NOK
    parameters = "?k[0]=NUMERIC_BETWEEN|dec|150|300";
    checkProperties(false, parameters);
    // test with a fake property => NOK
    parameters = "?k[0]=NUMERIC_BETWEEN|test|300|400";
    checkProperties(false, parameters);
    // test with not numeric property => NOK with Exception
    parameters = "?k[0]=NUMERIC_BETWEEN|galaxy|300|400";
    checkPropertiesException(Status.SERVER_ERROR_INTERNAL, parameters);
    // test with numeric property but not numeric value => NOK with Exception
    parameters = "?k[0]=NUMERIC_BETWEEN|dec|fuse2|fuse3";
    checkPropertiesException(Status.CLIENT_ERROR_BAD_REQUEST, parameters);
    // test with 2 real properties and correct values => OK
    parameters = "?k[0]=NUMERIC_BETWEEN|dec|50|150&k[1]=NUMERIC_BETWEEN|dec|50|150";
    checkProperties(true, parameters);
    // test with real property but missing value => NOK with Exception
    parameters = "?k[0]=NUMERIC_BETWEEN|dec|";
    checkPropertiesException(Status.CLIENT_ERROR_BAD_REQUEST, parameters);
  }

  @Test
  public void testDateBetweenField() {
    // date value = 2012-04-03T10:13:00z
    // test with a real property and correct value => OK
    String parameters = "?k[0]=DATE_BETWEEN|date|2011-04-03T10:13:00.000|2013-04-03T10:13:00.000";
    checkProperties(true, parameters);
    // test with a real property and incorrect value => NOK
    parameters = "?k[0]=DATE_BETWEEN|date|2012-05-03T10:13:00.000|2013-04-03T10:13:00.000";
    checkProperties(false, parameters);
    // test with a fake property => NOK
    parameters = "?k[0]=DATE_BETWEEN|test|2011-04-03T10:13:00.000|2013-04-03T10:13:00.000";
    checkProperties(false, parameters);
    // test with not date property => NOK with Exception
    parameters = "?k[0]=DATE_BETWEEN|galaxy|2011-04-03T10:13:00.000|2013-04-03T10:13:00.000";
    checkPropertiesException(Status.SERVER_ERROR_INTERNAL, parameters);
    // test with date property but not date value => NOK with Exception
    parameters = "?k[0]=DATE_BETWEEN|date|fuse2|fuse3";
    checkPropertiesException(Status.CLIENT_ERROR_BAD_REQUEST, parameters);
    // test with 2 real properties and correct values => OK
    parameters = "?k[0]=DATE_BETWEEN|date|2011-04-03T10:13:00.000|2013-04-03T10:13:00.000&k[1]=DATE_BETWEEN|date|2011-04-03T10:13:00.000|2013-04-03T10:13:00.000";
    checkProperties(true, parameters);
    // test with real property but missing value => NOK with Exception
    parameters = "?k[0]=DATE_BETWEEN|date|";
    checkPropertiesException(Status.CLIENT_ERROR_BAD_REQUEST, parameters);
  }

  @Test
  public void testNumericField() {
    // test with a real property and correct value => OK
    String parameters = "?k[0]=NUMBER_FIELD|dec|100.1";
    checkProperties(true, parameters);
    // test with a real property and incorrect value => NOK
    parameters = "?k[0]=NUMBER_FIELD|dec|150";
    checkProperties(false, parameters);
    // test with a fake property => NOK
    parameters = "?k[0]=NUMBER_FIELD|test|300";
    checkProperties(false, parameters);
    // test with not numeric property => NOK with Exception
    parameters = "?k[0]=NUMBER_FIELD|galaxy|300";
    checkPropertiesException(Status.SERVER_ERROR_INTERNAL, parameters);
    // test with numeric property but not numeric value => NOK with Exception
    parameters = "?k[0]=NUMBER_FIELD|dec|fuse2";
    checkPropertiesException(Status.CLIENT_ERROR_BAD_REQUEST, parameters);
    // test with 2 real properties and correct values => OK
    parameters = "?k[0]=NUMBER_FIELD|dec|100.1&k[1]=NUMBER_FIELD|dec|100.1";
    checkProperties(true, parameters);
    // test with real property but missing value => NOK with Exception
    parameters = "?k[0]=NUMBER_FIELD|dec|";
    checkPropertiesException(Status.CLIENT_ERROR_BAD_REQUEST, parameters);

  }

  @Test
  public void testMultipleFields() {
    // test with a good properties and correct values => OK
    String parameters = "?k[0]=NUMBER_FIELD|dec|100.1";
    parameters += "&k[1]=DATE_BETWEEN|date|2011-04-03T10:13:00.000|2013-04-03T10:13:00.000";
    parameters += "&k[2]=NUMERIC_BETWEEN|dec|50|150";
    parameters += "&k[3]=TEXTFIELD|galaxy|m31";
    checkProperties(true, parameters);

    // test with a good properties and incorrect values => NOK
    parameters = "?k[0]=NUMBER_FIELD|dec|100.2";
    parameters += "&k[1]=DATE_BETWEEN|date|2011-04-03T10:13:00.000|2013-04-03T10:13:00.000";
    parameters += "&k[2]=NUMERIC_BETWEEN|dec|50|150";
    parameters += "&k[3]=TEXTFIELD|galaxy|m31";
    checkProperties(false, parameters);

  }

  /**
   * Check a particular property on a dataset. If ok == true, it expect a success, otherwise a failure
   * 
   * @param ok
   *          whether or not to expect for a successful result
   * @param params
   *          the parameters to add to the request
   */
  private void checkProperties(boolean ok, String params) {
    Reference ref = new Reference(getHostUrl() + urlAttach + "/checkProperties" + params);
    ClientResource cr = new ClientResource(ref);
    Representation result = cr.get(getMediaTest());
    assertTrue(cr.getStatus().isSuccess());
    assertNotNull(result);
    assertTrue(cr.getStatus().isSuccess());

    Response response = getResponse(getMediaTest(), result, Response.class);
    assertEquals(ok, response.getSuccess());
    RIAPUtils.exhaust(result);
    cr.release();
  }

  /**
   * Check a particular property on a dataset. Expect an error with a particular Status
   * 
   * @param expectedStatus
   *          The expected status
   * @param params
   *          the parameters to add to the request
   */
  private void checkPropertiesException(Status expectedStatus, String params) {
    Reference ref = new Reference(getHostUrl() + urlAttach + "/checkProperties" + params);
    final Client client = new Client(Protocol.HTTP);
    Request request = new Request(Method.GET, ref);
    org.restlet.Response response = client.handle(request);
    try {
      assertNotNull(response);
      assertTrue(response.getStatus().isError());
      assertEquals(expectedStatus, response.getStatus());
    }
    finally {
      RIAPUtils.exhaust(response);
    }
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

      if (isArray) {
        xstream.addImplicitCollection(Response.class, "data", dataClass);
      }
      else {
        xstream.alias("item", dataClass);
        xstream.alias("item", Object.class, dataClass);
      }
      xstream.aliasField("data", Response.class, "data");

      SitoolsXStreamRepresentation<Response> rep = new SitoolsXStreamRepresentation<Response>(representation);
      rep.setXstream(xstream);

      if (media.isCompatible(getMediaTest())) {
        Response response = rep.getObject("response");
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

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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.restlet.data.MediaType;
import org.restlet.engine.Engine;
import org.restlet.ext.jackson.JacksonRepresentation;
import org.restlet.ext.xstream.XstreamRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;

import com.thoughtworks.xstream.XStream;

import fr.cnes.sitools.common.SitoolsSettings;
import fr.cnes.sitools.common.SitoolsXStreamRepresentation;
import fr.cnes.sitools.common.XStreamFactory;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.server.Consts;
import fr.cnes.sitools.units.dimension.helper.DimensionHelper;
import fr.cnes.sitools.units.dimension.model.SitoolsDimension;
import fr.cnes.sitools.units.dimension.model.SitoolsUnit;
import fr.cnes.sitools.util.RIAPUtils;

/**
 * Base class for tests for dimension CRUD administration
 * 
 * @author m.marseille (AKKA technologies)
 */
public abstract class AbstractDimensionTestCase extends AbstractSitoolsServerTestCase {

  /** Helper name given for dimension creation */
  private transient String helperName;

  /** List of converter names */
  private List<String> converterNames;

  /** Dimension for use */
  private SitoolsDimension dimension;

  /**
   * relative url for dataset management REST API
   * 
   * @return url
   */
  protected String getAttachUrl() {
    return SITOOLS_URL + SitoolsSettings.getInstance().getString(Consts.APP_DIMENSIONS_ADMIN_URL);
  }

  /**
   * absolute url for dataset management REST API
   * 
   * @return url
   */
  protected String getBaseUrl() {
    return super.getBaseUrl() + SitoolsSettings.getInstance().getString(Consts.APP_DIMENSIONS_ADMIN_URL);
  }

  /**
   * absolute url for dataset management REST API
   * 
   * @return url
   */
  protected String getUnitBaseUrl() {
    return super.getBaseUrl() + SitoolsSettings.getInstance().getString(Consts.APP_UNITS_URL);
  }

  /**
   * Base test for dimension CRUD
   */
  @Test
  public void testDimensionCRUD() {
    getDimensionHelper();
    getDimensionHelper(this.helperName);
    createUnconsistentDimension();
    getUnconsistentDimension();
    updateToConsistentDimension();
    deleteDimension();
    createWadl(getBaseUrl(), "dimensions_admin");
    createWadl(getUnitBaseUrl(), "units_client");
  }

  /**
   * Retrieve the dimension helper
   */
  private void getDimensionHelper() {
    ClientResource cr = new ClientResource(getBaseUrl() + "/unithelpers");
    Representation result = cr.get(getMediaTest());

    assertNotNull(result);
    assertTrue(cr.getStatus().isSuccess());

    Response response = getResponse(getMediaTest(), result, DimensionHelper.class, true);

    assertNotNull(response);
    DimensionHelper helper = (DimensionHelper) response.getData().get(0);
    this.helperName = helper.getHelperName();
    this.converterNames = helper.getConverters();
    RIAPUtils.exhaust(result);
  }

  /**
   * 
   * Retrieve the dimension helper
   * 
   * @param dimensionHelper
   *          the dimension helper class name
   */
  private void getDimensionHelper(String dimensionHelper) {
    ClientResource cr = new ClientResource(getBaseUrl() + "/unithelpers/" + dimensionHelper);
    Representation result = cr.get(getMediaTest());

    assertNotNull(result);
    assertTrue(cr.getStatus().isSuccess());

    Response response = getResponse(getMediaTest(), result, DimensionHelper.class, false);
    assertNotNull(response);
    assertNotNull(response.getItem());
    DimensionHelper helper = (DimensionHelper) response.getItem();
    assertEquals(this.helperName, helper.getHelperName());
    RIAPUtils.exhaust(result);
  }

  /**
   * Create an initial dimension which is not consistent
   */
  private void createUnconsistentDimension() {
    // Object init
    this.dimension = new SitoolsDimension();
    dimension.setName("dimensionTest");
    dimension.setDescription("dimension for tests");
    dimension.setDimensionHelperName(helperName);
    dimension.setUnitConverters(converterNames);
    List<String> unitNames = new ArrayList<String>();
    unitNames.add("ly");
    unitNames.add("m");
    unitNames.add("kpc");
    unitNames.add("kg");
    List<SitoolsUnit> units = new ArrayList<SitoolsUnit>();
    // Conformance test of units using API
    for (String unitName : unitNames) {
      ClientResource cru = new ClientResource(getUnitBaseUrl() + "/" + unitName);
      Representation res = cru.get(getMediaTest());
      assertNotNull(res);
      Response respu = getResponse(getMediaTest(), res, SitoolsUnit.class);
      assertNotNull(respu);
      assertTrue(respu.getSuccess());
      SitoolsUnit unit = (SitoolsUnit) respu.getItem();
      units.add(unit);
      assertTrue(unit.getLabel().equals(unitName));
      RIAPUtils.exhaust(res);
    }
    dimension.setUnits(units);
    dimension.setConsistent(false);
    // Posting
    Representation repr = getRepresentation(dimension, getMediaTest());

    ClientResource cr = new ClientResource(getBaseUrl() + "/dimension");
    Representation result = cr.post(repr, getMediaTest());
    assertNotNull(result);
    assertTrue(cr.getStatus().isSuccess());
    Response response = getResponse(getMediaTest(), result, SitoolsDimension.class);
    assertNotNull(response);
    assertTrue(response.isSuccess());
    SitoolsDimension out = (SitoolsDimension) response.getItem();
    assertNotNull(out);
    assertTrue(dimension.getName().equals(out.getName()));
    assertTrue(dimension.getDescription().equals(out.getDescription()));
    assertTrue(dimension.getDimensionHelperName().equals(out.getDimensionHelperName()));
    for (int i = 0; i < dimension.getUnits().size(); i++) {
      assertEqualUnits(dimension.getUnits().get(i), out.getUnits().get(i));
    }
    for (int i = 0; i < dimension.getUnitConverters().size(); i++) {
      assertTrue(dimension.getUnitConverters().get(i).equals(out.getUnitConverters().get(i)));
    }
    dimension.setId(out.getId());
    assertTrue(!out.isConsistent()); // This dimension is not consistent
    RIAPUtils.exhaust(repr);
    RIAPUtils.exhaust(result);
  }

  /**
   * Method to test that two Sitools units are equivalent
   * 
   * @param sitoolsUnit
   *          first unit
   * @param sitoolsUnit2
   *          second unit
   */
  private static void assertEqualUnits(SitoolsUnit sitoolsUnit, SitoolsUnit sitoolsUnit2) {
    assertTrue(sitoolsUnit.getLabel().equals(sitoolsUnit2.getLabel()));
    assertTrue(sitoolsUnit.getUnitName().equals(sitoolsUnit2.getUnitName()));
  }

  /**
   * Create an initial dimension which is not consistent
   */
  private void getUnconsistentDimension() {
    // Getting all
    ClientResource cr = new ClientResource(getBaseUrl() + "/dimension");
    Representation result = cr.get(getMediaTest());
    assertNotNull(result);
    assertTrue(cr.getStatus().isSuccess());
    Response response = getResponse(getMediaTest(), result, SitoolsDimension.class, true);
    assertNotNull(response);
    assertTrue(response.isSuccess());
    // we retrieve 3 dimensions, the one we created in that test, and the other 2 already on the server at the beginning
    // of the test
    assertTrue(response.getTotal() == 3);
    SitoolsDimension out = null;
    for (int i = 0; i < response.getData().size(); i++) {
      SitoolsDimension sd = (SitoolsDimension) response.getData().get(i);
      if (sd.getName().equals(dimension.getName())) {
        out = sd;
      }
    }
    assertNotNull(out);
    assertTrue(dimension.getName().equals(out.getName()));
    assertTrue(dimension.getDescription().equals(out.getDescription()));
    assertTrue(dimension.getDimensionHelperName().equals(out.getDimensionHelperName()));
    for (int i = 0; i < dimension.getUnits().size(); i++) {
      assertEqualUnits(dimension.getUnits().get(i), out.getUnits().get(i));
    }
    for (int i = 0; i < dimension.getUnitConverters().size(); i++) {
      assertTrue(dimension.getUnitConverters().get(i).equals(out.getUnitConverters().get(i)));
    }
    assertTrue(!out.isConsistent()); // This dimension is not consistent
    RIAPUtils.exhaust(result);
    // Getting by ID
    cr = new ClientResource(getBaseUrl() + "/dimension/" + dimension.getId());
    result = cr.get(getMediaTest());
    assertNotNull(result);
    assertTrue(cr.getStatus().isSuccess());
    response = getResponse(getMediaTest(), result, SitoolsDimension.class);
    assertNotNull(response);
    assertTrue(response.isSuccess());
    out = (SitoolsDimension) response.getItem();
    assertNotNull(out);
    assertTrue(dimension.getName().equals(out.getName()));
    assertTrue(dimension.getDescription().equals(out.getDescription()));
    assertTrue(dimension.getDimensionHelperName().equals(out.getDimensionHelperName()));
    for (int i = 0; i < dimension.getUnits().size(); i++) {
      assertEqualUnits(dimension.getUnits().get(i), out.getUnits().get(i));
    }
    for (int i = 0; i < dimension.getUnitConverters().size(); i++) {
      assertTrue(dimension.getUnitConverters().get(i).equals(out.getUnitConverters().get(i)));
    }
    assertTrue(!out.isConsistent()); // This dimension is not consistent
    RIAPUtils.exhaust(result);
  }

  /**
   * Create an initial dimension which is not consistent
   */
  private void updateToConsistentDimension() {
    // Object init
    List<String> unitNames = new ArrayList<String>();
    unitNames.add("ly");
    unitNames.add("m");
    unitNames.add("pc");
    unitNames.add("Hz");
    List<SitoolsUnit> units = new ArrayList<SitoolsUnit>();
    // Conformance test of units using API
    for (String unitName : unitNames) {
      ClientResource cru = new ClientResource(getUnitBaseUrl() + "/" + unitName);
      Representation res = cru.get(getMediaTest());
      assertNotNull(res);
      Response respu = getResponse(getMediaTest(), res, SitoolsUnit.class);
      assertNotNull(respu);
      assertTrue(respu.getSuccess());
      SitoolsUnit unit = (SitoolsUnit) respu.getItem();
      units.add(unit);
      assertTrue(unit.getLabel().equals(unitName));
      RIAPUtils.exhaust(res);
    }
    dimension.setUnits(units);
    // Posting
    Representation repr = getRepresentation(dimension, getMediaTest());
    ClientResource cr = new ClientResource(getBaseUrl() + "/dimension/" + dimension.getId());
    Representation result = cr.put(repr, getMediaTest());
    assertNotNull(result);
    assertTrue(cr.getStatus().isSuccess());
    Response response = getResponse(getMediaTest(), result, SitoolsDimension.class);
    assertNotNull(response);
    assertTrue(response.isSuccess());
    SitoolsDimension out = (SitoolsDimension) response.getItem();
    assertNotNull(out);
    assertTrue(dimension.getName().equals(out.getName()));
    assertTrue(dimension.getDescription().equals(out.getDescription()));
    assertTrue(dimension.getDimensionHelperName().equals(out.getDimensionHelperName()));
    for (int i = 0; i < dimension.getUnits().size(); i++) {
      assertEqualUnits(dimension.getUnits().get(i), out.getUnits().get(i));
    }
    for (int i = 0; i < dimension.getUnitConverters().size(); i++) {
      assertTrue(dimension.getUnitConverters().get(i).equals(out.getUnitConverters().get(i)));
    }
    dimension.setId(out.getId());
    assertTrue(out.isConsistent()); // This dimension is consistent now !
    RIAPUtils.exhaust(repr);
    RIAPUtils.exhaust(result);
  }

  /**
   * Create an initial dimension which is not consistent
   */
  private void deleteDimension() {
    ClientResource cr = new ClientResource(getBaseUrl() + "/dimension/" + dimension.getId());
    Representation result = cr.delete(getMediaTest());
    assertNotNull(result);
    assertTrue(cr.getStatus().isSuccess());
    Response response = getResponse(getMediaTest(), result, SitoolsDimension.class);
    assertNotNull(response);
    assertTrue(response.isSuccess());
    RIAPUtils.exhaust(result);
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
   * REST API Response Representation wrapper for single or multiple items expected
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
      xstream.alias("dimension", SitoolsDimension.class);
      xstream.alias("dimensionHelper", DimensionHelper.class);
      xstream.alias("item", dataClass);
      xstream.alias("item", Object.class, dataClass);
      xstream.alias("unitConverter", String.class);
      xstream.alias("unit", SitoolsUnit.class);

      if (dataClass.equals(SitoolsDimension.class)) {
        xstream.aliasField("dimension", Response.class, "item");
        if (media.isCompatible(MediaType.APPLICATION_JSON)) {
          xstream.addImplicitCollection(SitoolsDimension.class, "unitConverters", String.class);
          xstream.addImplicitCollection(SitoolsDimension.class, "sitoolsUnits", SitoolsUnit.class);
        }
      }

      if (dataClass.equals(DimensionHelper.class)) {
        xstream.aliasField("dimensionHelper", Response.class, "item");
        if (media.isCompatible(MediaType.APPLICATION_JSON)) {
          xstream.addImplicitCollection(DimensionHelper.class, "converters", "converters", String.class);
          xstream.addImplicitCollection(DimensionHelper.class, "systems", "systems", String.class);
        }
      }

      if (dataClass.equals(SitoolsUnit.class)) {
        xstream.aliasField("unit", Response.class, "item");
      }

      xstream.omitField(DimensionHelper.class, "convs");
      xstream.omitField(DimensionHelper.class, "sys");

      xstream.omitField(SitoolsDimension.class, "isConsistent");

      if (isArray) {
        if (media.isCompatible(MediaType.APPLICATION_JSON)) {
          xstream.addImplicitCollection(Response.class, "data", dataClass);
        }

        if (dataClass.equals(DimensionHelper.class)) {
          xstream.alias("dimensionHelper", Object.class, DimensionHelper.class);
          if (media.isCompatible(MediaType.APPLICATION_JSON)) {
            xstream.addImplicitCollection(Response.class, "data", DimensionHelper.class);
          }
        }
        if (dataClass.equals(SitoolsDimension.class)) {
          xstream.alias("dimension", Object.class, SitoolsDimension.class);
        }

      }
      else {
        xstream.alias("item", dataClass);
        xstream.alias("item", Object.class, dataClass);

      }

      SitoolsXStreamRepresentation<Response> rep = new SitoolsXStreamRepresentation<Response>(representation);
      rep.setXstream(xstream);

      if (media.isCompatible(getMediaTest())) {
        Response response = rep.getObject("response");
        return response;
      }
      else {
        Engine.getLogger(AbstractSitoolsTestCase.class.getName()).warning("Only JSON or XML supported in tests");
        return null;
      }
    }
    finally {
      RIAPUtils.exhaust(representation);
    }
  }

  /**
   * Builds XML or JSON Representation of Dictionary for Create and Update methods.
   * 
   * @param item
   *          Dictionary
   * @param media
   *          APPLICATION_XML or APPLICATION_JSON
   * @return XML or JSON Representation
   */
  public static Representation getRepresentation(SitoolsDimension item, MediaType media) {
    if (media.equals(MediaType.APPLICATION_JSON)) {
      return new JacksonRepresentation<SitoolsDimension>(item);
    }
    else if (media.equals(MediaType.APPLICATION_XML)) {
      XStream xstream = XStreamFactory.getInstance().getXStream(media, false);
      XstreamRepresentation<SitoolsDimension> rep = new XstreamRepresentation<SitoolsDimension>(media, item);
      configure(xstream);
      rep.setXstream(xstream);
      return rep;
    }
    else {
      Engine.getLogger(AbstractSitoolsTestCase.class.getName()).warning("Only JSON or XML supported in tests");
      return null; // TODO complete test with ObjectRepresentation
    }
  }

  /**
   * Configures XStream mapping of Response object with Dictionary content.
   * 
   * @param xstream
   *          XStream
   */
  private static void configure(XStream xstream) {
    xstream.autodetectAnnotations(false);
    xstream.alias("response", Response.class);
    xstream.alias("dimension", SitoolsDimension.class);
    xstream.omitField(SitoolsDimension.class, "isConsistent");
  }

}

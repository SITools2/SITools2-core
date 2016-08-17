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
import static org.junit.Assert.fail;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Test;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;

import fr.cnes.sitools.common.SitoolsSettings;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.dataset.converter.dto.ConverterModelDTO;
import fr.cnes.sitools.dataset.converter.model.ConverterParameter;
import fr.cnes.sitools.dataset.converter.model.ConverterParameterType;
import fr.cnes.sitools.dataset.model.Column;
import fr.cnes.sitools.dataset.model.DataSet;
import fr.cnes.sitools.dataset.model.SpecificColumnType;
import fr.cnes.sitools.datasource.jdbc.model.AttributeValue;
import fr.cnes.sitools.datasource.jdbc.model.Record;
import fr.cnes.sitools.server.Consts;
import fr.cnes.sitools.util.RIAPUtils;
import fr.cnes.sitools.utils.CreateDatasetUtil;
import fr.cnes.sitools.utils.GetRepresentationUtils;
import fr.cnes.sitools.utils.GetResponseUtils;

public class AbstractDataSetConverterTestCase extends AbstractDataSetManagerTestCase {

  private String converterClassName = "fr.cnes.sitools.converter.basic.LinearConverter";

  private String datasetId = "10000123654";

  /**
   * relative url for dataset management REST API
   * 
   * @return url
   */
  protected String getBaseUrl() {
    return super.getBaseUrl() + SitoolsSettings.getInstance().getString(Consts.APP_DATASETS_URL);
  }

  /**
   * absolute url for dataset management REST API
   * 
   * @return url
   */
  protected String getBaseUrlConverter() {
    return getBaseUrl() + "/%s" + SitoolsSettings.getInstance().getString(Consts.APP_DATASETS_CONVERTERS_URL);
  }

  /**
   * Tests the primary keys access for postgresql dataset
   * 
   * @throws InterruptedException
   */
  @Test
  public void test() throws InterruptedException {
    docAPI.setActive(false);
    DataSet ds = CreateDatasetUtil.createDatasetFusePG(datasetId, "/fuse_for_converters");
    Column virtualColumn = new Column();
    virtualColumn.setId("virtual");
    virtualColumn.setHeader("virtual");
    virtualColumn.setSortable(true);
    virtualColumn.setVisible(true);
    virtualColumn.setPrimaryKey(false);
    virtualColumn.setSpecificColumnType(SpecificColumnType.VIRTUAL);
    virtualColumn.setColumnAlias("virtual");
    ds.addColumn(virtualColumn);
    this.persistDataset(ds);
    // add a converter
    addConverter();
    // start the dataset
    this.changeStatus(ds.getId(), "/start");

  }

  private void addConverter() {
    ConverterModelDTO conv = createConverterObject();
    create(conv);
  }

  /**
   * Add a converter to a Dataset
   * 
   * @param item
   *          ConverterModelDTO
   */
  private void create(ConverterModelDTO item) {
    Representation rep = GetRepresentationUtils.getRepresentationConverter(item, getMediaTest());
    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      parameters.put("identifier", "dataset identifier");
      parameters.put("POST", "A <i>ConverterChainedModelDTO</i> object");
      postDocAPI(String.format(getBaseUrlConverter(), datasetId), "", rep, parameters,
          String.format(getBaseUrl(), "%identifier%"));
    }
    else {
      ClientResource cr = new ClientResource(String.format(getBaseUrlConverter(), datasetId));
      Representation result = cr.post(rep, getMediaTest());
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());

      Response response = GetResponseUtils.getResponseConverter(getMediaTest(), result, ConverterModelDTO.class,
          getMediaTest());
      assertTrue(response.getSuccess());
      ConverterModelDTO conv = (ConverterModelDTO) response.getItem();
      assertNotNull(conv);

      RIAPUtils.exhaust(result);
    }

  }

  /**
   * Create a ConverterModelDTO object with the specified description and identifier
   * 
   * @return the created ConverterModelDTO
   */
  public ConverterModelDTO createConverterObject() {
    ConverterModelDTO conv = new ConverterModelDTO();

    conv.setName("LinearConverter");
    conv.setDescription("A converter applying a linear transformation");
    conv.setClassAuthor("AKKA Technologies");
    conv.setClassOwner("CNES");
    conv.setClassVersion("0.3");
    conv.setClassName(converterClassName);
    //
    ConverterParameter a = new ConverterParameter("a", "a in y = a.x+b",
        ConverterParameterType.CONVERTER_PARAMETER_INTERN);
    ConverterParameter b = new ConverterParameter("b", "b in y = a.x+b",
        ConverterParameterType.CONVERTER_PARAMETER_INTERN);
    ConverterParameter precision = new ConverterParameter("precision", "result precision (#0.00)",
        ConverterParameterType.CONVERTER_PARAMETER_INTERN);
    ConverterParameter x = new ConverterParameter("x", "x in y = a.x+b", ConverterParameterType.CONVERTER_PARAMETER_IN);
    ConverterParameter y = new ConverterParameter("y", "y in y = a.x+b", ConverterParameterType.CONVERTER_PARAMETER_OUT);

    //
    a.setValue("3.0");
    b.setValue("0.0");
    x.setAttachedColumn("cycle");
    y.setAttachedColumn("virtual");
    precision.setValue("#0.00");

    //

    conv.getParameters().add(precision);
    conv.getParameters().add(a);
    conv.getParameters().add(b);
    conv.getParameters().add(y);
    conv.getParameters().add(x);

    return conv;

  }

  /**
   * Query the dataset with the given attachment and id, an expected Record must be provided as well to check if the
   * queried one is ok
   * 
   * @param urlAttach
   *          the url of the dataset
   * @param id
   *          the id of the record
   * @param expectedRecord
   *          the record expected
   */
  protected void queryDatasetWithConverter(String urlAttach, String id) {
    try {
      id = URLEncoder.encode(id, "UTF-8");

      String url = getHostUrl() + urlAttach + "/records/" + id;
      if (docAPI.isActive()) {
        Map<String, String> parameters = new LinkedHashMap<String, String>();
        retrieveDocAPI(url, "", parameters, url);
      }
      else {
        ClientResource cr = new ClientResource(url);
        Representation result = null;
        try {
          result = cr.get(getMediaTest());

          assertNotNull(result);
          assertTrue(cr.getStatus().isSuccess());

          ArrayList<Record> records = getRecords(getMediaTest(), result);

          assertNotNull(records);
          for (Record record : records) {
            Double cycle = null;
            Double virtual = null;
            for (AttributeValue attrValue : record.getAttributeValues()) {
              if (attrValue.getName().equals("cycle")) {
                cycle = (Double) attrValue.getValue();
              }
              if (attrValue.getName().equals("virtual")) {
                virtual = (Double) attrValue.getValue();
              }
              if (cycle != null && virtual != null) {
                Double cycleConverted = cycle * 3;
                assertEquals(cycleConverted, virtual);
              }
              else {
                fail("Cycle or virtual is null");
              }
            }
          }
        }
        finally {
          RIAPUtils.exhaust(result);
        }
      }
    }
    catch (UnsupportedEncodingException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

  }
}

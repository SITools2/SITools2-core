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
package fr.cnes.sitools.resources.order.cart;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.restlet.Context;
import org.restlet.data.MediaType;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.ext.wadl.MethodInfo;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Get;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import fr.cnes.sitools.common.SitoolsSettings;
import fr.cnes.sitools.common.application.ContextAttributes;
import fr.cnes.sitools.common.resource.SitoolsParameterizedResource;
import fr.cnes.sitools.dataset.DataSetApplication;
import fr.cnes.sitools.dataset.database.DatabaseRequest;
import fr.cnes.sitools.dataset.database.DatabaseRequestFactory;
import fr.cnes.sitools.dataset.database.DatabaseRequestParameters;
import fr.cnes.sitools.dataset.database.common.DataSetExplorerUtil;
import fr.cnes.sitools.dataset.model.DataSet;
import fr.cnes.sitools.datasource.jdbc.model.AttributeValue;
import fr.cnes.sitools.datasource.jdbc.model.Record;
import fr.cnes.sitools.plugins.resources.model.ResourceParameter;
import fr.cnes.sitools.util.Util;

/**
 * Cart resource
 */
public class CartResource extends SitoolsParameterizedResource {

  @Override
  public void sitoolsDescribe() {
    setName("CartResourceFacade");
    setDescription("Export dataset records to cart");
  }

  @Override
  public void doInit() {
    super.doInit();
  }

  /**
   * Get representation
   */
  @Get
  public Representation get() {
    return execute();
  }

  @Override
  protected void describeGet(MethodInfo info) {
    this.addInfo(info);
    info.setIdentifier("retrieve records and format them as orders");
    info.setDocumentation("Method to retrieve records of a dataset and format them as orders");
    addStandardGetRequestInfo(info);
    DataSetExplorerUtil.addDatasetExplorerGetRequestInfo(info);
    DataSetApplication application = (DataSetApplication) getApplication();
    DataSetExplorerUtil.addDatasetExplorerGetFilterInfo(info, application.getFilterChained());
    addStandardResponseInfo(info);
    addStandardInternalServerErrorInfo(info);
  }

  @Override
  protected Representation head(Variant variant) {
    Representation repr = super.head();
    repr.setMediaType(MediaType.TEXT_HTML);
    return repr;
  }

  /**
   * Execute the request and return a Representation
   */
  private Representation execute() {

    // Get context
    Context context = getContext();

    // generate the DatabaseRequest
    DataSetApplication datasetApp = (DataSetApplication) getApplication();
    DataSetExplorerUtil dsExplorerUtil = new DataSetExplorerUtil(datasetApp, getRequest(), getContext());

    // Get request parameters
    if (datasetApp.getConverterChained() != null) {
      datasetApp.getConverterChained().getContext().getAttributes().put("REQUEST", getRequest());
    }

    // Get DatabaseRequestParameters
    DatabaseRequestParameters params = dsExplorerUtil.getDatabaseParams();

    ResourceParameter maxRowsParam = this.getModel().getParameterByName("max_rows");
    String maxRowsStr = maxRowsParam.getValue();

    DataSet ds = datasetApp.getDataSet();
    if (maxRowsStr == null || maxRowsStr.equals("-1")) {
      params.setPaginationExtend(ds.getNbRecords());
    }
    else {
      params.setPaginationExtend(Integer.valueOf(maxRowsStr));
    }

    DatabaseRequest databaseRequest = DatabaseRequestFactory.getDatabaseRequest(params);
    Record rec = null;

    SitoolsSettings settings = (SitoolsSettings) context.getAttributes().get(ContextAttributes.SETTINGS);

    // make directories
    Calendar cal = Calendar.getInstance();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
    String date = sdf.format(cal.getTime());

    String user = this.getRequest().getClientInfo().getUser().getIdentifier();
    String rootdir = settings.getStoreDIR("Starter.USERSTORAGE_ROOT") + "/" + user;

    File ordersdir = new File(rootdir + "/resources_orders");
    File outputdir = new File(rootdir + "/resources_orders/dataset_" + date);
    File datadir = new File(outputdir + "/data");

    ordersdir.mkdir();
    outputdir.mkdir();
    datadir.mkdir();

    try {
      if (params.getDistinct()) {
        databaseRequest.createDistinctRequest();
      }
      else {
        databaseRequest.createRequest();
      }

      DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
      DocumentBuilder builder = dbf.newDocumentBuilder();

      Document document = builder.newDocument();

      Element rootElement = document.createElement("root");
      Element recordsElement = document.createElement("records");
      rootElement.appendChild(recordsElement);
      document.appendChild(rootElement);

      while (databaseRequest.nextResult()) {

        rec = databaseRequest.getRecord();

        List<AttributeValue> list = rec.getAttributeValues();
        AttributeValue obj;

        // set record element
        Element recordElement = document.createElement("record");

        for (Iterator<AttributeValue> it = list.iterator(); it.hasNext();) {
          obj = it.next();
          if (Util.isSet(obj)) {

            String columnAlias = obj.getName();
            Element columnElement = document.createElement(columnAlias);
            columnElement.setTextContent(String.valueOf(obj.getValue()));
            recordElement.appendChild(columnElement);
          }
        }

        recordsElement.appendChild(recordElement);

      }

      // Writing out the DOM to an XML File
      TransformerFactory transformerFactory = TransformerFactory.newInstance();
      Transformer transformer = transformerFactory.newTransformer();

      DOMSource source = new DOMSource(document);
      StreamResult result = new StreamResult(new File(outputdir + "/metadata.xml"));
      transformer.transform(source, result);

    }
    catch (Exception e) {
      e.printStackTrace();
      return new JsonRepresentation("label.download_ko");
    }

    return new JsonRepresentation("label.download_ok");

  }

}

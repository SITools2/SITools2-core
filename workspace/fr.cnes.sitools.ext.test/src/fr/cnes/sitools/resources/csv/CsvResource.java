/*******************************************************************************
 * Copyright 2011, 2012 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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
package fr.cnes.sitools.resources.csv;

import org.restlet.Context;
import org.restlet.data.Disposition;
import org.restlet.data.MediaType;
import org.restlet.ext.wadl.MethodInfo;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Get;

import fr.cnes.sitools.common.resource.SitoolsParameterizedResource;
import fr.cnes.sitools.dataset.DataSetApplication;
import fr.cnes.sitools.dataset.database.DatabaseRequestParameters;
import fr.cnes.sitools.dataset.database.common.DataSetExplorerUtil;
import fr.cnes.sitools.dataset.model.DataSet;
import fr.cnes.sitools.plugins.resources.model.ResourceParameter;

/**
 * HTML resource
 * 
 * 
 * @author m.gond
 */
public class CsvResource extends SitoolsParameterizedResource {

  @Override
  public void sitoolsDescribe() {
    setName("CsvResource");
    setDescription("Export dataset records with CSV format");
  }

  @Override
  public void doInit() {
    super.doInit();
  }

  /**
   * Get HTML
   * 
   * @return Representation the HTML result
   */
  @Get
  public Representation getCsv() {
    return execute();
  }

  @Override
  protected void describeGet(MethodInfo info) {
    this.addInfo(info);
    info.setIdentifier("retrieve_records and format it as CSV");
    info.setDocumentation("Method to retrieve records of a dataset and format it as CSV");
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
    repr.setMediaType(MediaType.TEXT_CSV);
    return repr;
  }

  /**
   * Execute the request and return a Representation
   * 
   * @return the HTML representation
   */
  private Representation execute() {
    Representation repr = null;

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

    String limitStr = getQuery().getFirstValue("limit");
    int limit = params.getPaginationExtend();
    if (limitStr != null && !limitStr.isEmpty()) {
      limit = new Integer(limitStr).intValue();
    }

    repr = new CsvExportRepresentation(MediaType.TEXT_CSV, params, datasetApp.getConverterChained(), context,
        (limit == 0));

    if (fileName != null && !"".equals(fileName)) {
      Disposition disp = new Disposition(Disposition.TYPE_ATTACHMENT);

      disp.setFilename(fileName);
      repr.setDisposition(disp);
    }
    return repr;
  }

}

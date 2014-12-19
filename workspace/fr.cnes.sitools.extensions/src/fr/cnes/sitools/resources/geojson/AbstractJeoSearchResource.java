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
package fr.cnes.sitools.resources.geojson;

import java.util.List;
import java.util.logging.Level;

import org.restlet.Context;
import org.restlet.data.Form;
import org.restlet.data.Status;
import org.restlet.ext.wadl.MethodInfo;
import org.restlet.ext.wadl.ParameterInfo;
import org.restlet.ext.wadl.ParameterStyle;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;

import fr.cnes.sitools.common.resource.SitoolsParameterizedResource;
import fr.cnes.sitools.dataset.DataSetApplication;
import fr.cnes.sitools.dataset.converter.business.ConverterChained;
import fr.cnes.sitools.dataset.database.DatabaseRequestParameters;
import fr.cnes.sitools.dataset.database.common.DataSetExplorerUtil;
import fr.cnes.sitools.dataset.dto.DictionaryMappingDTO;
import fr.cnes.sitools.dataset.model.DataSet;
import fr.cnes.sitools.datasource.common.SitoolsDataSource;
import fr.cnes.sitools.plugins.resources.model.ResourceParameter;

/**
 * Abstract class for JeoSearch Resource
 * 
 * 
 * @author m.gond
 */
public abstract class AbstractJeoSearchResource extends SitoolsParameterizedResource {

  /**
   * default count param name
   */
  private String countParamName = "limit";
  /**
   * default startPage param name
   */
  private String startPageParamName = "pw";
  /**
   * default startIndex param name
   */
  private String startIndexParamName = "start";

  /**
   * default count param value
   */
  private int countDefaultValue = 100;
  /**
   * default startPage param value
   */
  private int startPageDefaultValue = 1;
  /**
   * default startIndex param value
   */
  private int startIndexDefaultValue = 0;

  /**
   * Handle the search
   * 
   * @return a {@link Representation} representing the search as GeoJSON
   */
  @Get
  public Representation search() {
    Representation repr = null;

    // generate the DatabaseRequest
    DataSetApplication datasetApp = (DataSetApplication) getApplication();
    DataSetExplorerUtil dsExplorerUtil = new DataSetExplorerUtil(datasetApp, getRequest(), getContext());

    // Get request parameters
    // DataSetApplication datasetApp = (DataSetApplication)
    // getContext().getAttributes().get("DataSetApplication");
    // DataSet ds = datasetApp.getDataSet();
    // this.setConverterChained(datasetApp.getConverterChained());
    if (datasetApp.getConverterChained() != null) {
      datasetApp.getConverterChained().getContext().getAttributes().put("REQUEST", getRequest());
    }

    DataSet dataset = datasetApp.getDataSet();

    // Get DatabaseRequestParameters
    DatabaseRequestParameters params = dsExplorerUtil.getDatabaseParams();

    // first check if the datasource is activated or not
    SitoolsDataSource datasource = params.getDb();
    if (datasource == null || !"ACTIVE".equals(datasource.getDsModel().getStatus())) {
      // Response response = new Response(false, "Datasource not activated");
      // return getRepresentation(response, media);

      getResponse().setStatus(Status.SERVER_ERROR_SERVICE_UNAVAILABLE, "Datasource not activated");
      return null;
    }

    Form queryParams = getRequest().getResourceRef().getQueryAsForm();

    String countStr = queryParams.getFirstValue(countParamName);
    String startPageStr = queryParams.getFirstValue(startPageParamName);
    String startIndexStr = queryParams.getFirstValue(startIndexParamName);
    // count
    int count = countDefaultValue;
    if (countStr != null && !"".equals(countStr)) {
      count = new Integer(countStr);
    }
    // startPage
    int startPage = startPageDefaultValue;
    if (startPageStr != null && !"".equals(startPageStr)) {
      startPage = new Integer(startPageStr);
    }
    // startIndex
    int startIndex = startIndexDefaultValue;
    if (startIndexStr != null && !"".equals(startIndexStr)) {
      startIndex = new Integer(startIndexStr);
    }

    int start = count * (startPage - 1) + startIndex;
    if (start < 0) {
      start = 0;
    }
    int limit = count;

    params.setStartIndex(start);
    params.setPaginationExtend(limit);

    String dicoName = this.getParameterValue(JeoSearchResourcePostGisModel.DICO_PARAM_NAME);
    if (dicoName != null) {
      DictionaryMappingDTO dico = datasetApp.getColumnConceptMappingDTO(dicoName);
      if (dico != null) {
        // gets the dictionaryMapping
        List<String> columnsAlias = dico.getListColumnAliasMapped(JeoSearchResourcePostGisModel.CONCEPT_NAME);
        if (columnsAlias.size() == 0) {
          getLogger().log(Level.INFO,
              dataset.getName() + " no column mapped for concept " + JeoSearchResourcePostGisModel.CONCEPT_NAME);
        }
        else if (columnsAlias.size() > 1) {
          getLogger().log(Level.INFO,
              dataset.getName() + " too many columns mapped for concept " + JeoSearchResourcePostGisModel.CONCEPT_NAME);
        }
        else {
          String geometryColumnName = columnsAlias.get(0);
          // SvaTask svaTask = (SvaTask) context.getAttributes().get("SvaTask");
          // getLogger().log(Level.INFO, "svaTask: {0}", svaTask.toString());
          ResourceParameter paramsParam = new ResourceParameter();
          paramsParam.setName("params");
          paramsParam.setValueObject(params);
          this.getOverrideParams().add(paramsParam);

          repr = getRepresentation(params, geometryColumnName, datasetApp.getConverterChained(), dataset, getContext());
        }
      }
    }
    return repr;
  }

  @Override
  public void describeGet(MethodInfo info) {
    info.setDocumentation("Method to Export some records in GeoJSON format");
    info.setIdentifier("GeoJSON");
    addStandardGetRequestInfo(info);

    ParameterInfo countParam = new ParameterInfo(countParamName, false, "xs:integer", ParameterStyle.QUERY,
        "Replaced with the number of search results per page desired by the search client. ");
    countParam.setDefaultValue(String.valueOf(countDefaultValue));
    info.getRequest().getParameters().add(countParam);

    ParameterInfo startIndexParam = new ParameterInfo(startIndexParamName, false, "xs:integer", ParameterStyle.QUERY,
        "Replaced with the index of the first search result desired by the search client. ");
    startIndexParam.setDefaultValue(String.valueOf(startIndexDefaultValue));
    info.getRequest().getParameters().add(startIndexParam);

    ParameterInfo startPageParam = new ParameterInfo(startPageParamName, false, "xs:integer", ParameterStyle.QUERY,
        "Replaced with the page number of the set of search results desired by the search client. ");
    startPageParam.setDefaultValue(String.valueOf(startPageDefaultValue));
    info.getRequest().getParameters().add(startPageParam);

    DataSetExplorerUtil.addDatasetExplorerGetRequestInfo(info);
    DataSetApplication application = (DataSetApplication) getApplication();
    DataSetExplorerUtil.addDatasetExplorerGetFilterInfo(info, application.getFilterChained());
    addStandardResponseInfo(info);
    addStandardInternalServerErrorInfo(info);
  }

  /**
   * Get a representation
   * 
   * @param params
   *          the DatabaseRequestParameters
   * @param geometryColName
   *          the name of the Geometry column
   * @param converterChained
   *          the list of converters
   * @param dataset
   *          the dataset
   * @param context
   *          the context
   * @return a representation
   */
  public abstract Representation getRepresentation(DatabaseRequestParameters params, String geometryColName,
      ConverterChained converterChained, DataSet dataset, Context context);

}

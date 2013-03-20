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
package fr.cnes.sitools.dataset.database.common;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.Context;
import org.restlet.Request;
import org.restlet.data.CharacterSet;
import org.restlet.data.Form;
import org.restlet.data.Method;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.ext.wadl.MethodInfo;
import org.restlet.ext.wadl.ParameterInfo;
import org.restlet.ext.wadl.ParameterStyle;
import org.restlet.representation.FileRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.security.User;

import fr.cnes.sitools.dataset.DataSetApplication;
import fr.cnes.sitools.dataset.database.DatabaseRequestParameters;
import fr.cnes.sitools.dataset.database.Range;
import fr.cnes.sitools.dataset.filter.business.AbstractFilter;
import fr.cnes.sitools.dataset.filter.business.FilterChained;
import fr.cnes.sitools.dataset.model.BehaviorEnum;
import fr.cnes.sitools.dataset.model.Column;
import fr.cnes.sitools.dataset.model.DataSet;
import fr.cnes.sitools.dataset.model.Multisort;
import fr.cnes.sitools.dataset.model.Operator;
import fr.cnes.sitools.dataset.model.Predicat;
import fr.cnes.sitools.dataset.model.Sort;
import fr.cnes.sitools.dataset.model.SpecificColumnType;
import fr.cnes.sitools.dataset.model.Wildcard;
import fr.cnes.sitools.datasource.common.SitoolsDataSource;
import fr.cnes.sitools.datasource.jdbc.model.Structure;
import fr.cnes.sitools.datasource.jdbc.model.Table;
import fr.cnes.sitools.tasks.TaskUtils;
import fr.cnes.sitools.util.RIAPUtils;

/**
 * DBExplorerResource using DataSource for pooled connections
 * 
 * @author AKKA Technologies <a
 *         href="https://sourceforge.net/tracker/?func=detail&atid=2158259&aid=3355053&group_id=531341">[#3355053]</a><br/>
 *         2011/07/06 d.arpin {Rename the method getColumnFromQuery -> getColumnFromDistinctQuery. Rename
 *         getColumnVisible -> getSQLColumnVisible. Add getAllColumnVisible} <br/>
 */
public class DataSetExplorerUtil {

  /** The QueryDistinct */
  private static final String QUERYDISTINCT = "query";

  /** The COLS */
  private static final String COLS = "colModel";

  /** The distinct */
  private static final String DISTINCT = "distinct";

  /** The TEMPLATE_PARAM */
  private static final String TEMPLATE_FORM = "p[#]";

  /** The ranges param */
  private static final String RANGES = "ranges";

  /** maximal number of records */
  private int maxrows = 0;

  /** The fetchSize */
  private int fetchSize = 0;

  /** The parent application */
  private volatile DataSetApplication application = null;

  /**
   * List of predicates
   */
  private volatile ArrayList<Predicat> arrayPredicats = null;

  /**
   * The pagination
   */
  private Form pagination = null;

  /**
   * The baseRef
   */
  private String baseRef;
  /** The request */
  private Request request = null;

  /** if resource is relative to a record */
  private String recordName;

  /** databseRequest parameters */
  private DatabaseRequestParameters databaseParams;
  /** The context */
  private Context context;

  /**
   * Constructor with minimal arguments to build a Database Request parameters
   * 
   * @param application
   *          the DataSetApplication
   * @param request
   *          the {@link Request}
   * @param context
   *          the {@link Context}
   */
  public DataSetExplorerUtil(DataSetApplication application, Request request, Context context) {
    // TODO Auto-generated constructor stub
    this.application = application;
    this.request = request;
    this.setContext(context);

    // target : database, table, record
    Map<String, Object> attributes = this.getRequest().getAttributes();

    this.recordName = (attributes.get("record") != null) ? Reference.decode((String) attributes.get("record"),
        CharacterSet.UTF_8) : null;

    // parameters : pagination, ...
    this.pagination = this.getRequest().getResourceRef().getQueryAsForm();

    // TODO baseRef / publicBaseRef
    // pas de / à la fin...
    if (this.getRequest().getResourceRef().getBaseRef().toString().endsWith("/")) {
      this.baseRef = this.getRequest().getResourceRef().getBaseRef().toString()
          .substring(1, this.getRequest().getResourceRef().getBaseRef().toString().length());
    }
    else {
      this.baseRef = this.getRequest().getResourceRef().getBaseRef().toString();
    }

    databaseParams = new DatabaseRequestParameters(this.getDataSource(), this.getPaginationStartRecord(),
        this.getDistinct(), this.getCountIsDone(), this.getDataSet(), this.getPredicats(), this.getStructures(),
        this.getOrderBy(), this.getColumnFromDistinctQuery(), this.getSQLColumnVisible(), this.getPaginationExtend(),
        this.getBaseRef(), this.getListOfIds(), this.getRanges(), this.getAllColumnVisible());

    int nbmaxrows = this.application.getSettings().getInt("AbstractDatabaseRequest.MAX_ROWS");
    databaseParams.setMaxrows(nbmaxrows);

  }

  /**
   * Gets the recordName value
   * 
   * @return the recordName
   */
  public String getRecordName() {
    return recordName;
  }

  /**
   * Sets the value of recordName
   * 
   * @param recordName
   *          the recordName to set
   */
  public void setRecordName(String recordName) {
    this.recordName = recordName;
  }

  /**
   * Gets the request value
   * 
   * @return the request
   */
  public Request getRequest() {
    return request;
  }

  /**
   * Sets the value of request
   * 
   * @param request
   *          the request to set
   */
  public void setRequest(Request request) {
    this.request = request;
  }

  /**
   * Gets the SQL DataSource
   * 
   * @return SitoolsDataSource
   */
  public final SitoolsDataSource getDataSource() {
    return this.application.getDataSource();
  }

  /**
   * Gets the tableName value
   * 
   * @param i
   *          index
   * @return the tableName
   */
  public final String getTableName(int i) {
    return application.getDataSet().getStructures().get(i).getName();
  }

  /**
   * Get the QueryDistinct
   * 
   * @return String Value of the filter for Distinct results
   */
  public final String getQueryDistinct() {
    String result = pagination.getFirstValue(QUERYDISTINCT);
    if (result != null && result != "") {
      return result;
    }
    return null;
  }

  /**
   * Get a Column of the specified ColumnALias
   * 
   * @return a column
   */
  public final Column getColumnFromDistinctQuery() {
    String colModel = pagination.getFirstValue(COLS);
    if (colModel != null && colModel != "") {
      if (colModel.split(",").length != 1) {
        return null;
      }
      Column column = application.getDataSet().findByColumnAlias(colModel);

      return column;
    }
    return null;
  }

  /**
   * Get the list of the SQL visible column
   * 
   * @return a list of column set to visible or Asked by the request
   */
  public final List<Column> getSQLColumnVisible() {
    if (getRecordName() != null && !"".equals(getRecordName())) {
      return getColumnFromDataset();
    }
    else {
      String colModel = pagination.getFirstValue(COLS);
      HashMap<String, Column> results = new HashMap<String, Column>();
      if (colModel != null && colModel != "") {
        List<String> columns = Arrays.asList((colModel.substring(1, colModel.length() - 1)).split(", "));
        List<Column> model = application.getDataSet().getColumnModel();
        for (Column column : model) {
          if (columns.contains(column.getColumnAlias())) {
            if (column.getSpecificColumnType() == SpecificColumnType.DATABASE
                || column.getSpecificColumnType() == SpecificColumnType.SQL) {
              results.put(column.getColumnAlias(), column);
            }
          }
        }
        // Add all requestable columns
        for (Column column : model) {
          if (column.isPrimaryKey()
              || (column.getColumnRenderer() != null && BehaviorEnum.noClientAccess.equals(column.getColumnRenderer()
                  .getBehavior()))) {
            if (column.getSpecificColumnType() == SpecificColumnType.DATABASE
                || column.getSpecificColumnType() == SpecificColumnType.SQL) {
              results.put(column.getColumnAlias(), column);
            }
          }
        }
        if (!results.isEmpty()) {
          return new ArrayList<Column>(results.values());
        }
      }
      return application.getDataSet().getDefaultColumnVisible();
    }
  }

  /**
   * Get the list of All visible column Warning : don't use this method to build SQL request, it will fail if there is
   * some virtual columns
   * 
   * @return a list of all columns set to visible or Asked by the request
   */
  public final List<Column> getAllColumnVisible() {
    if (getRecordName() != null && !"".equals(getRecordName())) {
      return getColumnFromDataset();
    }
    String colModel = pagination.getFirstValue(COLS);
    ArrayList<Column> results = new ArrayList<Column>();
    if (colModel != null && colModel != "") {
      List<String> columns = Arrays.asList((colModel.substring(1, colModel.length() - 1)).split(", "));
      List<Column> model = application.getDataSet().getColumnModel();
      for (Column column : model) {
        if (columns.contains(column.getColumnAlias())) {
          results.add(column);
        }
      }
      // Add all requestable columns
      for (Column column : model) {
        if (column.isPrimaryKey() || BehaviorEnum.noClientAccess.equals(column.getColumnRenderer())) {
          if (!results.contains(column)) {
            results.add(column);
          }
        }
      }
      if (!results.isEmpty()) {
        return results;
      }

    }
    return application.getDataSet().getDefaultColumnVisible();
  }

  /**
   * Get the list of column defined in the DataSet
   * 
   * @return The list of column defined in the DataSet
   */
  public final List<Column> getColumnFromDataset() {
    List<Column> model = application.getDataSet().getColumnModel();
    return model;
  }

  /**
   * Gets the schemaName value
   * 
   * @param i
   *          index
   * @return the schemaName
   */
  public final String getSchemaName(int i) {
    return application.getDataSet().getStructures().get(i).getSchemaName();
  }

  /**
   * Get the reference from table
   * 
   * @param i
   *          index
   * @return the reference
   */
  public final String getFromTableName(int i) {
    String reference = new Table(getTableName(i), getSchemaName(i)).getFROMReference();
    return reference;
  }

  /**
   * Gets the structure value
   * 
   * @return the structure
   */
  public final List<Structure> getStructures() {
    return application.getDataSet().getStructures();
  }

  /**
   * Get the base reference
   * 
   * @return the base reference
   */
  public final String getBaseRef() {
    return this.baseRef;
  }

  /**
   * Get the form with pagination
   * 
   * @return the form
   */
  public final Form getForm() {
    return this.pagination;
  }

  /**
   * Read startRecord request parameter -> integer - 0 by default
   * 
   * @return startRecord
   */
  public final int getPaginationStartRecord() {
    String start = this.pagination.getFirstValue("start", true);
    try {
      int startrecord = ((start != null) && !start.equals("")) ? Integer.parseInt(start) : 0;
      return (startrecord > 0) ? startrecord : 0;
    }
    catch (NumberFormatException e) {
      return 0;
    }
  }

  /**
   * Read extend request parameter -> integer - 0 by default
   * 
   * @return extend
   */
  public final int getPaginationExtend() {
    String nbHits = this.pagination.getFirstValue("limit", true);
    try {
      int extend = ((nbHits != null) && !nbHits.equals("")) ? Integer.parseInt(nbHits)
          : DatabaseRequestParameters.RETURN_FIRST_PAGE;

      if (extend == -1) {
        return DatabaseRequestParameters.RETURN_ALL;
      }
      else {
        return (extend >= 0) ? extend : DatabaseRequestParameters.RETURN_FIRST_PAGE;
      }
    }
    catch (NumberFormatException e) {
      return DatabaseRequestParameters.RETURN_FIRST_PAGE;
    }
  }

  /**
   * Read extend request parameter -> integer - 0 by default
   * 
   * @return extend
   */
  public final boolean getCountIsDone() {
    boolean countIsNotDone = Boolean.parseBoolean(this.pagination.getFirstValue("nocount", true));
    try {
      boolean isDone = !countIsNotDone;
      return isDone;
    }
    catch (NumberFormatException e) {
      return true;
    }
  }

  /**
   * Read extend request parameter -> integer - 0 by default
   * 
   * @return extend
   */
  public final Multisort getOrderBy() {
    Multisort orders = new Multisort();
    try {
      String sortParam = this.pagination.getFirstValue("sort", true);
      if ((sortParam == null) || sortParam.equals("")) {
        return orders;
      }

      ObjectMapper mapper = new ObjectMapper();
      orders = mapper.readValue(sortParam, Multisort.class);

      for (Sort sort : orders.getOrdersList()) {
        Column col = application.getDataSet().findByColumnAlias(sort.getField());
        if (col != null) {
          sort.setField(col.getDataIndex());
        }
      }
      return orders;
    }
    catch (IOException e) {
      // e.printStackTrace();
      String sortParam = this.pagination.getFirstValue("sort", true);
      if ((sortParam != null) && !sortParam.equals("")) {
        Column col = application.getDataSet().findByColumnAlias(sortParam);
        if (col != null) {
          Sort sort = new Sort(col.getDataIndex(), getSortDirection());
          Sort[] sorts = new Sort[1];
          sorts[0] = sort;
          orders.setOrdersList(sorts);
          return orders;
        }
      }
    }
    return orders;

  }

  /**
   * Get the sort direction from the request
   * 
   * @return the sort direction
   */
  private String getSortDirection() {
    String dir = this.pagination.getFirstValue("dir", true);
    String result = "";
    if (dir != null && !dir.equals("") && (dir.contentEquals("ASC") || dir.contentEquals("DESC"))) {
      result = dir.toUpperCase();
    }
    return result;
  }

  /**
   * Convert the request parameters to the list of predicates
   * 
   * @return the list of predicates
   */
  public final ArrayList<Predicat> getPredicats() {

    if (this.arrayPredicats != null) {
      return arrayPredicats;
    }

    ArrayList<Predicat> predicats = new ArrayList<Predicat>();
    // if the dataset query type is WIZARD, we add the dataset's predicates
    if ("W".equals(application.getDataSet().getQueryType())) {
      predicats.addAll(application.getDataSet().getPredicat());
    }

    // add a Predicat for queryDistinct
    if (this.pagination.getFirstValue("query") != null) {
      Predicat predicat = new Predicat();
      Column columnQueried = getColumnFromDistinctQuery();

      predicat.setLeftAttribute(columnQueried);
      predicat.setRightValue("'" + this.pagination.getFirstValue("query") + "'");
      predicat.setWildcard(Wildcard.START_WITH);
      predicat.setCompareOperator(Operator.LIKE);

      predicats.add(predicat);
    }

    // predicats.addAll(getFormPredicats());

    predicats.addAll(getArrayPredicatsPostFilters());

    arrayPredicats = predicats;

    return predicats;
  }

  /**
   * Get the model of columns
   * 
   * @return a list of columns
   */
  public final List<Column> getColumnModel() {
    return application.getDataSet().getColumnModel();
  }

  // /**
  // * convert a operator to a SQL operator
  // *
  // * @param compareOperator
  // * the operator
  // * @return the SQL operator
  // */
  // public final String convertOperator(String compareOperator) {
  // for (Operator operator : Operator.values()) {
  // if (operator.name().equalsIgnoreCase(compareOperator)) {
  // return operator.value();
  // }
  // }
  // return null;
  // }

  /**
   * Gets the arrayPredicatsPostFilters value
   * 
   * @return the arrayPredicatsPostFilters
   */
  public final List<Predicat> getArrayPredicatsPostFilters() {
    List<Predicat> predicatsResult = null;
    try {
      if (application.getFilterChained() != null) {
        // Formulaire p[0]
        predicatsResult = application.getFilterChained().createPredicats(getRequest(), null);

      }
    }
    catch (ResourceException e) {
      application.getLogger().warning(
          "Exception when applying filterChained on dataset " + application.getDataSet().getName());
      throw e;
    }
    catch (Exception e) {
      application.getLogger().warning(
          "Exception when applying filterChained on dataset " + application.getDataSet().getName());
      throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
    }
    if (predicatsResult == null) {
      predicatsResult = new ArrayList<Predicat>();
    }
    return predicatsResult;
  }

  /**
   * Get distinct
   * 
   * @return true if distinct
   */
  public final Boolean getDistinct() {
    Boolean distinct = Boolean.parseBoolean(pagination.getFirstValue(DISTINCT));
    if (distinct != null) {
      return distinct;
    }
    return false;
  }

  /**
   * Get the DataSet associated to that resource
   * 
   * @return the DataSet associated to that resource
   * 
   */
  public final DataSet getDataSet() {
    return this.application.getDataSet();
  }

  /**
   * Gets the databaseParams value
   * 
   * @return the databaseParams
   */
  public final DatabaseRequestParameters getDatabaseParams() {
    return databaseParams;
  }

  /**
   * Sets the value of databaseParams
   * 
   * @param databaseParams
   *          the databaseParams to set
   */
  public final void setDatabaseParams(DatabaseRequestParameters databaseParams) {
    this.databaseParams = databaseParams;
  }

  /**
   * Get the list of identifiers
   * 
   * @return the list of identifiers
   */
  private String[] getListOfIds() {
    Form bodyForm = null;
    // Try to get the Form from the context, if not, get it from the body
    if (getContext() != null && getContext().getAttributes().get(TaskUtils.BODY_CONTENT) != null) {
      bodyForm = (Form) getContext().getAttributes().get(TaskUtils.BODY_CONTENT);
    }
    else {
      Representation body = this.getRequest().getEntity();
      if (body != null && body.isAvailable() && body.getSize() > 0) {
        bodyForm = new Form(body);
      }
    }

    if (bodyForm != null) {
      String urlFile = bodyForm.getFirstValue("urlFile");
      if (urlFile != null && urlFile != "") {
        Representation repFile = this.getFile(urlFile, this.getRequest().getClientInfo().getUser());

        if (repFile != null) {
          repFile = (FileRepresentation) repFile;
          JSONObject json;

          try {
            String text = repFile.getText();
            json = new JSONObject(text);

            // look for the primary key column
            Column primaryKey = null;
            List<Column> columns = this.getColumnModel();
            for (Column column : columns) {
              if (column.isPrimaryKey()) {
                primaryKey = column;
                break;
              }
            }

            // create the list of ids from the JSON object
            JSONObject orderRecord = json.getJSONObject("orderRecord");
            JSONArray records = orderRecord.getJSONArray("records");
            String[] idList = new String[records.length()];
            for (int i = 0; i < records.length(); i++) {
              idList[i] = records.getJSONObject(i).getString(primaryKey.getColumnAlias());
            }
            return idList;
          }
          catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
          }
          catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
          }
        }
      }
    }
    return null;
  }

  /**
   * Gets the representation of a File
   * 
   * @param fileUrl
   *          the url of the file
   * @param user
   *          the user
   * @return the Representation of a File
   * 
   */
  public final Representation getFile(String fileUrl, User user) {

    Request reqGET = new Request(Method.GET, RIAPUtils.getRiapBase() + fileUrl);
    reqGET.setClientInfo(getRequest().getClientInfo());

    org.restlet.Response r = application.getContext().getClientDispatcher().handle(reqGET);

    if (r == null) {
      throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
    }
    else if (Status.CLIENT_ERROR_FORBIDDEN.equals(r.getStatus())) {
      throw new ResourceException(Status.CLIENT_ERROR_FORBIDDEN);
    }
    else if (Status.CLIENT_ERROR_UNAUTHORIZED.equals(r.getStatus())) {
      throw new ResourceException(Status.CLIENT_ERROR_UNAUTHORIZED);
    }
    else if (Status.isError(r.getStatus().getCode())) {
      throw new ResourceException(r.getStatus());
    }

    return r.getEntity();

  }

  /**
   * Get the list of ranges in the request, or null if no ranges was defined
   * 
   * @return the list of ranges or null if no ranges was defined
   */
  private List<Range> getRanges() {
    List<Range> listRanges = null;
    String ranges = getRequest().getResourceRef().getQueryAsForm().getFirstValue(RANGES);
    if (ranges != null && !"".equals(ranges)) {
      listRanges = new ArrayList<Range>();
      try {
        JSONArray array = new JSONArray(ranges);
        for (int i = 0; i < array.length(); i++) {
          JSONArray rangeArray = array.getJSONArray(i);
          if (rangeArray.length() == 2) {
            Range range = new Range(rangeArray.getInt(0), rangeArray.getInt(1));
            listRanges.add(range);
          }
        }
      }
      catch (JSONException e) {
        application.getLogger().warning("Wrong range parameter");
        throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, e);
      }
      // Let's sort the list to have the ranges in the right order
      Collections.sort(listRanges);
    }
    return listRanges;
  }

  /**
   * Add the DatasetExplorer info to the MethodInfo
   * 
   * @param info
   *          the {@link MethodInfo}
   */
  public static void addDatasetExplorerGetRequestInfo(MethodInfo info) {
    ParameterInfo startInfo = new ParameterInfo("start", false, "xs:int", ParameterStyle.QUERY,
        "Starting index for SQL request.");
    ParameterInfo limitInfo = new ParameterInfo("limit", false, "xs:int", ParameterStyle.QUERY,
        "Maximal number of records returned.");
    ParameterInfo queryInfo = new ParameterInfo("query", false, "xs:string", ParameterStyle.QUERY,
        "Request sent by filters and forms.");
    ParameterInfo colModelInfo = new ParameterInfo("colModel", false, "xs:string", ParameterStyle.QUERY,
        "Column model description used for getting back records.");
    ParameterInfo sortInfo = new ParameterInfo("sort", false, "xs:string", ParameterStyle.QUERY,
        "Column name to sort by.");
    ParameterInfo dirInfo = new ParameterInfo("dir", false, "xs:string", ParameterStyle.QUERY,
        "(ASC or DESC) Sorting order.");
    ParameterInfo distinctInfo = new ParameterInfo("distinct", false, "xs:boolean", ParameterStyle.QUERY,
        "If true then creates a DISTINCT SQL request.");
    ParameterInfo rangesInfo = new ParameterInfo("ranges", false, "xs:string", ParameterStyle.QUERY,
        "Array of index ranges to select (example : [[1,10],[20,50]])");

    info.getRequest().getParameters().add(colModelInfo);
    info.getRequest().getParameters().add(startInfo);
    info.getRequest().getParameters().add(limitInfo);
    info.getRequest().getParameters().add(queryInfo);
    info.getRequest().getParameters().add(sortInfo);
    info.getRequest().getParameters().add(dirInfo);
    info.getRequest().getParameters().add(distinctInfo);
    info.getRequest().getParameters().add(rangesInfo);

  }

  /**
   * Add the Dataset Filter info to the {@link MethodInfo}
   * 
   * @param info
   *          the {@link MethodInfo}
   * @param filterChained
   *          the {@link FilterChained}
   */
  public static void addDatasetExplorerGetFilterInfo(MethodInfo info, FilterChained filterChained) {

    List<AbstractFilter> filters = new ArrayList<AbstractFilter>();
    if (filterChained != null) {
      filters = filterChained.getFilters();
    }

    for (AbstractFilter abstractFilter : filters) {
      HashMap<String, ParameterInfo> rpd = abstractFilter.getRequestParamsDescription();
      if (rpd != null) {
        for (Entry<String, ParameterInfo> paramDescription : rpd.entrySet()) {
          ParameterInfo paramInfo = paramDescription.getValue();
          info.getRequest().getParameters().add(paramInfo);
        }
      }

    }
  }

  /**
   * Gets the application value
   * 
   * @return the application
   */
  public final DataSetApplication getApplication() {
    return application;
  }

  /**
   * Sets the value of application
   * 
   * @param application
   *          the application to set
   */
  public void setApplication(DataSetApplication application) {
    this.application = application;
  }

  /**
   * Set the base reference of the resource
   * 
   * @param bref
   *          the new base reference
   */
  public final void setBaseRef(String bref) {
    this.baseRef = bref;
  }

  /**
   * Gets the fetchSize value
   * 
   * @return the fetchSize
   */
  public final int getFetchSize() {
    return fetchSize;
  }

  /**
   * Gets the maxrows value
   * 
   * @return the maxrows
   */
  public final int getMaxrows() {
    return maxrows;
  }

  /**
   * Gets the templateForm value
   * 
   * @return the templateForm
   */
  public static String getTemplateForm() {
    return TEMPLATE_FORM;
  }

  /**
   * Sets the value of context
   * 
   * @param context
   *          the context to set
   */
  public void setContext(Context context) {
    this.context = context;
  }

  /**
   * Gets the context value
   * 
   * @return the context
   */
  public Context getContext() {
    return context;
  }

}

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

import java.util.Map;

import org.restlet.data.CharacterSet;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.ext.wadl.MethodInfo;
import org.restlet.ext.xstream.XstreamRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;

import com.thoughtworks.xstream.XStream;

import fr.cnes.sitools.common.XStreamFactory;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.dataset.AbstractDataSetResource;
import fr.cnes.sitools.dataset.DataSetApplication;
import fr.cnes.sitools.dataset.database.DatabaseRequestParameters;
import fr.cnes.sitools.datasource.common.SitoolsDataSource;

/**
 * DBExplorerResource using DataSource for pooled connections
 * 
 * @author AKKA Technologies <a
 *         href="https://sourceforge.net/tracker/?func=detail&atid=2158259&aid=3355053&group_id=531341">[#3355053]</a><br/>
 *         2011/07/06 d.arpin {Rename the method getColumnFromQuery -> getColumnFromDistinctQuery. Rename
 *         getColumnVisible -> getSQLColumnVisible. Add getAllColumnVisible} <br/>
 */
public class DataSetExplorerResource extends AbstractDataSetResource {

  /** The database request params util */
  private DataSetExplorerUtil datasetExplorerUtil = null;

  /** The parent application */
  private DataSetApplication application = null;

  /** If the resource is a recordSet with pagination, this contains its content. */
  private boolean recordSetTarget;

  /** If the resource is a record, this contains its content. */
  private boolean recordTarget;

  /** if resource is relative to a record */
  private String recordName;

  /** databseRequest parameters */
  private DatabaseRequestParameters databaseParams;

  /**
   * if target is the record collection
   * 
   * @return boolean
   */
  public final boolean isRecordSetTarget() {
    return recordSetTarget;
  }

  /**
   * Set if the target is a record set
   * 
   * @param isRecordSetTarget
   *          true if is a record set
   */
  public final void setIfRecordSetTarget(boolean isRecordSetTarget) {
    this.recordSetTarget = isRecordSetTarget;
  }

  /**
   * if target is a specified record
   * 
   * @return boolean
   */
  public final boolean isRecordTarget() {
    return recordTarget;
  }

  @Override
  public void sitoolsDescribe() {
    setName("DataSetExplorerResource");
    setDescription("DataSet explorer");
  }

  @Override
  public void doInit() {

    super.doInit();

    // target : database, table, record
    Map<String, Object> attributes = this.getRequest().getAttributes();

    this.recordName = (attributes.get("record") != null) ? Reference.decode((String) attributes.get("record"),
        CharacterSet.UTF_8) : null;

    this.recordSetTarget = getRequest().getResourceRef().getLastSegment().equals("records");
    this.recordTarget = (this.recordName != null) && !this.recordSetTarget;

    // parent application
    application = (DataSetApplication) getApplication();

    datasetExplorerUtil = new DataSetExplorerUtil(application, this.getRequest(), getContext());

    databaseParams = datasetExplorerUtil.getDatabaseParams();
  }

  /**
   * Gets the representation for the given media type
   * 
   * @param media
   *          RESTlet MediaType
   * @return Representation
   */
  public Representation processConstraint(MediaType media) {
    // first check if the datasource is activated or not
    SitoolsDataSource datasource = databaseParams.getDb();
    if (datasource == null || !"ACTIVE".equals(datasource.getDsModel().getStatus())) {
      // Response response = new Response(false, "Datasource not activated");
      // return getRepresentation(response, media);

      getResponse().setStatus(Status.SERVER_ERROR_SERVICE_UNAVAILABLE, "Datasource not activated");
      return null;
    }
    if (recordSetTarget) { // RECORDS => RETOURNE LA LISTE DES RECORDS SELON LA
                           // PAGINATION
      // Representation dédiée au RecordSet
      Representation repr = new DBRecordSetRepresentation(media, this.datasetExplorerUtil);
      // ajout de la date dernière modification dans la reponse
      repr.setModificationDate(application.getDataSet().getExpirationDate());

      return repr;
    }
    else if (recordTarget) { // RECORD => RETOURNE LE RECORD SELON SA CLE
                             // PRIMAIRE
      // Representation dédiée au Record
      Representation repr = new DBRecordRepresentation(media, datasetExplorerUtil);
      // ajout de la date dernière modification dans la reponse
      repr.setModificationDate(application.getDataSet().getExpirationDate());
      return repr;
    }
    // method must return a representation
    else {
      return getRepresentation(new Response(false, "INCORRECT_TARGET"), media);
    }

  }

  /**
   * Gets XML representation of a DataSet
   * 
   * @return Representation
   */
  @Get("xml")
  public final Representation getXML() {
    Representation result = processConstraint(MediaType.TEXT_XML);
    return result;
  }

  /**
   * Describe the GET method
   * 
   * @param info
   *          the WADL information
   */
  @Override
  public void describeGet(MethodInfo info) {
    info.setIdentifier("retrieve_records");
    info.setDocumentation("Method to retrieve records of a dataset");
    addStandardGetRequestInfo(info);

    //
    //
    //
    // ParameterInfo startInfo = new ParameterInfo("start", false, "xs:int", ParameterStyle.QUERY,
    // "Starting index for SQL request.");
    // ParameterInfo limitInfo = new ParameterInfo("limit", false, "xs:int", ParameterStyle.QUERY,
    // "Maximal number of records returned.");
    // ParameterInfo queryInfo = new ParameterInfo("query", false, "xs:string", ParameterStyle.QUERY,
    // "Request sent by filters and forms.");
    // ParameterInfo colModelInfo = new ParameterInfo("colModel", false, "xs:string", ParameterStyle.QUERY,
    // "Column model description used for getting back records.");
    // ParameterInfo sortInfo = new ParameterInfo("sort", false, "xs:string", ParameterStyle.QUERY,
    // "Column name to sort by.");
    // ParameterInfo dirInfo = new ParameterInfo("dir", false, "xs:string", ParameterStyle.QUERY,
    // "(ASC or DESC) Sorting order.");
    // ParameterInfo distinctInfo = new ParameterInfo("distinct", false, "xs:boolean", ParameterStyle.QUERY,
    // "If true then creates a DISTINCT SQL request.");
    //
    // FilterChained filterChained = application.getFilterChained();
    // List<AbstractFilter> filters = new ArrayList<AbstractFilter>();
    // if (filterChained != null) {
    // filters = application.getFilterChained().getFilters();
    // }
    //
    // info.getRequest().getParameters().add(colModelInfo);
    // info.getRequest().getParameters().add(startInfo);
    // info.getRequest().getParameters().add(limitInfo);
    // info.getRequest().getParameters().add(queryInfo);
    // info.getRequest().getParameters().add(sortInfo);
    // info.getRequest().getParameters().add(dirInfo);
    // info.getRequest().getParameters().add(distinctInfo);
    //
    // for (AbstractFilter abstractFilter : filters) {
    // HashMap<String, ParameterInfo> rpd = abstractFilter.getRequestParamsDescription();
    // if (rpd != null) {
    // for (Entry<String, ParameterInfo> paramDescription : rpd.entrySet()) {
    // ParameterInfo paramInfo = paramDescription.getValue();
    // info.getRequest().getParameters().add(paramInfo);
    // }
    // }
    //
    // }
    DataSetExplorerUtil.addDatasetExplorerGetRequestInfo(info);
    DataSetExplorerUtil.addDatasetExplorerGetFilterInfo(info, application.getFilterChained());

    addStandardResponseInfo(info);
    addStandardInternalServerErrorInfo(info);
  }

  /**
   * Gets JSON representation of a DataSet
   * 
   * @return Representation
   */
  @Get("json")
  public final Representation getJSON() {
    Representation result = processConstraint(MediaType.APPLICATION_JSON);
    return result;
  }

  /**
   * Gets HTML representation of a DataSet
   * 
   * @return Representation
   */
  @Get("html")
  public final Representation getHTML() {
    // On retourne du XML si on demande du HTML pour le fonctionnement avec les navigateurs
    // L'export HTML n'étant pas implémenté on retourne du XML
    Representation result = processConstraint(MediaType.TEXT_XML);
    return result;
  }

  /**
   * Get the CSV representation of the DataSet
   * 
   * @return Representation
   */
  @Get("csv")
  public Representation getCSV() {
    Representation result = processConstraint(MediaType.TEXT_CSV);
    return result;
  }

  /**
   * XStream aliases are personalized for each resource.
   * 
   * @param response
   *          the response to transform
   * @param media
   *          the media type to use
   * @return TODO Optimisation : remonter au niveau DBExplorerApplication les 2 instances xstreamXML et xstreamJSON pour
   *         ne pas avoir à les recréer à chaque resource
   */
  @Override
  public Representation getRepresentation(Response response, MediaType media) {

    XStream xstream = XStreamFactory.getInstance().getXStream(media, getContext());
    configure(xstream, response);

    XstreamRepresentation<Response> rep = new XstreamRepresentation<Response>(media, response);
    rep.setXstream(xstream);
    return rep;
  }

  /**
   * Gets the application value
   * 
   * @return the application
   */
  public final DataSetApplication getApplication() {
    // conserver l'application sinon problème avec RIAP
    return (application == null) ? (DataSetApplication) super.getApplication() : application;
  }

  /**
   * Gets the datasetExplorerUtil value
   * 
   * @return the datasetExplorerUtil
   */
  public DataSetExplorerUtil getDatasetExplorerUtil() {
    return datasetExplorerUtil;
  }

}

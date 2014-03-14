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
package fr.cnes.sitools.dataset.opensearch;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.restlet.Request;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Preference;
import org.restlet.data.Status;
import org.restlet.ext.wadl.MethodInfo;
import org.restlet.ext.xstream.XstreamRepresentation;
import org.restlet.representation.ObjectRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Put;
import org.restlet.resource.ResourceException;

import fr.cnes.sitools.common.model.Resource;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.dataset.database.common.RequestFactory;
import fr.cnes.sitools.dataset.database.jdbc.RequestSql;
import fr.cnes.sitools.dataset.model.Column;
import fr.cnes.sitools.dataset.model.DataSet;
import fr.cnes.sitools.dataset.model.Predicat;
import fr.cnes.sitools.dataset.model.SpecificColumnType;
import fr.cnes.sitools.dataset.model.structure.SitoolsStructure;
import fr.cnes.sitools.dataset.opensearch.model.Opensearch;
import fr.cnes.sitools.dataset.opensearch.model.OpensearchColumn;
import fr.cnes.sitools.dataset.opensearch.runnables.OpensearchRefreshRunnable;
import fr.cnes.sitools.datasource.jdbc.model.JDBCDataSource;
import fr.cnes.sitools.feeds.model.FeedModel;
import fr.cnes.sitools.feeds.model.FeedSource;
import fr.cnes.sitools.server.Consts;
import fr.cnes.sitools.solr.model.DBConfigDTO;
import fr.cnes.sitools.solr.model.EntityDTO;
import fr.cnes.sitools.solr.model.FieldDTO;
import fr.cnes.sitools.solr.model.RssXSLTDTO;
import fr.cnes.sitools.solr.model.SchemaConfigDTO;
import fr.cnes.sitools.solr.model.SchemaFieldDTO;
import fr.cnes.sitools.solr.model.SolRConfigDTO;
import fr.cnes.sitools.util.RIAPUtils;

/**
 * Actions on DataSource : testConnection activation / disable of the associated Application(s)
 * 
 * @author jp.boignard (AKKA Technologies)
 * 
 */
public final class OpenSearchActionResource extends AbstractSearchResource {
  /**
   * Opensearch model object
   */
  private Opensearch os;

  @Override
  public void sitoolsDescribe() {
    setDescription("Actions on Opensearch configuration => Solr index management");
    setNegotiated(false);
  }

  @Override
  public void doInit() {
    super.doInit();
    // on charge la config
    os = getStore().retrieve(getDatasetId());
    if (os != null) {
      setIndexName(os.getId());
    }
  }

  /**
   * Executes the actions on an OpenSearch index Actions available are start, stop, refresh
   * 
   * @param representation
   *          the representation parameter
   * @param variant
   *          the variant parameter
   * @return a representation
   */
  @Put
  public Representation action(Representation representation, Variant variant) {
    Response response = null;

    do {

      if (os == null) {
        response = new Response(false, "OPENSEARCH_NOT_FOUND");
        break;
      }

      if (this.getReference().toString().endsWith("start")) {
        if ("ACTIVE".equals(os.getStatus())) {
          response = new Response(false, "OPENSEARCH_ACTIVE");
          break;
        }
        DataSet ds = this.getDataset(getDatasetId());

        if (!"ACTIVE".equals(ds.getStatus())) {
          response = new Response(false, "DATASET_INACTIVE");
          break;
        }
        // creation de l'index SolR
        Response resp = createSolRIndex(ds, os);
        if (resp.isSuccess()) {
          // Register Opensearch as observer of datasets resources
          unregisterObserver(os);
          registerObserver(os);
          this.os.setErrorMsg(null);
          this.os.setStatus("PENDING");

          createFeedOpensearch();

        }
        else {
          this.os.setStatus("INACTIVE");
          this.os.setErrorMsg(resp.getMessage());
        }
        this.getOpenSearchApplication().setCancelled(false);
        Opensearch osResult = getStore().update(os);
        response = new Response(true, osResult, Opensearch.class, "opensearch");

      }

      if (this.getReference().toString().endsWith("refresh")) {
        response = refreshOsIndex(os);
      }

      if (this.getReference().toString().endsWith("stop")) {
        response = stopOsIndex(os);
        deleteFeedOpensearch();

      }
      if (this.getReference().toString().endsWith("cancel")) {
        response = cancelCurrentOperation(os);
      }
    } while (false);
    // Response

    Representation rep = getRepresentation(response, variant);

    return rep;
  }

  /**
   * Cancel the current operation
   * 
   * @param os2
   *          the opensearch to cancel
   * @return a response
   */
  private Response cancelCurrentOperation(Opensearch os2) {
    Request reqPOST = new Request(Method.POST, RIAPUtils.getRiapBase() + getSitoolsSetting(Consts.APP_SOLR_URL) + "/"
        + os2.getId() + "/cancel");
    Response response = null;

    ArrayList<Preference<MediaType>> objectMediaType = new ArrayList<Preference<MediaType>>();
    objectMediaType.add(new Preference<MediaType>(MediaType.APPLICATION_ALL_XML));
    reqPOST.getClientInfo().setAcceptedMediaTypes(objectMediaType);

    org.restlet.Response responseSolr = null;
    try {
      responseSolr = getContext().getClientDispatcher().handle(reqPOST);

      if (responseSolr == null || Status.isError(responseSolr.getStatus().getCode())) {
        response = new Response(false, "OPENSEARCH_CANCEL_ERROR");
      }
      else {
        @SuppressWarnings("unchecked")
        XstreamRepresentation<Response> repr = (XstreamRepresentation<Response>) responseSolr.getEntity();
        Response resp = (Response) repr.getObject();
        if (resp.getSuccess()) {
          this.getOpenSearchApplication().setCancelled(true);
          response = new Response(true, "opensearch.cancel.successfull");
        }
        else {
          response = new Response(false, resp.getMessage());
        }
      }
      return response;
    }
    finally {
      RIAPUtils.exhaust(responseSolr);
    }
  }

  @Override
  public void describePut(MethodInfo info, String path) {
    if (path.endsWith("start")) {
      info.setDocumentation(" PUT /"
          + path
          + " : Activates the opensearch service on the dataset making it available for the DataSetApplication API users.");
    }
    else if (path.endsWith("stop")) {
      info.setDocumentation(" PUT /"
          + path
          + " : Disactivates the opensearch service on the dataset making it unavailable for the DataSetApplication API users.");
    }
    else if (path.endsWith("refresh")) {
      info.setDocumentation(" PUT /" + path
          + " : Regenerates the opensearch service indexes. This operation is asynchronous.");
    }
    else if (path.endsWith("cancel")) {
      info.setDocumentation(" PUT /" + path + " : Interrupts the task of lucene indexes generation.");
    }
    else {
      info.setDocumentation("Method to modify the opensearch service status.");
    }

    info.setIdentifier(" PUT /" + path);
    this.addStandardGetRequestInfo(info);
    this.addStandardResponseInfo(info);
  }

  /**
   * Creates the FeedModel bean associated to that OpenSearch
   */
  private void createFeedOpensearch() {
    // we create a feedModel representing the feed on this opensearch

    FeedModel feed = new FeedModel();

    feed.setFeedSource(FeedSource.OPENSEARCH);
    feed.setParent(getDatasetId());
    feed.setId(getDatasetId());
    feed.setName(getDatasetId());
    feed.setDescription(os.getDescription());
    feed.setTitle(os.getName());
    feed.setFeedType("rss_2.0");

    this.getStoreFeed().create(feed);

  }

  /**
   * creation l'index solR.
   * 
   * @param ds
   *          : the DataSet object
   * @param os
   *          the OpenSearch object
   * @return The response of the server
   */
  @SuppressWarnings("unchecked")
  private Response createSolRIndex(final DataSet ds, final Opensearch os) {
    // ... Activer => Creer l'index

    List<Column> columns = this.getIndexedColumns(ds, os);

    SolRConfigDTO solRcDTO = new SolRConfigDTO();

    DBConfigDTO dataConf = createSolRdbConfig(ds, columns);
    SchemaConfigDTO scDTO = createSolRSchema(ds, columns, solRcDTO, os);
    // set the rssXSLTDTO
    RssXSLTDTO rssConfig = getRssXSLTDTO(ds, columns, os);
    solRcDTO.setRssXSLTDTO(rssConfig);

    solRcDTO.setIndexName(this.getIndexName());
    solRcDTO.setSchemaConfigDTO(scDTO);
    solRcDTO.setDataConfigDTO(dataConf);

    // create the core
    Request reqPOST = new Request(Method.POST, RIAPUtils.getRiapBase() + getSitoolsSetting(Consts.APP_SOLR_URL)
        + "/create", new ObjectRepresentation<SolRConfigDTO>(solRcDTO));

    ArrayList<Preference<MediaType>> objectMediaType = new ArrayList<Preference<MediaType>>();
    objectMediaType.add(new Preference<MediaType>(MediaType.APPLICATION_ALL_XML));
    reqPOST.getClientInfo().setAcceptedMediaTypes(objectMediaType);

    org.restlet.Response r = null;
    try {
      r = getContext().getClientDispatcher().handle(reqPOST);

      if (r == null || Status.isError(r.getStatus().getCode())) {
        throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
      }

      XstreamRepresentation<Response> repr = (XstreamRepresentation<Response>) r.getEntity();
      Response resp = (Response) repr.getObject();

      if (resp.isSuccess()) {
        // create a runnable task to index asynchronously
        OpensearchRefreshRunnable osRunnable = new OpensearchRefreshRunnable(os, getStore(),
            getSitoolsSetting(Consts.APP_SOLR_URL), this.getContext(), this.getOpenSearchApplication());
        // run the task
        getOpenSearchApplication().getTaskService().execute(osRunnable);

      }
      return resp;
    }
    finally {
      RIAPUtils.exhaust(r);
    }

  }

  /**
   * SolR configuration, schema part. And set returned field
   * 
   * 
   * @param ds
   *          the DataSet
   * @param columns
   *          the columns definition
   * @param solRConf
   *          the solRConf model object
   * @param os
   *          the OpenSearch model object
   * @return a schemaConfigDTO
   */
  private SchemaConfigDTO createSolRSchema(final DataSet ds, final List<Column> columns, SolRConfigDTO solRConf,
      Opensearch os) {

    SchemaConfigDTO scDTO = new SchemaConfigDTO();
    // List of SchemaFields
    List<SchemaFieldDTO> fields = new ArrayList<SchemaFieldDTO>();
    // A schema field used in the loop
    SchemaFieldDTO field;

    Iterator<Column> itCol = columns.iterator();
    Column col;
    while (itCol.hasNext()) {
      col = itCol.next();
      field = new SchemaFieldDTO();
      field.setName(col.getColumnAlias());
      field.setIndexed(true);
      field.setStored(true);

      String type = this.getType(col.getId(), os);
      if (type != null) {
        field.setType(type);
      }
      else {
        // default type is text
        field.setType("text");
      }
      fields.add(field);

      if (col.getId().equals(os.getDefaultSearchField())) {
        scDTO.setDefaultSearchField(col.getColumnAlias());
      }

      if (col.getId().equals(os.getUniqueKey())) {
        scDTO.setUniqueKey(col.getColumnAlias());
      }
    }

    scDTO.setDocument(this.getIndexName());
    scDTO.setFields(fields);
    return scDTO;

  }

  /**
   * Gets the type of the column
   * 
   * @param id
   *          the id of the column
   * @param os
   *          the OpenSearch model object
   * @return the type of the column
   */
  private String getType(String id, Opensearch os) {
    List<OpensearchColumn> list = os.getIndexedColumns();
    boolean found = false;
    String type = null;
    for (Iterator<OpensearchColumn> iterator = list.iterator(); iterator.hasNext() && !found;) {
      OpensearchColumn opensearchColumn = iterator.next();
      if (opensearchColumn.getIdColumn().equals(id)) {
        type = opensearchColumn.getType();
      }
    }
    return type;
  }

  /**
   * SolR configuration, database configuration part
   * 
   * @param ds
   *          the DataSet
   * @param columns
   *          the columns definition
   * @return a DataConfigDTO model object
   */
  private DBConfigDTO createSolRdbConfig(DataSet ds, final List<Column> columns) {
    Resource datasourceResource = ds.getDatasource();
    // String url = datasource.getUrl();
    String id = datasourceResource.getId();

    JDBCDataSource datasource = this.getJDBCDatasource(id);

    DBConfigDTO dataConf = new DBConfigDTO();
    dataConf.setDocument(this.getIndexName());
    dataConf.setDatasource(datasource);

    // List of EntityDTO, we only have one entity for now
    List<EntityDTO> entities = new ArrayList<EntityDTO>();
    // The entity model
    EntityDTO entity;

    // get the name of the table
    if (ds.getStructures().size() > 0) {
      entity = new EntityDTO();
      String entityName = ds.getName();
      entity.setName(entityName);
      // create the request with the schemaName and the tableName
      RequestSql requestSQL = RequestFactory.getRequest(datasource.getDriverClass());

      List<Column> cols = ds.getColumnModel();
      List<Predicat> predicats = ds.getPredicat();
      SitoolsStructure structure = ds.getStructure();

      // On CAST le CONCAT en characteres pour ne pas récupérer n'importe quoi
      // Ce CAST n'est nécessaire que pour MYSQL, sur un champs de type SQL et
      // qui est clé primaire
      // TODO trouver une meilleure solution si c'est possible
      for (Iterator<Column> iterator = cols.iterator(); iterator.hasNext();) {
        Column column = iterator.next();
        if (column.getSpecificColumnType() != null && column.getSpecificColumnType().equals(SpecificColumnType.SQL)
            && column.getDataIndex() != null && column.getDataIndex().toLowerCase().contains("concat")) {
          column.setDataIndex("CAST(" + column.getDataIndex() + " as CHAR)");

        }
      }

      String sql = "SELECT " + requestSQL.getAttributes(cols);

      if ("S".equals(ds.getQueryType())) {
        sql += " " + ds.getSqlQuery();
      }
      else {
        sql += " FROM " + requestSQL.getFromClauseAdvanced(structure);
        sql += " WHERE 1=1 " + requestSQL.getWhereClause(predicats, ds.getColumnModel());
      }

      entity.setQuery(sql);

      entity.setFields(this.getFields(ds, columns, entityName));

      entities.add(entity);
    }
    dataConf.setEntities(entities);

    return dataConf;

  }

  /**
   * Gets the fieldsDTO list for a given list of <code>Column</code>
   * 
   * @param ds
   *          The DataSet model object
   * @param columns
   *          the columns definitions
   * @param entityName
   *          The name of the parent entity
   * @return ArrayList<FieldDTO>, the list of FieldDTO
   */
  private List<FieldDTO> getFields(DataSet ds, final List<Column> columns, String entityName) {
    // creates the fieldsDTO
    // pour l'instant le nom de la colonne = le nom de l'index lucene
    List<FieldDTO> fields = new ArrayList<FieldDTO>();
    FieldDTO field;
    Column col;
    String publicHostDomain = getOpenSearchApplication().getSettings().getPublicHostDomain();
    for (Iterator<Column> itCol = columns.iterator(); itCol.hasNext();) {
      col = itCol.next();
      field = new FieldDTO();
      field.setColumn(col.getColumnAlias());
      field.setName(col.getColumnAlias());

      if (os.getGuidField() != null && os.getGuidField().equals(col.getId())) {

        field.setTemplate(publicHostDomain + ds.getSitoolsAttachementForUsers() + "/records/" + "${" + entityName + "."
            + col.getColumnAlias() + "}");
      }

      if (os.getLinkField() != null && os.getLinkField().equals(col.getId()) && os.isLinkFieldRelative()) {
        field.setTemplate(publicHostDomain + "${" + entityName + "." + col.getColumnAlias() + "}");
      }

      fields.add(field);
    }
    return fields;
  }

  /**
   * Get the JDBCDatasource model object using RIAP
   * 
   * @param id
   *          : the JDBCDatasource model object id
   * @return a JDBCDatasource model object corresponding to the given id null if there is no JDBCDatasource object
   *         corresponding to the given id
   * 
   */
  private JDBCDataSource getJDBCDatasource(String id) {
    return RIAPUtils.getObject(id, getOpenSearchApplication().getSettings().getString(Consts.APP_DATASOURCES_URL),
        getContext());
  }

}

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
package fr.cnes.sitools.datasource.jdbc.dbexplorer;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.restlet.Context;
import org.restlet.data.CharacterSet;
import org.restlet.data.Form;
import org.restlet.data.LocalReference;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Reference;
import org.restlet.ext.freemarker.TemplateRepresentation;
import org.restlet.ext.xstream.XstreamRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

import com.thoughtworks.xstream.XStream;

import fr.cnes.sitools.common.XStreamFactory;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.datasource.jdbc.business.SitoolsSQLDataSource;
import fr.cnes.sitools.datasource.jdbc.model.Attribute;
import fr.cnes.sitools.datasource.jdbc.model.Database;
import fr.cnes.sitools.datasource.jdbc.model.Table;

/**
 * DBExplorerResource using DataSource for pooled connections
 * 
 * @author jp.boignard (AKKA Technologies)
 */
public class DBExplorerResource extends ServerResource {

  /** If the resource is a database, this contains its content. */
  private volatile boolean databaseTarget;

  /** If the resource is a database with a specific schema. */
  private volatile boolean schemaTarget;

  /** If the resource is a table, this contains its content. */
  private volatile boolean tableTarget;

  /**
   * If the resource is a recordSet with pagination, this contains its content.
   */
  private volatile boolean recordSetTarget;

  /** If the resource is a record, this contains its content. */
  private volatile boolean recordTarget;

  /** if resource is relative to a table */
  private String tableName;

  /** if resource is relative to a schema or if schema is given */
  private String schemaName;

  /** if resource is relative to a record */
  private String recordName;

  /** Table associated */
  private Table table = null;

  /** Logger current */
  private Logger logger = Context.getCurrentLogger();

  /** Max number of rows */
  private int maxrows = 0;

  /** Fetch size */
  private int fetchSize = 0;

  /** The parent DBexplorer application handler */
  private volatile DBExplorerApplication dbexplorer = null;

  /** Form */
  private Form pagination = null;

  /** Base reference */
  private String baseRef;

  /**
   * Returns if target is a database
   * 
   * @return true if database
   */
  public final boolean isDatabaseTarget() {
    return databaseTarget;
  }

  /**
   * Returns if target is a schema
   * 
   * @return true if schema
   */
  public final boolean isSchemaTarget() {
    return schemaTarget;
  }

  /**
   * Returns if target is a table
   * 
   * @return true if table
   */
  public final boolean isTableTarget() {
    return tableTarget;
  }

  /**
   * Returns if target is a record set
   * 
   * @return true if record set
   */
  public final boolean isRecordSetTarget() {
    return recordSetTarget;
  }

  /**
   * Returns if target is a record
   * 
   * @return true if record
   */
  public final boolean isRecordTarget() {
    return recordTarget;
  }

  @Override
  public final void doInit() {

    super.doInit();

    // parent : dbexplorer
    this.dbexplorer = (DBExplorerApplication) getApplication();

    // target : database, table, record
    Map<String, Object> attributes = this.getRequest().getAttributes();

    this.schemaName = (attributes.get("schemaName") != null) ? Reference.decode((String) attributes.get("schemaName"),
        CharacterSet.UTF_8) : null;

    this.tableName = (attributes.get("tableName") != null) ? Reference.decode((String) attributes.get("tableName"),
        CharacterSet.UTF_8) : null;

    this.table = new Table(tableName, schemaName);

    this.recordName = (attributes.get("record") != null) ? Reference.decode((String) attributes.get("record"),
        CharacterSet.UTF_8) : null;

    this.databaseTarget = (this.schemaName == null) && (this.tableName == null) && (this.recordName == null);
    this.schemaTarget = (this.schemaName != null) && (this.tableName == null) && (this.recordName == null);
    this.recordSetTarget = (this.tableName != null) && (this.getReference().getLastSegment().equals("records"));
    this.tableTarget = (this.tableName != null) && (this.recordName == null) && !this.recordSetTarget;
    this.recordTarget = (this.tableName != null) && (this.recordName != null) && !this.recordSetTarget;

    // authorized methods
    if (this.dbexplorer.isSelectAllowed()) {
      this.getAllowedMethods().add(Method.GET);
    }

    if (this.dbexplorer.isInsertAllowed()) {
      this.getAllowedMethods().add(Method.POST);
    }

    if (this.dbexplorer.isUpdateAllowed()) {
      this.getAllowedMethods().add(Method.PUT);
    }

    if (this.dbexplorer.isDeleteAllowed()) {
      this.getAllowedMethods().add(Method.DELETE);
    }

    // parameters : pagination, ...
    this.pagination = this.getQuery();

    // TODO baseRef / publicBaseRef
    // pas de / à la fin...
    if (this.getReference().getBaseRef().toString().endsWith("/")) {
      this.baseRef = this.getReference().getBaseRef().toString()
          .substring(1, this.getReference().getBaseRef().toString().length());
    }
    else {
      this.baseRef = this.getReference().getBaseRef().toString();
    }
  }

  /**
   * Get the DataSource
   * 
   * @return the DataSource associated
   */
  public final SitoolsSQLDataSource getDataSource() {
    return this.dbexplorer.getDataSource();
  }

  /**
   * Get the schema name
   * 
   * @return the schema name
   */
  public final String getSchemaName() {
    return schemaName;
  }

  /**
   * Get the table name
   * 
   * @return the table name
   */
  public final String getTableName() {
    return this.tableName;
  }

  /**
   * Get the table
   * 
   * @return the table
   */
  public final Table getTable() {
    return table;
  }

  /**
   * Get the record name
   * 
   * @return the record name
   */
  public final String getRecordName() {
    return this.recordName;
  }

  /**
   * Get the reference
   * 
   * @return the reference
   */
  public final String getFromTableName() {
    return new Table(tableName, schemaName).getFROMReference();
  }

  /**
   * Process constraint
   * 
   * @param media
   *          Client preference
   * @return Representation to be used
   */
  public Representation processConstraint(MediaType media) {

    // DATABASE => RETOURNE LA LISTE DES TABLES
    if (databaseTarget || schemaTarget) {
      // Construction de l'objet java Database a representer
      List<Table> tables = getDataSource().getTables(schemaName);
      fr.cnes.sitools.datasource.jdbc.model.Database database = new fr.cnes.sitools.datasource.jdbc.model.Database();
      database.setUrl(this.getReference().toString());

      if (schemaTarget) {
        String pathPart = "/tables/";
        if (getBaseRef().endsWith("tables")) {
          pathPart = "/";
        }
        for (Table iter : tables) {
          iter.setUrl(getBaseRef() + pathPart + iter.getName());
          database.getTables().add(iter);
        }
      }
      else {
        for (Table iter : tables) {
          iter.setUrl(getBaseRef() + "/schemas/" + iter.getSchema() + "/tables/" + iter.getName());
          database.getTables().add(iter);
        }
      }

      // representation HTML avec freemarker
      if (media.isCompatible(MediaType.TEXT_HTML)) {
        // Load the FreeMarker template
        Representation databaseFtl = new ClientResource(LocalReference.createClapReference(getClass().getPackage())
            + "/Database.ftl").get();

        // Wraps the bean with a FreeMarker representation
        return new TemplateRepresentation(databaseFtl, database, MediaType.TEXT_HTML);
      }
      else {
        // autres representations JSON, XML avec xstream
        Response response = new Response(true, database, Database.class, "database");
        return this.getRepresentation(response, media);

      }

    }
    else if (tableTarget) {
      // TABLE => RETOURNE LA LISTE DES COLONNES
      table.setUrl(this.getReference().toString());
      Connection conn = null;
      ResultSet resultSet = null;
      try {
        conn = getDataSource().getConnection();
        DatabaseMetaData metadata = conn.getMetaData();
        resultSet = metadata.getColumns(null, this.schemaName, getTableName(), null);
        while (resultSet.next()) {
          String name = resultSet.getString("COLUMN_NAME");
          String type = resultSet.getString("TYPE_NAME");
          short javaSqlDataType = resultSet.getShort("DATA_TYPE");
          int size = resultSet.getInt("COLUMN_SIZE");

          Attribute tc = new Attribute(name, type, size, javaSqlDataType);
          table.getAttributes().add(tc);
        }
      }
      catch (SQLException e) {
        logger.severe(e.getMessage());
        Response response = new Response(false, e.getMessage());
        return getRepresentation(response, MediaType.APPLICATION_JSON);
      }
      finally {
        closeConnection(conn);
        closeResultSet(resultSet);
      }

      // representation HTML avec freemarker
      if (media.isCompatible(MediaType.TEXT_HTML)) {
        // Load the FreeMarker template
        Representation tableFtl = new ClientResource(LocalReference.createClapReference(getClass().getPackage())
            + "/Table.ftl").get();

        // Wraps the bean with a FreeMarker representation
        return new TemplateRepresentation(tableFtl, table, MediaType.TEXT_HTML);
      }
      else {
        // autres representations JSON, XML avec xstream
        Response response = new Response(true, table, Table.class, "table");
        return this.getRepresentation(response, media);

      }
    }
//    else if (recordSetTarget) {
//      // RECORDS => RETOURNE LA LISTE DES RECORDS SELON LA PAGINATION
//      // Representation dédiée au RecordSet
//      return new DBRecordSetRepresentation(media, this);
//    }
//    else if (recordTarget) {
//      // RECORD => RETOURNE LE RECORD SELON SA CLE PRIMAIRE
//      // Representation dédiée au Record
//      return new DBRecordRepresentation(media, this);
//    }
    else {
      // method must return a representation
      return getRepresentation(new Response(false, "INCORRECT_TARGET"), media);
    }

  }

  /**
   * Get an XML representation
   * 
   * @return an XML representation
   */
  @Get("xml")
  public Representation getXML() {
    return processConstraint(MediaType.TEXT_XML);
  }

  /**
   * Get JSON representation
   * 
   * @return an JSON representation
   */
  @Get("json")
  public Representation getJSON() {
    return processConstraint(MediaType.APPLICATION_JSON);
  }

  /**
   * Get an HTML representation
   * 
   * @return an HTML representation
   */
  @Get("html")
  public Representation getHTML() {
    return processConstraint(MediaType.TEXT_HTML);
  }


  /**
   * Get base reference
   * 
   * @return base reference
   */
  public final String getBaseRef() {
    return this.baseRef;
  }

  /**
   * Get paginated form
   * 
   * @return form paginated
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
      logger.severe(e.getMessage());
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
      int extend = ((nbHits != null) && !nbHits.equals("")) ? Integer.parseInt(nbHits) : 0;
      return (extend > 0) ? extend : 0;
    }
    catch (NumberFormatException e) {
      logger.severe(e.getMessage());
      return 0;
    }
  }

  /**
   * XStream aliases are personalized for each resources
   * 
   * @param response
   *          the response to be transformed
   * @param media
   *          the media type to use
   * @return TODO Optimisation : remonter au niveau DBExplorerApplication les 2
   *         instances xstreamXML et xstreamJSON pour ne pas avoir à les recréer
   *         à chaque resource
   */
  public final Representation getRepresentation(Response response, MediaType media) {

    XStream xstream = XStreamFactory.getInstance().getXStream(media, getContext());

    // ALIAS
    xstream.autodetectAnnotations(true);
    // Tous les alias des classes du model
    // peut se faire même si null ...
    if (response.getItemClass() != null) {
      xstream.alias("item", Object.class, response.getItemClass());
    }

    // pour supprimer @class sur l'objet data > aliasField
    // peut se faire même si null ...
    if (response.getItemName() != null) {
      xstream.aliasField(response.getItemName(), Response.class, "item");
    }

    XstreamRepresentation<Response> rep = new XstreamRepresentation<Response>(media, response);
    rep.setXstream(xstream);
    return rep;
  }

  /**
   * Gets the fetchSize value
   * @return the fetchSize
   */
  public final int getFetchSize() {
    return fetchSize;
  }

  /**
   * Gets the maxrows value
   * @return the maxrows
   */
  public final int getMaxrows() {
    return maxrows;
  }
  
  /**
   * Method to close the connection
   * @param conn the connection to close
   */
  private void closeConnection(Connection conn) {
    if (conn != null) {
      try {
        conn.close();
      }
      catch (SQLException e) {
        logger.severe(e.getMessage());
      }
    }
  }
  
  /**
   * Method to close the result set
   * @param rs the result set to close
   */
  private void closeResultSet(ResultSet rs) {
    if (rs != null) {
      try {
        rs.close();
      }
      catch (SQLException e) {
        logger.severe(e.getMessage());
      }
    }
  }

}

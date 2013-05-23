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
package fr.cnes.sitools.datasource.jdbc.dbexplorer;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.restlet.data.LocalReference;
import org.restlet.data.MediaType;
import org.restlet.ext.freemarker.TemplateRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;

import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.dataset.model.Column;
import fr.cnes.sitools.dataset.model.DataSet;
import fr.cnes.sitools.datasource.jdbc.model.Database;
import fr.cnes.sitools.datasource.jdbc.model.Table;

/**
 * Class for getting a representation of a DataSet object
 * 
 * @author jp.boignard (AKKA Technologies)
 * 
 */
public class DataSetWrapperResource extends DBExplorerResource {

  /**
   * Get the representation of a process constraint
   * @param media
   *          RESTlet MediaType
   * @return Representation in JSON format of the DataSet conforming the client ExtJS application (and components) needs 
   */
  @Override
  public Representation processConstraint(MediaType media) {
    
    // DATABASE => RETOURNE LA LISTE DES TABLES
    if (isDatabaseTarget() || isSchemaTarget()) {
      // Construction de l'objet java Database a representer
      List<Table> tables = getDataSource().getTables(getSchemaName());
      fr.cnes.sitools.datasource.jdbc.model.Database database = new fr.cnes.sitools.datasource.jdbc.model.Database();
      database.setUrl(this.getReference().toString());

      if (isSchemaTarget()) {
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
    else if (isTableTarget()) {
      
      // TABLE => RETOURNE LA LISTE DES COLONNES

      DataSet myDataSet = new DataSet(this.getReference().toString(), getTable().getReference(), "Schema:"
          + getTable().getSchema() + " Table:" + getTable().getName()); // id = url
      // table.setUrl(this.getReference().toString());
      Connection conn = null;
      try {
        conn = getDataSource().getConnection();
        DatabaseMetaData metadata = conn.getMetaData();
        ResultSet resultSet = metadata.getColumns(null, null, getTableName(), null);
        while (resultSet.next()) {

          String name = resultSet.getString("COLUMN_NAME");
          String type = resultSet.getString("TYPE_NAME").toUpperCase();
          Integer size = resultSet.getInt("COLUMN_SIZE");
          
          String columnID = name;
          String columnDATAINDEX = name;
          String columnHEADER = name;

          int columnWIDTH = (size != null) ? size * 5 : 20;
          columnWIDTH = (columnWIDTH < 30) ? 30 : columnWIDTH;
          boolean columnVISIBLE = true;
          boolean columnSORTABLE = true;
          String columnTYPE = "string";

          if (type.contains("INT") || type.contains("FLOAT") || type.contains("NUM") || type.contains("DOUBLE")) {
            columnTYPE = "numeric";
          }

          Column column = new Column(columnID, columnDATAINDEX, columnHEADER, columnWIDTH, columnVISIBLE,
              columnSORTABLE, columnTYPE);

          myDataSet.addColumn(column);
        }
      }
      catch (SQLException e) {
        getLogger().warning(e.getMessage());
        Response response = new Response(false, e.getMessage());
        return getRepresentation(response, MediaType.APPLICATION_JSON);
      }
      finally {
        if (conn != null) {
          try {
            conn.close();
          }
          catch (SQLException e) {
            getLogger().warning(e.getMessage());
          }
        }
      }

      // representation HTML avec freemarker
      if (media.isCompatible(MediaType.APPLICATION_JSON)) {
        // autres representations JSON, XML avec xstream
        Response response = new Response(true, myDataSet, DataSet.class, "DataSet");
        return this.getRepresentation(response, media);
      }
      else {
        Response response = new Response(false, "FORMAT INCOMPATIBLE");
        return getRepresentation(response, MediaType.APPLICATION_JSON);
      }
    }
//    else if (isRecordSetTarget()) {
//      // RECORDS => RETOURNE LA LISTE DES RECORDS SELON LA PAGINATION
//      return new DBRecordSetRepresentation(media, this);
//    }
//    else if (isRecordTarget()) {
//      // RECORD => RETOURNE LE RECORD SELON SA CLE PRIMAIRE
//      return new DBRecordRepresentation(media, this);
//    }
    else {
      // method must return a representation
      return getRepresentation(new Response(false, "INCORRECT_TARGET"), media);
    }

  }

}

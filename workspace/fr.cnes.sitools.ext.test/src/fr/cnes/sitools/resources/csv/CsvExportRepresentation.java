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
package fr.cnes.sitools.resources.csv;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;

import org.restlet.Context;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.engine.Engine;
import org.restlet.representation.OutputRepresentation;
import org.restlet.resource.ResourceException;

import fr.cnes.sitools.common.exception.SitoolsException;
import fr.cnes.sitools.dataset.converter.business.ConverterChained;
import fr.cnes.sitools.dataset.database.DatabaseRequest;
import fr.cnes.sitools.dataset.database.DatabaseRequestFactory;
import fr.cnes.sitools.dataset.database.DatabaseRequestParameters;
import fr.cnes.sitools.dataset.database.common.DataSetExplorerResource;
import fr.cnes.sitools.dataset.model.Column;
import fr.cnes.sitools.datasource.jdbc.model.AttributeValue;
import fr.cnes.sitools.datasource.jdbc.model.Record;
import fr.cnes.sitools.units.dimension.model.SitoolsUnit;
import fr.cnes.sitools.util.DateUtils;

/**
 * Create a HTML representation of a list of records
 * 
 * @author m.gond
 * 
 */
public class CsvExportRepresentation extends OutputRepresentation {
  /**
   * Field separator for CSV
   */
  private static final String CSV_COMMA = ",";

  /**
   * Row end for CSV
   */
  private static final String CSV_ROWEND = "\n";

  /**
   * Replacement for comma in fields
   */
  private static final String CSV_COMMA_REPLACE = ".";

  /**
   * Replacement for double quotes in fields
   */
  private static final String CSV_DQUOTES_REPLACE = " ";

  /** the parameters */
  private DatabaseRequestParameters params;
  /** The Converter Chained */
  private ConverterChained converters;
  /** The database request */
  private DatabaseRequest databaseRequest;
  /** Whether or not to return records */
  private boolean noRecords;

  /**
   * Default constructor
   * 
   * @param mediaType
   *          the mediaType needed
   */
  public CsvExportRepresentation(MediaType mediaType) {
    super(mediaType);
    // TODO Auto-generated constructor stub
  }

  /**
   * Constructor with DatabaseRequestParameters and title
   * 
   * @param mediaType
   *          the mediaType needed
   * @param params
   *          the DatabaseRequestParameters
   * @param converters
   *          the converters
   * @param context
   *          the context
   * @param noRecords
   *          true to return no records, false otherwise
   * 
   */
  public CsvExportRepresentation(MediaType mediaType, DatabaseRequestParameters params, ConverterChained converters,
      Context context, boolean noRecords) {
    super(mediaType);
    this.params = params;
    this.converters = converters;
    this.noRecords = noRecords;

  }

  @Override
  public void write(OutputStream out) throws IOException {
    boolean isExportRequest = false;

    this.databaseRequest = DatabaseRequestFactory.getDatabaseRequest(params);
    if (databaseRequest != null) {
      try {
        if (params.getDistinct()) {
          databaseRequest.createDistinctRequest();
        }
        else {
          databaseRequest.createRequest();
        }

        // Step 0 : writing initial comments. If limit=0 is given, just write
        // counts
        isExportRequest = writeCsvComments(out);

        if (isExportRequest && databaseRequest.getCount() != 0) {
          // Step 1 : writing the header of the CSV
          writeCsvHeader(out);

          // Step 2 : writing CSV body
          writeCsvBody(out);
        }
      }
      catch (SitoolsException e) {
        throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
      }
      finally {
        if (databaseRequest != null) {
          try {
            databaseRequest.close();
          }
          catch (SitoolsException e) {
            e.printStackTrace();
          }
        }
        if (out != null) {
          out.close();
        }
      }
    }
  }

  /**
   * Write initial comments
   * 
   * @param arg0
   *          the stream to write to
   * @return true if the CSV export must continue
   */
  private boolean writeCsvComments(OutputStream arg0) {
    try {

      boolean isExport = true;

      if (databaseRequest != null) {

        // Next for reading first record
        databaseRequest.nextResult();

        String commentStart = "#";

        // Writing date
        String commentLine = commentStart;

        String dateStr = DateUtils.format(new Date(), DateUtils.SITOOLS_DATE_FORMAT);
        commentLine += "DATE : " + dateStr + "\n";

        // writing number of records sent
        String nrecords = commentStart + "NRECORDS : ";

        // If limit=0 : total count sent only
        if (noRecords) {
          // If nocount=true, no count is done
          if (!this.params.isCountDone()) {
            nrecords += "N/A";
          }
          else {
            nrecords += String.valueOf(databaseRequest.getTotalCount());
          }
          commentLine += nrecords + "\n";
          isExport = false;
        }
        if (isExport) {
          nrecords += String.valueOf(databaseRequest.getCount());
          commentLine += nrecords + "\n";
        }

        // Getting the headers
        // List<Column> cols = dsa.getDataSet().getColumnModel();
        List<Column> cols = this.params.getSqlVisibleColumns();
        String value = "";
        for (Column col : cols) {
          // if (col.isVisible()) {
          commentLine += commentStart;
          value = col.getColumnAlias() + " : ";
          SitoolsUnit unit = col.getUnit();
          if (unit != null && !"".equals(unit.getLabel())) {
            value += "unit =" + unit.getLabel();
          }
          value += "\n";
          commentLine += value;
          // }
        }
        arg0.write(commentLine.getBytes());
        arg0.flush();
      }
      return isExport;
    }
    catch (SitoolsException sqle) {
      Engine.getLogger(this.getClass().getName()).severe(sqle.getMessage());
      return false;
    }
    catch (IOException ioe) {
      Engine.getLogger(this.getClass().getName()).severe(ioe.getMessage());
      return false;
    }
  }

  /**
   * Write the CSV header
   * 
   * @param out
   *          the output stream
   * @throws IOException
   *           when stream problem occurs
   */
  private void writeCsvHeader(OutputStream out) throws IOException {
    try {
      if (this.databaseRequest != null) {

        // Request >> Record
        Record rec = databaseRequest.getRecord();

        // Record >> Converters >> Record
        if (this.converters != null) {
          rec = this.converters.getConversionOf(rec);
        }

        String head = "";
        // Getting the headers
        // List<Column> cols = dsa.getDataSet().getColumnModel();
        // Getting the columns for the list of visible column so that invisible
        // column won't be shown
        List<Column> cols = this.params.getSqlVisibleColumns();
        String value = null;
        for (Column col : cols) {
          value = treatHeads(col.getColumnAlias());
          head += value + CSV_COMMA;
        }
        head += CSV_ROWEND;
        head = head.replace(CSV_COMMA + CSV_ROWEND, CSV_ROWEND);
        out.write(head.getBytes());
      }
      out.flush();
    }
    catch (SitoolsException sqle) {
      Engine.getLogger(DataSetExplorerResource.class.getName()).log(Level.SEVERE, null, sqle);
      throw new ResourceException(Status.SERVER_ERROR_INTERNAL, "dataset.records.sql.error", sqle);
    }

  }

  /**
   * Write the CSV body
   * 
   * @param out
   *          the output stream where the CSV lines are written
   * @throws IOException
   *           if out stream writing fails
   */
  private void writeCsvBody(OutputStream out) throws IOException {
    try {
      out.write(getLine());
      while (databaseRequest.nextResult()) {
        out.write(getLine());
      }
      out.flush();
    }
    catch (SitoolsException sqle) {
      Engine.getLogger(DataSetExplorerResource.class.getName()).log(Level.SEVERE, null, sqle);
      throw new ResourceException(Status.SERVER_ERROR_INTERNAL, "dataset.records.sql", sqle);
    }
  }

  /**
   * Get a simple record line for writing CSV
   * 
   * @return bytes for stream output
   */
  private byte[] getLine() {
    // Request >> Record
    Record rec;
    String line = "";
    try {
      rec = databaseRequest.getRecord();

      // Record >> Converters >> Record
      if (this.converters != null) {
        rec = this.converters.getConversionOf(rec);
      }
      // Getting Values
      // List<Column> cols = dsa.getDataSet().getColumnModel();
      // Getting the columns for the list of visible column so that invisible
      // column won't be shown
      List<Column> cols = this.params.getSqlVisibleColumns();
      List<AttributeValue> attrList = rec.getAttributeValues();
      String value = null;
      for (Column col : cols) {
        // if (col.isVisible()) {
        value = null;
        for (AttributeValue val : attrList) {
          if (val.getName().equals(col.getColumnAlias())) {
            value = treatChars(val);
            break;
          }
        }
        if (value == null) {
          value = "";
        }
        line += value + CSV_COMMA;
        // }
      }
      line += CSV_ROWEND;
      line = line.replace(CSV_COMMA + CSV_ROWEND, CSV_ROWEND);

    }
    catch (SitoolsException sqle) {
      Engine.getLogger(DataSetExplorerResource.class.getName()).log(Level.SEVERE, null, sqle);
      throw new ResourceException(Status.SERVER_ERROR_INTERNAL, "dataset.records.sql", sqle);
    }
    return line.getBytes();
  }

  /**
   * Treating the special characters according to the given export policy
   * 
   * @param att
   *          the attribute to treat
   * @return the attribute modified
   */
  private String treatChars(AttributeValue att) {
    String str = "";
    if (att.getValue() != null && !"".equals(att.getValue())) {
      str = att.getValue().toString();
    }
    str = str.replaceAll("(\\n|\\r|\")", CSV_DQUOTES_REPLACE);
    str = str.replaceAll(CSV_COMMA, CSV_COMMA_REPLACE);
    return str;
  }

  /**
   * Treating the special characters in header according to the given export
   * policy
   * 
   * @param att
   *          the attribute to treat
   * @return the attribute modified
   */
  private String treatHeads(String att) {
    String str = att.replaceAll("(\\n|\\r|\")", CSV_DQUOTES_REPLACE);
    str = str.replaceAll(CSV_COMMA, CSV_COMMA_REPLACE);
    return str;
  }

}

     /*******************************************************************************
 * Copyright 2010-2013 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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
package fr.cnes.sitools.dataset.export;

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.restlet.data.Form;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

import fr.cnes.sitools.common.exception.SitoolsException;
import fr.cnes.sitools.dataset.DataSetApplication;
import fr.cnes.sitools.dataset.converter.business.ConverterChained;
import fr.cnes.sitools.dataset.database.DatabaseRequest;
import fr.cnes.sitools.dataset.database.common.DataSetExplorerResource;
import fr.cnes.sitools.dataset.database.common.DataSetExplorerUtil;
import fr.cnes.sitools.dataset.model.Column;
import fr.cnes.sitools.datasource.jdbc.model.AttributeValue;
import fr.cnes.sitools.datasource.jdbc.model.Record;
import fr.cnes.sitools.units.dimension.model.SitoolsUnit;

/**
 * 
 * <a href="http://sourceforge.net/tracker/?func=detail&aid=3314255&group_id=531341&atid=2158259 ">[3314255]</a><br/>
 * 2011/06/16 m.gond {Show only the visible columns} <br/>
 * 2011/07/04 d.arpin : {view all the columns asked in the csvBody}
 * 
 * <a href="https://sourceforge.net/tracker/?func=detail&atid=2158259&aid=3355053&group_id=531341">[#3355053]</a><br/>
 * 2011/07/06 d.arpin {Use the method getAllColumnVisible instead of getColumnVisible to get the virtual columns} <br/>
 * 
 * Class to create representation associated to export formats (i.e. CSV, PDF)
 * 
 * @author m.marseille (AKKA Technologies)
 * 
 */
public final class DBRecordSetExportRepresentation {

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

  /**
   * The database request used for CSV export
   */
  private DatabaseRequest databaseRequest;

  /**
   * The Form used for the Column model
   */
  private Form form;

  /**
   * The DataSet application for notions
   */
  private DataSetApplication dsa;

  /**
   * The converter chained for the request treatment before export
   */
  private ConverterChained converterChained;
  /**
   * The datasetExplorerResource
   */
  private DataSetExplorerUtil datasetExplorerUtil;

  /**
   * Constructor
   * 
   * @param dbr
   *          the DataSource request used
   * @param res
   *          the datasetExplorerResource
   * @param d
   *          the datasetApplication for notions
   * @param cvc
   *          the converters before export
   * 
   */
  public DBRecordSetExportRepresentation(DatabaseRequest dbr, DataSetExplorerUtil res, DataSetApplication d,
      ConverterChained cvc) {
    super();
    this.databaseRequest = dbr;
    this.form = res.getRequest().getResourceRef().getQueryAsForm();
    this.setDsa(d);
    this.converterChained = cvc;
    this.datasetExplorerUtil = res;

  }

  /**
   * Get the stream
   * 
   * @param arg0
   *          the initial stream
   * @throws IOException
   *           if writing problems occur
   */
  public void getStream(OutputStream arg0) throws IOException {

    boolean isExportRequest = false;

    // Step 0 : writing initial comments. If limit=0 is given, just write counts
    isExportRequest = writeCsvComments(arg0);

    if (isExportRequest && databaseRequest.getCount() != 0) {
      // Step 1 : writing the header of the CSV
      writeCsvHeader(arg0);

      // Step 2 : writing CSV body
      writeCsvBody(arg0);
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
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        commentLine += "DATE : " + sdf.format(cal.getTime()) + "\n";

        // writing number of records sent
        String nrecords = commentStart + "NRECORDS : ";

        // If limit=0 : total count sent only
        if (form.getFirst("limit") != null && form.getFirst("limit").getValue().contentEquals("0")) {
          // If nocount=true, no count is done
          if (form.getFirst("nocount") != null && form.getFirst("nocount").getValue().toLowerCase().equals("true")) {
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
        List<Column> cols = this.datasetExplorerUtil.getAllColumnVisible();
        commentLine = getCsvCommentHeader(commentStart, commentLine, cols);
        arg0.write(commentLine.getBytes());
        arg0.flush();
      }
      return isExport;
    }
    catch (SitoolsException sqle) {
      Logger.getLogger(this.getClass().getName()).severe(sqle.getMessage());
      return false;
    }
    catch (IOException ioe) {
      Logger.getLogger(this.getClass().getName()).severe(ioe.getMessage());
      return false;
    }
  }

  /**
   * Get the csv comment header from a list of {@link Column}
   * 
   * @param commentStart
   *          the commentStart
   * @param commentLine
   *          the commentLine
   * @param cols
   *          the list of {@link Column}
   * @return the header
   */
  private String getCsvCommentHeader(String commentStart, String commentLine, List<Column> cols) {
    String value = "";
    for (Column col : cols) {
      commentLine += commentStart;
      value = col.getColumnAlias() + " : ";
      SitoolsUnit unit = col.getUnit();
      if (unit != null && !"".equals(unit.getLabel())) {
        value += "unit =" + unit.getLabel();
      }
      value += "\n";
      commentLine += value;
    }
    return commentLine;
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
        if (converterChained != null) {
          rec = converterChained.getConversionOf(rec);
        }

        String head = "";
        // Getting the headers
        // List<Column> cols = dsa.getDataSet().getColumnModel();
        // Getting the columns for the list of visible column so that invisible column won't be shown
        List<Column> cols = this.datasetExplorerUtil.getAllColumnVisible();
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
      Logger.getLogger(DataSetExplorerResource.class.getName()).log(Level.SEVERE, null, sqle);
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
      Logger.getLogger(DataSetExplorerResource.class.getName()).log(Level.SEVERE, null, sqle);
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
      if (converterChained != null) {
        rec = converterChained.getConversionOf(rec);
      }
      // Getting Values
      // List<Column> cols = dsa.getDataSet().getColumnModel();
      // Getting the columns for the list of visible column so that invisible column won't be shown
      List<Column> cols = this.datasetExplorerUtil.getAllColumnVisible();
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
      Logger.getLogger(DataSetExplorerResource.class.getName()).log(Level.SEVERE, null, sqle);
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
   * Treating the special characters in header according to the given export policy
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

  /**
   * Gets the dsa value
   * 
   * @return the dsa
   */
  public DataSetApplication getDsa() {
    return dsa;
  }

  /**
   * Sets the value of dsa
   * 
   * @param dsa
   *          the dsa to set
   */
  public void setDsa(DataSetApplication dsa) {
    this.dsa = dsa;
  }

}

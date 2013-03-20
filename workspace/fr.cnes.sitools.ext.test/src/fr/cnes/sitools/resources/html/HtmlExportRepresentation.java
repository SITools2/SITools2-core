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
package fr.cnes.sitools.resources.html;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.restlet.Context;
import org.restlet.data.MediaType;
import org.restlet.representation.OutputRepresentation;

import fr.cnes.sitools.common.SitoolsSettings;
import fr.cnes.sitools.common.application.ContextAttributes;
import fr.cnes.sitools.common.exception.SitoolsException;
import fr.cnes.sitools.dataset.converter.business.ConverterChained;
import fr.cnes.sitools.dataset.database.DatabaseRequest;
import fr.cnes.sitools.dataset.database.DatabaseRequestFactory;
import fr.cnes.sitools.dataset.database.DatabaseRequestParameters;
import fr.cnes.sitools.dataset.model.BehaviorEnum;
import fr.cnes.sitools.dataset.model.Column;
import fr.cnes.sitools.dataset.model.ColumnRenderer;
import fr.cnes.sitools.datasource.jdbc.model.AttributeValue;
import fr.cnes.sitools.datasource.jdbc.model.Record;
import fr.cnes.sitools.util.Util;

/**
 * Create a HTML representation of a list of records
 * 
 * @author m.gond
 * 
 */
public class HtmlExportRepresentation extends OutputRepresentation {
  /** START_TR */
  private static final String START_TR = "<TR>";
  /** STOP_TR */
  private static final String STOP_TR = "</TR>";
  /** START_TD */
  private static final String START_TD = "<TD>";
  /** STOP_TD */
  private static final String STOP_TD = "</TD>";
  /** START_TH */
  private static final String START_TH = "<TH>";
  /** STOP_TH */
  private static final String STOP_TH = "</TH>";
  /** START_TABLE */
  private static final String START_TABLE = "<TABLE cellspacing=\"0\" id=\"the-table\">";
  /** STOP_TABLE */
  private static final String STOP_TABLE = "</TABLE>";
  /** START_THEAD */
  private static final String START_THEAD = "<THEAD style=\"background:#eeeeee;\">";
  /** STOP_THEAD */
  private static final String STOP_THEAD = "</THEAD>";
  /** START_TBODY */
  private static final String START_TBODY = "<TBODY>";
  /** STOP_TBODY */
  private static final String STOP_TBODY = "</TBODY>";
  /** HTTP */
  private static final String HTTP = "http://";

  /** the parameters */
  private DatabaseRequestParameters params;
  /** the html title */
  private String title;
  /** The Converter Chained */
  private ConverterChained converters;
  /** The context */
  private Context context;

  /**
   * Default constructor
   * 
   * @param mediaType
   *          the mediaType needed
   */
  public HtmlExportRepresentation(MediaType mediaType) {
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
   * @param title
   *          the title
   * @param converters
   *          the converters
   * @param context
   *          the context
   */
  public HtmlExportRepresentation(MediaType mediaType, DatabaseRequestParameters params, String title,
      ConverterChained converters, Context context) {
    super(mediaType);
    this.title = title;
    this.params = params;
    this.converters = converters;
    this.context = context;
  }

  @Override
  public void write(OutputStream outputStream) throws IOException {
    boolean headWriten = false;
    Record rec = null;

    DatabaseRequest databaseRequest = DatabaseRequestFactory.getDatabaseRequest(params);
    try {
      if (params.getDistinct()) {
        databaseRequest.createDistinctRequest();
      }
      else {
        databaseRequest.createRequest();
      }

      // Create a structure to store header and rendering information for each
      // column
      HashMap<String, Object[]> map = new HashMap<String, Object[]>();
      List<Column> visbleColumns = params.getSqlVisibleColumns();
      for (Iterator<Column> iterColumn = visbleColumns.iterator(); iterColumn.hasNext();) {
        Column column = iterColumn.next();
        Object[] colProperties = new Object[2];
        colProperties[0] = column.getHeader();
        colProperties[1] = column.getColumnRenderer();
        map.put(column.getColumnAlias(), colProperties);
      }

      // HTML export
      PrintStream out = new PrintStream(outputStream);
      out.print("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\""
          + "\"http://www.w3.org/TR/html4/loose.dtd\">"
          + "<html>"
          + "  <head>"
          + " <meta http-equiv=\"Expires\" content=\"0\">"
          + " <meta http-equiv=\"Pragma\" content=\"no-cache\">"
          + " <meta NAME=\"author\" content=\"CNES\">"
          + " <meta NAME=\"description\" content=\"SITools2 is an open source framework for scientific archives. It provides both search capabilities, data access and web services integration.\">"
          + " <meta NAME=\"keywords\" content=\"CNES, SITools2, open source, web service, archive,  scientific, Data Access Layer, information system, data server, tool, ${projectList}, open search, interoperability\">"
          + " <meta NAME=\"DC.Title\" content=\"SITools2 - Scientific archive\">"
          + " <meta NAME=\"DC.Creator\" content=\"SITools2 - CNES\">"
          + "   <meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">" + "   <title>" + title
          + "</title>" + "   <link rel=\"stylesheet\" type=\"text/css\" href=\"/sitools/common/res/css/main.css\">"
          + "   <script type=\"text/javascript\" src=\"/sitools/cots/extjs/adapter/ext/ext-base.js\"></script>"
          + "   <script type=\"text/javascript\" src=\"/sitools/cots/extjs/ext-all.js\"></script>"
          + "   <style type=\"text/css\">" + "   #the-table { border:1px solid #bbb;border-collapse:collapse; }"
          + "   #the-table td,#the-table th { border:1px solid #ccc;border-collapse:collapse;padding:5px; }"
          + "   </style>" + "  </head>" + "  <body>" + START_TABLE);

      // Get the public host domain

      SitoolsSettings settings = (SitoolsSettings) context.getAttributes().get(ContextAttributes.SETTINGS);
      String publicHostName = settings.getPublicHostDomain();

      String header = START_THEAD + START_TR;
      try {
        while (databaseRequest.nextResult()) {

          String currentLine = START_TR;

          rec = databaseRequest.getRecord();
          if (Util.isSet(converters)) {
            rec = converters.getConversionOf(rec);
          }
          List<AttributeValue> list = rec.getAttributeValues();
          AttributeValue obj;

          for (Iterator<AttributeValue> it = list.iterator(); it.hasNext();) {
            obj = it.next();

            if (Util.isSet(obj)) {

              String columnAlias = obj.getName();
              // if the column is visible and has to be added to the header, default to false (noClientAccess)
              boolean isVisible = false;
              if (Util.isSet(obj.getValue()) && !"".equals(obj.getValue())) {
                ColumnRenderer columnRenderer = (ColumnRenderer) map.get(columnAlias)[1];
                BehaviorEnum rendering = null;
                if (columnRenderer != null) {
                  rendering = columnRenderer.getBehavior();
                }
                if (!BehaviorEnum.noClientAccess.equals(rendering)) {
                  // set isVisible to true, because the column will be visible and show
                  isVisible = true;
                  currentLine += START_TD;
                  if (!Util.isSet(rendering)) {
                    currentLine += obj.getValue();
                  }
                  else if (rendering.equals(BehaviorEnum.ImgAutoThumb)) {
                    currentLine += "<img class=\"sitools-display-image\" src=\"" + String.valueOf(obj.getValue())
                        + "\" ALT=\"\">";
                  }
                  else if (rendering.equals(BehaviorEnum.ImgNoThumb) || rendering.equals(BehaviorEnum.localUrl)
                      || rendering.equals(BehaviorEnum.extUrlDesktop) || rendering.equals(BehaviorEnum.extUrlNewTab)) {
                    String value = String.valueOf(obj.getValue());
                    if (value.startsWith(HTTP)) {
                      currentLine += value;
                    }
                    else {
                      currentLine += publicHostName + value;
                    }
                  }
                  else {

                    String value = String.valueOf(obj.getValue());
                    currentLine += value;
                  }
                  currentLine += STOP_TD;
                }
              }
              else {
                currentLine += START_TD + "&nbsp;" + STOP_TD;
              }

              if (!headWriten && isVisible) {
                header += START_TH + map.get(columnAlias)[0] + STOP_TH;
              }

            }
            else {
              currentLine += START_TD + "&nbsp;" + STOP_TD;

            }
          }
          currentLine += STOP_TR;

          if (!headWriten) {
            header += STOP_TR + STOP_THEAD + START_TBODY;
            out.print(header);
            headWriten = true;
          }
          out.print(currentLine);
        }
        out.println(STOP_TBODY + STOP_TABLE + "</body></html>");
        out.close();
      }
      catch (SitoolsException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
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

      out.print(STOP_TABLE + "</body>");
    }
    catch (SitoolsException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
}

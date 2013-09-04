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
package fr.cnes.sitools.dataset.database.common;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.OutputRepresentation;
import org.restlet.resource.ResourceException;

import com.thoughtworks.xstream.XStream;

import fr.cnes.sitools.common.XStreamFactory;
import fr.cnes.sitools.common.exception.SitoolsException;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.dataset.DataSetApplication;
import fr.cnes.sitools.dataset.converter.business.ConverterChained;
import fr.cnes.sitools.dataset.database.DatabaseRequest;
import fr.cnes.sitools.dataset.database.DatabaseRequestFactory;
import fr.cnes.sitools.dataset.export.DBRecordSetExportRepresentation;
import fr.cnes.sitools.dataset.filter.business.FilterChained;
import fr.cnes.sitools.datasource.jdbc.model.AttributeValue;
import fr.cnes.sitools.datasource.jdbc.model.Record;

/**
 * <a href="http://sourceforge.net/tracker/?func=detail&aid=3314255&group_id=531341&atid=2158259 ">[3314255]</a><br/>
 * 16/06/2011 m.gond {} <br/>
 * 
 * Representation of a DB record set
 * 
 * @author jp.boignard (AKKA Technologies)
 */
public final class DBRecordSetRepresentation extends OutputRepresentation {

  /** Request definition */
  private DatabaseRequest databaseRequest = null;

  /** Chain of converters to be applied */
  private ConverterChained converterChained = null;

  /** Chain of filters to be applied */
  private FilterChained filterChained = null;

  /** Parent resource */
  private DataSetExplorerUtil datasetExplorerUtil = null;

  /**
   * Representation of the record set.
   * 
   * @param mediaType
   *          the {@code MediaType}
   * @param res
   *          the {@code DataSetExplorerResource}
   */
  public DBRecordSetRepresentation(MediaType mediaType, DataSetExplorerUtil res) {
    super(mediaType);

    this.datasetExplorerUtil = res;
    // this.databaseRequest = new DefaultDatabaseRequest(res.getDatabaseParams());
    this.databaseRequest = DatabaseRequestFactory.getDatabaseRequest(res.getDatabaseParams());

    this.converterChained = res.getApplication().getConverterChained();
    this.setFilterChained(res.getApplication().getFilterChained());

    // check that the request can be executed
    try {
      this.databaseRequest.checkRequest();
    }
    catch (SitoolsException e) {
      throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, e.getMessage());
    }
  }

  @Override
  public void write(OutputStream arg0) throws IOException {

    // First lets create the request to the database
    if (this.getMediaType().isCompatible(MediaType.APPLICATION_XML)
        || this.getMediaType().isCompatible(MediaType.TEXT_XML)
        || this.getMediaType().isCompatible(MediaType.APPLICATION_JSON)
        || this.getMediaType().isCompatible(MediaType.TEXT_CSV)) {

      try {
        if (this.datasetExplorerUtil.getDistinct()) {
          databaseRequest.createDistinctRequest();
        }
        else {
          databaseRequest.createRequest();
        }
        if (converterChained != null) {
          converterChained.getContext().getAttributes().put("REQUEST", datasetExplorerUtil.getRequest());
        }
        if (this.getMediaType().isCompatible(MediaType.APPLICATION_XML)
            || this.getMediaType().isCompatible(MediaType.TEXT_XML)) {

          // ResultSet >> Record >> Converters >> Record >> XML
          // retourner au format XML et en streaming, un objet Response
          // contenant la
          // liste des records

          XStream xstream = XStreamFactory.getInstance().getXStream(this.getMediaType(),
              this.datasetExplorerUtil.getContext());

          // ALIAS
          xstream.alias("record", Record.class);
          xstream.alias("value", Object.class, String.class);
          xstream.alias("response", Response.class);
          xstream.alias("attributeValue", AttributeValue.class);
          xstream.omitField(Response.class, "itemName");

          ObjectOutputStream out = xstream.createObjectOutputStream(arg0, "root");
          // if (this.databaseRequest.isCountDone()) {
          // // xstream.alias("total", Object.class, Integer.class);
          // // out.writeObject(databaseRequest.getTotalCount());
          // }

          try {
            // Checking if limit = 0 or -1 is present
            boolean isShortResponse = false;
            if (databaseRequest.getCount() == 0) {

              // // FIXME Bidouille limit
              // String param =
              // this.datasetExplorerUtil.getRequest().getResourceRef().getQueryAsForm().getFirst("limit").getValue();
              // // limit = 0 case : sending only total count
              // if (param.equals("0")) {
              isShortResponse = true;
              xstream.alias("response", Response.class);
              xstream.aliasType("success", boolean.class);
              xstream.aliasType("total", int.class);
              out = xstream.createObjectOutputStream(arg0, "response");
              out.writeBoolean(true);
              if (this.databaseRequest.isCountDone()) {
                out.writeInt(this.databaseRequest.getTotalCount());
              }
              out.flush();
              return;
              // }
            }
            if (!isShortResponse) {
              int nbRecordsSent = 0;
              while (databaseRequest.nextResult()) {

                // ResultSet >> Record
                Record rec = databaseRequest.getRecord();

                // Record >> Converters >> Record
                if (converterChained != null) {
                  // converterChained.getContext().getAttributes().put("REQUEST",
                  // datasetExplorerResource.getRequest());
                  rec = converterChained.getConversionOf(rec);
                }

                // Record >> XML
                out.writeObject(rec);
                out.flush();
                nbRecordsSent++;
              }
              Response response = new Response();
              if (this.databaseRequest.isCountDone()) {
                response.setTotal(this.databaseRequest.getTotalCount());
              }
              response.setCount(nbRecordsSent);
              response.setOffset(databaseRequest.getStartIndex());
              out.writeObject(response);
              // xstream.alias("count", Object.class, Integer.class);
              // out.writeObject(databaseRequest.getCount());
              // xstream.alias("offset", Object.class, Integer.class);
              // out.writeObject(databaseRequest.getStartIndex());

              out.flush();
            }
          }
          catch (SitoolsException esql) {
            Logger.getLogger(DataSetExplorerResource.class.getName()).log(Level.SEVERE, null, esql);
            throw new ResourceException(Status.SERVER_ERROR_INTERNAL, "dataset.records.sql", esql);
          }
          catch (IOException exml) {
            Logger.getLogger(DataSetExplorerResource.class.getName()).log(Level.SEVERE, null, exml);
            throw new ResourceException(Status.SERVER_ERROR_INTERNAL, "dataset.records.xml", exml);
          }
          finally {
            if (out != null) {
              out.close();
            }
          }
        }
        else if (this.getMediaType().isCompatible(MediaType.APPLICATION_JSON)) {

          // ResultSet >> Record >> Converters >> Record >> JSON

          PrintStream out = new PrintStream(arg0);
          int nbRecordsSent = 0;
          try {
            // Checking if limit = 0 or -1 is present
            // boolean isShortResponse = false;
            // if (this.datasetExplorerResource.getQuery().getFirst("limit") != null) {
            // String param = this.datasetExplorerResource.getQuery().getFirst("limit").getValue();
            // // limit = 0 case : sending only total count
            // if (param.equals("0")) {
            // isShortResponse = true;
            // out.print("{\"success\": true");
            // if (this.databaseRequest.isCountDone()) {
            // out.println(",");
            // out.println("\"totalCount\":" + databaseRequest.getTotalCount());
            // }
            // out.println("}");
            // out.flush();
            // return;
            // }
            // }
            out.println("{\"success\": true,");
            if (this.databaseRequest.isCountDone()) {
              out.println("\"total\":" + databaseRequest.getTotalCount() + ",");
            }

            // if (!isShortResponse) {

            out.println("\"data\":[");

            boolean isNext = false;

            

            while (databaseRequest.nextResult()) {

              if (isNext) {
                out.println(",");
              }
              else {
                isNext = true;
              }

              // ResultSet >> Record
              Record rec = databaseRequest.getRecord();

              // Record >> Converters >> Record
              if (converterChained != null) {
                rec = converterChained.getConversionOf(rec);
              }

              // Record >> JSON
              out.print(this.getJson(rec).toString());

              // if (!databaseRequest.isLastResult()) {
              // out.println(",");
              // }
              out.flush();
              nbRecordsSent++;
            }
           
            // }
          }

          catch (SitoolsException esql) {
            Logger.getLogger(DataSetExplorerResource.class.getName()).log(Level.SEVERE, null, esql);
            throw new ResourceException(Status.SERVER_ERROR_INTERNAL, "dataset.records.sql", esql);
          }
          finally {
            out.println();
            out.println("],");

            out.println("\"count\":" + nbRecordsSent + ",");
            out.println("\"offset\":" + databaseRequest.getStartIndex());
            out.println("}");
            if (out != null) {
              out.close();
            }
          }
        }
        else if (this.getMediaType().isCompatible(MediaType.TEXT_CSV)) {
          try {
            DataSetApplication dsa = this.datasetExplorerUtil.getApplication();
            DBRecordSetExportRepresentation exp = new DBRecordSetExportRepresentation(databaseRequest,
                this.datasetExplorerUtil, dsa, converterChained);
            exp.getStream(arg0);
          }
          finally {
            if (arg0 != null) {
              arg0.close();
            }
          }
        }
        
      }
      catch (SitoolsException sqle) {
        Logger.getLogger(DataSetExplorerResource.class.getName()).log(Level.SEVERE, null, sqle);

        throw new ResourceException(Status.SERVER_ERROR_INTERNAL, "dataset.records.sql", sqle);
      }
      finally {
        if (databaseRequest != null) {
          try {
            databaseRequest.close();
          }
          catch (SitoolsException e) {
            Logger.getLogger(this.getClass().getName()).severe(e.getMessage());
          }
        }
      }
    }
    else {
      throw new ResourceException(Status.CLIENT_ERROR_UNSUPPORTED_MEDIA_TYPE, "Supported media types : json, xml, csv.");
    }
  }

  /**
   * Return a <code>Record</code> in a <code>JSONObject</code> object
   * 
   * @param rec
   *          a Record
   * @return The <code>JSONObject</code> of the current <code>ResultSet</code>
   * @throws SitoolsException
   *           if an error occurs
   */
  private JSONObject getJson(Record rec) throws SitoolsException {
    try {
      JSONObject json = new JSONObject();
      if (rec != null) {

        List<AttributeValue> list = rec.getAttributeValues();
        AttributeValue obj;

        if (databaseRequest.buildURI() != null) {
          json.put("uri", databaseRequest.buildURI());
        }

        for (Iterator<AttributeValue> it = list.iterator(); it.hasNext();) {
          obj = it.next();
          if (obj != null) {
            Object value = (obj.getValue() != null) ? obj.getValue().toString() : obj.getValue();
            json.put(obj.getName(), value);
          }
        }

      }
      return json;
    }
    catch (JSONException ejson) {
      Logger.getLogger(DataSetExplorerResource.class.getName()).log(Level.SEVERE, null, ejson);
      throw new ResourceException(Status.SERVER_ERROR_INTERNAL, "dataset.records.json", ejson);
    }
  }

  /**
   * Gets the filterChained value
   * 
   * @return the filterChained
   */
  public FilterChained getFilterChained() {
    return filterChained;
  }

  /**
   * Sets the value of filterChained
   * 
   * @param filterChained
   *          the filterChained to set
   */
  public void setFilterChained(FilterChained filterChained) {
    this.filterChained = filterChained;
  }

}

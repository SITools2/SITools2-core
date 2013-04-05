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
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.ext.xstream.XstreamRepresentation;
import org.restlet.representation.OutputRepresentation;
import org.restlet.resource.ResourceException;

import com.thoughtworks.xstream.XStream;

import fr.cnes.sitools.common.XStreamFactory;
import fr.cnes.sitools.common.exception.SitoolsException;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.dataset.converter.business.ConverterChained;
import fr.cnes.sitools.dataset.database.DatabaseRequest;
import fr.cnes.sitools.dataset.database.DatabaseRequestFactory;
import fr.cnes.sitools.dataset.database.mongodb.SitoolsAttributeValueConverter;
import fr.cnes.sitools.dataset.filter.business.FilterChained;
import fr.cnes.sitools.datasource.jdbc.model.AttributeValue;
import fr.cnes.sitools.datasource.jdbc.model.Record;

/**
 * Representation of a record
 * 
 * @author jp.boignard (AKKA Technologies)
 */
public final class DBRecordRepresentation extends OutputRepresentation {

  /** Parent resource */
  private DataSetExplorerUtil res = null;

  /**
   * Chain of converters
   */
  private ConverterChained converterChained = null;

  /**
   * Chain of filters
   */
  private FilterChained filterChained = null;

  /** The request */
  private DatabaseRequest databaseRequest;

  /**
   * SQL table Representation
   * 
   * @param mediaType
   *          media type to use
   * @param res
   *          resource to associate
   */
  public DBRecordRepresentation(MediaType mediaType, DataSetExplorerUtil res) {
    super(mediaType);
    this.res = res;
    // db = res.getDataSource();
    // primaryKeys = getPrimaryKeys();

    this.databaseRequest = DatabaseRequestFactory.getDatabaseRequest(res.getDatabaseParams());

    this.converterChained = res.getApplication().getConverterChained();
    this.setFilterChained(res.getApplication().getFilterChained());

    // RequestSql request = RequestFactory.getRequest(res.getDataSource().getDsModel().getDriverClass());
    // List<Column> columns = res.getApplication().getDataSet().getColumnModel();
    // List<Predicat> predicats = res.getPredicats();
    // String sql = "";
    // if ("S".equals(res.getApplication().getDataSet().getQueryType())) {
    // sql += " " + res.getApplication().getDataSet().getSqlQuery();
    // }
    // else {
    // sql += " FROM " + getFromClause();
    // sql += " WHERE 1=1 " + request.getWhereClause(predicats, columns);
    // }
    //
    // this.basicJoin = sql;
    // this.converterChained = res.getApplication().getConverterChained();
    // this.setFilterChained(res.getApplication().getFilterChained());
    //
    // datasource = (SitoolsSQLDataSource) db;

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
    Record record = null;
    try {

      databaseRequest.createRequest();
      boolean first = true;
      // Only one
      while (databaseRequest.nextResult() && first) {
        record = databaseRequest.getRecord(); // , buildURI(rs));
        first = false;
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
          Logger.getLogger(this.getClass().getName()).severe(e.getMessage());
        }
      }
    }

    if (this.getMediaType().isCompatible(MediaType.APPLICATION_JSON)) {
      // Response response = new Response(true, record, Record.class, "record");
      JSONObject root = new JSONObject();
      try {
        if (record != null) {
          root.put("success", true);
          // application du convertisseur en sortie
          if (converterChained != null) {
            record = converterChained.getConversionOf(record);
          }
          JSONObject jo = getJson(record);
          root.put("record", jo);

        }
        else {
          root.put("success", false);
        }
        JsonRepresentation jr = new JsonRepresentation(root);
        arg0.write(jr.getText().getBytes());
      }
      catch (JSONException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      catch (SitoolsException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }

    }
    else if (this.getMediaType().isCompatible(MediaType.APPLICATION_XML)
        || this.getMediaType().isCompatible(MediaType.TEXT_XML)) {

      boolean success = (record == null) ? false : true;

      if (success && converterChained != null) {
        // application du convertisseur en sortie
        record = converterChained.getConversionOf(record);
      }

      Response response = new Response(success, record, Record.class, "record");
      XstreamRepresentation<Response> innerRepresentation = new XstreamRepresentation<Response>(this.getMediaType(),
          response);

      XStream xstream = XStreamFactory.getInstance().getXStream(this.getMediaType(), res.getContext());
      innerRepresentation.setXstream(xstream);

      // ALIAS
      xstream.autodetectAnnotations(true);

      xstream.registerConverter(new SitoolsAttributeValueConverter());

      // xstream.addImplicitCollection(Record.class, "attributeValues");
      // pour supprimer @class sur l'objet data
      if (response.getItemClass() != null) {
        xstream.alias("item", Object.class, response.getItemClass());
      }
      if (response.getItemName() != null) {
        xstream.aliasField(response.getItemName(), Response.class, "item");
      }

      innerRepresentation.write(arg0);
    }

  }

  /**
   * Build of constrains
   * 
   * @param keys
   *          keys to use
   * @return String the final string for constrains
   */

  /**
   * Set the chain of filters
   * 
   * @param filterChained
   *          the chain of filters
   */
  public void setFilterChained(FilterChained filterChained) {
    this.filterChained = filterChained;
  }

  /**
   * Get the chain of filters
   * 
   * @return the chain of filters
   */
  public FilterChained getFilterChained() {
    return filterChained;
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

        json.put("id", rec.getId());

        JSONArray attributesValues = new JSONArray();

        for (Iterator<AttributeValue> it = list.iterator(); it.hasNext();) {
          obj = it.next();
          if (obj != null) {
            attributesValues.put(getAttributeJSON(obj));
          }
        }
        json.put("attributeValues", attributesValues);

      }
      return json;
    }
    catch (JSONException ejson) {
      Logger.getLogger(DataSetExplorerResource.class.getName()).log(Level.SEVERE, null, ejson);
      throw new ResourceException(Status.SERVER_ERROR_INTERNAL, "dataset.records.json", ejson);
    }
  }

  /**
   * Create a {@link JSONObject} from an {@link AttributeValue}
   * 
   * @param obj
   *          the {@link AttributeValue}
   * @return a {@link JSONObject} containing the name and the value of the given {@link AttributeValue}
   * @throws JSONException
   *           if there is an error while creating the {@link JSONObject}
   */
  private JSONObject getAttributeJSON(AttributeValue obj) throws JSONException {
    JSONObject attribute = new JSONObject();
    attribute.put("name", obj.getName());
    Object value = (obj.getValue() != null) ? obj.getValue().toString() : obj.getValue();
    attribute.put("value", value);
    return attribute;
  }
}

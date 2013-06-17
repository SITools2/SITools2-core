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
package fr.cnes.sitools.resources.geojson.representations;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;

import org.restlet.Context;
import org.restlet.data.MediaType;
import org.restlet.ext.json.JsonRepresentation;

import fr.cnes.sitools.common.exception.SitoolsException;
import fr.cnes.sitools.dataset.converter.business.ConverterChained;
import fr.cnes.sitools.dataset.database.DatabaseRequest;
import fr.cnes.sitools.dataset.database.DatabaseRequestFactory;
import fr.cnes.sitools.dataset.database.DatabaseRequestParameters;
import fr.cnes.sitools.dataset.model.DataSet;
import fr.cnes.sitools.datasource.jdbc.model.AttributeValue;
import fr.cnes.sitools.datasource.jdbc.model.Record;
import fr.cnes.sitools.util.Util;

/**
 * Produce a GeoJson representation from a DatabaseRequest, a geometry column
 * and a list of converters
 * 
 * @author m.gond
 */
public class GeoJsonRepresentation extends JsonRepresentation {
  /** the parameters */
  private DatabaseRequestParameters params;
  /** the sva parent */
  private String geometryColName;
  /** The converters to apply */
  private ConverterChained converterChained;
  
  /**
   * Constructor with a DatabaseRequestParameters, a geometryColName and a
   * converterChained
   * 
   * @param params
   *          the DatabaseRequestParameters
   * @param geometryColName
   *          the name of the column containing the geometry data
   * @param converterChained
   *          the converter to apply
   * @param dataset
   *          the dataset, not used yet
   * @param context
   *          the context, not used yet
   */
  public GeoJsonRepresentation(DatabaseRequestParameters params, String geometryColName,
      ConverterChained converterChained, DataSet dataset, Context context) {
    super(MediaType.APPLICATION_JSON);
    this.params = params;
    this.geometryColName = geometryColName;
    this.converterChained = converterChained;
  }

  @Override
  public void write(Writer writer) throws IOException {

    Record rec = null;
    DatabaseRequest databaseRequest = DatabaseRequestFactory.getDatabaseRequest(params);
    try {
      if (params.getDistinct()) {
        databaseRequest.createDistinctRequest();
      }
      else {
        databaseRequest.createRequest();
      }
      writer.write("{");
      writer.write("\"type\":\"FeatureCollection\",");
      // start features
      writer.write("\"totalResults\":" + databaseRequest.getTotalCount() + ",");
      writer.write("\"features\":[");
      try {
        boolean first = true;
        while (databaseRequest.nextResult()) {
          rec = databaseRequest.getRecord();
          if (Util.isSet(converterChained)) {
            rec = converterChained.getConversionOf(rec);
          }
          if (!first) {
            writer.write(",");
          }
          else {
            first = false;
          }
          // creates a geometry and a properties string
          String geometry = new String();
          String properties = new String();
          boolean firstProp = true;
          for (Iterator<AttributeValue> it = rec.getAttributeValues().iterator(); it.hasNext();) {
            AttributeValue attr = it.next();
            if (attr.getName().equals(geometryColName)) {
              geometry += attr.getValue();
            }
            else {
              if (attr.getValue() != null && !attr.getValue().equals("")) {
                if (!firstProp) {
                  properties += ",";
                }
                else {
                  firstProp = false;
                }
                properties += "\"" + attr.getName() + "\":\"" + attr.getValue() + "\"";
              }
            }
          }
          // start feature
          writer.write("{");
          writer.write("\"type\":\"feature\",");
          // start geometry
          writer.write("\"geometry\":");
          writer.write(geometry);
          // end geometry
          writer.write(",");
          // start properties
          writer.write("\"properties\":{");
          writer.write(properties);
          // end properties
          writer.write("}");
          // end feature
          writer.write("}");
        }
        // end features
        writer.write("]");
      }
      catch (SitoolsException e) {
        writer.write("],");
        writer.write("\"error\":{");
        writer.write("\"code\":");
        writer.write("\"message\":" + e.getLocalizedMessage());
        writer.write("}");

      }
      finally {
        writer.write("}");
        if (databaseRequest != null) {
          try {
            databaseRequest.close();
          }
          catch (SitoolsException e) {
            e.printStackTrace();
          }
        }
        if (writer != null) {
          writer.flush();
        }
      }

    }

    catch (SitoolsException e) {
      e.printStackTrace();
    }

  }
}

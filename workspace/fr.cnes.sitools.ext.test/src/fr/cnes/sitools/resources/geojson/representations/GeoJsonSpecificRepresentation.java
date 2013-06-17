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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.Context;
import org.restlet.Request;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Preference;
import org.restlet.data.Status;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;

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
import fr.cnes.sitools.dataset.model.DataSet;
import fr.cnes.sitools.datasource.jdbc.model.AttributeValue;
import fr.cnes.sitools.datasource.jdbc.model.Record;
import fr.cnes.sitools.util.RIAPUtils;
import fr.cnes.sitools.util.Util;

/**
 * Produce a GeoJson representation from a DatabaseRequest, a geometry column
 * and a list of converters
 * 
 * @author m.gond
 */
public class GeoJsonSpecificRepresentation extends JsonRepresentation {
  /** the parameters */
  private DatabaseRequestParameters params;
  /** the sva parent */
  private String geometryColName;
  /** The converters to apply */
  private ConverterChained converterChained;
  /** The url of the dataset containing medias */
  private String datasetMediaUrl = null;
  /** The column alias in the media dataset for the link between the 2 datasets */
  private String columnAliasMedia = null;
  /** The column alias in the entry dataset for the link between the 2 datasets */
  private String columnAliasEntry = null;
  /** The Context */
  private Context context;
  /** The SitoolsSettings */
  private SitoolsSettings settings;
  /** the name of the column used as url for the media (in the media dataset) */
  private String colUrlName;
  /** the name of the column used as type for the media (in the media dataset) */
  private String colTypeName;
  /**
   * the name of the column used as identifier for the media (in the media
   * dataset)
   */
  private String colIdentifierName;
  /** the name of the column used as name for the media (in the media dataset) */
  private String colNameName;

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
   * @param dsEntry
   *          the DataSet containing the entries
   * @param context
   *          the Context
   * @param colUrlName
   *          the name of the column used as url for the media (in the media
   *          dataset)
   * @param colTypeName
   *          the name of the column used as type for the media (in the media
   *          dataset)
   * @param colIdentifierName
   *          the name of the column used as Identifier for the media (in the
   *          media dataset)
   * @param colNameName
   *          the name of the column used as name for the media (in the media
   *          dataset)
   */
  public GeoJsonSpecificRepresentation(DatabaseRequestParameters params,
      String geometryColName, ConverterChained converterChained,
      DataSet dsEntry, Context context, String colUrlName, String colTypeName,
      String colIdentifierName, String colNameName) {
    super(MediaType.APPLICATION_JSON);
    this.params = params;
    this.geometryColName = geometryColName;
    this.converterChained = converterChained;
    this.context = context;
    this.settings = (SitoolsSettings) context.getAttributes().get(
        ContextAttributes.SETTINGS);

    this.colUrlName = colUrlName;
    this.colTypeName = colTypeName;
    this.colIdentifierName = colIdentifierName;
    this.colNameName = colNameName;

    // get the datasetMediaUrl
    List<Column> columns = dsEntry.getColumnModel();
    for (Iterator<Column> iterator = columns.iterator(); iterator.hasNext();) {
      Column column = iterator.next();
      ColumnRenderer columnRenderer = column.getColumnRenderer();
      if (columnRenderer != null
          && columnRenderer.getBehavior() != null
          && (column.getColumnRenderer().getBehavior() == BehaviorEnum.datasetIconLink || column
              .getColumnRenderer().getBehavior() == BehaviorEnum.datasetLink)) {
        datasetMediaUrl = columnRenderer.getDatasetLinkUrl();
        // get the column alias in the entry dataset
        columnAliasEntry = column.getColumnAlias();
        // get the column alias in the media dataset
        columnAliasMedia = columnRenderer.getColumnAlias();
      }
    }

  }

  @Override
  public void write(Writer writer) throws IOException {

    Record rec = null;

    DatabaseRequest databaseRequest = DatabaseRequestFactory
        .getDatabaseRequest(params);
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
      // writer.write("\"totalResults\":" + databaseRequest.getCount() + ",");
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
          String building = new String();
          Object entryKey = null;
          for (Iterator<AttributeValue> it = rec.getAttributeValues()
              .iterator(); it.hasNext();) {
            AttributeValue attr = it.next();
            if (attr.getName().equals(geometryColName)) {
              geometry += attr.getValue();
            }
            else {
              if (attr.getValue() != null && !attr.getValue().equals("")) {
                if (attr.getName().equals(columnAliasEntry)) {
                  entryKey = attr.getValue();
                }
                if (attr.getName().equals("building_identifier")) {
                  building += "\"identifier\":" + attr.getValue() + ",";
                }
                else if (attr.getName().equals("building_peoplenb")) {
                  building += "\"peoplesNb\":" + attr.getValue() + ",";
                }
                else if (attr.getName().equals("building_state")) {
                  building += "\"state\":\"" + attr.getValue() + "\"";
                }
                else {
                  properties += "\"" + attr.getName() + "\":\""
                      + attr.getValue() + "\",";
                }
              }
            }
          }
          properties += "\"building\":{" + building + "},";
          String mediaStr = "";
          if (entryKey != null && datasetMediaUrl != null) {
            String audio = "";
            String video = "";
            String photo = "";
            do {
              // gets the medias
              String url = datasetMediaUrl + "/records?p[0]=RADIO|"
                  + columnAliasMedia + "|" + entryKey;

              Request reqGET = new Request(Method.GET, RIAPUtils.getRiapBase()
                  + url);
              ArrayList<Preference<MediaType>> objectMediaType = new ArrayList<Preference<MediaType>>();
              objectMediaType.add(new Preference<MediaType>(
                  MediaType.APPLICATION_JSON));
              reqGET.getClientInfo().setAcceptedMediaTypes(objectMediaType);
              org.restlet.Response response = null;

              response = context.getClientDispatcher().handle(reqGET);

              if (response == null
                  || Status.isError(response.getStatus().getCode())) {
                RIAPUtils.exhaust(response);
                break;
              }

              @SuppressWarnings("unchecked")
              Representation or = response.getEntity();
              try {
                String txt = or.getText();
                JSONObject jsonMedia = new JSONObject(txt);
                JSONArray jsonMediaList = jsonMedia.getJSONArray("data");
                boolean firstAudio = true;
                boolean firstPhoto = true;
                boolean firstVideo = true;
                for (int i = 0; i < jsonMediaList.length(); i++) {
                  JSONObject media = jsonMediaList.getJSONObject(i);
                  String type = media.getString(colTypeName);
                  if (type.equals("audio")) {
                    if (!firstAudio) {
                      audio += ",";
                    }
                    else {
                      firstAudio = false;
                    }
                    audio += getMediaAsExternalJson(media);
                  }
                  if (type.equals("photo")) {
                    if (!firstPhoto) {
                      photo += ",";
                    }
                    else {
                      firstPhoto = false;
                    }
                    photo += getMediaAsExternalJson(media);
                  }
                  if (type.equals("video")) {
                    if (!firstVideo) {
                      video += ",";
                    }
                    else {
                      firstVideo = false;
                    }
                    video += getMediaAsExternalJson(media);
                  }
                }

                mediaStr += "\"video\":[" + video + "],";
                mediaStr += "\"photo\":[" + photo + "],";
                mediaStr += "\"audio\":[" + audio + "]";
              }
              catch (IOException e) { // marshalling error
                throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
              }
              catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
              }
            } while (false);

          }
          properties += "\"media\":{" + mediaStr + "}";

          // start feature
          writer.write("{");
          writer.write("\"type\":\"Feature\",");
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

  private String getMediaAsExternalJson(JSONObject media) throws JSONException {
    String mediaStr = "";
    mediaStr += "{\"identifier\":\"" + media.getString(colIdentifierName)
        + "\",";
    mediaStr += "\"name\":\"" + media.getString(colNameName) + "\",";
    mediaStr += "\"lat\":\"0\",";
    mediaStr += "\"lon\":\"0\",";
    mediaStr += "\"url\":\"" + this.settings.getPublicHostDomain()
        + media.getString(colUrlName) + "\"}";
    return mediaStr;
  }
}

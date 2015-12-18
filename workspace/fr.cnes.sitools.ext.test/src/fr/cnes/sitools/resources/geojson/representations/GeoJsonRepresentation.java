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
package fr.cnes.sitools.resources.geojson.representations;

import fr.cnes.sitools.common.exception.SitoolsException;
import fr.cnes.sitools.dataset.converter.business.ConverterChained;
import fr.cnes.sitools.dataset.database.DatabaseRequest;
import fr.cnes.sitools.dataset.database.DatabaseRequestFactory;
import fr.cnes.sitools.dataset.database.DatabaseRequestParameters;
import fr.cnes.sitools.dataset.model.Column;
import fr.cnes.sitools.dataset.model.DataSet;
import fr.cnes.sitools.datasource.jdbc.model.AttributeValue;
import fr.cnes.sitools.datasource.jdbc.model.Record;
import fr.cnes.sitools.util.Util;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.restlet.Context;
import org.restlet.data.MediaType;
import org.restlet.representation.WriterRepresentation;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

/**
 * Produce a GeoJson representation from a DatabaseRequest, a geometry column
 * and a list of converters
 *
 * @author m.gond
 */
public class GeoJsonRepresentation extends WriterRepresentation {
    private final String primaryKey;
    /** the parameters */
    private DatabaseRequestParameters params;
    /** the sva parent */
    private String geometryColName;
    /** The converters to apply */
    private ConverterChained converterChained;

    private String quicklookColumn;
    private String thumbnailColumn;
    private String downloadColumn;
    private String mimeTypeColumn;

    /** The Context */
    private Context context;

    private static ObjectMapper mapper = new ObjectMapper(); // can reuse, share

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
        this.primaryKey = getPrimaryKeyColumnName(dataset);
        this.context = context;
    }

    public GeoJsonRepresentation(DatabaseRequestParameters params, String geometryColName, ConverterChained converterChained, DataSet dataset, Context context, String quicklookColumn, String thumbnailColumn, String downloadColumn, String mimeTypeColumn) {
        this(params, geometryColName, converterChained, dataset, context);
        this.downloadColumn = downloadColumn;
        this.thumbnailColumn = thumbnailColumn;
        this.quicklookColumn = quicklookColumn;
        this.mimeTypeColumn = mimeTypeColumn;
    }


    /**
     * Retrieve the primary key column name from the given dataset
     * @param dataset the DataSet
     * @return the primary key column alias or null if not found
     */
    protected String getPrimaryKeyColumnName(DataSet dataset) {
        String primaryColumnName = null;
        List<Column> columns = dataset.getColumnModel();
        for (Column column : columns) {
            if (column.isPrimaryKey()) {
                primaryColumnName = column.getColumnAlias();
                break;
            }
        }
        return primaryColumnName;
    }

    @Override
    public void write(Writer writer) throws IOException {
        Record rec = null;
        DatabaseRequest databaseRequest = DatabaseRequestFactory.getDatabaseRequest(params);

        JsonFactory jFactory = new JsonFactory();
        JsonGenerator jGenerator = null;
        mapper.getSerializationConfig().setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);

        jGenerator = jFactory.createJsonGenerator(writer);

        try {
            createDbRequest(databaseRequest);

            jGenerator.writeStartObject();
            jGenerator.writeStringField("type", "FeatureCollection");

            writeProperties(databaseRequest, jGenerator);
            writeFeatures(databaseRequest, jGenerator);

            jGenerator.writeEndObject();

        } catch (SitoolsException e) {

            writeError(jGenerator, e);
            jGenerator.flush();
            // end global object
            writer.flush();
        } finally {

            jGenerator.flush();
            // end global object
            writer.flush();

            if (databaseRequest != null) {
                try {
                    databaseRequest.close();
                } catch (SitoolsException e) {
                    context.getLogger().log(Level.SEVERE, "Cannot close database request", e);
                }
            }
        }
    }

    protected void writeError(JsonGenerator jGenerator, SitoolsException e) throws IOException {
        jGenerator.writeStartObject();
        jGenerator.writeObjectFieldStart("error");
        jGenerator.writeStringField("error", e.getLocalizedMessage());
        jGenerator.writeStringField("code", "500");
        //end error
        jGenerator.writeEndObject();
        //end object
        jGenerator.writeEndObject();
    }

    protected void writeFeatures(DatabaseRequest databaseRequest, JsonGenerator jGenerator) throws IOException, SitoolsException {
        Record rec;// Features
        jGenerator.writeArrayFieldStart("features");

        Object downloadUrl = null;
        Object mimeType = null;

        while (databaseRequest.nextResult()) {
            rec = databaseRequest.getRecord();
            if (Util.isSet(converterChained)) {
                rec = converterChained.getConversionOf(rec);
            }
          GeoJsonFeatureDTO geojson = new GeoJsonFeatureDTO();

          Map<String,Object> properties = geojson.getProperties();

            for (Iterator<AttributeValue> it = rec.getAttributeValues().iterator(); it.hasNext(); ) {
                AttributeValue attr = it.next();
                if (attr.getName().equals(geometryColName)) {
                    JsonNode geo = mapper.readTree(attr.getValue().toString());
                    geojson.setGeometry(geo);
                } else if (attr.getName().equals(this.primaryKey)) {
                    geojson.setId(attr.getValue().toString());
                    properties.put(attr.getName(), attr.getValue());
                } else if (attr.getName().equals(this.downloadColumn)) {
                  downloadUrl = attr.getValue();
                } else if (attr.getName().equals(this.mimeTypeColumn)) {
                  mimeType = attr.getValue();
                } else if (attr.getName().equals(this.thumbnailColumn)) {
                    properties.put("thumbnail", attr.getValue());
                } else if (attr.getName().equals(this.quicklookColumn)) {
                  properties.put("quicklook", attr.getValue());
                } else {
                  properties.put(attr.getName(), attr.getValue());
                }
            }
            // end properties

          if(downloadUrl!=null && mimeType!=null) {
            DownloadServiceDTO downloadServiceDTO = new DownloadServiceDTO();
            downloadServiceDTO.setUrl(downloadUrl.toString());
            downloadServiceDTO.setMimetype(mimeType.toString());
            geojson.getServices().put("download", downloadServiceDTO);
          }

          mapper.writeValue(jGenerator, geojson);
            // end feature
        }

        //end features
        jGenerator.writeEndArray();

    }

    protected void doWriteValue(JsonGenerator jGenerator, String name, Object value) throws IOException {
        jGenerator.writeFieldName(name);
        mapper.writeValue(jGenerator, value);
    }

    protected void writeProperties(DatabaseRequest databaseRequest, JsonGenerator jGenerator) throws IOException {
        jGenerator.writeNumberField("totalResults", databaseRequest.getCount());
        jGenerator.writeObjectFieldStart("properties");
        jGenerator.writeNumberField("totalResults", databaseRequest.getCount());
        jGenerator.writeEndObject();
    }

    protected void createDbRequest(DatabaseRequest databaseRequest) throws SitoolsException {
        if (params.getDistinct()) {
            databaseRequest.createDistinctRequest();
        } else {
            databaseRequest.createRequest();
        }
    }
}



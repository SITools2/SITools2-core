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
import org.codehaus.jackson.map.ObjectMapper;
import org.restlet.Context;
import org.restlet.data.MediaType;
import org.restlet.representation.WriterRepresentation;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.List;
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

        jGenerator = jFactory.createJsonGenerator(writer);

        try {
            createDbRequest(databaseRequest);

            startWriting(jGenerator);

            writeProperties(databaseRequest, jGenerator);
            writeFeatures(databaseRequest, jGenerator);

        } catch (SitoolsException e) {

            writeError(jGenerator, e);
            jGenerator.flush();
            // end global object
            writer.flush();
        } finally {
            jGenerator.writeEndArray();
            // end features
            jGenerator.writeEndObject();
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

        Object geometry = null;
        Object primaryKeyValue = null;
        Object downloadUrl = null;
        Object mimeType = null;

        while (databaseRequest.nextResult()) {
            rec = databaseRequest.getRecord();
            if (Util.isSet(converterChained)) {
                rec = converterChained.getConversionOf(rec);
            }

            // feature
            jGenerator.writeStartObject();
            jGenerator.writeStringField("type", "Feature");

            // properties
            jGenerator.writeObjectFieldStart("properties");

            for (Iterator<AttributeValue> it = rec.getAttributeValues().iterator(); it.hasNext(); ) {
                AttributeValue attr = it.next();
                if (attr.getName().equals(geometryColName)) {
                    geometry = attr.getValue();
                } else if (attr.getName().equals(this.primaryKey)) {
                    primaryKeyValue = attr.getValue();
                    this.doWriteValue(jGenerator, attr.getName(), attr.getValue());
                } else if (attr.getName().equals(this.downloadColumn)) {
                    downloadUrl = attr.getValue();
                } else if (attr.getName().equals(this.mimeTypeColumn)) {
                    mimeType = attr.getValue();
                } else if (attr.getName().equals(this.thumbnailColumn)) {
                    this.doWriteValue(jGenerator, "thumbnail", attr.getValue());
                } else if (attr.getName().equals(this.quicklookColumn)) {
                    this.doWriteValue(jGenerator, "quicklook", attr.getValue());
                } else {
                    this.doWriteValue(jGenerator, attr.getName(), attr.getValue());
                }
            }
            jGenerator.writeEndObject();
            // end properties

            if (Util.isSet(primaryKeyValue)) {
                // id
                jGenerator.writeFieldName("id");
                mapper.writeValue(jGenerator, primaryKeyValue);
            }
            if (Util.isSet(geometry)) {
                // geometry
                jGenerator.writeFieldName("geometry");
                jGenerator.writeRawValue(geometry.toString());
            }

            if (Util.isSet(downloadUrl) && Util.isSet(mimeType)) {
                //start services
                jGenerator.writeObjectFieldStart("services");
                //start download
                jGenerator.writeObjectFieldStart("download");
                this.doWriteValue(jGenerator, "mimetype", mimeType);
                this.doWriteValue(jGenerator, "url", downloadUrl);
                //end download
                jGenerator.writeEndObject();
                //end services
                jGenerator.writeEndObject();
            }

            jGenerator.writeEndObject();
            // end feature
        }

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

    protected void startWriting(JsonGenerator jGenerator) throws IOException {
        jGenerator.writeStartObject();
        jGenerator.writeStringField("type", "FeatureCollection");
    }

    protected void createDbRequest(DatabaseRequest databaseRequest) throws SitoolsException {
        if (params.getDistinct()) {
            databaseRequest.createDistinctRequest();
        } else {
            databaseRequest.createRequest();
        }
    }
}



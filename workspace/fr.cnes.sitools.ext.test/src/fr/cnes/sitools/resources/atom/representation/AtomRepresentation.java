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
package fr.cnes.sitools.resources.atom.representation;

import fr.cnes.sitools.common.exception.SitoolsException;
import fr.cnes.sitools.dataset.converter.business.ConverterChained;
import fr.cnes.sitools.dataset.database.DatabaseRequest;
import fr.cnes.sitools.dataset.database.DatabaseRequestFactory;
import fr.cnes.sitools.dataset.database.DatabaseRequestParameters;
import fr.cnes.sitools.dataset.model.Column;
import fr.cnes.sitools.dataset.model.DataSet;
import fr.cnes.sitools.datasource.jdbc.model.AttributeValue;
import fr.cnes.sitools.datasource.jdbc.model.Record;
import fr.cnes.sitools.util.DateUtils;
import fr.cnes.sitools.util.Util;
import org.codehaus.jackson.map.ObjectMapper;
import org.restlet.Context;
import org.restlet.data.MediaType;
import org.restlet.representation.WriterRepresentation;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.text.ParseException;
import java.util.*;
import java.util.logging.Level;

/**
 * Produce a GeoJson representation from a DatabaseRequest, a geometry column
 * and a list of converters
 *
 * @author m.gond
 */
public class AtomRepresentation extends WriterRepresentation {
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

    private String titleColumn;
    private String descriptionColumn;
    private String publishedColumn;
    private String updatedColumn;


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
    public AtomRepresentation(DatabaseRequestParameters params, String geometryColName,
        ConverterChained converterChained, DataSet dataset, Context context) {
        super(MediaType.APPLICATION_ATOM);
        this.params = params;
        this.geometryColName = geometryColName;
        this.converterChained = converterChained;
        this.primaryKey = getPrimaryKeyColumnName(dataset);
        this.context = context;
    }

    public AtomRepresentation(DatabaseRequestParameters params, String geometryColName,
        ConverterChained converterChained, DataSet dataset, Context context, String quicklookColumn,
        String thumbnailColumn, String downloadColumn, String mimeTypeColumn, String titleColumn,
        String descriptionColumn, String publishedColumn, String updatedColumn) {
        this(params, geometryColName, converterChained, dataset, context);
        this.downloadColumn = downloadColumn;
        this.thumbnailColumn = thumbnailColumn;
        this.quicklookColumn = quicklookColumn;
        this.mimeTypeColumn = mimeTypeColumn;

        this.titleColumn = titleColumn;
        this.descriptionColumn = descriptionColumn;
        this.publishedColumn = publishedColumn;
        this.updatedColumn = updatedColumn;
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


        writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        writer.write(
            "<feed xml:lang=\"en\" xmlns=\"http://www.w3.org/2005/Atom\" xmlns:time=\"http://a9.com/-/opensearch/extensions/time/1.0/\" xmlns:os=\"http://a9.com/-/spec/opensearch/1.1/\" xmlns:dc=\"http://purl.org/dc/elements/1.1/\" xmlns:georss=\"http://www.georss.org/georss\" xmlns:gml=\"http://www.opengis.net/gml\" xmlns:geo=\"http://a9.com/-/opensearch/extensions/geo/1.0/\" xmlns:eo=\"http://a9.com/-/opensearch/extensions/eo/1.0/\" xmlns:metalink=\"urn:ietf:params:xml:ns:metalink\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" xmlns:media=\"http://search.yahoo.com/mrss/\">\n");

        try {
            createDbRequest(databaseRequest);
            writeHeader(databaseRequest, writer, params.getDataset());
            writeFeatures(databaseRequest, writer);

        }
        catch (SitoolsException e) {

            // end global object
            writer.flush();
        }
        finally {
            writer.write("</feed>");
            // end global object
            writer.flush();

            if (databaseRequest != null) {
                try {
                    databaseRequest.close();
                }
                catch (SitoolsException e) {
                    context.getLogger().log(Level.SEVERE, "Cannot close database request", e);
                }
            }
        }
    }

    protected void writeFeatures(DatabaseRequest databaseRequest, Writer writer) throws IOException, SitoolsException {
        Record rec;// Features

        Object downloadUrl = null;
        Object mimeType = null;

        while (databaseRequest.nextResult()) {
            rec = databaseRequest.getRecord();
            if (Util.isSet(converterChained)) {
                rec = converterChained.getConversionOf(rec);
            }

            AtomEntryDTO atomEntryDTO = new AtomEntryDTO();

            //            Map<String,Object> properties = atomEntryDTO.getProperties();
            Map<String, Object> properties = new HashMap<String, Object>();


            for (Iterator<AttributeValue> it = rec.getAttributeValues().iterator(); it.hasNext(); ) {
                AttributeValue attr = it.next();
                if (attr.getName().equals(geometryColName)) {
                    atomEntryDTO.setGeometry(attr.getValue().toString());
                }
                else if (attr.getName().equals(this.primaryKey)) {
                    atomEntryDTO.setId(attr.getValue().toString());
                    properties.put(attr.getName(), attr.getValue());
                }
                else if (attr.getName().equals(this.downloadColumn)) {
                    atomEntryDTO.setDownload(attr.getValue().toString());
                }
                else if (attr.getName().equals(this.mimeTypeColumn)) {
                    atomEntryDTO.setDownloadMimeType(attr.getValue().toString());
                }
                else if (attr.getName().equals(this.thumbnailColumn)) {
                    atomEntryDTO.setThumbnail(attr.getValue().toString());
                }
                else if (attr.getName().equals(this.quicklookColumn)) {
                    atomEntryDTO.setQuicklook(attr.getValue().toString());
                }
                else if (attr.getName().equals(titleColumn)) {
                    atomEntryDTO.setTitle(attr.getValue().toString());
                }
                else if (attr.getName().equals(descriptionColumn)) {
                    atomEntryDTO.setDescription(attr.getValue().toString());
                }
                else if (attr.getName().equals(publishedColumn)) {
                    Date date = null;
                    try {
                        date = DateUtils.parse(attr.getValue().toString());
                        atomEntryDTO.setPublished(DateUtils.format(date, DateUtils.FORMAT_RFC_3339));
                    }
                    catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                else if (attr.getName().equals(updatedColumn)) {
                    Date date = null;
                    try {
                        date = DateUtils.parse(attr.getValue().toString());
                        atomEntryDTO.setUpdated(DateUtils.format(date, DateUtils.FORMAT_RFC_3339));
                    }
                    catch (ParseException e) {
                        e.printStackTrace();
                    }
                }

                else {
                    properties.put(attr.getName(), attr.getValue());
                }
            }

            writeEntry(writer, atomEntryDTO);
            // end properties
        }


    }

    protected void writeHeader(DatabaseRequest databaseRequest, Writer writer, DataSet dataset) throws IOException {
        StringWriter header = new StringWriter();

        header.write("\t<os:startIndex>" + databaseRequest.getStartIndex() + "</os:startIndex>\n");
        header.write("\t<os:itemsPerPage>" + databaseRequest.getCount() + "</os:itemsPerPage>\n");
        header.write("\t<os:totalResults>" + databaseRequest.getTotalCount() + "</os:totalResults>\n");
        header.write("\t<title>" + dataset.getName() + "</title>\n");
        header.write("\t<id>" + dataset.getId() + "</id>\n");
        header.write("\t<updated>" + DateUtils.format(dataset.getLastStatusUpdate(), DateUtils.FORMAT_RFC_3339)
            + "</updated>\n");

        writer.write(header.toString());
    }

    private void writeEntry(Writer writer, AtomEntryDTO atomEntryDTO) throws IOException {

        StringWriter entry = new StringWriter();
        entry.write("\t<entry>\n");

        entry.write("\t\t<id>" + atomEntryDTO.getId() + "</id>\n");
        entry.write("\t\t<title>" + atomEntryDTO.getTitle() + "</title>\n");
        entry.write("\t\t<published>" + atomEntryDTO.getPublished() + "</published>\n");
        entry.write("\t\t<updated>" + atomEntryDTO.getUpdated() + "</updated>\n");
        entry.write("\t\t<summary type='text'>" + atomEntryDTO.getDescription() + "</summary>\n");
        entry.write("\t\t" + atomEntryDTO.getGeometry() + "\n");


        if (atomEntryDTO.getThumbnail() != null && atomEntryDTO.getQuicklook() != null) {
            entry.write("\t\t<media:group>\n");
            if (atomEntryDTO.getThumbnail() != null) {
                entry.write("\t\t\t<media:content url=\"" + atomEntryDTO.getThumbnail() + "\" medium=\"image\">\n");
                entry.write(
                    "\t\t\t\t<media:category scheme=\"http://www.opengis.net/spec/EOMPOM/1.0\">THUMNAIL</media:category>\n");
                entry.write("\t\t\t</media:content>\n");
            }
            if (atomEntryDTO.getQuicklook() != null) {
                entry.write("\t\t\t<media:content url=\"" + atomEntryDTO.getQuicklook() + "\" medium=\"image\">\n");
                entry.write(
                    "\t\t\t\t<media:category scheme=\"http://www.opengis.net/spec/EOMPOM/1.0\">QUICKLOOK</media:category>\n");
                entry.write("\t\t\t</media:content>\n");
            }
            entry.write("\t\t</media:group>\n");
        }
        if (atomEntryDTO.getDownload() != null && atomEntryDTO.getDownloadMimeType() != null) {
            entry.write(
                "\t\t<link rel=\"enclosure\" type=\"" + atomEntryDTO.getDownloadMimeType() + "\" length=\"0\" href=\""
                    + atomEntryDTO.getDownload() + "\"/>\n");
        }

        entry.write("\t</entry>\n");

        writer.write(entry.toString());
    }


    protected void createDbRequest(DatabaseRequest databaseRequest) throws SitoolsException {
        if (params.getDistinct()) {
            databaseRequest.createDistinctRequest();
        }
        else {
            databaseRequest.createRequest();
        }
    }
}



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
package fr.cnes.sitools.resources.atom;

import fr.cnes.sitools.common.resource.SitoolsParameterizedResource;
import fr.cnes.sitools.dataset.DataSetApplication;
import fr.cnes.sitools.dataset.database.DatabaseRequestParameters;
import fr.cnes.sitools.dataset.database.common.DataSetExplorerUtil;
import fr.cnes.sitools.dataset.model.Column;
import fr.cnes.sitools.dataset.model.DataSet;
import fr.cnes.sitools.dataset.model.SpecificColumnType;
import fr.cnes.sitools.plugins.resources.model.ResourceParameter;
import fr.cnes.sitools.resources.atom.representation.AtomRepresentation;
import fr.cnes.sitools.resources.geojson.representations.GeoJsonRepresentation;
import org.restlet.Context;
import org.restlet.data.Disposition;
import org.restlet.data.MediaType;
import org.restlet.ext.wadl.MethodInfo;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Get;

import java.util.ArrayList;
import java.util.List;

/**
 * Classic implementation of JeoSearch resource
 *
 *
 * @author m.gond
 */
public class AtomPostGisResource extends SitoolsParameterizedResource {

    @Override
    public void sitoolsDescribe() {
        setName("AtomPostGisResource");
        setDescription("Export dataset records with ATOM format");
    }

    /**
     * Get GeoJSON
     *
     * @return Representation the GeoJSON result
     */
    @Get
    public Representation get() {
        return execute();
    }

    @Override
    protected void describeGet(MethodInfo info) {
        this.addInfo(info);
        info.setIdentifier("retrieve_records and format it as ATOM");
        info.setDocumentation("Method to retrieve records of a dataset and format it as ATOM");
        addStandardGetRequestInfo(info);
        DataSetExplorerUtil.addDatasetExplorerGetRequestInfo(info);
        DataSetApplication application = (DataSetApplication) getApplication();
        DataSetExplorerUtil.addDatasetExplorerGetFilterInfo(info, application.getFilterChained());
        addStandardResponseInfo(info);
        addStandardInternalServerErrorInfo(info);
    }

    @Override
    protected Representation head(Variant variant) {
        Representation repr = super.head();
        repr.setMediaType(MediaType.APPLICATION_JSON);
        return repr;
    }

    /**
     * Execute the request and return a Representation
     *
     * @return the HTML representation
     */
    private Representation execute() {
        Representation repr = null;

        // Get context
        Context context = getContext();

        // generate the DatabaseRequest
        DataSetApplication datasetApp = (DataSetApplication) getApplication();
        DataSetExplorerUtil dsExplorerUtil = new DataSetExplorerUtil(datasetApp, getRequest(), getContext());

        // Get request parameters
        if (datasetApp.getConverterChained() != null) {
            datasetApp.getConverterChained().getContext().getAttributes().put("REQUEST", getRequest());
        }

        // Get DatabaseRequestParameters
        DatabaseRequestParameters params = dsExplorerUtil.getDatabaseParams();

        ResourceParameter geometryColumnParam = this.getModel().getParameterByName(AtomPostGisResourceModel.GEOMETRY_COLUMN);
        String geometryColName = geometryColumnParam.getValue();

        // modify the sql visible columns to return some geoJSON
        List<Column> col = new ArrayList<Column>(params.getSqlVisibleColumns());
        List<Column> columnModel = params.getDataset().getColumnModel();
        for (Column column : columnModel) {
            if (column.getColumnAlias().equals(geometryColName)) {
                Column geoCol = new Column();
                // remove the coordinates column from the visible columns,
                // to have only the geoJson column

                for (Column columnVisible : col) {
                    if (columnVisible.getColumnAlias().equals(geometryColName)) {
                        col.remove(columnVisible);
                        break;
                    }
                }

                // then add the column with the GeoJSON export
                geoCol.setColumnAlias(column.getColumnAlias());
                // Ne doit pas servir normalement
                // geoCol.setColumnAliasDetail(column.getColumnAliasDetail());
                geoCol.setDataIndex("ST_AsGML(" + column.getTableName() + "." + column.getDataIndex() + ")");
                geoCol.setSpecificColumnType(SpecificColumnType.SQL);
                geoCol.setJavaSqlColumnType(column.getJavaSqlColumnType());
                geoCol.setVisible(true);
                col.add(geoCol);
                break;
            }
        }
        params.setSqlVisibleColumns(col);

        DataSet dataset = datasetApp.getDataSet();

        ResourceParameter quicklookColumnParam = this.getModel().getParameterByName(AtomPostGisResourceModel.QUICKLOOK_COLUMN);
        String quicklookCol = quicklookColumnParam.getValue();
        ResourceParameter thumbnailColumnParam = this.getModel().getParameterByName(AtomPostGisResourceModel.THUMBNAIL_COLUMN);
        String thumbnailColName = thumbnailColumnParam.getValue();
        ResourceParameter downloadColumnParam = this.getModel().getParameterByName(AtomPostGisResourceModel.DOWNLOAD_COLUMN);
        String downloadColName = downloadColumnParam.getValue();
        ResourceParameter mimeTypeColumnParam = this.getModel().getParameterByName(AtomPostGisResourceModel.MIME_TYPE_COLUMN);
        String mimeType = mimeTypeColumnParam.getValue();

        //ATOM PARAMETERS
        ResourceParameter titleParam = this.getModel().getParameterByName(AtomPostGisResourceModel.TITLE_COLUMN);
        String title = titleParam.getValue();
        ResourceParameter descriptionParam = this.getModel().getParameterByName(AtomPostGisResourceModel.DESCRIPTION_COLUMN);
        String description = descriptionParam.getValue();
        ResourceParameter publishedParam = this.getModel().getParameterByName(AtomPostGisResourceModel.PUBLISHED_COLUMN);
        String published = publishedParam.getValue();
        ResourceParameter updatedParam = this.getModel().getParameterByName(AtomPostGisResourceModel.UPDATED_COLUMN);
        String updated = updatedParam.getValue();

        repr = new AtomRepresentation(params, geometryColName, datasetApp.getConverterChained(), dataset, getContext(), quicklookCol, thumbnailColName, downloadColName, mimeType, title, description, published, updated);

        if (fileName != null && !"".equals(fileName)) {
            Disposition disp = new Disposition(Disposition.TYPE_ATTACHMENT);

            disp.setFilename(fileName);
            repr.setDisposition(disp);
        }
        return repr;
    }

}

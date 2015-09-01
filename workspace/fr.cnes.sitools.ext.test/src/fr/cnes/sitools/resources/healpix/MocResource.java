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
package fr.cnes.sitools.resources.healpix;

import cds.moc.HealpixMoc;
import cds.moc.MocCell;
import fr.cnes.sitools.common.exception.SitoolsException;
import fr.cnes.sitools.common.resource.SitoolsParameterizedResource;
import fr.cnes.sitools.dataset.DataSetApplication;
import fr.cnes.sitools.dataset.converter.business.ConverterChained;
import fr.cnes.sitools.dataset.database.DatabaseRequest;
import fr.cnes.sitools.dataset.database.DatabaseRequestFactory;
import fr.cnes.sitools.dataset.database.DatabaseRequestParameters;
import fr.cnes.sitools.dataset.database.common.DataSetExplorerUtil;
import fr.cnes.sitools.dataset.model.Column;
import fr.cnes.sitools.dataset.model.DataSet;
import fr.cnes.sitools.dataset.model.SpecificColumnType;
import fr.cnes.sitools.dataset.model.geometry.LngLatAlt;
import fr.cnes.sitools.dataset.model.geometry.Point;
import fr.cnes.sitools.dataset.model.geometry.Polygon;
import fr.cnes.sitools.datasource.jdbc.model.AttributeValue;
import fr.cnes.sitools.datasource.jdbc.model.Record;
import fr.cnes.sitools.plugins.resources.model.ResourceParameter;
import fr.cnes.sitools.resources.geojson.GeoJSONPostGisResourceModel;
import fr.cnes.sitools.util.Util;
import fr.cnes.sitools.utils.wkt.WKTReader;
import healpix.core.HealpixIndex;
import healpix.core.base.set.LongRangeSet;
import healpix.essentials.Pointing;
import healpix.essentials.Scheme;
import healpix.essentials.Vec3;
import healpix.tools.SpatialVector;
import org.restlet.Context;
import org.restlet.data.MediaType;
import org.restlet.ext.wadl.MethodInfo;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.representation.Variant;
import org.restlet.resource.Get;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Classic implementation of JeoSearch resource
 *
 *
 * @author m.gond
 */
public class MocResource extends SitoolsParameterizedResource {

    private static final String GEO_COLUMN_NAME = "sitools_geo_column";

    private static final int DEFAULT_ORDER = 12;

    private int nside;
    private int order;

    @Override
    public void sitoolsDescribe() {
        setName("MocResource");
        setDescription("MocResource");
    }


    @Override
    public void doInit() {
        super.doInit();
        ResourceParameter geometryColumnParam = this.getModel().getParameterByName(MocResourceModel.ORDER_PARAM);
        if(!Util.isEmpty(geometryColumnParam.getValue())) {
            order = Integer.parseInt(geometryColumnParam.getValue());
        }
        else {
            order = DEFAULT_ORDER;
        }
        nside = (int) Math.pow(2, order);
    }

    /**
     * Get GeoJSON
     *
     * @return Representation the GeoJSON result
     */
    @Get
    public Representation get() {
        Logger logger = getContext().getLogger();

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

        ResourceParameter geometryColumnParam = this.getModel().getParameterByName(MocResourceModel.GEOMETRY_COLUMN);
        String geometryColName = geometryColumnParam.getValue();

        // modify the sql visible columns to return some geoJSON
        List<Column> col = new ArrayList<Column>(params.getSqlVisibleColumns());
        List<Column> columnModel = params.getDataset().getColumnModel();
        for (Column column : columnModel) {
            if (column.getColumnAlias().equals(geometryColName)) {
                Column geoCol = new Column();
                // then add the column with the GeoJSON export
                geoCol.setColumnAlias(GEO_COLUMN_NAME);
                // Ne doit pas servir normalement
                // geoCol.setColumnAliasDetail(column.getColumnAliasDetail());
                geoCol.setDataIndex("ST_AsText(" + column.getTableName() + "." + column.getDataIndex() + ")");
                geoCol.setSpecificColumnType(SpecificColumnType.SQL);
                geoCol.setJavaSqlColumnType(column.getJavaSqlColumnType());
                geoCol.setVisible(true);
                col.add(geoCol);
                break;
            }
        }
        params.setSqlVisibleColumns(col);

        DataSet dataset = datasetApp.getDataSet();

        DatabaseRequest databaseRequest = DatabaseRequestFactory.getDatabaseRequest(params);
        Record rec = null;
        HealpixMoc moc = new HealpixMoc();
        try {
            if (params.getDistinct()) {
                databaseRequest.createDistinctRequest();
            } else {
                databaseRequest.createRequest();
            }

            ConverterChained converterChained = datasetApp.getConverterChained();

            HealpixIndex index = new HealpixIndex(nside, Scheme.NESTED);

            while (databaseRequest.nextResult()) {

                rec = databaseRequest.getRecord();
                if (Util.isSet(converterChained)) {
                    rec = converterChained.getConversionOf(rec);
                }

                for (AttributeValue attr : rec.getAttributeValues()) {
                    if (attr.getName().equals(GEO_COLUMN_NAME)) {
                        //Parse WKT
                        String wkt = (String) attr.getValue();
                        if (wkt.startsWith("POLYGON")) {
                            Polygon polygon = WKTReader.parsePolygon(wkt);
                            List<LngLatAlt> coords = polygon.getExteriorRing();

                            ArrayList<SpatialVector> vectors = new ArrayList<SpatialVector>();

                            for (LngLatAlt coord : coords) {
                                SpatialVector spatialVector = new SpatialVector();
                                double longitude = coord.getLongitude();
                                if (longitude < 0) {
                                    longitude += 360;
                                }
                                double lat = coord.getLatitude();
                                spatialVector.set(longitude, lat);
                                vectors.add(spatialVector);
                            }
                            LongRangeSet rangeSet = index.query_polygon(nside, vectors, 1, 1);

                            for (long pix : rangeSet) {
                                final MocCell mocCell = new MocCell();
                                mocCell.set(order, pix);
                                moc.add(mocCell);
                            }
                        }

                        if(wkt.startsWith("POINT")) {

                            Point point = WKTReader.parsePoint(wkt);
                            double longitude = point.getLongitude();
                            if (longitude < 0) {
                                longitude += 360;
                            }
                            double lat = point.getLatitude();

                            SpatialVector vec = new SpatialVector();
                            vec.set(longitude, lat);
                            Pointing pointing = new Pointing(vec);

                            long pix = index.ang2pix_nest(pointing.theta, pointing.phi);
                            final MocCell mocCell = new MocCell();
                            mocCell.set(order, pix);
                            moc.add(mocCell);
                        }
                        break;
                    }
                }
            }
        } catch (SitoolsException e) {
            logger.log(Level.WARNING, e.getMessage(), e);
        } catch (Exception e) {
            logger.log(Level.WARNING, e.getMessage(), e);
        } finally {
            if (databaseRequest != null) {
                try {
                    databaseRequest.close();
                } catch (SitoolsException e) {
                    context.getLogger().log(Level.SEVERE, "Cannot close database request", e);
                }
            }
        }
        moc.sort();
        return new StringRepresentation(moc.toString(), MediaType.APPLICATION_JSON);
    }

    @Override
    protected void describeGet(MethodInfo info) {
        this.addInfo(info);
        info.setIdentifier("retrieve_records and format it as GeoJSON");
        info.setDocumentation("Method to retrieve records of a dataset and format it as GeoJSON");
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

}

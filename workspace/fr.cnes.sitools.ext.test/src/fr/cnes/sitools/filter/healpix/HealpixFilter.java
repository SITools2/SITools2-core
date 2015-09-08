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
package fr.cnes.sitools.filter.healpix;

import java.util.*;

import fr.cnes.sitools.common.validator.ConstraintViolation;
import fr.cnes.sitools.common.validator.ConstraintViolationLevel;
import fr.cnes.sitools.dataset.filter.business.AbstractFilter;
import fr.cnes.sitools.dataset.filter.model.FilterParameter;
import fr.cnes.sitools.dataset.filter.model.FilterParameterType;
import fr.cnes.sitools.dataset.model.geometry.*;
import fr.cnes.sitools.dataset.plugins.filters.core.AbstractFormFilter;
import fr.cnes.sitools.extensions.astro.application.opensearch.responsibility.SiaHealpix;
import fr.cnes.sitools.plugins.resources.model.ResourceModel;
import fr.cnes.sitools.plugins.resources.model.ResourceParameter;
import fr.cnes.sitools.util.Util;
import fr.cnes.sitools.utils.wkt.WKTWriter;
import healpix.core.HealpixIndex;
import healpix.essentials.Pointing;
import healpix.essentials.Scheme;
import healpix.tools.SpatialVector;
import org.restlet.Request;
import org.restlet.data.Form;
import org.restlet.ext.wadl.ParameterInfo;
import org.restlet.ext.wadl.ParameterStyle;

import fr.cnes.sitools.common.validator.Validator;
import fr.cnes.sitools.dataset.DataSetApplication;
import fr.cnes.sitools.dataset.model.Column;
import fr.cnes.sitools.dataset.model.DataSet;
import fr.cnes.sitools.dataset.model.Operator;
import fr.cnes.sitools.dataset.model.Predicat;

/**
 * Filter for Healpix parameters
 * <p>
 * Note : Create predicates only for postgis datasource
 * </p>
 *
 * @author m.gond
 */
public class HealpixFilter extends AbstractFormFilter {

    private static final String ORDER = "order";
    private static final String HEALPIX = "healpix";
    private static final String COORD_SYSTEM = "coordSystem";

    public static final String GEOMETRY_COLUMN = "geometryColumn";
    public static final String SRID = "srid";

    // HEALPIX Properties
    /**
     * Multiplation factor to embed the entire Healpix pixel in the ROI.
     */
    private static final double MULT_FACT = 1.5;
    /**
     * Max value in degree of latitude axis.
     */
    private static final double MAX_DEC = 90.;
    /**
     * One degree.
     */
    private static final double ONE_DEG = 1.0;
    /**
     * One degree in arsec.
     */
    private static final double ONE_DEG_IN_ARSEC = 3600.;
    /**
     * Arcsec to degree conversion.
     */
    private static final double ARCSEC2DEG = ONE_DEG / ONE_DEG_IN_ARSEC;
    /**
     * Size resolution.
     */
    private transient double size;
    /**
     * Healpix index.
     */
    private transient HealpixIndex index;

    /**
     * BboxFilter constructor
     */
    public HealpixFilter() {
        super();
        this.setName("HealpixFilter");
        this.setDescription("Required when using Mizar component");
        this.setClassAuthor("AKKA Technologies");
        this.setClassOwner("CNES");
        this.setClassVersion("0.1");
        this.setDefaultFilter(false);

        FilterParameter param1 = new FilterParameter(GEOMETRY_COLUMN, "The name of column containing the geometry information", FilterParameterType.PARAMETER_IN);
        param1.setValueType("xs:dataset.columnAlias");
        this.addParam(param1);

        FilterParameter srid = new FilterParameter(SRID, "The SRID number to use for postgis request", FilterParameterType.PARAMETER_INTERN);
        srid.setValueType("xs:int");
        this.addParam(srid);
    }

    /*
     * (non-Javadoc)
     *
     * Parameters :
     * order:9
     * healpix:1157742
     * coordSystem:EQUATORIAL
     *
     *
     * @see fr.cnes.sitools.dataset.filter.business.AbstractFilter# getRequestParamsDescription()
     */
    @Override
    public HashMap<String, ParameterInfo> getRequestParamsDescription() {
        HashMap<String, ParameterInfo> rpd = new HashMap<String, ParameterInfo>();
        ParameterInfo paramInfo;
        paramInfo = new ParameterInfo(ORDER, true, "xs:int", ParameterStyle.QUERY, "healpix order");
        rpd.put("0", paramInfo);
        paramInfo = new ParameterInfo(HEALPIX, true, "xs:int", ParameterStyle.QUERY, "healpix index");
        rpd.put("1", paramInfo);
        paramInfo = new ParameterInfo(COORD_SYSTEM, true, "xs:STRING", ParameterStyle.QUERY, "EQUATORIAL or GALACTIC");
        rpd.put("2", paramInfo);
        this.setRequestParamsDescription(rpd);
        return rpd;
    }

    @Override
    public List<Predicat> createPredicats(Request request, List<Predicat> predicats) throws Exception {

        DataSetApplication dsApplication = null;
        DataSet ds = null;

        //lets apply our filter
        if (dsApplication == null) {
            dsApplication = (DataSetApplication) getContext().getAttributes().get("DataSetApplication");
            ds = dsApplication.getDataSet();
        }

        Map<String, FilterParameter> filterParam = this.getParametersMap();
        FilterParameter paramGeoCol = filterParam.get(GEOMETRY_COLUMN);
        FilterParameter paramSrid = filterParam.get(SRID);


        Form params = request.getResourceRef().getQueryAsForm();

        // Build predicat for filters param
        String orderString = params.getFirstValue(ORDER);
        String healpixString = params.getFirstValue(HEALPIX);
        String coordSystem = params.getFirstValue(COORD_SYSTEM);

        if (checkParam(orderString, healpixString, coordSystem, paramGeoCol, paramSrid)) {

            String columnAlias = paramGeoCol.getAttachedColumn();
            String srid = paramSrid.getValue();

            int order = Integer.parseInt(orderString);
            long healpix = Long.parseLong(healpixString);

            Column col = ds.findByColumnAlias(columnAlias);
            if (col != null && col.getFilter() != null && col.getFilter()) {

                //Calculer un polygon a partir des numeros Healpix
                final int nside = (int) Math.pow(2, order);
                index = new HealpixIndex(nside, Scheme.NESTED);
                SpatialVector[] vectors = index.corners_nest(healpix, 2);

                String wkt = getWKTString(vectors);

                String predicatFormat = " AND st_intersects(%s, ST_GeomFromText('%s', %s))";
                String predicatString = String.format(predicatFormat, col.getDataIndex(), wkt, srid);

                Predicat pred = new Predicat();
                pred.setStringDefinition(predicatString);
                predicats.add(pred);
            }
        }
        return predicats;
    }

    /**
     * Generate a WKT string from the given Polygon identified by the array of SpatialVector
     * @param vectors the list of vectors
     * @return a WKT string
     */
    private String getWKTString(SpatialVector[] vectors) {
        Polygon geom = new Polygon();
        List<LngLatAlt> points = new ArrayList<LngLatAlt>();
        LngLatAlt firstPoint = null;
        for (SpatialVector vector : vectors) {
            double latitude = vector.ra();
            if (latitude > 180) {
                latitude -= 360;
            }

            if (firstPoint == null) {
                firstPoint = new LngLatAlt(latitude, vector.dec());
            }
            points.add(new LngLatAlt(latitude, vector.dec()));
        }
        points.add(firstPoint);
        geom.add(points);

        return WKTWriter.write(geom);
    }

    private boolean checkParam(String orderString, String healpixString, String coordSystem, FilterParameter columnAlias, FilterParameter srid) {
        return !Util.isEmpty(orderString) &&
                !Util.isEmpty(healpixString) &&
                !Util.isEmpty(coordSystem) &&
                columnAlias != null &&
                !Util.isEmpty(columnAlias.getAttachedColumn()) &&
                srid != null &&
                !Util.isEmpty(srid.getValue());
    }

    @Override
    public Validator<AbstractFilter> getValidator() {
        return new Validator<AbstractFilter>() {

            @Override
            public Set<ConstraintViolation> validate(AbstractFilter item) {
                Set<ConstraintViolation> constraints = new HashSet<ConstraintViolation>();
                Map<String, FilterParameter> params = item.getParametersMap();

                //GEOMETRY COLUMN
                FilterParameter param = params.get(GEOMETRY_COLUMN);
                String value = param.getAttachedColumn();
                if (value.equals("")) {
                    ConstraintViolation constraint = new ConstraintViolation();
                    constraint.setMessage("There is not column defined");
                    constraint.setLevel(ConstraintViolationLevel.CRITICAL);
                    constraint.setValueName(param.getName());
                    constraints.add(constraint);
                }

                //SRID
                param = params.get(SRID);
                value = param.getValue();
                if (value.equals("")) {
                    ConstraintViolation constraint = new ConstraintViolation();
                    constraint.setMessage("There is srid defined");
                    constraint.setLevel(ConstraintViolationLevel.CRITICAL);
                    constraint.setValueName(param.getName());
                    constraints.add(constraint);
                }
                return constraints;
            }
        };
    }

}

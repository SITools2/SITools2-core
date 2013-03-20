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
package fr.cnes.sitools.dataset.plugins.filters.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.restlet.Request;
import org.restlet.data.Form;
import org.restlet.data.Status;
import org.restlet.ext.wadl.ParameterInfo;
import org.restlet.ext.wadl.ParameterStyle;
import org.restlet.resource.ResourceException;

import fr.cnes.sitools.common.exception.SitoolsException;
import fr.cnes.sitools.common.validator.ConstraintViolation;
import fr.cnes.sitools.common.validator.Validator;
import fr.cnes.sitools.dataset.DataSetApplication;
import fr.cnes.sitools.dataset.database.common.RequestFactory;
import fr.cnes.sitools.dataset.database.jdbc.RequestSql;
import fr.cnes.sitools.dataset.filter.business.AbstractFilter;
import fr.cnes.sitools.dataset.model.Column;
import fr.cnes.sitools.dataset.model.DataSet;
import fr.cnes.sitools.dataset.model.Predicat;
import fr.cnes.sitools.datasource.common.SitoolsDataSource;
import fr.cnes.sitools.datasource.jdbc.business.SitoolsSQLDataSourceFactory;
import fr.cnes.sitools.datasource.jdbc.model.Table;

/**
 * Filter defined for Single Value Component
 * 
 * 
 * @author d.arpin
 */
public final class ConeSearchPgSphereFilter extends AbstractFormFilter {
  /**
   * The number of values
   */
  private static final int NUMBER_OF_VALUES = 3;
  /**
   * The maximal declination 
   */
  private static final int MAXIMAL_DECLINATION = 90;

  /**
   * The index of dimension
   */
  private static final int DIMENSION = 5;
  /**
   * The index of unit
   */
  private static final int UNIT = 6;

  /**
   * The list of component that uses this filter
   */
  private enum TYPE_COMPONENT {
    /** cone Search cartesien type */
    CONE_SEARCH_PG_SPHERE
  }

  /**
   * the values from to
   */
  private String[] values;

  /**
   * Default constructor
   */
  public ConeSearchPgSphereFilter() {

    super();
    this.setName("ConeSearchPgSphere");
    this.setDescription("Required when using PgSphere cone search  Component");

    this.setClassAuthor("AKKA Technologies");
    this.setClassAuthor("AKKA Technologies");
    this.setClassOwner("CNES");
    this.setClassVersion("0.1");
    this.setDefaultFilter(true);

    HashMap<String, ParameterInfo> rpd = new HashMap<String, ParameterInfo>();

    ParameterInfo paramInfo;
    paramInfo = new ParameterInfo("p[#]", false, "xs:string", ParameterStyle.QUERY,
        "CONE_SEARCH_PG_SPHERE|RA_columnAlias,DEC_columnAlias,Id_columnAlias|RA_value|DEC_value|Radius_Value");
    rpd.put("0", paramInfo);
    paramInfo = new ParameterInfo("c[#]", false, "xs:string", ParameterStyle.QUERY,
        "CONE_SEARCH_PG_SPHERE|dictionaryName,RA_conceptName,DEC_conceptName,Id_conceptName|RA_value|DEC_value|Radius_Value");
    rpd.put("1", paramInfo);
    this.setRequestParamsDescription(rpd);
    //

  }

  @Override
  public List<Predicat> createPredicats(Request request, List<Predicat> predicats) throws Exception {
    DataSetApplication dsApplication = null;
    DataSet ds = null;

    boolean isConcept = true;

    Form params = request.getResourceRef().getQueryAsForm();
    boolean filterExists = true;
    int i = 0;
    // Build predicat for filters param
    while (filterExists) {
      // first check if the filter is applied on a Concept or not
      String index = TEMPLATE_PARAM_CONCEPT.replace("#", Integer.toString(i));
      String formParam = params.getFirstValue(index);
      if (formParam == null) {
        isConcept = false;
        index = TEMPLATE_PARAM.replace("#", Integer.toString(i));
        formParam = params.getFirstValue(index);
      }
      i++;
      if (formParam != null) {
        String[] parameters = formParam.split("\\|");
        TYPE_COMPONENT[] types = TYPE_COMPONENT.values();
        Boolean trouve = false;
        for (TYPE_COMPONENT typeCmp : types) {
          if (typeCmp.name().equals(parameters[TYPE])) {
            trouve = true;
          }
        }
        if (trouve) {
          if (dsApplication == null) {
            dsApplication = (DataSetApplication) getContext().getAttributes().get("DataSetApplication");
            if (dsApplication == null) {
              throw new SitoolsException("can not find DataSet Application");
            }
          }
          ds = dsApplication.getDataSet();

          if (checkValues(parameters)) {

            String[] columnsAlias = getColumnsAlias(isConcept, parameters, dsApplication);

            ArrayList<Column> columns = new ArrayList<Column>();
            for (String columnAlias : columnsAlias) {
              if (columnAlias != null) {
                Column col = ds.findByColumnAlias(columnAlias);
                if (col != null) {
                  columns.add(col);
                }
              }

            }

            if (columns.size() != NUMBER_OF_VALUES) {
              throw new SitoolsException("incorrect number of column");
            }
            Predicat predicat = new Predicat();
            SitoolsDataSource dsource = SitoolsSQLDataSourceFactory.getDataSource(ds.getDatasource().getId());
            RequestSql requestSql = RequestFactory.getRequest(dsource.getDsModel().getDriverClass());

            String raColumn = requestSql.convertColumnToString(columns.get(0));
            String decColumn = requestSql.convertColumnToString(columns.get(1));
            Column columnId = columns.get(2);
            String columnIdAttribute = requestSql.convertColumnToString(columnId);
            String columnIdAlias = columnId.getColumnAlias();

            String tableString = new Table(columns.get(0).getTableName(), columns.get(0).getSchema(), columns.get(0)
                .getTableAlias()).getFROMReference();

            // try to cast the values to check if there are doubles
            Double ra;
            Double dec;
            Double radius;
            try {
              ra = Double.valueOf(values[0]);
              dec = Double.valueOf(values[1]);
              radius = Double.valueOf(values[2]);
            }
            catch (NumberFormatException e) {
              throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "Not numeric value entered", e);
            }

            // TODO : gérer mieux la dimension par défaut et l'unité par défaut
            if (parameters.length == DIMENSION + 2) {
              // get dimension, if exists
              String dimension = parameters[DIMENSION];
              // get unit, if exists
              String unit = parameters[UNIT];
              try {
                radius = convert(unit, "°", values[2], dimension).doubleValue();
              }
              catch (SitoolsException e) {
                throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, e.getMessage(), e);
              }

            }

            Double decMin = dec - radius;
            Double decMax = dec + radius;

            String predDef = "";
            if (decMax > MAXIMAL_DECLINATION || decMin < -1 * MAXIMAL_DECLINATION) {
              predDef = " AND " + columnIdAttribute + " in (" + "SELECT box." + columnIdAlias + " FROM ("
                  + "    SELECT degrees(spoint(radians(" + raColumn + "),radians(" + decColumn
                  + ")) <-> spoint(radians(" + ra + "),radians(" + dec + "))) as distance, " + columnIdAttribute
                  + "  FROM " + tableString + "      WHERE (" + decColumn + " BETWEEN (" + dec + " - " + radius
                  + ") AND (" + dec + " + " + radius + "))" + "  ) as box" + "  WHERE box.distance <= " + radius
                  + "  ORDER BY distance ASC" + ")";

            }
            else {
              predDef = " AND " + columnIdAttribute + " in ( " + "SELECT box." + columnIdAlias + " FROM ( "
                  + "    SELECT degrees(spoint(radians(" + raColumn + "),radians(" + decColumn
                  + ")) <-> spoint(radians(" + ra + "),radians(" + dec + "))) as distance, " + columnIdAttribute
                  + "      FROM " + tableString + "      WHERE (" + decColumn + " BETWEEN (" + dec + " - " + radius
                  + ") AND (" + dec + " + " + radius + ")) " + "        AND (" + raColumn + " BETWEEN (" + ra + " - "
                  + radius + " / cos(abs(radians(" + dec + ")) + radians(" + radius + "))) AND (" + ra + " + " + radius
                  + " / cos(abs(radians(" + dec + ")) + radians(" + radius + "))))" + "  OR (" + raColumn
                  + " >= 360 + (" + ra + " - " + radius + " / cos(abs(radians(" + dec + ")) + radians(" + radius
                  + ")))) " + "  OR (" + raColumn + " <= (" + ra + " + " + radius + " / cos(abs(radians(" + dec
                  + ")) + radians(" + radius + "))) - 360)" + "  ) as box" + "  WHERE box.distance <= " + radius
                  + "  ORDER BY distance ASC" + ") ";

            }

            predicat.setStringDefinition(predDef);
            predicats.add(predicat);
          }
          return predicats;

        }

      }
      else {
        filterExists = false;
      }

    }

    return predicats;
  }

  /**
   * Check the number of values
   * 
   * @param parameters
   *          the values
   * @return true if the number of values is correct
   */
  private boolean checkValues(String[] parameters) {
    values = Arrays.copyOfRange(parameters, VALUES, VALUES + NUMBER_OF_VALUES);
    if (values.length == NUMBER_OF_VALUES) {
      return true;
    }
    return false;
  }

  /**
   * Gets the validator for this Filter
   * 
   * @return the validator for the filter
   */
  @Override
  public Validator<AbstractFilter> getValidator() {
    return new Validator<AbstractFilter>() {
      @Override
      public Set<ConstraintViolation> validate(AbstractFilter item) {
        // TODO Auto-generated method stub
        return null;
      }
    };
  }

}

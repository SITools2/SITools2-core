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
import fr.cnes.sitools.dataset.model.Operator;
import fr.cnes.sitools.dataset.model.Predicat;
import fr.cnes.sitools.datasource.common.SitoolsDataSource;
import fr.cnes.sitools.datasource.jdbc.business.SitoolsSQLDataSourceFactory;

/**
 * Filter defined for Single Value Component
 * 
 * 
 * @author d.arpin
 */
public final class ConeSearchCartesienFilter extends AbstractFormFilter {
  /**
   * The number of values
   */
  private static final int NUMBER_OF_VALUES = 3;
  /**
   * The index of dimension
   */
  private static final int DIMENSION = 5;
  /**
   * The index of unit
   */
  private static final int UNIT = 6;
  /**
   * The number of The maximal declination
   */
  private static final int MAXIMAL_DECLINATION = 90;

  /**
   * The list of component that uses this filter
   */
  private enum TYPE_COMPONENT {
    /** cone Search cartesien type */
    CONE_SEARCH_CARTESIEN
  }

  /**
   * the values from to
   */
  private String[] values;

  /**
   * Default constructor
   */
  public ConeSearchCartesienFilter() {

    super();
    this.setName("ConeSearchCartesien");
    this.setDescription("Required when using cartesian cone search  Component");

    this.setClassAuthor("AKKA Technologies");
    this.setClassAuthor("AKKA Technologies");
    this.setClassOwner("CNES");
    this.setClassVersion("0.2");
    this.setDefaultFilter(true);

    HashMap<String, ParameterInfo> rpd = new HashMap<String, ParameterInfo>();

    ParameterInfo paramInfo;
    paramInfo = new ParameterInfo("p[#]", false, "xs:string", ParameterStyle.QUERY,
        "CONE_SEARCH_CARTESIEN|columnAlias1,columnAlias2,columnAlias3|RA_value|DEC_value|SR_Value");
    rpd.put("0", paramInfo);
    paramInfo = new ParameterInfo("c[#]", false, "xs:string", ParameterStyle.QUERY,
        "CONE_SEARCH_CARTESIEN|dictionaryName,conceptName1,conceptName2,conceptName3|RA_value|DEC_value|SR_Value");
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
              Column col = ds.findByColumnAlias(columnAlias);
              if (col != null) {
                columns.add(col);
              }

            }

            if (columns.size() != NUMBER_OF_VALUES) {
              throw new SitoolsException("incorrect number of column");
            }
            SitoolsDataSource dsource = SitoolsSQLDataSourceFactory.getDataSource(ds.getDatasource().getId());
            RequestSql requestSql = RequestFactory.getRequest(dsource.getDsModel().getDriverClass());

            String xcol = requestSql.convertColumnToString(columns.get(0));
            String ycol = requestSql.convertColumnToString(columns.get(1));
            String zcol = requestSql.convertColumnToString(columns.get(2));

            // try to cast the values to check if there are doubles
            Double raParam;
            Double decParam;
            Double srParam;

            try {
              raParam = Double.valueOf(values[0]);
              decParam = Double.valueOf(values[1]);
              srParam = Double.valueOf(values[2]);
            }
            catch (NumberFormatException e) {
              throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "Not numeric value entered", e);
            }

            if (parameters.length == DIMENSION + 2) {
              // get dimension, if exists
              String dimension = parameters[DIMENSION];
              // get unit, if exists
              String unit = parameters[UNIT];
              try {
                srParam = convert(unit, "°", values[2], dimension).doubleValue();
              }
              catch (SitoolsException e) {
                throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, e.getMessage(), e);
              }

            }

            Double sr = Math.toRadians(srParam);
            Double alpha = Math.toRadians(raParam);
            Double delta = Math.toRadians(decParam);

            Double x = Math.cos(delta) * Math.cos(alpha);
            Double y = Math.cos(delta) * Math.sin(alpha);
            Double z = Math.sin(delta);

            Double zmin = Math.sin(delta - sr);
            Double zmax = Math.sin(delta + sr);

            if (delta + sr > Math.toRadians(MAXIMAL_DECLINATION)) {
              zmax = Double.valueOf("1");
            }

            if (delta - sr < Math.toRadians(-1 * MAXIMAL_DECLINATION)) {
              zmin = Double.valueOf("-1");
            }

            String leftString = xcol + "*" + x;
            leftString += " + " + ycol + "*" + y;
            leftString += " + " + zcol + "*" + z;
            Predicat predicat = new Predicat();
            predicat.setLeftString(leftString);

            predicat.setNbOpenedParanthesis(1);
            predicat.setNbClosedParanthesis(1);
            predicat.setClosedParenthesis(")");
            predicat.setOpenParenthesis("(");
            predicat.setCompareOperator(Operator.GTE);

            predicat.setRightValue(String.valueOf("'" + Math.cos(sr) + "'"));
            predicats.add(predicat);

            predicat = new Predicat();
            predicat.setStringDefinition(" AND " + zcol + " between " + zmin + " and " + zmax);
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

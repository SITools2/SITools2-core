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
package fr.cnes.sitools.dataset.plugins.filters.core;

import java.math.BigDecimal;
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
import fr.cnes.sitools.dataset.filter.business.AbstractFilter;
import fr.cnes.sitools.dataset.model.Column;
import fr.cnes.sitools.dataset.model.DataSet;
import fr.cnes.sitools.dataset.model.Operator;
import fr.cnes.sitools.dataset.model.Predicat;
import fr.cnes.sitools.form.dataset.model.NumericBetweenSelection;

/**
 * Filter defined for One or Between Component
 * 
 * 
 * @author d.arpin Change history : <a
 *         href="https://sourceforge.net/tracker/?func=detail&aid=3346624&group_id=531341&atid=2158259">[3346624]
 *         conversion done by numeric_between</a><br/>
 *         2011/07/04 d.arpin use Double instead of Float to prevent from precision lost <br/>
 */
public final class OneOrBetweenFilter extends AbstractFormFilter {
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
   * The list of component that uses this filter
   */
  private enum TYPE_COMPONENT {
    /** DefaultType */
    ONE_OR_BETWEEN
  }

  /** the values */
  private Object values;

  /**
   * Default constructor
   */
  public OneOrBetweenFilter() {

    super();
    this.setName("OneOrBetweenFilter");
    this.setDescription("Required when using One Or Between Components");

    this.setClassAuthor("AKKA Technologies");
    this.setClassAuthor("AKKA Technologies");
    this.setClassOwner("CNES");
    this.setClassVersion("0.1");
    this.setDefaultFilter(true);

    HashMap<String, ParameterInfo> rpd = new HashMap<String, ParameterInfo>();

    ParameterInfo paramInfo;
    paramInfo = new ParameterInfo("p[#]", false, "xs:string", ParameterStyle.QUERY,
        "ONE_OR_BETWEEN|columnAlias|valueEq|valueFrom|valueTo|dimension|unit");
    rpd.put("0", paramInfo);
    paramInfo = new ParameterInfo("c[#]", false, "xs:string", ParameterStyle.QUERY,
        "ONE_OR_BETWEEN|dictionaryName,conceptName|valueEq|valueFrom|valueTo|dimension|unit");
    rpd.put("1", paramInfo);
    this.setRequestParamsDescription(rpd);
    //

  }

  @Override
  public List<Predicat> createPredicats(Request request, List<Predicat> predicats) throws Exception {
    // Get the dataset
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
            ds = dsApplication.getDataSet();
          }
          String columnAlias = null;
          if (parameters.length >= VALUES) {

            /*
             * columnsAlias = parameters[COLUMN].split(","); ArrayList<Column> columns = new ArrayList<Column>(); for
             * (String columnAlias : columnsAlias) { Column col = ds.findByColumnAlias(columnAlias); if (col != null) {
             * columns.add(col); }
             * 
             * }
             */
            columnAlias = getColumnAlias(isConcept, parameters, dsApplication);
            if (columnAlias != null) {
              Column col = ds.findByColumnAlias(columnAlias);
              if (col != null && col.getFilter() != null && col.getFilter() && checkValues(parameters, col)) {
                if (values instanceof NumericBetweenSelection) {
                  NumericBetweenSelection val = (NumericBetweenSelection) values;
                  Predicat predicat = new Predicat();
                  predicat.setLeftAttribute(col);
                  predicat.setNbOpenedParanthesis(1);
                  predicat.setNbClosedParanthesis(0);
                  predicat.setCompareOperator(Operator.GTE);
                  predicat.setRightValue(new BigDecimal(val.getFrom().toString()));
                  predicats.add(predicat);
                  predicat = new Predicat();
                  predicat.setLeftAttribute(col);
                  predicat.setNbOpenedParanthesis(0);
                  predicat.setNbClosedParanthesis(1);
                  predicat.setCompareOperator(Operator.LTE);
                  predicat.setRightValue(new BigDecimal(val.getTo().toString()));
                  predicats.add(predicat);
                }
                else {
                  Predicat predicat = new Predicat();
                  predicat.setLeftAttribute(col);
                  predicat.setNbOpenedParanthesis(0);
                  predicat.setNbClosedParanthesis(0);
                  predicat.setCompareOperator(Operator.EQ);
                  predicat.setRightValue(values);
                  predicats.add(predicat);
                }
              }

            }

          }
        }
      }

      else {
        filterExists = false;
      }
    }

    return predicats;
  }

  /**
   * Check values of the form
   * 
   * @param parameters
   *          the parameters of the filter
   * @param col
   *          The column
   * @return true if values agree
   */
  private boolean checkValues(String[] parameters, Column col) {
    String[] paramValues = Arrays.copyOfRange(parameters, VALUES, DIMENSION);
    if (paramValues.length != NUMBER_OF_VALUES && paramValues.length != 1) {
      return false;
    }
    if (paramValues[0] == null || paramValues[0].equals("")) {
      Number value1;
      Number value2;
      try {
        value1 = new BigDecimal(paramValues[1]);
        value2 = new BigDecimal(paramValues[2]);
      }
      catch (NumberFormatException e) {
        throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "Not numeric value entered", e);
      }

      return check2Values(parameters, col, paramValues, value1, value2);
    }
    else {
      Number value;
      try {
        value = new BigDecimal(paramValues[0]);
      }
      catch (NumberFormatException e) {
        throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "Not numeric value entered", e);
      }
      
      return check1Value(parameters, col, paramValues, value);
    }
  }

  /**
   * Check 1 value
   * 
   * @param parameters
   *          the list of parameters
   * @param col
   *          the column
   * @param paramValues
   *          the parameters values
   * @param value
   *          the first value   
   * @return true if the values are correct, otherwise an exception is thrown
   * @throws ResourceException
   *           if there is an error
   */
  private boolean check1Value(String[] parameters, Column col, String[] paramValues, Number value)
    throws ResourceException {
    if (parameters.length == DIMENSION + 2) {
      // get dimension, if exists
      String dimension = parameters[DIMENSION];
      // get unit, if exists
      String unit = parameters[UNIT];
      try {
        value = convert(unit, col.getUnit().getUnitName(), paramValues[0], dimension);
      }
      catch (SitoolsException e) {
        throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, e.getMessage(), e);
      }
    }
    values = value;
    return true;
  }

  /**
   * Check 2 values
   * 
   * @param parameters
   *          the list of parameters
   * @param col
   *          the column
   * @param paramValues
   *          the parameters values
   * @param value1
   *          the first value
   * @param value2
   *          the second value
   * @return true if the values are correct, otherwise an exception is thrown
   * @throws ResourceException
   *           if there is an error
   */
  private boolean check2Values(String[] parameters, Column col, String[] paramValues, Number value1, Number value2)
    throws ResourceException {
    if (parameters.length == DIMENSION + 2) {
      // get dimension, if exists
      String dimension = parameters[DIMENSION];
      // get unit, if exists
      String unit = parameters[UNIT];
      try {
        if (col.getUnit() == null) {
          throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,
              "The column asked has no unit, it cannot be converted");
        }
        value1 = convert(unit, col.getUnit().getUnitName(), paramValues[1], dimension);
        value2 = convert(unit, col.getUnit().getUnitName(), paramValues[2], dimension);
      }
      catch (SitoolsException e) {
        throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, e.getMessage(), e);
      }
    }
    values = new NumericBetweenSelection(value1, value2);
    return true;
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

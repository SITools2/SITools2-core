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

import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.restlet.Request;
import org.restlet.data.Form;
import org.restlet.data.Status;
import org.restlet.ext.wadl.ParameterInfo;
import org.restlet.ext.wadl.ParameterStyle;
import org.restlet.resource.ResourceException;

import fr.cnes.sitools.common.validator.ConstraintViolation;
import fr.cnes.sitools.common.validator.Validator;
import fr.cnes.sitools.dataset.DataSetApplication;
import fr.cnes.sitools.dataset.filter.business.AbstractFilter;
import fr.cnes.sitools.dataset.model.Column;
import fr.cnes.sitools.dataset.model.DataSet;
import fr.cnes.sitools.dataset.model.Operator;
import fr.cnes.sitools.dataset.model.Predicat;
import fr.cnes.sitools.util.DateUtils;
import fr.cnes.sitools.util.SQLUtils;

/**
 * Filter defined for Date Between Component
 * 
 * 
 * @author d.arpin <a
 *         href="https://sourceforge.net/tracker/?func=detail&atid=2158259&aid=3411383&group_id=531341">[3411383]</a><br/>
 *         2011/09/19 d.arpin {add quotes arround date Value}
 */
public final class DateBetweenFilter extends AbstractFormFilter {
  /**
   * the values from to
   */
  private String[] values;

  /**
   * The list of component that uses this filter
   */
  private enum TYPE_COMPONENT {
    /** DefaultType */
    DATE_BETWEEN
  }

  /**
   * Default constructor
   */
  public DateBetweenFilter() {

    super();
    this.setName("DateBetweenFilter");
    this.setDescription("Required when using Date Between Components");

    this.setClassAuthor("AKKA Technologies");
    this.setClassAuthor("AKKA Technologies");
    this.setClassOwner("CNES");
    this.setClassVersion("0.1");
    this.setDefaultFilter(true);

    HashMap<String, ParameterInfo> rpd = new HashMap<String, ParameterInfo>();

    ParameterInfo paramInfo;
    paramInfo = new ParameterInfo("p[#]", false, "xs:string", ParameterStyle.QUERY, "DATE_BETWEEN|columnAlias|valueFrom|valueTo");
    rpd.put("0", paramInfo);
    paramInfo = new ParameterInfo("c[#]", false, "xs:string", ParameterStyle.QUERY, "DATE_BETWEEN|dictionaryName,conceptName|valueFrom|valueTo");
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
              if (col != null && col.getFilter() != null && col.getFilter() && checkValues(parameters)) {
                // escape the values to avoid SQL injection
                String value1 = SQLUtils.escapeString(values[0]);
                String value2 = SQLUtils.escapeString(values[1]);

                Date date1;
                Date date2;
                try {
                  date1 = DateUtils.parse(value1);
                  date2 = DateUtils.parse(value2);
                }
                catch (ParseException e) {
                  throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "The entered value was not a date");
                }
                catch (IllegalArgumentException e) {
                  throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "The entered value was not a date");
                }
                if (date1.before(date2) || date1.equals(date2)) {
                  Predicat predicat = new Predicat();
                  predicat.setLeftAttribute(col);
                  predicat.setNbOpenedParanthesis(1);
                  predicat.setNbClosedParanthesis(0);
                  predicat.setCompareOperator(Operator.GT);
                  predicat.setRightValue(date1);
                  predicats.add(predicat);
                  predicat = new Predicat();
                  predicat.setLeftAttribute(col);
                  predicat.setNbOpenedParanthesis(0);
                  predicat.setNbClosedParanthesis(1);
                  predicat.setCompareOperator(Operator.LT);
                  predicat.setRightValue(date2);
                  predicats.add(predicat);
                }
                else {
                  throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "From date must be before To date");
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
   * @return true if values agree
   */
  private boolean checkValues(String[] parameters) {
    values = Arrays.copyOfRange(parameters, VALUES, parameters.length);
    if (values.length == 2) {
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

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

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

import org.restlet.Request;
import org.restlet.data.Form;
import org.restlet.data.Status;
import org.restlet.ext.wadl.ParameterInfo;
import org.restlet.ext.wadl.ParameterStyle;
import org.restlet.resource.ResourceException;

import fr.cnes.sitools.common.exception.SitoolsException;
import fr.cnes.sitools.common.validator.Validator;
import fr.cnes.sitools.dataset.DataSetApplication;
import fr.cnes.sitools.dataset.model.Column;
import fr.cnes.sitools.dataset.model.DataSet;
import fr.cnes.sitools.dataset.model.Operator;
import fr.cnes.sitools.dataset.model.Predicat;

/**
 * 
 * Simple number field
 * 
 * @author d.arpin (AKKA technologies)
 */
public class NumberFieldFilter extends AbstractFormFilter {

  /**
   * The index of dimension
   */
  private static final int DIMENSION = 3;
  /**
   * The index of unit
   */
  private static final int UNIT = 4;

  /**
   * The list of component that uses this filter
   */
  private enum TYPE_COMPONENT {
    /** Text field type */
    NUMBER_FIELD
  }

  /**
   * Constructor
   */
  public NumberFieldFilter() {
    super();
    this.setName("NumberFieldFilter");
    this.setDescription("Filter on the number datatype");

    this.setClassAuthor("AKKA Technologies");
    this.setClassAuthor("AKKA Technologies");
    this.setClassOwner("CNES");
    this.setClassVersion("0.1");
    this.setDefaultFilter(true);

    HashMap<String, ParameterInfo> rpd = new HashMap<String, ParameterInfo>();

    ParameterInfo paramInfo;
    paramInfo = new ParameterInfo("p[#]", false, "xs:string", ParameterStyle.QUERY,
        "NUMBER_FIELD|columnAlias|value|dimension|unit");
    rpd.put("0", paramInfo);
    paramInfo = new ParameterInfo("c[#]", false, "xs:string", ParameterStyle.QUERY,
        "NUMBER_FIELD|dictionaryName,conceptName|value|dimension|unit");
    rpd.put("1", paramInfo);
    this.setRequestParamsDescription(rpd);
  }

  public Validator<?> getValidator() {

    return null;
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
        Boolean isFound = false;
        for (TYPE_COMPONENT typeCmp : types) {
          if (typeCmp.name().equals(parameters[TYPE])) {
            isFound = true;
          }
        }
        if (isFound) {
          if (dsApplication == null) {
            dsApplication = (DataSetApplication) getContext().getAttributes().get("DataSetApplication");
            ds = dsApplication.getDataSet();
          }
          String columnAlias = null;
          if (parameters.length >= VALUES) {
            columnAlias = getColumnAlias(isConcept, parameters, dsApplication);
            if (columnAlias != null) {
              Column col = ds.findByColumnAlias(columnAlias);
              if (col != null && col.getFilter() != null && col.getFilter()) {
                String valueStr = parameters[VALUES];
                if (valueStr != null) {
                  Number value;
                  try {
                    value = new BigDecimal(valueStr);
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
                      if (col.getUnit() == null) {
                        throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,
                            "The column asked has no unit, it cannot be converted");
                      }
                      value = convert(unit, col.getUnit().getUnitName(), valueStr, dimension);
                    }
                    catch (SitoolsException e) {
                      throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, e.getMessage(), e);
                    }
                  }
                  Predicat predicat = new Predicat();
                  predicat.setLeftAttribute(col);
                  predicat.setNbOpenedParanthesis(0);
                  predicat.setNbClosedParanthesis(0);
                  predicat.setCompareOperator(Operator.EQ);
                  predicat.setRightValue(value);
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

}

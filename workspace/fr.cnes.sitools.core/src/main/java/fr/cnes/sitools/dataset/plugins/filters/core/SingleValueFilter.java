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

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.restlet.Request;
import org.restlet.data.Form;
import org.restlet.ext.wadl.ParameterInfo;
import org.restlet.ext.wadl.ParameterStyle;

import fr.cnes.sitools.common.validator.ConstraintViolation;
import fr.cnes.sitools.common.validator.Validator;
import fr.cnes.sitools.dataset.DataSetApplication;
import fr.cnes.sitools.dataset.filter.business.AbstractFilter;
import fr.cnes.sitools.dataset.model.Column;
import fr.cnes.sitools.dataset.model.DataSet;
import fr.cnes.sitools.dataset.model.Operator;
import fr.cnes.sitools.dataset.model.Predicat;
import fr.cnes.sitools.util.SQLUtils;

/**
 * Filter defined for Single Value Component
 * 
 * 
 * @author d.arpin
 */
public final class SingleValueFilter extends AbstractFormFilter {

  /**
   * The list of component that uses this filter
   */
  private enum TYPE_COMPONENT {
    /** Text field type */
    TEXTFIELD,
    /** List box type */
    LISTBOX,
    /** Dropdown list type */
    DROPDOWNLIST,
    /** Radio button type */
    RADIO,
    /** Boolean Checkbox type */
    BOOLEAN_CHECKBOX
  }

  /**
   * Default constructor
   */
  public SingleValueFilter() {

    super();
    this.setName("SingleValueFilter");
    this.setDescription("Required when using Single Value Component");

    this.setClassAuthor("AKKA Technologies");
    this.setClassAuthor("AKKA Technologies");
    this.setClassOwner("CNES");
    this.setClassVersion("0.1");
    this.setDefaultFilter(true);

    HashMap<String, ParameterInfo> rpd = new HashMap<String, ParameterInfo>();

    ParameterInfo paramInfo;
    paramInfo = new ParameterInfo("p[#]", false, "xs:string", ParameterStyle.QUERY, "TEXTFIELD|columnAlias|value");
    rpd.put("0", paramInfo);
    paramInfo = new ParameterInfo("p[#]", false, "xs:string", ParameterStyle.QUERY, "RADIO|columnAlias|value");
    rpd.put("1", paramInfo);
    paramInfo = new ParameterInfo("p[#]", false, "xs:string", ParameterStyle.QUERY, "LISTBOX|columnAlias|value");
    rpd.put("2", paramInfo);
    paramInfo = new ParameterInfo("p[#]", false, "xs:string", ParameterStyle.QUERY, "DROPDOWNLIST|columnAlias|value");
    rpd.put("3", paramInfo);
    paramInfo = new ParameterInfo("p[#]", false, "xs:string", ParameterStyle.QUERY, "BOOLEAN_CHECKBOX|columnAlias|value");
    rpd.put("4", paramInfo);

    paramInfo = new ParameterInfo("c[#]", false, "xs:string", ParameterStyle.QUERY,
        "TEXTFIELD|dictionaryName,conceptName|value");
    rpd.put("5", paramInfo);
    paramInfo = new ParameterInfo("c[#]", false, "xs:string", ParameterStyle.QUERY,
        "RADIO|dictionaryName,conceptName|value");
    rpd.put("6", paramInfo);
    paramInfo = new ParameterInfo("c[#]", false, "xs:string", ParameterStyle.QUERY,
        "LISTBOX|dictionaryName,conceptName|value");
    rpd.put("7", paramInfo);
    paramInfo = new ParameterInfo("c[#]", false, "xs:string", ParameterStyle.QUERY,
        "DROPDOWNLIST|dictionaryName,conceptName|value");
    rpd.put("8", paramInfo);
    paramInfo = new ParameterInfo("c[#]", false, "xs:string", ParameterStyle.QUERY,
        "BOOLEAN_CHECKBOX|dictionaryName,conceptName|value");
    rpd.put("9", paramInfo);
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
            ds = dsApplication.getDataSet();
          }
          String columnAlias = null;
          if (parameters.length >= VALUES) {

            columnAlias = getColumnAlias(isConcept, parameters, dsApplication);
            if (columnAlias != null) {
              Column col = ds.findByColumnAlias(columnAlias);
              if (col != null && col.getFilter() != null && col.getFilter()) {
                // get the value and escape it to avoid SQL injection
                String value = SQLUtils.escapeString(parameters[VALUES]);
                Predicat predicat = new Predicat();
                if (value != null) {
                  predicat.setLeftAttribute(col);
                  predicat.setNbOpenedParanthesis(0);
                  predicat.setNbClosedParanthesis(0);
                  if (parameters[TYPE].equals(TYPE_COMPONENT.TEXTFIELD.name())) {
                    value = value.replace("*", "%");
                    predicat.setCompareOperator(Operator.LIKE);
                  }
                  else {
                    predicat.setCompareOperator(Operator.EQ);
                  }
                  predicat.setRightValue("'" + value + "'");

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

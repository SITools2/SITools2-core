    /*******************************************************************************
 * Copyright 2010-2016 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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
package fr.cnes.sitools.properties.core;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.data.Form;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

import fr.cnes.sitools.common.application.ContextAttributes;
import fr.cnes.sitools.form.dataset.model.NumericBetweenSelection;
import fr.cnes.sitools.properties.AbstractPropertyFilter;
import fr.cnes.sitools.properties.model.SitoolsProperty;
import fr.cnes.sitools.properties.model.SitoolsPropertyType;

/**
 * Numeric between filter type on property Handle search on Numeric property type
 * 
 * @author m.gond
 */
public class NumericBetweenPropertyFilter extends AbstractPropertyFilter {

  /**
   * The number of values
   */
  private static final int NUMBER_OF_VALUES = 2;

  /**
   * The list of component that uses this filter
   */
  private enum TYPE_COMPONENT {
    /** DefaultType */
    NUMERIC_BETWEEN
  }

  /** Numeric bewteen associated */
  private NumericBetweenSelection numericBetween;

  @Override
  public boolean match(Request request, Context context) {
    boolean isCompatible = true;
    boolean filterExists = true;

    Form params = request.getResourceRef().getQueryAsForm();
    int i = 0;
    while (filterExists && isCompatible) {
      // Build predicat for filters param
      String index = TEMPLATE_PARAM.replace("#", Integer.toString(i++));
      String formParam = params.getFirstValue(index);
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
          String propertyName = parameters[PROPERTY];
          @SuppressWarnings("unchecked")
          List<SitoolsProperty> properties = (List<SitoolsProperty>) context.getAttributes().get(
              ContextAttributes.LIST_SITOOLS_PROPERTIES);
          if (properties == null || properties.isEmpty()) {
            isCompatible = false;
          }
          else if (parameters.length > VALUES) {

            SitoolsProperty property = getProperty(propertyName, properties);
            if (property == null) {
              isCompatible = false;
            }
            else if (checkValues(parameters, property)) {
              try {
                BigDecimal propertyValue = new BigDecimal(property.getValue());
                BigDecimal from = (BigDecimal) numericBetween.getFrom();
                BigDecimal to = (BigDecimal) numericBetween.getTo();
                // if (!(numericBetween.getFrom() <= propertyValue && propertyValue <= numericBetween.getTo())) {
                if (!((from.compareTo(propertyValue) != 1) && (propertyValue.compareTo(to) != 1))) {
                  isCompatible = false;
                }
              }
              catch (NumberFormatException e) {
                throw new ResourceException(Status.SERVER_ERROR_INTERNAL, "Property value not numeric", e);
              }

            }
            else {
              isCompatible = false;
            }
          }
          else {
            throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "Not enough parameters given");

          }
        }
      }
      else {
        filterExists = false;
      }
    }

    return isCompatible;
  }

  /**
   * Check values of the form
   * 
   * @param parameters
   *          the parameters of the filter
   * @param property
   *          the {@link SitoolsProperty}
   * @return true if values agree
   */
  private boolean checkValues(String[] parameters, SitoolsProperty property) {
    if (!SitoolsPropertyType.Numeric.equals(property.getType())) {
      throw new ResourceException(Status.SERVER_ERROR_INTERNAL,
          "Property type is not numeric, cannot perform a numeric filter");
    }
    String[] values = Arrays.copyOfRange(parameters, VALUES, VALUES + NUMBER_OF_VALUES);
    if (values.length == NUMBER_OF_VALUES) {
      Number value1;
      Number value2;
      try {
        value1 = new BigDecimal(values[0]);
        value2 = new BigDecimal(values[1]);
      }
      catch (NumberFormatException e) {
        throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "Not numeric value entered");
      }
      if (value1 != null && value2 != null) {
        numericBetween = new NumericBetweenSelection(value1, value2);
        return true;
      }
    }
    return false;
  }
}

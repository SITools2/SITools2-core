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
package fr.cnes.sitools.properties.core;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.data.Form;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

import fr.cnes.sitools.common.application.ContextAttributes;
import fr.cnes.sitools.properties.AbstractPropertyFilter;
import fr.cnes.sitools.properties.model.DateBetweenSelection;
import fr.cnes.sitools.properties.model.SitoolsProperty;
import fr.cnes.sitools.properties.model.SitoolsPropertyType;
import fr.cnes.sitools.util.DateUtils;

/**
 * Date between filter type on property Handle search on Date property type
 * 
 * @author m.gond
 */
public class DateBetweenPropertyFilter extends AbstractPropertyFilter {

  /**
   * The number of values
   */
  private static final int NUMBER_OF_VALUES = 2;

  /**
   * The dateFormat to use
   */
  private String dateFormat;

  /**
   * The list of component that uses this filter
   */
  private enum TYPE_COMPONENT {
    /** DefaultType */
    DATE_BETWEEN
  }

  /** Numeric bewteen associated */
  private DateBetweenSelection dateBetween;

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
            dateFormat = DateUtils.SITOOLS_DATE_FORMAT;

            SitoolsProperty property = getProperty(propertyName, properties);
            if (property == null) {
              isCompatible = false;
            }
            else if (checkValues(parameters, property)) {

              Date propertyValue;
              try {
                propertyValue = DateUtils.parse(property.getValue(), dateFormat);
                if (propertyValue == null) {
                  throw new ResourceException(Status.SERVER_ERROR_INTERNAL, "Property date is Not a valid date : "
                      + property.getValue());
                }
                if (!(dateBetween.getFrom().before(propertyValue) && propertyValue.before(dateBetween.getTo()))) {
                  isCompatible = false;
                }
              }
              catch (ParseException e) {
                throw new ResourceException(Status.SERVER_ERROR_INTERNAL, "Property date is Not a valid date : "
                    + property.getValue(), e);
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
    if (!SitoolsPropertyType.Date.equals(property.getType())) {
      throw new ResourceException(Status.SERVER_ERROR_INTERNAL,
          "Property type is not date, cannot perform a date between filter");
    }
    String[] values = Arrays.copyOfRange(parameters, VALUES, VALUES + NUMBER_OF_VALUES);
    if (values.length == NUMBER_OF_VALUES) {

      Date date1 = null;
      Date date2 = null;
      try {
        date1 = DateUtils.parse(values[0], dateFormat);
        date2 = DateUtils.parse(values[1], dateFormat);
      }
      catch (ParseException e) {
        throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "Not a date");
      }

      if (date1 != null && date2 != null) {
        dateBetween = new DateBetweenSelection(date1, date2);
        return true;
      }
      else {
        throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "Not a date");
      }
    }
    return false;
  }

}

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

import java.util.List;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.data.Form;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

import fr.cnes.sitools.common.application.ContextAttributes;
import fr.cnes.sitools.properties.AbstractPropertyFilter;
import fr.cnes.sitools.properties.model.SitoolsProperty;
import fr.cnes.sitools.properties.model.SitoolsPropertyType;

/**
 * TextField filter on property
 * Handle search on String or Enum property type
 * 
 * 
 * @author m.gond
 */
public class TextFieldPropertyFilter extends AbstractPropertyFilter {

  /**
   * The list of component that uses this filter
   */
  private enum TYPE_COMPONENT {
    /** Map panel field type, boundary box */
    TEXTFIELD

  }

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
          else {

            SitoolsProperty property = getProperty(propertyName, properties);
            if (property == null) {
              isCompatible = false;
            }
            else if (parameters.length > VALUES) {

              // boolean ok = false;
              // String[] values = Arrays.copyOfRange(parameters, VALUES, parameters.length);
              // String[] valuesProperty = property.getValue().split("\\|");
              // for (int k = 0; k < values.length && !ok; k++) {
              // for (int j = 0; j < valuesProperty.length && !ok; j++) {
              // if (valuesProperty[j].equals(values[k])) {
              // ok = true;
              // }
              // }
              // }
              // isCompatible = ok;

              String value = parameters[VALUES];
              if (value == null) {
                throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "Given value is null");
              }
              String valueProperty = property.getValue();
              if (valueProperty == null) {
                throw new ResourceException(Status.SERVER_ERROR_INTERNAL, "Property value is null");
              }
              if (property.getType().equals(SitoolsPropertyType.String)) {
                isCompatible = value.equals(valueProperty);
              }
              else if (property.getType().equals(SitoolsPropertyType.Enum)) {
                String[] valuesProperty = property.getValue().split("\\|");
                for (int j = 0; j < valuesProperty.length && !isCompatible; j++) {
                  if (value.equals(valuesProperty[i])) {
                    isCompatible = true;
                  }
                }
              }
              else {
                throw new ResourceException(Status.SERVER_ERROR_INTERNAL,
                    "Unsupported property type, must be String or Enum");
              }

            }
            else {
              throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "Not enough parameters given");
            }
          }
        }
      }
      else {
        filterExists = false;
      }
    }

    return isCompatible;
  }
}

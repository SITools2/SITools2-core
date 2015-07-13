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
package fr.cnes.sitools.properties;

import java.util.List;

import org.restlet.Context;
import org.restlet.Request;

import fr.cnes.sitools.properties.model.SitoolsProperty;
/**
 * Abstract class for Filter on property
 * 
 *
 * @author m.gond
 */
public abstract class AbstractPropertyFilter {

  /**
   * The index of COLUMN
   */
  public static final int TYPE = 0;
  /**
   * The index of COLUMN
   */
  public static final int PROPERTY = 1;
  /**
   * The index of Values
   */
  public static final int VALUES = 2;

  /** The TEMPLATE_PARAM */
  public static final String TEMPLATE_PARAM = "k[#]";

  /**
   * Return true if the properties contained in the Context, matches the request parameters false otherwise
   * 
   * 
   * @param request
   *          The {@link Request} containing the request parameters
   * @param context
   *          the {@link Context} containing the properties
   *          (getContext().getAttributes().get(ContextAttributes.LIST_SITOOLS_PROPERTIES)
   * @return true if the properties contains in the Context, matches the request parameters false otherwise
   */
  public abstract boolean match(Request request, Context context);

  /**
   * Get a {@link SitoolsProperty} for a given propertyName in the given properties list
   * 
   * @param propertyName
   *          the name of the property
   * @param properties
   *          the List of {@link SitoolsProperty}
   * @return the DataSetProperty with the given name or null if it is not found
   */
  public SitoolsProperty getProperty(String propertyName, List<SitoolsProperty> properties) {
    SitoolsProperty property = null;
    if (properties != null) {
      for (SitoolsProperty prop : properties) {
        if (prop.getName().equals(propertyName)) {
          property = prop;
          break;
        }
      }
    }
    return property;

  }

}

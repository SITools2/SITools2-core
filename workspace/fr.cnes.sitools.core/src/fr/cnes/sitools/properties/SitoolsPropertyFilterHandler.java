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
package fr.cnes.sitools.properties;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.restlet.Context;
import org.restlet.Request;

import fr.cnes.sitools.properties.core.DateBetweenPropertyFilter;
import fr.cnes.sitools.properties.core.NumericBetweenPropertyFilter;
import fr.cnes.sitools.properties.core.NumericPropertyFilter;
import fr.cnes.sitools.properties.core.TextFieldPropertyFilter;

/**
 * Default Handler for Property filters Declares all the default PropertyFilters and apply
 * <p>
 * To change the list of PropertyFilter, just override this class and override the constructor. In the constructor just
 * add new AbstractPropertyFilter to the list and that's it.
 * </p>
 * 
 * @author m.gond
 */
public class SitoolsPropertyFilterHandler {
  /** List of PropertyFilters */
  private List<AbstractPropertyFilter> filters;

  /**
   * Default constructor. Instantiate default PropertyFilters
   */
  public SitoolsPropertyFilterHandler() {
    // instantiate the list of AbstractPropertyFilter
    List<AbstractPropertyFilter> filtersConstructor = new ArrayList<AbstractPropertyFilter>();
    filtersConstructor.add(new TextFieldPropertyFilter());
    filtersConstructor.add(new NumericBetweenPropertyFilter());
    filtersConstructor.add(new DateBetweenPropertyFilter());
    filtersConstructor.add(new NumericPropertyFilter());
    this.setFilters(filtersConstructor);
  }

  /**
   * Match method that return true if the properties contained in the Context matches the request parameters false
   * otherwise
   * 
   * @param request
   *          The {@link Request} containing the request parameters
   * @param context
   *          the {@link Context} containing the properties
   *          (getContext().getAttributes().get(ContextAttributes.LIST_SITOOLS_PROPERTIES)
   * @return true if the properties contained in the Context matches the request parameters false otherwise
   */
  public boolean match(Request request, Context context) {
    boolean result = true;
    for (Iterator<AbstractPropertyFilter> iterator = getFilters().iterator(); iterator.hasNext() && result;) {
      AbstractPropertyFilter filter = iterator.next();
      result = result && filter.match(request, context);
    }
    return result;
  }

  /**
   * Gets the filters value
   * 
   * @return the filters
   */
  public List<AbstractPropertyFilter> getFilters() {
    return filters;
  }

  /**
   * Sets the value of filters
   * 
   * @param filters
   *          the filters to set
   */
  public void setFilters(List<AbstractPropertyFilter> filters) {
    this.filters = filters;
  }

}

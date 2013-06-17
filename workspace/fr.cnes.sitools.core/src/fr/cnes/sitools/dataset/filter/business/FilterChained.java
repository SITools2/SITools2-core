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
package fr.cnes.sitools.dataset.filter.business;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.restlet.Request;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

import fr.cnes.sitools.common.validator.ConstraintViolation;
import fr.cnes.sitools.common.validator.Validator;
import fr.cnes.sitools.dataset.model.Predicat;

/**
 * A chain of filters
 * 
 * @author AKKA
 */
public final class FilterChained extends AbstractFilter {

  /**
   * Name of the model.
   */
  private String name;

  /**
   * The list of filters inside the model.
   */
  private List<AbstractFilter> filters;

  /**
   * Constructor.
   * 
   * @param n
   *          the name of the model
   * @param d
   *          the description of the model
   */
  public FilterChained(final String n, final String d) {
    super();
    this.filters = new ArrayList<AbstractFilter>();
    setName(n);
    setDescription(d);
  }

  /**
   * Constructor
   */
  public FilterChained() {
    this.filters = new ArrayList<AbstractFilter>();
  }

  /**
   * Get the model name.
   * 
   * @return the model name
   */
  public String getModelName() {
    return name;
  }

  /**
   * Returns the list of filters.
   * 
   * @return the list of filters
   */
  public List<AbstractFilter> getFilters() {
    return filters;
  }

  /**
   * Add a filter in the model.
   * 
   * @param ac
   *          the filter to add
   */
  public void addFilter(final AbstractFilter ac) {
    filters.add(ac);
  }

  /**
   * Delete a filter from the model.
   * 
   * @param filtername
   *          the name of the filter to delete
   * @throws Exception
   *           when filter to remove is not found
   */
  public void deleteFilter(final String filtername) throws Exception {
    boolean isfound = false;
    for (int i = 0; i < filters.size(); i++) {
      AbstractFilter c = filters.get(i);
      if (c.getName().compareTo(filtername) == 0) {
        isfound = true;
        filters.remove(i);
        break;
      }
    }
    if (!isfound) {
      throw new Exception("Filter Model : filter to remove not found");
    }
  }

  @Override
  public List<Predicat> createPredicats(Request request, List<Predicat> predicats) throws Exception {
    if (predicats == null) {
      predicats = new ArrayList<Predicat>();
    }
    List<Predicat> out = null;
    if (filters.size() > 0) {
      for (int i = 0; i < filters.size(); i++) {
        try {           
          out = filters.get(i).createPredicats(request, predicats);
          if (out != null) {
            predicats = out;
          }
        }
        catch (Exception e) {
          throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
        }
      }
    }
    if (out == null) {
      return predicats;
    }
    return out;
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

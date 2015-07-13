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
package fr.cnes.sitools.plugins.filters.business;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.restlet.Context;
import org.restlet.routing.Filter;

import fr.cnes.sitools.common.exception.SitoolsException;
import fr.cnes.sitools.plugins.filters.model.FilterModel;

/**
 * FilterFactory
 * 
 * @author jp.boignard (AKKA Technologies)
 */
public final class FilterFactory {

  /**
   * Default constructor, Factory Class so private constructor
   */
  private FilterFactory() {
    super();
  }

  /**
   * getInstance
   * 
   * @param context
   *          the context
   * @param filterId
   *          the filterId
   * @param model
   *          the model
   * @return the instanciate Filter
   * @throws SitoolsException
   *           if there is an error during the instantiation of the Filter
   */
  @SuppressWarnings("unchecked")
  public static Filter getInstance(Context context, String filterId, FilterModel model) throws SitoolsException {
    Filter filterInstance = null;
    try {
      @SuppressWarnings("rawtypes")
      Class classImpl = Class.forName(model.getFilterClassName());

      Class<?>[] objParam = new Class<?>[1];
      objParam[0] = Context.class;

      Constructor<Filter> constructor;

      constructor = classImpl.getConstructor(objParam);

      context.getAttributes().put("FILTER_ID", filterId);
      context.getAttributes().put("FILTER_MODEL", model);

      filterInstance = constructor.newInstance(context);
    }
    catch (SecurityException e) {
      throw new SitoolsException(e.getMessage(), e);
    }
    catch (IllegalArgumentException e) {
      throw new SitoolsException(e.getMessage(), e);
    }
    catch (ClassNotFoundException e) {
      throw new SitoolsException(e.getMessage(), e);
    }
    catch (NoSuchMethodException e) {
      throw new SitoolsException(e.getMessage(), e);
    }
    catch (InstantiationException e) {
      throw new SitoolsException(e.getMessage(), e);
    }
    catch (IllegalAccessException e) {
      throw new SitoolsException(e.getMessage(), e);
    }
    catch (InvocationTargetException e) {
      throw new SitoolsException(e.getMessage(), e);
    }

    return filterInstance;
  }
}

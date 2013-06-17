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
package fr.cnes.sitools.common.store;

import java.io.Closeable;
import java.util.List;

import fr.cnes.sitools.common.model.IResource;
import fr.cnes.sitools.common.model.ResourceCollectionFilter;

/**
 * Base class for Store interfaces
 * @param <T> Class derived from IResource to allow basic fields for serialization
 * @author m.marseille (AKKA technologies)
 */
public interface SitoolsStore<T extends IResource> extends Closeable {
  
  /**
   * Method for getting all objects
   * @return Array
   */
  T[] getArray();

  /**
   * Method for getting objects according to the XQuery
   * @param xquery
   *          String with XQuery syntax
   * @return Array
   */
  T[] getArrayByXQuery(String xquery);

  /**
   * Method for getting objects according to the specified filter
   * @param filter
   *          criteria (pagination, ...)
   * @return Array
   */
  T[] getArray(ResourceCollectionFilter filter);

  /**
   * Method for getting all objects
   * @return ArrayList of objects
   */
  List<T> getList();

  /**
   * Method for getting objects with specific criteria
   * @param filter
   *          criteria (pagination, ...)
   * @return ArrayList of objects
   */
  List<T> getList(ResourceCollectionFilter filter);

  /**
   * Method for getting objects according to the pagination criteria
   * 
   * @param filter
   *          pagination
   * @param resources
   *          input
   * @return ArrayList of objects
   */
  List<T> getPage(ResourceCollectionFilter filter, List<T> resources);

  /**
   * Method for getting objects with XQuery request syntax
   * 
   * @param xquery
   *          String
   * @return ArrayList of objects
   */
  List<T> getListByXQuery(String xquery);

  /**
   * Method for creating a object
   * 
   * @param resource
   *          input
   * @return created object
   */
  T create(T resource);

  /**
   * Method for retrieving a object by its id
   * 
   * @param id
   *          project identifier
   * @return retrieved object
   */
  T retrieve(String id);

  /**
   * Method for updating a object
   * 
   * @param resource
   *          input
   * @return updated object
   */
  T update(T resource);

  /**
   * Method for deleting a object by its id
   * 
   * @param id
   *          object identifier
   * @return true if deleted
   */
  boolean delete(String id);
  
  /**
   * Get the list of object by parent ID
   * @param id the parent ID
   * @return the list of resource objects
   */
  List<T> retrieveByParent(String id);


}

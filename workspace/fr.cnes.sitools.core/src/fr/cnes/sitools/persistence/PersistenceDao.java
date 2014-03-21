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
package fr.cnes.sitools.persistence;

import java.util.Collection;

import fr.cnes.sitools.common.model.ResourceCollectionFilter;

/**
 * Persistence DAO interface
 * 
 * @author AKKA
 * 
 * @param <E>
 */
public interface PersistenceDao<E extends Persistent> {

  /**
   * Get an element by ID
   * 
   * @param id
   *          the ID
   * @return an element
   */
  E get(String id);

  /**
   * Save an element
   * 
   * @param o
   *          the element to save
   */
  void save(E o);

  /**
   * Update an element
   * 
   * @param o
   *          the element to save
   */
  void update(E o);

  /**
   * Save a collection of elements
   * 
   * @param os
   *          the collection to save
   */
  void saveAll(Collection<E> os);

  /**
   * Get the list of elements
   * 
   * @return the list
   */
  Collection<E> getList();
  
  /**
   * Get the list of elements
   * @param filter query filter
   * @return the list
   */
  Collection<E> getList(ResourceCollectionFilter filter);

  /**
   * Get the list of elements
   * @param filter query filter
   * @param result the full result list
   * @return the list 
   */
  Collection<E> getPage(ResourceCollectionFilter filter, Collection<E> result);
    
  /**
   * Delete an element
   * 
   * @param o
   *          the element to delete
   */
  void delete(E o);

}

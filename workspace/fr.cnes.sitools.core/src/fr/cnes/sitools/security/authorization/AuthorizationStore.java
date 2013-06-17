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
package fr.cnes.sitools.security.authorization;

import java.io.Closeable;
import java.util.List;

import fr.cnes.sitools.common.model.ResourceCollectionFilter;
import fr.cnes.sitools.security.authorization.client.ResourceAuthorization;

/**
 * Interface for managing ResourceAuthorization objects persistence.
 * 
 * @author jp.boignard (AKKA Technologies)
 * 
 */
public interface AuthorizationStore extends Closeable {
  /**
   * Method for getting all objects
   * 
   * @return Array
   */
  ResourceAuthorization[] getArray();

  /**
   * Method for getting objects according to the XQuery
   * 
   * @param xquery
   *          String with XQuery syntax
   * @return Array
   */
  ResourceAuthorization[] getArrayByXQuery(String xquery);

  /**
   * Method for getting authorizations according to the specified filter
   * 
   * @param filter
   *          criteria (pagination, ...)
   * @return Array
   */
  ResourceAuthorization[] getArray(ResourceCollectionFilter filter);

  /**
   * Method for getting authorizations according to the specified filter and type
   * 
   * @param filter  criteria (pagination, ...)
   * @param resourceType Resource type
   * @return ResourceAuthorization 
   */
  ResourceAuthorization[] getArrayByType(ResourceCollectionFilter filter, String resourceType);
  /**
   * Method for getting all authorization
   * 
   * @return List of authorization
   */
  List<ResourceAuthorization> getList();

  /**
   * Method for getting authorization with specific criteria
   * 
   * @param filter
   *          criteria (pagination, ...)
   * @return List of authorization
   */
  List<ResourceAuthorization> getList(ResourceCollectionFilter filter);

  /**
   * Method for getting authorization with specific criteria
   * 
   * @param filter criteria (pagination, ...)
   * @param resourceType Resource type
   * @return List of authorization
   */
  List<ResourceAuthorization> getListByType(ResourceCollectionFilter filter, String resourceType);
  /**
   * Method for getting authorization with XQuery request syntax
   * 
   * @param xquery
   *          String
   * @return List of authorization
   */
  List<ResourceAuthorization> getListByXQuery(String xquery);

  /**
  * Method for getting authorizations according to the pagination criteria
  *
  * @param filter
  * pagination
  * @param authorizations
  * input
  * @return List of authorizations
  */
  List<ResourceAuthorization> getPage(ResourceCollectionFilter filter, List<ResourceAuthorization> authorizations);
  
  /**
   * Method for creating a ResourceAuthorization
   * 
   * @param authorization
   *          input
   * @return created ResourceAuthorization
   */
  ResourceAuthorization create(ResourceAuthorization authorization);

  /**
   * Method for retrieving a authorization by its id
   * 
   * @param id
   *          authorization identifier
   * @return retrieved authorization
   */
  ResourceAuthorization retrieve(String id);

  /**
   * Method for updating a authorization
   * 
   * @param authorization
   *          input
   * @return updated authorization
   */
  ResourceAuthorization update(ResourceAuthorization authorization);

  /**
   * Method for deleting a ResourceAuthorization by its id
   * 
   * @param id
   *          authorization identifier
   * @return true if deleted
   */
  boolean delete(String id);

}

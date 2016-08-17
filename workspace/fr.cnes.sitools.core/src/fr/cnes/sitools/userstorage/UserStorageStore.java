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
package fr.cnes.sitools.userstorage;

import java.io.Closeable;
import java.util.List;

import fr.cnes.sitools.common.exception.SitoolsException;
import fr.cnes.sitools.common.model.ResourceCollectionFilter;
import fr.cnes.sitools.userstorage.model.UserStorage;

/**
 * Interface for managing UserStorage objects persistence.
 * 
 * @author jp.boignard (AKKA Technologies)
 * 
 * @deprecated use UserStorageStoreInterface
 */
public interface UserStorageStore extends Closeable {
  /**
   * Method for getting all objects
   * 
   * @return Array
   */
  UserStorage[] getArray();

  /**
   * Method for getting userStorages according to the specified filter
   * 
   * @param filter
   *          criteria (pagination, ...)
   * @return Array
   */
  UserStorage[] getArray(ResourceCollectionFilter filter);

  /**
   * Method for getting all userStorage
   * 
   * @return ArrayList of userStorage
   */
  List<UserStorage> getList();

  /**
   * Method for getting userStorage with specific criteria
   * 
   * @param filter
   *          criteria (pagination, ...)
   * @return ArrayList of userStorage
   */
  List<UserStorage> getList(ResourceCollectionFilter filter);

  /**
   * Method for getting userStorages according to the pagination criteria
   * 
   * @param filter
   *          pagination
   * @param userStorages
   *          input
   * @return ArrayList of userStorage
   */
  List<UserStorage> getPage(ResourceCollectionFilter filter, List<UserStorage> userStorages);

  /**
   * Method for creating a UserStorage
   * 
   * @param userStorage
   *          UserStorage input
   * @return created UserStorage
   * @throws SitoolsException
   *           if error occurs when creating a new UserStorage
   */
  UserStorage create(UserStorage userStorage) throws SitoolsException;

  /**
   * Method for retrieving a userStorage by its id
   * 
   * @param id
   *          userStorage identifier
   * @return retrieved userStorage
   */
  UserStorage retrieve(String id);

  /**
   * Method for updating a userStorage
   * 
   * @param userStorage
   *          input
   * @return updated userStorage
   */
  UserStorage update(UserStorage userStorage);

  /**
   * Method for deleting a UserStorage by its id
   * 
   * @param id
   *          userStorage identifier
   * @return true if deleted
   */
  boolean delete(String id);

}

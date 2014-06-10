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
package fr.cnes.sitools.userstorage;

import java.io.File;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.restlet.Context;

import fr.cnes.sitools.common.exception.SitoolsException;
import fr.cnes.sitools.common.model.ResourceCollectionFilter;
import fr.cnes.sitools.common.model.ResourceComparator;
import fr.cnes.sitools.persistence.XmlMapStore;
import fr.cnes.sitools.tasks.model.TaskModel;
import fr.cnes.sitools.userstorage.model.UserStorage;

/**
 * Storage of dimensions
 * 
 * @author jp.boignard (AKKA technologies)
 */
public class UserStorageStoreXMLMap extends XmlMapStore<UserStorage> implements UserStorageStoreInterface {

  /** default location for file persistence */
  private static final String COLLECTION_NAME = UserStorage.class.getSimpleName().toLowerCase();

  /**
   * Constructor without file
   * 
   * @param context
   *          the Restlet Context
   */
  public UserStorageStoreXMLMap(Context context) {
    super(UserStorage.class, context);
    File defaultLocation = new File(COLLECTION_NAME);
    init(defaultLocation);
  }

  /**
   * Constructor with file location
   * 
   * @param location
   *          the file location
   * @param context
   *          the Restlet Context
   */
  public UserStorageStoreXMLMap(File location, Context context) {
    super(UserStorage.class, location, context);
  }

  @Override
  public void init(File location) {
    Map<String, Class<?>> aliases = new ConcurrentHashMap<String, Class<?>>();
    aliases.put("userStorage", UserStorage.class);
    this.init(location, aliases);
  }

  @Override
  public UserStorage create(UserStorage resource) {
    // Check if resource id
    if (resource.getUserId() == null || "".equals(resource.getUserId())) {
      throw new RuntimeException("USERSTORAGE_USERIDENTIFIER_MANDATORY");
    }
    resource.setId(resource.getUserId());

    return super.create(resource);
  }

  @Override
  public List<UserStorage> retrieveByParent(String id) {
    throw new RuntimeException(SitoolsException.NOT_IMPLEMENTED);
  }

  @Override
  public void sort(List<UserStorage> result, ResourceCollectionFilter filter) {
    if ((filter != null) && (filter.getSort() != null) && !filter.getSort().equals("")) {
      Collections.sort(result, new ResourceComparator<UserStorage>(filter) {
        @Override
        public int compare(UserStorage arg0, UserStorage arg1) {
          if (arg0.getUserId() == null) {
            return 1;
          }
          if (arg1.getUserId() == null) {
            return -1;
          }
          String s1 = (String) arg0.getUserId();
          String s2 = (String) arg1.getUserId();

          return super.compare(s1, s2);
        }
      });
    }
  }

  @Override
  public String getCollectionName() {
    return COLLECTION_NAME;
  }

  @Override
  public UserStorage update(UserStorage userStorage) {
    UserStorage result = null;

    Map<String, UserStorage> map = getMap();
    UserStorage current = map.get(userStorage.getId());
    result = current;
    current.setStorage(userStorage.getStorage());
    current.setStatus(userStorage.getStatus());
    
    map.put(userStorage.getId(), current);
    return result;
  }

}

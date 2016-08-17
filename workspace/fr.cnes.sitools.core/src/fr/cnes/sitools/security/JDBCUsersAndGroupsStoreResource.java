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
package fr.cnes.sitools.security;

import java.lang.reflect.Field;
import java.util.ResourceBundle;

import org.restlet.engine.Engine;

/**
 * SQL Request
 * 
 * @author jp.boignard (AKKA Technologies)
 */
public abstract class JDBCUsersAndGroupsStoreResource {

  /** SQL request */
  public String CREATE_GROUP;
  /** SQL request */
  public String CREATE_GROUPUSERS;
  /** SQL request */
  public String CREATE_USER;
  /** SQL request */
  public String CREATE_USER_PROPERTY;
  /** SQL request */
  public String DELETE_GROUP;
  /** SQL request */
  public String DELETE_GROUPUSERS;
  /** SQL request */
  public String DELETE_USER;
  /** SQL request */
  public String DELETE_USER_PROPERTY;
  /** SQL request */
  public String SELECT_GROUP_BY_ID;
  /** SQL request */
  public String SELECT_GROUPS;
  /** SQL request */
  public String SELECT_GROUPS_BY_USER;
  /** SQL request */
  public String SELECT_USER_BY_ID;
  /** SQL request */
  public String SELECT_USER_PROPERTY;
  /** SQL request */
  public String SELECT_USERS;
  /** SQL request */
  public String SELECT_USERS_BY_GROUP;
  /** SQL request */
  public String UPDATE_GROUP;
  /** SQL request */
  public String UPDATE_USER_WITH_PW;
  /** SQL request */
  public String UPDATE_USER_WITHOUT_PW;

  /**
   * initializeMessages reading the resource bundle file
   * 
   * @param bundleName
   *          resource bundle name
   * @param properties
   *          Class name to be reflected
   */
  public static void initializeMessages(String bundleName, JDBCUsersAndGroupsStoreResource properties) {
    ResourceBundle bundle = ResourceBundle.getBundle(bundleName);
    for (Field field : properties.getClass().getFields()) {
      try {
        field.setAccessible(true);
        field.set(properties, bundle.getString(field.getName()));
      }
      catch (IllegalArgumentException e) {
        Engine.getLogger("JDBCUsersAndGroupsStoreResource").severe(e.getMessage());
      }
      catch (IllegalAccessException e) {
        Engine.getLogger("JDBCUsersAndGroupsStoreResource").severe(e.getMessage());
      }
    }
  }

}

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
package fr.cnes.sitools.role;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.restlet.Context;

import fr.cnes.sitools.common.store.SitoolsStoreXML;
import fr.cnes.sitools.role.model.Role;

/**
 * RoleStore implementation with XStream FilePersistenceStrategy
 * 
 * @author jp.boignard (AKKA Technologies)
 * 
 */
@Deprecated
public final class RoleStoreXML extends SitoolsStoreXML<Role> {

  /** default location for file persistence */
  private static final String COLLECTION_NAME = "roles";

  /**
   * Constructor with the XML file location
   * 
   * @param location
   *          file location
   * @param context
   *          the Restlet Context
   */
  public RoleStoreXML(File location, Context context) {
    super(Role.class, location, context);
  }

  /**
   * Default Constructor
   * 
   * @param context
   *          the Restlet Context
   */
  public RoleStoreXML(Context context) {
    super(Role.class, context);
    File defaultLocation = new File(COLLECTION_NAME);
    init(defaultLocation);
  }

  /**
   * XStream FilePersistenceStrategy initialization
   * 
   * @param location
   *          Directory
   */
  public void init(File location) {
    Map<String, Class<?>> aliases = new ConcurrentHashMap<String, Class<?>>();
    aliases.put("role", Role.class);
    this.init(location, aliases);
  }

  public Role update(Role role) {
    Role result = null;
    for (Iterator<Role> it = getRawList().iterator(); it.hasNext();) {
      Role current = it.next();
      if (current.getId().equals(role.getId())) {
        getLog().info("Updating role");

        result = current;
        current.setName(role.getName());
        current.setDescription(role.getDescription());
        current.setUsers(role.getUsers());
        current.setGroups(role.getGroups());
        it.remove();

        break;
      }
    }
    if (result != null) {
      getRawList().add(result);
    }
    return result;
  }

  public List<Role> retrieveByParent(String id) {
    return null;
  }

  @Override
  public String getCollectionName() {
    return "role";
  }

}

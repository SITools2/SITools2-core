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
package fr.cnes.sitools.datasource.jdbc;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.restlet.Context;

import fr.cnes.sitools.datasource.jdbc.model.JDBCDataSource;
import fr.cnes.sitools.persistence.XmlMapStore;

public class JDBCDataSourceStoreXMLMap extends XmlMapStore<JDBCDataSource> implements
    JDBCDataSourceStoreInterface {

  /** Default location for file persistence */
  private static final String COLLECTION_NAME = "datasources";

  /**
   * Constructor with the XML file location
   * 
   * @param location
   *          Directory for file persistence
   * @param context
   *          the Restlet Context
   */
  public JDBCDataSourceStoreXMLMap(File location, Context context) {
    super(JDBCDataSource.class, location, context);
  }

  /**
   * Default constructor
   * 
   * @param context
   *          the Restlet Context
   */
  public JDBCDataSourceStoreXMLMap(Context context) {
    super(JDBCDataSource.class, context);
    File defaultLocation = new File(COLLECTION_NAME);
    getLog().info("Store location " + defaultLocation.getAbsolutePath());
    init(defaultLocation);
  }

  @Override
  public List<JDBCDataSource> retrieveByParent(String id) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String getCollectionName() {
    return COLLECTION_NAME;
  }

  @Override
  public void init(File location) {
    Map<String, Class<?>> aliases = new ConcurrentHashMap<String, Class<?>>();
    aliases.put("jdbcdatasource", JDBCDataSource.class);
    this.init(location, aliases);
  }

  @Override
  public JDBCDataSource update(JDBCDataSource datasource) {
    JDBCDataSource result = null;
    Map<String, JDBCDataSource> map = getMap();
    JDBCDataSource current = map.get(datasource.getId());
    
    if (current == null) {
      getLog().warning("Cannot update " + COLLECTION_NAME + " that doesn't already exists");
      return null;
    }
    getLog().info("Updating DataSource");
    
    result = current;
    current.setName(datasource.getName());
    current.setDescription(datasource.getDescription());
    current.setDriverClass(datasource.getDriverClass());
    current.setUrl(datasource.getUrl());
    current.setUserLogin(datasource.getUserLogin());
    if (datasource.getUserPassword() != null) {
      current.setUserPassword(datasource.getUserPassword());
    }
    // FIXME une modification du datasource => detachement.
    current.setStatus(datasource.getStatus());
    current.setMaxActive(datasource.getMaxActive());
    current.setInitialSize(datasource.getInitialSize());
    current.setSchemaOnConnection(datasource.getSchemaOnConnection());
    current.setSitoolsAttachementForUsers(datasource.getSitoolsAttachementForUsers());
    current.setLastStatusUpdate(new Date());

    if (result != null) {
      map.put(datasource.getId(), current);
    }
    return result;
  }

}

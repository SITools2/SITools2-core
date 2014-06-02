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
package fr.cnes.sitools.datasource.mongodb;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.restlet.Context;

import fr.cnes.sitools.datasource.mongodb.model.MongoDBDataSource;
import fr.cnes.sitools.persistence.XmlMapStore;

public final class MongoDBDataSourceStoreXMLMap extends XmlMapStore<MongoDBDataSource> implements
    MongoDBDataSourceStoreInterface {

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
  public MongoDBDataSourceStoreXMLMap(File location, Context context) {
    super(MongoDBDataSource.class, location, context);
  }

  /**
   * Default constructor
   * 
   * @param context
   *          the Restlet Context
   */
  public MongoDBDataSourceStoreXMLMap(Context context) {
    super(MongoDBDataSource.class, context);
    File defaultLocation = new File(COLLECTION_NAME);
    getLog().info("Store location " + defaultLocation.getAbsolutePath());
    init(defaultLocation);
  }

  @Override
  public List<MongoDBDataSource> retrieveByParent(String id) {
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
    aliases.put("mongodbdatasource", MongoDBDataSource.class);
    this.init(location, aliases);
  }

  @Override
  public MongoDBDataSource update(MongoDBDataSource datasource) {
    MongoDBDataSource result = null;

    getLog().info("Updating DataSource");

    Map<String, MongoDBDataSource> map = getMap();
    MongoDBDataSource current = map.get(datasource.getId());

    result = current;

    current.setName(datasource.getName());
    current.setDescription(datasource.getDescription());
    current.setUrl(datasource.getUrl());
    current.setUserLogin(datasource.getUserLogin());
    if (datasource.getUserPassword() != null) {
      current.setUserPassword(datasource.getUserPassword());
    }
    // FIXME une modification du datasource => detachement.
    current.setStatus(datasource.getStatus());
    current.setMaxActive(datasource.getMaxActive());
    current.setSitoolsAttachementForUsers(datasource.getSitoolsAttachementForUsers());
    current.setDatabaseName(datasource.getDatabaseName());
    current.setPortNumber(datasource.getPortNumber());
    current.setDriverClass(datasource.getDriverClass());
    current.setAuthentication(datasource.isAuthentication());

    if (result != null) {
      map.put(datasource.getId(), current);
    }
    return result;
  }

}

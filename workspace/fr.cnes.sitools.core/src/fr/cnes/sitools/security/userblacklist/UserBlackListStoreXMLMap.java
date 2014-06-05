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
package fr.cnes.sitools.security.userblacklist;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import org.restlet.Context;
import org.restlet.engine.Engine;

import fr.cnes.sitools.common.exception.SitoolsException;
import fr.cnes.sitools.persistence.XmlMapStore;

/**
 * Storage of dimensions
 * 
 * @author jp.boignard (AKKA technologies)
 */
public class UserBlackListStoreXMLMap extends XmlMapStore<UserBlackListModel> implements
    UserBlackListStoreInterface {

  /** default location for file persistence */
  private static final String COLLECTION_NAME = "userBlacklist";

  /** static logger for this store implementation */
  private static Logger log = Engine.getLogger(UserBlackListStoreXMLMap.class.getName());

  /**
   * Constructor without file
   * 
   * @param context
   *          the Restlet Context
   */
  public UserBlackListStoreXMLMap(Context context) {
    super(UserBlackListModel.class, context);
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
  public UserBlackListStoreXMLMap(File location, Context context) {
    super(UserBlackListModel.class, location, context);
  }

  @Override
  public List<UserBlackListModel> retrieveByParent(String id) {
    throw new RuntimeException(SitoolsException.NOT_IMPLEMENTED);
  }

  @Override
  public void init(File location) {
    Map<String, Class<?>> aliases = new ConcurrentHashMap<String, Class<?>>();
    aliases.put("userBlackListModel", UserBlackListModel.class);
    this.init(location, aliases);
  }

  @Override
  public String getCollectionName() {
    return COLLECTION_NAME;
  }

}

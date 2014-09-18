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
package fr.cnes.sitools.registry;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import org.restlet.Context;
import org.restlet.engine.Engine;

import fr.cnes.sitools.common.model.ResourceCollectionFilter;
import fr.cnes.sitools.common.store.SitoolsStoreXML;
import fr.cnes.sitools.registry.model.AppRegistry;

/**
 * Implementation of ResourceManagerStore with XStream FilePersistenceStrategy
 * 
 * @author AKKA
 * 
 */
@Deprecated
public final class AppRegistryStoreXML extends SitoolsStoreXML<AppRegistry> {

  /** default location for file persistence */
  private static final String COLLECTION_NAME = "applications";

  /** static logger for this store implementation */
  private static Logger log = Engine.getLogger(AppRegistryStoreXML.class.getName());

  /**
   * Constructor with the XML file location
   * 
   * @param location
   *          directory of FilePersistenceStrategy
   * @param context
   *          the Context
   */
  public AppRegistryStoreXML(File location, Context context) {
    super(AppRegistry.class, location, context);
  }

  /**
   * Default constructor
   * 
   * @param context
   *          the Context
   */
  public AppRegistryStoreXML(Context context) {
    super(AppRegistry.class, context);
    File defaultLocation = new File(COLLECTION_NAME);
    init(defaultLocation);
  }

  @Override
  public AppRegistry update(AppRegistry manager) {
    AppRegistry result = null;
    for (Iterator<AppRegistry> it = getRawList().iterator(); it.hasNext();) {
      AppRegistry current = it.next();
      if (null == current) {
        log.warning(" AppRegistry.update. it is null.");
        continue;
      }

      if (current.getId().equals(manager.getId())) {
        log.fine("Updating ResourceManager");

        result = current;
        current.setName(manager.getName());
        current.setDescription(manager.getDescription());
        current.setLastUpdate(manager.getLastUpdate());
        current.setResources(manager.getResources());
        it.remove();

        break;
      }
    }
    if (result != null) {
      getRawList().add(result);
    }
    return result;
  }

  @Override
  public List<AppRegistry> getList(ResourceCollectionFilter filter) {
    List<AppRegistry> result = new ArrayList<AppRegistry>();
    if ((getRawList() == null) || (getRawList().size() <= 0) || (filter.getStart() > getRawList().size())) {
      return result;
    }

    // Filtre
    if ((filter.getQuery() != null) && !filter.getQuery().equals("")) {
      for (AppRegistry item : getRawList()) {
        if (null == item.getName()) {
          continue;
        }
        if (item.getName().toLowerCase().startsWith(filter.getQuery().toLowerCase())) {
          result.add(item);
        }
      }
    }
    else {
      result.addAll(getRawList());
    }

    // Si index premier element > nombre d'elements filtres => resultat vide
    if (filter.getStart() > result.size()) {
      result.clear();
      return result;
    }

    // Tri
    sort(result, filter);

    // Pagination
    int start = (filter.getStart() <= 0) ? 0 : filter.getStart() - 1;
    int limit = ((filter.getLimit() <= 0) || ((filter.getLimit() + start) > result.size())) ? (result.size() - start)
        : filter.getLimit();
    // subList
    // Returns a view of the portion of this list between the specified fromIndex, inclusive,
    // and toIndex, exclusive.
    List<AppRegistry> page = result.subList(start, start + limit); // pas -1 puisque exclusive

    return new ArrayList<AppRegistry>(page);
  }

  /**
   * XStream FilePersistenceStrategy initialization
   * 
   * @param location
   *          Directory
   */
  public void init(File location) {
    Map<String, Class<?>> aliases = new ConcurrentHashMap<String, Class<?>>();
    aliases.put("manager", AppRegistry.class);
    this.init(location, aliases);
  }

  @Override
  public List<AppRegistry> retrieveByParent(String id) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String getCollectionName() {
    return COLLECTION_NAME;
  }

}

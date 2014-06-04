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
package fr.cnes.sitools.plugins.filters;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.restlet.Context;

import fr.cnes.sitools.persistence.XmlMapStore;
import fr.cnes.sitools.plugins.filters.model.FilterModel;

/**
 * Implementation of FilterModelStore with XStream FilePersistenceStrategy
 * 
 * @author AKKA
 * 
 */
public class FilterPluginStoreXMLMap extends XmlMapStore<FilterModel> implements FilterPluginStoreInterface {

  /** default location for file persistence */
  private static final String COLLECTION_NAME = "filterPlugins";

  /**
   * Constructor with the XML file location
   * 
   * @param location
   *          directory of FilePersistenceStrategy
   * @param context
   *          the Restlet Context
   */
  public FilterPluginStoreXMLMap(File location, Context context) {
    super(FilterModel.class, location, context);
  }

  /**
   * Default constructor
   * 
   * @param context
   *          the Restlet Context
   */
  public FilterPluginStoreXMLMap(Context context) {
    super(FilterModel.class, context);
    File defaultLocation = new File(COLLECTION_NAME);
    init(defaultLocation);
  }

  @Override
  public FilterModel update(FilterModel filter) {
    FilterModel result = null;

    Map<String, FilterModel> map = getMap();
    FilterModel current = map.get(filter.getId());
    result = current;
    current.setParent(filter.getParent());
    current.setName(filter.getName());
    current.setDescription(filter.getDescription());
    current.setFilterClassName(filter.getFilterClassName());
    current.setParametersMap(filter.getParametersMap());
    current.setDescriptionAction(filter.getDescriptionAction());
    map.put(filter.getId(), current);
    return result;
  }

  /**
   * XStream FilePersistenceStrategy initialization
   * 
   * @param location
   *          Directory
   */
  public void init(File location) {
    Map<String, Class<?>> aliases = new ConcurrentHashMap<String, Class<?>>();
    aliases.put("filterPlugin", FilterModel.class);
    this.init(location, aliases);
  }

  @Override
  public List<FilterModel> retrieveByParent(String id) {
    List<FilterModel> result = new ArrayList<FilterModel>();
    for (Iterator<FilterModel> it = getList().iterator(); it.hasNext();) {
      FilterModel current = it.next();
      if (current.getParent().equals(id)) {
        result.add(current);
      }
    }
    return result;
  }

  @Override
  public String getCollectionName() {
    return COLLECTION_NAME;
  }

}

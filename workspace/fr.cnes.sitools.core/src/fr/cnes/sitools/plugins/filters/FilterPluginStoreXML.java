/*******************************************************************************
 * Copyright 2011, 2012 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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
/**
 * 
 */
package fr.cnes.sitools.plugins.filters;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.restlet.Context;

import fr.cnes.sitools.common.store.SitoolsStoreXML;
import fr.cnes.sitools.plugins.filters.model.FilterModel;

/**
 * Storage for filter plugins
 * 
 * @author jp.boignard (AKKA Technologies)
 */
public final class FilterPluginStoreXML extends SitoolsStoreXML<FilterModel> {

  /** default location for file persistence */
  private static final String COLLECTION_NAME = "filterPlugins";

  /**
   * Default constructor
   */
  public FilterPluginStoreXML(Context context) {
    super(FilterModel.class, context);
    File defaultLocation = new File(COLLECTION_NAME);
    init(defaultLocation);
  }

  /**
   * Constructor with the XML file location
   * 
   * @param location
   *          directory of FilePersistenceStrategy
   */
  public FilterPluginStoreXML(File location, Context context) {
    super(FilterModel.class, location, context);
  }

  @Override
  public void init(File location) {
    Map<String, Class<?>> aliases = new ConcurrentHashMap<String, Class<?>>();
    aliases.put("filterPlugin", FilterModel.class);
    this.init(location, aliases);
  }

  @Override
  public FilterModel update(FilterModel filter) {
    FilterModel result = null;
    for (Iterator<FilterModel> it = getRawList().iterator(); it.hasNext();) {
      FilterModel current = it.next();
      if (current.getId().equals(filter.getId())) {
        getLog().fine("Updating filter plugin");

        result = current;
        current.setParent(filter.getParent());
        current.setName(filter.getName());
        current.setDescription(filter.getDescription());
        current.setFilterClassName(filter.getFilterClassName());
        current.setParametersMap(filter.getParametersMap());
        current.setDescriptionAction(filter.getDescriptionAction());
        it.remove();
        break;
      }
    }
    if (result != null) {
      getRawList().add(result);
    }
    return result;
  }

  /**
   * Get the list of filters
   * 
   * @param id
   *          the parent id
   * @return the list
   */
  @Override
  public List<FilterModel> retrieveByParent(String id) {
    List<FilterModel> result = new ArrayList<FilterModel>();
    for (Iterator<FilterModel> it = getRawList().iterator(); it.hasNext();) {
      FilterModel current = it.next();
      if (current.getParent().equals(id)) {
        getLog().fine("Parent found");
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

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
package fr.cnes.sitools.dataset.filter;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.restlet.Context;

import fr.cnes.sitools.dataset.filter.model.FilterChainedModel;
import fr.cnes.sitools.dataset.filter.model.FilterModel;
import fr.cnes.sitools.dataset.filter.model.FilterParameter;
import fr.cnes.sitools.persistence.XmlMapStore;

/**
 * Implementation of filterStore with XStream FilePersistenceStrategy
 * 
 * @author AKKA
 * 
 */
public class FilterStoreXMLMap extends XmlMapStore<FilterChainedModel> implements FilterStoreInterface {

  /** default location for file persistence */
  private static final String COLLECTION_NAME = "filters";

  /**
   * Constructor with the XML file location
   * 
   * @param location
   *          directory of FilePersistenceStrategy
   * @param context
   *          the Context
   */
  public FilterStoreXMLMap(File location, Context context) {
    super(FilterChainedModel.class, location, context);
  }

  /**
   * Default constructor
   * 
   * @param context
   *          the Context
   */
  public FilterStoreXMLMap(Context context) {
    super(FilterChainedModel.class, context);
    File defaultLocation = new File(COLLECTION_NAME);
    init(defaultLocation);
  }

  @Override
  public List<FilterChainedModel> retrieveByParent(String id) {

    return null;
  }

  @Override
  public String getCollectionName() {
    return COLLECTION_NAME;
  }

  @Override
  public void init(File location) {
    Map<String, Class<?>> aliases = new ConcurrentHashMap<String, Class<?>>();
    aliases.put("filterChainedModel", FilterChainedModel.class);
    aliases.put("filterModel", FilterModel.class);
    aliases.put("filterParameter", FilterParameter.class);
    this.init(location, aliases);
  }

  @Override
  public FilterChainedModel update(FilterChainedModel filter) {
    FilterChainedModel result = null;

    Map<String, FilterChainedModel> map = getMap();
    FilterChainedModel current = map.get(filter.getId());
    if (current == null) {
      getLog().warning("Cannot update " + COLLECTION_NAME + " that doesn't already exists");
      return null;
    }
    if (current != null && current.getId().equals(filter.getId())) {
      getLog().info("Updating FilterChainedModel");

      result = current;

      current.setDescription(filter.getDescription());
      current.setName(filter.getName());
      current.setParent(filter.getParent());

      // generate ids for filters if new filter have been added
      // ajout des filterParameter dans le hashMap (parametersMap) et clean
      // du arrayList (parameters)
      if (filter.getFilters() != null) {
        FilterModel filterModel;
        for (Iterator<FilterModel> itConv = filter.getFilters().iterator(); itConv.hasNext();) {
          // generate Ids
          filterModel = itConv.next();
          if (filterModel.getId() == null || "".equals(filterModel.getId())) {
            filterModel.setId(UUID.randomUUID().toString());
          }
        }
      }
      current.setFilters(filter.getFilters());

      map.put(filter.getId(), current);
    }

    return result;
  }
}

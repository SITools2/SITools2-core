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
import java.util.List;
import java.util.Map;
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
public final class FilterStoreXMLMap extends XmlMapStore<FilterChainedModel> implements FilterStoreInterface {

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
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public FilterChainedModel update(FilterChainedModel resource) {
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
    aliases.put("filterChainedModel", FilterChainedModel.class);
    aliases.put("filterModel", FilterModel.class);
    aliases.put("filterParameter", FilterParameter.class);
    this.init(location, aliases);
  }

  
  
  
}

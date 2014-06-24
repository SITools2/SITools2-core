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
package fr.cnes.sitools.units.dimension;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.restlet.Context;

import fr.cnes.sitools.persistence.XmlMapStore;
import fr.cnes.sitools.units.dimension.model.SitoolsDimension;

/**
 * Storage of dimensions
 * 
 * @author jp.boignard (AKKA technologies)
 */
public class DimensionStoreXMLMap extends XmlMapStore<SitoolsDimension> implements DimensionStoreInterface {

  /** default location for file persistence */
  private static final String COLLECTION_NAME = "dimensions";

  /**
   * Constructor without file
   * 
   * @param context
   *          the Restlet Context
   */
  public DimensionStoreXMLMap(Context context) {
    super(SitoolsDimension.class, context);
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
  public DimensionStoreXMLMap(File location, Context context) {
    super(SitoolsDimension.class, location, context);
  }

  @Override
  public List<SitoolsDimension> retrieveByParent(String id) {
    List<SitoolsDimension> result = new ArrayList<SitoolsDimension>();
    for (Iterator<SitoolsDimension> it = getList().iterator(); it.hasNext();) {
      SitoolsDimension current = it.next();
      if (current.getParent().equals(id)) {
        getLog().info("Parent found");
        result.add(current);
      }
    }
    return result;
  }

  @Override
  public SitoolsDimension update(SitoolsDimension resource) {
    SitoolsDimension result = null;

    getLog().finest("Updating SitoolsDimension");

    Map<String, SitoolsDimension> map = getMap();
    SitoolsDimension current = map.get(resource.getId());

    result = current;
    current.setDimensionHelperName(resource.getDimensionHelperName());
    current.setName(resource.getName());
    current.setDescription(resource.getDescription());
    current.setUnitConverters(resource.getUnitConverters());
    current.setUnits(resource.getUnits());

    if (result != null) {
      map.put(resource.getId(), current);
    }
    return result;
  }

  @Override
  public void init(File location) {
    Map<String, Class<?>> aliases = new ConcurrentHashMap<String, Class<?>>();
    aliases.put("dimension", SitoolsDimension.class);
    this.init(location, aliases);
  }

  @Override
  public String getCollectionName() {
    return COLLECTION_NAME;
  }

}

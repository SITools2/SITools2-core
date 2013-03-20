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
package fr.cnes.sitools.collections;

import java.io.File;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.restlet.Context;

import fr.cnes.sitools.collections.model.Collection;
import fr.cnes.sitools.common.model.ResourceCollectionFilter;
import fr.cnes.sitools.common.model.ResourceComparator;
import fr.cnes.sitools.common.store.SitoolsStoreXML;

/**
 * Implementation of CollectionStore with XStream FilePersistenceStrategy
 * 
 * @author AKKA
 * 
 */
public final class CollectionsStoreXML extends SitoolsStoreXML<Collection> {

  /** default location for file persistence */
  private static final String COLLECTION_NAME = "collections";

  /**
   * Constructor with the XML file location
   * 
   * @param location
   *          directory of FilePersistenceStrategy
   * @param context TODO
   */
  public CollectionsStoreXML(File location, Context context) {
    super(Collection.class, location, context);
  }

  /**
   * Default constructor
   */
  public CollectionsStoreXML(Context context) {
    super(Collection.class, context);
    File defaultLocation = new File(COLLECTION_NAME);
    init(defaultLocation);
  }

  @Override
  public Collection update(Collection collection) {
    Collection result = null;
    for (Iterator<Collection> it = getRawList().iterator(); it.hasNext();) {
      Collection current = it.next();
      if (current.getId().equals(collection.getId())) {
        getLog().info("Updating collection");

        result = current;
        current.setId(collection.getId());
        current.setName(collection.getName());
        current.setDescription(collection.getDescription());
        current.setDataSets(collection.getDataSets());
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
   * Sort the list (by default on the name)
   * 
   * @param result
   *          list to be sorted
   * @param filter
   *          ResourceCollectionFilter with sort properties.
   */
  public void sort(List<Collection> result, ResourceCollectionFilter filter) {
    if ((filter != null) && (filter.getSort() != null) && !filter.getSort().equals("")) {
      Collections.sort(result, new ResourceComparator<Collection>(filter) {
        @Override
        public int compare(Collection arg0, Collection arg1) {

          String s1 = (String) arg0.getName();
          String s2 = (String) arg1.getName();

          return super.compare(s1, s2);
        }
      });
    }
  }

  /**
   * XStream FilePersistenceStrategy initialization
   * 
   * @param location
   *          Directory
   */
  public void init(File location) {
    Map<String, Class<?>> aliases = new ConcurrentHashMap<String, Class<?>>();
    aliases.put("Collection", Collection.class);
    this.init(location, aliases);
  }

  @Override
  public List<Collection> retrieveByParent(String id) {
    return null;
  }

  @Override
  public String getCollectionName() {
    return COLLECTION_NAME;
  }

}

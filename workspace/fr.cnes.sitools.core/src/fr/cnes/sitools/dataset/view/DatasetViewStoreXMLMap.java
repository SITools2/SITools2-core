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
package fr.cnes.sitools.dataset.view;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.restlet.Context;

import fr.cnes.sitools.common.model.ResourceCollectionFilter;
import fr.cnes.sitools.common.model.ResourceComparator;
import fr.cnes.sitools.dataset.view.model.DatasetView;
import fr.cnes.sitools.persistence.XmlMapStore;

/**
 * Implementation of DatasetViewStore with XStream FilePersistenceStrategy
 * 
 * @author AKKA
 * 
 */
public class DatasetViewStoreXMLMap extends XmlMapStore<DatasetView> implements DatasetViewStoreInterface {

  /** default location for file persistence */
  private static final String COLLECTION_NAME = "datasetViews";

  /**
   * Constructor with the XML file location
   * 
   * @param location
   *          directory of FilePersistenceStrategy
   * @param context
   *          the Restlet Context
   */
  public DatasetViewStoreXMLMap(File location, Context context) {
    super(DatasetView.class, location, context);
  }

  /**
   * Default constructor
   * 
   * @param context
   *          the Restlet Context
   */
  public DatasetViewStoreXMLMap(Context context) {
    super(DatasetView.class, context);
    File defaultLocation = new File(COLLECTION_NAME);
    init(defaultLocation);
  }

  @Override
  public List<DatasetView> retrieveByParent(String id) {
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
    aliases.put("datasetView", DatasetView.class);
    this.init(location, aliases);
  }

  @Override
  public DatasetView update(DatasetView datasetView) {
    DatasetView result = null;

    Map<String, DatasetView> map = getMap();
    DatasetView current = map.get(datasetView.getId());
    if (current == null) {
      getLog().warning("Cannot update " + COLLECTION_NAME + " that doesn't already exists");
      return null;
    }
    getLog().info("Updating datasetView");
    result = current;

    current.setId(datasetView.getId());
    current.setJsObject(datasetView.getJsObject());
    current.setImageUrl(datasetView.getImageUrl());
    current.setName(datasetView.getName());
    current.setFileUrl(datasetView.getFileUrl());
    current.setPriority(datasetView.getPriority());
    current.setDescription(datasetView.getDescription());
    current.setDependencies(datasetView.getDependencies());

    map.put(datasetView.getId(), current);

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
  public void sort(List<DatasetView> result, ResourceCollectionFilter filter) {
    if ((filter != null) && (filter.getSort() != null) && !filter.getSort().equals("")) {
      Collections.sort(result, new ResourceComparator<DatasetView>(filter) {
        @Override
        public int compare(DatasetView arg0, DatasetView arg1) {
          if (arg0.getName() == null) {
            return 1;
          }
          if (arg1.getName() == null) {
            return -1;
          }
          String s1 = (String) arg0.getName();
          String s2 = (String) arg1.getName();

          return super.compare(s1, s2);
        }
      });
    }
  }

}

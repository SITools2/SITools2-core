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
package fr.cnes.sitools.dataset;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.restlet.Context;

import fr.cnes.sitools.common.model.ResourceCollectionFilter;
import fr.cnes.sitools.common.model.ResourceComparator;
import fr.cnes.sitools.common.store.SitoolsSynchronizedStoreXML;
import fr.cnes.sitools.dataset.model.Column;
import fr.cnes.sitools.dataset.model.DataSet;

/**
 * Class for managing DataSet objects persistence with XStream FilePersistenceStrategy
 * 
 * @author AKKA
 * 
 */
 @Deprecated
public final class DataSetStoreXML extends SitoolsSynchronizedStoreXML<DataSet> {

  /** Default location */
  private static final String COLLECTION_NAME = "datasets";

  /**
   * Constructor with the XML file location
   * 
   * @param location
   *          directory of FilePersistenceStrategy
   * @param context
   *          the Context
   */
  public DataSetStoreXML(File location, Context context) {
    super(DataSet.class, location, context);
  }

  /**
   * Default constructor
   * 
   * @param context
   *          the Context
   */
  public DataSetStoreXML(Context context) {
    super(DataSet.class, context);
    File defaultLocation = new File(COLLECTION_NAME);
    init(defaultLocation);
  }

  @Override
  public DataSet update(DataSet dataset) {
    DataSet result = null;
    List<DataSet> rawList = getRawList();
    synchronized (rawList) {

      for (Iterator<DataSet> it = rawList.iterator(); it.hasNext();) {
        DataSet current = it.next();
        if (current.getId().equals(dataset.getId())) {
          getLog().info("Updating DataSet");

          result = current;
          current.setName(dataset.getName());
          current.setDescription(dataset.getDescription());
          current.setImage(dataset.getImage());
          current.setQueryType(dataset.getQueryType());
          current.setSqlQuery(dataset.getSqlQuery());
          current.setDatasource(dataset.getDatasource());
          current.setColumnModel(dataset.getColumnModel());
          current.setStructures(dataset.getStructures());
          current.setSitoolsAttachementForUsers(dataset.getSitoolsAttachementForUsers());
          current.setStatus(dataset.getStatus());
          current.setPredicat(dataset.getPredicat());
          current.setExpirationDate(dataset.getExpirationDate());
          current.setDescriptionHTML(dataset.getDescriptionHTML());
          current.setNbRecords(dataset.getNbRecords());
          current.setVisible(dataset.isVisible());
          current.setDirty(dataset.getDirty());
          current.setDatasetView(dataset.getDatasetView());
          current.setDictionaryMappings(dataset.getDictionaryMappings());
          current.setStructure(dataset.getStructure());
          current.setProperties(dataset.getProperties());
          current.setDatasetViewConfig(dataset.getDatasetViewConfig());
          it.remove();

          break;
        }
      }
      if (result != null) {

        // attribution d'un id par défaut à chaque colonne.
        if (dataset.getColumnModel() != null) {
          for (Column column : dataset.getColumnModel()) {
            if ((column.getId() == null) || column.getId().equals("")) {
              column.setId(UUID.randomUUID().toString());
            }
          }
        }

        rawList.add(result);
      }
    }
    return result;
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.cnes.sitools.dataSet.DataSetStore#getList(fr.cnes.sitools.common.model.ResourceCollectionFilter)
   */
  @Override
  public List<DataSet> getList(ResourceCollectionFilter filter) {
    List<DataSet> result = new ArrayList<DataSet>();
    List<DataSet> rawList = getRawList();
    synchronized (rawList) {
      if ((getRawList() == null) || (rawList.size() <= 0) || (filter.getStart() > getRawList().size())) {
        return result;
      }

      // Filtre
      if ((filter.getQuery() != null) && !filter.getQuery().equals("")) {
        for (DataSet dataSet : rawList) {
          if (null == dataSet.getName()) {
            continue;
          }
          if (dataSet.getName().toLowerCase().startsWith(filter.getQuery().toLowerCase())) {
            result.add(dataSet);
          }
        }
      }
      else {
        result.addAll(rawList);
      }

      // Si index premier element > nombre d'elements filtres => resultat vide
      if (filter.getStart() > result.size()) {
        result.clear();
        return result;
      }

      // Tri
      sort(result, filter);
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
  public void sort(List<DataSet> result, ResourceCollectionFilter filter) {
    if ((filter != null) && (filter.getSort() != null) && !filter.getSort().equals("")) {
      Collections.sort(result, new ResourceComparator<DataSet>(filter));
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
    aliases.put("dataset", DataSet.class);
    this.init(location, aliases);
  }

  @Override
  public List<DataSet> retrieveByParent(String id) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String getCollectionName() {
    return COLLECTION_NAME;
  }

}

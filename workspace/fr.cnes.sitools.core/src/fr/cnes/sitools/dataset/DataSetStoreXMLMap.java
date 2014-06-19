package fr.cnes.sitools.dataset;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.restlet.Context;

import fr.cnes.sitools.common.model.ResourceCollectionFilter;
import fr.cnes.sitools.common.model.ResourceComparator;
import fr.cnes.sitools.dataset.model.Column;
import fr.cnes.sitools.dataset.model.DataSet;
import fr.cnes.sitools.persistence.XmlSynchronizedMapStore;

/**
 * DataSetStoreXMLMap
 * 
 * @author tx.chevallier
 */
public class DataSetStoreXMLMap extends XmlSynchronizedMapStore<DataSet> implements DataSetStoreInterface {

  /** Default location */
  private static final String COLLECTION_NAME = "datasets";

  /**
   * DataSetStoreXMLMap
   * 
   * @param cl
   *          Class<DataSet>
   * @param context
   *          Context
   */
  public DataSetStoreXMLMap(Class<DataSet> cl, Context context) {
    super(cl, context);
  }

  /**
   * DataSetStoreXMLMap
   * 
   * @param cl
   *          Class<DataSet>
   * @param location
   *          File
   * @param context
   *          Context
   */
  public DataSetStoreXMLMap(Class<DataSet> cl, File location, Context context) {
    super(cl, location, context);
  }

  /**
   * Constructor with file location
   * 
   * @param location
   *          the file location
   * @param context
   *          the Restlet Context
   */
  public DataSetStoreXMLMap(File location, Context context) {
    super(DataSet.class, location, context);
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

  @Override
  public void init(File location) {
    Map<String, Class<?>> aliases = new ConcurrentHashMap<String, Class<?>>();
    aliases.put("dataset", DataSet.class);
    this.init(location, aliases);
  }

  @Override
  public DataSet update(DataSet dataset) {

    DataSet result = null;
    getLog().finest("Updating DataSet");
    Map<String, DataSet> map = getMap();

    synchronized (map) {

      DataSet current = map.get(dataset.getId());

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
      current.setLastStatusUpdate(dataset.getLastStatusUpdate());

      if (result != null) {

        // attribution d'un id par défaut à chaque colonne.
        if (dataset.getColumnModel() != null) {
          for (Column column : dataset.getColumnModel()) {
            if ((column.getId() == null) || column.getId().equals("")) {
              column.setId(UUID.randomUUID().toString());
            }
          }
        }
        map.put(dataset.getId(), current);
      }
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

}

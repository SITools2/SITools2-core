package fr.cnes.sitools.dataset;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.restlet.Context;

import fr.cnes.sitools.dataset.model.DataSet;
import fr.cnes.sitools.persistence.XmlSynchronizedMapStore;

/**
 * DataSetStoreXMLMap
 * 
 * @author tx.chevallier
 */
public class DataSetStoreXMLMap extends XmlSynchronizedMapStore<DataSet> implements DataSetStoreInterface  {

  
  /** Default location */
  private static final String COLLECTION_NAME = "datasets";
  
  /**
   * DataSetStoreXMLMap
   * @param cl Class<DataSet>
   * @param context Context
   */
  public DataSetStoreXMLMap(Class<DataSet> cl, Context context) {
    super(cl, context);
  }

  /**
   * DataSetStoreXMLMap
   * @param cl Class<DataSet>
   * @param location File
   * @param context Context
   */
  public DataSetStoreXMLMap(Class<DataSet> cl, File location, Context context) {
    super(cl, location, context);
  }
  
  
  /**
   * Constructor with file location
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



}

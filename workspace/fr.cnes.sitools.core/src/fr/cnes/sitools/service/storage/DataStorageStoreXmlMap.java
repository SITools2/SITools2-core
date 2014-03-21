package fr.cnes.sitools.service.storage;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.restlet.Context;

import fr.cnes.sitools.common.exception.SitoolsException;
import fr.cnes.sitools.persistence.XmlMapStore;
import fr.cnes.sitools.service.storage.model.StorageDirectory;

public class DataStorageStoreXmlMap extends XmlMapStore<StorageDirectory> implements DataStorageStoreInterface {

  /** default location for file persistence */
  private static final String COLLECTION_NAME = "datastorage";

  /**
   * Constructor without file
   * 
   * @param context
   *          the Restlet Context
   */
  public DataStorageStoreXmlMap(Context context) {
    super(StorageDirectory.class, context);
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
  public DataStorageStoreXmlMap(File location, Context context) {
    super(StorageDirectory.class, location, context);
  }
  
  
  @Override
  public List<StorageDirectory> retrieveByParent(String id) {
    throw new RuntimeException(SitoolsException.NOT_IMPLEMENTED);
  }

  @Override
  public String getCollectionName() {
    return COLLECTION_NAME;
  }

  @Override
  public void init(File location) {
    Map<String, Class<?>> aliases = new ConcurrentHashMap<String, Class<?>>();
    aliases.put("directory", StorageDirectory.class);
    this.init(location, aliases);
  }

}

package fr.cnes.sitools.registry;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import org.restlet.Context;
import org.restlet.engine.Engine;

import fr.cnes.sitools.persistence.XmlMapStore;
import fr.cnes.sitools.registry.model.AppRegistry;

public class ApplicationStoreXMLMap extends XmlMapStore<AppRegistry> implements ApplicationStoreInterface {

  /** Default location */
  private static final String COLLECTION_NAME = "applications";
  
  /** static logger for this store implementation */
  private static Logger log = Engine.getLogger(ApplicationStoreXMLMap.class.getName());
  
  /**
   * DataSetStoreXMLMap
   * @param cl Class<DataSet>
   * @param context Context
   */
  public ApplicationStoreXMLMap(Class<AppRegistry> cl, Context context) {
    super(cl, context);
  }

  /**
   * DataSetStoreXMLMap
   * @param cl Class<DataSet>
   * @param location File
   * @param context Context
   */
  public ApplicationStoreXMLMap(Class<AppRegistry> cl, File location, Context context) {
    super(cl, location, context);
  }
  
  
  /**
   * Constructor with file location
   * @param location
   *          the file location
   * @param context
   *          the Restlet Context
   */
  public ApplicationStoreXMLMap(File location, Context context) {
    super(AppRegistry.class, location, context);
  }

  public List<AppRegistry> retrieveByParent(String id) {
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
    aliases.put("manager", AppRegistry.class);
    this.init(location, aliases);
  }
  
  @Override
  public AppRegistry update(AppRegistry manager) {
    AppRegistry result = null;

    Map<String, AppRegistry> map = getMap();
    AppRegistry current = map.get(manager.getId());
    if (current == null) {
      getLog().warning("Cannot update " + COLLECTION_NAME + " that doesn't already exists");
      return null;
    }
    log.finest("Updating Application");
    result = current;
    
    current.setName(manager.getName());
    current.setDescription(manager.getDescription());
    current.setLastUpdate(manager.getLastUpdate());
    current.setResources(manager.getResources());

    if (result != null) {
      map.put(manager.getId(), current);
    }
    return result;
  }
  
  
  

}

package fr.cnes.sitools.registry;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.restlet.Context;

import fr.cnes.sitools.dataset.model.DataSet;
import fr.cnes.sitools.feeds.model.FeedAuthorModel;
import fr.cnes.sitools.feeds.model.FeedEntryModel;
import fr.cnes.sitools.persistence.XmlMapStore;
import fr.cnes.sitools.registry.model.AppRegistry;

public class ApplicationStoreXMLMap extends XmlMapStore<AppRegistry> implements ApplicationStoreInterface {

  /** Default location */
  private static final String COLLECTION_NAME = "datasets";
  
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

  @Override
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
    aliases.put("FeedEntryModel", FeedEntryModel.class);
    aliases.put("author", FeedAuthorModel.class);
    this.init(location, aliases);
  }

}

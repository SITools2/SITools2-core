package fr.cnes.sitools.collections;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.restlet.Context;

import fr.cnes.sitools.collections.model.Collection;
import fr.cnes.sitools.persistence.XmlMapStore;
import fr.cnes.sitools.security.authorization.client.ResourceAuthorization;



public class CollectionsStoreXMLMap extends XmlMapStore<Collection> implements CollectionStoreInterface {

  
  /** default location for file persistence */
  private static final String COLLECTION_NAME = "collections";
  
  
  public CollectionsStoreXMLMap(Class<Collection> cl, File location, Context context) {
    super(cl, location, context);
  }
  
  public CollectionsStoreXMLMap(File location, Context context) {
    super(Collection.class, location, context);
  }


  @Override
  public List<Collection> retrieveByParent(String id) {
    return null;
  }

  @Override
  public String getCollectionName() {
    return COLLECTION_NAME;
  }

  @Override
  public void init(File location) {
    Map<String, Class<?>> aliases = new ConcurrentHashMap<String, Class<?>>();
    aliases.put("resourceAuthorization", Collection.class);
    this.init(location, aliases);
  }
  
  

}

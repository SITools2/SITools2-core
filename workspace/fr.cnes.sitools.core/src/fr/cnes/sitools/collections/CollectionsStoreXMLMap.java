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
import fr.cnes.sitools.order.model.Order;
import fr.cnes.sitools.persistence.XmlMapStore;

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
    aliases.put("Collection", Collection.class);
    this.init(location, aliases);
  }

  @Override
  public Collection update(Collection collection) {
    Collection result = null;

    Map<String, Collection> map = getMap();
    Collection current = map.get(collection.getId());
    if (current == null) {
      getLog().warning("Cannot update " + COLLECTION_NAME + " that doesn't already exists");
      return null;
    }
    getLog().info("Updating collection");
    result = current;
    current.setId(collection.getId());
    current.setName(collection.getName());
    current.setDescription(collection.getDescription());
    current.setDataSets(collection.getDataSets());

    map.put(collection.getId(), current);

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

}

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
package fr.cnes.sitools.persistence;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.restlet.Context;
import org.restlet.data.MediaType;
import org.restlet.engine.Engine;

import com.google.common.collect.Lists;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.persistence.FilePersistenceStrategy;
import com.thoughtworks.xstream.persistence.XmlMap;

import fr.cnes.sitools.common.SitoolsSettings;
import fr.cnes.sitools.common.XStreamFactory;
import fr.cnes.sitools.common.application.ContextAttributes;
import fr.cnes.sitools.common.model.IResource;
import fr.cnes.sitools.common.model.ResourceCollectionFilter;

/**
 * Class to extend if resource contains method providing paginable results
 * 
 * @param <E>
 *          The elements returned for pagination
 * @author AKKA
 */
public abstract class XmlMapStoreXStream<E extends IResource> {

  /** The restlet {@link Context} */
  protected Context context;

  /** Store structure is a Map<String, IPersistent> */
  private Map<String, E> map;
  
  /** The xstream */
  private XStream xstream;

  /** Logger */
  private Logger log;

  /** Paginable */
  private E paginableClass;

  /**
   * Constructor with file location
   * 
   * @param context
   *          TODO
   */
  public XmlMapStoreXStream(Context context) {
    super();
    this.context = context;
    log = Engine.getLogger(this.getClass().getName());
  }

  /**
   * Constructor with file location
   * 
   * @param location
   *          the file location
   * @param context
   *          TODO
   */
  public XmlMapStoreXStream(File location, Context context) {
    super();
    log = Engine.getLogger(this.getClass().getName());
    this.context = context;
    init(location);
  }

  /**
   * Initialization with location method to override in order to define class aliases <br/>
   * <pre>
   * {@code
   *    Map<String, Class<?>> aliases = new ConcurrentHashMap<String, Class<?>>();
   *    aliases.put("dimension", SitoolsDimension.class);
   *    this.init(location, aliases);
   * }
   * </pre>
   * @param location
   *          the file location
   */
  public abstract void init(File location);

  /**
   * Get the list according to filter and full list
   * 
   * @param filter
   *          query filters
   * @param result
   *          global results
   * @return the list of elements to be returned in the page
   */
  public final List<E> getPage(ResourceCollectionFilter filter, List<E> result) {
    if (result.size() == 0) {
      return result;
    }

    // Pagination
    int start = (filter.getStart() <= 0) ? 0 : filter.getStart();
    int limit = ((filter.getLimit() <= 0) || ((filter.getLimit() + start) > result.size())) ? (result.size() - start)
        : filter.getLimit();
    // subList
    // Returns a view of the portion of this list between the specified
    // fromIndex, inclusive,
    // and toIndex, exclusive.
    List<E> page = null;
    synchronized (result) {
      page = result.subList(start, start + limit); // pas -1 puisque
    }
    // exclusive

    return new ArrayList<E>(page);
  }

  /**
   * XStream FilePersistenceStrategy initialization
   * 
   * @param location
   *          Directory
   * @param aliases
   *          map of aliases given to objects
   */
  @SuppressWarnings("unchecked")
  public final void init(File location, Map<String, Class<?>> aliases) {
    log.info("Store location " + location.getAbsolutePath());
    SitoolsSettings settings = (SitoolsSettings) context.getAttributes().get(ContextAttributes.SETTINGS);
    boolean strict = !settings.isStartWithMigration();
    xstream = XStreamFactory.getInstance().getXStream(MediaType.APPLICATION_XML, context, strict);

    xstream.autodetectAnnotations(true);
    for (String alias : aliases.keySet()) {
      xstream.alias(alias, aliases.get(alias));
    }

    // creates the xml data storage engine - as a XmlMap, backed by a
    // FilePersistenceStrategy
    XmlMap dataStoreXStream = new XmlMap(new FilePersistenceStrategy(location, xstream));
    map = Collections.synchronizedMap(dataStoreXStream);    
  }
  
  /**
   * Clear the current store and add all coll items in the store
   * @param coll List<E>
   */
  public final void init(List<E> coll){
    List<E> list = Collections.synchronizedList(coll);
    map.clear();
    for (Iterator<E> iterator = list.iterator(); iterator.hasNext();) {
      E e = (E) iterator.next();
      map.put(e.getId(), e);
    }
  }  

  /**
   * Return the list
   * 
   * @return the list
   */
  public List<E> getList() {    
    ArrayList<E> result = Lists.newArrayList(map.values());
    sort(result, null);
    return result;
  }

  /**
   * Sort the list according to criteria specified in subclasses
   * 
   * @param list
   *          the list
   * @param filter
   *          the filter
   */
  public abstract void sort(List<E> list, ResourceCollectionFilter filter);

  /**
   * Sets the value of list
   * 
   * @param list
   *          the list to set
   */
  public final void setList(List<E> list) {
    init(list); // this.list = list;
  }

  /**
   * @return Map<String, E>  -- private ? Modifying this Map automatically persists data
   */
  public Map<String, E> getMap() {
    return map;
  }

  /**
   * Gets the instance value
   * 
   * @return the instance
   */
  public final E getPaginableClass() {
    return paginableClass;
  }

  /**
   * Gets the log value
   * 
   * @return the log
   */
  public final Logger getLog() {
    return log;
  }

  public Context getContext() {
    return context;
  }

  public XStream getXstream() {
    return xstream;
  }
  
  
  
}

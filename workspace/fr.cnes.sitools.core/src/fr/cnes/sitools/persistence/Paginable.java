    /*******************************************************************************
 * Copyright 2010-2013 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.restlet.Context;
import org.restlet.data.MediaType;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.persistence.FilePersistenceStrategy;
import com.thoughtworks.xstream.persistence.XmlArrayList;

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
public abstract class Paginable<E extends IResource> {

  /** The restlet {@link Context} */
  protected Context context;

  /** List associated */
  private List<E> list;

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
  public Paginable(Context context) {
    super();
    this.context = context;
  }

  /**
   * Constructor with file location
   * 
   * @param location
   *          the file location
   * @param context
   *          TODO
   */
  public Paginable(File location, Context context) {
    super();
    log = Logger.getLogger(this.getClass().getName());
    this.context = context;
    init(location);
  }

  /**
   * Initialization with location method to override
   * 
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
    XStream xstream = XStreamFactory.getInstance().getXStream(MediaType.APPLICATION_XML, context, strict);

    xstream.autodetectAnnotations(true);
    for (String alias : aliases.keySet()) {
      xstream.alias(alias, aliases.get(alias));
    }
    FilePersistenceStrategy strategy = new FilePersistenceStrategy(location, xstream);
    List<E> xstreamList = new XmlArrayList(strategy);
    list = Collections.synchronizedList(xstreamList);

  }

  /**
   * Return the list
   * 
   * @return the list
   */
  public final List<E> getList() {
    // List<E> result = new ArrayList<E>();
    // if ((list != null) || (list.size() > 0)) {
    // result.addAll(list);
    // }
    List<E> result = Collections.unmodifiableList(list);

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
    this.list = list;
  }

  /**
   * Gets the list value
   * 
   * @return the list
   */
  public final List<E> getRawList() {
    return list;
  }

  /**
   * get the list with XQuery
   * 
   * @param xquery
   *          the XQuery
   * @return the list
   */
  public final List<E> getListByXQuery(String xquery) {
    log.warning("getListByXQuery DEFAULT IMPLEMENTATION : getList");
    return getList();
  }

  /**
   * Close the Store ...
   */
  public final void close() {
    // TODO
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

}

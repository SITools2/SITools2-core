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
package fr.cnes.sitools.persistence;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

import org.restlet.Context;
import org.restlet.data.MediaType;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.persistence.FilePersistenceStrategy;
import com.thoughtworks.xstream.persistence.XmlMap;

import fr.cnes.sitools.common.SitoolsSettings;
import fr.cnes.sitools.common.XStreamFactory;
import fr.cnes.sitools.common.application.ContextAttributes;
import fr.cnes.sitools.common.model.ResourceCollectionFilter;

/**
 * Generic implementation of PersistenceDao.
 * 
 * @author jp.boignard (AKKA Technologies)
 * 
 * @param <E>
 *          Element must extend Persistent
 */
public abstract class XmlPersistenceDaoImpl<E extends Persistent> implements PersistenceDao<E> {

  /** Store structure is a Map<String, Persistent> */
  private final Map<String, E> dataStore;

  /** The xstream */
  private XStream xstream;

  /**
   * Constructor for file based persistence
   * 
   * @param storageRoot
   *          - the base dir where all the xml files will be saved
   * @param context
   *          TODO
   */
  @SuppressWarnings("unchecked")
  public XmlPersistenceDaoImpl(final File storageRoot, Context context) {

    SitoolsSettings settings = (SitoolsSettings) context.getAttributes().get(ContextAttributes.SETTINGS);
    boolean strict = !settings.isStartWithMigration();

    // xstream core
    xstream = XStreamFactory.getInstance().getXStream(MediaType.APPLICATION_XML, context, strict);

    // creates the xml data storage engine - as a XmlMap, backed by a
    // FilePersistenceStrategy
    XmlMap dataStoreXStream = new XmlMap(new FilePersistenceStrategy(storageRoot, xstream));
    dataStore = Collections.synchronizedMap(dataStoreXStream);
  }

  /**
   * Gets the xstream value
   * 
   * @return the xstream
   */
  public final XStream getXstream() {
    return xstream;
  }

  /**
   * Sets the value of xstream
   * 
   * @param xstream
   *          the xstream to set
   */
  public final void setXstream(XStream xstream) {
    this.xstream = xstream;
  }

  @Override
  public final E get(String id) {
    return dataStore.get(id);
  }

  @Override
  public final void save(E o) {
    dataStore.put(o.getId(), o);
  }

  @Override
  public final void update(E o) {
    dataStore.put(o.getId(), o);
  }

  @Override
  public final void saveAll(Collection<E> os) {
    Iterator<E> iterator = os.iterator();
    while (iterator.hasNext()) {
      E e = iterator.next();
      save(e);
    }
  }

  @Override
  public final Collection<E> getList() {
    return dataStore.values();
  }

  @Override
  public Collection<E> getList(ResourceCollectionFilter filter) {
    return dataStore.values();
  }

  @Override
  public final Collection<E> getPage(ResourceCollectionFilter filter, Collection<E> result) {
    if (result == null) {
      result = dataStore.values();
    }
    if ((result == null) || (result.size() == 0)) {
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
    return new ArrayList<E>(result).subList(start, start + limit);
  }

  @Override
  public final void delete(E o) {
    dataStore.remove(o.getId());
  }

}

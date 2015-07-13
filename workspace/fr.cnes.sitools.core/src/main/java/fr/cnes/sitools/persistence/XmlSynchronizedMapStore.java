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
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.restlet.Context;

import fr.cnes.sitools.common.model.IResource;
import fr.cnes.sitools.common.model.ResourceCollectionFilter;
import fr.cnes.sitools.common.model.ResourceComparator;
import fr.cnes.sitools.common.store.SitoolsStore;

/**
 * Base class for XML stores
 * 
 * @param <T>
 *          object class to be stored
 * @author jp.boignard (AKKA technologies)
 */
public abstract class XmlSynchronizedMapStore<T extends IResource> extends XmlMapStoreXStream<T> implements
    SitoolsStore<T> {

  /**
   * Object instance
   */
  private Class<T> persistentClass;

  /**
   * Constructor with object class
   * 
   * @param cl
   *          the class to keep
   * @param context
   *          org.restlet.Context
   */
  public XmlSynchronizedMapStore(Class<T> cl, Context context) {
    super(context);
    this.persistentClass = cl;
  }

  /**
   * Constructor with file location
   * 
   * @param cl
   *          the class to keep
   * @param location
   *          the file location
   * @param context
   *          org.restlet.Context
   */
  public XmlSynchronizedMapStore(Class<T> cl, File location, Context context) {
    super(location, context);
    this.persistentClass = cl;
  }

  @SuppressWarnings("unchecked")

  public T[] getArray() {
    T[] result = null;
    Map<String, T> map = getMap();
    synchronized (map) {
      if ((map != null) && (map.size() > 0)) {
        T[] array = (T[]) Array.newInstance(persistentClass, map.size());
        result = (T[]) map.values().toArray(array);
      }
      else {
        result = (T[]) Array.newInstance(persistentClass, 0);
      }
    }
    return result;
  }

  @SuppressWarnings("unchecked")

  public T[] getArray(ResourceCollectionFilter filter) {
    List<T> resultList = getList(filter);

    T[] result = null;
    if ((resultList != null) && (resultList.size() > 0)) {
      result = resultList.toArray((T[]) Array.newInstance(persistentClass, getMap().size()));
    }
    else {
      result = (T[]) Array.newInstance(persistentClass, 0);
    }
    return result;
  }


  public List<T> getList(ResourceCollectionFilter filter) {
    List<T> result = new ArrayList<T>();
    Map<String, T> map = getMap();
    synchronized (map) {

      if ((map == null) || (map.size() <= 0) || (filter.getStart() > map.size())) {
        return result;
      }

      // Filtre
      if ((filter.getQuery() != null) && !filter.getQuery().equals("")) {
        for (T resource : map.values()) {
          if (null == resource.getName()) {
            continue;
          }
          if ("strict".equals(filter.getMode())) {
            if (resource.getName().equals(filter.getQuery())) {
              result.add(resource);
            }
          }
          else {
            if (resource.getName().toLowerCase().startsWith(filter.getQuery().toLowerCase())) {
              result.add(resource);
            }
          }
        }
      }
      else {
        result.addAll(getMap().values());
      }
    }

    // Si index premier element > nombre d'elements filtres => resultat vide
    if (filter.getStart() > result.size()) {
      result.clear();
      return result;
    }

    // Tri
    sort(result, filter);

    return result;
  }


  public List<T> saveList(List<T> os) {
    Iterator<T> iterator = os.iterator();
    while (iterator.hasNext()) {
      T e = iterator.next();
      save(e);
    }
    return os;
  }

  /**
   * Create a new entry in the store if not ever exists
   * 
   * @param resource
   *          T to be created
   * @return created resource
   */

  public T create(T resource) {
    T result = null;
    if (resource.getId() == null || "".equals(resource.getId())) {
      resource.setId(UUID.randomUUID().toString());
    }
    Map<String, T> map = getMap();
    synchronized (map) {
      result = map.get(resource.getId());
      if (result == null) {
        getMap().put(resource.getId(), resource);
        result = resource;
      }
      else {
        getLog().finest(getCollectionName() + " found");
      }
    }
    return result;
  }


  public T retrieve(String id) {
    T result = null;
    Map<String, T> map = getMap();
    synchronized (map) {
      result = map.get(id);
      if (result == null) {
        getLog().finest(getCollectionName() + " not found.");
      }
      else {
        getLog().finest(getCollectionName() + " found");
      }
    }
    return result;
  }


  public T update(T o) {
    Map<String, T> map = getMap();
    synchronized (map) {
      map.put(o.getId(), o);
    }
    return o;
  }


  public boolean delete(String id) {
    boolean result = false;
    Map<String, T> map = getMap();
    synchronized (map) {
      if (map.containsKey(id)) {
        getLog().finest("Removing " + getCollectionName());
        map.remove(id);
        result = true;
      }
    }
    return result;
  }


  public void sort(List<T> list, ResourceCollectionFilter filter) {
    if ((filter != null) && (filter.getSort() != null) && !filter.getSort().equals("")) {
      Collections.sort(list, new ResourceComparator<T>(filter));
    }
  }

  // ================================

  /**
   * PRIVATE used only by saveAll
   * 
   * @param o
   *          T
   * @return modified resource
   */
  private T save(T o) {
    getMap().put(o.getId(), o);
    return o;
  }

  /**
   * Method to implement for collection name
   * 
   * @return the collection name
   */
  public abstract String getCollectionName();

}

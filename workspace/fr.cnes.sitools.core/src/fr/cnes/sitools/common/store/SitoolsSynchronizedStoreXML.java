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
package fr.cnes.sitools.common.store;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.restlet.Context;

import fr.cnes.sitools.common.model.IResource;
import fr.cnes.sitools.common.model.ResourceCollectionFilter;
import fr.cnes.sitools.common.model.ResourceComparator;
import fr.cnes.sitools.persistence.Paginable;

/**
 * Base class for XML stores
 * 
 * @param <T>
 *          object class to be stored
 * @author m.marseille (AKKA technologies)
 */
public abstract class SitoolsSynchronizedStoreXML<T extends IResource> extends Paginable<T> implements SitoolsStore<T> {

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
   *          TODO
   */
  public SitoolsSynchronizedStoreXML(Class<T> cl, Context context) {
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
   *          TODO
   */
  public SitoolsSynchronizedStoreXML(Class<T> cl, File location, Context context) {
    super(location, context);
    this.persistentClass = cl;
  }

  @SuppressWarnings("unchecked")
  @Override
  public final T[] getArray() {
    T[] result = null;
    List<T> rawList = getRawList();
    synchronized (rawList) {
      if ((rawList != null) && (rawList.size() > 0)) {
        T[] array = (T[]) Array.newInstance(persistentClass, rawList.size());
        result = rawList.toArray(array);
      }
      else {
        result = (T[]) Array.newInstance(persistentClass, 0);
      }
    }

    return result;
  }

  @Override
  public final T[] getArrayByXQuery(String xquery) {
    getLog().severe("getArrayByXQuery NOT IMPLEMENTED");
    return null;
  }

  @SuppressWarnings("unchecked")
  @Override
  public final T[] getArray(ResourceCollectionFilter filter) {
    List<T> resultList = getList(filter);

    T[] result = null;
    if ((resultList != null) && (resultList.size() > 0)) {
      result = resultList.toArray((T[]) Array.newInstance(persistentClass, getRawList().size()));
    }
    else {
      result = (T[]) Array.newInstance(persistentClass, 0);
    }
    return result;
  }

  @Override
  public List<T> getList(ResourceCollectionFilter filter) {
    List<T> result = new ArrayList<T>();
    List<T> rawList = getRawList();
    synchronized (rawList) {
      if ((getRawList() == null) || (getRawList().size() <= 0) || (filter.getStart() > getRawList().size())) {
        return result;
      }
      // Filtre
      if ((filter.getQuery() != null) && !filter.getQuery().equals("")) {
        for (T resource : rawList) {
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
        result.addAll(rawList);
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

  @Override
  public T create(T resource) {
    T result = null;
    if (resource.getId() == null || "".equals(resource.getId())) {
      resource.setId(UUID.randomUUID().toString());
    }
    List<T> rawList = getRawList();
    synchronized (rawList) {
      // Recherche sur l'id
      for (Iterator<T> it = rawList.iterator(); it.hasNext();) {
        T current = it.next();
        if (current.getId().equals(resource.getId())) {
          getLog().info(getCollectionName() + " found");
          result = current;
          break;
        }
      }

      if (result == null) {
        rawList.add(resource);
        result = resource;
      }
    }
    return result;
  }

  @Override
  public final T retrieve(String id) {
    T result = null;
    List<T> rawList = getRawList();
    synchronized (rawList) {
      for (Iterator<T> it = rawList.iterator(); it.hasNext();) {
        T current = it.next();
        if (current.getId().equals(id)) {
          getLog().info(getCollectionName() + " found");
          result = current;
          break;
        }
      }
      if (result == null) {
        getLog().info(getCollectionName() + " not found.");
      }
    }
    return result;
  }

  @Override
  public final boolean delete(String id) {
    boolean result = false;
    List<T> rawList = getRawList();
    synchronized (rawList) {
      for (Iterator<T> it = rawList.iterator(); it.hasNext();) {
        T current = it.next();
        if (current.getId().equals(id)) {
          getLog().info("Removing " + getCollectionName());
          it.remove();
          result = true;
          break;
        }
      }
    }
    return result;
  }

  @Override
  public void sort(List<T> list, ResourceCollectionFilter filter) {
    if ((filter != null) && (filter.getSort() != null) && !filter.getSort().equals("")) {
      Collections.sort(list, new ResourceComparator<T>(filter));
    }
  }

  /**
   * Method to implement for collection name
   * 
   * @return the collection name
   */
  public abstract String getCollectionName();

}

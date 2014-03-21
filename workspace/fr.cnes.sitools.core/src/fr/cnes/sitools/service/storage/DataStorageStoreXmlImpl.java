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
package fr.cnes.sitools.service.storage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.restlet.Context;

import fr.cnes.sitools.common.model.ResourceCollectionFilter;
import fr.cnes.sitools.persistence.XmlPersistenceDaoImpl;
import fr.cnes.sitools.service.storage.model.StorageDirectory;

/**
 * Specialized XML Persistence implementation of DataStorageStore.
 * 
 * @author jp.boignard (AKKA Technologies)
 */
public final class DataStorageStoreXmlImpl extends XmlPersistenceDaoImpl<StorageDirectory> implements DataStorageStore {

  /**
   * Constructor
   * 
   * @param storageRoot
   *          Path for file persistence strategy
   *  @param context the context
   */
  public DataStorageStoreXmlImpl(File storageRoot, Context context) {
    super(storageRoot, context);
  }

  @Override
  public List<StorageDirectory> getList(ResourceCollectionFilter filter) {

    List<StorageDirectory> result = new ArrayList<StorageDirectory>();
    if ((getList() == null) || (getList().size() <= 0) || (filter.getStart() > getList().size())) {
      return result;
    }

    // Filtre
    if ((filter.getQuery() != null) && !filter.getQuery().equals("")) {
      for (StorageDirectory resource : getList()) {
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
      result.addAll(getList());
    }

    // Si index premier element > nombre d'elements filtres => resultat vide
    if (filter.getStart() > result.size()) {
      result.clear();
      return result;
    }

    return result;
  }

}

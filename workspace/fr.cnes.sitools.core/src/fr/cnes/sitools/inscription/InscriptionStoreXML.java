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
package fr.cnes.sitools.inscription;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.restlet.Context;

import fr.cnes.sitools.common.model.ResourceCollectionFilter;
import fr.cnes.sitools.common.model.ResourceComparator;
import fr.cnes.sitools.common.store.SitoolsStoreXML;
import fr.cnes.sitools.inscription.model.Inscription;

/**
 * Class for managing Inscription objects persistence
 * 
 * @author jp.boignard (AKKA Technologies)
 * 
 */
@Deprecated
public final class InscriptionStoreXML extends SitoolsStoreXML<Inscription> {

  /** default location for file persistence */
  private static final String COLLECTION_NAME = "inscriptions";

  /**
   * Constructor with the XML file location
   * 
   * @param location
   *          directory for file persistence
   * @param context
   *          the Restlet Context
   */
  public InscriptionStoreXML(File location, Context context) {
    super(Inscription.class, location, context);
  }

  /**
   * Default constructor
   * 
   * @param context
   *          the Restlet Context
   */
  public InscriptionStoreXML(Context context) {
    super(Inscription.class, context);
    File defaultLocation = new File(COLLECTION_NAME);
    init(defaultLocation);
  }

  /**
   * XStream FilePersistenceStrategy initialization
   * 
   * @param location
   *          Directory
   */
  public void init(File location) {
    Map<String, Class<?>> aliases = new ConcurrentHashMap<String, Class<?>>();
    aliases.put("inscription", Inscription.class);
    this.init(location, aliases);
  }

  @Override
  public Inscription update(Inscription inscription) {
    Inscription result = null;
    for (Iterator<Inscription> it = getRawList().iterator(); it.hasNext();) {
      Inscription current = it.next();
      if (current.getId().equals(inscription.getId())) {
        getLog().info("Updating inscription");

        result = current;
        current.setEmail(inscription.getEmail());
        current.setFirstName(inscription.getFirstName());
        current.setLastName(inscription.getLastName());
        current.setIdentifier(inscription.getIdentifier());
        current.setComment(inscription.getComment());
        current.setProperties(inscription.getProperties());

        current.setPassword(inscription.getPassword());

        current.setStatus(inscription.getStatus());
        // TODO all fields

        it.remove();

        break;
      }
    }
    if (result != null) {
      getRawList().add(result);
    }
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
  public void sort(List<Inscription> result, ResourceCollectionFilter filter) {
    if ((filter != null) && (filter.getSort() != null) && !filter.getSort().equals("")) {
      Collections.sort(result, new ResourceComparator<Inscription>(filter) {
        @Override
        public int compare(Inscription arg0, Inscription arg1) {
          if (arg0.getIdentifier() == null) {
            return 1;
          }
          if (arg1.getIdentifier() == null) {
            return -1;
          }
          String s1 = (String) arg0.getIdentifier();
          String s2 = (String) arg1.getIdentifier();

          return super.compare(s1, s2);
        }
      });
    }
  }

  @Override
  public List<Inscription> getList(ResourceCollectionFilter filter) {
    List<Inscription> result = new ArrayList<Inscription>();
    if ((getRawList() == null) || (getRawList().size() <= 0) || (filter.getStart() > getRawList().size())) {
      return result;
    }

    // Filtre
    if ((filter.getQuery() != null) && !filter.getQuery().equals("")) {
      for (Inscription resource : getRawList()) {
        if (null == resource.getIdentifier()) {
          continue;
        }
        if ("strict".equals(filter.getMode())) {
          if (resource.getIdentifier().equals(filter.getQuery())) {
            result.add(resource);
          }
        }
        else {
          if (resource.getIdentifier().toLowerCase().startsWith(filter.getQuery().toLowerCase())) {
            result.add(resource);
          }
        }
      }
    }
    else {
      result.addAll(getRawList());
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
  public List<Inscription> retrieveByParent(String id) {
    return null;
  }

  @Override
  public String getCollectionName() {
    return COLLECTION_NAME;
  }

}

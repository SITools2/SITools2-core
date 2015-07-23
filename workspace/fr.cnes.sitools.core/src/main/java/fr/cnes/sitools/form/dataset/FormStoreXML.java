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
package fr.cnes.sitools.form.dataset;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.restlet.Context;

import fr.cnes.sitools.common.model.ResourceCollectionFilter;
import fr.cnes.sitools.common.store.SitoolsStoreXML;
import fr.cnes.sitools.form.dataset.model.Form;
import fr.cnes.sitools.form.dataset.model.SimpleParameter;

/**
 * Implementation of formStore with XStream FilePersistenceStrategy
 * 
 * @author AKKA
 * 
 */
@Deprecated
public final class FormStoreXML extends SitoolsStoreXML<Form> {

  /** default location for file persistence */
  private static final String COLLECTION_NAME = "forms";

  /**
   * Constructor with the XML file location
   * 
   * @param location
   *          directory of FilePersistenceStrategy
   * @param context
   *          the Context
   */
  public FormStoreXML(File location, Context context) {
    super(Form.class, location, context);
  }

  /**
   * Default constructor
   * 
   * @param context
   *          the Context
   */
  public FormStoreXML(Context context) {
    super(Form.class, context);
    File defaultLocation = new File(COLLECTION_NAME);
    init(defaultLocation);
  }

  @Override
  public List<Form> getList(ResourceCollectionFilter filter) {
    List<Form> result = new ArrayList<Form>();
    if ((getRawList() == null) || (getRawList().size() <= 0) || (filter.getStart() > getRawList().size())) {
      return result;
    }

    // Filtre parent en premier
    if ((filter.getParent() != null) && !filter.getParent().equals("")) {
      for (Form form : getRawList()) {
        if (null == form.getParent()) {
          continue;
        }
        if (form.getParent().equals(filter.getParent())) {
          result.add(form);
        }
      }
    }
    else {
      result.addAll(getRawList());
    }

    // Filtre Query
    if ((filter.getQuery() != null) && !filter.getQuery().equals("")) {
      for (Form form : result) {
        if (null == form.getName()) {
          result.remove(form);
          continue;
        }
        if (form.getName().toLowerCase().startsWith(filter.getQuery().toLowerCase())) {
          if ((filter.getParent() != null) && !filter.getParent().equals(form.getParent())) {
            result.remove(form);
          }
        }
      }
    }

    // Si index premier element > nombre d'elements filtres => resultat vide
    if (filter.getStart() > result.size()) {
      result.clear();
      return result;
    }

    // Tri
    sort(result, filter);

    return new ArrayList<Form>(result);
  }

  public Form update(Form form) {
    Form result = null;
    for (Iterator<Form> it = getRawList().iterator(); it.hasNext();) {
      Form current = it.next();
      if (current.getId().equals(form.getId())) {
        getLog().info("Updating Form");

        result = current;
        current.setName(form.getName());
        current.setDescription(form.getDescription());
        current.setParent(form.getParent());
        current.setParameters(form.getParameters());
        current.setWidth(form.getWidth());
        current.setHeight(form.getHeight());
        current.setCss(form.getCss());
        current.setParentUrl(form.getParentUrl());
        current.setZones(form.getZones());
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
   * XStream FilePersistenceStrategy initialization
   * 
   * @param location
   *          Directory
   */
  public void init(File location) {
    Map<String, Class<?>> aliases = new ConcurrentHashMap<String, Class<?>>();
    aliases.put("form", Form.class);
    aliases.put("SimpleParameter", SimpleParameter.class);
    this.init(location, aliases);
  }

  public List<Form> retrieveByParent(String id) {

    return null;
  }

  @Override
  public String getCollectionName() {
    return COLLECTION_NAME;
  }

}

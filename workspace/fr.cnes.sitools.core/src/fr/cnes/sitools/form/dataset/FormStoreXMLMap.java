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
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.restlet.Context;

import fr.cnes.sitools.common.model.ResourceCollectionFilter;
import fr.cnes.sitools.form.dataset.model.Form;
import fr.cnes.sitools.form.dataset.model.SimpleParameter;
import fr.cnes.sitools.persistence.XmlMapStore;

public class FormStoreXMLMap extends XmlMapStore<Form> implements FormStoreInterface {

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
  public FormStoreXMLMap(File location, Context context) {
    super(Form.class, location, context);
  }

  /**
   * Default constructor
   * 
   * @param context
   *          the Context
   */
  public FormStoreXMLMap(Context context) {
    super(Form.class, context);
    File defaultLocation = new File(COLLECTION_NAME);
    init(defaultLocation);
  }

  @Override
  public List<Form> retrieveByParent(String id) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String getCollectionName() {
    return COLLECTION_NAME;
  }

  @Override
  public void init(File location) {
    Map<String, Class<?>> aliases = new ConcurrentHashMap<String, Class<?>>();
    aliases.put("form", Form.class);
    aliases.put("SimpleParameter", SimpleParameter.class);
    this.init(location, aliases);
  }

  @Override
  public List<Form> getList(ResourceCollectionFilter filter) {
    List<Form> result = new ArrayList<Form>();
    if ((getList() == null) || (getList().size() <= 0) || (filter.getStart() > getList().size())) {
      return result;
    }

    // Filtre parent en premier
    if ((filter.getParent() != null) && !filter.getParent().equals("")) {
      for (Form form : getList()) {
        if (null == form.getParent()) {
          continue;
        }
        if (form.getParent().equals(filter.getParent())) {
          result.add(form);
        }
      }
    }
    else {
      result.addAll(getList());
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

  @Override
  public Form update(Form form) {
    Form result = null;
    
    Map<String, Form> map = getMap();
    Form current = map.get(form.getId());
    if (current == null) {
      getLog().warning("Cannot update " + COLLECTION_NAME + " that doesn't already exists");
      return null;
    }
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
    
    map.put(form.getId(), current);

    return result;
  }

}

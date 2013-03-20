/*******************************************************************************
 * Copyright 2011, 2012 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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
package fr.cnes.sitools.form.project;

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
import fr.cnes.sitools.form.project.model.FormProject;

/**
 * Implementation of CollectionStore with XStream FilePersistenceStrategy
 * 
 * @author AKKA
 * 
 */
public final class FormProjectStoreXML extends SitoolsStoreXML<FormProject> {

  /** default location for file persistence */
  private static final String COLLECTION_NAME = "formProject";

  /**
   * Constructor with the XML file location
   * 
   * @param location
   *          directory of FilePersistenceStrategy
   */
  public FormProjectStoreXML(File location, Context context) {
    super(FormProject.class, location, context);
  }

  /**
   * Default constructor
   */
  public FormProjectStoreXML(Context context) {
    super(FormProject.class, context);
    File defaultLocation = new File(COLLECTION_NAME);
    init(defaultLocation);
  }

  @Override
  public FormProject update(FormProject formProject) {
    FormProject result = null;
    for (Iterator<FormProject> it = getRawList().iterator(); it.hasNext();) {
      FormProject current = it.next();
      if (current.getId().equals(formProject.getId())) {
        getLog().info("Updating formProject");

        result = current;
        // specific projetForm parameters
        current.setDictionary(formProject.getDictionary());
        current.setCollection(formProject.getCollection());
        current.setParameters(formProject.getParameters());
        current.setProperties(formProject.getProperties());
        current.setNbDatasetsMax(formProject.getNbDatasetsMax());
        current.setUrlServicePropertiesSearch(formProject.getUrlServicePropertiesSearch());
        current.setUrlServiceDatasetSearch(formProject.getUrlServiceDatasetSearch());

        current.setIdServicePropertiesSearch(formProject.getIdServicePropertiesSearch());
        current.setIdServiceDatasetSearch(formProject.getIdServiceDatasetSearch());

        // Common parameters
        current.setId(formProject.getId());
        current.setName(formProject.getName());
        current.setDescription(formProject.getDescription());
        current.setParent(formProject.getParent());
        current.setParameters(formProject.getParameters());
        current.setWidth(formProject.getWidth());
        current.setHeight(formProject.getHeight());
        current.setCss(formProject.getCss());
        current.setParentUrl(formProject.getParentUrl());

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
  public void sort(List<FormProject> result, ResourceCollectionFilter filter) {
    if ((filter != null) && (filter.getSort() != null) && !filter.getSort().equals("")) {
      Collections.sort(result, new ResourceComparator<FormProject>(filter) {
        @Override
        public int compare(FormProject arg0, FormProject arg1) {

          String s1 = (String) arg0.getName();
          String s2 = (String) arg1.getName();

          return super.compare(s1, s2);
        }
      });
    }
  }

  /**
   * XStream FilePersistenceStrategy initialization
   * 
   * @param location
   *          Directory
   */
  public void init(File location) {
    Map<String, Class<?>> aliases = new ConcurrentHashMap<String, Class<?>>();
    aliases.put("FormProject", FormProject.class);
    this.init(location, aliases);
  }

  @Override
  public List<FormProject> retrieveByParent(String id) {
    return null;
  }

  @Override
  public String getCollectionName() {
    return COLLECTION_NAME;
  }

  @Override
  public List<FormProject> getList(ResourceCollectionFilter filter) {
    List<FormProject> result = new ArrayList<FormProject>();
    if ((getRawList() == null) || (getRawList().size() <= 0) || (filter.getStart() > getRawList().size())) {
      return result;
    }

    // Filtre parent en premier
    if ((filter.getParent() != null) && !filter.getParent().equals("")) {
      for (FormProject formProject : getRawList()) {
        if (null == formProject.getParent()) {
          continue;
        }
        if (formProject.getParent().equals(filter.getParent())) {
          result.add(formProject);
        }
      }
    }
    else {
      result.addAll(getRawList());
    }

    // Filtre Query
    if ((filter.getQuery() != null) && !filter.getQuery().equals("")) {
      for (FormProject formProject : result) {
        if (null == formProject.getName()) {
          result.remove(formProject);
        }
        if (formProject.getName().toLowerCase().startsWith(filter.getQuery().toLowerCase())) {
          if ((filter.getParent() != null) && !filter.getParent().equals(formProject.getParent())) {
            result.remove(formProject);
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

    return new ArrayList<FormProject>(result);
  }

}

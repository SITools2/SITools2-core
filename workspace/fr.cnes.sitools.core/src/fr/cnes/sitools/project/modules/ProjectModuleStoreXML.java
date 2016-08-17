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
package fr.cnes.sitools.project.modules;

import java.io.File;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.restlet.Context;

import fr.cnes.sitools.common.model.ResourceCollectionFilter;
import fr.cnes.sitools.common.model.ResourceComparator;
import fr.cnes.sitools.common.store.SitoolsStoreXML;
import fr.cnes.sitools.project.modules.model.ProjectModuleModel;

/**
 * Implementation of ProjectModuleStore with XStream FilePersistenceStrategy
 * 
 * @author AKKA Technologies
 * 
 */
@Deprecated
public final class ProjectModuleStoreXML extends SitoolsStoreXML<ProjectModuleModel> {

  /** default location for file persistence */
  private static final String COLLECTION_NAME = "ProjectModules";

  /**
   * Constructor with the XML file location
   * 
   * @param location
   *          directory of FilePersistenceStrategy
   * @param context
   *          the Restlet Context
   */
  public ProjectModuleStoreXML(File location, Context context) {
    super(ProjectModuleModel.class, location, context);
  }

  /**
   * Default constructor
   * 
   * @param context
   *          the Restlet Context
   */
  public ProjectModuleStoreXML(Context context) {
    super(ProjectModuleModel.class, context);
    File defaultLocation = new File(COLLECTION_NAME);
    init(defaultLocation);
  }

  @Override
  public ProjectModuleModel update(ProjectModuleModel module) {
    ProjectModuleModel result = null;
    for (Iterator<ProjectModuleModel> it = getRawList().iterator(); it.hasNext();) {
      ProjectModuleModel current = it.next();
      if (current.getId().equals(module.getId())) {
        getLog().info("Updating ProjectModule");

        result = current;
        current.setId(module.getId());
        current.setName(module.getName());
        current.setDescription(module.getDescription());
        current.setTitle(module.getTitle());

        current.setUrl(module.getUrl());
        current.setImagePath(module.getImagePath());
        current.setIcon(module.getIcon());

        current.setX(module.getX());
        current.setY(module.getY());
        current.setDefaultHeight(module.getDefaultHeight());
        current.setDefaultWidth(module.getDefaultWidth());

        current.setAuthor(module.getAuthor());
        current.setVersion(module.getVersion());

        current.setXtype(module.getXtype());
        current.setSpecificType(module.getSpecificType());

        current.setPriority(module.getPriority());

        current.setDependencies(module.getDependencies());

        current.setLabel(module.getLabel());

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
  public void sort(List<ProjectModuleModel> result, ResourceCollectionFilter filter) {
    if ((filter != null) && (filter.getSort() != null) && !filter.getSort().equals("")) {
      Collections.sort(result, new ResourceComparator<ProjectModuleModel>(filter) {
        @Override
        public int compare(ProjectModuleModel arg0, ProjectModuleModel arg1) {
          if (arg0.getName() == null) {
            return 1;
          }
          if (arg1.getName() == null) {
            return -1;
          }
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
    aliases.put("projectModule", ProjectModuleModel.class);
    this.init(location, aliases);
  }

  @Override
  public List<ProjectModuleModel> retrieveByParent(String id) {
    return null;
  }

  @Override
  public String getCollectionName() {
    return COLLECTION_NAME;
  }

}

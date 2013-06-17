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
package fr.cnes.sitools.project;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.restlet.Context;

import fr.cnes.sitools.common.store.SitoolsSynchronizedStoreXML;
import fr.cnes.sitools.project.model.Project;

/**
 * Implementation of ProjectStore with XStream FilePersistenceStrategy
 * 
 * @author AKKA
 * 
 */
public final class ProjectStoreXML extends SitoolsSynchronizedStoreXML<Project> {

  /** default location for file persistence */
  private static final String COLLECTION_NAME = "projects";

  /**
   * Constructor with the XML file location
   * 
   * @param location
   *          directory of FilePersistenceStrategy
   * @param context
   *          the Restlet Context
   */
  public ProjectStoreXML(File location, Context context) {
    super(Project.class, location, context);
  }

  /**
   * Default constructor
   * 
   * @param context
   *          the Restlet Context
   */
  public ProjectStoreXML(Context context) {
    super(Project.class, context);
    File defaultLocation = new File(COLLECTION_NAME);
    init(defaultLocation);
  }

  @Override
  public Project update(Project project) {
    Project result = null;
    List<Project> rawList = getRawList();
    synchronized (rawList) {
      for (Iterator<Project> it = rawList.iterator(); it.hasNext();) {
        Project current = it.next();
        if (current.getId().equals(project.getId())) {
          getLog().info("Updating Project");

          result = current;
          current.setName(project.getName());
          current.setDescription(project.getDescription());
          current.setImage(project.getImage());
          current.setCss(project.getCss());
          current.setDataSets(project.getDataSets());
          current.setStatus(project.getStatus());
          current.setSitoolsAttachementForUsers(project.getSitoolsAttachementForUsers());
          current.setVisible(project.isVisible());
          current.setModules(project.getModules());
          current.setHtmlDescription(project.getHtmlDescription());
          current.setMaintenanceText(project.getMaintenanceText());
          current.setMaintenance(project.isMaintenance());
          current.setHtmlHeader(project.getHtmlHeader());
          current.setLinks(project.getLinks());
          current.setFtlTemplateFile(project.getFtlTemplateFile());
          current.setNavigationMode(project.getNavigationMode());
          it.remove();

          break;
        }
      }
    }
    if (result != null) {
      rawList.add(result);
    }
    return result;
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.cnes.sitools.common.store.SitoolsStoreXML#create(fr.cnes.sitools.common.model.IResource)
   */
  @Override
  public Project create(Project resource) {

    return super.create(resource);
  }

  /**
   * XStream FilePersistenceStrategy initialization
   * 
   * @param location
   *          Directory
   */
  public void init(File location) {
    Map<String, Class<?>> aliases = new ConcurrentHashMap<String, Class<?>>();
    aliases.put("project", Project.class);
    this.init(location, aliases);
  }

  @Override
  public List<Project> retrieveByParent(String id) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String getCollectionName() {
    return COLLECTION_NAME;
  }

}

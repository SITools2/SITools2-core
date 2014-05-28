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
package fr.cnes.sitools.project;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.restlet.Context;

import fr.cnes.sitools.common.exception.SitoolsException;
import fr.cnes.sitools.persistence.XmlMapStore;
import fr.cnes.sitools.project.model.Project;

/**
 * Implementation of ProjectStore with XStream FilePersistenceStrategy
 * 
 * @author AKKA
 * 
 */
public final class ProjectStoreXMLMap extends XmlMapStore<Project> implements ProjectStoreInterface {

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
  public ProjectStoreXMLMap(File location, Context context) {
    super(Project.class, location, context);
  }

  /**
   * Default constructor
   * 
   * @param context
   *          the Restlet Context
   */
  public ProjectStoreXMLMap(Context context) {
    super(Project.class, context);
    File defaultLocation = new File(COLLECTION_NAME);
    init(defaultLocation);
  }

  @Override
  public Project update(Project project) {
    Project result = null;

    Map<String, Project> map = getMap();
    Project current = map.get(project.getId());
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
    map.put(project.getId(), current);
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
    aliases.put("project", Project.class);
    this.init(location, aliases);
  }

  @Override
  public List<Project> retrieveByParent(String id) {
    throw new RuntimeException(SitoolsException.NOT_IMPLEMENTED);
  }

  @Override
  public String getCollectionName() {
    return COLLECTION_NAME;
  }

}

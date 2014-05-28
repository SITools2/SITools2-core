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
package fr.cnes.sitools.project.modules;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.restlet.Context;

import fr.cnes.sitools.common.exception.SitoolsException;
import fr.cnes.sitools.persistence.XmlMapStore;
import fr.cnes.sitools.project.modules.model.ProjectModuleModel;

/**
 * Implementation of ProjectModuleModelStore with XStream FilePersistenceStrategy
 * 
 * @author AKKA
 * 
 */
public final class ProjectModuleStoreXMLMap extends XmlMapStore<ProjectModuleModel> implements
    ProjectModuleStoreInterface {

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
  public ProjectModuleStoreXMLMap(File location, Context context) {
    super(ProjectModuleModel.class, location, context);
  }

  /**
   * Default constructor
   * 
   * @param context
   *          the Restlet Context
   */
  public ProjectModuleStoreXMLMap(Context context) {
    super(ProjectModuleModel.class, context);
    File defaultLocation = new File(COLLECTION_NAME);
    init(defaultLocation);
  }

  @Override
  public ProjectModuleModel update(ProjectModuleModel module) {
    ProjectModuleModel result = null;

    Map<String, ProjectModuleModel> map = getMap();
    ProjectModuleModel current = map.get(module.getId());
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
    map.put(module.getId(), current);
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
    aliases.put("projectModule", ProjectModuleModel.class);
    this.init(location, aliases);
  }

  @Override
  public List<ProjectModuleModel> retrieveByParent(String id) {
    throw new RuntimeException(SitoolsException.NOT_IMPLEMENTED);
  }

  @Override
  public String getCollectionName() {
    return COLLECTION_NAME;
  }

}

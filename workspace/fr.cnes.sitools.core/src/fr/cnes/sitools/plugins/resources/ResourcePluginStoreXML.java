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
package fr.cnes.sitools.plugins.resources;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.restlet.Context;

import fr.cnes.sitools.common.store.SitoolsStoreXML;
import fr.cnes.sitools.plugins.resources.model.ResourceModel;

/**
 * Storage for resource plugins
 * 
 * @author m.marseille (AKKA Technologies)
 */
@Deprecated
public final class ResourcePluginStoreXML extends SitoolsStoreXML<ResourceModel> {

  /** default location for file persistence */
  private static final String COLLECTION_NAME = "resourcePlugins";

  /**
   * Default constructor
   * 
   * @param context
   *          the Restlet Context
   */
  public ResourcePluginStoreXML(Context context) {
    super(ResourceModel.class, context);
    File defaultLocation = new File(COLLECTION_NAME);
    init(defaultLocation);
  }

  /**
   * Constructor with the XML file location
   * 
   * @param location
   *          directory of FilePersistenceStrategy
   * @param context
   *          the Restlet Context
   */
  public ResourcePluginStoreXML(File location, Context context) {
    super(ResourceModel.class, location, context);
  }

  @Override
  public void init(File location) {
    Map<String, Class<?>> aliases = new ConcurrentHashMap<String, Class<?>>();
    aliases.put("resourcePlugin", ResourceModel.class);
    this.init(location, aliases);
  }

  @Override
  public ResourceModel update(ResourceModel resource) {
    ResourceModel result = null;
    for (Iterator<ResourceModel> it = getRawList().iterator(); it.hasNext();) {
      ResourceModel current = it.next();
      if (current.getId().equals(resource.getId())) {
        getLog().info("Updating resource plugin");

        result = current;
        current.setId(resource.getId());
        current.setName(resource.getName());
        current.setDescription(resource.getDescription());
        current.setClassAuthor(resource.getClassAuthor());
        current.setClassVersion(resource.getClassVersion());
        current.setClassName(resource.getClassName());
        current.setClassOwner(resource.getClassOwner());
        // current.setCurrentClassAuthor(resource.getCurrentClassVersion());
        // current.setCurrentClassVersion(resource.getCurrentClassVersion());
        current.setParametersMap(resource.getParametersMap());
        current.setDescriptionAction(resource.getDescriptionAction());
        // specific ResourceModelDTO attributes
        current.setApplicationClassName(resource.getApplicationClassName());
        current.setDataSetSelection(resource.getDataSetSelection());
        current.setParent(resource.getParent());
        current.setResourceClassName(resource.getResourceClassName());
        current.setBehavior(resource.getBehavior());
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
   * Get the list of resources
   * 
   * @param id
   *          the parent id
   * @return the list
   */
  @Override
  public List<ResourceModel> retrieveByParent(String id) {
    List<ResourceModel> result = new ArrayList<ResourceModel>();
    for (Iterator<ResourceModel> it = getRawList().iterator(); it.hasNext();) {
      ResourceModel current = it.next();
      if (current.getParent().equals(id)) {
        getLog().info("Application found");
        result.add(current);

      }
    }
    return result;
  }

  @Override
  public String getCollectionName() {
    return COLLECTION_NAME;
  }

}

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
package fr.cnes.sitools.plugins.guiservices.implement;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.restlet.Context;

import fr.cnes.sitools.persistence.XmlMapStore;
import fr.cnes.sitools.plugins.guiservices.implement.model.GuiServicePluginModel;

/**
 * Implementation of GuiServicePluginModelStore with XStream FilePersistenceStrategy
 * 
 * @author AKKA
 * 
 */
public final class GuiServicePluginStoreXMLMap extends XmlMapStore<GuiServicePluginModel> implements
    GuiServicePluginStoreInterface {

  /** default location for file persistence */
  private static final String COLLECTION_NAME = "GuiServicePlugins";

  /**
   * Constructor with the XML file location
   * 
   * @param location
   *          directory of FilePersistenceStrategy
   * @param context
   *          the Restlet Context
   */
  public GuiServicePluginStoreXMLMap(File location, Context context) {
    super(GuiServicePluginModel.class, location, context);
  }

  /**
   * Default constructor
   * 
   * @param context
   *          the Restlet Context
   */
  public GuiServicePluginStoreXMLMap(Context context) {
    super(GuiServicePluginModel.class, context);
    File defaultLocation = new File(COLLECTION_NAME);
    init(defaultLocation);
  }

  @Override
  public GuiServicePluginModel update(GuiServicePluginModel guiService) {
    GuiServicePluginModel result = null;

    Map<String, GuiServicePluginModel> map = getMap();
    GuiServicePluginModel current = map.get(guiService.getId());
    result = current;
    current.setId(guiService.getId());
    current.setName(guiService.getName());
    current.setDescription(guiService.getDescription());

    current.setAuthor(guiService.getAuthor());
    current.setVersion(guiService.getVersion());

    current.setXtype(guiService.getXtype());

    current.setPriority(guiService.getPriority());

    current.setDependencies(guiService.getDependencies());

    current.setLabel(guiService.getLabel());
    current.setIcon(guiService.getIcon());

    current.setDescriptionAction(guiService.getDescriptionAction());
    current.setDataSetSelection(guiService.getDataSetSelection());

    current.setParameters(guiService.getParameters());
    current.setParent(guiService.getParent());
    map.put(guiService.getId(), current);

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
    aliases.put("GuiServicePluginModel", GuiServicePluginModel.class);
    this.init(location, aliases);
  }

  @Override
  public List<GuiServicePluginModel> retrieveByParent(String id) {
    List<GuiServicePluginModel> result = new ArrayList<GuiServicePluginModel>();
    for (Iterator<GuiServicePluginModel> it = getList().iterator(); it.hasNext();) {
      GuiServicePluginModel current = it.next();
      if (current.getParent().equals(id)) {
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

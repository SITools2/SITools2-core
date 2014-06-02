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
package fr.cnes.sitools.plugins.guiservices.declare;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.restlet.Context;

import fr.cnes.sitools.persistence.XmlMapStore;
import fr.cnes.sitools.plugins.guiservices.declare.model.GuiServiceModel;

public class GuiServiceStoreXMLMap extends XmlMapStore<GuiServiceModel> implements GuiServiceStoreInterface {

  /** default location for file persistence */
  private static final String COLLECTION_NAME = "GuiServices";

  /**
   * Constructor with the XML file location
   * 
   * @param location
   *          directory of FilePersistenceStrategy
   * @param context
   *          the Context
   */
  public GuiServiceStoreXMLMap(File location, Context context) {
    super(GuiServiceModel.class, location, context);
  }

  /**
   * Default constructor
   * 
   * @param context
   *          the Context
   */
  public GuiServiceStoreXMLMap(Context context) {
    super(GuiServiceModel.class, context);
    File defaultLocation = new File(COLLECTION_NAME);
    init(defaultLocation);
  }

  @Override
  public List<GuiServiceModel> retrieveByParent(String id) {
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
    aliases.put("guiService", GuiServiceModel.class);
    this.init(location, aliases);
  }

  @Override
  public GuiServiceModel update(GuiServiceModel guiService) {
    GuiServiceModel result = null;

    Map<String, GuiServiceModel> map = getMap();
    GuiServiceModel current = map.get(guiService.getId());

    getLog().info("Updating ProjectguiService");

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
    current.setDefaultGuiService(guiService.isDefaultGuiService());
    current.setDataSetSelection(guiService.getDataSetSelection());
    current.setDefaultVisibility(guiService.isDefaultVisibility());

    map.put(guiService.getId(), current);

    return result;
  }

}

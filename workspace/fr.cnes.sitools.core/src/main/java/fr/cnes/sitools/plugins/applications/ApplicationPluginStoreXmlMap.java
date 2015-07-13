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
package fr.cnes.sitools.plugins.applications;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.restlet.Context;

import fr.cnes.sitools.common.exception.SitoolsException;
import fr.cnes.sitools.persistence.XmlMapStore;
import fr.cnes.sitools.plugins.applications.model.ApplicationPluginModel;

/**
 * Specialized XML Persistence implementation of ApplicationPluginModel.
 * 
 * @author jp.boignard (AKKA Technologies)
 */
public class ApplicationPluginStoreXmlMap extends XmlMapStore<ApplicationPluginModel> implements
    ApplicationPluginStoreInterface {

  /** default location for file persistence */
  private static final String COLLECTION_NAME = "plugins_applications";

  /**
   * Constructor without file
   * 
   * @param context
   *          the Restlet Context
   */
  public ApplicationPluginStoreXmlMap(Context context) {
    super(ApplicationPluginModel.class, context);
    File defaultLocation = new File(COLLECTION_NAME);
    init(defaultLocation);
  }

  /**
   * Constructor with file location
   * 
   * @param location
   *          the file location
   * @param context
   *          the Restlet Context
   */
  public ApplicationPluginStoreXmlMap(File location, Context context) {
    super(ApplicationPluginModel.class, location, context);
  }

  @Override
  public void init(File location) {
    Map<String, Class<?>> aliases = new ConcurrentHashMap<String, Class<?>>();
    aliases.put("ApplicationPluginModel", ApplicationPluginModel.class);
    this.init(location, aliases);

    // TODO --> instead autodetecteAnnotations use alias.put * here
    getXstream().autodetectAnnotations(true);
  }

  public List<ApplicationPluginModel> retrieveByParent(String id) {
    throw new RuntimeException(SitoolsException.NOT_IMPLEMENTED);
  }

  @Override
  public String getCollectionName() {
    return COLLECTION_NAME;
  }

}

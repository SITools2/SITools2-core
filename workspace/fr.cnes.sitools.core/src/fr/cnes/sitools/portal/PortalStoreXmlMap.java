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
package fr.cnes.sitools.portal;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.restlet.Context;

import fr.cnes.sitools.common.exception.SitoolsException;
import fr.cnes.sitools.persistence.XmlMapStore;
import fr.cnes.sitools.portal.model.Portal;

/**
 * Specialized XML Persistence implementation of PortalStoreInterface.
 * 
 * @author jp.boignard (AKKA Technologies)
 */
public final class PortalStoreXmlMap extends XmlMapStore<Portal> implements PortalStoreInterface {
  
  /** default location for file persistence */
  private static final String COLLECTION_NAME = Portal.class.getSimpleName().toLowerCase(); 
  
  /**
   * Constructor
   * 
   * @param storageRoot
   *          Path for file persistence strategy
   * @param context
   *          the Restlet Context
   */
  public PortalStoreXmlMap(File storageRoot, Context context) {
    super(Portal.class, storageRoot, context);
  }

  @Override
  public List<Portal> retrieveByParent(String id) {
    throw new RuntimeException(SitoolsException.NOT_IMPLEMENTED);
  }
  
  @Override
  public String getCollectionName() {
    return COLLECTION_NAME;
  }

  @Override
  public void init(File location) {
    Map<String, Class<?>> aliases = new HashMap<String, Class<?>>();
    aliases.put(COLLECTION_NAME, Portal.class);
    this.init(location, aliases); 
  }
  
}

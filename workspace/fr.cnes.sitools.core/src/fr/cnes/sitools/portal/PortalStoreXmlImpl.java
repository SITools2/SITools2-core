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

import org.restlet.Context;

import fr.cnes.sitools.persistence.XmlPersistenceDaoImpl;
import fr.cnes.sitools.portal.model.Portal;

/**
 * Specialized XML Persistence implementation of PortalStore.
 * 
 * @author jp.boignard (AKKA Technologies)
 */
public final class PortalStoreXmlImpl extends XmlPersistenceDaoImpl<Portal> implements PortalStore {

  /**
   * Constructor
   * 
   * @param storageRoot
   *          Path for file persistence strategy
   * @param context
   *          the Restlet Context
   */
  public PortalStoreXmlImpl(File storageRoot, Context context) {
    super(storageRoot, context);
  }

}

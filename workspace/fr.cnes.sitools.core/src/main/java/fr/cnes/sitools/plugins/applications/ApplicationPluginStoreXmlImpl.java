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
package fr.cnes.sitools.plugins.applications;

import java.io.File;

import org.restlet.Context;

import fr.cnes.sitools.persistence.XmlPersistenceDaoImpl;
import fr.cnes.sitools.plugins.applications.model.ApplicationPluginModel;

/**
 * Specialized XML Persistence implementation of SvaTaskStore.
 * 
 * @author jp.boignard (AKKA Technologies)
 */
public final class ApplicationPluginStoreXmlImpl extends XmlPersistenceDaoImpl<ApplicationPluginModel> implements
    ApplicationPluginStore {

  /**
   * Constructor
   * 
   * @param storageRoot
   *          Path for file persistence strategy
   * @param context
   *          the Restlet Context
   * 
   */
  public ApplicationPluginStoreXmlImpl(File storageRoot, Context context) {
    super(storageRoot, context);

    getXstream().autodetectAnnotations(true);
    getXstream().alias("ApplicationPluginModel", ApplicationPluginModel.class);
  }
}

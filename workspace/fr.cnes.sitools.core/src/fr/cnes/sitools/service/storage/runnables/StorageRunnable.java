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
package fr.cnes.sitools.service.storage.runnables;

import org.restlet.Context;

import fr.cnes.sitools.service.storage.DataStorageStore;
import fr.cnes.sitools.service.storage.StorageAdministration;
import fr.cnes.sitools.service.storage.model.StorageDirectory;

/**
 * Abstract class for StorageRunnable object
 * 
 * 
 * @author jp.boignard (AKKA Technologies)
 */
public abstract class StorageRunnable implements Runnable {

  /** Storage object */
  protected StorageDirectory sd;
  /** StorageDirectory store */
  protected DataStorageStore store;
  /** SolrUrl */
  protected String solrUrl;
  /** Context */
  protected Context context;
  /** Storage Application */
  protected StorageAdministration application;
  
}

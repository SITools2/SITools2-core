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
package fr.cnes.sitools.json;

import org.restlet.data.MediaType;

import fr.cnes.sitools.AbstractStorageDirectoryTestCase;
import fr.cnes.sitools.api.DocAPI;

/**
 * Storage directory test case for JSON requests
 * @author m.marseille (AKKA Technologies)
 */
public class StorageDirectoryTestCase extends AbstractStorageDirectoryTestCase {

  static {
    setMediaTest(MediaType.APPLICATION_JSON);

    docAPI = new DocAPI(StorageDirectoryTestCase.class, "Storage directory API with JSON format");
    docAPI.setActive(true);
    docAPI.setMediaTest(MediaType.APPLICATION_JSON);

  }

  /**
   * Default constructor
   */
  public StorageDirectoryTestCase() {
    super();
  }
  
}

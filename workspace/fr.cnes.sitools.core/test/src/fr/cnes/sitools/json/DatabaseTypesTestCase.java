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
package fr.cnes.sitools.json;

import org.restlet.data.MediaType;

import fr.cnes.sitools.AbstractDatabaseTypesTestCase;
import fr.cnes.sitools.api.DocAPI;

/**
 * Test case of database types exchanges in JSON format
 * 
 * @author d.arpin (AKKA Technologies)
 */
public class DatabaseTypesTestCase extends AbstractDatabaseTypesTestCase {
  static {
    setMediaTest(MediaType.APPLICATION_JSON);

    docAPI = new DocAPI(DatabaseTypesTestCase.class, "Database type test case");
    docAPI.setActive(true);
    docAPI.setMediaTest(MediaType.APPLICATION_JSON);

  }

  /**
   * Default constructor
   */
  public DatabaseTypesTestCase() {
    super();
  }
}

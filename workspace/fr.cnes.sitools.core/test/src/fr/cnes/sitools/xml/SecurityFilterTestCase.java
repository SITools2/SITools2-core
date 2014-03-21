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
package fr.cnes.sitools.xml;

import org.restlet.data.MediaType;

import fr.cnes.sitools.AbstractSecurityFilterTestCase;
import fr.cnes.sitools.api.DocAPI;

/**
 * Tests the filter for security Try to access the record API with a Security
 * Filter attached to it The Filter is configured to block every access, so
 * every request will fail.
 * 
 *
 * @author m.gond (AKKA Technologies)
 */
public class SecurityFilterTestCase extends AbstractSecurityFilterTestCase {
  static {
    setMediaTest(MediaType.APPLICATION_XML);
    docAPI = new DocAPI(SecurityFilterTestCase.class, "Security filter test case with XML format");
    docAPI.setMediaTest(MediaType.APPLICATION_XML);
  }

  /**
   * Default constructor
   */
  public SecurityFilterTestCase() {
    super();
  }
}

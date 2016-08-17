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
package fr.cnes.sitools.xml;

import org.restlet.data.MediaType;

import fr.cnes.sitools.AbstractFilterPluginTestCase;
import fr.cnes.sitools.api.DocAPI;

/**
 * Tests for filter plugins, format XML
 * 
 * @author m.marseille (AKKA Technologies)
 * 
 */
public class FilterPluginTestCase extends AbstractFilterPluginTestCase {

  static {
    docAPI = new DocAPI(FilterPluginTestCase.class, "Filter Plugin API with XML format");
    docAPI.setActive(true);
    docAPI.setMediaTest(MediaType.APPLICATION_XML);

  }

  /**
   * Default constructor
   */
  public FilterPluginTestCase() {
    super();

  }
}

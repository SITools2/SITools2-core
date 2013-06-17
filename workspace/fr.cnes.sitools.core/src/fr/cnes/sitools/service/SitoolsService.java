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
package fr.cnes.sitools.service;

import java.util.Map;

import org.restlet.Component;

import fr.cnes.sitools.common.SitoolsSettings;

/**
 * Interface for SITools services
 * @author m.marseille (AKKA Technologies)
 */
public interface SitoolsService {

  /**
   * Get component of the service
   * @return the RESTlet component of the service
   */
  Component getComponent();

  /**
   * Start the service
   */
  void start();

  /**
   * Stop the service
   */
  void stop();

  /**
   * Restart the service
   */
  void restart();

  /**
   * Get settings of the service
   * @return the SITools set of settings
   */
  SitoolsSettings getSettings();

  /**
   * Get properties of the service
   * @return the table of properties
   */
  Map<String, Object> getProperties();
}

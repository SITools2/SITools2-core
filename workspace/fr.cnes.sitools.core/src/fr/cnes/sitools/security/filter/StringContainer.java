/*******************************************************************************
 * Copyright 2012 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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
package fr.cnes.sitools.security.filter;

/**
 * Interface for providing "contains" method ...
 * Used by BlackList ip filter
 *
 * @author jp.boignard (AKKA Technologies)
 */
public interface StringContainer {

  /**
   * Abstract method to check if ip is black listed
   * @param ip
   *          String IP address (X.X.X.X with X between 0 and 255)
   * @return true if ip is blacklisted
   */
  boolean contains(String ip);

}
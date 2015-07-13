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
package fr.cnes.sitools.security.model;

/**
 * Data policy class
 * @author AKKA
 *
 */
public final class DataPolicy {

  /**
   * Metadata
   */
  private boolean metadata;
  
  /**
   * Data
   */
  private boolean data;

  /**
   * Check if metadata
   * @return true if metadata
   */
  public boolean isMetadata() {
    return metadata;
  }

  /**
   * Indicates if metadata
   * @param metadata true if metadata
   */
  public void setMetadata(boolean metadata) {
    this.metadata = metadata;
  }

  /**
   * Check if data
   * @return true if data
   */
  public boolean isData() {
    return data;
  }

  /**
   * Indicates if data
   * @param data true if data
   */
  public void setData(boolean data) {
    this.data = data;
  }

}

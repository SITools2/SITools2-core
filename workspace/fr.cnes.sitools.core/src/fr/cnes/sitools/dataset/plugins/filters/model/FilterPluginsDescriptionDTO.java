/*******************************************************************************
 * Copyright 2011, 2012 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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
package fr.cnes.sitools.dataset.plugins.filters.model;

import fr.cnes.sitools.common.model.AbstractPluginsDescriptionDTO;

/**
 * DTO class to store filterDescription
 * 
 * @author AKKA Technologies
 */
public final class FilterPluginsDescriptionDTO extends AbstractPluginsDescriptionDTO {
  /**
   * DefaultFilters
   */
  private Boolean defaultFilter = false;

  /**
   * Gets the defaultFilter value
   * @return the defaultFilter
   */
  public Boolean getDefaultFilter() {
    return defaultFilter;
  }

  /**
   * Sets the value of defaultFilter
   * @param defaultFilter the defaultFilter to set
   */
  public void setDefaultFilter(Boolean defaultFilter) {
    this.defaultFilter = defaultFilter;
  }
  
  

}

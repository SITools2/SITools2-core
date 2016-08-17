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
package fr.cnes.sitools.project.model;

import java.util.List;

/**
 * Class definition of a project DTO to define a priority and category
 * 
 * 
 * @author b.fiorito (AKKA Technologies)
 * 
 */
public final class ProjectPriorityDTO {

  /** list of minimal projects properties */
  private List<MinimalProjectPriorityDTO> minimalProjectPriorityList;

  /**
   * Gets the minimalProjectPriorityList value
   * @return the minimalProjectPriorityList
   */
  public List<MinimalProjectPriorityDTO> getMinimalProjectPriorityList() {
    return minimalProjectPriorityList;
  }

  /**
   * Sets the value of minimalProjectPriorityList
   * @param minimalProjectPriorityList the minimalProjectPriorityList to set
   */
  public void setMinimalProjectPriorityList(List<MinimalProjectPriorityDTO> minimalProjectPriorityList) {
    this.minimalProjectPriorityList = minimalProjectPriorityList;
  }

}
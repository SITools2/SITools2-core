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
package fr.cnes.sitools.client.model;

/**
 * DTO Class to store AdminIndex information temporarily
 * 
 * @author b.fiorito (AKKA Technologies)
 */
public class AdminIndexDTO {

  /**
   * sitools base url
   */
  private String appUrl;

  /**
   * Gets the appUrl value
   * @return the appUrl
   */
  public String getAppUrl() {
    return appUrl;
  }

  /**
   * Sets the value of appUrl
   * @param appUrl the appUrl to set
   */
  public void setAppUrl(String appUrl) {
    this.appUrl = appUrl;
  }
  
  
  
}

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
package fr.cnes.sitools.status;

import org.restlet.data.Status;
import org.restlet.service.StatusService;

/**
 * StatusDTO for freemarker template
 * 
 * @author jp.boignard (AKKA Technologies)
 * 
 */
public class StatusDTO {
  
  /** status */
  private Status status;
  
  /** Service */
  private StatusService service;

  /**
   * Constructor
   */
  public StatusDTO() {
    super();
  }

  /**
   * Gets the status value
   * 
   * @return the status
   */
  public Status getStatus() {
    return status;
  }

  /**
   * Sets the value of status
   * 
   * @param status
   *          the status to set
   */
  public void setStatus(Status status) {
    this.status = status;
  }

  /**
   * Gets the service value
   * 
   * @return the service
   */
  public StatusService getService() {
    return service;
  }

  /**
   * Sets the value of service
   * 
   * @param service
   *          the service to set
   */
  public void setService(StatusService service) {
    this.service = service;
  }

  /**
   * Status description if one
   * 
   * @param status
   * @return String
   */
  public String getStatusInfo() {
    return (status.getName() != null) ? status.getName() : "No information available for this result status";
  }

}

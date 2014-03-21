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
package fr.cnes.sitools.tasks.model;

/**
 * Enumeration of possible status of a SVA
 * @author m.marseille (AKKA Technologies)
 */
public enum TaskStatus {
  
  /**
   * Job starting up
   */
  TASK_STATUS_PENDING,
  
  /**
   * Job running status
   */
  TASK_STATUS_RUNNING,
  
  /**
   * Job aborted due to failure
   */
  TASK_STATUS_FAILURE,
  
  /**
   * Job canceled by the user
   */
  TASK_STATUS_CANCELED,
  
  /**
   * Treatment finished
   */
  TASK_STATUS_FINISHED,
  
  /**
   * Job canceled because of server stop while RUNNING
   */
  TASK_STATUS_CANCELED_RUNNING,
  
  /**
   * Job canceled because of server stop while PENDING
   */
  TASK_STATUS_CANCELED_PENDING
  
}

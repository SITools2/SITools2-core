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
package fr.cnes.sitools.tasks.model;

/**
 * Enumeration for RunTypes declaration 
 * 
 * @author m.gond (AKKA Technologies)
 */
public enum TaskRunTypeAdministration {
  /** Synchronous processing Forced */
  TASK_FORCE_RUN_SYNC,
  /** Asynchronous processing Forced */
  TASK_FORCE_RUN_ASYNC,
  /** Synchronous processing by default, but the client can change it */
  TASK_DEFAULT_RUN_SYNC,
  /** Asynchronous processing by default, but the client can change it */
  TASK_DEFAULT_RUN_ASYNC,
}

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
package fr.cnes.sitools.plugins.resources.model;

/**
 * Parameter types for Parameterized resources
 * 
 * @author m.marseille (AKKA Technologies)
 */
public enum ResourceParameterType {

  // BEGIN ADDED FROM SvaParameterType
  // A remplacer par l'utilisation du valueType xs:dataset.column.name ou xs:dictionary
  /**
   * A parameter IN is read only
   */
  @Deprecated
  PARAMETER_IN,

  /**
   * A parameter OUT is write only
   */
  @Deprecated
  PARAMETER_OUT,

  /**
   * A parameter INOUT is readable and writable
   */
  @Deprecated
  PARAMETER_INOUT,

  // END ADDED FROM SvaParameterType

  /**
   * Attach type for url attachments
   */
  PARAMETER_ATTACHMENT,

  /**
   * Intern Parameters for resource bean specificity
   */
  PARAMETER_INTERN,

  /**
   * Parameter to be set up by the final user before service call, use default otherwise
   */
  PARAMETER_USER_INPUT,
  /**
   * Parameter used only in the client interface
   */
  PARAMETER_USER_GUI
}

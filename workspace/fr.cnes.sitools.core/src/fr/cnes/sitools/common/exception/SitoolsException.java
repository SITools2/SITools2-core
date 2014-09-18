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
package fr.cnes.sitools.common.exception;

/**
 * Base class for Exceptions.
 * 
 * @author jp.boignard (AKKA Technologies)
 * 
 */
public class SitoolsException extends Exception {

  /** RuntimeException message to be thrown when a method is not implemented */
  public static final String NOT_IMPLEMENTED = "NOT IMPLEMENTED";
  
//  /** RuntimeException message to be thrown when a method is not applicable for a class */
//  public static final String NOT_APPLICABLE = "NOT APPLICABLE";
  
  /**
   * serialVersionUID.
   */
  private static final long serialVersionUID = 1L;
  
  /**
   * Constructor with message and cause thrown.
   * 
   * @param message
   *          message to be sent
   * @param cause
   *          what causes exception
   */
  public SitoolsException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * Constructor with message.
   * 
   * @param message
   *          message to be sent
   */
  public SitoolsException(String message) {
    super(message);
  }

}

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
package fr.cnes.sitools.security.authorization.business;

import java.util.ArrayList;
import java.util.List;

import org.restlet.data.Method;

/**
 * Authorization utility
 * 
 * @author jp.boignard (AKKA Technologies)
 * 
 */
public final class Util {

  /**
   * Private constructor for utility classes
   */
  private Util() {
  }

  /**
   * Gets common methods used in sitools
   * 
   * @return ArrayList<Method>
   */
  public static List<Method> getReferencedMethods() {
    return toArrayList(Method.GET, Method.HEAD, Method.POST, Method.PUT, Method.DELETE, Method.OPTIONS);
  }

  /**
   * Wrapper
   * 
   * @param methods
   *          liste of Method
   * @return ArrayList<Method>
   */
  private static List<Method> toArrayList(Method... methods) {
    List<Method> result = new ArrayList<Method>();
    for (Method method : methods) {
      result.add(method);
    }
    return result;
  }

}

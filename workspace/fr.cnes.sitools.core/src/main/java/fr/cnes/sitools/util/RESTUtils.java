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
package fr.cnes.sitools.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * Tools for 
 * 
 *
 * @author jp.boignard (AKKA Technologies)
 */
public final class RESTUtils {

  /**
   * Constructor
   */
  private RESTUtils() {
    super();
  }
  
  /**
   * Decodes string encoded in UTF-8 (useful for request parameters, or url fragments)
   * @param value url fragment
   * @return decoded String
   */
  public static String decode(Object value) {
    if (value == null) {
      return null;
    }

    try {
      return URLDecoder.decode(value.toString(), "UTF-8");
    }
    catch (UnsupportedEncodingException e) {
      return null;
    }
  }
  
}

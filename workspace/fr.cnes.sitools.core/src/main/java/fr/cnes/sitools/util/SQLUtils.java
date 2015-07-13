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

/**
 * Mysql Utilities
 * 
 * @author Ralph Ritoch <rritoch@gmail.com>, method mysqlRealEscapeString rest is m.gond (AKKA Technologies)
 * @copyright Ralph Ritoch 2011 ALL RIGHTS RESERVED
 * @link http://www.vnetpublishing.com
 * 
 */
public final class SQLUtils {

  /**
   * SQLUtils constructor
   */
  private SQLUtils() {
    super();
  }

  /**
   * Escape string to protected against SQL Injection
   * 
   * You must add a single quote ' around the result of this function for data, or a backtick ` around table and row
   * identifiers. If this function returns null than the result should be changed to "NULL" without any quote or
   * backtick.
   * 
   * @param str
   *          the string to excape
   * @return the escaped string
   * @throws Exception
   *           if there is an error
   */
  public static String mysqlRealEscapeString(String str) throws Exception {
    if (str == null) {
      return null;
    }

    if (str.replaceAll("[a-zA-Z0-9_!@#$%^&*()-=+~.;:,\\Q[\\E\\Q]\\E<>{}\\/? ]", "").length() < 1) {
      return str;
    }

    String cleanString = str;
    cleanString = cleanString.replaceAll("\\\\", "\\\\\\\\");
    cleanString = cleanString.replaceAll("\\n", "\\\\n");
    cleanString = cleanString.replaceAll("\\r", "\\\\r");
    cleanString = cleanString.replaceAll("\\t", "\\\\t");
    cleanString = cleanString.replaceAll("\\00", "\\\\0");
    cleanString = cleanString.replaceAll("'", "''");
    cleanString = cleanString.replaceAll("\\\"", "\\\\\"");

    if (cleanString.replaceAll("[a-zA-Z0-9_!@#$%^&*()-=+~.;:,\\Q[\\E\\Q]\\E<>{}\\/?\\\\\"' ]", "").length() < 1) {
      return cleanString;
    }
    return cleanString;
  }

  /**
   * Escape data to protected against SQL Injection
   * 
   * @param str
   *          the string to escape
   * @return the escaped string
   * @throws Exception
   *           if something is wrong
   */
  public static String escapeString(String str) throws Exception {
    if (str == null) {
      return "NULL";
    }
    return mysqlRealEscapeString(str);
  }

}

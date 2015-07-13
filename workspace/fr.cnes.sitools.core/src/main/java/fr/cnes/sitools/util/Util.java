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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility Class
 * 
 * @author AKKA
 * 
 */
public final class Util {

  /**
   * Private constructor for utility class
   */
  private Util() {
    super();
  }

  /**
   * Check if object is not null
   * 
   * @param object
   *          Object
   * @return true if object not null
   */
  public static boolean isSet(Object object) {
    return (null != object);
  }

  /**
   * Check if object is not null and not empty
   * 
   * @param object
   *          the String to check
   * @return true if object is not empty, false otherwise
   */
  public static boolean isNotEmpty(String object) {
    return (null != object) && !(object.equals(""));
  }

  /**
   * Check if object is null or empty
   * 
   * @param object
   *          the String to check
   * @return true if object is empty or null, false otherwise
   */
  public static boolean isEmpty(String object) {
    return (null == object) || object.equals("");
  }

  /**
   * Check if object is not null and not empty and text is "true".
   * 
   * @param object
   *          the Object
   * @return true if object is not null and not empty and text is "true", false otherwise
   */
  public static boolean isTrue(Object object) {
    return (null != object) && object.toString().equalsIgnoreCase("true");
  }

  /**
   * Check that email is a valid email Address
   * 
   * @param email
   *          the email Address String
   * @return true if email is a valid email address, false otherwise
   */
  public static boolean isValidEmail(String email) {
    boolean isValid = false;
    String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
    CharSequence inputStr = email;
    // Make the comparison case-insensitive.
    Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
    Matcher matcher = pattern.matcher(inputStr);
    if (matcher.matches()) {
      isValid = true;
    }
    return isValid;
  }

  /**
   * Indicates if the current operating system is in the Windows family.
   * 
   * @return True if the current operating system is in the Windows family.
   */
  public static boolean isWindows() {
    return System.getProperty("os.name").toLowerCase().indexOf("win") >= 0;
  }

}

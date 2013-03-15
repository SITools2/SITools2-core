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
package fr.cnes.sitools.datasource.jdbc.business;

import fr.cnes.sitools.datasource.jdbc.model.Structure;

/**
 * Utility class for JDBC Structure querying
 * 
 * @author jp.boignard (AKKA Technologies)
 * 
 */
public final class Wrapper {

  /**
   * Private constructor for utility classes
   */
  private Wrapper() {
    super();
  }

  /**
   * Gets the reference string of the attribute in the SQL structure
   * 
   * @param structure
   *          the referenced Structure
   * @param attributeName
   *          the given attribute name
   * @return String reference
   */
  public static String getReference(Structure structure, String attributeName) {
    if ((structure.getAlias() == null) || structure.getAlias().equals("")) {
      return structure.getName() + "." + attributeName;
    }
    else {
      return structure.getAlias() + "." + attributeName;
    }
  }

  /**
   * Gets reference of the structure
   * 
   * @param structure
   *          the referenced Structure
   * @return String reference
   */
  public static String getReference(Structure structure) {
    return (structure.getSchemaName() != null) ? (structure.getSchemaName() + "." + structure.getName()) : structure
        .getName();
  }

  /**
   * Gets reference of the structure
   * 
   * @param structure
   *          the referenced Structure
   * @return String reference
   */
  public static String getFROMReference(Structure structure) {
    return (structure.getSchemaName() != null) ? (structure.getSchemaName() + ".\"" + structure.getName() + "\"")
        : "\"" + structure.getName() + "\"";
  }

}

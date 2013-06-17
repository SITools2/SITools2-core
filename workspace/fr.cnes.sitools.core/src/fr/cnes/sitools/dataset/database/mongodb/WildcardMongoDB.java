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
package fr.cnes.sitools.dataset.database.mongodb;

import fr.cnes.sitools.dataset.model.Wildcard;

/**
 * Utility class to get Query operator String for MongoDB
 * 
 * 
 * @author m.gond
 */
public final class WildcardMongoDB {

  /**
   * Default private constructor
   */
  private WildcardMongoDB() {

  }

  /**
   * Get the operator value for a MongoDB query
   * 
   * @param wildcard
   *          the {@link Wildcard}
   * @param value
   *          the value to transform
   * @return the string representation of this operator for MongoDB
   */
  public static String applyWildcard(Wildcard wildcard, String value) {
    switch (wildcard) {
      case CONTAINS:
        break;
      case END_WITH:
        value = value + "$";
        break;
      case START_WITH:
        value = "^" + value;
        break;
      default:
    }
    return value;

  }

}

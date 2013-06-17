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

import com.mongodb.QueryOperators;

import fr.cnes.sitools.dataset.model.Operator;

/**
 * Utility class to get Query operator String for MongoDB
 * 
 * 
 * @author m.gond
 */
public final class OperatorMongoDB {

  /**
   * Default private constructor
   */
  private OperatorMongoDB() {

  }

  /**
   * Get the operator value for a MongoDB query
   * 
   * @param operator
   *          the {@link Operator}
   * @return the string representation of this operator for MongoDB
   */
  public static String getOperatorValue(Operator operator) {
    String value;
    switch (operator) {
      case EQ:
        value = null;
        break;
      case GT:
        value = QueryOperators.GT;
        break;
      case GTE:
        value = QueryOperators.GTE;
        break;
      case IN:
        value = QueryOperators.IN;
        break;
      case LIKE:
        value = "LIKE";
        break;
      case LT:
        value = QueryOperators.LT;
        break;
      case LTE:
        value = QueryOperators.LTE;
        break;
      case NOTIN:
        value = QueryOperators.NIN;
        break;
      case GEO_OVERLAP:
        value = "$within";
        break;
      case NE:
        value = QueryOperators.NE;
        break;
      default:
        value = null;
    }
    return value;

  }

}

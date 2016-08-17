     /*******************************************************************************
 * Copyright 2010-2016 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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
package fr.cnes.sitools.dataset.database.jdbc;

import fr.cnes.sitools.dataset.model.Operator;

/**
 * Utility class to get Query operator String for SQL
 * 
 * 
 * @author m.gond
 */
public final class OperatorSQL {

  /**
   * Default private constructor
   */
  private OperatorSQL() {

  }

  /**
   * Get the operator value for a SQL query
   * 
   * @param operator
   *          the {@link Operator}
   * @return the string representation of this operator for SQL
   */
  public static String getOperatorValue(Operator operator) {
    String value;
    switch (operator) {
      case EQ:
        value = " = ";
        break;
      case GT:
        value = ">";
        break;
      case GTE:
        value = ">=";
        break;
      case IN:
        value = " IN ";
        break;
      case LIKE:
        value = " like ";
        break;
      case LT:
        value = "<";
        break;
      case LTE:
        value = "<=";
        break;
      case NOTIN:
        value = " NOT IN ";
        break;
      case GEO_OVERLAP:
        value = " && ";
        break;
      case NE:
        value = " != ";
        break;
      default:
        value = " = ";
    }
    return value;

  }
}

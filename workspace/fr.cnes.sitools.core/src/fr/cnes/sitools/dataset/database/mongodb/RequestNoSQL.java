/*******************************************************************************
 * Copyright 2011 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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

import java.util.List;

import fr.cnes.sitools.dataset.model.Column;
import fr.cnes.sitools.dataset.model.DataSet;
import fr.cnes.sitools.dataset.model.Predicat;

/**
 * Interface for NoSQL Request A voir si c'est vraiment utile, et si c'est assez générique pour d'autres types de base
 * NoSQL => Certainement pas
 * 
 * @author m.gond
 */
public interface RequestNoSQL {

  /**
   * Get the attributes of the request
   * 
   * @param columnVisible
   *          list of visible columns
   * @return attributes
   */
  String getAttributes(List<Column> columnVisible);

  /**
   * Convert a predicate to a SQL filter
   * 
   * @param predicat
   *          the predicate
   * @return the filter
   */
  String getFilter(Predicat predicat);

  /**
   * Get the order by defined
   * 
   * @param ds
   *          the primary keys defined in the request
   * @return the ORDER BY clause
   */
  String getOrderBy(DataSet ds);

  /**
   * Get the WHERE clause
   * 
   * @param predicats
   *          a list of predicates
   * @param columns
   *          a list of columns
   * @return the WHERE clause
   */
  String getFilterClause(List<Predicat> predicats, List<Column> columns);

  

}

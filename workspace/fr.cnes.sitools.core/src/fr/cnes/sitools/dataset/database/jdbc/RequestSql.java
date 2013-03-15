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
package fr.cnes.sitools.dataset.database.jdbc;

import java.util.List;

import fr.cnes.sitools.dataset.model.Column;
import fr.cnes.sitools.dataset.model.DataSet;
import fr.cnes.sitools.dataset.model.Predicat;
import fr.cnes.sitools.dataset.model.structure.SitoolsStructure;
import fr.cnes.sitools.dataset.model.structure.StructureNodeComplete;
import fr.cnes.sitools.dataset.model.structure.TypeJointure;

/**
 * SQL interface to insure basic methods
 * 
 * @author AKKA Technologies
 * 
 */
public interface RequestSql {

  /**
   * Get the attributes of the request
   * 
   * @param columnVisible
   *          list of visible columns
   * @return attributes
   */
  String getAttributes(List<Column> columnVisible);

  /**
   * Get from clause
   * 
   * @param structures
   *          a list of structures
   * @return the clause
   */
//  String getFromClause(List<Structure> structures);

  /**
   * Get from clause
   * 
   * @param structure
   *          a SitoolsStructure
   * @return the clause
   */
  String getFromClauseAdvanced(SitoolsStructure structure);
  
  /**
   * Get the WHERE clause
   * 
   * @param predicats
   *          a list of predicates
   * @param columns
   *          a list of columns
   * @return the WHERE clause
   */
  String getWhereClause(List<Predicat> predicats, List<Column> columns);

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
   * @param ds the primary keys defined in the request
   * @return the ORDER BY clause
   */
  String getOrderBy(DataSet ds);

  /**
   * Built the left part of the filter SQL
   * 
   * @param attribute
   *          the column
   * @return the left part of SQL filter
   */
  String convertColumnToStringAttribute(Column attribute);
  
  /**
   * Built the left part of the filter SQL
   * 
   * @param attribute
   *          the column
   * @return the left part of SQL filter
   */
  String convertColumnToString(Column attribute);
  
  /**
   * Transform a join type to string
   * @param typeJointure the join type
   * @return the corresponding string for the implemented request type
   */
  String typeJointureToString(TypeJointure typeJointure);
  
  /**
   * Get the node from the node list, the level and the closing parenthesis
   * @param nodeList the list of nodes
   * @param level the level
   * @param parentheseFermante the closing parenthesis
   * @return The node
   */
  String getNodeFrom(List<StructureNodeComplete> nodeList, int level, String parentheseFermante);
  
}

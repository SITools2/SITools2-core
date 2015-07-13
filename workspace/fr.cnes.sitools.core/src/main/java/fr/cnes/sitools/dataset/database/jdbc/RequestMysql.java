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
package fr.cnes.sitools.dataset.database.jdbc;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.restlet.Context;

import fr.cnes.sitools.dataset.model.Column;
import fr.cnes.sitools.dataset.model.DataSet;
import fr.cnes.sitools.dataset.model.Multisort;
import fr.cnes.sitools.dataset.model.Operator;
import fr.cnes.sitools.dataset.model.Predicat;
import fr.cnes.sitools.dataset.model.Sort;
import fr.cnes.sitools.dataset.model.SpecificColumnType;
import fr.cnes.sitools.dataset.model.structure.SitoolsStructure;
import fr.cnes.sitools.dataset.model.structure.StructureNodeComplete;
import fr.cnes.sitools.dataset.model.structure.TypeJointure;
import fr.cnes.sitools.util.DateUtils;

/**
 * MySQL request
 * 
 * @author AKKA
 * 
 */
public final class RequestMysql implements RequestSql {
  /** Resource logger Context / Application ? ok */
  private Logger logger = Context.getCurrentLogger();

  /**
   * Get the attributes of the request
   * 
   * @param columnVisible
   *          list of visible columns
   * @return attributes
   */
  public String getAttributes(List<Column> columnVisible) {
    // changement du columnModel
    String attributes = "";
    String glue = "";
    for (Column column : columnVisible) {
      if (column.getSpecificColumnType() != null
          && (column.getSpecificColumnType().equals(SpecificColumnType.DATABASE) || column.getSpecificColumnType()
              .equals(SpecificColumnType.SQL))) {
        attributes += glue + convertColumnToStringAttribute(column);
        glue = ", ";

      }
    }
    return attributes;
  }

  /**
   * Get from clause
   * 
   * @param structure
   *          the sitoolsStructure
   * @return the clause
   */
  public String getFromClauseAdvanced(SitoolsStructure structure) {
    String clause = "";
    int level = 0;

    clause += structure.getMainTable().getName() + " ";
    if (structure.getMainTable().getAlias() != null && !"".equals(structure.getMainTable().getAlias())) {
      clause += structure.getMainTable().getAlias() + " ";
    }
    clause += getNodeFrom(structure.getNodeList(), level, "");

    return clause;
  }

  public String getNodeFrom(List<StructureNodeComplete> nodeList, int level, String parentheseFermante) {
    // First loop to get all tables node
    String clause = "";
    String parentheseAFermer = "";
    if (nodeList == null) {
      return clause;
    }
    for (StructureNodeComplete snc : nodeList) {
      if (StructureNodeComplete.TABLE_NODE.equals(snc.getType())) {

        clause += typeJointureToString(snc.getTypeJointure()) + " ";

        clause += snc.getTable().getName() + " ";
        if (snc.getTable().getAlias() != null && !"".equals(snc.getTable().getAlias())) {
          clause += snc.getTable().getAlias() + " ";
        }

        if (snc.getChildren() != null) {
          clause += getNodeFrom(snc.getChildren(), level + 1, parentheseAFermer);
        }
      }
      if (StructureNodeComplete.JOIN_CONDITION_NODE.equals(snc.getType())) {
        Predicat predicat = snc.getPredicat();
        clause += getFilter(predicat) + " ";

      }
      clause += parentheseFermante;

    }
    // if (depth <= 1 && parentheseFermante != null) {
    // clause += parentheseFermante;
    // }
    // Second loop to get all join Nodes
    // for (StructureNodeComplete snc : nodeList) {
    // if (StructureNodeComplete.JOIN_CONDITION_NODE.equals(snc.getType())) {
    // Predicat predicat = snc.getPredicat();
    // clause += getFilter(predicat) + " ";
    //
    // }
    // }

    return clause;
  }

  public String typeJointureToString(TypeJointure typeJointure) {
    String result = null;
    switch (typeJointure) {
      case CROSS_JOIN:
        result = "CROSS JOIN";
        break;
      case LEFT_JOIN:
        result = "LEFT JOIN";
        break;
      case RIGHT_JOIN:
        result = "RIGHT JOIN";
        break;
      case INNER_JOIN:
        result = "INNER JOIN";
        break;
      case LEFT_OUTER_JOIN:
        result = "LEFT OUTER JOIN";
        break;
      case RIGHT_OUTER_JOIN:
        result = "RIGHT OUTER JOIN";
        break;
      default:
        result = "INNER JOIN";
        break;
    }
    return result;
  }

  /**
   * Get the WHERE clause
   * 
   * @param predicats
   *          a list of predicates
   * @param columns
   *          a list of columns
   * @return the WHERE clause
   */
  public String getWhereClause(List<Predicat> predicats, List<Column> columns) {
    String whereClause = "";
    for (Predicat predicat : predicats) {
      whereClause += getFilter(predicat) + " ";
    }
    return whereClause;
  }

  /**
   * Convert a predicate to a SQL filter
   * 
   * @param predicat
   *          the predicate
   * @return the filter
   */
  public String getFilter(Predicat predicat) {
    String pred = "";
    if (predicat.getStringDefinition() != null && !"".equals(predicat.getStringDefinition())) {
      return predicat.getStringDefinition();
    }
    Column attribute = predicat.getLeftAttribute();
    // ajouter mon operator OR AND
    pred += (predicat.getLogicOperator() == null ? " AND " : predicat.getLogicOperator()) + " ";
    // recuperer la parenthese
    if (predicat.getOpenParenthesis() != null) {
      pred += predicat.getOpenParenthesis();
    }

    // calculer le predicat
    if (predicat.getLeftString() != null && !"".equals(predicat.getLeftString())) {
      pred += predicat.getLeftString();
    }
    else {
      pred += convertColumnToString(attribute);
    }

    pred += OperatorSQL.getOperatorValue(predicat.getCompareOperator());
    // jointure ou filtre
    if (predicat.getRightValue() != null) {
      // filtre
      if (predicat.getRightValue() instanceof String) {
        String value = ((String) predicat.getRightValue()).replace("\"", "\'").replace("*", "%");

        if (predicat.getWildcard() != null) {
          value = WildcardSQL.applyWildcard(predicat.getWildcard(), value);
        }

        pred += value;

      }
      else if (predicat.getRightValue() instanceof Boolean) {
        pred += predicat.getRightValue().toString();
      }
      else if (predicat.getRightValue() instanceof Double) {
        pred += predicat.getRightValue().toString();
      }
      else if (predicat.getRightValue() instanceof Float) {
        pred += predicat.getRightValue().toString();
      }
      else if (predicat.getRightValue() instanceof BigDecimal) {
        pred += ((BigDecimal) predicat.getRightValue()).toPlainString();
      }
      else if (predicat.getRightValue() instanceof List) {
        @SuppressWarnings("unchecked")
        List<String> values = (List<String>) predicat.getRightValue();
        String in = "";
        String glue = "";
        for (String value : values) {
          in += glue + "'" + value + "'";
          glue = ", ";
        }
        pred += "(" + in + ")";
      }
      else if (predicat.getRightValue() instanceof Date) {
        Date date = (Date) predicat.getRightValue();
        pred += "'" + DateUtils.format(date) + "'";
      }
      else {
        pred += ((String) predicat.getRightValue()).replace("\"", "\'").replace("*", "%");
      }
    }
    else if (predicat.getRightAttribute() != null) {
      pred += convertColumnToString(predicat.getRightAttribute());
    }
    else {
      // if the predicate is not created, we return "" to skip the predicate
      logger.log(Level.INFO, "Predicat :'" + pred + "' was skipped because value is null");
      return "";
    }
    // recuperer la parenthese fermante
    if (predicat.getClosedParenthesis() != null) {
      pred += predicat.getClosedParenthesis();
    }
    return pred;
  }

  /**
   * convert a operator to a SQL operator
   * 
   * @param compareOperator
   *          the operator
   * @return the SQL operator
   */
  public String convertOperator(String compareOperator) {
    for (Operator operator : Operator.values()) {
      if (operator.name().equalsIgnoreCase(compareOperator)) {
        return OperatorSQL.getOperatorValue(operator);
      }
    }
    return null;
  }

  /**
   * Built the left part of the filter SQL
   * 
   * @param attribute
   *          the column
   * @return the left part of SQL filter
   */
  public String convertColumnToStringAttribute(Column attribute) {
    String result = convertColumnToString(attribute);

    if (attribute.getColumnAlias() != null && !"".equals(attribute.getColumnAlias())) {
      result += " as " + attribute.getColumnAlias();
    }
    return result;

  }

  /**
   * Get the SQL string corresponding to a column
   * 
   * @param attribute
   *          the column to convert
   * @return the column convert to string
   */
  public String convertColumnToString(Column attribute) {
    String result = "";
    if (attribute.getSpecificColumnType() == SpecificColumnType.DATABASE) {
      result = (attribute.getTableAlias() != null && !"".equals(attribute.getTableAlias()) ? attribute.getTableAlias()
          : attribute.getTableName()) + "." + attribute.getDataIndex();
    }
    if (attribute.getSpecificColumnType() == SpecificColumnType.SQL) {
      result = attribute.getDataIndex();
    }
    return result;

  }

  public String getOrderBy(DataSet ds) {
    String orderBy = "";
    List<Column> columnsOrderBy = ds.getColumnOrderBy();
    if (columnsOrderBy != null && columnsOrderBy.size() != 0) {
      orderBy = " ORDER BY ";
    }
    String glue = "";
    for (Column column : columnsOrderBy) {
      orderBy += glue + convertColumnToString(column) + " " + column.getOrderBy();
      glue = ", ";
    }
    return orderBy;
  }

  public String getOrderBy(Multisort orders, List<Column> columns) {
    String result = "";
    // if the order list is empty, there are no order by to add
    if (orders == null || orders.getOrdersList() == null || orders.getOrdersList().length == 0) {
      return "";
    }

    result = " ORDER BY ";
    String glue = "";
    for (int i = 0; i < orders.getOrdersList().length; i++) {
      Sort order = orders.getOrdersList()[i];
      Column column = getColumnFromAlias(order.getField(), columns);
      result += glue + " " + convertColumnToString(column) + " " + order.getDirection().name();
      glue = ", ";
    }
    return result;
  }

  private Column getColumnFromAlias(String field, List<Column> columns) {
    Column col = null;
    for (Column column : columns) {
      if (column.getColumnAlias().equals(field)) {
        col = column;
        break;
      }
    }
    return col;
  }
}

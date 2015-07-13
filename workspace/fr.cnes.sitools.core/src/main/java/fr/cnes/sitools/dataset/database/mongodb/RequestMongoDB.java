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
package fr.cnes.sitools.dataset.database.mongodb;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;

import fr.cnes.sitools.dataset.model.Column;
import fr.cnes.sitools.dataset.model.DataSet;
import fr.cnes.sitools.dataset.model.Multisort;
import fr.cnes.sitools.dataset.model.Operator;
import fr.cnes.sitools.dataset.model.Predicat;
import fr.cnes.sitools.dataset.model.Sort;
import fr.cnes.sitools.dataset.model.Sort.SortOrder;
import fr.cnes.sitools.dataset.model.geometry.GeometryObject;
import fr.cnes.sitools.dataset.model.geometry.Point;
import fr.cnes.sitools.util.DateUtils;

/**
 * MongoDB Request
 * 
 * 
 * @author m.gond
 */
public class RequestMongoDB implements RequestNoSQL {
  /** Constant _id column name */
  private static final String ID_COLUMN_NAME = "_id";

  public String getAttributes(List<Column> columnVisible) {
    BasicDBObject keys = new BasicDBObject();
    boolean foundId = false;
    for (Column column : columnVisible) {
      if (ID_COLUMN_NAME.equals(column.getDataIndex())) {
        foundId = true;
      }
      keys.append(column.getDataIndex(), 1);
    }
    if (!foundId) {
      keys.append(ID_COLUMN_NAME, 0);
    }

    return keys.toString();
  }

  public String getFilterClause(List<Predicat> predicats, List<Column> columns) {
    BasicDBObject whereClause = new BasicDBObject();
    // loop over the predicats
    // boolean first = true;
    // String result = "{";
    //
    // Map<String, List<Predicat>> orderedPredicats = orderPredicat(predicats);
    // for (Predicat predicat : predicats) {
    //
    // String filter = getFilter(predicat);
    // if (filter != null && !"".equals(filter)) {
    // // DBObject objPredicat = (DBObject) JSON.parse(filter);
    // // if (objPredicat != null) {
    // if (first) {
    // whereClause.append("$and", new ArrayList<DBObject>());
    // }
    // else {
    // result += ",";
    // }
    // first = false;
    //
    // result += filter;
    //
    // // ((List<DBObject>) whereClause.get("$and")).add(objPredicat);
    //
    // // if (whereClause.containsField(key)) {
    // // // If the key already exists append the value to the existing key
    // // // DBObject obj = new BasicDBObject();
    // // // obj.put("$and", objPredicat.get(key));
    // //
    // // if (!whereClause.containsField("$and")) {
    // // whereClause.append("$and", new ArrayList<DBObject>());
    // // DBObject pred = (DBObject) whereClause.get(key);
    // // whereClause.remove(key);
    // // ((List<DBObject>) whereClause.get("$and")).add(pred);
    // //
    // // }
    // // ((List<DBObject>) whereClause.get("$and")).add(objPredicat);
    // //
    // // // ((DBObject) whereClause.get(key)).putAll(obj);
    // // }
    // // else {
    // // // if the key doesn't exists just append the predicat to the whereClause
    // // whereClause.append(key, objPredicat.get(key));
    // // }
    //
    // }
    // // }
    // }

    for (Predicat predicat : predicats) {
      String filter = getFilter(predicat);
      if (filter != null && !"".equals(filter)) {
        DBObject objPredicat = (DBObject) JSON.parse(filter);
        if (objPredicat != null) {
          Set<String> keys = objPredicat.keySet();
          for (String key : keys) {
            if (whereClause.containsField(key)) {
              ((DBObject) whereClause.get(key)).putAll((DBObject) objPredicat.get(key));
            }
            else {
              whereClause.append(key, objPredicat.get(key));
            }

          }
        }
      }
    }

    return whereClause.toString();
  }

  public String getFilter(Predicat predicat) {
    String jsonPredicat = "";
    if (predicat.getStringDefinition() != null && !"".equals(predicat.getStringDefinition())) {
      return predicat.getStringDefinition();
    }
    BasicDBObject object = null;
    Object value = null;

    if (predicat.getRightValue() != null) {
      // filtre
      if (predicat.getRightValue() instanceof String) {
        // rightValue = ((String) predicat.getRightValue()).replace("'", "");
        value = getValueFromColumnType(predicat.getRightValue(), predicat);

      }
      else if (predicat.getRightValue() instanceof Boolean) {
        value = predicat.getRightValue();
      }
      else if (predicat.getRightValue() instanceof Double) {
        value = predicat.getRightValue();
      }
      else if (predicat.getRightValue() instanceof Float) {
        value = predicat.getRightValue();
      }
      else if (predicat.getRightValue() instanceof BigDecimal) {
        value = ((BigDecimal) predicat.getRightValue()).doubleValue();
      }
      // List of values
      else if (predicat.getRightValue() instanceof List) {
        @SuppressWarnings("unchecked")
        List<String> values = (List<String>) predicat.getRightValue();
        List<Object> tmpValue = new ArrayList<Object>();
        for (String val : values) {
          tmpValue.add(getValueFromColumnType(val, predicat));
        }
        value = tmpValue;
      }
      // Geometry object
      else if (predicat.getRightValue() instanceof GeometryObject) {
        GeometryObject geom = (GeometryObject) predicat.getRightValue();
        List<List<Double>> points = new ArrayList<List<Double>>();
        for (Point point : geom.getPoints()) {
          List<Double> pointArray = new ArrayList<Double>();
          pointArray.add(point.getX());
          pointArray.add(point.getY());
          points.add(pointArray);
        }
        value = new BasicDBObject("$polygon", points);
      }
      else {
        value = predicat.getRightValue();
      }

      // get the compare operator and create a DBObject if it needs it
      String operator = OperatorMongoDB.getOperatorValue(predicat.getCompareOperator());
      if (Operator.LIKE.equals(predicat.getCompareOperator())) {
        value = Pattern.compile((String) value, Pattern.CASE_INSENSITIVE);
      }
      else if (operator != null) {
        value = new BasicDBObject(operator, value);
      }
      if (predicat.getLeftString() != null) {
        object = new BasicDBObject(predicat.getLeftString(), value);

      }
      else {
        object = new BasicDBObject(predicat.getLeftAttribute().getDataIndex(), value);
      }

    }

    if (object != null) {
      jsonPredicat = object.toString();
    }

    return jsonPredicat;
  }

  /**
   * Get the value of an Object from the column type of the leftAttribute of the {@link Predicat}
   * 
   * @param value
   *          the Object
   * @param predicat
   *          the {@link Predicat}
   * @return the value with the Real type of the given Object
   */
  private Object getValueFromColumnType(Object value, Predicat predicat) {

    try {
      value = ((String) value).replace("'", "");
      if (String.class.getSimpleName().equals(predicat.getLeftAttribute().getSqlColumnType())) {
        if (predicat.getWildcard() != null) {
          value = WildcardMongoDB.applyWildcard(predicat.getWildcard(), (String) value);
        }
      }
      else if (Date.class.getSimpleName().equals(predicat.getLeftAttribute().getSqlColumnType())) {
        value = DateUtils.parse(value.toString());
      }
      else if (Double.class.getSimpleName().equals(predicat.getLeftAttribute().getSqlColumnType())) {
        value = Double.parseDouble(value.toString());
      }
      else if (Float.class.getSimpleName().equals(predicat.getLeftAttribute().getSqlColumnType())) {
        value = Float.parseFloat(value.toString());
      }
      else if (Integer.class.getSimpleName().equals(predicat.getLeftAttribute().getSqlColumnType())) {
        value = Integer.parseInt(value.toString());
      }
      else if (Boolean.class.getSimpleName().equals(predicat.getLeftAttribute().getSqlColumnType())) {
        value = Boolean.parseBoolean(value.toString());
      }

      else if (ObjectId.class.getSimpleName().equals(predicat.getLeftAttribute().getSqlColumnType())) {
        value = new ObjectId(value.toString());
      }
    }
    catch (NumberFormatException e) {
      return value;
    }
    catch (ParseException e) {
      return value;
    }

    return value;
  }

  public String getOrderBy(DataSet ds) {
    BasicDBObject orderBy = new BasicDBObject();
    List<Column> columnsOrderBy = ds.getColumnOrderBy();
    if (columnsOrderBy != null && columnsOrderBy.size() != 0) {
      for (Column column : columnsOrderBy) {
        int orderDir;
        if ("ASC".equals(column.getOrderBy())) {
          orderDir = 1;
        }
        else {
          orderDir = -1;
        }
        orderBy.append(column.getDataIndex(), orderDir);
      }
    }
    return orderBy.toString();
  }

  /**
   * Get the Sorting Order for the given {@link Multisort} as a String
   * 
   * @param multisort
   *          the sort order definition
   * @return the Sorting order as a String
   */
  public String getOrderBy(Multisort multisort) {
    BasicDBObject orderBy = new BasicDBObject();
    Sort[] sorts = multisort.getOrdersList();
    if (sorts != null && sorts.length != 0) {
      for (int i = 0; i < sorts.length; i++) {
        Sort sort = sorts[i];
        int orderDir;
        if (SortOrder.ASC.equals(sort.getDirection())) {
          orderDir = 1;
        }
        else {
          orderDir = -1;
        }
        orderBy.append(sort.getField(), orderDir);
      }

    }
    return orderBy.toString();
  }

}

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
package fr.cnes.sitools.utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.bson.types.ObjectId;

import fr.cnes.sitools.common.model.Resource;
import fr.cnes.sitools.dataset.model.Column;
import fr.cnes.sitools.dataset.model.ColumnRenderer;
import fr.cnes.sitools.dataset.model.BehaviorEnum;
import fr.cnes.sitools.dataset.model.DataSet;
import fr.cnes.sitools.dataset.model.Operator;
import fr.cnes.sitools.dataset.model.Predicat;
import fr.cnes.sitools.dataset.model.SpecificColumnType;
import fr.cnes.sitools.dataset.model.structure.SitoolsStructure;
import fr.cnes.sitools.dataset.model.structure.StructureNodeComplete;
import fr.cnes.sitools.dataset.model.structure.TypeJointure;
import fr.cnes.sitools.datasource.jdbc.model.Structure;
import fr.cnes.sitools.datasource.jdbc.model.Table;
import fr.cnes.sitools.units.dimension.model.SitoolsUnit;

/**
 * Util class for DataSet model objet creation
 * 
 * 
 * @author m.gond
 */
public class CreateDatasetUtil {

  /**
   * Get the definition of the datasource on MySQL database
   * 
   * @return a Resource with the datasource definition on MySQL database
   */
  public static Resource getDatasourceMySQL() {
    Resource datasource = new Resource();
    datasource.setId("3153200a-b9eb-400e-ba8b-bc6ec9c68d90");
    datasource.setType("datasource");
    datasource.setMediaType("datasource");
    return datasource;
  }

  /**
   * Get the definition of the datasource on Postgresql database
   * 
   * @return a Resource with the datasource definition on Postgresql database
   */
  public static Resource getDatasourcePostgresql() {
    Resource datasource = new Resource();
    datasource.setId("8e00fe38-6f95-4338-a81c-ed2ab1db9340");
    datasource.setType("datasource");
    datasource.setMediaType("datasource");
    return datasource;
  }

  /**
   * Get the definition of the datasource on MySQL database
   * 
   * @return a Resource with the datasource definition on MongoDB database
   */
  public static Resource getDatasourceMongoDB() {
    Resource datasource = new Resource();
    datasource.setId("ac5c5a8b-0edc-4e5d-83ac-d8476a5e69cb");
    datasource.setType("datasource");
    datasource.setMediaType("datasource/mongodb");
    return datasource;
  }

  /**
   * Create Dataset for Postgresql datasource. This dataset is created on the test.table_tests table
   * 
   * @param id
   *          the id of the dataset
   * @param primaryKey
   *          the primary key needed
   * @param withDatasetUrl
   *          true if datasetRequestUrl should be included, false otherwise
   * @param urlAttachment
   *          the url attachment for the dataset
   * @return the DataSet created
   * 
   */
  public static DataSet createDatasetTestPG(String id, String primaryKey, boolean withDatasetUrl, String urlAttachment) {
    DataSet item = new DataSet();
    item.setId(id);
    item.setName("Dataset_primary_key");
    item.setDescription("Dataset_primary_key_description");

    Structure a = new Structure("", "table_tests");
    a.setSchemaName("test");
    ArrayList<Structure> aliases = new ArrayList<Structure>();
    aliases.add(a);
    item.setStructures(aliases);

    SitoolsStructure ss = new SitoolsStructure();
    Table t = new Table("table_tests", "test");
    ss.setMainTable(t);
    item.setStructure(ss);

    ArrayList<Column> columns = new ArrayList<Column>();
    Column c1 = new Column("1", "int", "1", 1, true, true, "int4");
    c1.setColumnAlias("int");
    c1.setSchema("test");
    c1.setSpecificColumnType(SpecificColumnType.DATABASE);
    c1.setTableName("table_tests");
    c1.setFilter(true);
    c1.setPrimaryKey(c1.getColumnAlias().equals(primaryKey));
    c1.setOrderBy(c1.getColumnAlias().equals(primaryKey) ? "ASC" : null);

    Column c2 = new Column("2", "float", "2", 2, true, true, "float4");
    c2.setColumnAlias("float");
    c2.setSchema("test");
    c2.setSpecificColumnType(SpecificColumnType.DATABASE);
    c2.setTableName("table_tests");
    c2.setFilter(true);
    c2.setPrimaryKey(c2.getColumnAlias().equals(primaryKey));
    c2.setOrderBy(c2.getColumnAlias().equals(primaryKey) ? "ASC" : null);

    Column c3 = new Column("3", "double", "3", 3, true, true, "float8");
    c3.setColumnAlias("double");
    c3.setSchema("test");
    c3.setSpecificColumnType(SpecificColumnType.DATABASE);
    c3.setTableName("table_tests");
    c3.setFilter(true);
    c3.setPrimaryKey(c3.getColumnAlias().equals(primaryKey));
    c3.setOrderBy(c3.getColumnAlias().equals(primaryKey) ? "ASC" : null);

    Column c4 = new Column("4", "varchar", "4", 4, true, true, "varchar");
    c4.setColumnAlias("varchar");
    c4.setSchema("test");
    c4.setSpecificColumnType(SpecificColumnType.DATABASE);
    c4.setTableName("table_tests");
    c4.setFilter(true);
    c4.setPrimaryKey(c4.getColumnAlias().equals(primaryKey));
    c4.setOrderBy(c4.getColumnAlias().equals(primaryKey) ? "ASC" : null);

    Column c5 = new Column("5", "varchar", "5", 5, true, true, "varchar");
    c5.setColumnAlias("varchar_id");
    c5.setSchema("test");
    c5.setSpecificColumnType(SpecificColumnType.DATABASE);
    c5.setTableName("table_tests");
    c5.setFilter(true);
    c5.setPrimaryKey(c5.getColumnAlias().equals(primaryKey));
    c5.setOrderBy(c5.getColumnAlias().equals(primaryKey) ? "ASC" : null);

    Column c6 = new Column("6", "timestamp", "6", 6, true, true, "timestamp");
    c6.setColumnAlias("timestamp");
    c6.setSchema("test");
    c6.setSpecificColumnType(SpecificColumnType.DATABASE);
    c6.setTableName("table_tests");
    c6.setFilter(true);
    c6.setPrimaryKey(c6.getColumnAlias().equals(primaryKey));
    c6.setOrderBy(c6.getColumnAlias().equals(primaryKey) ? "ASC" : null);

    Column c7 = new Column("7", "date", "7", 7, true, true, "date");
    c7.setColumnAlias("date");
    c7.setSchema("test");
    c7.setSpecificColumnType(SpecificColumnType.DATABASE);
    c7.setTableName("table_tests");
    c7.setFilter(true);
    c7.setPrimaryKey(c7.getColumnAlias().equals(primaryKey));
    c7.setOrderBy(c7.getColumnAlias().equals(primaryKey) ? "ASC" : null);

    Column c9 = new Column("9", "smallint", "9", 9, true, true, "int2");
    c9.setColumnAlias("smallint");
    c9.setSchema("test");
    c9.setSpecificColumnType(SpecificColumnType.DATABASE);
    c9.setTableName("table_tests");
    c9.setFilter(true);
    c9.setPrimaryKey(c9.getColumnAlias().equals(primaryKey));
    c9.setOrderBy(c9.getColumnAlias().equals(primaryKey) ? "ASC" : null);

    Column c10 = new Column("10", "timestamp_with_time_zone", "10", 10, true, true, "timestamptz");
    c10.setColumnAlias("timestamp_with_time_zone");
    c10.setSchema("test");
    c10.setSpecificColumnType(SpecificColumnType.DATABASE);
    c10.setTableName("table_tests");
    c10.setFilter(true);
    c10.setPrimaryKey(c10.getColumnAlias().equals(primaryKey));
    c10.setOrderBy(c10.getColumnAlias().equals(primaryKey) ? "ASC" : null);

    Column c11 = new Column("11", "bigint", "11", 11, true, true, "int8");
    c11.setColumnAlias("bigint");
    c11.setSchema("test");
    c11.setSpecificColumnType(SpecificColumnType.DATABASE);
    c11.setTableName("table_tests");
    c11.setFilter(true);
    c11.setPrimaryKey(c11.getColumnAlias().equals(primaryKey));
    c11.setOrderBy(c11.getColumnAlias().equals(primaryKey) ? "ASC" : null);

    Column c12 = new Column("12", "numeric", "12", 12, true, true, "numeric");
    c12.setColumnAlias("numeric");
    c12.setSchema("test");
    c12.setSpecificColumnType(SpecificColumnType.DATABASE);
    c12.setTableName("table_tests");
    c12.setFilter(true);
    c12.setPrimaryKey(c12.getColumnAlias().equals(primaryKey));
    c12.setOrderBy(c12.getColumnAlias().equals(primaryKey) ? "ASC" : null);

    Column c13 = new Column("13", "char10", "13", 13, true, true, "bpchar");
    c13.setColumnAlias("char10");
    c13.setSchema("test");
    c13.setSpecificColumnType(SpecificColumnType.DATABASE);
    c13.setTableName("table_tests");
    c13.setFilter(true);
    c13.setPrimaryKey(c13.getColumnAlias().equals(primaryKey));
    c13.setOrderBy(c13.getColumnAlias().equals(primaryKey) ? "ASC" : null);

    Column c14 = new Column("14", "serial", "14", 14, true, true, "serial");
    c14.setColumnAlias("serial");
    c14.setSchema("test");
    c14.setSpecificColumnType(SpecificColumnType.DATABASE);
    c14.setTableName("table_tests");
    c14.setFilter(true);
    c14.setPrimaryKey(c14.getColumnAlias().equals(primaryKey));
    c14.setOrderBy(c14.getColumnAlias().equals(primaryKey) ? "ASC" : null);

    Column c15 = new Column("15", "bigserial", "15", 15, true, true, "bigserial");
    c15.setColumnAlias("bigserial");
    c15.setSchema("test");
    c15.setSpecificColumnType(SpecificColumnType.DATABASE);
    c15.setTableName("table_tests");
    c15.setFilter(true);
    c15.setPrimaryKey(c15.getColumnAlias().equals(primaryKey));
    c15.setOrderBy(c15.getColumnAlias().equals(primaryKey) ? "ASC" : null);

    Column c16 = new Column("16", "time_without_time_zone", "16", 16, true, true, "time");
    c16.setColumnAlias("time_without_time_zone");
    c16.setSchema("test");
    c16.setSpecificColumnType(SpecificColumnType.DATABASE);
    c16.setTableName("table_tests");
    c16.setFilter(true);
    c16.setPrimaryKey(c16.getColumnAlias().equals(primaryKey));
    c16.setOrderBy(c16.getColumnAlias().equals(primaryKey) ? "ASC" : null);

    columns.add(c1);
    columns.add(c2);
    columns.add(c3);
    columns.add(c4);
    columns.add(c5);
    columns.add(c6);
    columns.add(c7);
    columns.add(c9);
    columns.add(c10);
    columns.add(c11);
    columns.add(c12);
    columns.add(c13);
    columns.add(c14);
    columns.add(c15);
    columns.add(c16);

    if (withDatasetUrl) {
      for (Iterator<Column> iterator = columns.iterator(); iterator.hasNext();) {
        Column column = iterator.next();
        ColumnRenderer colRenderer = new ColumnRenderer();
        colRenderer.setBehavior(BehaviorEnum.datasetLink);
        colRenderer.setColumnAlias(column.getColumnAlias());
        colRenderer.setDatasetLinkUrl(urlAttachment);
        column.setColumnRenderer(colRenderer);
      }
    }

    item.setColumnModel(columns);

    item.setSitoolsAttachementForUsers(urlAttachment);

    item.setQueryType("W");
    Resource datasource = getDatasourcePostgresql();
    item.setDatasource(datasource);
    item.setDirty(false);

    return item;
  }

  /**
   * Create Dataset for MySQL datasource. This dataset is created on the TABLE_TESTS table
   * 
   * @param id
   *          the id of the dataset
   * @param primaryKey
   *          the primary key needed
   * @param withDatasetUrl
   *          true if datasetRequestUrl should be included, false otherwise
   * @param urlAttachment
   *          the url attachment for the dataset
   * @return the DataSet created
   * 
   */
  public static DataSet createDatasetTestMySQL(String id, String primaryKey, boolean withDatasetUrl,
      String urlAttachment) {
    DataSet item = new DataSet();
    item.setId(id);
    item.setName("Dataset_primary_key_mysql");
    item.setDescription("Dataset_primary_key_description_mysql");

    Structure a = new Structure("", "TABLE_TESTS");
    ArrayList<Structure> aliases = new ArrayList<Structure>();
    aliases.add(a);
    item.setStructures(aliases);

    SitoolsStructure ss = new SitoolsStructure();
    Table t = new Table("TABLE_TESTS");
    ss.setMainTable(t);
    item.setStructure(ss);

    ArrayList<Column> columns = new ArrayList<Column>();
    Column c1 = new Column("1", "field_varchar_id", "1", 1, true, true, "VARCHAR");
    c1.setColumnAlias("field_varchar_id");
    c1.setSpecificColumnType(SpecificColumnType.DATABASE);
    c1.setTableName("TABLE_TESTS");
    c1.setFilter(true);
    c1.setPrimaryKey(c1.getColumnAlias().equals(primaryKey));
    c1.setOrderBy(c1.getColumnAlias().equals(primaryKey) ? "ASC" : null);

    Column c2 = new Column("2", "field_tiny_int", "2", 2, true, true, "TINYINT");
    c2.setColumnAlias("field_tiny_int");
    c2.setSpecificColumnType(SpecificColumnType.DATABASE);
    c2.setTableName("TABLE_TESTS");
    c2.setFilter(true);
    c2.setPrimaryKey(c2.getColumnAlias().equals(primaryKey));
    c2.setOrderBy(c2.getColumnAlias().equals(primaryKey) ? "ASC" : null);

    Column c3 = new Column("3", "field_small_int", "3", 3, true, true, "SMALLINT");
    c3.setColumnAlias("field_small_int");
    c3.setSpecificColumnType(SpecificColumnType.DATABASE);
    c3.setTableName("TABLE_TESTS");
    c3.setFilter(true);
    c3.setPrimaryKey(c3.getColumnAlias().equals(primaryKey));
    c3.setOrderBy(c3.getColumnAlias().equals(primaryKey) ? "ASC" : null);

    Column c4 = new Column("4", "field_medium_int", "4", 4, true, true, "MEDIUMINT");
    c4.setColumnAlias("field_medium_int");
    c4.setSpecificColumnType(SpecificColumnType.DATABASE);
    c4.setTableName("TABLE_TESTS");
    c4.setFilter(true);
    c4.setPrimaryKey(c4.getColumnAlias().equals(primaryKey));
    c4.setOrderBy(c4.getColumnAlias().equals(primaryKey) ? "ASC" : null);

    Column c5 = new Column("5", "field_int", "5", 5, true, true, "INT");
    c5.setColumnAlias("field_int");
    c5.setSpecificColumnType(SpecificColumnType.DATABASE);
    c5.setTableName("TABLE_TESTS");
    c5.setFilter(true);
    c5.setPrimaryKey(c5.getColumnAlias().equals(primaryKey));
    c5.setOrderBy(c5.getColumnAlias().equals(primaryKey) ? "ASC" : null);

    Column c6 = new Column("6", "field_big_int", "6", 6, true, true, "BIGINT");
    c6.setColumnAlias("field_big_int");
    c6.setSpecificColumnType(SpecificColumnType.DATABASE);
    c6.setTableName("TABLE_TESTS");
    c6.setFilter(true);
    c6.setPrimaryKey(c6.getColumnAlias().equals(primaryKey));
    c6.setOrderBy(c6.getColumnAlias().equals(primaryKey) ? "ASC" : null);

    // Column c7 = new Column("7", "field_float", "7", 7, true, true, "FLOAT");
    // c7.setColumnAlias("field_float");
    // c7.setSpecificColumnType(SpecificColumnType.DATABASE);
    // c7.setTableName("TABLE_TESTS");
    // c7.setPrimaryKey(c7.getColumnAlias().equals(primaryKey));
    //
    // Column c8 = new Column("8", "field_double", "8", 8, true, true,
    // "DOUBLE");
    // c8.setColumnAlias("field_double");
    // c8.setSpecificColumnType(SpecificColumnType.DATABASE);
    // c8.setTableName("TABLE_TESTS");
    // c8.setPrimaryKey(c8.getColumnAlias().equals(primaryKey));

    Column c9 = new Column("9", "field_decimal", "9", 9, true, true, "DECIMAL");
    c9.setColumnAlias("field_decimal");
    c9.setSpecificColumnType(SpecificColumnType.DATABASE);
    c9.setTableName("TABLE_TESTS");
    c9.setFilter(true);
    c9.setPrimaryKey(c9.getColumnAlias().equals(primaryKey));
    c9.setOrderBy(c9.getColumnAlias().equals(primaryKey) ? "ASC" : null);

    Column c10 = new Column("10", "field_varchar", "10", 10, true, true, "VARCHAR");
    c10.setColumnAlias("field_varchar");
    c10.setSpecificColumnType(SpecificColumnType.DATABASE);
    c10.setTableName("TABLE_TESTS");
    c10.setFilter(true);
    c10.setPrimaryKey(c10.getColumnAlias().equals(primaryKey));
    c10.setOrderBy(c10.getColumnAlias().equals(primaryKey) ? "ASC" : null);

    Column c11 = new Column("11", "field_timestamp", "11", 11, true, true, "TIMESTAMP");
    c11.setColumnAlias("field_timestamp");
    c11.setSpecificColumnType(SpecificColumnType.DATABASE);
    c11.setTableName("TABLE_TESTS");
    c11.setFilter(true);
    c11.setPrimaryKey(c11.getColumnAlias().equals(primaryKey));
    c11.setOrderBy(c11.getColumnAlias().equals(primaryKey) ? "ASC" : null);

    Column c12 = new Column("12", "field_datetime", "12", 12, true, true, "DATETIME");
    c12.setColumnAlias("field_datetime");
    c12.setSpecificColumnType(SpecificColumnType.DATABASE);
    c12.setTableName("TABLE_TESTS");
    c12.setFilter(true);
    c12.setPrimaryKey(c12.getColumnAlias().equals(primaryKey));
    c12.setOrderBy(c12.getColumnAlias().equals(primaryKey) ? "ASC" : null);

    Column c13 = new Column("13", "field_date", "13", 13, true, true, "DATE");
    c13.setColumnAlias("field_date");
    c13.setSpecificColumnType(SpecificColumnType.DATABASE);
    c13.setTableName("TABLE_TESTS");
    c13.setFilter(true);
    c13.setPrimaryKey(c13.getColumnAlias().equals(primaryKey));
    c13.setOrderBy(c13.getColumnAlias().equals(primaryKey) ? "ASC" : null);

    Column c14 = new Column("14", "field_year", "14", 14, true, true, "YEAR");
    c14.setColumnAlias("field_year");
    c14.setSpecificColumnType(SpecificColumnType.DATABASE);
    c14.setTableName("TABLE_TESTS");
    c14.setFilter(true);
    c14.setPrimaryKey(c14.getColumnAlias().equals(primaryKey));
    c14.setOrderBy(c14.getColumnAlias().equals(primaryKey) ? "ASC" : null);

    Column c15 = new Column("15", "field_time", "15", 15, true, true, "TIME");
    c15.setColumnAlias("field_time");
    c15.setSpecificColumnType(SpecificColumnType.DATABASE);
    c15.setTableName("TABLE_TESTS");
    c15.setFilter(true);
    c15.setPrimaryKey(c15.getColumnAlias().equals(primaryKey));
    c15.setOrderBy(c15.getColumnAlias().equals(primaryKey) ? "ASC" : null);

    Column c16 = new Column("16", "field_char", "16", 16, true, true, "CHAR");
    c16.setColumnAlias("field_char");
    c16.setSpecificColumnType(SpecificColumnType.DATABASE);
    c16.setTableName("TABLE_TESTS");
    c16.setFilter(true);
    c16.setPrimaryKey(c16.getColumnAlias().equals(primaryKey));
    c16.setOrderBy(c16.getColumnAlias().equals(primaryKey) ? "ASC" : null);

    columns.add(c1);
    columns.add(c2);
    columns.add(c3);
    columns.add(c4);
    columns.add(c5);
    columns.add(c6);
    // columns.add(c7);
    // columns.add(c8);
    columns.add(c9);
    columns.add(c10);
    columns.add(c11);
    columns.add(c12);
    columns.add(c13);
    columns.add(c14);
    columns.add(c15);
    columns.add(c16);

    if (withDatasetUrl) {
      for (Iterator<Column> iterator = columns.iterator(); iterator.hasNext();) {
        Column column = iterator.next();
        ColumnRenderer colRenderer = new ColumnRenderer();
        colRenderer.setBehavior(BehaviorEnum.datasetLink);
        colRenderer.setColumnAlias(column.getColumnAlias());
        colRenderer.setDatasetLinkUrl(urlAttachment);
        column.setColumnRenderer(colRenderer);
      }
    }

    item.setColumnModel(columns);

    item.setSitoolsAttachementForUsers(urlAttachment);

    item.setQueryType("W");
    Resource datasource = getDatasourceMySQL();
    item.setDatasource(datasource);
    item.setDirty(false);

    return item;

  }

  public static DataSet createDatasetTestMongoDB(String id, String primaryKey, boolean withDatasetUrl,
      String urlAttachment) {

    DataSet item = new DataSet();
    item.setId(id);
    item.setName("Dataset_primary_key_mongodb");
    item.setDescription("Dataset_primary_key_description");

    Structure a = new Structure("", "table_tests");
    a.setType("collection");
    ArrayList<Structure> aliases = new ArrayList<Structure>();
    aliases.add(a);
    item.setStructures(aliases);

    SitoolsStructure ss = new SitoolsStructure();
    Table t = new Table();
    t.setName("table_tests");
    ss.setMainTable(t);
    item.setStructure(ss);

    ArrayList<Column> columns = new ArrayList<Column>();
    Column c1 = new Column("1", "_id", "_id", 1, true, true, ObjectId.class.getSimpleName());
    c1.setColumnAlias("_id");
    c1.setSpecificColumnType(SpecificColumnType.DATABASE);
    c1.setTableName("table_tests");
    c1.setFilter(true);
    c1.setPrimaryKey(c1.getColumnAlias().equals(primaryKey));
    c1.setOrderBy(c1.getColumnAlias().equals(primaryKey) ? "ASC" : null);

    Column c2 = new Column("2", "int", "int", 2, true, true, Integer.class.getSimpleName());
    c2.setColumnAlias("int");
    c2.setSpecificColumnType(SpecificColumnType.DATABASE);
    c2.setTableName("table_tests");
    c2.setFilter(true);
    c2.setPrimaryKey(c2.getColumnAlias().equals(primaryKey));
    c2.setOrderBy(c2.getColumnAlias().equals(primaryKey) ? "ASC" : null);

    Column c3 = new Column("3", "float", "float", 3, true, true, Float.class.getSimpleName());
    c3.setColumnAlias("float");
    c3.setSpecificColumnType(SpecificColumnType.DATABASE);
    c3.setTableName("table_tests");
    c3.setFilter(true);
    c3.setPrimaryKey(c3.getColumnAlias().equals(primaryKey));
    c3.setOrderBy(c3.getColumnAlias().equals(primaryKey) ? "ASC" : null);

    Column c4 = new Column("4", "double", "double", 4, true, true, Double.class.getSimpleName());
    c4.setColumnAlias("double");
    c4.setSpecificColumnType(SpecificColumnType.DATABASE);
    c4.setTableName("table_tests");
    c4.setFilter(true);
    c4.setPrimaryKey(c4.getColumnAlias().equals(primaryKey));
    c4.setOrderBy(c4.getColumnAlias().equals(primaryKey) ? "ASC" : null);

    Column c5 = new Column("5", "varchar", "varchar", 5, true, true, String.class.getSimpleName());
    c5.setColumnAlias("varchar");
    c5.setSpecificColumnType(SpecificColumnType.DATABASE);
    c5.setTableName("table_tests");
    c5.setFilter(true);
    c5.setPrimaryKey(c5.getColumnAlias().equals(primaryKey));
    c5.setOrderBy(c5.getColumnAlias().equals(primaryKey) ? "ASC" : null);

    Column c7 = new Column("7", "text", "text", 6, true, true, String.class.getSimpleName());
    c7.setColumnAlias("text");
    c7.setSpecificColumnType(SpecificColumnType.DATABASE);
    c7.setTableName("table_tests");
    c7.setFilter(true);
    c7.setPrimaryKey(c7.getColumnAlias().equals(primaryKey));
    c7.setOrderBy(c7.getColumnAlias().equals(primaryKey) ? "ASC" : null);

    Column c8 = new Column("8", "date", "date", 7, true, true, Date.class.getSimpleName());
    c8.setColumnAlias("date");
    c8.setSpecificColumnType(SpecificColumnType.DATABASE);
    c8.setTableName("table_tests");
    c8.setFilter(true);
    c8.setPrimaryKey(c8.getColumnAlias().equals(primaryKey));
    c8.setOrderBy(c8.getColumnAlias().equals(primaryKey) ? "ASC" : null);

    Column c9 = new Column("9", "date_with_time", "date_with_time", 9, true, true, Date.class.getSimpleName());
    c9.setColumnAlias("date_with_time");
    c9.setSpecificColumnType(SpecificColumnType.DATABASE);
    c9.setTableName("table_tests");
    c9.setFilter(true);
    c9.setPrimaryKey(c9.getColumnAlias().equals(primaryKey));
    c9.setOrderBy(c9.getColumnAlias().equals(primaryKey) ? "ASC" : null);

    Column c10 = new Column("10", "char", "char", 9, true, true, String.class.getSimpleName());
    c10.setColumnAlias("char");
    c10.setSpecificColumnType(SpecificColumnType.DATABASE);
    c10.setTableName("table_tests");
    c10.setFilter(true);
    c10.setPrimaryKey(c10.getColumnAlias().equals(primaryKey));
    c10.setOrderBy(c10.getColumnAlias().equals(primaryKey) ? "ASC" : null);

    Column c11 = new Column("11", "bool", "bool", 11, true, true, Boolean.class.getSimpleName());
    c11.setColumnAlias("bool");
    c11.setSpecificColumnType(SpecificColumnType.DATABASE);
    c11.setTableName("table_tests");
    c11.setFilter(true);
    c11.setPrimaryKey(c11.getColumnAlias().equals(primaryKey));
    c11.setOrderBy(c11.getColumnAlias().equals(primaryKey) ? "ASC" : null);

    columns.add(c1);
    columns.add(c2);
    columns.add(c3);
    columns.add(c4);
    columns.add(c5);
    columns.add(c7);
    columns.add(c8);
    columns.add(c9);
    columns.add(c10);
    columns.add(c11);

    if (withDatasetUrl) {
      for (Iterator<Column> iterator = columns.iterator(); iterator.hasNext();) {
        Column column = iterator.next();
        ColumnRenderer colRenderer = new ColumnRenderer();
        colRenderer.setBehavior(BehaviorEnum.datasetLink);
        colRenderer.setColumnAlias(column.getColumnAlias());
        colRenderer.setDatasetLinkUrl(urlAttachment);
        column.setColumnRenderer(colRenderer);
      }
    }

    item.setColumnModel(columns);

    item.setSitoolsAttachementForUsers(urlAttachment);

    item.setQueryType("W");
    Resource datasource = getDatasourceMongoDB();
    item.setDatasource(datasource);
    item.setDirty(false);

    return item;
  }

  /**
   * Create Dataset for Postgresql datasource on the fuse_prg_id table. This dataset is created on the fuse.fuse_prg_id
   * table
   * 
   * @param id
   *          the id of the dataset
   * @param urlAttachment
   *          the url attachment for the dataset
   * @return the DataSet created
   * 
   */
  public static DataSet createDatasetFusePG(String id, String urlAttachment) {
    DataSet item = new DataSet();
    item.setId(id);
    item.setName("Dataset_fuse_for_pg");
    item.setDescription("Dataset_fuse_description");

    Structure a = new Structure("", "fuse_prg_id");
    a.setSchemaName("fuse");
    ArrayList<Structure> aliases = new ArrayList<Structure>();
    aliases.add(a);
    item.setStructures(aliases);

    SitoolsStructure ss = new SitoolsStructure();
    Table t = new Table("fuse_prg_id", "fuse");
    ss.setMainTable(t);
    item.setStructure(ss);

    ArrayList<Column> columns = new ArrayList<Column>();
    Column c1 = new Column("prop_id", "prop_id", "prop_id", 100, true, true, "varchar");
    c1.setColumnAlias("prop_id");
    c1.setSchema("fuse");
    c1.setSpecificColumnType(SpecificColumnType.DATABASE);
    c1.setTableName("fuse_prg_id");
    c1.setFilter(true);
    c1.setPrimaryKey(true);
    c1.setOrderBy("ASC");

    Column c2 = new Column("title", "title", "title", 100, true, true, "varchar");
    c2.setColumnAlias("title");
    c2.setSchema("fuse");
    c2.setSpecificColumnType(SpecificColumnType.DATABASE);
    c2.setTableName("fuse_prg_id");
    c2.setFilter(true);

    Column c3 = new Column("cycle", "cycle", "cycle", 100, true, true, "int4");
    c3.setColumnAlias("cycle");
    c3.setSchema("fuse");
    c3.setSpecificColumnType(SpecificColumnType.DATABASE);
    c3.setTableName("fuse_prg_id");
    c3.setFilter(true);
    c3.setUnit(new SitoolsUnit("m"));
    c3.setDimensionId("Lengths");

    Column c4 = new Column("fname", "fname", "fname", 100, true, true, "varchar");
    c4.setColumnAlias("fname");
    c4.setSchema("fuse");
    c4.setSpecificColumnType(SpecificColumnType.DATABASE);
    c4.setTableName("fuse_prg_id");
    c4.setFilter(true);

    Column c5 = new Column("lname", "lname", "lname", 100, true, true, "varchar");
    c5.setColumnAlias("lname");
    c5.setSchema("fuse");
    c5.setSpecificColumnType(SpecificColumnType.DATABASE);
    c5.setTableName("fuse_prg_id");
    c5.setFilter(true);

    Column c6 = new Column("lname", "lname", "lname", 100, true, true, "varchar");
    c6.setColumnAlias("lname");
    c6.setSchema("fuse");
    c6.setSpecificColumnType(SpecificColumnType.DATABASE);
    c6.setTableName("fuse_prg_id");
    c6.setFilter(true);

    Column c7 = new Column("institution", "institution", "institution", 100, true, true, "varchar");
    c7.setColumnAlias("institution");
    c7.setSchema("fuse");
    c7.setSpecificColumnType(SpecificColumnType.DATABASE);
    c7.setTableName("fuse_prg_id");
    c7.setFilter(true);

    Column c8 = new Column("abstract", "abstract", "abstract", 100, true, true, "text");
    c8.setColumnAlias("abstract");
    c8.setSchema("fuse");
    c8.setSpecificColumnType(SpecificColumnType.DATABASE);
    c8.setTableName("fuse_prg_id");
    c8.setFilter(true);

    columns.add(c1);
    columns.add(c2);
    columns.add(c3);
    columns.add(c4);
    columns.add(c5);
    columns.add(c6);
    columns.add(c7);
    columns.add(c8);

    item.setColumnModel(columns);

    item.setSitoolsAttachementForUsers(urlAttachment);

    item.setQueryType("W");
    Resource datasource = getDatasourcePostgresql();
    item.setDatasource(datasource);
    item.setDirty(false);
    return item;
  }

  /**
   * Create Dataset for MongoDB datasource on the fuse_prg_id table. This dataset is created on the fuse.fuse_prg_id
   * table
   * 
   * @param id
   *          the id of the dataset
   * @param urlAttachment
   *          the url attachment for the dataset
   * @return the DataSet created
   * 
   */
  public static DataSet createDatasetFuseMongoDB(String id, String urlAttachment) {
    DataSet item = new DataSet();
    item.setId(id);
    item.setName("Dataset_fuse_mongodb");
    item.setDescription("Dataset_fuse_description");

    Structure a = new Structure();
    a.setName("fuse_prg_id");
    a.setType("collection");
    ArrayList<Structure> aliases = new ArrayList<Structure>();
    aliases.add(a);
    item.setStructures(aliases);

    SitoolsStructure ss = new SitoolsStructure();
    Table t = new Table();
    t.setName("fuse_prg_id");
    ss.setMainTable(t);
    item.setStructure(ss);

    ArrayList<Column> columns = new ArrayList<Column>();
    Column c1 = new Column("prop_id", "prop_id", "prop_id", 100, true, true, "String");
    c1.setColumnAlias("prop_id");
    c1.setSpecificColumnType(SpecificColumnType.DATABASE);
    c1.setTableName("fuse_prg_id");
    c1.setFilter(true);
    c1.setPrimaryKey(true);
    c1.setOrderBy("ASC");

    Column c2 = new Column("title", "title", "title", 100, true, true, "String");
    c2.setColumnAlias("title");
    c2.setSpecificColumnType(SpecificColumnType.DATABASE);
    c2.setTableName("fuse_prg_id");
    c2.setFilter(true);

    Column c3 = new Column("cycle", "cycle", "cycle", 100, true, true, "Int32");
    c3.setColumnAlias("cycle");
    c3.setSpecificColumnType(SpecificColumnType.DATABASE);
    c3.setTableName("fuse_prg_id");
    c3.setFilter(true);
    c3.setUnit(new SitoolsUnit("m"));
    c3.setDimensionId("Lengths");

    Column c4 = new Column("fname", "fname", "fname", 100, true, true, "String");
    c4.setColumnAlias("fname");
    c4.setSpecificColumnType(SpecificColumnType.DATABASE);
    c4.setTableName("fuse_prg_id");
    c4.setFilter(true);

    Column c5 = new Column("lname", "lname", "lname", 100, true, true, "String");
    c5.setColumnAlias("lname");
    c5.setSpecificColumnType(SpecificColumnType.DATABASE);
    c5.setTableName("fuse_prg_id");
    c5.setFilter(true);

    Column c6 = new Column("lname", "lname", "lname", 100, true, true, "String");
    c6.setColumnAlias("lname");
    c6.setSpecificColumnType(SpecificColumnType.DATABASE);
    c6.setTableName("fuse_prg_id");
    c6.setFilter(true);

    Column c7 = new Column("institution", "institution", "institution", 100, true, true, "String");
    c7.setColumnAlias("institution");
    c7.setSpecificColumnType(SpecificColumnType.DATABASE);
    c7.setTableName("fuse_prg_id");
    c7.setFilter(true);

    Column c8 = new Column("abstract", "abstract", "abstract", 100, true, true, "String");
    c8.setColumnAlias("abstract");
    c8.setSpecificColumnType(SpecificColumnType.DATABASE);
    c8.setTableName("fuse_prg_id");
    c8.setFilter(true);

    columns.add(c1);
    columns.add(c2);
    columns.add(c3);
    columns.add(c4);
    columns.add(c5);
    columns.add(c6);
    columns.add(c7);
    columns.add(c8);

    item.setColumnModel(columns);

    item.setSitoolsAttachementForUsers(urlAttachment);

    item.setQueryType("W");
    Resource datasource = getDatasourceMongoDB();
    item.setDatasource(datasource);
    item.setDirty(false);
    return item;
  }

  /**
   * Create Dataset for Postgresql datasource on the USERS table. This dataset is created on the sitools.USERS table
   * 
   * @param id
   *          the id of the dataset
   * @param urlAttachment
   *          the url attachment for the dataset
   * @return the DataSet created
   * 
   */
  public static DataSet createDatasetUsersPG(String id, String urlAttachment) {

    DataSet item = new DataSet();
    item.setId(id);
    item.setName("Dataset_users");
    item.setDescription("Dataset_users_description");

    Structure a = new Structure("", "USERS");
    a.setSchemaName("sitools");
    ArrayList<Structure> aliases = new ArrayList<Structure>();
    aliases.add(a);
    item.setStructures(aliases);

    SitoolsStructure ss = new SitoolsStructure();
    Table t = new Table("USERS", "sitools");
    ss.setMainTable(t);
    item.setStructure(ss);

    ArrayList<Column> columns = new ArrayList<Column>();
    Column c1 = new Column("identifier", "identifier", "identifier", 100, true, true, "varchar");
    c1.setColumnAlias("identifier");
    c1.setSchema("sitools");
    c1.setSpecificColumnType(SpecificColumnType.DATABASE);
    c1.setTableName("USERS");
    c1.setFilter(true);
    c1.setPrimaryKey(true);
    c1.setOrderBy("ASC");

    Column c2 = new Column("firstname", "firstname", "firstname", 100, true, true, "varchar");
    c2.setColumnAlias("firstname");
    c2.setSchema("sitools");
    c2.setSpecificColumnType(SpecificColumnType.DATABASE);
    c2.setTableName("USERS");
    c2.setFilter(true);

    Column c3 = new Column("lastname", "lastname", "lastname", 100, true, true, "varchar");
    c3.setColumnAlias("lastname");
    c3.setSchema("sitools");
    c3.setSpecificColumnType(SpecificColumnType.DATABASE);
    c3.setTableName("USERS");
    c3.setFilter(true);

    Column c4 = new Column("email", "email", "email", 100, true, true, "varchar");
    c4.setColumnAlias("email");
    c4.setSchema("sitools");
    c4.setSpecificColumnType(SpecificColumnType.DATABASE);
    c4.setTableName("USERS");
    c4.setFilter(true);

    columns.add(c1);
    columns.add(c2);
    columns.add(c3);
    columns.add(c4);

    item.setColumnModel(columns);

    item.setSitoolsAttachementForUsers(urlAttachment);

    item.setQueryType("W");
    Resource datasource = getDatasourcePostgresql();
    item.setDatasource(datasource);
    item.setDirty(false);

    return item;
  }

  /**
   * Create Dataset for Postgresql datasource on the USERS table. This dataset is created on the sitools.USERS table
   * 
   * @param id
   *          the id of the dataset
   * @param urlAttachment
   *          the url attachment for the dataset
   * @return the DataSet created
   * 
   */
  public static DataSet createDatasetUsersMongoDB(String id, String urlAttachment) {

    DataSet item = new DataSet();
    item.setId(id);
    item.setName("Dataset_users_mongodb");
    item.setDescription("Dataset_users_description");

    Structure a = new Structure("", "USERS");
    a.setSchemaName("sitools");
    ArrayList<Structure> aliases = new ArrayList<Structure>();
    aliases.add(a);
    item.setStructures(aliases);

    SitoolsStructure ss = new SitoolsStructure();
    Table t = new Table();
    t.setName("USERS");
    ss.setMainTable(t);
    item.setStructure(ss);

    ArrayList<Column> columns = new ArrayList<Column>();
    Column c1 = new Column("identifier", "identifier", "identifier", 100, true, true, "String");
    c1.setColumnAlias("identifier");
    c1.setSpecificColumnType(SpecificColumnType.DATABASE);
    c1.setTableName("USERS");
    c1.setFilter(true);
    c1.setPrimaryKey(true);
    c1.setOrderBy("ASC");

    Column c2 = new Column("firstname", "firstname", "firstname", 100, true, true, "String");
    c2.setColumnAlias("firstname");
    c2.setSpecificColumnType(SpecificColumnType.DATABASE);
    c2.setTableName("USERS");
    c2.setFilter(true);

    Column c3 = new Column("lastname", "lastname", "lastname", 100, true, true, "String");
    c3.setColumnAlias("lastname");
    c3.setSpecificColumnType(SpecificColumnType.DATABASE);
    c3.setTableName("USERS");
    c3.setFilter(true);

    Column c4 = new Column("email", "email", "email", 100, true, true, "String");
    c4.setColumnAlias("email");
    c4.setSpecificColumnType(SpecificColumnType.DATABASE);
    c4.setTableName("USERS");
    c4.setFilter(true);

    columns.add(c1);
    columns.add(c2);
    columns.add(c3);
    columns.add(c4);

    item.setColumnModel(columns);

    item.setSitoolsAttachementForUsers(urlAttachment);

    item.setQueryType("W");
    Resource datasource = getDatasourceMongoDB();
    item.setDatasource(datasource);
    item.setDirty(false);

    return item;
  }

  /**
   * Create Dataset for Postgresql datasource on the headers table without all the columns of the table. This dataset is
   * created on the fuse.headers table
   * 
   * @param id
   *          the id of the dataset
   * @param urlAttachment
   *          the url attachment for the dataset
   * @return the DataSet created
   * 
   */
  public static DataSet createDatasetHeadersSimplePG(String id, String urlAttachment) {
    DataSet item = new DataSet();
    item.setId(id);
    item.setName("Dataset_headers");
    item.setDescription("Dataset_headers_description");

    Structure a = new Structure("", "headers");
    a.setSchemaName("fuse");
    ArrayList<Structure> aliases = new ArrayList<Structure>();
    aliases.add(a);
    item.setStructures(aliases);

    SitoolsStructure ss = new SitoolsStructure();
    Table t = new Table("headers", "fuse");
    ss.setMainTable(t);
    item.setStructure(ss);

    ArrayList<Column> columns = new ArrayList<Column>();
    Column c1 = new Column("dataset", "dataset", "dataset", 100, true, true, "varchar");
    c1.setColumnAlias("dataset");
    c1.setSchema("fuse");
    c1.setSpecificColumnType(SpecificColumnType.DATABASE);
    c1.setTableName("headers");
    c1.setFilter(true);
    c1.setPrimaryKey(true);
    c1.setOrderBy("ASC");

    Column c2 = new Column("targname", "targname", "targname", 100, true, true, "varchar");
    c2.setColumnAlias("targname");
    c2.setSchema("fuse");
    c2.setSpecificColumnType(SpecificColumnType.DATABASE);
    c2.setTableName("headers");
    c2.setFilter(true);

    Column c3 = new Column("ra_targ", "ra_targ", "ra_targ", 100, true, true, "float8");
    c3.setColumnAlias("ra_targ");
    c3.setSchema("fuse");
    c3.setSpecificColumnType(SpecificColumnType.DATABASE);
    c3.setTableName("headers");
    c3.setFilter(true);

    Column c4 = new Column("dec_targ", "dec_targ", "dec_targ", 100, true, true, "float8");
    c4.setColumnAlias("dec_targ");
    c4.setSchema("fuse");
    c4.setSpecificColumnType(SpecificColumnType.DATABASE);
    c4.setTableName("headers");
    c4.setFilter(true);

    Column c5 = new Column("dateobs", "dateobs", "dateobs", 100, true, true, "time");
    c5.setColumnAlias("dateobs");
    c5.setSchema("fuse");
    c5.setSpecificColumnType(SpecificColumnType.DATABASE);
    c5.setTableName("headers");
    c5.setFilter(true);

    Column c6 = new Column("x_pos", "x_pos", "x_pos", 100, true, true, "float8");
    c6.setColumnAlias("x_pos");
    c6.setSchema("fuse");
    c6.setSpecificColumnType(SpecificColumnType.DATABASE);
    c6.setTableName("headers");
    c6.setFilter(true);

    Column c7 = new Column("y_pos", "y_pos", "y_pos", 100, true, true, "float8");
    c7.setColumnAlias("y_pos");
    c7.setSchema("fuse");
    c7.setSpecificColumnType(SpecificColumnType.DATABASE);
    c7.setTableName("headers");
    c7.setFilter(true);

    Column c8 = new Column("z_pos", "z_pos", "z_pos", 100, true, true, "float8");
    c8.setColumnAlias("z_pos");
    c8.setSchema("fuse");
    c8.setSpecificColumnType(SpecificColumnType.DATABASE);
    c8.setTableName("headers");
    c8.setFilter(true);

    columns.add(c1);
    columns.add(c2);
    columns.add(c3);
    columns.add(c4);
    columns.add(c5);
    columns.add(c6);
    columns.add(c7);
    columns.add(c8);

    item.setColumnModel(columns);

    item.setSitoolsAttachementForUsers(urlAttachment);

    item.setQueryType("W");
    Resource datasource = getDatasourcePostgresql();
    item.setDatasource(datasource);
    item.setDirty(false);
    return item;
  }

  /**
   * Create Dataset for Postgresql datasource on the headers table without all the columns of the table. This dataset is
   * created on the fuse.headers table
   * 
   * @param id
   *          the id of the dataset
   * @param urlAttachment
   *          the url attachment for the dataset
   * @return the DataSet created
   * 
   */
  public static DataSet createDatasetHeadersSimpleMySQL(String id, String urlAttachment) {
    DataSet item = new DataSet();
    item.setId(id);
    item.setName("Dataset_headers_mysql");
    item.setDescription("Dataset_headers_description");

    Structure a = new Structure("", "HEADERS");
    ArrayList<Structure> aliases = new ArrayList<Structure>();
    aliases.add(a);
    item.setStructures(aliases);

    SitoolsStructure ss = new SitoolsStructure();
    Table t = new Table("HEADERS");
    ss.setMainTable(t);
    item.setStructure(ss);

    ArrayList<Column> columns = new ArrayList<Column>();
    Column c1 = new Column("DATASET", "DATASET", "DATASET", 100, true, true, "VARCHAR");
    c1.setColumnAlias("DATASET");
    c1.setSpecificColumnType(SpecificColumnType.DATABASE);
    c1.setTableName("HEADERS");
    c1.setFilter(true);
    c1.setPrimaryKey(true);
    c1.setOrderBy("ASC");

    Column c2 = new Column("TARGNAME", "TARGNAME", "TARGNAME", 100, true, true, "VARCHAR");
    c2.setColumnAlias("TARGNAME");
    c2.setSpecificColumnType(SpecificColumnType.DATABASE);
    c2.setTableName("HEADERS");
    c2.setFilter(true);

    Column c3 = new Column("RA_TARG", "RA_TARG", "RA_TARG", 100, true, true, "DOUBLE");
    c3.setColumnAlias("RA_TARG");
    c3.setSpecificColumnType(SpecificColumnType.DATABASE);
    c3.setTableName("HEADERS");
    c3.setFilter(true);

    Column c4 = new Column("DEC_TARG", "DEC_TARG", "DEC_TARG", 100, true, true, "DOUBLE");
    c4.setColumnAlias("DEC_TARG");
    c4.setSpecificColumnType(SpecificColumnType.DATABASE);
    c4.setTableName("HEADERS");
    c4.setFilter(true);

    Column c5 = new Column("DATEOBS", "DATEOBS", "DATEOBS", 100, true, true, "TIMESTAMP");
    c5.setColumnAlias("DATEOBS");
    c5.setSpecificColumnType(SpecificColumnType.DATABASE);
    c5.setTableName("HEADERS");
    c5.setFilter(true);

    Column c6 = new Column("X_POS", "X_POS", "X_POS", 100, true, true, "DOUBLE");
    c6.setColumnAlias("X_POS");
    c6.setSpecificColumnType(SpecificColumnType.DATABASE);
    c6.setTableName("HEADERS");
    c6.setFilter(true);

    Column c7 = new Column("Y_POS", "Y_POS", "Y_POS", 100, true, true, "DOUBLE");
    c7.setColumnAlias("Y_POS");
    c7.setSpecificColumnType(SpecificColumnType.DATABASE);
    c7.setTableName("HEADERS");
    c7.setFilter(true);

    Column c8 = new Column("z_POS", "z_POS", "z_POS", 100, true, true, "DOUBLE");
    c8.setColumnAlias("z_POS");
    c8.setSpecificColumnType(SpecificColumnType.DATABASE);
    c8.setTableName("HEADERS");
    c8.setFilter(true);

    columns.add(c1);
    columns.add(c2);
    columns.add(c3);
    columns.add(c4);
    columns.add(c5);
    columns.add(c6);
    columns.add(c7);
    columns.add(c8);

    item.setColumnModel(columns);

    item.setSitoolsAttachementForUsers(urlAttachment);

    item.setQueryType("W");
    Resource datasource = getDatasourceMySQL();
    item.setDatasource(datasource);
    item.setDirty(false);

    return item;
  }

  /**
   * Create Dataset for Postgresql datasource on the headers table without all the columns of the table. This dataset is
   * created on the fuse.headers table
   * 
   * @param id
   *          the id of the dataset
   * @param urlAttachment
   *          the url attachment for the dataset
   * @return the DataSet created
   * 
   */
  public static DataSet createDatasetHeadersJoinMySQL(String id, String urlAttachment, TypeJointure typeJointure) {
    DataSet item = new DataSet();
    item.setId(id);
    item.setName("Dataset_headers_Join");
    item.setDescription("Dataset_headers_description");

    Structure a = new Structure("", "HEADERS");
    ArrayList<Structure> aliases = new ArrayList<Structure>();
    aliases.add(a);
    aliases.add(new Structure("", "IAPDATASETS"));
    aliases.add(new Structure("", "OBJECT_CLASS"));
    item.setStructures(aliases);

    SitoolsStructure ss = new SitoolsStructure();
    Table t = new Table("HEADERS");
    ss.setMainTable(t);
    List<StructureNodeComplete> nodeList = new ArrayList<StructureNodeComplete>();

    StructureNodeComplete snc = new StructureNodeComplete();
    snc.setLeaf(false);
    snc.setTable(new Table("IAPDATASETS"));
    snc.setType(StructureNodeComplete.TABLE_NODE);
    snc.setTypeJointure(typeJointure);

    StructureNodeComplete sncChild = new StructureNodeComplete();
    Predicat pred = new Predicat();
    Column rightAttribute = new Column();
    rightAttribute.setDataIndex("DATASET");
    rightAttribute.setVisible(true);
    rightAttribute.setFilter(true);
    rightAttribute.setTableName("IAPDATASETS");
    rightAttribute.setSqlColumnType("VARCHAR");
    rightAttribute.setSpecificColumnType(SpecificColumnType.DATABASE);
    rightAttribute.setColumnAlias("dataset");
    rightAttribute.setJavaSqlColumnType((short) 12);
    pred.setRightAttribute(rightAttribute);

    Column leftAttribute = new Column();
    leftAttribute.setDataIndex("DATASET");
    leftAttribute.setVisible(true);
    leftAttribute.setFilter(true);
    leftAttribute.setTableName("HEADERS");
    leftAttribute.setSqlColumnType("VARCHAR");
    leftAttribute.setSpecificColumnType(SpecificColumnType.DATABASE);
    leftAttribute.setColumnAlias("dataset_head");
    leftAttribute.setJavaSqlColumnType((short) 12);
    pred.setLeftAttribute(leftAttribute);
    pred.setIndice(0);
    pred.setLogicOperator("on");
    pred.setCompareOperator(Operator.EQ);
    item.setStructure(ss);
    sncChild.setPredicat(pred);
    sncChild.setLeaf(true);
    sncChild.setType(StructureNodeComplete.JOIN_CONDITION_NODE);

    List<StructureNodeComplete> sncChilds = new ArrayList<StructureNodeComplete>();
    sncChilds.add(sncChild);

    snc.setChildren(sncChilds);

    nodeList.add(snc);

    snc = new StructureNodeComplete();
    snc.setLeaf(false);
    snc.setTable(new Table("OBJECT_CLASS"));
    snc.setType(StructureNodeComplete.TABLE_NODE);
    snc.setTypeJointure(typeJointure);

    sncChild = new StructureNodeComplete();
    pred = new Predicat();
    rightAttribute = new Column();
    rightAttribute.setDataIndex("OBJ_NBR");
    rightAttribute.setVisible(true);
    rightAttribute.setFilter(true);
    rightAttribute.setTableName("OBJECT_CLASS");
    rightAttribute.setSqlColumnType("INT");
    rightAttribute.setSpecificColumnType(SpecificColumnType.DATABASE);
    rightAttribute.setColumnAlias("obj_nbr");
    rightAttribute.setJavaSqlColumnType((short) 4);
    pred.setRightAttribute(rightAttribute);

    leftAttribute = new Column();
    leftAttribute.setDataIndex("OBJCLASS");
    leftAttribute.setVisible(true);
    leftAttribute.setFilter(true);
    leftAttribute.setTableName("HEADERS");
    leftAttribute.setSqlColumnType("VARCHAR");
    leftAttribute.setSpecificColumnType(SpecificColumnType.DATABASE);
    leftAttribute.setColumnAlias("dataset_head");
    leftAttribute.setJavaSqlColumnType((short) -6);
    pred.setLeftAttribute(leftAttribute);
    pred.setIndice(0);
    pred.setLogicOperator("on");
    pred.setCompareOperator(Operator.EQ);
    item.setStructure(ss);
    sncChild.setPredicat(pred);

    sncChild.setLeaf(true);
    sncChild.setType(StructureNodeComplete.JOIN_CONDITION_NODE);

    sncChilds = new ArrayList<StructureNodeComplete>();
    sncChilds.add(sncChild);

    snc.setChildren(sncChilds);

    nodeList.add(snc);

    ss.setNodeList(nodeList);

    ArrayList<Column> columns = new ArrayList<Column>();
    Column c1 = new Column("DATASET", "DATASET", "DATASET", 100, true, true, "VARCHAR");
    c1.setColumnAlias("dataset_head");
    c1.setSpecificColumnType(SpecificColumnType.DATABASE);
    c1.setTableName("HEADERS");
    c1.setFilter(true);
    c1.setPrimaryKey(true);
    c1.setOrderBy("ASC");

    Column c2 = new Column("DATASET", "DATASET", "DATASET", 100, true, true, "VARCHAR");
    c2.setColumnAlias("dataset");
    c2.setSpecificColumnType(SpecificColumnType.DATABASE);
    c2.setTableName("IAPDATASETS");
    c2.setFilter(true);

    Column c3 = new Column("OBJCLASS", "OBJCLASS", "OBJCLASS", 100, true, true, "TINYINT");
    c3.setColumnAlias("objclass");
    c3.setSpecificColumnType(SpecificColumnType.DATABASE);
    c3.setTableName("HEADERS");
    c3.setFilter(true);

    columns.add(c1);
    columns.add(c2);
    columns.add(c3);

    item.setColumnModel(columns);

    item.setSitoolsAttachementForUsers(urlAttachment);

    item.setQueryType("W");
    Resource datasource = getDatasourceMySQL();
    item.setDatasource(datasource);
    item.setDirty(false);

    return item;
  }

  /**
   * Create Dataset for Postgresql datasource on the headers table without all the columns of the table. This dataset is
   * created on the fuse.headers table
   * 
   * @param id
   *          the id of the dataset
   * @param urlAttachment
   *          the url attachment for the dataset
   * @return the DataSet created
   * 
   */
  public static DataSet createDatasetHeadersJoinPG(String id, String urlAttachment, TypeJointure typeJointure) {
    DataSet item = new DataSet();
    item.setId(id);
    item.setName("Dataset_headers_postgresql");
    item.setDescription("Dataset_headers_description");

    Structure a = new Structure("", "headers");
    ArrayList<Structure> aliases = new ArrayList<Structure>();
    aliases.add(a);
    aliases.add(new Structure("", "iapdatasets"));
    aliases.add(new Structure("", "object_class"));
    item.setStructures(aliases);

    SitoolsStructure ss = new SitoolsStructure();
    Table t = new Table("headers", "fuse");
    ss.setMainTable(t);
    List<StructureNodeComplete> nodeList = new ArrayList<StructureNodeComplete>();

    StructureNodeComplete snc = new StructureNodeComplete();
    snc.setLeaf(false);
    snc.setTable(new Table("iapdatasets", "fuse"));
    snc.setType(StructureNodeComplete.TABLE_NODE);
    snc.setTypeJointure(typeJointure);

    StructureNodeComplete sncChild = new StructureNodeComplete();
    Predicat pred = new Predicat();
    Column rightAttribute = new Column();
    rightAttribute.setDataIndex("dataset");
    rightAttribute.setVisible(true);
    rightAttribute.setFilter(true);
    rightAttribute.setSchema("fuse");
    rightAttribute.setTableName("iapdatasets");
    rightAttribute.setSqlColumnType("varchar");
    rightAttribute.setSpecificColumnType(SpecificColumnType.DATABASE);
    rightAttribute.setColumnAlias("dataset");
    rightAttribute.setJavaSqlColumnType((short) 12);
    pred.setRightAttribute(rightAttribute);

    Column leftAttribute = new Column();
    leftAttribute.setDataIndex("dataset");
    leftAttribute.setVisible(true);
    leftAttribute.setFilter(true);
    leftAttribute.setTableName("headers");
    leftAttribute.setSchema("fuse");
    leftAttribute.setSqlColumnType("varchar");
    leftAttribute.setSpecificColumnType(SpecificColumnType.DATABASE);
    leftAttribute.setColumnAlias("dataset_head");
    leftAttribute.setJavaSqlColumnType((short) 12);
    pred.setLeftAttribute(leftAttribute);
    pred.setIndice(0);
    pred.setLogicOperator("on");
    pred.setCompareOperator(Operator.EQ);
    item.setStructure(ss);
    sncChild.setPredicat(pred);
    sncChild.setLeaf(true);
    sncChild.setType(StructureNodeComplete.JOIN_CONDITION_NODE);

    List<StructureNodeComplete> sncChilds = new ArrayList<StructureNodeComplete>();
    sncChilds.add(sncChild);

    snc.setChildren(sncChilds);

    nodeList.add(snc);

    snc = new StructureNodeComplete();
    snc.setLeaf(false);
    snc.setTable(new Table("object_class", "fuse"));
    snc.setType(StructureNodeComplete.TABLE_NODE);
    snc.setTypeJointure(typeJointure);

    sncChild = new StructureNodeComplete();
    pred = new Predicat();
    rightAttribute = new Column();
    rightAttribute.setDataIndex("obj_nbr");
    rightAttribute.setVisible(true);
    rightAttribute.setFilter(true);
    rightAttribute.setTableName("object_class");
    rightAttribute.setSchema("fuse");
    rightAttribute.setSqlColumnType("integer");
    rightAttribute.setSpecificColumnType(SpecificColumnType.DATABASE);
    rightAttribute.setColumnAlias("obj_nbr");
    rightAttribute.setJavaSqlColumnType((short) 4);
    pred.setRightAttribute(rightAttribute);

    leftAttribute = new Column();
    leftAttribute.setDataIndex("objclass");
    leftAttribute.setVisible(true);
    leftAttribute.setFilter(true);
    leftAttribute.setTableName("headers");
    leftAttribute.setSchema("fuse");
    leftAttribute.setSqlColumnType("smallint");
    leftAttribute.setSpecificColumnType(SpecificColumnType.DATABASE);
    leftAttribute.setColumnAlias("dataset_head");
    leftAttribute.setJavaSqlColumnType((short) -6);
    pred.setLeftAttribute(leftAttribute);
    pred.setIndice(0);
    pred.setLogicOperator("on");
    pred.setCompareOperator(Operator.EQ);
    item.setStructure(ss);
    sncChild.setPredicat(pred);

    sncChild.setLeaf(true);
    sncChild.setType(StructureNodeComplete.JOIN_CONDITION_NODE);

    sncChilds = new ArrayList<StructureNodeComplete>();
    sncChilds.add(sncChild);

    snc.setChildren(sncChilds);

    nodeList.add(snc);

    ss.setNodeList(nodeList);

    ArrayList<Column> columns = new ArrayList<Column>();
    Column c1 = new Column("dataset", "dataset", "dataset", 100, true, true, "varchar");
    c1.setColumnAlias("dataset_head");
    c1.setSchema("fuse");
    c1.setSpecificColumnType(SpecificColumnType.DATABASE);
    c1.setTableName("headers");
    c1.setFilter(true);
    c1.setPrimaryKey(true);
    c1.setOrderBy("ASC");

    Column c2 = new Column("dataset", "dataset", "dataset", 100, true, true, "varchar");
    c2.setColumnAlias("dataset");
    c2.setSchema("fuse");
    c2.setSpecificColumnType(SpecificColumnType.DATABASE);
    c2.setTableName("iapdatasets");
    c2.setFilter(true);

    Column c3 = new Column("obj_nbr", "obj_nbr", "obj_nbr", 100, true, true, "integer");
    c3.setColumnAlias("objclass");
    c3.setSchema("fuse");
    c3.setSpecificColumnType(SpecificColumnType.DATABASE);
    c3.setTableName("object_class");
    c3.setFilter(true);

    columns.add(c1);
    columns.add(c2);
    columns.add(c3);

    item.setColumnModel(columns);

    item.setSitoolsAttachementForUsers(urlAttachment);

    item.setQueryType("W");
    Resource datasource = getDatasourcePostgresql();
    item.setDatasource(datasource);
    item.setDirty(false);
    return item;
  }

  /**
   * Create Dataset for Postgresql datasource on the headers table without all the columns of the table. This dataset is
   * created on the fuse.headers table
   * 
   * @param id
   *          the id of the dataset
   * @param urlAttachment
   *          the url attachment for the dataset
   * @return the DataSet created
   * 
   */
  public static DataSet createDatasetViewHeadersJoinMySQL(String id, String urlAttachment) {
    DataSet item = new DataSet();
    item.setId(id);
    item.setName("Dataset_headers_Join_Views");
    item.setDescription("Dataset_headers_description");

    Structure a = new Structure("", "VIEW_HEADERS");
    ArrayList<Structure> aliases = new ArrayList<Structure>();
    aliases.add(a);
    aliases.add(new Structure("", "VIEW_HEADERS_BIS"));
    item.setStructures(aliases);

    SitoolsStructure ss = new SitoolsStructure();
    Table t = new Table("VIEW_HEADERS");
    ss.setMainTable(t);
    List<StructureNodeComplete> nodeList = new ArrayList<StructureNodeComplete>();

    StructureNodeComplete snc = new StructureNodeComplete();
    snc.setLeaf(false);
    snc.setTable(new Table("VIEW_HEADERS_BIS"));
    snc.setType(StructureNodeComplete.TABLE_NODE);
    snc.setTypeJointure(TypeJointure.INNER_JOIN);

    StructureNodeComplete sncChild = new StructureNodeComplete();
    Predicat pred = new Predicat();
    Column rightAttribute = new Column();
    rightAttribute.setDataIndex("DATASET");
    rightAttribute.setVisible(true);
    rightAttribute.setFilter(true);
    rightAttribute.setTableName("VIEW_HEADERS");
    rightAttribute.setSqlColumnType("VARCHAR");
    rightAttribute.setSpecificColumnType(SpecificColumnType.DATABASE);
    rightAttribute.setColumnAlias("dataset");
    rightAttribute.setJavaSqlColumnType((short) 12);
    pred.setRightAttribute(rightAttribute);

    Column leftAttribute = new Column();
    leftAttribute.setDataIndex("DATASET");
    leftAttribute.setVisible(true);
    leftAttribute.setFilter(true);
    leftAttribute.setTableName("VIEW_HEADERS_BIS");
    leftAttribute.setSqlColumnType("VARCHAR");
    leftAttribute.setSpecificColumnType(SpecificColumnType.DATABASE);
    leftAttribute.setColumnAlias("dataset_bis");
    leftAttribute.setJavaSqlColumnType((short) 12);
    pred.setLeftAttribute(leftAttribute);
    pred.setIndice(0);
    pred.setLogicOperator("on");
    pred.setCompareOperator(Operator.EQ);
    item.setStructure(ss);
    sncChild.setPredicat(pred);
    sncChild.setLeaf(true);
    sncChild.setType(StructureNodeComplete.JOIN_CONDITION_NODE);

    List<StructureNodeComplete> sncChilds = new ArrayList<StructureNodeComplete>();
    sncChilds.add(sncChild);

    snc.setChildren(sncChilds);

    nodeList.add(snc);

    ss.setNodeList(nodeList);

    ArrayList<Column> columns = new ArrayList<Column>();
    Column c1 = new Column("DATASET", "DATASET", "DATASET", 100, true, true, "VARCHAR");
    c1.setColumnAlias("dataset");
    c1.setSpecificColumnType(SpecificColumnType.DATABASE);
    c1.setTableName("VIEW_HEADERS");
    c1.setFilter(true);
    c1.setPrimaryKey(true);
    c1.setOrderBy("ASC");

    Column c2 = new Column("DATASET", "DATASET", "DATASET", 100, true, true, "VARCHAR");
    c2.setColumnAlias("dataset_bis");
    c2.setSpecificColumnType(SpecificColumnType.DATABASE);
    c2.setTableName("VIEW_HEADERS_BIS");
    c2.setFilter(true);

    columns.add(c1);
    columns.add(c2);

    item.setColumnModel(columns);

    item.setSitoolsAttachementForUsers(urlAttachment);

    item.setQueryType("W");
    Resource datasource = getDatasourceMySQL();
    item.setDatasource(datasource);
    item.setDirty(false);

    return item;
  }

  /**
   * Create Dataset for Postgresql datasource on the headers table without all the columns of the table. This dataset is
   * created on the fuse.headers table
   * 
   * @param id
   *          the id of the dataset
   * @param urlAttachment
   *          the url attachment for the dataset
   * @return the DataSet created
   * 
   */
  public static DataSet createDatasetViewHeadersJoinPG(String id, String urlAttachment) {
    DataSet item = new DataSet();
    item.setId(id);
    item.setName("Dataset_headers_PG");
    item.setDescription("Dataset_headers_description");

    Structure a = new Structure("", "view_headers");
    ArrayList<Structure> aliases = new ArrayList<Structure>();
    aliases.add(a);
    aliases.add(new Structure("", "view_headers_bis"));
    item.setStructures(aliases);

    SitoolsStructure ss = new SitoolsStructure();
    Table t = new Table("view_headers", "fuse");
    ss.setMainTable(t);
    List<StructureNodeComplete> nodeList = new ArrayList<StructureNodeComplete>();

    StructureNodeComplete snc = new StructureNodeComplete();
    snc.setLeaf(false);
    snc.setTable(new Table("view_headers_bis", "fuse"));
    snc.setType(StructureNodeComplete.TABLE_NODE);
    snc.setTypeJointure(TypeJointure.INNER_JOIN);

    StructureNodeComplete sncChild = new StructureNodeComplete();
    Predicat pred = new Predicat();
    Column rightAttribute = new Column();
    rightAttribute.setDataIndex("dataset");
    rightAttribute.setVisible(true);
    rightAttribute.setFilter(true);
    rightAttribute.setSchema("fuse");
    rightAttribute.setTableName("view_headers");
    rightAttribute.setSqlColumnType("varchar");
    rightAttribute.setSpecificColumnType(SpecificColumnType.DATABASE);
    rightAttribute.setColumnAlias("dataset");
    rightAttribute.setJavaSqlColumnType((short) 12);
    pred.setRightAttribute(rightAttribute);

    Column leftAttribute = new Column();
    leftAttribute.setDataIndex("dataset");
    leftAttribute.setVisible(true);
    leftAttribute.setFilter(true);
    leftAttribute.setTableName("view_headers_bis");
    leftAttribute.setSchema("fuse");
    leftAttribute.setSqlColumnType("varchar");
    leftAttribute.setSpecificColumnType(SpecificColumnType.DATABASE);
    leftAttribute.setColumnAlias("dataset_head");
    leftAttribute.setJavaSqlColumnType((short) 12);
    pred.setLeftAttribute(leftAttribute);
    pred.setIndice(0);
    pred.setLogicOperator("on");
    pred.setCompareOperator(Operator.EQ);
    item.setStructure(ss);
    sncChild.setPredicat(pred);
    sncChild.setLeaf(true);
    sncChild.setType(StructureNodeComplete.JOIN_CONDITION_NODE);

    List<StructureNodeComplete> sncChilds = new ArrayList<StructureNodeComplete>();
    sncChilds.add(sncChild);

    snc.setChildren(sncChilds);

    nodeList.add(snc);

    ss.setNodeList(nodeList);

    ArrayList<Column> columns = new ArrayList<Column>();
    Column c1 = new Column("dataset", "dataset", "dataset", 100, true, true, "varchar");
    c1.setColumnAlias("dataset");
    c1.setSchema("fuse");
    c1.setSpecificColumnType(SpecificColumnType.DATABASE);
    c1.setTableName("view_headers");
    c1.setFilter(true);
    c1.setPrimaryKey(true);
    c1.setOrderBy("ASC");

    Column c2 = new Column("dataset", "dataset", "dataset", 100, true, true, "varchar");
    c2.setColumnAlias("dataset_bis");
    c2.setSchema("fuse");
    c2.setSpecificColumnType(SpecificColumnType.DATABASE);
    c2.setTableName("view_headers_bis");
    c2.setFilter(true);

    columns.add(c1);
    columns.add(c2);

    item.setColumnModel(columns);

    item.setSitoolsAttachementForUsers(urlAttachment);

    item.setQueryType("W");
    Resource datasource = getDatasourcePostgresql();
    item.setDatasource(datasource);
    item.setDirty(false);
    return item;
  }

  /**
   * Create Dataset for Postgresql datasource on the jeo_entries table. This dataset is created on the
   * sitools.jeo_entries table of the CNES_SIG database
   * 
   * @param id
   *          the id of the dataset
   * @param urlAttachment
   *          the url attachment for the dataset
   * @return the DataSet created
   * 
   */
  public static DataSet createDatasetJeoEntryPG(String id, String urlAttachment) {
    DataSet item = new DataSet();
    item.setId(id);
    item.setName("JeoEntry");
    item.setDescription("Dataset_jeo_entry_description");

    Structure a = new Structure("", "jeo_entries");
    a.setSchemaName("sitools_used_for_tests");
    ArrayList<Structure> aliases = new ArrayList<Structure>();
    aliases.add(a);
    item.setStructures(aliases);

    SitoolsStructure ss = new SitoolsStructure();
    Table t = new Table("jeo_entries", "sitools_used_for_tests");
    ss.setMainTable(t);
    item.setStructure(ss);

    ArrayList<Column> columns = new ArrayList<Column>();
    Column c1 = new Column("identifier", "identifier", "identifier", 100, true, true, "varchar");
    c1.setColumnAlias("identifier");
    c1.setSchema("sitools_used_for_tests");
    c1.setSpecificColumnType(SpecificColumnType.DATABASE);
    c1.setTableName("jeo_entries");
    c1.setFilter(true);
    c1.setPrimaryKey(true);
    c1.setOrderBy("ASC");

    Column c2 = new Column("date", "date", "date", 100, true, true, "date");
    c2.setColumnAlias("date");
    c2.setSchema("sitools_used_for_tests");
    c2.setSpecificColumnType(SpecificColumnType.DATABASE);
    c2.setTableName("jeo_entries");
    c2.setFilter(true);

    Column c3 = new Column("building_identifier", "building_identifier", "building_identifier", 100, true, true,
        "varchar");
    c3.setColumnAlias("building_identifier");
    c3.setSchema("sitools_used_for_tests");
    c3.setSpecificColumnType(SpecificColumnType.DATABASE);
    c3.setTableName("jeo_entries");
    c3.setFilter(true);

    Column c4 = new Column("building_state", "building_state", "building_state", 100, true, true, "varchar");
    c4.setColumnAlias("building_state");
    c4.setSchema("sitools_used_for_tests");
    c4.setSpecificColumnType(SpecificColumnType.DATABASE);
    c4.setTableName("jeo_entries");
    c4.setFilter(true);

    Column c5 = new Column("building_peoplenb", "building_peoplenb", "building_peoplenb", 100, true, true, "int4");
    c5.setColumnAlias("building_peoplenb");
    c5.setSchema("sitools_used_for_tests");
    c5.setSpecificColumnType(SpecificColumnType.DATABASE);
    c5.setTableName("jeo_entries");
    c5.setFilter(true);

    Column c6 = new Column("ele", "ele", "ele", 100, true, true, "float8");
    c6.setColumnAlias("ele");
    c6.setSchema("sitools_used_for_tests");
    c6.setSpecificColumnType(SpecificColumnType.DATABASE);
    c6.setTableName("jeo_entries");
    c6.setFilter(true);

    Column c7 = new Column("coord", "coord", "coord", 100, true, true, "geometry");
    c7.setColumnAlias("coord");
    c7.setSchema("sitools_used_for_tests");
    c7.setSpecificColumnType(SpecificColumnType.DATABASE);
    c7.setTableName("jeo_entries");
    c7.setFilter(true);
    ColumnRenderer colRenderer = new ColumnRenderer(BehaviorEnum.noClientAccess);
    c7.setColumnRenderer(colRenderer);

    columns.add(c1);
    columns.add(c2);
    columns.add(c3);
    columns.add(c4);
    columns.add(c5);
    columns.add(c6);
    columns.add(c7);

    item.setColumnModel(columns);

    item.setSitoolsAttachementForUsers(urlAttachment);

    item.setQueryType("W");
    Resource datasource = new Resource();
    datasource.setId("4486885e-a71e-44ed-9fbe-6186beaec9ad");
    datasource.setType("datasource");
    datasource.setMediaType("datasource");
    item.setDatasource(datasource);
    item.setDirty(false);
    return item;
  }

  /**
   * Create Dataset for MongoDB datasource on the jeo_entries table. This dataset is created on the jeo_entries
   * collection of the Continuous_integration_tests Database
   * 
   * @param id
   *          the id of the dataset
   * @param urlAttachment
   *          the url attachment for the dataset
   * @return the DataSet created
   * 
   */
  public static DataSet createDatasetJeoEntryMongoDB(String id, String urlAttachment) {
    DataSet item = new DataSet();
    item.setId(id);
    item.setName("JeoEntry_mongodb");
    item.setDescription("Dataset_jeo_entry_description");

    Structure a = new Structure();
    a.setAlias("jeo_entries");
    ArrayList<Structure> aliases = new ArrayList<Structure>();
    aliases.add(a);
    item.setStructures(aliases);

    SitoolsStructure ss = new SitoolsStructure();
    Table t = new Table();
    t.setName("jeo_entries");
    ss.setMainTable(t);
    item.setStructure(ss);

    ArrayList<Column> columns = new ArrayList<Column>();
    Column c1 = new Column("identifier", "identifier", "identifier", 100, true, true, "String");
    c1.setColumnAlias("identifier");
    c1.setSpecificColumnType(SpecificColumnType.DATABASE);
    c1.setTableName("jeo_entries");
    c1.setFilter(true);
    c1.setPrimaryKey(true);
    c1.setOrderBy("ASC");

    Column c2 = new Column("date", "date", "date", 100, true, true, "Date");
    c2.setColumnAlias("date");
    c2.setSpecificColumnType(SpecificColumnType.DATABASE);
    c2.setTableName("jeo_entries");
    c2.setFilter(true);

    Column c3 = new Column("building_identifier", "building_identifier", "building_identifier", 100, true, true,
        "String");
    c3.setColumnAlias("building_identifier");
    c3.setSpecificColumnType(SpecificColumnType.DATABASE);
    c3.setTableName("jeo_entries");
    c3.setFilter(true);

    Column c4 = new Column("building_state", "building_state", "building_state", 100, true, true, "String");
    c4.setColumnAlias("building_state");
    c4.setSpecificColumnType(SpecificColumnType.DATABASE);
    c4.setTableName("jeo_entries");
    c4.setFilter(true);

    Column c5 = new Column("building_peoplenb", "building_peoplenb", "building_peoplenb", 100, true, true, "Double");
    c5.setColumnAlias("building_peoplenb");
    c5.setSpecificColumnType(SpecificColumnType.DATABASE);
    c5.setTableName("jeo_entries");
    c5.setFilter(true);

    Column c6 = new Column("ele", "ele", "ele", 100, true, true, "Double");
    c6.setColumnAlias("ele");
    c6.setSpecificColumnType(SpecificColumnType.DATABASE);
    c6.setTableName("jeo_entries");
    c6.setFilter(true);

    Column c7 = new Column("coord", "coord", "coord", 100, true, true, "BasicDBObject");
    c7.setColumnAlias("coord");
    c7.setSpecificColumnType(SpecificColumnType.DATABASE);
    c7.setTableName("jeo_entries");
    c7.setFilter(true);
    ColumnRenderer colRenderer = new ColumnRenderer(BehaviorEnum.noClientAccess);
    c7.setColumnRenderer(colRenderer);

    columns.add(c1);
    columns.add(c2);
    columns.add(c3);
    columns.add(c4);
    columns.add(c5);
    columns.add(c6);
    columns.add(c7);

    item.setColumnModel(columns);

    item.setSitoolsAttachementForUsers(urlAttachment);

    item.setQueryType("W");
    Resource datasource = getDatasourceMongoDB();
    item.setDatasource(datasource);
    item.setDirty(false);
    return item;
  }

  public static DataSet createDatasetArticlesMongoDB(String id, String urlAttachment) {
    DataSet item = new DataSet();
    item.setId(id);
    item.setName("Dataset_articles");
    item.setDescription("Dataset_articles_description");

    Structure a = new Structure("", "Articles");
    a.setType("collection");
    ArrayList<Structure> aliases = new ArrayList<Structure>();
    aliases.add(a);
    item.setStructures(aliases);

    SitoolsStructure ss = new SitoolsStructure();
    Table t = new Table();
    t.setName("Articles");
    ss.setMainTable(t);
    item.setStructure(ss);

    ArrayList<Column> columns = new ArrayList<Column>();
    Column c1 = new Column("1", "_id", "_id", 1, true, true, String.class.getSimpleName());
    c1.setColumnAlias("_id");
    c1.setSpecificColumnType(SpecificColumnType.DATABASE);
    c1.setTableName("Articles");
    c1.setFilter(true);
    c1.setPrimaryKey(true);
    c1.setOrderBy("ASC");

    Column c2 = new Column("2", "product", "product", 2, true, true, String.class.getSimpleName());
    c2.setColumnAlias("product");
    c2.setSpecificColumnType(SpecificColumnType.DATABASE);
    c2.setTableName("Articles");
    c2.setFilter(true);

    Column c3 = new Column("3", "parameter", "parameter", 3, true, true, String.class.getSimpleName());
    c3.setColumnAlias("parameter");
    c3.setSpecificColumnType(SpecificColumnType.DATABASE);
    c3.setTableName("Articles");
    c3.setFilter(true);

    Column c4 = new Column("4", "deliveredBy", "deliveredBy", 4, true, true, String.class.getSimpleName());
    c4.setColumnAlias("deliveredBy");
    c4.setSpecificColumnType(SpecificColumnType.DATABASE);
    c4.setTableName("Articles");
    c4.setFilter(true);

    Column c5 = new Column("5", "sensor", "sensor", 5, true, true, String.class.getSimpleName());
    c5.setColumnAlias("sensor");
    c5.setSpecificColumnType(SpecificColumnType.DATABASE);
    c5.setTableName("Articles");
    c5.setFilter(true);

    Column c6 = new Column("6", "timeCoverage.startDate", "startDate", 6, true, true, Date.class.getSimpleName());
    c6.setColumnAlias("startDate");
    c6.setSpecificColumnType(SpecificColumnType.DATABASE);
    c6.setTableName("Articles");
    c6.setFilter(true);

    Column c7 = new Column("7", "timeCoverage.endDate", "endDate", 7, true, true, Date.class.getSimpleName());
    c7.setColumnAlias("endDate");
    c7.setSpecificColumnType(SpecificColumnType.DATABASE);
    c7.setTableName("Articles");
    c7.setFilter(true);

    Column c8 = new Column("8", "spaceCoverage.zone", "zone", 9, true, true, String.class.getSimpleName());
    c8.setColumnAlias("zone");
    c8.setSpecificColumnType(SpecificColumnType.DATABASE);
    c8.setTableName("Articles");
    c8.setFilter(true);

    Column c9 = new Column("9", "uri", "uri", 9, true, true, String.class.getSimpleName());
    c9.setColumnAlias("uri");
    c9.setSpecificColumnType(SpecificColumnType.DATABASE);
    c9.setTableName("Articles");
    c9.setFilter(true);

    Column c10 = new Column("10", "Pproduct", "Pproduct", 11, true, true, String.class.getSimpleName());
    c10.setColumnAlias("Pproduct");
    c10.setSpecificColumnType(SpecificColumnType.DATABASE);
    c10.setTableName("Articles");
    c10.setFilter(true);

    Column c11 = new Column("11", "Pparameter", "Pparameter", 11, true, true, String.class.getSimpleName());
    c11.setColumnAlias("Pparameter");
    c11.setSpecificColumnType(SpecificColumnType.DATABASE);
    c11.setTableName("Articles");
    c11.setFilter(true);

    Column c12 = new Column("12", "Pproject", "Pproject", 11, true, true, String.class.getSimpleName());
    c12.setColumnAlias("Pproject");
    c12.setSpecificColumnType(SpecificColumnType.DATABASE);
    c12.setTableName("Articles");
    c12.setFilter(true);

    columns.add(c1);
    columns.add(c2);
    columns.add(c3);
    columns.add(c4);
    columns.add(c5);
    columns.add(c6);
    columns.add(c7);
    columns.add(c8);
    columns.add(c9);
    columns.add(c10);
    columns.add(c11);
    columns.add(c12);

    item.setColumnModel(columns);

    item.setSitoolsAttachementForUsers(urlAttachment);

    item.setQueryType("W");
    Resource datasource = getDatasourceMongoDB();
    item.setDatasource(datasource);
    item.setDirty(false);

    return item;
  }

}
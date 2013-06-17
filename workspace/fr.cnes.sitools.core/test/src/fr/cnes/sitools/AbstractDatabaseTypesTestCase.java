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
package fr.cnes.sitools;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Test;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;

import fr.cnes.sitools.datasource.jdbc.model.Record;

/**
 * The aim of the test is to check if the API handle properly usual database types
 * 
 * @author m.gond (AKKA Technologies)
 * 
 * 
 *         PG insert into test.table_tests values ( 0, 1.1553268, 8.96095939005054, 0, 0, 'ceci est un enregistrement
 *         0', '2011-04-21 14:01:13.047791', '2011-04-21 14:01:13.047794+02', '2011-04-21', '14:01:13.04697+02', 1, 'y',
 *         true, 97, 0.675227390775783, 'ceci est 8', 1, 1, '14:01:13.04697' ); insert into test.table_tests values ( 1,
 *         1.316118, 5.916038733962690, 1, 1, 'ceci est un enregistrement 1', '2011-04-21 14:01:13.048296', '2011-08-21
 *         14:01:13.048297+02', '2011-04-27', '14:25:13.04697+02', 2, 'y', false, 22, 3.43866701866351, 'ceci est 5', 2,
 *         2, '14:28:13.04697' );
 * 
 *         MYSQL INSERT INTO `TABLE_TESTS`
 *         (field_varchar_id,field_tiny_int,field_small_int,field_medium_int,field_int,field_big_int
 *         ,field_float,field_double
 *         ,field_decimal,field_varchar,field_text,field_timestamp,field_datetime,field_date,field_year
 *         ,field_time,field_char,field_bool) VALUES
 *         ('0',10,1,10,10,97,1.15533,8.96095939005054,'125.25360','varchar','Ceci est un enregistrement de test
 *         1','2011-04-27 11:23:36.0','2011-03-11 11:01:50','2010-04-01',2011,'14:01:13','y',1); INSERT INTO
 *         `TABLE_TESTS`
 *         (field_varchar_id,field_tiny_int,field_small_int,field_medium_int,field_int,field_big_int,field_float
 *         ,field_double
 *         ,field_decimal,field_varchar,field_text,field_timestamp,field_datetime,field_date,field_year,field_time
 *         ,field_char,field_bool) VALUES ('1',-10,0,101,-10,22,1.31612,5.91603873396269,'-250.15200','varchar 2','Ceci
 *         est un enregistrement de test 2','2011-04-26 16:31:38','2011-04-11
 *         11:01:50','2010-05-01',2010,'14:01:15','n',0);
 * 
 */
public abstract class AbstractDatabaseTypesTestCase extends AbstractDataSetManagerTestCase {

  /** url attachment of the dataset with MySQL datasource */
  private static String urlAttachMySQL = "/dataset/test/mysql";

  /** url attachment of the dataset with postgreSQL datasource */
  private static String urlAttachPostgreSQL = "/dataset/test/pg";

  /** Details of the first MySQL record */
  private static HashMap<String, String> recDetail1MySQL;
  /** Details of the second MySQL record */
  private static HashMap<String, String> recDetail2MySQL;

  /** Details of the first PostgreSQL record */
  private static HashMap<String, String> recDetail1PostgreSQL;
  /** Details of the second PostgreSQL record */
  private static HashMap<String, String> recDetail2PostgreSQL;

  /**
   * absolute url for MySQL datasource's dataset
   * 
   * @return url
   */
  protected String getBaseUrlMySQL() {
    return super.getHostUrl() + urlAttachMySQL;
  }

  /**
   * absolute url for MySQL datasource's dataset
   * 
   * @return url
   */
  protected String getBaseUrlPostgreSQL() {
    return super.getHostUrl() + urlAttachPostgreSQL;
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.cnes.sitools.AbstractSitoolsServerTestCase#setUp()
   */
  @Override
  public void setUp() throws Exception {
    // TODO Auto-generated method stub
    super.setUp();
    setDetailsRecord1Mysql();
    setDetailsRecord2Mysql();

    setDetailsRecord1PostgreSQL();
    setDetailsRecord2PostgreSQL();

  }

  /**
   * Set the details of the first MySQL record
   */
  private void setDetailsRecord1Mysql() {
    recDetail1MySQL = new HashMap<String, String>();
    recDetail1MySQL.put("field_varchar_id", "0");
    recDetail1MySQL.put("field_tiny_int", "10");
    recDetail1MySQL.put("field_small_int", "1");
    recDetail1MySQL.put("field_medium_int", "10");
    recDetail1MySQL.put("field_int", "10");
    recDetail1MySQL.put("field_big_int", "97");
    recDetail1MySQL.put("field_float", "1.15533");
    recDetail1MySQL.put("field_double", "8.96095939005054");
    recDetail1MySQL.put("field_decimal", "125.25360");
    recDetail1MySQL.put("field_varchar", "varchar");
    recDetail1MySQL.put("field_text", "Ceci est un enregistrement de test 1");
    recDetail1MySQL.put("field_timestamp", "2011-04-27T11:23:36.000");
    recDetail1MySQL.put("field_datetime", "2011-03-11T11:01:50.000");
    recDetail1MySQL.put("field_date", "2010-04-01T00:00:00.000");
    recDetail1MySQL.put("field_year", "2011-01-01T00:00:00.000");
    recDetail1MySQL.put("field_time", "14:01:13.000");
    recDetail1MySQL.put("field_char", "y");
    recDetail1MySQL.put("field_bool", "1");

  }

  /**
   * Set the details of the first MySQL record
   */
  private void setDetailsRecord2Mysql() {
    recDetail2MySQL = new HashMap<String, String>();
    recDetail2MySQL.put("field_varchar_id", "1");
    recDetail2MySQL.put("field_tiny_int", "-10");
    recDetail2MySQL.put("field_small_int", "0");
    recDetail2MySQL.put("field_medium_int", "101");
    recDetail2MySQL.put("field_int", "-10");
    recDetail2MySQL.put("field_big_int", "22");
    recDetail2MySQL.put("field_float", "1.31612");
    recDetail2MySQL.put("field_double", "5.91603873396269");
    recDetail2MySQL.put("field_decimal", "-250.15200");
    recDetail2MySQL.put("field_varchar", "varchar 2");
    recDetail2MySQL.put("field_text", "Ceci est un enregistrement de test 2");
    recDetail2MySQL.put("field_timestamp", "2011-04-26T16:31:38.000");
    recDetail2MySQL.put("field_datetime", "2011-04-11T11:01:50.000");
    recDetail2MySQL.put("field_date", "2010-05-01T00:00:00.000");
    recDetail2MySQL.put("field_year", "2010-01-01T00:00:00.000");
    recDetail2MySQL.put("field_time", "14:01:15.000");
    recDetail2MySQL.put("field_char", "n");
    recDetail2MySQL.put("field_bool", "0");

  }

  /**
   * Set the details of the first PostgreSQL record
   */
  private void setDetailsRecord1PostgreSQL() {
    recDetail1PostgreSQL = new HashMap<String, String>();
    recDetail1PostgreSQL.put("int", "0");
    recDetail1PostgreSQL.put("float", "1.1553268");
    recDetail1PostgreSQL.put("double", "8.96095939005054");
    recDetail1PostgreSQL.put("varchar", "0");
    recDetail1PostgreSQL.put("varchar_id", "0");
    recDetail1PostgreSQL.put("text", "ceci est un enregistrement 0");
    recDetail1PostgreSQL.put("timestamp", "2011-04-21T14:01:13.047");
    recDetail1PostgreSQL.put("date", "2011-04-21T00:00:00.000");
    recDetail1PostgreSQL.put("smallint", "1");
    recDetail1PostgreSQL.put("char", "y");
    recDetail1PostgreSQL.put("bool", "t");
    recDetail1PostgreSQL.put("timestamp_with_time_zone", "2011-04-21T14:01:13.048");
    recDetail1PostgreSQL.put("bigint", "97");
    recDetail1PostgreSQL.put("numeric", "0.675227390775783");
    recDetail1PostgreSQL.put("char10", "ceci est 8");
    recDetail1PostgreSQL.put("serial", "1");
    recDetail1PostgreSQL.put("bigserial", "1");
    recDetail1PostgreSQL.put("time_without_time_zone", "14:01:13.047");

  }

  /**
   * Set the details of the first PostgreSQL record
   */
  private void setDetailsRecord2PostgreSQL() {
    recDetail2PostgreSQL = new HashMap<String, String>();
    recDetail2PostgreSQL.put("int", "1");
    recDetail2PostgreSQL.put("float", "1.316118");
    recDetail2PostgreSQL.put("double", "5.91603873396269");
    recDetail2PostgreSQL.put("varchar", "1");
    recDetail2PostgreSQL.put("varchar_id", "1");
    recDetail2PostgreSQL.put("text", "ceci est un enregistrement 1");
    recDetail2PostgreSQL.put("timestamp", "2011-04-21T14:01:13.048");
    recDetail2PostgreSQL.put("date", "2011-04-27T00:00:00.000");
    recDetail2PostgreSQL.put("smallint", "2");
    recDetail2PostgreSQL.put("char", "y");
    recDetail2PostgreSQL.put("bool", "f");
    recDetail2PostgreSQL.put("timestamp_with_time_zone", "2011-08-21T14:01:13.048");
    recDetail2PostgreSQL.put("bigint", "22");
    recDetail2PostgreSQL.put("numeric", "3.43866701866351");
    recDetail2PostgreSQL.put("char10", "ceci est 5");
    recDetail2PostgreSQL.put("serial", "2");
    recDetail2PostgreSQL.put("bigserial", "2");
    recDetail2PostgreSQL.put("time_without_time_zone", "14:28:13.047");
  }

  /**
   * Test getting the records
   */
  @Test
  public void test() {
    docAPI.setActive(false);
    checkRecordsMysql();
    checkRecordsPostgreSQL();
  }

  /**
   * Test getting the records
   */
  @Test
  public void testAPI() {
    docAPI.setActive(true);
    docAPI.appendChapter("Check database fields");
    docAPI.appendSubChapter("MySQL dataset", "mysql");
    checkRecordsMysql();
    docAPI.appendSubChapter("postgreSQL dataset", "pg");
    checkRecordsPostgreSQL();
    docAPI.close();
  }

  /**
   * Test API records for MySQL
   */
  public void checkRecordsMysql() {
    String url = getBaseUrlMySQL() + "/records?limit=2";
    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      retrieveDocAPI(url, "", parameters, url);
    }
    else {
      ClientResource cr = new ClientResource(url);
      Representation result = cr.get(getMediaTest());
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());

      ArrayList<Record> records = getRecords(getMediaTest(), result);

      Record rec1 = getRecord(recDetail1MySQL);
      Record rec2 = getRecord(recDetail2MySQL);

      assertRecord(rec1, records.get(0));
      assertRecord(rec2, records.get(1));

    }
  }

  /**
   * Tests API records for PostgresSQL
   */
  public void checkRecordsPostgreSQL() {
    String url = getBaseUrlPostgreSQL() + "/records?limit=2";
    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      retrieveDocAPI(url, "", parameters, url);
    }
    else {
      ClientResource cr = new ClientResource(url);
      Representation result = cr.get(getMediaTest());
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());

      ArrayList<Record> records = getRecords(getMediaTest(), result);

      Record rec1 = getRecord(recDetail1PostgreSQL);
      Record rec2 = getRecord(recDetail2PostgreSQL);

      assertRecord(rec1, records.get(0));
      assertRecord(rec2, records.get(1));

    }
  }

}

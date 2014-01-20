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
package fr.cnes.sitools.izpack.model;

import com.izforge.izpack.installer.AutomatedInstallData;

/**
 * Model to store informations about JDBC connection
 * 
 * @author m.gond
 * 
 */
public class FileJDBCConnectionModel implements JDBCConnectionModel {
  /**
   * Identifier of the IzPack installer form fields
   */
  /** The db type */
  public static final String ID_DB_TYPE = "input.database.driver";
  /** The db host */
  public static final String ID_DB_FILE_PATH = "input.database.filepath";
  /** The db name */
  public static final String ID_DB_NAME = "input.database.name";
  /** The db schema */
  public static final String ID_DB_SCHEMA = "input.database.schema";
  /** The db user */
  public static final String ID_DB_USER = "db_user";
  /** The db password */
  public static final String ID_DB_PASSWORD = "db_pwd";

  /** The db url */
  private String dbUrl;
  /** The db username */
  private String dbUser;
  /** The db password */
  private String dbPassword;
  /** The db driver class name */
  private String dbDriverClassName;
  /** The db driver class name */
  private String dbType;
  /** The db url */
  private String dbSchema;

  /** The hsqldb */
  private final String hsqldbDriverClassName = "org.hsqldb.jdbcDriver";

  /**
   * Constructor with AutomatedInstallData IzPack data
   * 
   * @param aid
   *          the AutomatedInstallData
   */
  public FileJDBCConnectionModel(AutomatedInstallData aid) {
    String type = aid.getVariable(ID_DB_TYPE);
    String filePath = aid.getVariable(ID_DB_FILE_PATH);
    String name = aid.getVariable(ID_DB_NAME);
    String schema = aid.getVariable(ID_DB_SCHEMA);

    String user = aid.getVariable(ID_DB_USER);
    String password = aid.getVariable(ID_DB_PASSWORD);

    constructor(type, filePath, name, schema, user, password);
  }

  /**
   * Model to represent a connection to the database
   * 
   * @param type
   *          the type of the database (mysql or postgresql)
   * @param host
   *          the host of the database
   * @param port
   *          the port of the database
   * @param name
   *          the name of the database
   * @param schema
   *          the schema of the database
   * @param dbUser
   *          the user of the database
   * @param dbPassword
   *          the password of the database
   */
  public FileJDBCConnectionModel(String type, String filePath, String name, String schema, String dbUser,
      String dbPassword) {
    constructor(type, filePath, name, schema, dbUser, dbPassword);
  }

  /**
   * Model to represent a connection to the database
   * 
   * @param type
   *          the type of the database (mysql or postgresql)
   * @param host
   *          the host of the database
   * @param port
   *          the port of the database
   * @param name
   *          the name of the database
   * @param schema
   *          the schema of the database
   * @param dbUser
   *          the user of the database
   * @param dbPassword
   *          the password of the database
   */
  public void constructor(String type, String filePath, String name, String schema, String dbUser, String dbPassword) {
    this.dbUser = dbUser;
    this.dbPassword = dbPassword;

    String url = "jdbc:" + type + ":file://" + filePath + "/" + name;

    if ("hsqldb".equals(type)) {
      this.dbDriverClassName = this.hsqldbDriverClassName;
    }

    this.dbUrl = url;
    this.dbType = type;
    this.dbSchema = schema;
  }

  /**
   * Gets the dbUrl value
   * 
   * @return the dbUrl
   */
  public String getDbUrl() {
    return dbUrl;
  }

  /**
   * Sets the value of dbUrl
   * 
   * @param dbUrl
   *          the dbUrl to set
   */
  public void setDbUrl(String dbUrl) {
    this.dbUrl = dbUrl;
  }

  /**
   * Gets the dbUser value
   * 
   * @return the dbUser
   */
  public String getDbUser() {
    return dbUser;
  }

  /**
   * Sets the value of dbUser
   * 
   * @param dbUser
   *          the dbUser to set
   */
  public void setDbUser(String dbUser) {
    this.dbUser = dbUser;
  }

  /**
   * Gets the dbPassword value
   * 
   * @return the dbPassword
   */
  public String getDbPassword() {
    return dbPassword;
  }

  /**
   * Sets the value of dbPassword
   * 
   * @param dbPassword
   *          the dbPassword to set
   */
  public void setDbPassword(String dbPassword) {
    this.dbPassword = dbPassword;
  }

  /**
   * Gets the dbDriverClassName value
   * 
   * @return the dbDriverClassName
   */
  public String getDbDriverClassName() {
    return dbDriverClassName;
  }

  /**
   * Sets the value of dbDriverClassName
   * 
   * @param dbDriverClassName
   *          the dbDriverClassName to set
   */
  public void setDbDriverClassName(String dbDriverClassName) {
    this.dbDriverClassName = dbDriverClassName;
  }

  /**
   * Sets the value of dbType
   * 
   * @param dbType
   *          the dbType to set
   */
  public void setDbType(String dbType) {
    this.dbType = dbType;
  }

  /**
   * Gets the dbType value
   * 
   * @return the dbType
   */
  public String getDbType() {
    return dbType;
  }

  /**
   * Sets the value of dbSchema
   * 
   * @param dbSchema
   *          the dbSchema to set
   */
  public void setDbSchema(String dbSchema) {
    this.dbSchema = dbSchema;
  }

  /**
   * Gets the dbSchema value
   * 
   * @return the dbSchema
   */
  public String getDbSchema() {
    return dbSchema;
  }

  @Override
  public String getDbConnectionPassword() {
    // HSQLDB first connexion password is empty
    return "";
  }

}

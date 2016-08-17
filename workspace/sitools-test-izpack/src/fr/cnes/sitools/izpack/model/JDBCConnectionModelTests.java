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
package fr.cnes.sitools.izpack.model;

import com.izforge.izpack.installer.AutomatedInstallData;

/**
 * Model to store informations about JDBC connection
 * 
 * @author m.gond
 * 
 */
public class JDBCConnectionModelTests {
	/**
	 * Identifier of the IzPack installer form fields
	 */
	/** The db host */
	public static final String ID_DB_MYSQL_HOST = "input.database.hostname.mysql";
	/** The db port */
	public static final String ID_DB_MYSQL_PORT = "input.database.port.mysql";
	/** The db name */
	public static final String ID_DB_MYSQL_NAME = "input.database.name.mysql";
	/** The db user */
	public static final String ID_DB_MYSQL_USER = "db_user_mysql";
	/** The db password */
	public static final String ID_DB_MYSQL_PASSWORD = "db_pwd_mysql";
	/** The db host */
	public static final String ID_DB_PGSQL_HOST = "input.database.hostname.pgsql";
	/** The db port */
	public static final String ID_DB_PGSQL_PORT = "input.database.port.pgsql";
	/** The db name */
	public static final String ID_DB_PGSQL_NAME = "input.database.name.pgsql";
	/** The db user */
	public static final String ID_DB_PGSQL_USER = "db_user_pgsql";
	/** The db password */
	public static final String ID_DB_PGSQL_PASSWORD = "db_pwd_pgsql";
	/** The db schema */
	public static final String ID_DB_SCHEMA = "input.database.schema";

	/** The db url */
	private String dbMysqlName;
	/** The db url */
	private String dbPgsqlName;
	/** The db url */
	private String dbMysqlUrl;
	/** The db url */
	private String dbPgsqlUrl;
	/** The db username */
	private String dbMysqlUser;
	/** The db username */
	private String dbPgsqlUser;
	/** The db password */
	private String dbMysqlPassword;
	/** The db password */
	private String dbPgsqlPassword;
	/** The db driver class name */
	private String dbMysqlDriverClassName;
	/** The db driver class name */
	private String dbPgsqlDriverClassName;
	/** The db url */
	private String dbSchema;

	/** The postgresql */
	private final String postgresqlDriverClassName = "org.postgresql.Driver";
	/** The mysql */
	private final String mysqlDriverClassName = "org.gjt.mm.mysql.Driver";

	/**
	 * Constructor with AutomatedInstallData IzPack data
	 * 
	 * @param aid
	 *            the AutomatedInstallData
	 */
	public JDBCConnectionModelTests(AutomatedInstallData aid) {
		String myhost = aid.getVariable(ID_DB_MYSQL_HOST);
		String myport = aid.getVariable(ID_DB_MYSQL_PORT);
		String myname = aid.getVariable(ID_DB_MYSQL_NAME);
		String pghost = aid.getVariable(ID_DB_PGSQL_HOST);
		String pgport = aid.getVariable(ID_DB_PGSQL_PORT);
		String pgname = aid.getVariable(ID_DB_PGSQL_NAME);

		String schema = aid.getVariable(ID_DB_SCHEMA);

		String myuser = aid.getVariable(ID_DB_MYSQL_USER);
		String mypassword = aid.getVariable(ID_DB_MYSQL_PASSWORD);
		String pguser = aid.getVariable(ID_DB_PGSQL_USER);
		String pgpassword = aid.getVariable(ID_DB_PGSQL_PASSWORD);

		constructor(myhost, myport, myname, pghost, pgport, pgname, schema,
				myuser, mypassword, pguser, pgpassword);
	}

	/**
	 * Model to represent a connection to the database
	 * 
	 * @param type
	 *            the type of the database (mysql or postgresql)
	 * @param host
	 *            the host of the database
	 * @param port
	 *            the port of the database
	 * @param name
	 *            the name of the database
	 * @param schema
	 *            the schema of the database
	 * @param dbUser
	 *            the user of the database
	 * @param dbPassword
	 *            the password of the database
	 */
	public void constructor(String myhost, String myport, String myname,
			String pghost, String pgport, String pgname, String schema,
			String dbMyUser, String dbMyPassword, String dbPgUser,
			String dbPgPassword) {
		// this.dbMysqlName = "cnes-fuse";
		// this.dbPgsqlName = "CNES";
		this.dbMysqlName = myname;
		this.dbPgsqlName = pgname;
		this.dbMysqlUser = dbMyUser;
		this.dbPgsqlUser = dbPgUser;
		this.dbMysqlPassword = dbMyPassword;
		this.dbPgsqlPassword = dbPgPassword;

		String myurl = "jdbc:mysql://" + myhost + ":" + myport + "/"
				+ dbMysqlName + "?allowMultiQueries=true";
		String pgurl = "jdbc:postgresql://" + pghost + ":" + pgport + "/"
				+ dbPgsqlName;

		this.dbMysqlDriverClassName = this.mysqlDriverClassName;
		this.dbPgsqlDriverClassName = this.postgresqlDriverClassName;

		this.dbMysqlUrl = myurl;
		this.dbPgsqlUrl = pgurl;

		this.dbSchema = "fuse";
	}

	/**
	 * @return the dbMysqlUrl
	 */
	public String getDbMysqlUrl() {
		return dbMysqlUrl;
	}

	/**
	 * @param dbMysqlUrl
	 *            the dbMysqlUrl to set
	 */
	public void setDbMysqlUrl(String dbMysqlUrl) {
		this.dbMysqlUrl = dbMysqlUrl;
	}

	/**
	 * @return the dbPgsqlUrl
	 */
	public String getDbPgsqlUrl() {
		return dbPgsqlUrl;
	}

	/**
	 * @return the dbPgsqlUrl
	 */
	public String getDbPgsqlUrlWithSchema(String schema) {
		return dbPgsqlUrl + "?schema=" + schema;
	}

	/**
	 * @param dbPgsqlUrl
	 *            the dbPgsqlUrl to set
	 */
	public void setDbPgsqlUrl(String dbPgsqlUrl) {
		this.dbPgsqlUrl = dbPgsqlUrl;
	}

	/**
	 * @return the dbMysqlUser
	 */
	public String getDbMysqlUser() {
		return dbMysqlUser;
	}

	/**
	 * @param dbMysqlUser
	 *            the dbMysqlUser to set
	 */
	public void setDbMysqlUser(String dbMysqlUser) {
		this.dbMysqlUser = dbMysqlUser;
	}

	/**
	 * @return the dbPgsqlUser
	 */
	public String getDbPgsqlUser() {
		return dbPgsqlUser;
	}

	/**
	 * @param dbPgsqlUser
	 *            the dbPgsqlUser to set
	 */
	public void setDbPgsqlUser(String dbPgsqlUser) {
		this.dbPgsqlUser = dbPgsqlUser;
	}

	/**
	 * @return the dbMysqlPassword
	 */
	public String getDbMysqlPassword() {
		return dbMysqlPassword;
	}

	/**
	 * @param dbMysqlPassword
	 *            the dbMysqlPassword to set
	 */
	public void setDbMysqlPassword(String dbMysqlPassword) {
		this.dbMysqlPassword = dbMysqlPassword;
	}

	/**
	 * @return the dbPgsqlPassword
	 */
	public String getDbPgsqlPassword() {
		return dbPgsqlPassword;
	}

	/**
	 * @param dbPgsqlPassword
	 *            the dbPgsqlPassword to set
	 */
	public void setDbPgsqlPassword(String dbPgsqlPassword) {
		this.dbPgsqlPassword = dbPgsqlPassword;
	}

	/**
	 * @return the dbMysqlDriverClassName
	 */
	public String getDbMysqlDriverClassName() {
		return dbMysqlDriverClassName;
	}

	/**
	 * @param dbMysqlDriverClassName
	 *            the dbMysqlDriverClassName to set
	 */
	public void setDbMysqlDriverClassName(String dbMysqlDriverClassName) {
		this.dbMysqlDriverClassName = dbMysqlDriverClassName;
	}

	/**
	 * @return the dbPgsqlDriverClassName
	 */
	public String getDbPgsqlDriverClassName() {
		return dbPgsqlDriverClassName;
	}

	/**
	 * @param dbPgsqlDriverClassName
	 *            the dbPgsqlDriverClassName to set
	 */
	public void setDbPgsqlDriverClassName(String dbPgsqlDriverClassName) {
		this.dbPgsqlDriverClassName = dbPgsqlDriverClassName;
	}

	/**
	 * @return the dbSchema
	 */
	public String getDbSchema() {
		return dbSchema;
	}

	/**
	 * @param dbSchema
	 *            the dbSchema to set
	 */
	public void setDbSchema(String dbSchema) {
		this.dbSchema = dbSchema;
	}

	public void setDbMysqlName(String dbMysqlName) {
		this.dbMysqlName = dbMysqlName;
	}

	public String getDbMysqlName() {
		return dbMysqlName;
	}

	public void setDbPgsqlName(String dbPgsqlName) {
		this.dbPgsqlName = dbPgsqlName;
	}

	public String getDbPgsqlName() {
		return dbPgsqlName;
	}

}

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
package fr.cnes.sitools.izpack.actions;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;

import com.izforge.izpack.event.SimpleInstallerListener;
import com.izforge.izpack.installer.AutomatedInstallData;
import com.izforge.izpack.installer.InstallerException;
import com.izforge.izpack.installer.ScriptParser;
import com.izforge.izpack.util.AbstractUIProgressHandler;

import fr.cnes.sitools.izpack.model.JDBCConnectionModel;
import fr.cnes.sitools.izpack.model.JDBCConnectionModelFactory;

public class InstallDatabaseAction extends SimpleInstallerListener {

  /** List of MySQL files for user database */
  private ArrayList<String> listMySQLFiles;

  /** List of Postgresql files for user database */
  private ArrayList<String> listPostgreSQLFiles;

  /** List of HSQLDB files for user database */
  private ArrayList<String> listHSQLDBFiles;

  /**
   * The model of the connection model
   */
  private JDBCConnectionModel jdbcModel;

  public InstallDatabaseAction() throws Exception {
    super();
    listMySQLFiles = new ArrayList<String>();
    listMySQLFiles.add("database/MYSQL_CNES/cnes_GROUPS.sql");
    listMySQLFiles.add("database/MYSQL_CNES/cnes_USER_GROUP.sql");
    listMySQLFiles.add("database/MYSQL_CNES/cnes_USER_PROPERTIES.sql");
    listMySQLFiles.add("database/MYSQL_CNES/cnes_USERS.sql");

    // List of Postgresql files for user database
    listPostgreSQLFiles = new ArrayList<String>();
    listPostgreSQLFiles.add("database/PGSQL/pgsql_sitools.sql");

    // List of HSQLDB files for user database
    listHSQLDBFiles = new ArrayList<String>();
    listHSQLDBFiles.add("database/HSQLDB/SITOOLS_CREATE_SCHEMA.sql");
    listHSQLDBFiles.add("database/HSQLDB/SITOOLS_CREATE_TABLES.sql");
    listHSQLDBFiles.add("database/HSQLDB/SITOOLS_INSERT_DATA.sql");
  }

  @Override
  public void afterPacks(AutomatedInstallData arg0, AbstractUIProgressHandler arg1) throws Exception {
    super.afterPacks(arg0, arg1);
    boolean installDBSelected = Boolean.parseBoolean(getInstalldata().getVariable("dbInstallSelected"));

    if (!installDBSelected) {
      return;
    }

    ArrayList<String> fileList = prepareInstallation();
    installDatabase(jdbcModel, fileList);
  }

  /**
   * Map the ProcessingClient parameters to the class properties.
   * 
   * @param aid
   *          the data
   */
  private ArrayList<String> prepareInstallation() {

    jdbcModel = JDBCConnectionModelFactory.getModel(getInstalldata());

    ArrayList<String> fileList;

    if (jdbcModel.getDbType().equals("mysql")) {
      fileList = this.listMySQLFiles;
    }
    else if (jdbcModel.getDbType().equals("postgresql")) {
      fileList = this.listPostgreSQLFiles;
    }
    else {
      fileList = this.listHSQLDBFiles;
    }

    return fileList;
  }

  /**
   * Install database
   * 
   * @param jdbcModel
   *          the JDBC model
   * @param fileList
   *          the file list
   * @throws Exception
   *           when occurs
   */
  private void installDatabase(JDBCConnectionModel jdbcModel, ArrayList<String> fileList) throws Exception {
    Connection cnx = null;
    Statement stat = null;
    PrintStream out = System.out;
    String installPath = getInstalldata().getVariable(ScriptParser.INSTALL_PATH);
    try {
      do {
        out.println("Test jdbc data source connection ..." + jdbcModel.getDbUrl());

        Class.forName(jdbcModel.getDbDriverClassName());
        out.println("Load driver class : OK");

        out.println("Get connection ");

        cnx = DriverManager.getConnection(jdbcModel.getDbUrl(), jdbcModel.getDbUser(), jdbcModel.getDbConnectionPassword());

        out.println("Loop through the files");
        String ligne;
        String request;
        for (Iterator<String> iterator = fileList.iterator(); iterator.hasNext();) {

          String fileName = installPath + "/" + iterator.next();

          out.println("File :  " + fileName);

          InputStream ips = new FileInputStream(fileName);
          InputStreamReader ipsr = new InputStreamReader(ips);
          BufferedReader br = new BufferedReader(ipsr);
          request = "";

          StringBuilder stringBuilder = new StringBuilder();
          String ls = System.getProperty("line.separator");
          while ((ligne = br.readLine()) != null) {
            stringBuilder.append(ligne);
            stringBuilder.append(ls);
          }
          request = stringBuilder.toString();
          br.close();

          out.flush();
          stringBuilder = null;

          try {
            // stat = cnx.prepareStatement(request);
            cnx.setAutoCommit(false);
            stat = cnx.createStatement();
            stat.execute(request);
            // stat.execute();
            cnx.commit();
            stat.close();

          }
          catch (Exception e) {
            throw new InstallerException("Warning there was an error while installing the databases :\n "
                + e.getLocalizedMessage(), e);
          }
          out.println("Execute statement on connection : OK");
        }
      } while (false);
    }
    catch (Exception e) {
      throw e;
    }
    finally {
      if (stat != null) {
        try {
          stat.close();
        }
        catch (SQLException e) {
          throw e;
        }
      }
      if (cnx != null) {
        try {
          cnx.close();
        }
        catch (SQLException e) {
          throw e;
        }
      }
    }
  }
}

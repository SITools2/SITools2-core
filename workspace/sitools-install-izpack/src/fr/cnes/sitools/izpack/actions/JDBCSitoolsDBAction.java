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
import java.io.File;
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

import com.izforge.izpack.Pack;
import com.izforge.izpack.PackFile;
import com.izforge.izpack.event.SimpleInstallerListener;
import com.izforge.izpack.installer.AutomatedInstallData;
import com.izforge.izpack.installer.InstallerException;
import com.izforge.izpack.installer.ScriptParser;
import com.izforge.izpack.util.AbstractUIProgressHandler;

import fr.cnes.sitools.izpack.model.JDBCConnectionModel;

/**
 * IzPack action to create the user databases
 * 
 * @author m.gond
 * 
 */
public class JDBCSitoolsDBAction extends SimpleInstallerListener {

  /** List of MySQL files */
  private ArrayList<String> listMySQLFiles;
  /** List of Postgresql files */
  private ArrayList<String> listPostgreSQLFiles;

  /**
   * Default constructor
   */
  public JDBCSitoolsDBAction() {
    super();

    listMySQLFiles = new ArrayList<String>();
    listMySQLFiles.add("database/MYSQL_CNES/cnes_GROUPS.sql");
    listMySQLFiles.add("database/MYSQL_CNES/cnes_USER_GROUP.sql");
    listMySQLFiles.add("database/MYSQL_CNES/cnes_USER_PROPERTIES.sql");
    listMySQLFiles.add("database/MYSQL_CNES/cnes_USERS.sql");

    listPostgreSQLFiles = new ArrayList<String>();
    listPostgreSQLFiles.add("database/PGSQL/pgsql_sitools.sql");

  }

  @Override
  public void afterDir(File file, PackFile arg1) throws Exception {
    super.afterDir(file, arg1);
  }

  @Override
  public void beforeFile(File file, PackFile arg1) throws Exception {
    super.beforeFile(file, arg1);
  }

  @Override
  public void afterInstallerInitialization(AutomatedInstallData arg0) {
    super.afterInstallerInitialization(arg0);
  }

  @Override
  public void afterPack(Pack pack, Integer arg1, AbstractUIProgressHandler arg2) throws Exception {
    super.afterPack(pack, arg1, arg2);

    PrintStream out = System.out;
    if (!"database".equals(pack.name)) {
      return;
    }

    JDBCConnectionModel jdbcModel = new JDBCConnectionModel(getInstalldata());

    String installPath = getInstalldata().getVariable(ScriptParser.INSTALL_PATH);

    ArrayList<String> fileList;
    if (jdbcModel.getDbType().equals("mysql")) {
      fileList = this.listMySQLFiles;
    }
    else {
      fileList = this.listPostgreSQLFiles;
    }

    Connection cnx = null;
    Statement stat = null;

    try {
      do {
        out.println("Test jdbc data source connection ...");

        Class.forName(jdbcModel.getDbDriverClassName());
        out.println("Load driver class : OK");

        out.println("Get connection ");

        cnx = DriverManager.getConnection(jdbcModel.getDbUrl(), jdbcModel.getDbUser(), jdbcModel.getDbPassword());

        out.println("Loop through the files");
        String ligne;
        String request;
        for (Iterator<String> iterator = fileList.iterator(); iterator.hasNext();) {
          String fileName = installPath + "/" + iterator.next();

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

          request = request.replace("$" + JDBCConnectionModel.ID_DB_SCHEMA, jdbcModel.getDbSchema());
          request = request.replace("$" + JDBCConnectionModel.ID_DB_USER, jdbcModel.getDbUser());

          out.flush();

          try {
            stat = cnx.createStatement();
            stat.execute(request);
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

  @Override
  public void afterPacks(AutomatedInstallData arg0, AbstractUIProgressHandler arg1) throws Exception {
    super.afterPacks(arg0, arg1);
  }

  @Override
  public void beforeDir(File arg0, PackFile arg1) throws Exception {
    super.beforeDir(arg0, arg1);
  }

  @Override
  public void afterFile(File file, PackFile arg1) throws Exception {
    super.afterFile(file, arg1);

  }

  @Override
  public void beforePack(Pack pack, Integer arg1, AbstractUIProgressHandler arg2) throws Exception {
    super.beforePack(pack, arg1, arg2);
  }

  @Override
  public void beforePacks(AutomatedInstallData arg0, Integer arg1, AbstractUIProgressHandler arg2) throws Exception {
    super.beforePacks(arg0, arg1, arg2);
  }

  @Override
  public boolean isFileListener() {
    return true;
  }

}

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
package fr.cnes.sitools.izpack.panel;

import java.awt.LayoutManager2;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

import javax.swing.JButton;
import javax.swing.JLabel;

import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.datatype.IDataTypeFactory;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.ext.mysql.MySqlDataTypeFactory;
import org.dbunit.ext.postgresql.PostgresqlDataTypeFactory;
import org.dbunit.operation.DatabaseOperation;

import com.izforge.izpack.gui.ButtonFactory;
import com.izforge.izpack.gui.IzPanelLayout;
import com.izforge.izpack.gui.LabelFactory;
import com.izforge.izpack.installer.InstallData;
import com.izforge.izpack.installer.InstallerException;
import com.izforge.izpack.installer.InstallerFrame;
import com.izforge.izpack.installer.IzPanel;
import com.izforge.izpack.installer.ScriptParser;

import fr.cnes.sitools.izpack.model.JDBCConnectionModelTests;
import fr.cnes.sitools.izpack.model.SchemaExportFileModel;

/**
 * Custom panel to create database
 * 
 * @author m.gond (AKKA Technologies)
 * 
 */
public class DatabasePanel extends IzPanel implements ActionListener {

  /**
   * serialVersionUID
   */
  private static final long serialVersionUID = 1L;
  /** List of MySQL files to create tables */
  private ArrayList<String> listMySQLFiles;
  /** List of Postgresql files to create tables */
  private ArrayList<String> listPostgreSQLFiles;

  /** List of MySQL files to fill tables */
  private ArrayList<String> listMySQLExportFiles;
  /** List of Postgresql files to fill tables */
  private ArrayList<SchemaExportFileModel> listPostgreSQLexportFiles;

  /** buttonUser */
  private JButton buttonTests;

  /** CORE path */
  private static final String CORE_PATH = "/workspace/fr.cnes.sitools.core/test/";

  /**
   * Default constructor
   * 
   * @param parent
   *          the parent Frame
   * @param idata
   *          the data
   */
  public DatabasePanel(InstallerFrame parent, InstallData idata) {
    this(parent, idata, new IzPanelLayout());
    // List of MySQL files for user database
    listMySQLFiles = new ArrayList<String>();
    listMySQLFiles.add(CORE_PATH + "res/create/mysql/cnes-fuse_HEADERS.sql");
    listMySQLFiles.add(CORE_PATH + "res/create/mysql/cnes-fuse_IAPDATASETS.sql");
    listMySQLFiles.add(CORE_PATH + "res/create/mysql/cnes-fuse_OBJECT_CLASS.sql");
    listMySQLFiles.add(CORE_PATH + "res/create/mysql/cnes-fuse_FUSE_PRG_ID.sql");
    listMySQLFiles.add(CORE_PATH + "res/create/mysql/cnes_GROUPS.sql");
    listMySQLFiles.add(CORE_PATH + "res/create/mysql/cnes_USER_GROUP.sql");
    listMySQLFiles.add(CORE_PATH + "res/create/mysql/cnes_USER_PROPERTIES.sql");
    listMySQLFiles.add(CORE_PATH + "res/create/mysql/cnes_USERS.sql");
    listMySQLFiles.add(CORE_PATH + "res/create/mysql/cnes_VIEW_HEADERS.sql");
    listMySQLFiles.add(CORE_PATH + "res/create/mysql/cnes_VIEW_HEADERS_BIS.sql");
    listMySQLFiles.add(CORE_PATH + "res/create/mysql/cnes-test_TABLE_TESTS.sql");
    // List of Postgresql files for user database
    listPostgreSQLFiles = new ArrayList<String>();
    listPostgreSQLFiles.add(CORE_PATH + "res/create/postgresql/pgsql_fuse.sql");
    listPostgreSQLFiles.add(CORE_PATH + "res/create/postgresql/pgsql_sitools.sql");
    listPostgreSQLFiles.add(CORE_PATH + "res/create/postgresql/pgsql_test.sql");

    // list of MySQL export files
    listMySQLExportFiles = new ArrayList<String>();
    listMySQLExportFiles.add(CORE_PATH + "res/export/mysql/export_fuse_mysql.xml");
    

    // list of MySQL export files
    listPostgreSQLexportFiles = new ArrayList<SchemaExportFileModel>();
    listPostgreSQLexportFiles.add(new SchemaExportFileModel("fuse", CORE_PATH
        + "res/export/postgresql/export_fuse_pg.xml"));
  }

  /**
   * The constructor with Layout parameter
   * 
   * @param parent
   *          the parent panel
   * @param idata
   *          the data
   * @param layout
   *          the layout
   */
  public DatabasePanel(InstallerFrame parent, InstallData idata, LayoutManager2 layout) {
    super(parent, idata, layout);

    JLabel label = LabelFactory.create(parent.langpack.getString("panel.databasePanel.label"),
        parent.icons.getImageIcon("host"), LEADING);
    add(label, NEXT_LINE);

    add(IzPanelLayout.createVerticalStrut(15));

    buttonTests = ButtonFactory.createButton(parent.langpack.getString("panel.databasePanel.tests.button"),
        parent.icons.getImageIcon("edit"), idata.buttonsHColor);
    buttonTests.setToolTipText(parent.langpack.getString("panel.databasePanel.tests.button.tip"));
    buttonTests.setActionCommand("user");
    buttonTests.addActionListener(this);

    add(buttonTests, NEXT_LINE);
    add(IzPanelLayout.createVerticalStrut(15));

    // At end of layouting we should call the completeLayout method also
    // they do
    // nothing.
    getLayoutHelper().completeLayout();

  }

  /**
   * Install the user databases
   * 
   * @param idata
   *          the data
   * @throws Exception
   *           if something is wrong
   */
  public void installMySQLDatabaseForTests(InstallData idata) throws Exception {

    JDBCConnectionModelTests jdbcModel = new JDBCConnectionModelTests(idata);
    installMysqlDatabase(jdbcModel, this.listMySQLFiles);
    installPgsqlDatabase(jdbcModel, this.listPostgreSQLFiles);
    this.emitNotification(parent.langpack.getString("database.user.create.successfully"));
    buttonTests.setEnabled(false);
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
  private void installMysqlDatabase(JDBCConnectionModelTests jdbcModel, ArrayList<String> fileList) throws Exception {
    Connection cnx = null;
    Statement stat = null;
    PrintStream out = System.out;
    String installPath = idata.getVariable(ScriptParser.INSTALL_PATH);
    try {
      out.println("Test jdbc data source connection ...");

      Class.forName(jdbcModel.getDbMysqlDriverClassName());
      out.println("Load driver class : OK");

      out.println("Get connection ");

      cnx = DriverManager.getConnection(jdbcModel.getDbMysqlUrl(), jdbcModel.getDbMysqlUser(),
          jdbcModel.getDbMysqlPassword());

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
      for (Iterator<String> iterator = this.listMySQLExportFiles.iterator(); iterator.hasNext();) {
        String mysqlFile = iterator.next();
        // Now putting data in there ...
        IDataSet dataset = new FlatXmlDataSetBuilder().build(new FileInputStream(this.idata.getInstallPath() + "/"
            + mysqlFile));
        IDatabaseConnection connect = new DatabaseConnection(cnx);
        connect.getConfig().setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new MySqlDataTypeFactory());
        DatabaseOperation.CLEAN_INSERT.execute(connect, dataset);
      }
    }
    catch (Exception e) {
      e.printStackTrace();
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
  private void installPgsqlDatabase(JDBCConnectionModelTests jdbcModel, ArrayList<String> fileList) throws Exception {
    Connection cnx = null;
    Connection newcnx = null;
    Statement stat = null;
    PrintStream out = System.out;
    String installPath = idata.getVariable(ScriptParser.INSTALL_PATH);
    try {
      out.println("Test jdbc data source connection ...");

      Class.forName(jdbcModel.getDbPgsqlDriverClassName());
      out.println("Load driver class : OK");

      out.println("Get connection ");

      cnx = DriverManager.getConnection(jdbcModel.getDbPgsqlUrl(), jdbcModel.getDbPgsqlUser(),
          jdbcModel.getDbPgsqlPassword());

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

      for (Iterator<SchemaExportFileModel> iterator = this.listPostgreSQLexportFiles.iterator(); iterator.hasNext();) {

        SchemaExportFileModel export = iterator.next();
        // Now putting data in there ...
        FlatXmlDataSetBuilder builder = new FlatXmlDataSetBuilder();
        builder.setColumnSensing(true);

        IDataSet dataset = builder.build(new FileInputStream(this.idata.getInstallPath() + "/" + export.getFileName()));

        newcnx = DriverManager.getConnection(jdbcModel.getDbPgsqlUrl(), jdbcModel.getDbPgsqlUser(),
            jdbcModel.getDbPgsqlPassword());

        newcnx.setAutoCommit(true);

        IDatabaseConnection connect = new DatabaseConnection(newcnx, export.getSchema());

        IDataTypeFactory factory = new PostgresqlDataTypeFactory();

        connect.getConfig().setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, factory);
        // connect.getConfig().setProperty(DatabaseConfig.FEATURE_CASE_SENSITIVE_TABLE_NAMES, true);
        // connect.getConfig().setProperty(DatabaseConfig.FEATURE_QUALIFIED_TABLE_NAMES, true);

        DatabaseOperation.CLEAN_INSERT.execute(connect, dataset);

        newcnx.close();
      }

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
          if (newcnx != null) {
            newcnx.close();
          }
        }
        catch (SQLException e) {
          throw e;
        }
      }
    }

  }

  /*
   * (non-Javadoc)
   * 
   * @see com.izforge.izpack.installer.IzPanel#panelActivate()
   */
  @Override
  public void panelActivate() {
    // TODO Auto-generated method stub
    super.panelActivate();
    this.parent.lockPrevButton();
  }

  /**
   * Indicates wether the panel has been validated or not.
   * 
   * @return Always true.
   */
  public boolean isValidated() {
    return true;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    if (e.getActionCommand().equals("user")) {
      try {
        installMySQLDatabaseForTests(this.idata);
      }
      catch (Exception ex) {
        ex.printStackTrace();
        emitError("Database Error", ex.getMessage());
      }
    }
    else {
      emitNotification("TODO");
      // try {
      // installTestsDatabase(this.idata);
      // }
      // catch (Exception ex) {
      // emitError("title.database.error", ex.getMessage());
      // }

    }
  }

}

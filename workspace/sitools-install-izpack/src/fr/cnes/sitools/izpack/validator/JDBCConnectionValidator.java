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
package fr.cnes.sitools.izpack.validator;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;

import com.izforge.izpack.installer.AutomatedInstallData;
import com.izforge.izpack.installer.DataValidator;

import fr.cnes.sitools.izpack.model.JDBCConnectionModel;
import fr.cnes.sitools.izpack.model.JDBCConnectionModelFactory;

/**
 * IzPackValidator for JDBCConnexion Validate that the connection settings
 * entered by the user are good and that the database can be accessed
 * 
 * @author m.gond (AKKA Technologies)
 * 
 */
public class JDBCConnectionValidator implements DataValidator {
  /**
   * The model of the connection model
   */
  private JDBCConnectionModel jdbcModel;

  /**
   * Constants for JDBC driver classes
   */

  /** Contains the error trace */
  private ArrayList<String> trace;

  /**
   * Validate the data
   * 
   * @param aid
   *          the data
   * @return a Status telling whether it is OK or ERROR
   */
  @Override
  public Status validateData(AutomatedInstallData aid) {
    trace = new ArrayList<String>();
    this.mapParams(aid);
    if (jdbcModel.getDbType().equals("hsqldb")) {
      return Status.OK;
    }
    return testDatasource();

  }

  @Override
  public boolean getDefaultAnswer() {
    return false;
  }

  @Override
  public String getErrorMessageId() {
    String errorMsg = "";
    for (Iterator<String> iterator = trace.iterator(); iterator.hasNext();) {
      String err = iterator.next();
      errorMsg += err + "\n";
    }
    return errorMsg;

  }

  @Override
  public String getWarningMessageId() {
    return "input.warning.database";
  }

  /**
   * Test the datasource with the class parameters
   * 
   * @return true if the datasource is correctly configurate, false otherwise
   */
  private Status testDatasource() {
    Connection cnxUsr = null;
    Statement statUsr = null;

    Status result = Status.OK;
    try {
      do {
        trace.add("Test jdbc data source connection for user ...");
        try {
          Class.forName(jdbcModel.getDbDriverClassName());
          trace.add("Load driver class : OK");
        }
        catch (Exception e) {
          result = Status.ERROR;
          trace.add("Load driver class failed. Cause: " + e.getMessage());
          break;
        }

        try {
          cnxUsr = DriverManager.getConnection(jdbcModel.getDbUrl(), jdbcModel.getDbUser(), jdbcModel.getDbPassword());
        }
        catch (Exception e) {
          result = Status.ERROR;
          trace.add("Get connection failed. Cause: " + e.getMessage());
          break;
        }

        try {
          statUsr = cnxUsr.createStatement();
          statUsr.executeQuery("SELECT 1");
          trace.add("Execute statement on connection : OK");

          if (jdbcModel.getDbType().equals("postgresql")) {
            statUsr = cnxUsr.createStatement();
            ResultSet rs = statUsr
                .executeQuery("select exists (select * from pg_catalog.pg_namespace where nspname = '"
                    + jdbcModel.getDbSchema() + "');");

            rs.next();
            boolean exists = rs.getBoolean(1);
            if (exists) {
              result = Status.WARNING;
            }
          }
        }
        catch (Exception e) {
          result = Status.ERROR;
          trace.add("Execute statement on connection failed. Cause: " + e.getMessage());
          break;
        }

      } while (false);

    }
    finally {
      if (statUsr != null) {
        try {
          statUsr.close();
        }
        catch (SQLException e) {

        }
      }
      if (cnxUsr != null) {
        try {
          cnxUsr.close();
        }
        catch (SQLException e) {

        }
      }
    }
    return result;
  }

  /**
   * Map the ProcessingClient parameters to the class properties.
   * 
   * @param aid
   *          the data
   */
  private void mapParams(AutomatedInstallData aid) {
    jdbcModel = JDBCConnectionModelFactory.getModel(aid);

  }
}

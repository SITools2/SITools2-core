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
package fr.cnes.sitools.izpack;

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
import java.util.List;

import com.izforge.izpack.installer.InstallerException;

public class HSQLDBTest {

  public static void main(String[] args) throws Exception {

    // List of HSQLDB files for user database
    List<String> fileList = new ArrayList<String>();
    fileList.add("database/HSQLDB/SITOOLS_CREATE_SCHEMA.sql");
    fileList.add("database/HSQLDB/SITOOLS_CREATE_TABLES.sql");
    fileList.add("database/HSQLDB/SITOOLS_INSERT_DATA.sql");

    Connection cnx = null;
    Statement stat = null;
    PrintStream out = System.out;
    String installPath = "D:\\CNES-ULISSE-2.0-GIT";
    try {
      do {
        out.println("Test jdbc data source connection ...");

        Class.forName("org.hsqldb.jdbcDriver");
        out.println("Load driver class : OK");

        out.println("Get connection ");

        cnx = DriverManager.getConnection("jdbc:hsqldb:file://D:\\CNES-ULISSE-2.0-GIT/data/HSQLDB/CNES", "sitools", "");

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

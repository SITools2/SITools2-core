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
package test;

import org.testng.annotations.Test;

import primitive.Datasource;
import primitive.Menu;
import primitive.SEL;
import primitive.SELTestCase;

public class ADM321 extends SELTestCase {
  /**
   * Javascript needed to execute in order to select row in the grid
   * 
   * @param gridId
   *          Grid id
   * @param rowIndex
   *          Index of the row to select
   * @return Javascript to select row
   */
  public static String selectLastRow() {
    return "Ext.getCmp(ID.BOX.DATABASE).getSelectionModel().selectLastRow()";
  }

  @Test(dependsOnGroups = { "initClientAdmin.*" })
  public void selectUsers() throws Exception {
    Menu.select("DatabaseNodeId");
    SEL.sleep(1000);
  }

  @Test(dependsOnGroups = { "initClientAdmin.*" }, dependsOnMethods = { "selectUsers" })
  public void datasource() throws Exception {
    String url = "jdbc:postgresql://localhost:5432/CNES";
	Datasource.createDatasource(url, "PGSelenium", "/postgres", "PostgreSQL");
    // assertTrue (selenium.isTextPresent("jdbc:"));
    SEL.sleep(1000);
    selenium.runScript(selectLastRow());
    Datasource.modifyDatasource();
    // // TODO : ne passe pas avec les bibliotheques actuelles
    Datasource.deleteDatasource("PGSelenium");

  }
}
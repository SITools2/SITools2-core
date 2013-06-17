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

import primitive.Menu;
import primitive.Role;
import primitive.SEL;
import primitive.SELTestCase;

@Test
public class ADMTestRoles extends SELTestCase {

  /**
   * Javascript needed to execute in order to select row in the grid
   * 
   * @param gridId
   *          Grid id
   * @param rowIndex
   *          Index of the row to select
   * @return Javascript to select row
   */
  public static String selectGridRow() {
    return "Ext.getCmp(ID.BOX.ROLE).getSelectionModel().selectLastRow()";
  }

  @Test(dependsOnGroups = { "initClientAdmin.*" })
  public void selectRoles() throws Exception {
    Menu.select("roleNodeId");
    SEL.sleep(1000);
  }

  @Test(dependsOnGroups = { "initClientAdmin.*" }, dependsOnMethods = { "selectRoles" })
  public void roles() throws Exception {
    Role.createRole("testRoleName", "testRoleDescription");
    Role.createRoleAndCancel("badName", "badDescription");
    // assertTrue (selenium.isTextPresent("testFirstName"));
    SEL.sleep(1000);
    selenium.runScript(selectGridRow());
    // TODO : ne passe pas avec les bibliotheques actuelles
    // assertTrue (selenium.isTextPresent("testRoleName"));
    // assertTrue (selenium.isTextPresent("testRoleDescription"));
    Role.modifyRole("testNameMod", "testDescrMod");
    // TODO : ne passe pas avec les bibliotheques actuelles
    // assertTrue (selenium.isTextPresent("testNameMod"));
    // assertTrue (selenium.isTextPresent("testDescrMod"));
    selenium.runScript(selectGridRow());
    Role.deleteRole();
    Role.deleteAndCancelRole();
  }

}

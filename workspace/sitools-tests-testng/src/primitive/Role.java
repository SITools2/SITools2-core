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
package primitive;

import com.thoughtworks.selenium.Selenium;

public class Role {
  static Selenium selenium = SEL.getSelenium();

  public static void createRole(String roleName, String roleDescription) throws Exception {
    SEL.waitPresence("xpath=//button[contains(.,'Create')]");
    selenium.click("xpath=//button[contains(.,'Create')]");

    SEL.waitPresence("xpath=//input[@name='name']");
    selenium.type("xpath=//input[@name='name']", roleName);
    selenium.type("xpath=//input[@name='description']", roleDescription);
    selenium.click("xpath=//table[@id='okButtonId']");
    SEL.sleep(1000);

  }

  public static void modifyRole(String roleName, String roleDescription) throws Exception {
    SEL.waitPresence("xpath=//button[contains(.,'Create')]");
    selenium.click("xpath=//button[contains(.,'Edit')]");

    SEL.waitPresence("xpath=//input[@name='name']");
    selenium.type("xpath=//input[@name='name']", roleName);
    selenium.type("xpath=//input[@name='description']", roleDescription);
    selenium.click("xpath=//table[@id='okButtonId']");

    SEL.sleep(1000);

  }

  public static void deleteRole() throws Exception {
    SEL.waitPresence("xpath=//button[contains(.,'Create')]");
    selenium.click("xpath=//button[contains(.,'Delete')]");

    SEL.waitPresence("xpath=//button[contains(.,'Yes')]");
    selenium.click("xpath=//button[contains(.,'Yes')]");

    SEL.sleep(1000);

  }

  public static void createRoleAndCancel(String name, String description) throws Exception {

    SEL.waitPresence("xpath=//button[contains(.,'Create')]");
    selenium.click("xpath=//button[contains(.,'Create')]");

    SEL.waitPresence("xpath=//input[@name='name']");
    selenium.type("xpath=//input[@name='name']", name);
    selenium.type("xpath=//input[@name='description']", description);
    selenium.click("xpath=//table[@id='cancelButtonId']");
    SEL.sleep(1000);
  }

  public static void deleteAndCancelRole() throws Exception {
    SEL.waitPresence("xpath=//button[contains(.,'Create')]");
    selenium.click("xpath=//button[contains(.,'Delete')]");

    SEL.waitPresence("xpath=//button[contains(.,'Yes')]");
    selenium.click("xpath=//button[contains(.,'No')]");

    SEL.sleep(1000);

  }

}

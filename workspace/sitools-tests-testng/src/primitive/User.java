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
package primitive;

import com.thoughtworks.selenium.Selenium;

public class User {
  static Selenium selenium = SEL.getSelenium();

  public static void createUser(String firstName, String lastName, String email, String login, String secret)
      throws Exception {
    SEL.waitPresence("xpath=//button[contains(.,'Create')]");
    selenium.click("xpath=//button[contains(.,'Create')]");

    SEL.waitPresence("xpath=//input[@name='firstName']");
    selenium.type("xpath=//input[@name='firstName']", firstName);
    selenium.type("xpath=//input[@name='lastName']", lastName);
    selenium.type("xpath=//input[@name='email']", email);
    selenium.type("xpath=//input[@name='identifier']", login);
    selenium.type("xpath=//input[@name='secret']", secret);
    selenium.type("confirmSecret", secret);
    selenium.click("xpath=//button[contains(.,'OK')]");

    SEL.sleep(1000);

    // selenium.getSelectedValue("toto");
  }

  public static void modifyUser(String firstName, String lastName, String email, String login, String secret)
      throws Exception {
    SEL.waitPresence("xpath=//button[contains(.,'Create')]");
    selenium.click("xpath=//button[contains(.,'Edit')]");

    SEL.waitPresence("xpath=//input[@name='firstName']");
    selenium.type("xpath=//input[@name='firstName']", firstName);
    selenium.type("xpath=//input[@name='lastName']", lastName);
    selenium.type("xpath=//input[@name='email']", email);
    selenium.type("xpath=//input[@name='identifier']", login);
    selenium.type("xpath=//input[@name='secret']", secret);
    selenium.type("confirmSecret", secret);
    selenium.click("xpath=//button[contains(.,'OK')]");

    SEL.sleep(1000);

    // selenium.getSelectedValue("toto");
  }

  public static void deleteUser() throws Exception {
    SEL.waitPresence("xpath=//button[contains(.,'Create')]");
    selenium.click("xpath=//button[contains(.,'Delete')]");

    SEL.waitPresence("xpath=//button[contains(.,'Yes')]");
    selenium.click("xpath=//button[contains(.,'Yes')]");

    SEL.sleep(1000);

    // selenium.getSelectedValue("toto");
  }
}

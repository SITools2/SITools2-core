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
package test;

import org.testng.annotations.Test;

import primitive.Login;
import primitive.SEL;
import primitive.SELTestCase;

/**
 * 
 * 
 *
 */
public class AdminLogin extends SELTestCase {

  /**
   * 
   *
   */
  @Test(groups = { "initClientAdmin" })
  public void load() throws Exception {
    selenium.open("/sitools/client-admin/");
    selenium.waitForPageToLoad("10000");
    
//    selenium.setSpeed("1000");
    
    SEL.sleep(1000);
  }

  /**
   * 
   *
   */
  @Test(groups = { "initClientAdmin" }, dependsOnMethods = { "load" })
  public void login() throws Exception {
    Login.login("admin", "admin");
    selenium.waitForPageToLoad("10000");
    SEL.sleep(5000);
  }

}

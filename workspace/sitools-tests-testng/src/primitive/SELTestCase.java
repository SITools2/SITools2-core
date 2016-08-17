 /*******************************************************************************
 * Copyright 2010-2016 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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

import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;

import com.thoughtworks.selenium.Selenium;

public class SELTestCase {

  protected static Selenium selenium = null;

  @BeforeSuite
  public void startSel() throws Exception {
    String host = System.getProperty("selenium.host", "localhost");
    int port = Integer.parseInt(System.getProperty("selenium.port", "4444"));
    String env = System.getProperty("selenium.env", "*firefox");
    String url = System.getProperty("webapp.url", "http://sitools.akka.eu:8184");
    SEL.init(host, port, env, url);
    SEL.start();
  }

  @BeforeTest
  public void setUp() throws Exception {
    selenium = SEL.getSelenium();
    if (selenium == null)
      throw new Exception("Selenium is not initialized");
  }

  @AfterSuite
  public void stopSel() throws Exception {
    SEL.stop();
  }

}

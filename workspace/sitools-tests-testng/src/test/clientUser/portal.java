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
package test.clientUser;

import org.testng.Assert;
import org.testng.annotations.Test;

import primitive.SEL;
import primitive.SELTestCase;

/**
 * 
 * 
 *
 */
public class portal extends SELTestCase {

  /**
   * 
   *
   */
  @Test(groups = { "initClientUser" }, description="Open the portal")
  public void load() throws Exception {
    selenium.open("/sitools/client-user/index.html");
    selenium.waitForPageToLoad("10000");
    SEL.sleep(1000);
  }
  
  /**
   * 
   *
   */
  @Test(groups = { "initClientUser" }, dependsOnMethods = { "load" }, description="Open the Selenium project desktop")
  public void goToProject() throws Exception {
	  selenium.click("xpath=//div[@id='projectDataView']/descendant::*[(text()='ProjectTestSelenium')]");
	  selenium.waitForPopUp("undefined", "30000");
	  selenium.selectWindow("undefined");

	  String[] location = selenium.getLocation().split("/");
	  String projectId = location[location.length - 2];
	  
	  SEL.waitPresence("//a[@id='fisheye-menu-bottom-DataSetExplorer']");
	  selenium.click("//a[@id='fisheye-menu-bottom-DataSetExplorer']");
	  
	  //Expand all datasets
	  String datasetTree = "//div[@id='DataSetExplorer']/descendant::ul[1]/li";
	  String datasetNode = datasetTree + "/div/img[1]";
	  SEL.waitPresence(datasetNode);
	  selenium.click(datasetNode);
	  
	  
	  String datasetSeleniumNode = datasetTree + "/descendant::*[text()='DatasetMSqlSelenium']/ancestor::div[1]/descendant::img[2]";
	  SEL.waitPresence(datasetSeleniumNode);
	  
	  selenium.click(datasetSeleniumNode);
	  
	  String showData = datasetTree + "/ul/li/ul/li[1]/div/a";
	  SEL.waitPresence(showData);
	  String nodeShowData = datasetTree + "/ul/li/ul/li[1]/div";
	  String nodeId = selenium.getAttribute(nodeShowData + "@ext:tree-node-id"); 
	  
	  selenium.click(showData);
	  
	  Assert.assertTrue(selenium.isTextPresent("Display data"));
//	  SEL.waitPresence("//div[@id='wind" + nodeId + "']");
  }

}
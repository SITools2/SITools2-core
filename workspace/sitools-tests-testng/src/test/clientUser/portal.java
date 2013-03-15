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
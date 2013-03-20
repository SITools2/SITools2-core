package test.clientUser;

import junit.framework.Assert;

import org.testng.annotations.Test;

import primitive.SEL;
import primitive.SEL4Ext;
import primitive.SELTestCase;

public class mutiDsForm  extends SELTestCase  {
	
	/**
	 * Open the project named ProjectTestSelenium
	 * @throws Exception
	 */
	@Test(groups = { "initClientUser" }, description="Open the Selenium project desktop")
	public void openProject() throws Exception {
		selenium.open("/sitools/client-user/ProjectTestSelenium/project-index.html");
		selenium.waitForPageToLoad("10000");
		SEL.sleep(1000);
	}
	@Test(groups = { "initClientUser" }, dependsOnMethods = { "openProject" }, description="Login as admin if not logged")
	public void login() throws Exception {
		SEL.sleep(5000);
		SEL.mouseDown(SEL.locByText("button", "Start"));
		try {
			SEL.click(SEL.locByText("span", "Login") + "/parent::*");
			SEL.type(SEL.locByName("input", "login"), "admin");
			SEL.type(SEL.locByName("input", "password"), "admin");
			SEL.click(SEL.locByText("button", "Login"));
			
			selenium.waitForPageToLoad("10000");
			SEL.sleep(1000);
		}
		catch (Exception e) {
			//the user is logged, nothing to do...
		}
	}

	@Test(groups = { "initClientUser" }, dependsOnMethods = { "login" }, description="Open the form multidsForm")
	public void loadForm() throws Exception {
		SEL.click(SEL.locById("a", "fisheye-menu-bottom-formsModule"));
		SEL.waitPresence(SEL.locByText("span", "Projects Forms"), 10000);
		
		String gridForm = SEL4Ext.getPanelIdFromTitle(SEL4Ext.getActiveWinId(), "Projects Forms");
		SEL.mouseDown(SEL4Ext.locGridLine(gridForm, 1));
		SEL.click(SEL.locByText(gridForm, "button", "View Form", true));
		
		SEL.waitPresence(SEL.locByText("span", "Query forms :"), 10000);
		SEL.sleep(2000);
		
		String formWin = SEL4Ext.getActiveWinId("formProject");
		
		String formConcept = SEL4Ext.getPanelIdFromTitle(formWin, "3 - Search records on Concepts");
		SEL.type(SEL.locByItem(formConcept, "input", true) + "[2]", "0");
		SEL.type(SEL.locByItem(formConcept, "input", true) + "[3]", "1");
		SEL.click(SEL.locByText(formWin, "button", "Search", true) + "[2]");
		SEL.sleep(1000);
	}
	@Test(groups = { "initClientUser" }, dependsOnMethods = { "loadForm" }, description="Open the results, assert on the results number for each ds")
	public void loadResultMultiDs() throws Exception {
		SEL.waitPresence(SEL.locByText("span", "Results : FormSelenium"));
		String winResult = SEL4Ext.getActiveWinId();
		SEL.waitPresence(SEL.locByText("div", "Request Done"), 15000);
		SEL.sleep(5000);
		String linePg = SEL4Ext.locGridLine(winResult, "PgSelenium");
		String lineM = SEL4Ext.locGridLine(winResult, "DatasetMSqlSelenium");
		Assert.assertTrue(selenium.isElementPresent(SEL.locByText(linePg, "div", "PgSelenium", true)));
		Assert.assertTrue(selenium.isElementPresent(SEL.locByText(linePg, "div", "2240", true)));
		
		Assert.assertTrue(selenium.isElementPresent(SEL.locByText(lineM, "div", "DatasetMSqlSelenium", true)));
		Assert.assertTrue(selenium.isElementPresent(SEL.locByText(lineM, "div", "2416", true)));
		
	}
}

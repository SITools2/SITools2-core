package sitoolsObjects;

import org.testng.Assert;

import imageChooser.imageChooser;
import primitive.Menu;
import primitive.SEL;

import com.thoughtworks.selenium.Selenium;

public class Project {
	static Selenium selenium = SEL.getSelenium();

	public static void createProject(String name, String[] datasetName)
			throws Exception {
		SEL.waitPresence("xpath=//button[contains(.,'Create')]");
		selenium.click("xpath=//button[contains(.,'Create')]");

		SEL.waitPresence("xpath=//input[@name='name']");
		selenium.type("xpath=//input[@name='name']", name);
		selenium.type("xpath=//input[@name='description']",
				"test avec Selenium");

		selenium.click("xpath=//form/div[4]/descendant::img");
		imageChooser.selectImage(1);

		// deuxieme onglet :
		selenium.click("xpath=//ul[1]/li[2]/a[2]");
		selenium.click("xpath=//button[contains(.,'Add')]");
		SEL.sleep(1000);
		selenium.controlKeyDown();
		for (String dataset : datasetName) {
			selenium.mouseDown("xpath=//div[@id='projectsDatasetWinId']/descendant::div[text()='" + dataset + "']");
		}
		selenium.controlKeyUp();
		selenium.click("xpath=//div[@id='projectsDatasetWinId']/descendant::button[contains(.,'OK')]");
		selenium.click("xpath=//div[@id='cmpSetupProjectId']/descendant::button[contains(.,'OK')]");
		SEL.sleep(2000);
		Assert.assertTrue(selenium.isTextPresent(name));
	}
	
	public static void activeProject(String name) throws Exception {
		Menu.select("projectsNodeId");
		SEL.sleep(1000);
		SEL.waitPresence("xpath=//div[@id='projectsBoxId']/descendant::div[text()='" + name + "']");
		selenium.mouseDown("xpath=//div[@id='projectsBoxId']/descendant::div[text()='" + name + "']");
		selenium.click("xpath=//button[contains(.,'Enable')]");
		SEL.sleep(1000);
	}
	
	public static void deleteProject(String name) {
		SEL.waitPresence("xpath=//div[@id='projectsBoxId']/descendant::div[text()='" + name + "']");
		selenium.mouseDown("xpath=//div[@id='projectsBoxId']/descendant::div[text()='" + name + "']");
		selenium.click("xpath=//button[contains(.,'Delete')]");
		SEL.waitPresence("//button[text()='Yes']");
		selenium.click("//button[text()='Yes']");
	}
	public static void deleteIfExists(String name) throws Exception {
		if (SEL.isTextPresent(name)) {
			deleteProject(name);
		}
	}

}

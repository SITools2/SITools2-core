package primitive;

import com.thoughtworks.selenium.Selenium;

public class Datasource {
	static Selenium selenium = SEL.getSelenium();

	public static void createDatasource(String url, String name,
			String attachement, String driver) throws Exception {
		SEL.waitPresence("xpath=//button[contains(.,'Create')]");
		selenium.click("xpath=//button[contains(.,'Create')]");

		SEL.waitPresence("xpath=//input[@name='name']");
		selenium.type("xpath=//input[@name='name']", name);
		selenium.type("xpath=//input[@name='description']", "Description");
		selenium.click("xpath=//form[@id='datasourceForm']/div[4]/descendant::img[@id]");
		selenium.click("xpath=//div[contains(@class,'x-combo-list-inner')]/div[text()='"+driver+"']");
		
		selenium.type("xpath=//input[@name='url']", url);
		selenium.type("xpath=//input[@name='schemaOnConnection']", "");
		selenium.type("xpath=//input[@name='sitoolsAttachementForUsers']",
				attachement);
		selenium.type("xpath=//input[@name='userLogin']", "sitools");
		selenium.type("xpath=//input[@name='userPassword']", "sitools");
		selenium.type("xpath=//input[@name='maxActive']", "10");
		selenium.type("xpath=//input[@name='initialSize']", "1");

		selenium.click("//div[@id='cmpSetupDatabaseId']/descendant::button[contains(.,'OK')]");

		SEL.sleep(1000);

		// selenium.getSelectedValue("toto");
	}

	public static void activeDatasource (String name) throws Exception {
		Menu.select("DatabaseNodeId");
		SEL.sleep(1000);
		SEL.waitPresence("xpath=//div[@id='databaseBoxId']/descendant::div[text()='" + name + "']");
		selenium.mouseDown("xpath=//div[@id='databaseBoxId']/descendant::div[text()='" + name + "']");
		selenium.click("xpath=//button[contains(.,'Enable')]");
		SEL.sleep(1000);
	}

	public static void modifyDatasource() throws Exception {
		SEL.waitPresence("xpath=//button[contains(.,'Create')]");
		selenium.click("xpath=//button[contains(.,'Edit')]");

		SEL.waitPresence("xpath=//input[@name='name']");
		selenium.type("xpath=//input[@name='name']", "DataSource modifiee");

		selenium.click("xpath=//button[contains(.,'OK')]");

		SEL.sleep(1000);

		// selenium.getSelectedValue("toto");
	}

	public static void deleteDatasource(String name) throws Exception {
		SEL.waitPresence("xpath=//div[@id='databaseBoxId']/descendant::div[text()='" + name + "']");
		selenium.mouseDown("xpath=//div[@id='databaseBoxId']/descendant::div[text()='" + name + "']");
		selenium.click("xpath=//button[contains(.,'Delete')]");
		SEL.waitPresence("//button[text()='Yes']");
		selenium.click("//button[text()='Yes']");
		
		// selenium.getSelectedValue("toto");
	}
	
	public static void deleteIfExists(String name) throws Exception {
		if (SEL.isTextPresent(name)) {
			deleteDatasource(name);
		}
	}
	
}

package sitoolsObjects;

import junit.framework.Assert;
import primitive.SEL;
import primitive.SEL4Ext;

import com.thoughtworks.selenium.Selenium;

public class Collection {
	static Selenium selenium = SEL.getSelenium();
	public static void create(String name) throws Exception {
		SEL.click(SEL.locByText("button", "Create"));
		SEL.type(SEL.locByName("input", "name"), name);
		SEL.type(SEL.locByName("input", "description"), name);
		String winId = SEL4Ext.getActiveWinId();
		
		SEL.click(SEL.locByText(winId, "button", "Add", true));
		selenium.controlKeyDown();
		SEL.mouseDown(SEL4Ext.locGridLine("projectsDatasetWinId", "DatasetMSqlSelenium"));
		SEL.mouseDown(SEL4Ext.locGridLine("projectsDatasetWinId", "PgSelenium"));
		selenium.controlKeyUp();
		SEL.click(SEL.locByText("projectsDatasetWinId", "button", "OK", true));
		
		
		SEL.click(SEL.locByText(winId, "button", "OK", true));
		
	}
	
	public static void delete(String name) throws Exception {
		
		SEL.click(SEL.locByText("button", "Delete"));

		SEL.mouseDown(SEL4Ext.locGridLine("collectionBoxId", name));
		SEL.click(SEL.locByText("button", "Delete"));
		SEL.click(SEL.locByText(SEL4Ext.getActiveWinId(), "button", "Yes", true));
		SEL.sleep(2000);
		Assert.assertFalse(SEL.isTextPresent(name));
	}

	public static void deleteIfExists(String name) throws Exception {
		if (SEL.isTextPresent(name)) {
			delete(name);
		}
	}
	
	
}

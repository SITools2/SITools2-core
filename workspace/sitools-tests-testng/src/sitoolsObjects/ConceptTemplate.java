package sitoolsObjects;

import junit.framework.Assert;
import primitive.SEL;
import primitive.SEL4Ext;

import com.thoughtworks.selenium.Selenium;

public class ConceptTemplate {
	static Selenium selenium = SEL.getSelenium();
	public static void createEmptyTemplate(String name) throws Exception {
		SEL.click(SEL.locByText("button", "Create"));
		SEL.type(SEL.locByName("input", "name"), name);
		SEL.type(SEL.locByName("input", "description"), name);
		String winId = SEL4Ext.getActiveWinId();
		
		selenium.click(SEL.locByText(winId, "*", "Concept properties", true));
		
		SEL.click(SEL.locByText(winId, "button", "OK", true));
		
	}
	public static void createContextTemplate(String name) throws Exception {
		SEL.click(SEL.locByText("button", "Create"));
		SEL.type(SEL.locByName("input", "name"), name);
		SEL.type(SEL.locByName("input", "description"), name);
		String winId = SEL4Ext.getActiveWinId();
		
		selenium.click(SEL.locByText(winId, "*", "Concept properties", true));
		SEL.click(SEL.locByText(winId, "button", "Create", true));
//		String line = SEL.getGridLine("gridPropertySelect", 1);
		selenium.doubleClick(SEL4Ext.locGridCell("gridPropertySelect", 1, 1));
		selenium.type(SEL4Ext.selectTextEditor(), "Contexte");
		SEL.pressEnter();
		
		SEL.click(SEL.locByText(winId, "button", "OK", true));
		
	}
	
	public static void delete(String name) throws Exception {
		
		SEL.click(SEL.locByText("button", "Delete"));

		SEL.mouseDown(SEL4Ext.locGridLine("groupBoxId", name));
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

package sitoolsObjects;

import junit.framework.Assert;
import primitive.SEL;
import primitive.SEL4Ext;

import com.thoughtworks.selenium.Selenium;

public class Form {
	static Selenium selenium = SEL.getSelenium();
	public static void create(String datasetName, String name) throws Exception {
		SEL.click(SEL4Ext.locComboTriggerIcon("formsBoxId", 1));
		SEL.click(SEL4Ext.locSelectComboValue(datasetName));
		
		SEL.click(SEL.locByText("button", "Create"));
		SEL.type(SEL.locByName("input", "name"), name);
		SEL.type(SEL.locByName("input", "description"), name);
		
		String winFormId = SEL4Ext.getActiveWinId();
		
		SEL4Ext.goToTab(winFormId, "Disposition");
		
		addSimpleFormCmp("TEXTFIELD", "dataset", "dataset", 10, 10);
		addSimpleFormCmp("NUMERIC_BETWEEN", "dataset", "dataset", 10, 40);
		addSimpleFormCmp("ONE_OR_BETWEEN", "dataset", "dataset", 10, 70);
		addSimpleFormCmp("NUMBER_FIELD", "dataset", "dataset", 10, 100);
		
		
		SEL.click(SEL.locByText(winFormId, "button", "OK", true));
		
	}
	
	public static void delete(String datasetName, String name) throws Exception {
		
		SEL.click(SEL4Ext.locComboTriggerIcon("formsBoxId", 1));
		SEL.click(SEL4Ext.locSelectComboValue(datasetName));
		SEL.click(SEL.locByText("button", "Delete"));

		SEL.mouseDown(SEL4Ext.locGridLine("formsBoxId", name));
		SEL.click(SEL.locByText("button", "Delete"));
		SEL.click(SEL.locByText(SEL4Ext.getActiveWinId(), "button", "Yes", true));
		SEL.sleep(3000);
		Assert.assertFalse(SEL.isTextPresent(name));
	}

	public static void deleteIfExists(String datasetName, String name) throws Exception {
		SEL.click(SEL4Ext.locComboTriggerIcon("formsBoxId", 1));
		SEL.click(SEL4Ext.locSelectComboValue(datasetName));
		if (SEL.isTextPresent(name)) {
			delete(datasetName, name);
		}
	}
	
	/**
	 * 
	 * @param cmpType
	 * @param label
	 * @param param
	 * @param xpos
	 * @param ypos
	 * @throws Exception
	 */
	private static void addSimpleFormCmp(String cmpType, String label, String param, int xpos, int ypos) throws Exception {
		String dragEl = SEL4Ext.locGridLine(SEL4Ext.getPanelIdFromTitle(SEL4Ext.getActiveWinId(), "Component list"), cmpType);
		
		SEL.dragAndDrop(dragEl, SEL.locByCss("absoluteLayout", "div", "x-panel-body", true), xpos, ypos);
		SEL.type(SEL.locByName("input", "LABEL_PARAM1"), label);
		
		String winCmpId = SEL4Ext.getActiveWinId();
		
		SEL.click(SEL4Ext.locComboTriggerIcon(winCmpId, "PARAM1"));
		SEL.sleep(2000);
		SEL.click(SEL4Ext.locSelectComboValue(param));
		SEL.pressEnter();
		
		SEL.click(SEL.locByText(winCmpId, "button", "OK", true));
		SEL.sleep(2000);
	}
}

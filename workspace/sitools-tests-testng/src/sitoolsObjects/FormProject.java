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
package sitoolsObjects;

import junit.framework.Assert;
import primitive.SEL;
import primitive.SEL4Ext;

import com.thoughtworks.selenium.Selenium;

public class FormProject {
	static Selenium selenium = SEL.getSelenium();
	public static void create(String projectName, String name) throws Exception {
		SEL.click(SEL4Ext.locComboTriggerIcon("multiDsBoxId", 1));
		SEL.click(SEL4Ext.locSelectComboValue(projectName));
		
		SEL.click(SEL.locByText("button", "Create"));
		SEL.type(SEL.locByName("input", "name"), name);
		SEL.type(SEL.locByName("input", "description"), name);
		SEL.type(SEL.locByName("input", "nbDatasetsMax"), "5");
		SEL.type(SEL.locByName("input", "urlServicePropertiesSearch"), "/" + name + "/propService" );
		SEL.type(SEL.locByName("input", "urlServiceDatasetSearch"), "/" + name + "/multiDsService");
		
		SEL.click(SEL4Ext.locComboTriggerIcon("formMainFormId", "comboCollections"));
		SEL.click(SEL4Ext.locSelectComboValue("CollectionSelenium"));
		
		SEL.sleep(2000);
		SEL.click(SEL4Ext.locComboTriggerIcon("formMainFormId", "comboDictionnaires"));
		SEL.click(SEL4Ext.locSelectComboValue("DictionarySelenium"));
		
		SEL.sleep(2000);
		Assert.assertTrue(SEL.isTextPresent("axe Z"));
		
		SEL4Ext.goToTab("cmpMultiDsId", "Properties");
		SEL.click(SEL.locByText("gridProperties", "button", "Create", true));
		for (int i = 1; i < 2; i++) {
			selenium.doubleClick(SEL4Ext.locGridCell("gridProperties", i, 1));
			SEL.sleep(1000);
			SEL.click(SEL4Ext.locComboEditorIcon());
			SEL4Ext.locSelectComboValue("prop " + String.valueOf(i));
			SEL.pressEnter();
			
			selenium.doubleClick(SEL4Ext.locGridCell("gridProperties", i, 2));
			SEL.sleep(1000);
			SEL.click(SEL4Ext.locComboEditorIcon());
			SEL4Ext.locSelectComboValue("TEXTFIELD");
			SEL.pressEnter();
			
		}
		
		String winFormId = SEL4Ext.getActiveWinId();
		
		SEL4Ext.goToTab("cmpMultiDsId", "Disposition");
		
		addSimpleFormCmp("TEXTFIELD", "TextField Z", "Z", 10, 10);
		addSimpleFormCmp("NUMERIC_BETWEEN", "nb Z", "Z", 10, 40);
		addSimpleFormCmp("ONE_OR_BETWEEN", "One Z", "Z", 10, 70);
		addSimpleFormCmp("NUMBER_FIELD", "Number Z", "Z", 10, 100);
		
		
		SEL.click(SEL.locByText(winFormId, "button", "OK", true));
		
	}
	
	public static void delete(String projectName, String name) throws Exception {
		
		SEL.click(SEL4Ext.locComboTriggerIcon("multiDsBoxId", 1));
		SEL.click(SEL4Ext.locSelectComboValue(projectName));
		SEL.click(SEL.locByText("button", "Delete"));

		SEL.mouseDown(SEL4Ext.locGridLine("multiDsBoxId", name));
		SEL.click(SEL.locByText("button", "Delete"));
		SEL.click(SEL.locByText(SEL4Ext.getActiveWinId(), "button", "Yes", true));
		SEL.sleep(3000);
		Assert.assertFalse(SEL.isTextPresent(name));
	}

	public static void deleteIfExists(String projectName, String name) throws Exception {
		SEL.click(SEL4Ext.locComboTriggerIcon("multiDsBoxId", 1));
		SEL.click(SEL4Ext.locSelectComboValue(projectName));
		if (SEL.isTextPresent(name)) {
			delete(projectName, name);
		}
	}
	
	private static void addSimpleFormCmp(String type, String label, String param, int xpos, int ypos) throws Exception {
		SEL.dragAndDrop(SEL4Ext.locGridLine(SEL4Ext.getPanelIdFromTitle("cmpMultiDsId", "Component list"), type), SEL.locByCss("absoluteLayout", "div", "x-panel-body", true), xpos, ypos);
		SEL.type(SEL.locByName("input", "LABEL_PARAM1"), label);
		SEL.type(SEL.locByName("input", "PARAM1"), param);
		
		String winCmpId = SEL4Ext.getActiveWinId();
		
		SEL.click(SEL4Ext.locComboTriggerIcon(winCmpId, "PARAM1"));
		SEL.mouseDown(SEL4Ext.locSelectComboValue(1));
		
		SEL.pressEnter();
		
		SEL.click(SEL.locByText(winCmpId, "button", "OK", true));
	}
}

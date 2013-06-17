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

public class Dictionary {
	static Selenium selenium = SEL.getSelenium();
	public static void createDictionary(String name) throws Exception {
		SEL.click(SEL.locByText("button", "Create"));
		
		String winId = SEL4Ext.getActiveWinId();
		
		SEL.mouseDown(SEL4Ext.locGridLine("gridTemplates", "ConceptWithContext"));
		
		selenium.click(SEL.locByText(winId, "*", "Dictionary information", true));
		
		SEL.type(SEL.locByName("input", "name"), name);
		SEL.type(SEL.locByName("input", "description"), name);
		
		//Create concept X
		selenium.click(SEL.locByText(winId, "*", "Select concepts", true));
		SEL4Ext.goToTab(winId, "Select concepts");
		SEL.sleep(1000);
		SEL.click(SEL.locByText(winId, "button", "Create", true));
		selenium.doubleClick(SEL4Ext.locGridCell("gridConceptsSelect", 1, 1));
		selenium.type(SEL4Ext.selectTextEditor(), "X");
		SEL.sleep(1000);
		SEL.pressEnter();
		SEL.sleep(1000);
		selenium.doubleClick(SEL4Ext.locGridCell("gridConceptsSelect", 1, 2));
		selenium.type(SEL4Ext.selectTextEditor(), "Position sur l'axe X");
		SEL.pressEnter();
		selenium.doubleClick(SEL4Ext.locGridCell("gridConceptsSelect", 1, 3));
		selenium.type(SEL4Ext.selectTextEditor(), "Valeur Comprise entre 0 et 1");
		SEL.pressEnter();
		
		//Create concept Y 
		SEL.click(SEL.locByText(winId, "button", "Create", true));
		selenium.doubleClick(SEL4Ext.locGridCell("gridConceptsSelect", 2, 1));
		selenium.type(SEL4Ext.selectTextEditor(), "Y");
		SEL.sleep(1000);
		SEL.pressEnter();
		SEL.sleep(1000);
		selenium.doubleClick(SEL4Ext.locGridCell("gridConceptsSelect", 2, 2));
		selenium.type(SEL4Ext.selectTextEditor(), "Position sur l'axe Y");
		SEL.pressEnter();
		selenium.doubleClick(SEL4Ext.locGridCell("gridConceptsSelect", 2, 3));
		selenium.type(SEL4Ext.selectTextEditor(), "Valeur Comprise entre 0 et 1");
		SEL.pressEnter();

		//Create concept Z
		SEL.click(SEL.locByText(winId, "button", "Create", true));
		selenium.doubleClick(SEL4Ext.locGridCell("gridConceptsSelect", 3, 1));
		selenium.type(SEL4Ext.selectTextEditor(), "Z");
		SEL.sleep(1000);
		SEL.pressEnter();
		SEL.sleep(1000);
		selenium.doubleClick(SEL4Ext.locGridCell("gridConceptsSelect", 3, 2));
		selenium.type(SEL4Ext.selectTextEditor(), "Position sur l'axe Z");
		SEL.pressEnter();
		selenium.doubleClick(SEL4Ext.locGridCell("gridConceptsSelect", 3, 3));
		selenium.type(SEL4Ext.selectTextEditor(), "Valeur Comprise entre 0 et 1");
		SEL.pressEnter();

		SEL.click(SEL.locByText(winId, "button", "OK", true));

	}
	public static void deleteDictionary(String name) throws Exception {
		
		SEL.click(SEL.locByText("button", "Delete"));

		SEL.mouseDown(SEL4Ext.locGridLine("groupBoxId", name));
		SEL.click(SEL.locByText("button", "Delete"));
		SEL.click(SEL.locByText(SEL4Ext.getActiveWinId(), "button", "Yes", true));
		SEL.sleep(2000);
		Assert.assertFalse(SEL.isTextPresent(name));
	}

	public static void deleteIfExists(String name) throws Exception {
		if (SEL.isTextPresent(name)) {
			deleteDictionary(name);
		}
	}
}

 /*******************************************************************************
 * Copyright 2010-2014 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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

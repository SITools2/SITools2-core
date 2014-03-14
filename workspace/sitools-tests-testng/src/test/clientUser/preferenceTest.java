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
package test.clientUser;

import junit.framework.Assert;

import org.testng.annotations.Test;

import primitive.SEL;
import primitive.SEL4Ext;
import primitive.SELTestCase;

public class preferenceTest  extends SELTestCase  {
	
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

	@Test(groups = { "initClientUser" }, dependsOnMethods = { "login" }, description="Open some windows")
	public void savePublicPreference() throws Exception {
		//save Empty preferences for admin :
		SEL.mouseDown(SEL.locByText("button", "Start"));
		SEL.click(SEL.locByText("span", "Save") + "/parent::*");
		
		SEL.click(SEL.locById("a", "fisheye-menu-bottom-formsModule"));
		SEL.waitPresence(SEL.locByText("span", "Datasets Forms"), 10000);
		
		SEL.dragAndDrop(SEL.locByCss("formsModule", "div", "x-window-header", true), SEL.locById("bureau"), 0, 400);
		
		String gridForm = SEL4Ext.getPanelIdFromTitle(SEL4Ext.getActiveWinId(), "Datasets Forms");
		SEL.mouseDown(SEL4Ext.locGridLine(gridForm, "FormPgSelenium"));
		SEL.click(SEL.locByText(gridForm, "button", "View Form", true));
		
		SEL.waitPresence(SEL.locByText("span", "Query forms :"), 10000);
		SEL.sleep(2000);
		
		String formWin = SEL4Ext.getActiveWinId("form");
		SEL.dragAndDrop(SEL.locByCss(formWin, "div", "x-window-header", true), "bureau", 0, 0);
		
		SEL.type(SEL.locByItem(formWin, "input", true) + "[1]", "A0%");
		SEL.click(SEL.locByText(formWin, "button", "Search", true));

		SEL.waitPresence(SEL.locByText("span", "Display data :"), 10000);
		
		String dataWin = SEL4Ext.getActiveWinId("windResultForm");
		SEL.dragAndDrop(SEL.locByCss(dataWin, "div", "x-window-header", true), "bureau", 602, 0);
		
		SEL.mouseDown(SEL.locByText("button", "Start"));
		SEL.click(SEL.locByText("span", "Public Save") + "/parent::*");
		SEL.sleep(1000);
		
		SEL.mouseDown(SEL.locByText("button", "Start"));
		SEL.click(SEL.locByText("span", "Logout") + "/parent::*");
		selenium.waitForPageToLoad("10000");
		SEL.sleep(1000);
		
		//V�rifier que les pr�f�rences utilisateur sont charg�es : 
		SEL.waitPresence(SEL.locByText("span", "Datasets Forms"), 10000);
		Assert.assertEquals(selenium.getElementPositionLeft(SEL4Ext.getWinId("formsModule")), 0);
		Assert.assertEquals(selenium.getElementPositionTop(SEL4Ext.getWinId("formsModule")), 395);
		
		SEL.waitPresence(SEL.locByText("span", "Query forms :"), 10000);
		
		SEL.waitPresence(SEL4Ext.getWinId("data"), 10000);
		Assert.assertEquals(selenium.getElementPositionLeft(SEL4Ext.getWinId("data")), 591);
		Assert.assertEquals(selenium.getElementPositionTop(SEL4Ext.getWinId("data")), 0);

		
	}
}

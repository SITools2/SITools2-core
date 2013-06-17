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
package primitive;

import com.thoughtworks.selenium.Selenium;

public class SEL4Ext {
	private static Selenium selenium = SEL.getSelenium();

	/**
	 * Return the xpath of an ExtJs trigger Icon
	 * @param parent the parent locator. It should be an id or an xpath
	 * @param inputName the name of the combo Input.
	 * @return the generated xpath.
	 */
	public static String locComboTriggerIcon (String parent, String inputName) {
		String result = "";
		if ("//".equals(parent.substring(0, 2)) || "xpath=//".equals(parent.substring(0, 7))) {
			result = parent;
		}
		else {
			result = SEL.locById(parent);
		}
		result += "/descendant::input[@name='" + inputName + "']/following-sibling::img";
		return result;
	}

	/**
	 * Return the xpath of an ExtJs trigger Icon
	 * @param parent the parent locator. It should be an id or an xpath
	 * @param nthField the name of the combo Input.
	 * @return the generated xpath.
	 */
	public static String locComboTriggerIcon (String form, Integer nthField) {
		String result = "";
		if ("//".equals(form.substring(0, 2)) || "xpath=//".equals(form.substring(0, 7))) {
			result = form;
		}
		else {
			result = SEL.locById(form);
		}
		result += "/descendant::input[@type='text'][" + nthField + "]/following-sibling::img";
		return result;
	}

	/**
	 * Return the xpath of the visible ExtJs Combo list option.
	 * @param name the name to look in the combo Options.
	 * @return the generated xpath.
	 */
	public static String locSelectComboValue (String name) {
		return "//div[contains(@class, 'x-combo-list') and contains(@style, 'visible')]/descendant::div[text()='" + name + "']";		
	}

	/**
	 * Return the xpath of the visible ExtJs Combo list option.
	 * @param optionRow the number of the option to check.
	 * @return the generated xpath.
	 */
	public static String locSelectComboValue (int optionRow) {
		return "//div[contains(@class, 'x-combo-list') and contains(@style, 'visible')]/descendant::div[contains(@class,'x-combo-list-item')][" + optionRow + "]";		
	}

	/**
	 * Return an xpath locator of the ExtJs Grid textfield editor. 
	 * Search for css x-editor and visible style attribute 
	 * @return the generated xpath.
	 */
	public static String selectTextEditor () {
		return "//div[contains(@class, 'x-editor') and contains(@style, 'visible')]/input";		
	}

	/**
	 * Return an xpath locator of the ExtJs Grid combo editor icon. 
	 * Search for css x-editor and visible style attribute 
	 * @return the generated xpath.
	 */
	public static String locComboEditorIcon () {
		return "//div[contains(@class, 'x-editor') and contains(@style, 'visible')]/descendant::img";		
	}

	/**
	 * Return an xpath locator of the ExtJs Grid combo editor. 
	 * Search for Extjs css x-editor and visible style attribute 
	 * @return the generated xpath.
	 */
	public static String locComboEditor () {
		return "//div[contains(@class, 'x-editor') and contains(@style, 'visible')]/descendant::input";		
	}

	/**
	 * Return an xpath locator of the ExtJs datePicker Today button. 
	 * Search for Extjs css x-date-menu and visible style attribute 
	 * @return the generated xpath.
	 */
	public static String locToday() {
		return SEL.locByText("//div[contains(@class, 'x-date-menu') and contains(@style, 'visible')]", "button", "Today", true);
	}
	
	/**
	 * Return an id of the active window in an ExtJs generated html page. 
	 * You can specify an id to filter possible active windows.
	 * Search for Extjs css x-window and visible style attribute. loop on each window and return the id of the window with the greatest z-index style attribute.
	 * @param id An id to filter the active window search.
	 * @return the id of the active window.
	 */
	private static String searchActiveWin(String id) throws Exception {
		String xpath = "//div[contains(@class,'x-window') and contains(@style,'visible')";
		if (id != null && !"".equals(id)) {
			xpath += " and contains(@id,'" + id + "')";
		}
		xpath += "]";
		SEL.waitPresence(xpath, 2000);
		int nbWin = (Integer) selenium.getXpathCount(xpath);
		if (nbWin == 0) {
			throw new Exception();
		}
		int maxZIndex = 0;
		int zIndex;
		String winId = "";
		String activeWinId = "";
		for (int i = 1; i <= nbWin; i++) {
			winId = selenium.getAttribute(xpath + "[" + i + "]@id");
			zIndex = Integer.parseInt(SEL.getStyleAttribute(SEL.locById(winId), "z-index"));
			if (zIndex > maxZIndex) {
				maxZIndex = zIndex;
				activeWinId = winId;
			}
		}
		return activeWinId;
	}

	/**
	 * Return an id of the window in an ExtJs generated html page. 
	 * You can specify an id to filter possible active windows.
	 * Search for Extjs css x-window and visible style attribute. 
	 * @param id An id to filter the active window search.
	 * @return the id of the window.
	 */
	private static String searchWin(String id) throws Exception {
		String xpath = "//div[contains(@class,'x-window') and contains(@style,'visible')";
		if (id != null && !"".equals(id)) {
			xpath += " and contains(@id,'" + id + "')";
		}
		xpath += "]";
		SEL.waitPresence(xpath, 2000);
		int nbWin = (Integer) selenium.getXpathCount(xpath);
		if (nbWin == 0) {
			throw new Exception();
		}
		
		return xpath;
	}
	/**
	 * Return an id of the window in an ExtJs generated html page. 
	 * You can specify an id to filter possible active windows.
	 * Sleep for 5 seconds to wait any window to open. 
	 * Try to find any window. Try again after 5 seconds if any errors occurs. 
	 * @param id An id to filter the active window search.
	 * @return the id of the active window.
	 */
	public static String getWinId(String id) throws Exception {
		SEL.sleep(5000);
		try {
			return searchWin(id);
		} catch (Exception e) {
			SEL.sleep(5000);
			return searchWin(id);
		}
	}
	
	
	/**
	 * Return an id of the active window in an ExtJs generated html page. 
	 * You can specify an id to filter possible active windows.
	 * Sleep for 5 seconds to wait any window to open. 
	 * Try to find any active window. Try again after 5 seconds if any errors occurs. 
	 * @param id An id to filter the active window search.
	 * @return the id of the active window.
	 */
	public static String getActiveWinId(String id) throws Exception {
		SEL.sleep(5000);
		try {
			return searchActiveWin(id);
		} catch (Exception e) {
			SEL.sleep(5000);
			return searchActiveWin(id);
		}
	}

	/**
	 * Return an id of the active window in an ExtJs generated html page. 
	 * Sleep for 5 seconds to wait any window to open. 
	 * Try to find any active window. Try again after 5 seconds if any errors occurs. 
	 * @return the id of the active window.
	 */
	public static String getActiveWinId() throws Exception {
		SEL.sleep(5000);
		try {
			return searchActiveWin("");
		} catch (Exception e) {
			SEL.sleep(5000);
			return searchActiveWin("");
		}
	}

	/**
	 * Return an xpath for the ExtJs gridBody of a specific grid Id. 
	 * @param gridId The id (or xpath) of the grid. 
	 * @return the generated xpath.
	 */
	public static String locGridBody(String gridId) {
		return SEL.locByCss(gridId, "div", "x-grid3-body", true);
	}

	/**
	 * Return an xpath for the ExtJs grid line of a specific grid Id and rowNumber.
	 * The element located by generated xpath is the div with ExtJs x-grid3-row class
	 * @param gridId The id (or xpath) of the grid. 
	 * @param rowNumber the row Number. 
	 * @return the generated xpath.
	 */
	public static String locGridLine(String gridId, int rowNumber) {
		return SEL.locByItem(locGridBody(gridId), "tr[" + rowNumber + "]", true) + "/ancestor::div[contains(@class,'x-grid3-row')]";
	}

	/**
	 * Return an xpath for the ExtJs grid line of a specific grid Id and rowNumber.
	 * The element located by generated xpath is the tr
	 * @param gridId The id (or xpath) of the grid. 
	 * @param rowNumber the row Number. 
	 * @return the generated xpath.
	 */
	public static String locGridTr(String gridId, int rowNumber) {
		return SEL.locByItem(locGridBody(gridId), "tr[" + rowNumber + "]", true);
	}

	/**
	 * Return an xpath for the ExtJs grid line of a specific grid Id and a text to look for.
	 * The element located by generated xpath is the div with  x-grid3-row class
	 * @param gridId The id (or xpath) of the grid. 
	 * @param text the text to look for. 
	 * @return the generated xpath.
	 */
	public static String locGridLine(String gridId, String text) {
		return SEL.locByText(locGridBody(gridId), "div", text,  true) + "/ancestor::div[contains(@class,'x-grid3-row')]";
	}

	/**
	 * Return an xpath for the ExtJs grid line of a specific grid Id and a text to look for.
	 * The element located by generated xpath is the tr
	 * @param gridId The id (or xpath) of the grid. 
	 * @param text the text to look for. 
	 * @return the generated xpath.
	 */
	public static String locGridTr(String gridId, String text) {
		return SEL.locByText(locGridBody(gridId), "div", text,  true) + "/ancestor::tr";
	}

	/**
	 * Return an xpath for the ExtJs grid cell of a specific grid.
	 * The element located by generated xpath is the td element.
	 * @param gridId The id (or xpath) of the grid. 
	 * @param rowNumber the row Number. 
	 * @param cellNumber the column number of the cell. 
	 * @return the generated xpath.
	 */
	public static String locGridCell(String gridId, int rowNumber, int cellNumber) {
		return SEL.locByItem(locGridTr(gridId, rowNumber), "td[" + cellNumber + "]", false);
	}

	/**
	 * In an ExtJs tabPanel, execute a click on the panel with a specific title. 
	 * @param parent the tabPanel, or ancestor id (or xpath)
	 * @param text the title of the panel to activate. 
	 * @throws Exception 
	 */
	public static void goToTab(String parent, String text) throws Exception {
		String tabPanel = SEL.locByCss(SEL.locById(parent), "div", "x-tab-panel", true);
		SEL.click(SEL.locByText(tabPanel, "span", text, true) + "/parent::*/parent::*");
	}

	/**
	 * Return the id of the Extjs generated Panel element with a specific title. 
	 * @param parent the parent locator. It should be an id or an xpath
	 * @param text the title of the panel. 
	 * @return the id of the generated Panel. 
	 * @throws Exception 
	 */
	public static String getPanelIdFromTitle(String parent, String text) throws Exception {
		String xpath = SEL.locByItem(parent, "div", true); 
		xpath += "[contains(@class,'x-panel-header')]/span[contains(text(),'" + text + "')]/parent::*/parent::*";
		try {
			return selenium.getAttribute(xpath + "@id");
		} catch (Exception e) {
			SEL.waitPresence(xpath, 5000);
			return selenium.getAttribute(xpath + "@id");
		}

	}
	/**
	 * Edit a value in an ExtJs Editor grid. 
	 * @param gridBody the id or the xpath to locate the grid Body
	 * @param lineNumber the line number start at 1
	 * @param colNumber the column to edit (start at 1)
	 * @param editedValue the value to edit. 
	 * @throws InterruptedException
	 */
	public static void editGridValue(String gridBody, int lineNumber, int colNumber, String editedValue) throws InterruptedException {
		try {
			selenium.doubleClickAt(SEL4Ext.locGridCell(gridBody, lineNumber, colNumber), "5,5");
			selenium.type(SEL4Ext.selectTextEditor(), editedValue);
			SEL.sleep(1000);
			SEL.pressEnter();
			SEL.sleep(1000);
		}
		catch (Exception e) {
			selenium.doubleClickAt(SEL4Ext.locGridCell(gridBody, lineNumber, colNumber), "5,5");
			selenium.type(SEL4Ext.selectTextEditor(), editedValue);
			SEL.sleep(1000);
			SEL.pressEnter();
			SEL.sleep(1000);
		}
	}

	/**
	 * Edit a value in an ExtJs Editor grid. 
	 * @param gridBody the id or the xpath to locate the grid Body
	 * @param searchedValue the value to look at to determine the line Number.
	 * @param colNumber the column to edit (start at 1)
	 * @param editedValue the value to edit. 
	 * @throws InterruptedException
	 */
	public static void editGridValue(String gridBody, String searchedValue, int colNumber, String editedValue) throws InterruptedException {
		String line = SEL4Ext.locGridLine(gridBody, searchedValue);
		int lineNumber = (Integer) selenium.getElementIndex(line) + 1;
		editGridValue(gridBody, lineNumber, colNumber, editedValue);
	}

	/**
	 * Edit a value in an Extjs Editor grid with a combo editor. 
	 * @param gridBody the id or the xpath to locate the grid Body
	 * @param lineNumber The number of the line to edit (start at 1)
	 * @param colNumber the column to edit (start at 1)
	 * @param editedValue the value to edit. 
	 * @throws Exception 
	 */
	public static void editComboGridValue(String gridBody, int lineNumber, int colNumber, String editedValue) throws Exception {
		selenium.doubleClickAt(SEL4Ext.locGridCell(gridBody, lineNumber, colNumber), "5,5");
		
		selenium.click(SEL4Ext.locComboEditorIcon());
		SEL.sleep(500);			
		SEL.click(SEL4Ext.locSelectComboValue(editedValue));
		SEL.sleep(500);			
		SEL.pressEnter();
		SEL.sleep(1000);			
	}

	/**
	 * Edit a value in an ExtJs Editor grid using a combo editor. 
	 * @param gridBody the id or the xpath to locate the grid Body
	 * @param searchedValue the value to look at to determine the line Number.
	 * @param colNumber the column to edit (start at 1)
	 * @param editedValue the value to edit. 
	 * @throws Exception 
	 */
	public static void editComboGridValue(String gridBody, String searchedValue, int colNumber, String editedValue) throws Exception {
		String line = SEL4Ext.locGridLine(gridBody, searchedValue);
		int lineNumber = (Integer) selenium.getElementIndex(line) + 1;
		editComboGridValue(gridBody, lineNumber, colNumber, editedValue);
	}


}

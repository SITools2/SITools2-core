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

import static junit.framework.Assert.fail;

import com.thoughtworks.selenium.DefaultSelenium;
import com.thoughtworks.selenium.Selenium;

public class SEL {

	private static long TIMEOUT = 5000;
	private static Selenium selenium;

	/**
	 * Launch a DefaultSelenium with params
	 * @param server
	 * @param port
	 * @param browser
	 * @param url
	 */
	public static void init(String server, int port, String browser, String url) {
		selenium = new DefaultSelenium(server, port, browser, url);
	}

	/**
	 * Start selenium
	 */
	public static void start() {
		selenium.start();
	}

	/**
	 * Stop Selenium
	 */
	public static void stop() {
		selenium.stop();
	}

	/**
	 * return selenium
	 */
	public static Selenium getSelenium() {
		return selenium;
	}

	/**
	 * Gets the result of evaluating the specified JavaScript snippet. The snippet may have multiple lines, but only the result of the last line will be returned
	 * @param script Js Script
	 * @return
	 */
	public static String eval(String script) {
		return selenium.getEval("with(window){" + script + "}");
	}

	/**
	 * use the js method getCmpLocator to find a locator
	 * @param cmp
	 * @return
	 */
	public static String getLocator(String cmp) {
		return eval("getCmpLocator(" + cmp + ")");
	}

	/**
	 * Sleep during msec milliSeconds.
	 * @param msec
	 * @throws InterruptedException
	 */
	public static void sleep(long msec) throws InterruptedException {
		Thread.sleep(msec);
	}

	/**
	 * Sets the selenium speed
	 * @param msec
	 * @throws InterruptedException
	 */
	public static void setDelay(long msec) throws InterruptedException {
		selenium.setSpeed(String.valueOf(msec));
	}

	/**
	 * set the selenium timeOut
	 * @param sec
	 * @throws InterruptedException
	 */
	public static void setTimeout(short sec) throws InterruptedException {
		selenium.setTimeout(String.valueOf(sec));
	}

	/**
	 * wait until locator is founded or for TIMEOUT milliseconds. 
	 * Fails if timeout is over, and locator is not founded.
	 * @param locator
	 */
	public static void waitPresence(String locator) {
		for (long second = 0;; second++) {
			if (second*1000 >= TIMEOUT)
				fail("timeout : " + locator);
			try {
				if (selenium.isElementPresent(locator))
					break;
				sleep(1000);
			} catch (Exception e) {
			}
		}
	}
	/**
	 * Same as waitPresence, with a specific Timeout.
	 * @param locator
	 * @param timeout
	 * @throws Exception 
	 */
	public static void waitPresence(String locator, int timeout) throws Exception {
		for (long second = 0;; second++) {
			if (second*1000 >= timeout)
				throw new Exception("timeout : " + locator);
			try {
				if (selenium.isElementPresent(locator))
					break;
				sleep(1000);
			} catch (Exception e) {
			}
		}
	}
	
	/**
	 * Try to click on a located element. 
	 * If not present, wait for 5 seconds and try again. 
	 * @param locator
	 * @throws Exception 
	 */
	public static void click (String locator) throws Exception {
		try {
			selenium.click(locator);
			SEL.sleep(100);
		} catch (Exception e) {
			waitPresence(locator, 5000);
			selenium.click(locator);
		}
	}
	
	/**
	 * Try to execute selenium.type on a located element. 
	 * If not present, wait for 5 seconds and try again. 
	 * @param locator
	 * @param value the value to type
	 * @throws Exception 
	 */
	public static void type (String locator, String value) throws Exception {
		try {
			selenium.type(locator, value);
		} catch (Exception e) {
			waitPresence(locator, 5000);
			selenium.type(locator, value);
		}
	}

	/**
	 * Try to execute selenium.mouseDown on a located element. 
	 * If not present, wait for 5 seconds and try again. 
	 * @param locator
	 * @throws Exception 
	 */
	public static void mouseDown (String locator) throws Exception {
		try {
			selenium.mouseDown(locator);
			sleep(100);
		} catch (Exception e) {
			waitPresence(locator, 5000);
			selenium.mouseDown(locator);
		}
	}
	
	/**
	 * Try to execute selenium.clickAt on a located element. 
	 * If not present, wait for 5 seconds and try again. 
	 * @param locator
	 * @throws Exception 
	 */
	public static void clickAt (String locator, String coord) throws Exception {
		try {
			selenium.clickAt(locator, coord);
			sleep(100);
		} catch (Exception e) {
			waitPresence(locator, 5000);
			selenium.clickAt(locator, coord);
		}
	}

	/**
	 * Try to execute selenium.doubleClickAt on a located element. 
	 * If not present, wait for 5 seconds and try again. 
	 * @param locator
	 * @throws Exception 
	 */
	public static void doubleClickAt (String locator, String coord) throws Exception {
		try {
			selenium.doubleClickAt(locator, coord);
			sleep(100);
		} catch (Exception e) {
			waitPresence(locator, 5000);
			selenium.doubleClickAt(locator, coord);
		}
	}

	
	/**
	 * Return an xpath locator according to the item.  
	 * @param item The item to locate ("div", "span", etc...)
	 * @return String the generated xpath.
	 */
	public static String locByItem (String item) {
		return "//" + item;
	}
	
	/**
	 * Return an xpath locator according to the item with a given id.  
	 * @param item The item to locate ("div", "span", etc...)
	 * @param id The item id
	 * @return String the generated xpath.
	 */
	public static String locById (String item, String id) {
		return locByItem(item) + "[@id='" + id + "']";
	}
	
	/**
	 * Return an xpath locator according to a given id.  
	 * @param id The item id
	 * @return String the generated xpath.
	 */
	public static String locById (String id) {
		if (id == null || "".equals(id)) {
			return "";
		}
		return "//*[@id='" + id + "']";
	}

	/**
	 * Return an xpath locator. 
	 * You can find any item wich is a descendant of an item. 
	 * @param String parent the parent locator. It should be an id or an xpath
	 * @param String item The item to locate ("div", "span", etc...)
	 * @param boolean descendant true if not direct child, false if direct child.
	 * @return String the generated xpath.
	 */
	public static String locByItem (String parent, String item, boolean descendant) {
		String result = "";
		if ("//".equals(parent.substring(0, 2)) || "xpath=//".equals(parent.substring(0, 7))) {
			result = parent + "/";
			if (descendant) {
				result += "descendant::";
			}
			result +=  item;
		}
		else {
			result = locById(parent) + "/";
			if (descendant) {
				result += "descendant::";
			}
			result += item;
		}
		return result;		
	}
	
	/**
	 * Return an xpath locator. 
	 * You can find any item with a given css. 
	 * @param String item The item to locate ("div", "span", etc...)
	 * @param String css the css to look for
	 * @return String the generated xpath.
	 */
	public static String locByCss (String item, String css) {
		return locByItem(item) +  "[contains(@class, '" + css + "')]";
	}

	/**
	 * Return an xpath locator. 
	 * You can find any item containing a given text. 
	 * @param item The item to locate ("div", "span", etc...)
	 * @param text the text to look for
	 * @return the generated xpath.
	 */
	public static String locByText (String item, String text) {
		return locByItem(item) +  "[contains(text(), '" + text + "')]";
	}
	
	/**
	 * Return an xpath locator. 
	 * You can find any item wich is a descendant of an item and contains a given text. 
	 * @param String parent the parent locator. It should be an id or an xpath
	 * @param String item The item to locate ("div", "span", etc...)
	 * @param String text the text to look for
	 * @param boolean descendant true if not direct child, false if direct child.
	 * @return String the generated xpath.
	 */
	public static String locByText (String parent, String item, String text, boolean descendant) {
		String result = "";
		if ("//".equals(parent.substring(0, 2)) || "xpath=//".equals(parent.substring(0, 7))) {
			result = parent  + "/";
			if (descendant) {
				result += "descendant::";
			}
			result +=  item + "[contains(text(), '" + text + "')]";
		}
		else {
			result = locById(parent) + "/";
			if (descendant) {
				result += "descendant::";
				
			}
			
			result += item + "[contains(text(), '" + text + "')]";
		}
		return result;
	}
	
	/**
	 * Return an xpath locator. 
	 * You can find any item with a specific name attribute
	 * @param item The item to locate ("div", "span", etc...)
	 * @param name the name to look for
	 * @return the generated xpath.
	 */
	public static String locByName (String item, String name) {
		return locByItem(item) +  "[@name='" + name + "']";
	}
	
	/**
	 * Return an xpath locator. 
	 * You can find any item wich is a descendant of an item and have a given css. 
	 * @param String parent the parent locator. It should be an id or an xpath
	 * @param String item The item to locate ("div", "span", etc...)
	 * @param String css the css to look for
	 * @param boolean descendant true if not direct child, false if direct child.
	 * @return String the generated xpath.
	 */
	public static String locByCss (String parent, String item, String css, boolean descendant) {
		String result = "";
		if ("//".equals(parent.substring(0, 2)) || "xpath=//".equals(parent.substring(0, 7))) {
			result = parent  + "/";
			if (descendant) {
				result += "descendant::";
			}
			result +=  item + "[contains(@class,'" + css + "')]";
		}
		else {
			result = locById(parent) + "/";
			if (descendant) {
				result += "descendant::";
			}
			result += item + "[contains(@class,'" + css + "')]";
		}
		return result;
	}
	
	/**
	 * Return an xpath locator. 
	 * You can find any item having a given style attribute. 
	 * @param String item The item to locate ("div", "span", etc...)
	 * @param String style the style attribute to look for
	 * @return String the generated xpath.
	 */
	public static String locByStyle (String item, String style) {
		return locByItem(item) +  "[contains(@style, '" + style + "')]";
	}
	
	/**
	 * Return an xpath locator. 
	 * You can find any item which is a descendant of an item and have a given style attribute. 
	 * @param String parent the parent locator. It should be an id or an xpath
	 * @param String item The item to locate ("div", "span", etc...)
	 * @param String style the style attribute to look for
	 * @param boolean descendant true if not direct child, false if direct child.
	 * @return String the generated xpath.
	 */
	public static String locByStyle (String parent, String item, String style, boolean descendant) {
		String result = "";
		if ("//".equals(parent.substring(0, 2)) || "xpath=//".equals(parent.substring(0, 7))) {
			result = parent  + "/";
			if (descendant) {
				result += "descendant::";
			}
			result +=  item + "[contains(@style,'" + style + "')]";
		}
		else {
			result = locById(parent) + "/";
			if (descendant) {
				result += "descendant::";
			}
			result += item + "[contains(@style,'" + style + "')]";
		}
		return result;
	}
	
	
	
	/**
	 * Simulates a keyDown on "ENTER" 
	 */
	public static void pressEnter() {
		selenium.keyPressNative(new Integer (java.awt.event.KeyEvent.VK_ENTER).toString());
	}
	
	/**
	 * Simulates a keyDown on "TAB" 
	 */
	public static void pressTab() {
		selenium.keyPressNative(new Integer (java.awt.event.KeyEvent.VK_TAB).toString());
	}
	
	/**
	 * Try to find the specific text. 
	 * If not finded, wait 5 seconds and return true if founded, false otherwise.
	 * @param the text to look for. 
	 * @return boolean find Element 
	 */
	public static boolean isTextPresent(String text) throws InterruptedException {
		if (selenium.isTextPresent(text)) {
			return true;
		}
		else {
			sleep(5000);
			return selenium.isTextPresent(text);
		}
	}
	

	/**
	 * Executes a drag And drop. 
	 * @param dragEl the locator of the element to be dragged. 
	 * @param dropEl the locator of the element where to drop. 
	 * @param dh the Delta Height where to drop target.  
	 * @param dw the Delta width where to drop target.  
	 * @throws Exception 
	 */
	public static void dragAndDrop(String dragEl, String dropEl, int dh, int dw) throws Exception{
		click(dragEl);
		selenium.mouseUp(dragEl);
		selenium.mouseDownAt(dragEl, "5,5");
		selenium.mouseMoveAt(dropEl, dh + "," + dw);
		selenium.mouseOver(dropEl);
		selenium.mouseUpAt(dropEl, dh + "," + dw);
	}
	
	/**
	 * Get the value of the style attribute of a specific element. 
	 * @param xPath the locator to the element. 
	 * @param styleAttrSearched the style Attribute to look for. 
	 * @return the value of the style Attribute
	 */
	public static String getStyleAttribute(String xPath, String styleAttrSearched) {
		String style = selenium.getAttribute(xPath + "@style");
		String result = "";
		if (style == null || "".equals(style)) {
			return "";
		}
		String[] tabStyle = style.split(";");
		for (String styleAttr : tabStyle) {
			String[] tab = styleAttr.split(":");
			if (styleAttrSearched.equals(tab[0].trim())) {
				result = tab[1].trim();
			}
			
		}
		return result;
	}
}

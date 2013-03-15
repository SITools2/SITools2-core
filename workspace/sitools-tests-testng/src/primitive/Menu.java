package primitive;

import com.thoughtworks.selenium.Selenium;

public class Menu {
	static Selenium selenium = SEL.getSelenium();

	public static void select(String nodeId) throws Exception {
		String locator = SEL.getLocator("getMenuNode('" + nodeId + "')");
		SEL.waitPresence(locator);
		selenium.doubleClick(locator);
		locator = SEL.getLocator("getMenuNode('" + nodeId + "')");
		selenium.click(locator);
		SEL.sleep(1000);
	}
}

package primitive;

import com.thoughtworks.selenium.Selenium;

public class Login {
	static Selenium selenium = SEL.getSelenium();

	public static void login(String user, String password) throws Exception {
		SEL.waitPresence("logId");
		selenium.type("logId", user);
		selenium.type("pwdId", password);
		selenium.click("xpath=//button[contains(.,'Login')]");
		SEL.sleep(1000);
	}
}

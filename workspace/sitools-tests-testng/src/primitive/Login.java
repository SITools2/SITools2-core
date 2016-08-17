 /*******************************************************************************
 * Copyright 2010-2016 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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

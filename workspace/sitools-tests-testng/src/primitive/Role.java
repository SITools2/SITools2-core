package primitive;

import com.thoughtworks.selenium.Selenium;

public class Role {
  static Selenium selenium = SEL.getSelenium();

  public static void createRole(String roleName, String roleDescription) throws Exception {
    SEL.waitPresence("xpath=//button[contains(.,'Create')]");
    selenium.click("xpath=//button[contains(.,'Create')]");

    SEL.waitPresence("xpath=//input[@name='name']");
    selenium.type("xpath=//input[@name='name']", roleName);
    selenium.type("xpath=//input[@name='description']", roleDescription);
    selenium.click("xpath=//table[@id='okButtonId']");
    SEL.sleep(1000);

  }

  public static void modifyRole(String roleName, String roleDescription) throws Exception {
    SEL.waitPresence("xpath=//button[contains(.,'Create')]");
    selenium.click("xpath=//button[contains(.,'Edit')]");

    SEL.waitPresence("xpath=//input[@name='name']");
    selenium.type("xpath=//input[@name='name']", roleName);
    selenium.type("xpath=//input[@name='description']", roleDescription);
    selenium.click("xpath=//table[@id='okButtonId']");

    SEL.sleep(1000);

  }

  public static void deleteRole() throws Exception {
    SEL.waitPresence("xpath=//button[contains(.,'Create')]");
    selenium.click("xpath=//button[contains(.,'Delete')]");

    SEL.waitPresence("xpath=//button[contains(.,'Yes')]");
    selenium.click("xpath=//button[contains(.,'Yes')]");

    SEL.sleep(1000);

  }

  public static void createRoleAndCancel(String name, String description) throws Exception {

    SEL.waitPresence("xpath=//button[contains(.,'Create')]");
    selenium.click("xpath=//button[contains(.,'Create')]");

    SEL.waitPresence("xpath=//input[@name='name']");
    selenium.type("xpath=//input[@name='name']", name);
    selenium.type("xpath=//input[@name='description']", description);
    selenium.click("xpath=//table[@id='cancelButtonId']");
    SEL.sleep(1000);
  }

  public static void deleteAndCancelRole() throws Exception {
    SEL.waitPresence("xpath=//button[contains(.,'Create')]");
    selenium.click("xpath=//button[contains(.,'Delete')]");

    SEL.waitPresence("xpath=//button[contains(.,'Yes')]");
    selenium.click("xpath=//button[contains(.,'No')]");

    SEL.sleep(1000);

  }

}

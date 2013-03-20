package primitive;

import com.thoughtworks.selenium.Selenium;

public class User {
  static Selenium selenium = SEL.getSelenium();

  public static void createUser(String firstName, String lastName, String email, String login, String secret)
      throws Exception {
    SEL.waitPresence("xpath=//button[contains(.,'Create')]");
    selenium.click("xpath=//button[contains(.,'Create')]");

    SEL.waitPresence("xpath=//input[@name='firstName']");
    selenium.type("xpath=//input[@name='firstName']", firstName);
    selenium.type("xpath=//input[@name='lastName']", lastName);
    selenium.type("xpath=//input[@name='email']", email);
    selenium.type("xpath=//input[@name='identifier']", login);
    selenium.type("xpath=//input[@name='secret']", secret);
    selenium.type("confirmSecret", secret);
    selenium.click("xpath=//button[contains(.,'OK')]");

    SEL.sleep(1000);

    // selenium.getSelectedValue("toto");
  }

  public static void modifyUser(String firstName, String lastName, String email, String login, String secret)
      throws Exception {
    SEL.waitPresence("xpath=//button[contains(.,'Create')]");
    selenium.click("xpath=//button[contains(.,'Edit')]");

    SEL.waitPresence("xpath=//input[@name='firstName']");
    selenium.type("xpath=//input[@name='firstName']", firstName);
    selenium.type("xpath=//input[@name='lastName']", lastName);
    selenium.type("xpath=//input[@name='email']", email);
    selenium.type("xpath=//input[@name='identifier']", login);
    selenium.type("xpath=//input[@name='secret']", secret);
    selenium.type("confirmSecret", secret);
    selenium.click("xpath=//button[contains(.,'OK')]");

    SEL.sleep(1000);

    // selenium.getSelectedValue("toto");
  }

  public static void deleteUser() throws Exception {
    SEL.waitPresence("xpath=//button[contains(.,'Create')]");
    selenium.click("xpath=//button[contains(.,'Delete')]");

    SEL.waitPresence("xpath=//button[contains(.,'Yes')]");
    selenium.click("xpath=//button[contains(.,'Yes')]");

    SEL.sleep(1000);

    // selenium.getSelectedValue("toto");
  }
}

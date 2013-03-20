package test;

import org.testng.annotations.Test;

import primitive.Login;
import primitive.SEL;
import primitive.SELTestCase;

/**
 * 
 * 
 *
 */
public class AdminLogin extends SELTestCase {

  /**
   * 
   *
   */
  @Test(groups = { "initClientAdmin" })
  public void load() throws Exception {
    selenium.open("/sitools/client-admin/");
    selenium.waitForPageToLoad("10000");
    
//    selenium.setSpeed("1000");
    
    SEL.sleep(1000);
  }

  /**
   * 
   *
   */
  @Test(groups = { "initClientAdmin" }, dependsOnMethods = { "load" })
  public void login() throws Exception {
    Login.login("admin", "admin");
    selenium.waitForPageToLoad("10000");
    SEL.sleep(5000);
  }

}

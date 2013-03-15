package test;

import org.testng.Assert;
import org.testng.annotations.Test;

import primitive.Menu;
import primitive.SEL;
import primitive.SELTestCase;
import primitive.User;

public class ADM51 extends SELTestCase {
  /**
   * Javascript needed to execute in order to select row in the grid
   * 
   * @param gridId
   *          Grid id
   * @param rowIndex
   *          Index of the row to select
   * @return Javascript to select row
   */
  public static String selectGridRow() {
    return "Ext.getCmp(ID.BOX.USER).getSelectionModel().selectLastRow()";
  }

  @Test(dependsOnGroups = { "initClientAdmin.*" })
  public void selectUsers() throws Exception {
    Menu.select("usrNodeId");
    SEL.sleep(1000);
  }

  @Test(dependsOnGroups = { "initClientAdmin.*" }, dependsOnMethods = { "selectUsers" })
  public void users() throws Exception {
    User.createUser("testFirstName", "lastName", "email@toto.com", "login", "secret");
    // assertTrue (selenium.isTextPresent("firstNameMod"));
    SEL.sleep(1000);
    // String script = "Ext.getComponent(ID.BOX.USER).getSelectionModel().selectLastRow();";
    selenium.mouseDown("xpath=//div[@id='mainPanelId']/descendant::div[text()='testFirstName']");
    Assert.assertTrue(selenium.isTextPresent("testFirstName"));

    User.modifyUser("firstNameMod", "lastName", "email@toto.com", "login", "secret");
    Assert.assertTrue(selenium.isTextPresent("firstNameMod"));
    
    selenium.mouseDown("xpath=//div[@id='mainPanelId']/descendant::div[text()='firstNameMod']");
	User.deleteUser();

  }
}

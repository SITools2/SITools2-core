package test;

import org.testng.annotations.Test;

import primitive.Menu;
import primitive.SEL;
import primitive.SELTestCase;

public class ADM02 extends SELTestCase {

  @Test(dependsOnGroups = { "initClientAdmin.*" })
  public void selectUsers() throws Exception {
    Menu.select("usrNodeId");
    SEL.sleep(1000);
  }

  @Test(dependsOnGroups = { "initClientAdmin.*" })
  public void selectGroups() throws Exception {
    Menu.select("grpNodeId");
    SEL.sleep(1000);
  }

  @Test(dependsOnGroups = { "initClientAdmin.*" }, enabled = false)
  public void createUser() throws Exception {
    Menu.select("usrNodeId");
    // TODO
    // ...
  }
}

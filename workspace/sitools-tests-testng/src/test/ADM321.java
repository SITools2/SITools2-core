package test;

import org.testng.annotations.Test;

import primitive.Datasource;
import primitive.Menu;
import primitive.SEL;
import primitive.SELTestCase;

public class ADM321 extends SELTestCase {
  /**
   * Javascript needed to execute in order to select row in the grid
   * 
   * @param gridId
   *          Grid id
   * @param rowIndex
   *          Index of the row to select
   * @return Javascript to select row
   */
  public static String selectLastRow() {
    return "Ext.getCmp(ID.BOX.DATABASE).getSelectionModel().selectLastRow()";
  }

  @Test(dependsOnGroups = { "initClientAdmin.*" })
  public void selectUsers() throws Exception {
    Menu.select("DatabaseNodeId");
    SEL.sleep(1000);
  }

  @Test(dependsOnGroups = { "initClientAdmin.*" }, dependsOnMethods = { "selectUsers" })
  public void datasource() throws Exception {
    String url = "jdbc:postgresql://localhost:5432/CNES";
	Datasource.createDatasource(url, "PGSelenium", "/postgres", "PostgreSQL");
    // assertTrue (selenium.isTextPresent("jdbc:"));
    SEL.sleep(1000);
    selenium.runScript(selectLastRow());
    Datasource.modifyDatasource();
    // // TODO : ne passe pas avec les bibliotheques actuelles
    Datasource.deleteDatasource("PGSelenium");

  }
}
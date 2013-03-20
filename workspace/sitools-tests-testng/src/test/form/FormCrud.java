package test.form;

import org.testng.annotations.Test;

import primitive.Menu;
import primitive.SEL;
import sitoolsObjects.Form;

public class FormCrud {
	@Test(groups = { "admin" }, dependsOnGroups = { "initClientAdmin.*" })
	public void createFormProject() throws Exception {
		Menu.select("formsNodeId");
		SEL.sleep(1000);

	    Form.deleteIfExists("PgSelenium", "FormPgSelenium");
	    Form.create("PgSelenium", "FormPgSelenium");
	    
		
	}
}

package test.form;

import org.testng.annotations.Test;

import primitive.Menu;
import primitive.SEL;
import sitoolsObjects.FormProject;

public class FormProjectCrud {
	@Test(groups = { "admin" }, dependsOnGroups = { "initClientAdmin.*" })
	public void createFormProject() throws Exception {
		Menu.select("multiDsNodeId");
		SEL.sleep(1000);

	    FormProject.deleteIfExists("ProjectTestSelenium", "FormSelenium");
	    FormProject.create("ProjectTestSelenium", "FormSelenium");
	    
		
	}
}

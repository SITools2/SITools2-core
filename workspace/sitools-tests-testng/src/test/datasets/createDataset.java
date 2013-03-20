package test.datasets;

import org.testng.annotations.Test;

import primitive.Datasource;
import primitive.Menu;
import primitive.SEL;
import primitive.SELTestCase;
import sitoolsObjects.Dataset;
import sitoolsObjects.Project;
import static junit.framework.Assert.fail;

public class createDataset extends SELTestCase {

	@Test(groups = { "admin" }, dependsOnGroups = { "initClientAdmin.*" })
	public void createAndActiveDatasource() throws Exception {
		Menu.select("DatabaseNodeId");
		SEL.sleep(1000);
		String datasourceName = "PostgreSQL";
		if (selenium
				.isElementPresent("//div[@id='databaseBoxId']/descendant::div[text()='"
						+ datasourceName + "']")) {
			Datasource.deleteDatasource(datasourceName);
		}

		String url = "jdbc:postgresql://localhost:5432/CNES";
		Datasource.createDatasource(url, datasourceName, "/postgres",
				"PostgreSQL");
		if (!selenium
				.isElementPresent("//div[@id='databaseBoxId']/descendant::div[text()='"
						+ datasourceName + "']")) {
			fail("datasource Not Created");
		}
		Datasource.activeDatasource(datasourceName);

		url = "jdbc:MSqlSelenium://odysseus2.silogic.fr:3306/cnes-fuse";
		datasourceName = "MSqlSelenium";
		if (selenium
				.isElementPresent("//div[@id='databaseBoxId']/descendant::div[text()='"
						+ datasourceName + "']")) {
			Datasource.deleteDatasource(datasourceName);
		}
		Datasource.createDatasource(url, datasourceName, "/MSqlSelenium", "MSqlSelenium");

		SEL.waitPresence("//div[@id='databaseBoxId']/descendant::div[text()='"
				+ datasourceName + "']");
		Datasource.activeDatasource("MSqlSelenium");
	}

	@Test(groups = { "admin" }, dependsOnGroups = { "initClientAdmin.*" }, dependsOnMethods = { "createAndActiveDatasource" })
	public void createAndActiveDataset() throws Exception {
		Menu.select("datasetsSqlNodeId");
		SEL.sleep(1000);
		String name = "DatasetMSqlSelenium";
		while (selenium
				.isElementPresent("//div[@id='datasetsBoxId']/descendant::div[text()='"
						+ name + "']")) {
			Dataset.deleteDataset(name);
		}

		Dataset.createDataset(name, 0);

		SEL.waitPresence("//div[@id='datasetsBoxId']/descendant::div[text()='"
				+ name + "']");
		Dataset.addSemantic(name);
		Dataset.activeDataset(name);
	}

	@Test(groups = { "admin" }, dependsOnGroups = { "initClientAdmin.*" }, dependsOnMethods = { "createAndActiveDataset" })
	public void createProject() throws Exception {
		Menu.select("projectsNodeId");
//		SEL.sleep(1000);
//		String name = "ProjectTestSelenium";
//		while (selenium
//				.isElementPresent("//div[@id='projectsBoxId']/descendant::div[text()='"
//						+ name + "']")) {
//			Project.deleteProject(name);
//			SEL.sleep(1000);
//		}
//		SEL.sleep(1000);
////		Project.createProject(name, "DatasetMSqlSelenium");
//		Project.activeProject(name);
//		
//		SEL.waitPresence("//div[@id='projectsBoxId']/descendant::div[text()='" + name + "']");

	}

}
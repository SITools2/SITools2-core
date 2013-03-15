package test.datasets;

import org.testng.annotations.Test;

import primitive.Datasource;
import primitive.Menu;
import primitive.SEL;
import primitive.SELTestCase;
import sitoolsObjects.Dataset;
import sitoolsObjects.Project;

public class DatasetCrud extends SELTestCase {

	/**
	 * Method test wich creates a Datasource named PGSelenium (delete it first if created) 
	 * and another on Mysql called MSqlSelenium. 
	 * @throws Exception
	 */
	@Test(groups = { "admin" }, dependsOnGroups = { "initClientAdmin.*" })
	public void createDatasource() throws Exception {
		Menu.select("DatabaseNodeId");
		SEL.sleep(1000);

	    String url = "jdbc:postgresql://localhost:5432/CNES";
		Datasource.deleteIfExists("PGSelenium");
	    Datasource.createDatasource(url, "PGSelenium", "/postgres", "PostgreSQL");
		Datasource.activeDatasource("PGSelenium");
		
	    url = "jdbc:mysql://odysseus2.silogic.fr:3306/cnes-fuse";
	    Datasource.deleteIfExists("MSqlSelenium");
	    Datasource.createDatasource(url, "MSqlSelenium", "/MSqlSelenium", "MySQL");
		Datasource.activeDatasource("MSqlSelenium");
	}
	
	@Test(groups = { "admin" }, dependsOnGroups = { "initClientAdmin.*" }, dependsOnMethods = { "createDatasource" })
	public void createDataset() throws Exception {
		Menu.select("datasetsSqlNodeId");
		SEL.sleep(1000);
		String name = "DatasetMSqlSelenium";
		
		Dataset.deleteIfExists(name);
		Dataset.createDataset(name, 0);
		Dataset.addSemantic(name);
		Dataset.activeDataset(name);
		
	    selenium.open("/sitools/client-admin/");
	    selenium.waitForPageToLoad("10000");
	    SEL.sleep(5000);
		Menu.select("datasetsSqlNodeId");
		SEL.sleep(2000);
		
		name = "PgSelenium";
		Dataset.deleteIfExists(name);
		Dataset.createDataset(name, 1);
		Dataset.addSemantic(name);
		Dataset.activeDataset(name);
	}
	@Test(groups = { "admin" }, dependsOnGroups = { "initClientAdmin.*" }, dependsOnMethods = {"createDataset" })
	public void createProject() throws Exception {
		Menu.select("projectsNodeId");
		SEL.sleep(1000);
		
		String[] datasets = {"DatasetMSqlSelenium", "PgSelenium"};
		Project.deleteIfExists("ProjectTestSelenium");
		Project.createProject("ProjectTestSelenium", datasets);
		Project.activeProject("ProjectTestSelenium");
	}
	
}
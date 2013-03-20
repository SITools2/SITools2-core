package test.datasets;

import org.testng.Assert;
import org.testng.annotations.Test;

import primitive.Menu;
import primitive.SEL;
import primitive.SELTestCase;
import sitoolsObjects.Collection;
import sitoolsObjects.ConceptTemplate;

public class CollectionCrud extends SELTestCase {

	/**
	 * Adds a new Collection called CollectionSelenium
	 * @throws Exception
	 */
	@Test(groups = { "admin" }, dependsOnGroups = { "initClientAdmin.*" }, description="Creates a Collection named CollectionSelenium (delete it first if exists)")
	public void create() throws Exception {
		Menu.select("collectionsNodeId");
		Collection.deleteIfExists("CollectionSelenium");
		Collection.create("CollectionSelenium");
		SEL.sleep(1000);
		Assert.assertTrue(SEL.isTextPresent("CollectionSelenium"));
	}
}


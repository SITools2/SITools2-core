package test.datasets;

import org.testng.Assert;
import org.testng.annotations.Test;

import primitive.Menu;
import primitive.SEL;
import primitive.SELTestCase;
import sitoolsObjects.ConceptTemplate;
import sitoolsObjects.Dictionary;

public class Semantic extends SELTestCase {

	@Test(groups = { "admin" }, dependsOnGroups = { "initClientAdmin.*" })
	public void createDictionaryTemplate() throws Exception {
		Menu.select("templateNodeId");
		ConceptTemplate.deleteIfExists("ConceptWithContext");
		ConceptTemplate.createContextTemplate("ConceptWithContext");
		SEL.sleep(1000);
		Assert.assertTrue(SEL.isTextPresent("ConceptWithContext"));
		
		ConceptTemplate.deleteIfExists("EmptyConcept");
		ConceptTemplate.createEmptyTemplate("EmptyConcept");
		SEL.sleep(1000);
		Assert.assertTrue(SEL.isTextPresent("EmptyConcept"));
		
	}
	@Test(groups = { "admin" }, dependsOnGroups = { "initClientAdmin.*" }, dependsOnMethods = {"createDictionaryTemplate" })
	public void createDictionary() throws Exception {
		Menu.select("dictionaryNodeId");
		Dictionary.deleteIfExists("DictionarySelenium");
		Dictionary.createDictionary("DictionarySelenium");
		SEL.sleep(1000);
		Assert.assertTrue(SEL.isTextPresent("DictionarySelenium"));
	}
	
}

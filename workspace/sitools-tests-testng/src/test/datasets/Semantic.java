 /*******************************************************************************
 * Copyright 2010-2014 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
 *
 * This file is part of SITools2.
 *
 * SITools2 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SITools2 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with SITools2.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
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

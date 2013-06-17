 /*******************************************************************************
 * Copyright 2010-2013 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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

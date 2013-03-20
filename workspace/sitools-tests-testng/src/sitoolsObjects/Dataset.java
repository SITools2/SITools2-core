package sitoolsObjects;

import imageChooser.imageChooser;
import primitive.Menu;
import primitive.SEL;
import primitive.SEL4Ext;

import com.thoughtworks.selenium.Selenium;

public class Dataset {
	static Selenium selenium = SEL.getSelenium();
	static String MSqlSelenium_JOIN_CONDITION = "LCASE(CONCAT(CONCAT(HEADERS.DATASET,IAPDATASETS.DATASET),OBJECT_CLASS.OBJ_NBR))";
	static String PG_JOIN_CONDITION = "\"headers\".dataset||\"iapdatasets\".dataset||\"object_class\".obj_nbr";
	static String[] MSqlSelenium_TABLES = {"HEADERS", "IAPDATASETS", "OBJECT_CLASS"};
	static String[] PG_TABLES = {"headers", "iapdatasets", "object_class"};
	
	/**
	 * Create a dataset
	 * @param name Name 
	 * @param datasource 0 -> MSqlSelenium; 1 -> PGSelenium
	 */
	public static void createDataset(String name, int datasource) throws Exception {
		String dbValue;
		String [] tablesArray;
		String pkey;
		switch (datasource) {
		case 0:
			dbValue = "MSqlSelenium";
			tablesArray = MSqlSelenium_TABLES;
			pkey = MSqlSelenium_JOIN_CONDITION;
			break;
		case 1:
			dbValue = "PGSelenium";
			tablesArray = PG_TABLES;
			pkey = PG_JOIN_CONDITION;
			break;

		default:
			dbValue = "MSqlSelenium";
			tablesArray = MSqlSelenium_TABLES;
			pkey = MSqlSelenium_JOIN_CONDITION;
		}
		SEL.click(SEL.locByText("button", "Create"));

		SEL.waitPresence(SEL.locByName("input", "name"));
		selenium.type(SEL.locByName("input", "name"), name);
		selenium.type(SEL.locByName("input", "description"), "Description");

		String formProp = SEL.locByItem("datasetsMultiTablesPanel", "form", true);
		
		//click on image
		selenium.click(SEL4Ext.locComboTriggerIcon(formProp, "image"));
		
		imageChooser.selectImage(1);
		
		// click on Datasource
//		selenium.click("xpath=//div[@id='datasetsMultiTablesPanel']/descendant::form/div[4]/descendant::img");
		selenium.click(SEL4Ext.locComboTriggerIcon(formProp, "comboDataSource"));
		//choix de la connexion BDD : 
//		selenium.click("xpath=//div[@class='x-combo-list-inner']/div[text()='MSqlSelenium']");
		selenium.click(SEL4Ext.locSelectComboValue(dbValue));
		SEL.sleep(1000);
		SEL.pressEnter();
		
		SEL.sleep(1000);
		selenium.type(SEL.locById("sitoolsAttachementForUsers"), "/dataset/" + name);
		// fin du premier panel

		// 2° panel : select Propriétés
//		selenium.click("//div[@id='datasetsMultiTablesPanel']/descendant::*[text()='Properties']");
		selenium.click(SEL.locByText("datasetsMultiTablesPanel", "*", "Properties", true));
		selenium.click(SEL.locByText("componentGridProperties", "button", "Create", true));
		selenium.click(SEL.locByText("componentGridProperties", "button", "Create", true));
		selenium.click(SEL.locByText("componentGridProperties", "button", "Create", true));
		selenium.click(SEL.locByText("componentGridProperties", "button", "Create", true));
		String gridPropertyId = "componentGridProperties";
		for (int i = 1; i < 5; i++) {
			String type = "";
			String propertyValue = "";
			switch (i) {
			case 1:
				type = "String";
				propertyValue = "TextValue";
				break;
			case 2:
				type = "Numeric";
				propertyValue = "100";
				break;
			case 3:
				type = "Date";
				propertyValue = "2012-01-01 00:00:00";
				
				break;
			case 4:
				type = "Enum";
				propertyValue = "1|2|3|4";
				break;

			default:
				break;
			}
			SEL4Ext.editGridValue(gridPropertyId, i, 1, "prop " + String.valueOf(i));
			
			SEL4Ext.editComboGridValue(gridPropertyId, i, 2, type);
			
			SEL.doubleClickAt(SEL4Ext.locGridCell(gridPropertyId, i, 3), "5,5");
			SEL.sleep(500);			
			if ("Date".equals(type)) {
				SEL.click(SEL4Ext.locComboEditorIcon());
				SEL.sleep(500);
				SEL.click(SEL4Ext.locToday());
			}
			else {
				SEL.type(SEL4Ext.selectTextEditor(), propertyValue);
			}
			SEL.pressEnter();
			
		}
		
		
		
		// 3° panel : select Tables
//		selenium.click("xpath=//div[@id='datasetsMultiTablesPanel']/descendant::ul[1]/li[3]/a[2]");
		selenium.click(SEL.locByText("datasetsMultiTablesPanel", "*", "Select tables", true));
		// attente du chargement des tables
		
		String tableJDBCBody = SEL.locByCss("Tables_JDBC", "div", "x-grid3-body", true);
		SEL.waitPresence(SEL.locByItem(tableJDBCBody, "div[1]", false));
		// click sur la premiere ligne
		selenium.controlKeyDown();
		for (int j = 0; j < tablesArray.length; j++) {
			SEL.mouseDown(SEL4Ext.locGridLine("Tables_JDBC", tablesArray[j]));
		}
		selenium.controlKeyUp();
		// click sur bouton >
		selenium.click(SEL.locByStyle("selectTablesPanel", "button", "simple-arrow-right.png", true));

		// 4° panel : select fields
		selenium.click(SEL.locByText("datasetsMultiTablesPanel", "*", "Select fields", true));
		// attente du chargement des fields
		String gridColumnsTables = SEL.locByCss("gridColumnTables", "div", "x-grid3-body", true);
		SEL.waitPresence(SEL.locByItem(gridColumnsTables, "div[1]", true));
		// ajouter tous les fields
		selenium.click(SEL.locByStyle("datasetsMultiTablesPanel_SelectFields", "button", "double-arrow-right.png", true));
//		selenium.click("//div[@id='datasetsMultiTablesPanel_SelectFields']/div/div/div/div/div/div/div[2]/descendant::table[2]");

		// 5° panel : fields Setup
		selenium.click(SEL.locByText("datasetsMultiTablesPanel", "*", "Fields setup", true));
		// attente du chargement des fields
		SEL.sleep(1000);
		//construction de la clé primaire
		selenium.click(SEL.locByText("gridColumnSelect", "button", "Create", true));
		selenium.type(SEL.locByItem("columnPropId", "input[2]", true), "primary_key");
		selenium.type(SEL.locByItem("columnPropId", "textarea", true), pkey);
		selenium.click(SEL.locByItem("columnPropId", "button[1]", true));
		
		//update du column ALias de headers.dataset : 
		
		SEL4Ext.editGridValue(SEL.locByCss("gridColumnSelect", "div", "x-grid3-unlocked", true), "dataset", 3, "dataset2");
		selenium.mouseDown("xpath=//div[text()='primary_key'][1]/parent::*/parent::*/td[11]/div/div");

		//update du de l'ordre : 
		selenium.doubleClick("//div[text()='primary_key'][1]/parent::*/parent::*/td[10]");
		SEL.sleep(1000);
		selenium.click(SEL4Ext.locComboEditorIcon());
		SEL.sleep(1000);
		selenium.click(SEL4Ext.locSelectComboValue("ASC"));
		SEL.sleep(1000);
		selenium.keyPressNative(new Integer (java.awt.event.KeyEvent.VK_ENTER).toString());
		SEL.sleep(1000);
		
		
		// 6° panel : conditions :
		selenium.click("xpath=//div[@id='datasetsMultiTablesPanel']/descendant::ul[1]/li[6]/a[2]");
		SEL.sleep(1000);
		//ajout de deux condition : 
		selenium.contextMenu("//a[@class='x-tree-node-anchor']/span[text()='INNER_JOIN " + tablesArray[1] + "']");
		selenium.click("//span[text()='Add Join Condition']");
		
		SEL.click(SEL4Ext.locComboTriggerIcon("joinConditionWin", "leftAttributeField"));
		try {
			SEL.click(SEL4Ext.locSelectComboValue(tablesArray[1].toUpperCase() + ".dataset"));
		}
		catch (Exception e) {
			SEL.click(SEL4Ext.locSelectComboValue(tablesArray[0].toUpperCase() + ".dataset"));
		}
		
		SEL.click(SEL4Ext.locComboTriggerIcon("joinConditionWin", "rightAttributeField"));
		try {
			SEL.click(SEL4Ext.locSelectComboValue(tablesArray[0].toUpperCase() + ".dataset2"));
		}
		catch (Exception e) {
			SEL.click(SEL4Ext.locSelectComboValue(tablesArray[1].toUpperCase() + ".dataset2"));
		}
		
		SEL.click("//div[@id='joinConditionWin']/descendant::button[text()='OK']");
		
		selenium.contextMenu("//a[@class='x-tree-node-anchor']/span[text()='INNER_JOIN " + tablesArray[2] + "']");
		SEL.click("//span[text()='Add Join Condition']");
		
		SEL.click(SEL4Ext.locComboTriggerIcon("joinConditionWin", "leftAttributeField"));
		SEL.click(SEL4Ext.locSelectComboValue(tablesArray[2].toUpperCase() + ".obj_nbr"));
		
		SEL.sleep(2000);
		SEL.click(SEL4Ext.locComboTriggerIcon("joinConditionWin", "rightAttributeField"));
		SEL.click(SEL4Ext.locSelectComboValue(tablesArray[0].toUpperCase() + ".objclass"));
		
		SEL.sleep(2000);
		
		SEL.click("//div[@id='joinConditionWin']/descendant::button[text()='OK']");
		

		
		selenium.click("xpath=//div[@id='datasetsMultiTablesPanel']/descendant::button[contains(.,'OK')]");

	}
	
	public static void activeDataset(String name) throws Exception {
		Menu.select("datasetsSqlNodeId");
		SEL.sleep(1000);
		SEL.waitPresence("xpath=//div[@id='datasetsBoxId']/descendant::div[text()='" + name + "']");
		selenium.mouseDown("xpath=//div[@id='datasetsBoxId']/descendant::div[text()='" + name + "']");
		selenium.click("xpath=//button[contains(.,'Enable')]");
		SEL.sleep(1000);
	}
	
	
	public static void addSemantic(String name) throws Exception {
		Menu.select("datasetsSqlNodeId");
		SEL.sleep(1000);
		SEL.waitPresence(SEL4Ext.locGridLine("datasetsBoxId", name));
		selenium.mouseDown(SEL4Ext.locGridLine("datasetsBoxId", name));
		selenium.click(SEL.locByText("datasetsBoxId", "button", "Semantic", true));
		String semWin = SEL4Ext.getActiveWinId();
		SEL.click(SEL4Ext.locComboTriggerIcon(semWin, 1));
		SEL.click(SEL4Ext.locSelectComboValue("DictionarySelenium"));
		
		//Mapping x_pos avec X
		String dsColGridId = SEL4Ext.getPanelIdFromTitle(semWin, "s columns");
		SEL.mouseDown(SEL4Ext.locGridLine(dsColGridId, "x_pos"));
		
		String dsDicoGridId = SEL4Ext.getPanelIdFromTitle(semWin, "s concepts");
		SEL.mouseDown(SEL4Ext.locGridLine(dsDicoGridId, "X"));
		
		SEL.click(SEL.locByText(semWin, "button", "Map", true));
		
		//Mapping y_pos avec Y
		SEL.mouseDown(SEL4Ext.locGridLine(dsColGridId, "y_pos"));
		SEL.mouseDown(SEL4Ext.locGridLine(dsDicoGridId, "Y"));
		SEL.click(SEL.locByText(semWin, "button", "Map", true));
		
		//Mapping x_pos avec X
		SEL.mouseDown(SEL4Ext.locGridLine(dsColGridId, "z_pos"));
		SEL.mouseDown(SEL4Ext.locGridLine(dsDicoGridId, "Z"));
		SEL.click(SEL.locByText(semWin, "button", "Map", true));
		
		SEL.click(SEL.locByText(semWin, "button", "Save and Close", true));
		
		SEL.sleep(1000);
	}

	public static void modifyDataset() throws Exception {
		SEL.waitPresence("xpath=//button[contains(.,'Create')]");
		selenium.click("xpath=//button[contains(.,'Modify')]");

		SEL.waitPresence("xpath=//input[@name='name']");
		selenium.type("xpath=//input[@name='name']", "DataSource updated");

		selenium.click("xpath=//button[contains(.,'OK')]");

		SEL.sleep(1000);

		// selenium.getSelectedValue("toto");
	}

	public static void deleteDataset(String name) throws Exception {
		SEL.waitPresence("xpath=//div[@id='datasetsBoxId']/descendant::div[text()='" + name + "']");
		selenium.mouseDown("xpath=//div[@id='datasetsBoxId']/descendant::div[text()='" + name + "']");
		selenium.click("xpath=//button[contains(.,'Delete')]");

		SEL.waitPresence("//button[text()='Yes']");
		selenium.click("//button[text()='Yes']");
		SEL.sleep(1000);

		// selenium.getSelectedValue("toto");
	}
	public static void deleteIfExists(String name) throws Exception {
		if (SEL.isTextPresent(name)) {
			deleteDataset(name);
		}
	}
	
}

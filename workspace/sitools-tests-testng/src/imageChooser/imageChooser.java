package imageChooser;

import primitive.SEL;

import com.thoughtworks.selenium.Selenium;

public class imageChooser {
	static Selenium selenium = SEL.getSelenium();
	public static void selectImage (int imageNumber) throws Exception {
	    SEL.waitPresence("xpath=//div[@id='imageChooserDataViewId']/div[" + imageNumber + "]");
		selenium.click("xpath=//div[@id='imageChooserDataViewId']/div[" + imageNumber + "]");
	    selenium.click("xpath=//*[@id='ok-btn']/descendant::tr[2]/td[2]");
	}
}

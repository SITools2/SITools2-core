package primitive;

import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;

import com.thoughtworks.selenium.Selenium;

public class SELTestCase {

  protected static Selenium selenium = null;

  @BeforeSuite
  public void startSel() throws Exception {
    String host = System.getProperty("selenium.host", "localhost");
    int port = Integer.parseInt(System.getProperty("selenium.port", "4444"));
    String env = System.getProperty("selenium.env", "*firefox");
    String url = System.getProperty("webapp.url", "http://sitools.akka.eu:8184");
    SEL.init(host, port, env, url);
    SEL.start();
  }

  @BeforeTest
  public void setUp() throws Exception {
    selenium = SEL.getSelenium();
    if (selenium == null)
      throw new Exception("Selenium is not initialized");
  }

  @AfterSuite
  public void stopSel() throws Exception {
    SEL.stop();
  }

}

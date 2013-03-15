package fr.cnes.sitools;

import org.junit.BeforeClass;

/**
 * 
 * 
 *
 * @author jp.boignard
 */
public class AbstractSitoolsServerInsecureTestCase extends AbstractSitoolsServerTestCase {

  /**
   * Executed once before all test methods
   */
  @BeforeClass
  public static void before() {
    setSecure(false);
    setup();
    start();
  }

}

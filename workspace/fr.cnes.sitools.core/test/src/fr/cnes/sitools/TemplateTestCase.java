package fr.cnes.sitools;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import org.junit.Test;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;

import fr.cnes.sitools.common.SitoolsSettings;
import fr.cnes.sitools.server.Consts;

/**
 * 
 * TemplateTestCase
 * 
 * @author tx.chevallier
 */
public class TemplateTestCase extends AbstractSitoolsServerTestCase {

  /**
   * get Url to attach
   * 
   * @return url
   */
  protected String getAttachUrl() {
    return SITOOLS_URL + SitoolsSettings.getInstance().getString(Consts.APP_ADMINISTRATOR_URL);
  }

  /**
   * get base url
   * 
   * @return url
   */
  @Override
  protected String getBaseUrl() {
    return super.getBaseUrl() + SitoolsSettings.getInstance().getString(Consts.APP_ADMINISTRATOR_URL);
  }

  /**
   * test retrieve css
   * 
   * @throws Exception exception
   */
  @Test
  public void testRetrieveCss() throws Exception {
    retrieve("css");
  }

  /**
   * test retrieve ftl
   * 
   * @throws Exception exception
   */
  @Test
  public void testRetrieveFtl() throws Exception {
    retrieve("ftl");
  }

  /**
   * test retrieve cgu
   * 
   * @throws Exception exception
   */
  @Test
  public void testRetrieveCgu() throws Exception {
    retrieve("cgu.html");
  }

  /**
   * retrieve
   * 
   * @param attachment string
   * @throws IOException exception
   */
  public void retrieve(String attachment) throws IOException {

    String uri = getBaseUrl() + "/" + attachment;
    ClientResource cr = new ClientResource(uri);

    Representation result = cr.get();
    assertNotNull(result);

  }

}

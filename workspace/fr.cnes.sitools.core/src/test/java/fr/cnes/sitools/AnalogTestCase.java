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
package fr.cnes.sitools;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.restlet.data.MediaType;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.ClientResource;

import fr.cnes.sitools.common.SitoolsSettings;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.server.Consts;
import fr.cnes.sitools.util.RIAPUtils;
import fr.cnes.sitools.util.Util;
import fr.cnes.sitools.utils.GetResponseUtils;

public class AnalogTestCase extends AbstractSitoolsServerTestCase {
  /** The settings */
  private SitoolsSettings settings = SitoolsSettings.getInstance();
  /** The windows analog resource url */
  private String windowsAnalogUrl = "/plugin/windows/analog";
  /** The linux analog resource url */
  private String linuxAnalogUrl = "/plugin/linux/analog";

  /**
   * absolute url for project management REST API
   * 
   * @return url
   */
  protected String getBaseUrl() {
    return super.getBaseUrl() + settings.getString(Consts.APP_ADMINISTRATOR_URL);
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.cnes.sitools.AbstractSitoolsServerTestCase#setUp()
   */
  @Override
  public void setUp() throws Exception {
    super.setUp();
    setMediaTest(MediaType.APPLICATION_XML);
  }

  /**
   * Test of Analog resource
   */
  @Test
  public void testAnalog() {

    String url;
    if (Util.isWindows()) {
      url = windowsAnalogUrl;
    }
    else {
      url = linuxAnalogUrl;
    }
    generateLog(url);
    getLog(url);
  }

  /**
   * Generate log analysis by invoking put on analog resource
   * 
   * @param url
   *          the url to call
   */
  private void generateLog(String url) {
    Representation repr = new StringRepresentation("");
    url = getBaseUrl() + url;

    ClientResource cr = new ClientResource(url);
    Representation result = cr.put(repr, getMediaTest());
    assertNotNull(result);
    Response response = GetResponseUtils.getResponse(getMediaTest(), result, getMediaTest());
    assertNotNull(response);
    assertTrue(response.getSuccess());
    RIAPUtils.exhaust(result);

  }

  /**
   * Get the generated log
   * 
   * @param url
   *          the url to call
   */
  private void getLog(String url) {
    url = getBaseUrl() + url;

    ClientResource cr = new ClientResource(url);
    Representation result = cr.get(getMediaTest());
    assertNotNull(result);
    assertTrue(cr.getStatus().isSuccess());
    RIAPUtils.exhaust(result);

  }

}

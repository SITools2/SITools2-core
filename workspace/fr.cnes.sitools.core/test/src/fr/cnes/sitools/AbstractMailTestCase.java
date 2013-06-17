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
package fr.cnes.sitools;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.junit.Test;
import org.restlet.data.MediaType;
import org.restlet.ext.xstream.XstreamRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;

import com.thoughtworks.xstream.XStream;

import fr.cnes.sitools.common.SitoolsXStreamRepresentation;
import fr.cnes.sitools.common.XStreamFactory;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.mail.model.Mail;
import fr.cnes.sitools.mail.model.MailConfiguration;
import fr.cnes.sitools.server.Consts;
import fr.cnes.sitools.util.RIAPUtils;

/**
 * Test for mail API
 *
 * @author m.marseille (AKKA Technologies)
 */
public abstract class AbstractMailTestCase extends AbstractSitoolsServerTestCase {

  /**
   * absolute url for role management REST API
   * 
   * @return url
   */
  protected String getBaseUrl() {
    return super.getBaseUrl() + settings.getString(Consts.APP_MAIL_ADMIN_URL);
  }

  /**
   * Unit Test
   */
  @Test
  public void testMail() {
    docAPI.setActive(false);
    postMail();
    createWadl(getBaseUrl(), "mails");
  }

  /**
   * Invoke POST on root URL
   */
  private void postMail() {
    
    // Create mail
    Mail mail = new Mail();
    List<String> toList = new ArrayList<String>();
    toList.add("m.gond@akka.eu");
    mail.setSubject("testSubject");
    mail.setFrom("JunitTest");
    mail.setToList(toList);
    mail.setBody("Test");
    
    Representation rep = getRepresentation(mail, getMediaTest());
    ClientResource cr = new ClientResource(getBaseUrl());
    Representation result = cr.post(rep, getMediaTest());
    assertNotNull(result);
    assertTrue(cr.getStatus().isSuccess());
    
  }
  
  /**
   * Response to Representation
   * 
   * @param mail
   *          Mail
   * @param media
   *          MediaType
   * @return Representation
   */
  public static Representation getRepresentation(Mail mail, MediaType media) {
    if (media.equals(MediaType.APPLICATION_JSON) || media.equals(MediaType.APPLICATION_XML)) {
      XStream xstream = XStreamFactory.getInstance().getXStream(media);
      configure(xstream);
      XstreamRepresentation<Mail> rep = new XstreamRepresentation<Mail>(media, mail);
      rep.setXstream(xstream);
      return rep;
    }
    else {
      Logger.getLogger(AbstractSitoolsTestCase.class.getName()).warning("Only JSON or XML supported in tests");
      return null;
    }
  }
  
  /**
   * Configure XStream mapping of a Response object
   * 
   * @param xstream
   *          XStream
   */
  private static void configure(XStream xstream) {
    xstream.autodetectAnnotations(false);
    xstream.alias("response", Response.class);
  }
  
  /**
   * REST API Response Representation wrapper for single or multiple items expexted
   * 
   * @param media
   *          MediaType expected
   * @param representation
   *          service response representation
   * @param dataClass
   *          class expected for items of the Response object
   * @param isArray
   *          if true wrap the data property else wrap the item property
   * @return Response
   */
  public static Response getResponse(MediaType media, Representation representation, Class<?> dataClass, boolean isArray) {
    try {
      if (!media.isCompatible(MediaType.APPLICATION_JSON) && !media.isCompatible(MediaType.APPLICATION_XML)) {
        Logger.getLogger(AbstractSitoolsTestCase.class.getName()).warning("Only JSON or XML supported in tests");
        return null;
      }

      XStream xstream = XStreamFactory.getInstance().getXStreamReader(media);
      xstream.autodetectAnnotations(false);
      xstream.alias("response", Response.class);
      xstream.alias("mailconfig", MailConfiguration.class);
      
      xstream.alias("item", dataClass);
      xstream.alias("item", Object.class, dataClass);

      xstream.aliasField("data", Response.class, "data");

      SitoolsXStreamRepresentation<Response> rep = new SitoolsXStreamRepresentation<Response>(representation);
      rep.setXstream(xstream);

      if (media.isCompatible(getMediaTest())) {
        Response response = rep.getObject("response");

        return response;
      }
      else {
        Logger.getLogger(AbstractSitoolsTestCase.class.getName()).warning("Only JSON or XML supported in tests");
        return null;
        // TODO complete test with ObjectRepresentation
      }
    }
    finally {
      RIAPUtils.exhaust(representation);
    }
  }

  
}
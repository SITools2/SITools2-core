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
package fr.cnes.sitools.security.captcha;

import org.restlet.data.CookieSetting;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Get;
import org.restlet.resource.ResourceException;

import fr.cnes.sitools.common.SitoolsResource;
import fr.cnes.sitools.common.SitoolsSettings;
import fr.cnes.sitools.common.application.SitoolsApplication;

/**
 * The Resource to get Captcha
 * 
 * @author jp.boignard (AKKA Technologies)
 */
public class CaptchaResource extends SitoolsResource {

  @Override
  protected void doInit() {
    // TODO Auto-generated method stub
    super.doInit();

    getVariants().clear();
    getVariants().add(new Variant(MediaType.IMAGE_PNG));
    getVariants().add(new Variant(MediaType.IMAGE_JPEG));
  }

  /**
   * Get a new image captcha representation in the format according to the Accepted
   * 
   * @param variant
   *          accepted image format
   * @return CaptchaRepresentation (an image)
   * 
   */
  @Override
  @Get
  protected Representation get(Variant variant) {

    // List<Preference<MediaType>> medias = getRequest().getClientInfo().getAcceptedMediaTypes();
    // if (medias.get(0).equals(MediaType.IMAGE_PNG))
    // getPreferredVariant(variants)

    SitoolsSettings settings = ((SitoolsApplication) getApplication()).getSettings();
    CaptchaContainer container = (CaptchaContainer) getApplication().getContext().getAttributes()
        .get("Security.Captcha.CaptchaContainer");

    String width = getRequest().getResourceRef().getQueryAsForm()
        .getFirstValue("width", settings.getString("Security.Captcha.default.width"));
    String height = getRequest().getResourceRef().getQueryAsForm()
        .getFirstValue("height", settings.getString("Security.Captcha.default.height"));
    MediaType media = (variant != null) ? variant.getMediaType() : MediaType.IMAGE_PNG;
    String length = settings.getString("Security.Captcha.default.length", "8");

    if (container != null) {
      Captcha captcha = container.post(Integer.parseInt(width), Integer.parseInt(height), Integer.parseInt(length));

      String captchaID = captcha.getId();
      CookieSetting cookie = new CookieSetting("captcha", captchaID);
      cookie.setPath("/");
      getResponse().getCookieSettings().add(cookie);

      return new CaptchaRepresentation(media, captcha);
    }

    throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
  }

  /**
   * CaptchaResource Describe
   */
  public void sitoolsDescribe() {
    setName("CaptchaResource");
    setDescription("Resource that returns a captcha image");
    setNegotiated(true);
  }

}

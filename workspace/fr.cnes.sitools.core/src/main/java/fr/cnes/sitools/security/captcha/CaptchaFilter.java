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

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Status;
import org.restlet.routing.Filter;

/**
 * Filter on captcha if relative request parameters are present
 * 
 * @author jp.boignard (AKKA Technologies)
 */
public class CaptchaFilter extends Filter {

  /**
   * CaptchaContainer providing a Captcha getter
   */
  private CaptchaContainer captchaContainer = null;

  /**
   * Constructor
   * 
   * @param context
   *          Context
   */
  public CaptchaFilter(Context context) {
    super(context);
    captchaContainer = (CaptchaContainer) context.getAttributes().get("Security.Captcha.CaptchaContainer");
    if (captchaContainer == null) {
      return;
    }

    // Security.filter.captcha.enabled is used (specially in test) to force captcha filter not to be used.
    Boolean withCaptcha = (Boolean) context.getAttributes().get("Security.Captcha.enabled");
    if (withCaptcha != null && !withCaptcha) {
      captchaContainer = null;
    }
  }

  @Override
  protected int beforeHandle(Request request, Response response) {

    if (captchaContainer == null) {
      return super.beforeHandle(request, response);
    }

    // Keep captcha request parameters
    String idCaptcha = request.getResourceRef().getQueryAsForm().getFirstValue("captcha.id");
    String keyCaptcha = request.getResourceRef().getQueryAsForm().getFirstValue("captcha.key");

    if ((null == idCaptcha) || (null == keyCaptcha)) {
      response.setStatus(Status.CLIENT_ERROR_FORBIDDEN);
      return STOP;
    }

    // Check captcha
    Captcha captcha = captchaContainer.get(idCaptcha);

    if ((null == captcha) || !captcha.check(keyCaptcha)) {
      response.setStatus(Status.CLIENT_ERROR_FORBIDDEN);
      return STOP;
    }

    return super.beforeHandle(request, response);
  }

}

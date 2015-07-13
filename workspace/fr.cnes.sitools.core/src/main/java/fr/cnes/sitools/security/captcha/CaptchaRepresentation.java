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

import java.io.IOException;
import java.io.OutputStream;

import javax.imageio.ImageIO;

import org.restlet.data.MediaType;
import org.restlet.representation.OutputRepresentation;

/**
 * Representation to represent a Catpcha Object
 * 
 * @author jp.boignard (AKKA Technologies)
 */
public class CaptchaRepresentation extends OutputRepresentation {
  /** The Captcha Object */
  private Captcha captcha = null;

  /**
   * Default constructor
   * 
   * @param mediaType
   *          the {@link MediaType} needed
   */
  public CaptchaRepresentation(MediaType mediaType) {
    super(mediaType);
  }

  /**
   * Constructor with {@link MediaType} and {@link Captcha}
   * 
   * @param mediaType
   *          the {@link MediaType}
   * @param captcha
   *          the {@link Captcha}
   */
  public CaptchaRepresentation(MediaType mediaType, Captcha captcha) {
    super(mediaType);
    this.captcha = captcha;
  }

  @Override
  public void write(OutputStream arg0) throws IOException {
    // String[] formats = ImageIO.getReaderFormatNames();
    // String[] writers = ImageIO.getWriterFormatNames();

    String format = "png";

    if (getMediaType().equals(MediaType.IMAGE_JPEG)) {
      format = "jpeg";
    }
    else if (getMediaType().equals(MediaType.IMAGE_PNG)) {
      format = "png";
    }

    // FileOutputStream fos = new FileOutputStream("C:/output.png");
    // ImageIO.write(captcha.getImage(), format, fos);
    // fos.close();

    ImageIO.write(captcha.getImage(), format, arg0);
  }

}

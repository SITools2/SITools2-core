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

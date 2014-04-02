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

import java.awt.image.BufferedImage;

import nl.captcha.backgrounds.GradiatedBackgroundProducer;
import nl.captcha.gimpy.FishEyeGimpyRenderer;
import nl.captcha.noise.StraightLineNoiseProducer;
import nl.captcha.text.producer.DefaultTextProducer;

/**
 * Sitools Captcha
 * 
 * @author jp.boignard (AKKA Technologies)
 */
public class Captcha {

  /** chars used for answer producer */
  private static final char[] CHARS = new char[] {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'k', 'm', 'n', 'p', 'r',
      's', 'w', 'x', 'y', '2', '3', '4', '5', '6', '7', '8'};

  /** captcha identifier (System.currentTimeMillis) */
  private long id;

  /** captcha generated answer */
  private String answer;

  /** captcha generated image */
  private BufferedImage image;

  // private long timeMillis;

  /**
   * Constructor
   * 
   * @param width
   *          int width in pixel of the required generated image
   * @param height
   *          int heigth in pixem of the required generated image
   * @param length
   *          int number of characters for the generated answer
   */
  public Captcha(int width, int height, int length) {

    long timeMillis = System.currentTimeMillis();

    // id = UUID.randomUUID().toString();
    id = timeMillis;

    nl.captcha.Captcha.Builder builder = new nl.captcha.Captcha.Builder(width, height);

    // Noise
    builder.addBackground(new GradiatedBackgroundProducer());
    builder.addNoise(new StraightLineNoiseProducer());
    builder.gimp(new FishEyeGimpyRenderer());
    builder.addBorder();

    // Text
    DefaultTextProducer textProducer = new DefaultTextProducer(length, CHARS);
    nl.captcha.Captcha captcha = builder.addText(textProducer).build();

    answer = captcha.getAnswer();
    image = captcha.getImage();
  }

  /**
   * Get captcha identifier
   * 
   * @return String to be used in a Container.
   */
  public String getId() {
    return String.valueOf(id);
  }

  // public String getAnswer() {
  // return answer;
  // }

  /**
   * Get the generated image. After a first call, the image could be null.
   * 
   * @return BufferedImage
   */
  public BufferedImage getImage() {
    return image;
  }

  /**
   * get SystemTimeMillis at Captcha new instance Time.
   * 
   * @return long
   */
  public long getTimeMillis() {
    return id; // timeMillis;
  }

  /**
   * Compare internal masked answer with a response.
   * 
   * @param response
   *          String
   * @return boolean true if identical
   */
  public boolean check(String response) {
    return (answer.equals(response));
  }

  /**
   * get the answer of the captcha
   * 
   * 
   * @return String, the answer of the captcha.Useful for tests
   */
  public String getAnswer() {
    return answer;
  }

}

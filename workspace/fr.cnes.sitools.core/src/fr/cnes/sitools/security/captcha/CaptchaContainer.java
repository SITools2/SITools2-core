    /*******************************************************************************
 * Copyright 2010-2016 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Basic in memory Captcha container
 * 
 * @author jp.boignard (AKKA Technologies)
 */
public class CaptchaContainer {

  /** in memory list of generated capchas */
  private Map<String, Captcha> captchas = new Hashtable<String, Captcha>();

  /** max duration of captcha in seconds */
  private int maxduration = 300;

  /** max number of captchas for prevent attacks */
  private int maxsize = 50;

  /**
   * Getter an ever existent captcha Can be do only one time.
   * 
   * @param id
   *          captcha identifier
   * @return Captcha
   */
  public Captcha get(String id) {
    Captcha captcha = captchas.get(id);
    captchas.remove(id);
    return captcha;
  }

  /**
   * Get a new captcha
   * @param width int width in pixel of the required generated image
   * @param height int heigth in pixem of the required generated image
   * @param length int number of characters for the generated answer
   * @return Captcha
   */
  public Captcha post(Integer width, Integer height, Integer length) {

    recyclingDuration(maxduration);

    recyclingSize(maxsize);

    Captcha captcha = new Captcha(width, height, length);
    captchas.put(captcha.getId(), captcha);
    
    return captcha;
  }

  /**
   * Delete a captcha when no more useful
   * 
   * @param id
   *          Captcha identifier
   */
  public void delete(String id) {
    captchas.remove(id);
  }

  /**
   * recycling old captchas
   * 
   * @param seconds maxduration of a captcha in container.
   */
  public void recyclingDuration(int seconds) {
    long millis = System.currentTimeMillis();
    Set<String> captchasKeySet = captchas.keySet();
    for (Iterator<String> iterator = captchasKeySet.iterator(); iterator.hasNext();) {
      String key = (String) iterator.next();
      Captcha captcha = captchas.get(key);
      if (captcha.getTimeMillis() + (seconds * 1000) > millis) {
        iterator.remove();
      }
    }
  }

  /**
   * limiting captchas
   * 
   * @param limit max number of captcha in container
   */
  public void recyclingSize(int limit) {
    if (captchas.size() < limit) {
      return;
    }

    // detect oldest Captcha
    long oldestTimeMillis = System.currentTimeMillis();
    String oldestCaptchaId = null;
    Set<String> captchasKeySet = captchas.keySet();
    for (Iterator<String> iterator = captchasKeySet.iterator(); iterator.hasNext();) {
      String key = (String) iterator.next();
      Captcha captcha = captchas.get(key);
      if (captcha.getTimeMillis() < oldestTimeMillis) {
        oldestTimeMillis = captcha.getTimeMillis();
        oldestCaptchaId  = captcha.getId();
      }
    }
    if (oldestCaptchaId != null) {
      captchas.remove(oldestCaptchaId);
    }
  }

}

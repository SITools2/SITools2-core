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
package fr.cnes.sitools.common.model;

/**
 * Update plans for a resource, affecting expiration dates and modification dates
 * of a request answer.
 * 
 * 
 * @author jp.boignard (AKKA Technologies)
 * 
 */
public final class UpdatingPlan {

  /** Monday plan. */
  private boolean mo;

  /** Tuesday plan. */
  private boolean tu;

  /** Wednesday plan. */
  private boolean we;

  /** Thursday plan. */
  private boolean th;

  /** Friday plan. */
  private boolean fr;

  /** Saturday plan. */
  private boolean sa;

  /** Sunday plan. */
  private boolean su;

  /** Hour plan. */
  private int hour;

  /** Time duration plan. */
  private int duration;

  /**
   * Constructor.
   */
  private UpdatingPlan() {
    super();
  }

  /**
   * Gets the Monday value.
   * 
   * @return true if Monday
   */
  public boolean isMo() {
    return mo;
  }

  /**
   * Sets the value of Monday
   * 
   * @param mon
   *          true if set to Monday
   */
  public void setMo(final boolean mon) {
    this.mo = mon;
  }

  /**
   * Gets the Tuesday value.
   * 
   * @return true of Tuesday is set
   */
  public boolean isTu() {
    return tu;
  }

  /**
   * Sets the value of Tuesday.
   * 
   * @param tue
   *          true to set Tuesday
   */
  public void setTu(final boolean tue) {
    this.tu = tue;
  }

  /**
   * Gets the Wednesday value.
   * 
   * @return true if Wednesday is set.
   */
  public boolean isWe() {
    return we;
  }

  /**
   * Sets the value of Wednesday.
   * 
   * @param wen
   *          true to set Wednesday.
   */
  public void setWe(final boolean wen) {
    this.we = wen;
  }

  /**
   * Gets the Thursday value.
   * 
   * @return true if Thursday is set.
   */
  public boolean isTh() {
    return th;
  }

  /**
   * Sets the value of Thursday.
   * 
   * @param thu
   *          true to set Thursday
   */
  public void setTh(final boolean thu) {
    this.th = thu;
  }

  /**
   * Gets the Friday value.
   * 
   * @return true if Friday is set.
   */
  public boolean isFr() {
    return fr;
  }

  /**
   * Sets the value of Friday.
   * 
   * @param fri
   *          true to set Friday
   */
  public void setFr(final boolean fri) {
    this.fr = fri;
  }

  /**
   * Gets the Saturday value.
   * 
   * @return true if Saturday is set.
   */
  public boolean isSa() {
    return sa;
  }

  /**
   * Sets the value of Saturday.
   * 
   * @param sat
   *          true to set Saturday
   */
  public void setSa(final boolean sat) {
    this.sa = sat;
  }

  /**
   * Gets the Sunday value.
   * 
   * @return true if Sunday is set.
   */
  public boolean isSu() {
    return su;
  }

  /**
   * Sets the value of Sunday.
   * 
   * @param sun to set Sunday
   *          the sun to set
   */
  public void setSu(final boolean sun) {
    this.su = sun;
  }

  /**
   * Gets the hour value.
   * 
   * @return the hour
   */
  public int getHour() {
    return hour;
  }

  /**
   * Sets the value of hour.
   * 
   * @param hourToSet
   *          the hour to set
   */
  public void setHour(final int hourToSet) {
    this.hour = hourToSet;
  }

  /**
   * Gets the duration value.
   * 
   * @return the duration
   */
  public int getDuration() {
    return duration;
  }

  /**
   * Sets the value of duration.
   * 
   * @param durat
   *          the duration to set
   */
  public void setDuration(final int durat) {
    this.duration = durat;
  }

}

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
package fr.cnes.sitools.form.dataset.model;

/**
 * Class for numeric BETWEEN in forms
 * 
 * @author AKKA Change history : <a
 *         href="https://sourceforge.net/tracker/?func=detail&aid=3346624&group_id=531341&atid=2158259">[3346624]
 *         conversion done by numeric_between</a><br/>
 *         2011/07/04 d.arpin use Double instead of Float to prevent from precision lost <br/>
 * 
 */
public final class NumericBetweenSelection extends SimpleParameter {

  /**
   * Initial float
   */
  private Number from;

  /**
   * Final float
   */
  private Number to;

  /**
   * Constructor
   */
  public NumericBetweenSelection() {
    super();
    // TODO Auto-generated constructor stub
  }

  /**
   * Constructor
   * 
   * @param from
   *          the starting float
   * @param to
   *          the end float
   */
  public NumericBetweenSelection(Number from, Number to) {
    super();
    this.from = from;
    this.to = to;
  }

  /**
   * Gets the from value
   * 
   * @return the from
   */
  public Number getFrom() {
    return from;
  }

  /**
   * Sets the value of from
   * 
   * @param from
   *          the from to set
   */
  public void setFrom(Number from) {
    this.from = from;
  }

  /**
   * Gets the to value
   * 
   * @return the to
   */
  public Number getTo() {
    return to;
  }

  /**
   * Sets the value of to
   * 
   * @param to
   *          the to to set
   */
  public void setTo(Number to) {
    this.to = to;
  }
}

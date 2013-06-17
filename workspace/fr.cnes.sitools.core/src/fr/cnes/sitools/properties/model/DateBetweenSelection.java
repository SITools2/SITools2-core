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
package fr.cnes.sitools.properties.model;

import java.util.Date;

/**
 * Class for numeric BETWEEN in forms
 * 
 * @author AKKA Change history : <a
 *         href="https://sourceforge.net/tracker/?func=detail&aid=3346624&group_id=531341&atid=2158259">[3346624]
 *         conversion done by numeric_between</a><br/>
 *         2011/07/04 d.arpin use Date instead of Float to prevent from precision lost <br/>
 * 
 */
public final class DateBetweenSelection {

  /**
   * Initial float
   */
  private Date from;

  /**
   * Final float
   */
  private Date to;

  /**
   * Constructor
   */
  public DateBetweenSelection() {
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
  public DateBetweenSelection(Date from, Date to) {
    super();
    this.from = from;
    this.to = to;
  }

  /**
   * Get the initial float
   * 
   * @return the from
   */
  public Date getFrom() {
    return from;
  }

  /**
   * Set the from
   * 
   * @param from
   *          the from to set
   */
  public void setFrom(Date from) {
    this.from = from;
  }

  /**
   * Get the to
   * 
   * @return the to
   */
  public Date getTo() {
    return to;
  }

  /**
   * Set the to
   * 
   * @param to
   *          the to to set
   */
  public void setTo(Date to) {
    this.to = to;
  }

}

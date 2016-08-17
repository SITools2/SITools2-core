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
package fr.cnes.sitools.dataset.database;

/**
 * Object to store range between to integer
 * 
 * 
 * @author m.gond
 */
public class Range implements Comparable<Range> {
  /** The start index of the range */
  private int start;
  /** The end index of the range */
  private int end;

  /**
   * Default constructor
   */
  public Range() {
    super();
  }

  /**
   * Constuctor with start and end
   * 
   * @param start
   *          the start param
   * @param end
   *          the end param
   */
  public Range(int start, int end) {
    this.start = start;
    this.end = end;
  }

  /**
   * Gets the start value
   * 
   * @return the start
   */
  public int getStart() {
    return start;
  }

  /**
   * Sets the value of start
   * 
   * @param start
   *          the start to set
   */
  public void setStart(int start) {
    this.start = start;
  }

  /**
   * Gets the end value
   * 
   * @return the end
   */
  public int getEnd() {
    return end;
  }

  /**
   * Sets the value of end
   * 
   * @param end
   *          the end to set
   */
  public void setEnd(int end) {
    this.end = end;
  }

  /**
   * Calculate the range size
   * 
   * @return the rangeSize
   */
  public int getSize() {
    return end - start + 1;
  }

  @Override
  public int compareTo(Range o) {
    Integer currentStart = new Integer(getStart());
    Integer oStart = new Integer(o.getStart());
    return currentStart.compareTo(oStart);
  }

  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "Range [start=" + start + ", end=" + end + "]";
  }
  
  
}

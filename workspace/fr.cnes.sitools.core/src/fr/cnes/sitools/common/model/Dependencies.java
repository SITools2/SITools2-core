/*******************************************************************************
 * Copyright 2011, 2012 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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

import java.util.List;

/**
 * Dependencies Model Object for Project Module
 * <p>
 * Used to store lists of URLs containing js or css URLs
 * 
 * 
 * @author m.gond
 */
public class Dependencies {
  /** List of js {@link Url} */
  private List<Url> js;
  /** List of css {@link Url} */
  private List<Url> css;

  /**
   * Gets the js value
   * 
   * @return the js
   */
  public List<Url> getJs() {
    return js;
  }

  /**
   * Sets the value of js
   * 
   * @param js
   *          the js to set
   */
  public void setJs(List<Url> js) {
    this.js = js;
  }

  /**
   * Gets the css value
   * 
   * @return the css
   */
  public List<Url> getCss() {
    return css;
  }

  /**
   * Sets the value of css
   * 
   * @param css
   *          the css to set
   */
  public void setCss(List<Url> css) {
    this.css = css;
  }

}

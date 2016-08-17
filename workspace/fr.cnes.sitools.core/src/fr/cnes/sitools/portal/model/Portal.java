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
package fr.cnes.sitools.portal.model;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import fr.cnes.sitools.common.model.Resource;
import fr.cnes.sitools.persistence.Persistent;

/**
 * Portal resource
 * 
 * @author jp.boignard (AKKA Technologies)
 * 
 */
@XStreamAlias("portal")
public final class Portal extends Resource implements Persistent {

  /** serialVersionUID */
  private static final long serialVersionUID = 5075327358420875478L;

  /** favicon configurable for portal */
  private String favicon;

  /**
   * Default constructor
   */
  public Portal() {
    super();
  }

  /**
   * Gets the favicon value
   * 
   * @return the favicon
   */
  public String getFavicon() {
    return favicon;
  }

  /**
   * Sets the value of favicon
   * 
   * @param favicon
   *          the favicon to set
   */
  public void setFavicon(String favicon) {
    this.favicon = favicon;
  }

}

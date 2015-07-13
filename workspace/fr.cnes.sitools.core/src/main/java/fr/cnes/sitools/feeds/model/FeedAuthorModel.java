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
package fr.cnes.sitools.feeds.model;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Bean representing an Author
 * 
 * @author AKKA Technologies
 */
@XStreamAlias("FeedAuthorModel")
public final class FeedAuthorModel {
  /**
   * name of the author
   */
  private String name;
  /**
   * email of the author
   */
  private String email;
  /**
   * Gets the name value
   * @return the name
   */
  public String getName() {
    return name;
  }
  /**
   * Sets the value of name
   * @param name the name to set
   */
  public void setName(String name) {
    this.name = name;
  }
  /**
   * Gets the email value
   * @return the email
   */
  public String getEmail() {
    return email;
  }
  /**
   * Sets the value of email
   * @param email the email to set
   */
  public void setEmail(String email) {
    this.email = email;
  }
  
  
}

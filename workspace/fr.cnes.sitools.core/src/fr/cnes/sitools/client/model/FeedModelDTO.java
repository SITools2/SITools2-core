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
package fr.cnes.sitools.client.model;
/**
 * DTO to store Feeds definition temporary
 * 
 * @author m.gond (AKKA Technologies)
 */
public final class FeedModelDTO {

  /**
   * The relative URL
   */
  private String url;
  /**
   * The id of the feed
   */
  private String id;
  /**
   * The type of the feed 
   */
  private String feedType;
  /**
   * The title of the feed
   */
  private String title;
  /**
   * Gets the URL value
   * @return Feed URL
   */
  public String getUrl() {
    return url;
  }
  /**
   * Sets the value of URL
   * @param url the URL to set
   */
  public void setUrl(String url) {
    this.url = url;
  }
  /**
   * Gets the id value
   * @return the id
   */
  public String getId() {
    return id;
  }
  /**
   * Sets the value of id
   * @param id the id to set
   */
  public void setId(String id) {
    this.id = id;
  }
  /**
   * Gets the feedType value
   * @return the feedType
   */
  public String getFeedType() {
    return feedType;
  }
  /**
   * Sets the value of feedType
   * @param feedType the feedType to set
   */
  public void setFeedType(String feedType) {
    this.feedType = feedType;
  }
  /**
   * Gets the title value
   * @return the title
   */
  public String getTitle() {
    return title;
  }
  /**
   * Sets the value of title
   * @param title the title to set
   */
  public void setTitle(String title) {
    this.title = title;
  }
  
  
  
}

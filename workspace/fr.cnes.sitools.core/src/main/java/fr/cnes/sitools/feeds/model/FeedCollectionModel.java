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

import java.util.ArrayList;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Bean for Feed collection definition
 * 
 * @author m.gond (AKKA Technologies)
 */
@XStreamAlias("FeedCollectionModel")
public final class FeedCollectionModel {
  /**
   * Feeds list
   */
  @XStreamAlias("feeds")
  private ArrayList<FeedModel> feeds;

  /**
   * Default constructor
   */
  public FeedCollectionModel() {
    feeds = new ArrayList<FeedModel>();
  }

  /**
   * Sets the value of feeds
   * 
   * @param feeds
   *          the feeds to set
   */
  public void setFeeds(ArrayList<FeedModel> feeds) {
    this.feeds = feeds;
  }

  /**
   * Gets the feeds value
   * 
   * @return the feeds
   */
  public ArrayList<FeedModel> getFeeds() {
    return feeds;
  }

}

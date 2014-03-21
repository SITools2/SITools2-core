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
package fr.cnes.sitools.client.model;

import java.util.ArrayList;
import java.util.List;
/**
 * DTO Class to store PortalIndex information temporarily
 * 
 * @author m.gond (AKKA Technologies)
 */
public final class PortalIndexDTO {
  /**
   * The list of projects
   */
  private String projectList;
  /**
   * The list of feeds
   */
  private List<FeedModelDTO> feeds;
  
  /**
   * sitools base url
   */
  private String appUrl;

  /**
   * Default constructor
   */
  public PortalIndexDTO() {
    super();
    feeds = new ArrayList<FeedModelDTO>();
  }

  /**
   * Gets the projectList value
   * @return the projectList
   */
  public String getProjectList() {
    return projectList;
  }

  /**
   * Sets the value of projectList
   * @param projectList the projectList to set
   */
  public void setProjectList(String projectList) {
    this.projectList = projectList;
  }

  /**
   * Gets the feeds value
   * @return the feeds
   */
  public List<FeedModelDTO> getFeeds() {
    return feeds;
  }

  /**
   * Sets the value of feeds
   * @param feeds the feeds to set
   */
  public void setFeeds(List<FeedModelDTO> feeds) {
    this.feeds = feeds;
  }

  /**
   * Gets the appUrl value
   * @return the appUrl
   */
  public String getAppUrl() {
    return appUrl;
  }

  /**
   * Sets the value of appUrl
   * @param appUrl the appUrl to set
   */
  public void setAppUrl(String appUrl) {
    this.appUrl = appUrl;
  }

}

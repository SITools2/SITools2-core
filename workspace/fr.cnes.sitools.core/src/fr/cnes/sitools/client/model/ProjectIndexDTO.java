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

import java.util.ArrayList;
import java.util.List;

import fr.cnes.sitools.project.model.Project;

/**
 * Used to create the index.html page Store temporary a list of AddDatasetOpensearchDTO. It also stores Feeds
 * description
 * 
 * @author m.gond (AKKA Technologies)
 * 
 */
public final class ProjectIndexDTO {

  /**
   * The list of AppDatasetOpensearchDTO associated to that project.
   */
  private List<AppDatasetOpensearchDTO> appDsOsDTOs;
  /**
   * ProjectName.
   */
  private String projectName;
  /**
   * The description of the project.
   */
  private String projectDescription;

  /**
   * The CSS of the project.
   */
  private String projectCss;

  /**
   * list of feeds
   */
  private List<FeedModelDTO> feeds;

  /**
   * sitools base url
   */
  private String appUrl;
  
  /**
   * The whole project model object
   */
  private Project project;

  /**
   * Constructor.
   */
  public ProjectIndexDTO() {
    super();
    this.appDsOsDTOs = new ArrayList<AppDatasetOpensearchDTO>();
    this.feeds = new ArrayList<FeedModelDTO>();
  }

  /**
   * Add a AppDatasetOpensearchDTO to the collection.
   * 
   * @param appDsOsDTO
   *          : The AppDatasetOpensearchDTO to store
   */
  public void addAppDatasetOpensearchDTO(AppDatasetOpensearchDTO appDsOsDTO) {
    this.appDsOsDTOs.add(appDsOsDTO);
  }

  /**
   * Add a AppDatasetOpensearchDTO to the collection.
   * 
   * @param url
   *          : the DataSet application URL
   * @param shortName
   *          : the shortName of the OpenSearch description file
   * @param datasetName
   *          : the name of the DataSet
   */
  public void addAppDatasetOpensearchDTO(String url, String shortName, String datasetName) {
    AppDatasetOpensearchDTO app = new AppDatasetOpensearchDTO();
    if (shortName != null) {
      app.setShortName(shortName);
    }
    if (url != null) {
      app.setUrl(url);
    }
    if (datasetName != null) {
      app.setDatasetName(datasetName);
    }
    this.addAppDatasetOpensearchDTO(app);
  }

  /**
   * Gets the list of AppDatasetOpensearchDTO.
   * 
   * @return the list of AppDatasetOpensearchDTO
   */
  public List<AppDatasetOpensearchDTO> getAppDsOsDTOs() {
    return appDsOsDTOs;
  }

  /**
   * Get the project name
   * 
   * @return the projectName
   */
  public String getProjectName() {
    return projectName;
  }

  /**
   * Set the project name
   * 
   * @param projectName
   *          the projectName to set
   */
  public void setProjectName(String projectName) {
    this.projectName = projectName;
  }

  /**
   * Get the project description
   * 
   * @return the project description
   */
  public String getProjectDescription() {
    return projectDescription;
  }

  /**
   * Set the project description
   * 
   * @param projectDescription
   *          the project description to set
   */
  public void setProjectDescription(String projectDescription) {
    this.projectDescription = projectDescription;
  }

  /**
   * Sets the value of feeds
   * 
   * @param feeds
   *          the feeds to set
   */
  public void setFeeds(List<FeedModelDTO> feeds) {
    this.feeds = feeds;
  }

  /**
   * Gets the feeds value
   * 
   * @return the feeds
   */
  public List<FeedModelDTO> getFeeds() {
    return feeds;
  }

  /**
   * Get the project CSS
   * 
   * @return the Project's CSS
   */
  public String getProjectCss() {
    return projectCss;
  }

  /**
   * Set the the Project's CSS
   * 
   * @param projectCss
   *          the CSS to set
   */
  public void setProjectCss(String projectCss) {
    this.projectCss = projectCss;
  }

  /**
   * Gets the appUrl value
   * 
   * @return the appUrl
   */
  public String getAppUrl() {
    return appUrl;
  }

  /**
   * Sets the value of appUrl
   * 
   * @param appUrl
   *          the appUrl to set
   */
  public void setAppUrl(String appUrl) {
    this.appUrl = appUrl;
  }

  /**
   * Sets the value of project
   * @param project the project to set
   */
  public void setProject(Project project) {
    this.project = project;
  }

  /**
   * Gets the project value
   * @return the project
   */
  public Project getProject() {
    return project;
  }

}

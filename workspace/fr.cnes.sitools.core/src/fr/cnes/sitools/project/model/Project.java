/*******************************************************************************
 * Copyright 2010-2016 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
 * <p/>
 * This file is part of SITools2.
 * <p/>
 * SITools2 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p/>
 * SITools2 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU General Public License
 * along with SITools2.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package fr.cnes.sitools.project.model;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import fr.cnes.sitools.common.model.IResource;
import fr.cnes.sitools.common.model.Resource;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Class definition of a project
 *
 *
 * @author jp.boignard (AKKA Technologies)
 *
 */
@XStreamAlias("project") public final class Project implements IResource, Serializable {

  /** serialVersionUID */
  private static final long serialVersionUID = -3868128415548604123L;

  /**
   * Object identifier
   */
  private String id;

  /**
   * Object name
   */
  private String name;

  /**
   * Object description
   */
  private String description;

  /**
   * image = resource
   */
  private Resource image;

  /**
   * Project CSS
   */
  private String css;

  /**
   * Datasets are resources exposed by another application. The project notion came after dataset one. To one project ,
   * multiple datasets can be attached.
   */
  @XStreamAlias("dataSets") private List<Resource> dataSets = null;

  /** Project status (enabled/disabled) */
  private String status;

  /**
   * Attachment for exposition to users TODO >> Provisoire en attendant les expositions
   */
  private String sitoolsAttachementForUsers = null;

  /**
   * If the project is visible even if it is not authorized
   */
  private boolean visible;

  /**
   * If the project is authorized ( DTO attribute, only for communication with the client )
   */
  private boolean authorized;

  /**
   * Modules are exposed by another application. To one project , multiple modules can be attached.
   */
  @XStreamAlias("modules") private List<ProjectModule> modules = null;

  /**
   * HTML header description
   */
  private String htmlHeader = null;

  private Footer footer = null;

  /**
   * HTML description
   */
  private String htmlDescription = null;

  /**
   * true when project is in mainteance
   */
  private boolean maintenance = false;

  /**
   * Text to display when project in maintenance
   */
  private String maintenanceText = null;

  /** A list of links associated to the project */
  private List<LinkModel> links = null;

  /** File name of the template for the project */
  private String ftlTemplateFile = null;

  /** File name of the template for the project */
  private String navigationMode = null;

  /** Order to display projects in portal */
  private Integer priority;

  /** Categorize project in a portlet with this name */
  private String categoryProject;

  /** The date of the last status update */
  private Date lastStatusUpdate;

  /**
   * Default constructor
   */
  public Project() {
    super();
  }

  /**
   * Gets the id value
   *
   * @return the id
   */
  public String getId() {
    return id;
  }

  /**
   * Sets the value of id
   *
   * @param id
   *          the id to set
   */
  public void setId(String id) {
    this.id = id;
  }

  /**
   * Gets the name value
   *
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * Sets the value of name
   *
   * @param name
   *          the name to set
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Gets the description value
   *
   * @return the description
   */
  public String getDescription() {
    return description;
  }

  /**
   * Sets the value of description
   *
   * @param description
   *          the description to set
   */
  public void setDescription(String description) {
    this.description = description;
  }

  /**
   * Gets the css value
   *
   * @return the css
   */
  public String getCss() {
    return css;
  }

  /**
   * Sets the value of css
   *
   * @param css
   *          the css to set
   */
  public void setCss(String css) {
    this.css = css;
  }

  /**
   * Gets the image value
   *
   * @return the image
   */
  public Resource getImage() {
    return image;
  }

  /**
   * Sets the value of image
   *
   * @param image
   *          the image to set
   */
  public void setImage(Resource image) {
    this.image = image;
  }

  /**
   * Gets the dataSets value
   *
   * @return the dataSets
   */
  public List<Resource> getDataSets() {
    return dataSets;
  }

  /**
   * Sets the value of dataSets
   *
   * @param dataSets
   *          the dataSets to set
   */
  public void setDataSets(List<Resource> dataSets) {
    this.dataSets = dataSets;
  }

  /**
   * Facilities with Arrays
   *
   * @return array
   */
  public Resource[] getDataSetsArray() {
    Resource[] result = null;
    if (dataSets != null) {
      result = new Resource[dataSets.size()];
      result = dataSets.toArray(result);
    }
    else {
      result = new Resource[0];
    }
    return result;
  }

  /**
   * Sets the array of dataset resources
   *
   * @param resources
   *          array of datasets
   */
  public void setDataSetsArray(Resource[] resources) {
    this.dataSets = new ArrayList<Resource>(Arrays.asList(resources));
  }

  @Override public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    return result;
  }

  @Override public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    Project other = (Project) obj;
    if (id == null) {
      if (other.id != null) {
        return false;
      }
    }
    else if (!id.equals(other.id)) {
      return false;
    }
    return true;
  }

  /**
   * Sets the value of status
   *
   * @param status
   *          the status to set
   */
  public void setStatus(String status) {
    this.status = status;
  }

  /**
   * Gets the status value
   *
   * @return the status
   */
  public String getStatus() {
    return status;
  }

  /**
   * Gets the sitoolsAttachementForUsers value
   *
   * @return the sitoolsAttachementForUsers
   */
  public String getSitoolsAttachementForUsers() {
    return sitoolsAttachementForUsers;
  }

  /**
   * Sets the value of sitoolsAttachementForUsers
   *
   * @param sitoolsAttachementForUsers
   *          the sitoolsAttachementForUsers to set
   */
  public void setSitoolsAttachementForUsers(String sitoolsAttachementForUsers) {
    this.sitoolsAttachementForUsers = sitoolsAttachementForUsers;
  }

  /**
   * Sets the value of visible
   *
   * @param visible
   *          the visible to set
   */
  public void setVisible(boolean visible) {
    this.visible = visible;
  }

  /**
   * Gets the visible value
   *
   * @return the visible
   */
  public boolean isVisible() {
    return visible;
  }

  /**
   * Sets the value of authorized
   *
   * @param authorized
   *          the authorized to set
   */
  public void setAuthorized(boolean authorized) {
    this.authorized = authorized;
  }

  /**
   * Gets the authorized value
   *
   * @return the authorized
   */
  public boolean isAuthorized() {
    return authorized;
  }

  /**
   * Gets the modules value
   *
   * @return the modules
   */
  public List<ProjectModule> getModules() {
    return modules;
  }

  /**
   * Sets the value of modules
   *
   * @param modules
   *          the modules to set
   */
  public void setModules(List<ProjectModule> modules) {
    this.modules = modules;
  }

  /**
   * Gets the htmlHeader value
   *
   * @return the htmlHeader
   */
  public String getHtmlHeader() {
    return htmlHeader;
  }

  /**
   * Sets the value of htmlHeader
   *
   * @param htmlHeader
   *          the htmlHeader to set
   */
  public void setHtmlHeader(String htmlHeader) {
    this.htmlHeader = htmlHeader;
  }

  /**
   * Gets the project Footer
   *
   * @return the footer
   */
  public Footer getFooter() {
    return footer;
  }

  /**
   * Sets the value of footer
   *
   * @param footer
   *          the footer to set
   */
  public void setFooter(Footer footer) {
    this.footer = footer;
  }

  /**
   * Gets the htmlDescription value
   *
   * @return the htmlDescription
   */
  public String getHtmlDescription() {
    return htmlDescription;
  }

  /**
   * Sets the value of htmlDescription
   *
   * @param htmlDescription
   *          the htmlDescription to set
   */
  public void setHtmlDescription(String htmlDescription) {
    this.htmlDescription = htmlDescription;
  }

  /**
   * Gets the maintenance value
   *
   * @return the maintenance
   */
  public boolean isMaintenance() {
    return maintenance;
  }

  /**
   * Sets the value of maintenance
   *
   * @param maintenance
   *          the maintenance to set
   */
  public void setMaintenance(boolean maintenance) {
    this.maintenance = maintenance;
  }

  /**
   * Gets the maintenanceText value
   *
   * @return the maintenanceText
   */
  public String getMaintenanceText() {
    return maintenanceText;
  }

  /**
   * Sets the value of maintenanceText
   *
   * @param maintenanceText
   *          the maintenanceText to set
   */
  public void setMaintenanceText(String maintenanceText) {
    this.maintenanceText = maintenanceText;
  }

  /**
   * Sets the value of links
   *
   * @param links
   *          the links to set
   */
  public void setLinks(List<LinkModel> links) {
    this.links = links;
  }

  /**
   * Gets the links value
   *
   * @return the links
   */
  public List<LinkModel> getLinks() {
    return links;
  }

  /**
   * Sets the value of ftlTemplateFile
   *
   * @param ftlTemplateFile
   *          the ftlTemplateFile to set
   */
  public void setFtlTemplateFile(String ftlTemplateFile) {
    this.ftlTemplateFile = ftlTemplateFile;
  }

  /**
   * Gets the ftlTemplateFile value
   *
   * @return the ftlTemplateFile
   */
  public String getFtlTemplateFile() {
    return ftlTemplateFile;
  }

  /**
   * Gets the navigationMode value
   *
   * @return the navigationMode
   */
  public String getNavigationMode() {
    return navigationMode;
  }

  /**
   * Sets the value of navigationMode
   *
   * @param navigationMode
   *          the navigationMode to set
   */
  public void setNavigationMode(String navigationMode) {
    this.navigationMode = navigationMode;
  }

  /**
   * Gets the priority value
   *
   * @return the priority
   */
  public Integer getPriority() {
    return priority;
  }

  /**
   * Sets the value of priority
   *
   * @param priority
   *          the priority to set
   */
  public void setPriority(Integer priority) {
    this.priority = priority;
  }

  /**
   * Gets the categoryProject value
   *
   * @return the categoryProject
   */
  public String getCategoryProject() {
    return categoryProject;
  }

  /**
   * Sets the value of categoryProject
   *
   * @param categoryProject
   *          the categoryProject to set
   */
  public void setCategoryProject(String categoryProject) {
    this.categoryProject = categoryProject;
  }

  /**
   * Gets the lastStatusUpdate value
   *
   * @return the lastStatusUpdate
   */
  public Date getLastStatusUpdate() {
    return lastStatusUpdate;
  }

  /**
   * Sets the value of lastStatusUpdate
   *
   * @param lastStatusUpdate
   *          the lastStatusUpdate to set
   */
  public void setLastStatusUpdate(Date lastStatusUpdate) {
    this.lastStatusUpdate = lastStatusUpdate;
  }

}

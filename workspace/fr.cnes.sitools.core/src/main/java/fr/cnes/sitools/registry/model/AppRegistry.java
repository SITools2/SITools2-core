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
package fr.cnes.sitools.registry.model;

import java.util.ArrayList;

import fr.cnes.sitools.common.model.IResource;
import fr.cnes.sitools.common.model.Resource;

/**
 * Object container for resources. Resources can be applications or others.
 * 
 * @author jp.boignard (AKKA Technologies)
 * 
 */
public final class AppRegistry implements IResource {

  /** identifier */
  private String id = null;

  /** name */
  private String name = null;

  /** description */
  private String description = null;

  /** last update */
  private String lastUpdate = null;

  /** resources */
  private ArrayList<Resource> resources = new ArrayList<Resource>();

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
   * Gets the lastUpdate value
   * 
   * @return the lastUpdate
   */
  public String getLastUpdate() {
    return lastUpdate;
  }

  /**
   * Sets the value of lastUpdate
   * 
   * @param lastUpdate
   *          the lastUpdate to set
   */
  public void setLastUpdate(String lastUpdate) {
    this.lastUpdate = lastUpdate;
  }

  /**
   * Gets the resources value
   * 
   * @return the resources
   */
  public ArrayList<Resource> getResources() {
    return resources;
  }

  /**
   * Sets the value of resources
   * 
   * @param resources
   *          the resources to set
   */
  public void setResources(ArrayList<Resource> resources) {
    this.resources = resources;
  }

}

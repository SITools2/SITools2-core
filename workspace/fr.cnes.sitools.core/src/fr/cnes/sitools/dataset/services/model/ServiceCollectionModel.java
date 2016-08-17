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
package fr.cnes.sitools.dataset.services.model;

import java.util.List;

import fr.cnes.sitools.common.model.IResource;

/**
 * Model class to represent a collection of {@link ServiceModel} on a dataset.
 * 
 * 
 * 
 * @author m.gond
 */
public class ServiceCollectionModel implements IResource {

  /** The id */
  private String id;
  /** The name */
  private String name;
  /** The description */
  private String description;
  /** The list of services */
  private List<ServiceModel> services;

  @Override
  public String getId() {
    return id;
  }

  @Override
  public void setId(String id) {
    this.id = id;
  }

  @Override
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
   * Gets the services value
   * 
   * @return the services
   */
  public List<ServiceModel> getServices() {
    return services;
  }

  /**
   * Sets the value of services
   * 
   * @param services
   *          the services to set
   */
  public void setServices(List<ServiceModel> services) {
    this.services = services;
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

  @Override
  public String getDescription() {
    return description;
  }

}

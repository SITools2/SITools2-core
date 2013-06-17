     /*******************************************************************************
 * Copyright 2010-2013 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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
package fr.cnes.sitools.collections.model;

import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import fr.cnes.sitools.common.model.IResource;
import fr.cnes.sitools.common.model.Resource;

/**
 * Form component class
 * 
 * @author AKKA
 * 
 */
@XStreamAlias("Collection")
public final class Collection implements IResource {

  /**
   * ID of the form component
   */
  private String id;

  /**
   * Name of the collection
   */
  private String name;

  /**
   * Description of the collection
   */
  private String description;

  /**
   * Datasets are resources exposed by another application. The project notion came after dataset one. To one project ,
   * multiple datasets can be attached.
   */
  @XStreamAlias("dataSets")
  private List<Resource> dataSets = null;

  /**
   * Constructor
   */
  public Collection() {
    super();
    // TODO Auto-generated constructor stub
  }

  /**
   * Get the ID
   * 
   * @return the id
   */
  public String getId() {
    return id;
  }

  /**
   * Set the ID
   * 
   * @param id
   *          the id to set
   */
  public void setId(final String id) {
    this.id = id;
  }

  @Override
  public String getName() {
    return this.name;
  }

  @Override
  public String getDescription() {
    return this.description;
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
   * Sets the value of description
   * 
   * @param description
   *          the description to set
   */
  public void setDescription(String description) {
    this.description = description;
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

}

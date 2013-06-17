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
package fr.cnes.sitools.dataset.converter.model;

import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import fr.cnes.sitools.common.model.IResource;

/**
 * Class to store The chained list of converter
 * 
 * @author m.gond (AKKA Technologies)
 */
@XStreamAlias("converterChainedModel")
public final class ConverterChainedModel implements IResource {
  /**
   * Id
   */
  private String id;
  /**
   * Name
   */
  private String name;
  /**
   * Description
   */
  private String description;

  /**
   * DataSet id
   */
  private String parent = null;
  /**
   * Ordered List of converter model
   */
  @XStreamAlias("converters")
  private List<ConverterModel> converters;

  /**
   * Default constructor
   */
  public ConverterChainedModel() {
    super();
    converters = new ArrayList<ConverterModel>();
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
   * Gets the converters value
   * 
   * @return the converters
   */
  public List<ConverterModel> getConverters() {
    return converters;
  }

  /**
   * Sets the value of converters
   * 
   * @param converters
   *          the converters to set
   */
  public void setConverters(List<ConverterModel> converters) {
    this.converters = converters;
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
   * Gets the name value
   * 
   * @return the name
   */
  public String getName() {
    return name;
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
   * Gets the description value
   * 
   * @return the description
   */
  public String getDescription() {
    return description;
  }

  /**
   * Sets the value of parent
   * 
   * @param parent
   *          the parent to set
   */
  public void setParent(String parent) {
    this.parent = parent;
  }

  /**
   * Gets the parent value
   * 
   * @return the parent
   */
  public String getParent() {
    return parent;
  }

}

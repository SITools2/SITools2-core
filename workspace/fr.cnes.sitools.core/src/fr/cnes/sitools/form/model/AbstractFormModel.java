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
package fr.cnes.sitools.form.model;

import java.util.List;

import fr.cnes.sitools.common.model.IResource;
import fr.cnes.sitools.form.dataset.model.Zone;

/**
 * Abstract model class for FormModel
 * 
 * 
 * @author m.gond
 */
public abstract class AbstractFormModel implements IResource {

  /**
   * Form Id
   */
  private String id;
  /**
   * Parent DataSet Id
   */
  private String parent;
  /**
   * Form Name
   */
  private String name;
  /**
   * Associated CSS
   */
  private String css;
  /**
   * Form Description
   */
  private String description;
  /**
   * Form Width
   */
  private int width;
  /**
   * Form height
   */
  private int height;
  /**
   * Parent url attachment DataSet
   */
  private String parentUrl;

  /**
   * The list of parameters of the Form
   */
  private List<AbstractParameter> parameters;

  /**
   * The list of zones in the Form
   */
  private List<Zone> zones;

  /**
   * Default constructor
   */
  public AbstractFormModel() {
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
   * Gets the parent value
   * 
   * @return the parent
   */
  public String getParent() {
    return parent;
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
   * Gets the width value
   * 
   * @return the width
   */
  public int getWidth() {
    return width;
  }

  /**
   * Sets the value of width
   * 
   * @param width
   *          the width to set
   */
  public void setWidth(int width) {
    this.width = width;
  }

  /**
   * Gets the height value
   * 
   * @return the height
   */
  public int getHeight() {
    return height;
  }

  /**
   * Sets the value of height
   * 
   * @param height
   *          the height to set
   */
  public void setHeight(int height) {
    this.height = height;
  }

  /**
   * Gets the parentUrl value
   * 
   * @return the parentUrl
   */
  public String getParentUrl() {
    return parentUrl;
  }

  /**
   * Sets the value of parentUrl
   * 
   * @param parentUrl
   *          the parentUrl to set
   */
  public void setParentUrl(String parentUrl) {
    this.parentUrl = parentUrl;
  }

  /**
   * Gets the parameters value
   * 
   * @return the parameters
   */
  public List<AbstractParameter> getParameters() {
    return parameters;
  }

  /**
   * Sets the value of parameters
   * 
   * @param parameters
   *          the parameters to set
   */
  public void setParameters(List<AbstractParameter> parameters) {
    this.parameters = parameters;
  }

  /**
   * Gets the zones value
   * 
   * @return the zones
   */
  public List<Zone> getZones() {
    return zones;
  }

  /**
   * Sets the value of zones
   * 
   * @param zones
   *          the zones to set
   */
  public void setZones(List<Zone> zones) {
    this.zones = zones;
  }

}
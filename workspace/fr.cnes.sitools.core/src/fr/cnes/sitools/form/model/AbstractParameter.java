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
package fr.cnes.sitools.form.model;

import java.util.List;

import fr.cnes.sitools.units.dimension.model.SitoolsUnit;
import fr.cnes.sitools.util.Property;

/**
 * Class representing an abstract parameter
 * 
 * @author BAILLAGOU
 * @version 1.0 01-September.-2010 08:58:57
 */
public abstract class AbstractParameter {

  /**
   * Identifier of the parameter
   */
  private String id;

  /**
   * Code of the parameter
   */
  private List<String> code;

  /**
   * Label of the parameter
   */
  private String label;

  /**
   * Width of the parameter
   */
  private int width;

  /**
   * Height of the parameter
   */
  private int height;

  /**
   * X position of the parameter
   */
  private int xpos;

  /**
   * Y position of the parameter
   */
  private int ypos;

  /**
   * CSS of the parameter
   */
  private String css;

  /**
   * jsAdmin object of the parameter
   */
  private String jsAdminObject;

  /**
   * jsUser object of the parameter
   */
  private String jsUserObject;

  /**
   * The id of the container panel
   */
  private String containerPanelId;

  /**
   * Default Values of the parameter
   */
  private List<String> defaultValues;

  /**
   * value Selection of the parameter
   */
  private String valueSelection;

  /**
   * autoComplete of the parameter
   */
  private boolean autoComplete;

  /**
   * Next one
   */
  private AbstractParameter nextParameter;

  /**
   * Next one
   */
  private String parentParam;

  /**
   * The Sitools DimensionId
   */
  private String dimensionId;

  /**
   * unit: The SitoolsUnit
   */
  private SitoolsUnit unit;

  /**
   * The Extra params
   */
  private List<Property> extraParams;

  /**
   * Constructor
   */
  public AbstractParameter() {
    super();
  }

  /**
   * Get the code
   * 
   * @return the code
   */
  public final List<String> getCode() {
    return code;
  }

  /**
   * Set the code
   * 
   * @param id
   *          the code
   */
  public final void setCode(final List<String> id) {
    this.code = id;
  }

  /**
   * Get the label
   * 
   * @return the label
   */
  public final String getLabel() {
    return label;
  }

  /**
   * Set the label
   * 
   * @param label
   *          the label
   */
  public final void setLabel(final String label) {
    this.label = label;
  }

  /**
   * Get the next parameter
   * 
   * @return the next parameter
   */
  public final AbstractParameter getNextParameter() {
    return nextParameter;
  }

  /**
   * Set the next parameter
   * 
   * @param nextParameter
   *          the next parameter
   */
  public final void setNextParameter(final AbstractParameter nextParameter) {
    this.nextParameter = nextParameter;
  }

  /**
   * Get the width
   * 
   * @return the width
   */
  public final int getWidth() {
    return width;
  }

  /**
   * Set the width
   * 
   * @param width
   *          the width to set
   */
  public final void setWidth(int width) {
    this.width = width;
  }

  /**
   * Get the X position
   * 
   * @return the X position
   */
  public final int getXpos() {
    return xpos;
  }

  /**
   * Set the X position
   * 
   * @param xpos
   *          the X position to set
   */
  public final void setXpos(final int xpos) {
    this.xpos = xpos;
  }

  /**
   * Get the Y position
   * 
   * @return the Y position
   */
  public final int getYpos() {
    return ypos;
  }

  /**
   * Set the Y position
   * 
   * @param ypos
   *          the Y position to set
   */
  public final void setYpos(final int ypos) {
    this.ypos = ypos;
  }

  /**
   * Get the ID
   * 
   * @return the id
   */
  public final String getId() {
    return id;
  }

  /**
   * Set the ID
   * 
   * @param id
   *          the id to set
   */
  public final void setId(final String id) {
    this.id = id;
  }

  /**
   * Get the height
   * 
   * @return the height
   */
  public final int getHeight() {
    return height;
  }

  /**
   * Set the height
   * 
   * @param height
   *          the height to set
   */
  public final void setHeight(final int height) {
    this.height = height;
  }

  /**
   * Get the CSS
   * 
   * @return the CSS
   */
  public final String getCss() {
    return css;
  }

  /**
   * Set the CSS
   * 
   * @param css
   *          the CSS to set
   */
  public final void setCss(final String css) {
    this.css = css;
  }

  /**
   * Get the JavaS object
   * 
   * @return the jsAdminObject
   */
  public final String getJsAdminObject() {
    return jsAdminObject;
  }

  /**
   * Get the defaultValues
   * 
   * @return the defaultValue
   */
  public final List<String> getDefaultValues() {
    return defaultValues;
  }

  /**
   * Set the defaultValues
   * 
   * @param defaultValues
   *          the values to set
   */
  public final void setDefaultValues(List<String> defaultValues) {
    this.defaultValues = defaultValues;
  }

  /**
   * get the valueSelection
   * 
   * @return the valueSelection
   */
  public final String getValueSelection() {
    return valueSelection;
  }

  /**
   * Set the valueSelection
   * 
   * @param valueSelection
   *          the valueSelection to set
   */
  public final void setValueSelection(String valueSelection) {
    this.valueSelection = valueSelection;
  }

  /**
   * get the autoComplete
   * 
   * @return the autoComplete
   */
  public final boolean isAutoComplete() {
    return autoComplete;
  }

  /**
   * set the autoComplete
   * 
   * @param autoComplete
   *          the autoComplete to set
   */
  public final void setAutoComplete(boolean autoComplete) {
    this.autoComplete = autoComplete;
  }

  /**
   * Get the parentParam
   * 
   * @return the parent parameter Id
   */
  public final String getParentParam() {
    return parentParam;
  }

  /**
   * Set the parentParam
   * 
   * @param parentParam
   *          the parentParam Id
   */
  public final void setParentParam(final String parentParam) {
    this.parentParam = parentParam;
  }

  /**
   * Gets the jsUserObject value
   * 
   * @return the jsUserObject
   */
  public String getJsUserObject() {
    return jsUserObject;
  }

  /**
   * Sets the value of jsUserObject
   * 
   * @param jsUserObject
   *          the jsUserObject to set
   */
  public void setJsUserObject(String jsUserObject) {
    this.jsUserObject = jsUserObject;
  }

  /**
   * Sets the value of jsAdminObject
   * 
   * @param jsAdminObject
   *          the jsAdminObject to set
   */
  public void setJsAdminObject(String jsAdminObject) {
    this.jsAdminObject = jsAdminObject;
  }

  /**
   * Gets the dimensionId value
   * 
   * @return the dimensionId
   */
  public String getDimensionId() {
    return dimensionId;
  }

  /**
   * Sets the value of dimensionId
   * 
   * @param dimensionId
   *          the dimensionId to set
   */
  public void setDimensionId(String dimensionId) {
    this.dimensionId = dimensionId;
  }

  /**
   * Gets the extraParams value
   * 
   * @return the extraParams
   */
  public List<Property> getExtraParams() {
    return extraParams;
  }

  /**
   * Sets the value of extraParams
   * 
   * @param extraParams
   *          the extraParams to set
   */
  public void setExtraParams(List<Property> extraParams) {
    this.extraParams = extraParams;
  }

  /**
   * Gets the unit value
   * 
   * @return the unit
   */
  public SitoolsUnit getUnit() {
    return unit;
  }

  /**
   * Sets the value of unit
   * 
   * @param unit
   *          the unit to set
   */
  public void setUnit(SitoolsUnit unit) {
    this.unit = unit;
  }

  /**
   * Gets the containerPanelId value
   * 
   * @return the containerPanelId
   */
  public String getContainerPanelId() {
    return containerPanelId;
  }

  /**
   * Sets the value of containerPanelId
   * 
   * @param containerPanelId
   *          the containerPanelId to set
   */
  public void setContainerPanelId(String containerPanelId) {
    this.containerPanelId = containerPanelId;
  }

}

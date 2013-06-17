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
package fr.cnes.sitools.form.dataset.dto;

import java.util.List;

import fr.cnes.sitools.units.dimension.model.SitoolsUnit;
import fr.cnes.sitools.util.Property;

/**
 * DTO parameters
 * 
 * @author AKKA (OpenWiz)
 */
public final class ParameterDTO {

  /**
   * Comment for <code>type</code>
   */
  private String id;

  /**
   * Comment for <code>type</code>
   */
  private String type;

  /**
   * Comment for <code>values</code>
   */
  private List<ValueDTO> values;

  /**
   * Comment for <code>code</code>
   */
  private List<String> code;

  /**
   * Comment for <code>label</code>
   */
  private String label;

  /**
   * Comment for <code>nextParameter</code>
   */
  private String nextParameter;


  /**
   * Comment for <code>geoWKTSelection</code>
   */
  private String geoWKTSelection;

  /**
   * Comment for <code>geoWKTMaxExtent</code>
   */
  private String geoWKTMaxExtent;

  /**
   * Comment for <code>periodMinExtent</code>
   */
  private String periodMinExtent;

  /**
   * Comment for <code>periodMaxExtent</code>
   */
  private String periodMaxExtent;

  /**
   * Comment for <code>from</code>
   */
  private String from;

  /**
   * Comment for <code>to</code>
   */
  private String to;

  /**
   * Comment for <code>excludedDates</code>
   */
  private List<String> excludedDates;

  /**
   * default values of a parameter.
   */
  private List<String> defaultValues;

  /**
   * Comment for <code>date</code>
   */
  private String date;

  /**
   * Width of the DTO
   */
  private int width;

  /**
   * Height of the DTO
   */
  private int height;

  /**
   * X position of the DTO
   */
  private int xpos;

  /**
   * Y position of the DTO
   */
  private int ypos;

  /**
   * CSS associated to the DTO
   */
  private String css;

  /**
   * JavaScript object associated to the DTO
   */
  private String jsAdminObject;

  /**
   * JavaScript object associated to the DTO
   */
  private String jsUserObject;


  /**
   * the way the Values are requested : "S" : specified by the administrator "D" : from the database .
   */
  private String valueSelection;

  /**
   * autoComplete : true to load a store with a distinct query
   */
  private boolean autoComplete;

  /**
   * parentParam : The Parent parameter 
   */
  private String parentParam;

  /**
   * dimensionId: The SitoolsDimensionID
   */
  private String dimensionId;

  /**
   * unit: The SitoolsUnit
   */
  private SitoolsUnit unit;

  /**
   * Extra Params
   */
  private List<Property> extraParams;
  
  /**
   * Constructor
   */
  public ParameterDTO() {
    super();
  }

  /**
   * Returns the DTO type
   * 
   * @return the type
   */
  public String getType() {
    return type;
  }

  /**
   * Set the DTO type
   * 
   * @param type
   *          the type to set
   */
  public void setType(final String type) {
    this.type = type;
  }

  /**
   * Get the code of the DTO
   * 
   * @return the code
   */
  public List<String> getCode() {
    return code;
  }

  /**
   * Set the code of the DTO
   * 
   * @param code
   *          the code to set
   */
  public void setCode(final List<String> code) {
    this.code = code;
  }

  /**
   * Get the list of values of the DTO
   * 
   * @return the values
   */
  public List<ValueDTO> getValues() {
    return values;
  }

  /**
   * Set the list of values of the DTO
   * 
   * @param values
   *          the values to set
   */
  public void setValues(final List<ValueDTO> values) {
    this.values = values;
  }

  /**
   * Get the label of the DTO
   * 
   * @return the label
   */
  public String getLabel() {
    return label;
  }

  /**
   * Set the label of the DTO
   * 
   * @param label
   *          the label to set
   */
  public void setLabel(final String label) {
    this.label = label;
  }

  /**
   * Get the next parameter of the DTO
   * 
   * @return the nextParameter
   */
  public String getNextParameter() {
    return nextParameter;
  }

  /**
   * Set the next parameter of the DTO
   * 
   * @param nextParameter
   *          the nextParameter to set
   */
  public void setNextParameter(final String nextParameter) {
    this.nextParameter = nextParameter;
  }

  /**
   * Get the geoWKTSelection
   * 
   * @return the geoWKTSelection
   */
  public String getGeoWKTSelection() {
    return geoWKTSelection;
  }

  /**
   * Set the geoWKTSelection
   * 
   * @param geoWKTSelection
   *          the geoWKTSelection to set
   */
  public void setGeoWKTSelection(final String geoWKTSelection) {
    this.geoWKTSelection = geoWKTSelection;
  }

  /**
   * Get the geoWKTMaxExtent
   * 
   * @return the geoWKTMaxExtent
   */
  public String getGeoWKTMaxExtent() {
    return geoWKTMaxExtent;
  }

  /**
   * Set the geoWKTMaxExtent
   * 
   * @param geoWKTMaxExtent
   *          the geoWKTMaxExtent to set
   */
  public void setGeoWKTMaxExtent(String geoWKTMaxExtent) {
    this.geoWKTMaxExtent = geoWKTMaxExtent;
  }

  /**
   * Get the periodMinExtent
   * 
   * @return the periodMinExtent
   */
  public String getPeriodMinExtent() {
    return periodMinExtent;
  }

  /**
   * Set the periodMinExtent
   * 
   * @param periodMinExtent
   *          the periodMinExtent to set
   */
  public void setPeriodMinExtent(final String periodMinExtent) {
    this.periodMinExtent = periodMinExtent;
  }

  /**
   * Get the periodMaxExtent
   * 
   * @return the periodMaxExtent
   */
  public String getPeriodMaxExtent() {
    return periodMaxExtent;
  }

  /**
   * Set the periodMaxExtent
   * 
   * @param periodMaxExtent
   *          the periodMaxExtent to set
   */
  public void setPeriodMaxExtent(final String periodMaxExtent) {
    this.periodMaxExtent = periodMaxExtent;
  }

  /**
   * Get where it comes from
   * 
   * @return the from
   */
  public String getFrom() {
    return from;
  }

  /**
   * Set where it comes from
   * 
   * @param from
   *          the from to set
   */
  public void setFrom(final String from) {
    this.from = from;
  }

  /**
   * Get where it goes to
   * 
   * @return the to
   */
  public String getTo() {
    return to;
  }

  /**
   * Set where it goes to
   * 
   * @param to
   *          the to to set
   */
  public void setTo(final String to) {
    this.to = to;
  }

  /**
   * Get the dates excluded
   * 
   * @return the excludedDates
   */
  public List<String> getExcludedDates() {
    return excludedDates;
  }

  /**
   * Set the dates excluded
   * 
   * @param excludedDates
   *          the excludedDates to set
   */
  public void setExcludedDates(final List<String> excludedDates) {
    this.excludedDates = excludedDates;
  }

  /**
   * Get the date
   * 
   * @return the date
   */
  public String getDate() {
    return date;
  }

  /**
   * Set the date
   * 
   * @param date
   *          the date to set
   */
  public void setDate(final String date) {
    this.date = date;
  }

  /**
   * Get the width of the DTO
   * 
   * @return the width
   */
  public int getWidth() {
    return width;
  }

  /**
   * Set the width of the DTO
   * 
   * @param width
   *          the width to set
   */
  public void setWidth(final int width) {
    this.width = width;
  }

  /**
   * Get the X position
   * 
   * @return the X position
   */
  public int getXpos() {
    return xpos;
  }

  /**
   * Set the X position
   * 
   * @param xpos
   *          the X position to set
   */
  public void setXpos(final int xpos) {
    this.xpos = xpos;
  }

  /**
   * Get the Y position
   * 
   * @return the Y position
   */
  public int getYpos() {
    return ypos;
  }

  /**
   * Set the Y position
   * 
   * @param ypos
   *          the Y position to set
   */
  public void setYpos(final int ypos) {
    this.ypos = ypos;
  }

  /**
   * Get the DTO ID
   * 
   * @return the id
   */
  public String getId() {
    return id;
  }

  /**
   * Set the DTO ID
   * 
   * @param id
   *          the id to set
   */
  public void setId(final String id) {
    this.id = id;
  }

  /**
   * Get the height
   * 
   * @return the height
   */
  public int getHeight() {
    return height;
  }

  /**
   * Set the height
   * 
   * @param height
   *          the height to set
   */
  public void setHeight(final int height) {
    this.height = height;
  }

  /**
   * Get the CSS associated
   * 
   * @return the CSS
   */
  public String getCss() {
    return css;
  }

  /**
   * Set the CSS associated
   * 
   * @param css
   *          the CSS to set
   */
  public void setCss(final String css) {
    this.css = css;
  }

  /**
   * Gets the defaultValues property
   * 
   * @return List<String> defaultValues
   */
  public List<String> getDefaultValues() {
    return defaultValues;
  }

  /**
   * Sets the defaultValues property
   * 
   * @param defaultValues
   *          the defaultValue to set
   */
  public void setDefaultValues(List<String> defaultValues) {
    this.defaultValues = defaultValues;
  }

  /**
   * Get the valueSelection
   * 
   * @return the valueSelection
   */
  public String getValueSelection() {
    return valueSelection;
  }

  /**
   * Set the valueSelection
   * 
   * @param valueSelection
   *          the valueSelection to set
   */
  public void setValueSelection(String valueSelection) {
    this.valueSelection = valueSelection;
  }

  /**
   * get the autoComplete
   * 
   * @return the autoComplete
   */
  public boolean isAutoComplete() {
    return autoComplete;
  }

  /**
   * set the autoComplete
   * 
   * @param autoComplete the autoComplete to set
   */
  public void setAutoComplete(boolean autoComplete) {
    this.autoComplete = autoComplete;
  }

  /**
   * get the parentParam
   * 
   * @return the parentParam
   */
  public String getParentParam() {
    return parentParam;
  }

  /**
   * set the parentParam
   * 
   * @param parentParam the parentParam to set
   */
  public void setParentParam(String parentParam) {
    this.parentParam = parentParam;
  }

  /**
   * Gets the jsUserObject value
   * @return the jsUserObject
   */
  public String getJsUserObject() {
    return jsUserObject;
  }

  /**
   * Sets the value of jsUserObject
   * @param jsUserObject the jsUserObject to set
   */
  public void setJsUserObject(String jsUserObject) {
    this.jsUserObject = jsUserObject;
  }

  /**
   * Gets the jsAdminObject value
   * @return the jsAdminObject
   */
  public String getJsAdminObject() {
    return jsAdminObject;
  }

  /**
   * Sets the value of jsAdminObject
   * @param jsAdminObject the jsAdminObject to set
   */
  public void setJsAdminObject(String jsAdminObject) {
    this.jsAdminObject = jsAdminObject;
  }

  /**
   * Gets the dimensionId value
   * @return the dimensionId
   */
  public String getDimensionId() {
    return dimensionId;
  }

  /**
   * Sets the value of dimensionId
   * @param dimensionId the dimensionId to set
   */
  public void setDimensionId(String dimensionId) {
    this.dimensionId = dimensionId;
  }

  /**
   * Gets the extraParams value
   * @return the extraParams
   */
  public List<Property> getExtraParams() {
    return extraParams;
  }

  /**
   * Sets the value of extraParams
   * @param extraParams the extraParams to set
   */
  public void setExtraParams(List<Property> extraParams) {
    this.extraParams = extraParams;
  }

  /**
   * Gets the unit value
   * @return the unit
   */
  public SitoolsUnit getUnit() {
    return unit;
  }

  /**
   * Sets the value of unit
   * @param unit the unit to set
   */
  public void setUnit(SitoolsUnit unit) {
    this.unit = unit;
  }
  
  


}

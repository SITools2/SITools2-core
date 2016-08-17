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
package fr.cnes.sitools.units.dimension.model;

import javax.measure.unit.Unit;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.restlet.engine.Engine;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Overlay for Unit framework
 * 
 * @author m.marseille (AKKA technologies)
 */
@XStreamAlias("unit")
public final class SitoolsUnit {

  /** Unit label */
  private String label;

  /** Unit identifier */
  private String unitName;

  /**
   * Constructor
   */
  public SitoolsUnit() {
  }

  /**
   * Constructor with unit name
   * 
   * @param sequence
   *          the unit name
   */
  public SitoolsUnit(String sequence) {
    this.label = sequence;
    this.unitName = sequence;
  }

  /**
   * Check if the sequence can lead to a unit creation
   * 
   * @param sequence
   *          the unit name
   * @return true if the sequence is understood
   */
  public Unit<?> getInstanceFrom(String sequence) {
    try {
      return Unit.valueOf(sequence);
    }
    catch (Exception e) {
      Engine.getLogger("SitoolsUnit").info(sequence + " : not a valid unit");
    }
    return null;
  }

  /**
   * Return the encapsulated unit
   * 
   * @return the unit
   */
  @JsonIgnore
  public Unit<?> getUnit() {
    return this.getInstanceFrom(unitName);
  }

  /**
   * Sets the value of label
   * 
   * @param label
   *          the label to set
   */
  public void setLabel(String label) {
    this.label = label;
  }

  /**
   * Gets the label value
   * 
   * @return the label
   */
  public String getLabel() {
    return label;
  }

  /**
   * Sets the value of unitName
   * 
   * @param unitName
   *          the unitName to set
   */
  public void setUnitName(String unitName) {
    this.unitName = unitName;
  }

  /**
   * Gets the unitName value
   * 
   * @return the unitName
   */
  public String getUnitName() {
    return unitName;
  }

}

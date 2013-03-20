/*******************************************************************************
 * Copyright 2011, 2012 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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
/**
 * 
 */
package fr.cnes.sitools.units.dimension.model;

import javax.measure.unit.Unit;
import javax.measure.unit.UnitConverter;

/**
 * Base class for converters to register for unit conversions
 * Extends basic converter that is not sufficient for converters
 * between unit with different dimensions
 * @author m.marseille (AKKA technologies)
 */
public abstract class SitoolsUnitConverter extends UnitConverter {

  /**
   * Serializable class
   */
  private static final long serialVersionUID = 1L;

  /**
   * Starting Unit
   */
  private Unit<?> startUnit;
  
  /**
   * target Unit
   */
  private Unit<?> targetUnit;
  
  /**
   * Sets the value of startUnit
   * @param startUnit the startUnit to set
   */
  public final void setStartUnit(Unit<?> startUnit) {
    this.startUnit = startUnit;
  }

  /**
   * Sets the value of targetUnit
   * @param targetUnit the targetUnit to set
   */
  public final void setTargetUnit(Unit<?> targetUnit) {
    this.targetUnit = targetUnit;
  }

  /**
   * Gets the startUnit value
   * @return the startUnit
   */
  public Unit<?> getStartUnit() {
    return startUnit;
  }

  /**
   * Gets the targetUnit value
   * @return the targetUnit
   */
  public Unit<?> getTargetUnit() {
    return targetUnit;
  }
  
  /**
   * Get the converter between the two units
   * @return a Unit converter to go from base to target
   */
  public abstract UnitConverter getBaseToTargetConverter();
  
  /**
   * Get the converter in opposite way
   * @return a Unit converter to go from target to base
   */
  public final UnitConverter getTargetToBaseConverter() {
    return getBaseToTargetConverter().inverse();
  }
  

}

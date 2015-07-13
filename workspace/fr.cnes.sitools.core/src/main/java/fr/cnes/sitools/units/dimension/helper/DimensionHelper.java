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
package fr.cnes.sitools.units.dimension.helper;

import java.util.ArrayList;
import java.util.List;

import javax.measure.unit.SystemOfUnits;
import javax.measure.unit.Unit;

import org.restlet.engine.Helper;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

import fr.cnes.sitools.units.dimension.model.SitoolsUnitConverter;

/**
 * Dimension helper to register all known converters
 * 
 * @author m.marseille (AKKA technologies)
 */
@XStreamAlias("dimensionHelper")
public class DimensionHelper extends Helper {

  /** Helper name necessary for business */
  private String helperName;

  /** The list of converters names */
  private List<String> converters = new ArrayList<String>();

  /** The list of system names */
  private List<String> systems = new ArrayList<String>();

  /** The list of converters registered by the helper */
  @XStreamOmitField
  private List<SitoolsUnitConverter> convs = new ArrayList<SitoolsUnitConverter>();

  /** The list of systems registered by the helper */
  @XStreamOmitField
  private List<SystemOfUnits> sys = new ArrayList<SystemOfUnits>();

  /**
   * Constructor holding class name
   */
  public DimensionHelper() {
    this.helperName = this.getClass().getCanonicalName();
  }

  /**
   * Get the list of registered converters
   * 
   * @return the list
   */
  public final List<SitoolsUnitConverter> getRegisteredConverters() {
    return convs;
  }

  /**
   * register a converter to the dimension helper
   * 
   * @param conv
   *          the converter to register
   */
  public final void registerUnitConverter(SitoolsUnitConverter conv) {
    convs.add(conv);
    converters.add(conv.getClass().getCanonicalName());
  }

  /**
   * register a measure system to the dimension helper
   * 
   * @param s
   *          the system to register
   */
  public final void registerSystem(SystemOfUnits s) {
    sys.add(s);
    systems.add(s.getClass().getSimpleName());
  }

  /**
   * Get the converter for the given Unit to another unit.
   * 
   * @param u
   *          the base unit
   * @param v
   *          the target unit
   * @return the converter registered
   */
  public final SitoolsUnitConverter getConverter(Unit<?> u, Unit<?> v) {
    if (u.isCompatible(v)) {
      return (SitoolsUnitConverter) u.getConverterToAny(v);
    }
    else {
      for (SitoolsUnitConverter conv : convs) {
        if (conv.getStartUnit().equals(u) && conv.getTargetUnit().equals(v)) {
          return (SitoolsUnitConverter) conv;
        }
        if (conv.getStartUnit().equals(v) && conv.getTargetUnit().equals(u)) {
          return (SitoolsUnitConverter) conv.inverse();
        }
      }
    }
    return null;
  }

  /**
   * Gets the converters value
   * 
   * @return the converters
   */
  public final List<String> getConverters() {
    return converters;
  }

  /**
   * Sets the value of helperName
   * 
   * @param helperName
   *          the helperName to set
   */
  public final void setHelperName(String helperName) {
    this.helperName = helperName;
  }

  /**
   * Gets the helperName value
   * 
   * @return the helperName
   */
  public final String getHelperName() {
    return this.helperName;
  }

  /**
   * Gets the systems value
   * 
   * @return the systems
   */
  public final List<String> getSystems() {
    return systems;
  }

  /**
   * Gets the sys value
   * 
   * @return the sys
   */
  public List<SystemOfUnits> getRegisteredSystems() {
    return sys;
  }

  /**
   * Sets the value of converters
   * 
   * @param converters
   *          the converters to set
   */
  public void setConverters(List<String> converters) {
    this.converters = converters;
  }

  /**
   * Sets the value of systems
   * 
   * @param systems
   *          the systems to set
   */
  public void setSystems(List<String> systems) {
    this.systems = systems;
  }

}

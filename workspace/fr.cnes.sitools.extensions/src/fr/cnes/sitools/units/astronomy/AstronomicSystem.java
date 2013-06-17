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
package fr.cnes.sitools.units.astronomy;

import static javax.measure.unit.MetricSystem.HERTZ;
import static javax.measure.unit.MetricSystem.KILOGRAM;
import static javax.measure.unit.MetricSystem.METRE;
import static javax.measure.unit.MetricSystem.RADIAN;
import static javax.measure.unit.MetricSystem.SECOND;
import static javax.measure.unit.MetricSystem.WATT;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.measure.quantity.Angle;
import javax.measure.quantity.Length;
import javax.measure.quantity.Mass;
import javax.measure.quantity.Power;
import javax.measure.quantity.Time;
import javax.measure.unit.ProductUnit;
import javax.measure.unit.SystemOfUnits;
import javax.measure.unit.Unit;
import javax.measure.unit.converter.MultiplyConverter;

/**
 * Units for astronomical measurements
 * 
 * @author m.marseille (AKKA technologies)
 * 
 */
public final class AstronomicSystem extends SystemOfUnits {

  /**
   * Set of units in the system
   */
  public static final Set<Unit<?>> UNITS = new HashSet<Unit<?>>();

  /**
   * Add Angstrom unit, equivalent to a tenth of a nanometer
   */
  public static final Unit<Length> ANGSTROM = addUnit(new AstronomicUnit<Length>("" + (char) 143, METRE,
      new MultiplyConverter(1e-10)));

  /**
   * Add Parsec unit, equivalent 3.085677581e16 meters
   */
  public static final Unit<Length> PARSEC = addUnit(new AstronomicUnit<Length>("pc", METRE, new MultiplyConverter(
      3.085677581e16)));

  /**
   * Add astronomical unit, equivalent 1.495978707e11 meters
   */
  public static final Unit<Length> ASTRONOMICAL_UNIT = addUnit(new AstronomicUnit<Length>("AU", METRE,
      new MultiplyConverter(1.495978707e11)));

  /**
   * Add light-year unit, equivalent 9.460730472580800e15 meters
   */
  public static final Unit<Length> LIGHT_YEAR = addUnit(new AstronomicUnit<Length>("ly", METRE, new MultiplyConverter(
      9.460730472580800e15)));

  /**
   * Add solar radius unit, equivalent 6.955e8 meters
   */
  public static final Unit<Length> SOLAR_RADIUS = addUnit(new AstronomicUnit<Length>("Rsol", METRE,
      new MultiplyConverter(6.955e8)));

  /**
   * Calendar year unit, i.e. equivalent to 365 days exactly
   */
  public static final Unit<Time> CALENDAR_YEAR = addUnit(new AstronomicUnit<Time>("Y", SECOND, new MultiplyConverter(
      31536000)));

  /**
   * Sideral year unit, i.e. equivalent to a complete rotation of the Earth
   * around the sun 3.155814954e7 seconds
   */
  public static final Unit<Time> SIDERAL_YEAR = addUnit(new AstronomicUnit<Time>("y", SECOND, new MultiplyConverter(
      3.155814954e7)));

  /**
   * Calendar (solar) day
   */
  public static final Unit<Time> SOLAR_DAY = addUnit(new AstronomicUnit<Time>("D", SECOND, new MultiplyConverter(86400)));

  /**
   * Earth rotation period : SIDERAL_DAY
   */
  public static final Unit<Time> SIDERAL_DAY = addUnit(new AstronomicUnit<Time>("d", SECOND, new MultiplyConverter(
      86164.1)));

  /**
   * Degree
   */
  public static final Unit<Angle> DEGREE_ANGLE = addUnit(new AstronomicUnit<Angle>("°", RADIAN, new MultiplyConverter(
      2 * Math.PI / 360)));

  /**
   * Hour angle : 24h = 360 degrees = 2pi rad
   */
  public static final Unit<Angle> HOUR_ANGLE = addUnit(new AstronomicUnit<Angle>("hr", RADIAN, new MultiplyConverter(
      2 * Math.PI / 24)));

  /**
   * Minute angle : 1h = 60 minutes
   */
  public static final Unit<Angle> MINUTE_ANGLE = addUnit(new AstronomicUnit<Angle>("min", RADIAN, HOUR_ANGLE
      .getConverterToMetric().concatenate(new MultiplyConverter(1.0 / 60))));

  /**
   * Second angle : 1m = 60 seconds
   */
  public static final Unit<Angle> SECOND_ANGLE = addUnit(new AstronomicUnit<Angle>("sec", RADIAN, MINUTE_ANGLE
      .getConverterToMetric().concatenate(new MultiplyConverter(1.0 / 60))));
  
  /**
   * arcdegree : 360 deg = 2pi radians
   */
  public static final Unit<Angle> ARC_DEGREE = addUnit(new AstronomicUnit<Angle>("deg", RADIAN, new MultiplyConverter(
      2 * Math.PI / 360)));
  
  /**
   * arcminute : 1 degree = 60 arcminutes
   */
  public static final Unit<Angle> ARC_MINUTE = addUnit(new AstronomicUnit<Angle>("'", RADIAN, ARC_DEGREE
      .getConverterToMetric().concatenate(new MultiplyConverter(1.0 / 60))));
  
  /**
   * arcsecond : 1 degree = 60 arcminutes = 3600 arcseconds
   */
  public static final Unit<Angle> ARC_SECOND = addUnit(new AstronomicUnit<Angle>("\"", RADIAN, ARC_MINUTE
      .getConverterToMetric().concatenate(new MultiplyConverter(1.0 / 60))));

  /**
   * Solar mass
   */
  public static final Unit<Mass> SOLAR_MASS = addUnit(new AstronomicUnit<Mass>("Msol", KILOGRAM, new MultiplyConverter(
      1.988435e30)));

  /**
   * Solar luminosity
   */
  public static final Unit<Power> SOLAR_LUMINOSITY = addUnit(new AstronomicUnit<Power>("Lsol", WATT,
      new MultiplyConverter(3.846e26)));

  /**
   * Jansky
   */
  public static final Unit<Power> JANSKY = addUnit(new ProductUnit<Power>(WATT.divide(METRE.pow(2).multiply(HERTZ)))
      .multiply(1.0e-26));

  /**
   * Instance for singleton
   */
  private static AstronomicSystem instance = null;

  /**
   * Singleton constructor
   */
  private AstronomicSystem() {

  }

  /**
   * Get singleton
   * @return the instance
   */
  public static synchronized AstronomicSystem getInstance() {
    if (instance == null) {
      instance = new AstronomicSystem();
    }
    return instance;
  }

  @Override
  public Set<Unit<?>> getUnits() {
    return Collections.unmodifiableSet(UNITS);
  }

  /**
   * Add a unit to the system and return it
   * 
   * @param <U>
   *          the unit type to instantiate
   * @param unit
   *          the unit to add
   * @return the unit added
   */
  private static <U extends Unit<?>> U addUnit(U unit) {
    UNITS.add(unit);
    return unit;
  }

}

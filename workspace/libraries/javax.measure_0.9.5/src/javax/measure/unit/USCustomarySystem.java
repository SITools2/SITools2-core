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
package javax.measure.unit;

import static javax.measure.unit.MetricSystem.*;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.measure.quantity.Angle;
import javax.measure.quantity.Area;
import javax.measure.quantity.DataAmount;
import javax.measure.quantity.Energy;
import javax.measure.quantity.Length;
import javax.measure.quantity.Mass;
import javax.measure.quantity.Temperature;
import javax.measure.quantity.Time;
import javax.measure.quantity.Velocity;
import javax.measure.quantity.Volume;

/**
 * <p> This class contains units from the United States customary system.</p>
 *
 * @author <a href="mailto:jean-marie@dautelle.com">Jean-Marie Dautelle</a>
 * @author  <a href="mailto:jsr275@catmedia.us">Werner Keil</a>
 * @version 1.2 ($Revision: 192 $), $Date: 2010-02-24 17:46:38 +0100 (Mi, 24 Feb 2010) $
 * @see <a href="http://en.wikipedia.org/wiki/United_States_customary_units">Wikipedia: United State Customary Units</a>
 */
public final class USCustomarySystem extends SystemOfUnits {

    /**
     * Holds collection of units.
     */
    private static final Set<Unit<?>> UNITS = new HashSet<Unit<?>>();

    /**
     * Default constructor (prevents this class from being instantiated).
     */
    private USCustomarySystem() {
    }

    /**
     * Returns the unique instance of this class.
     *
     * @return the NonSI instance.
     */
    public static USCustomarySystem getInstance() {
        return INSTANCE;
    }
    
    private static final USCustomarySystem INSTANCE = new USCustomarySystem();


    // //////////
    // Length //
    // //////////
    /**
     * US name for {@link MetricSystem#METRE}.
     */
    public static final Unit<Length> METER = METRE;
    
    /**
     * A unit of length equal to <code>0.3048 m</code> (standard name
     * <code>ft</code>).
     */
    public static final Unit<Length> FOOT = addUnit(METER.multiply(
            3048.0).divide(10000.0));

    /**
     * A unit of length equal to <code>1200/3937 m</code> (standard name
     * <code>foot_survey_us</code>). See also: <a
     * href="http://www.sizes.com/units/foot.htm">foot</a>
     */
    public static final Unit<Length> FOOT_SURVEY = addUnit(METER.multiply(1200).divide(3937));

    /**
     * A unit of length equal to <code>0.9144 m</code> (standard name
     * <code>yd</code>).
     */
    public static final Unit<Length> YARD = addUnit(FOOT.multiply(3));

    /**
     * A unit of length equal to <code>0.0254 m</code> (standard name
     * <code>in</code>).
     */
    public static final Unit<Length> INCH = addUnit(FOOT.divide(12));

    /**
     * A unit of length equal to <code>1609.344 m</code> (standard name
     * <code>mi</code>).
     */
    public static final Unit<Length> MILE = addUnit(METER.multiply(1609344).divide(
            1000));

    /**
     * A unit of length equal to <code>1852.0 m</code> (standard name
     * <code>nmi</code>).
     */
    public static final Unit<Length> NAUTICAL_MILE = addUnit(METER.multiply(1852));


    // ////////
    // Mass //
    // ////////

    /**
     * A unit of mass equal to <code>453.59237 grams</code> (avoirdupois pound,
     * standard name <code>lb</code>).
     */
    public static final Unit<Mass> POUND = addUnit(KILOGRAM.multiply(
            45359237).divide(100000000));

    /**
     * A unit of mass equal to <code>1 / 16 {@link #POUND}</code> (standard name <code>oz</code>).
     */
    public static final Unit<Mass> OUNCE = addUnit(POUND.divide(16));

    /**
     * A unit of mass equal to <code>2000 {@link #POUND}</code> (short ton, standard name
     * <code>ton</code>).
     */
    public static final Unit<Mass> TON = addUnit(POUND.multiply(2000));


    // ///////////////
    // Temperature //
    // ///////////////

  /**
     * A unit of temperature equal to <code>5/9 °K</code> (standard name
     * <code>°R</code>).
     */
    public static final Unit<Temperature> RANKINE = addUnit(KELVIN.multiply(5).divide(9));

    /**
     * A unit of temperature equal to degree Rankine minus
     * <code>459.67 °R</code> (standard name <code>°F</code>).
     *
     * @see #RANKINE
     */
    public static final Unit<Temperature> FAHRENHEIT = addUnit(RANKINE.add(459.67));


    // /////////
    // Angle //
    // /////////

    /**
     * A unit of angle equal to a full circle or <code>2<i>&pi;</i>
     * {@link MetricSystem#RADIAN}</code> (standard name <code>rev</code>).
     */
    public static final Unit<Angle> REVOLUTION = addUnit(RADIAN.multiply(2).multiply(Math.PI).asType(Angle.class));

    /**
     * A unit of angle equal to <code>1/360 {@link #REVOLUTION}</code> (standard name <code>deg</code>).
     */
    public static final Unit<Angle> DEGREE_ANGLE = addUnit(REVOLUTION.divide(360));

    /**
     * A unit of angle equal to <code>1/60 {@link #DEGREE_ANGLE}</code> (standard name <code>'</code>).
     */
    public static final Unit<Angle> MINUTE_ANGLE = addUnit(DEGREE_ANGLE.divide(60));

    /**
     * A unit of angle equal to <code>1/60 {@link #MINUTE_ANGLE}</code> (standard name <code>"</code>).
     */
    public static final Unit<Angle> SECOND_ANGLE = addUnit(MINUTE_ANGLE.divide(60));


    // ////////////
    // Duration //
    // ////////////
    /**
     * A unit of time equal to <code>60 s</code> (standard name
     * <code>min</code>).
     */
    public static final Unit<Time> MINUTE = addUnit(SECOND.multiply(60));

    /**
     * A unit of duration equal to <code>60 {@link #MINUTE}</code> (standard name <code>h</code>).
     */
    public static final Unit<Time> HOUR = addUnit(MINUTE.multiply(60));
        

    // ////////////
    // Velocity //
    // ////////////
    /**
     * A unit of velocity expressing the number of {@link #FOOT feet} per
     * {@link MetricSystem#SECOND second}.
     */
    public static final Unit<Velocity> FEET_PER_SECOND = addUnit(
            FOOT.divide(SECOND)).asType(Velocity.class);

    /**
     * A unit of velocity expressing the number of international {@link #MILE
     * miles} per {@link #HOUR hour} (abbreviation <code>mph</code>).
     */
    public static final Unit<Velocity> MILES_PER_HOUR = addUnit(
            MILE.divide(HOUR)).asType(Velocity.class);

    /**
     * A unit of velocity expressing the number of {@link #NAUTICAL_MILE
     * nautical miles} per {@link #HOUR hour} (abbreviation <code>kn</code>).
     */
    public static final Unit<Velocity> KNOT = addUnit(
            NAUTICAL_MILE.divide(HOUR)).asType(Velocity.class);

    // ////////
    // Area //
    // ////////

    /**
     * A unit of area equal to <code>100 m²</code> (standard name <code>a</code>
     * ).
     */
    public static final Unit<Area> ARE = addUnit(SQUARE_METRE.multiply(100));

    // ///////////////
    // Data Amount //
    // ///////////////
    /**
     * A unit of data amount equal to <code>8 {@link MetricSystem#BIT}</code> (BinarY TErm, standard name
     * <code>byte</code>).
     */
    public static final Unit<DataAmount> BYTE = addUnit(BIT.multiply(8));

    /**
     * Equivalent {@link #BYTE}
     */
    public static final Unit<DataAmount> OCTET = BYTE;

    
    // //////////
    // Energy //
    // //////////

    /**
     * A unit of energy equal to one electron-volt (standard name
     * <code>eV</code>, also recognized <code>keV, MeV, GeV</code>).
     */
    public static final Unit<Energy> ELECTRON_VOLT = addUnit(JOULE.multiply(1.602176462e-19));

    // //////////
    // Volume //
    // //////////
    /**
     * A unit of volume equal to one cubic decimeter (default label
     * <code>L</code>, also recognized <code>µL, mL, cL, dL</code>).
     */
    public static final Unit<Volume> LITER = addUnit(CUBIC_METRE.divide(1000));

    /**
     * A unit of volume equal to one cubic inch (<code>in³</code>).
     */
    public static final Unit<Volume> CUBIC_INCH = addUnit(INCH.pow(3).asType(
            Volume.class));
    
    /**
     * A unit of volume equal to one US dry gallon. (standard name
     * <code>gallon_dry_us</code>).
     */
    public static final Unit<Volume> GALLON_DRY = addUnit(CUBIC_INCH.multiply(
            2688025).divide(10000));
    
    /**
     * A unit of volume equal to one US gallon, Liquid Unit. The U.S. liquid
     * gallon is based on the Queen Anne or Wine gallon occupying 231 cubic
     * inches (standard name <code>gal</code>).
     */
    public static final Unit<Volume> GALLON_LIQUID = addUnit(CUBIC_INCH.multiply(231));

    /**
     * A unit of volume equal to <code>1 / 128 {@link #GALLON_LIQUID}</code> (standard name
     * <code>oz_fl</code>).
     */
    public static final Unit<Volume> OUNCE_LIQUID = addUnit(GALLON_LIQUID.divide(128));

    /**
     * A unit of volume <code>~ 1 drop or 0.95 grain of water </code> (standard name
     * <code>min</code>).
     */
    public static final Unit<Volume> MINIM = addUnit(MICRO(LITER).multiply(61.61152d));

    /**
     * A unit of volume equal to <code>60 {@link #MINIM}</code> (standard name
     * <code>fl dr</code>).
     */
    public static final Unit<Volume> FLUID_DRAM = addUnit(MINIM.multiply(60));

    /**
     * A unit of volume equal to <code>80 {@link #MINIM}</code> (standard name
     * <code>tsp</code>).
     */
    public static final Unit<Volume> TEASPOON = addUnit(MINIM.multiply(80));

    /**
     * A unit of volume equal to <code>3 {@link #TEASPOON}</code> (standard name
     * <code>Tbsp</code>).
     */
    public static final Unit<Volume> TABLESPOON = addUnit(TEASPOON.multiply(3));

    
    /**
     * A unit of volume equal to <code>238.4810 {@link #LITER}</code> (standard name
     * <code>bbl</code>).
     */
    public static final Unit<Volume> OIL_BARREL = addUnit(LITER.multiply(238.4810d));

    // ///////////////////
    // Collection View //
    // ///////////////////
    /**
     * Returns a read only view over the units defined in this class.
     *
     * @return the collection of NonSI units.
     */
    public Set<Unit<?>> getUnits() {
        return Collections.unmodifiableSet(UNITS);
    }

    /**
     * Adds a new unit to the collection.
     *
     * @param unit the unit being added.
     * @return <code>unit</code>.
     */
    private static <U extends Unit<?>> U addUnit(U unit) {
        UNITS.add(unit);
        return unit;
    }

}

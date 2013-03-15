/**
 *   Copyright (c) 2005-2010, Jean-Marie Dautelle, Werner Keil
 *   All rights reserved.
 *
 *   See LICENSE.txt for the Specification License
 */
package javax.measure.unit;

import java.math.BigInteger;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.measure.quantity.Acceleration;
import javax.measure.quantity.AmountOfSubstance;
import javax.measure.quantity.Angle;
import javax.measure.quantity.Area;
import javax.measure.quantity.CatalyticActivity;
import javax.measure.quantity.DataAmount;
import javax.measure.quantity.ElectricCapacitance;
import javax.measure.quantity.ElectricCharge;
import javax.measure.quantity.ElectricConductance;
import javax.measure.quantity.ElectricCurrent;
import javax.measure.quantity.ElectricInductance;
import javax.measure.quantity.ElectricPotential;
import javax.measure.quantity.ElectricResistance;
import javax.measure.quantity.Energy;
import javax.measure.quantity.Force;
import javax.measure.quantity.Frequency;
import javax.measure.quantity.Illuminance;
import javax.measure.quantity.Length;
import javax.measure.quantity.LuminousFlux;
import javax.measure.quantity.LuminousIntensity;
import javax.measure.quantity.MagneticFlux;
import javax.measure.quantity.MagneticFluxDensity;
import javax.measure.quantity.MagnetomotiveForce;
import javax.measure.quantity.Mass;
import javax.measure.quantity.Power;
import javax.measure.quantity.Pressure;
import javax.measure.quantity.Quantity;
import javax.measure.quantity.RadiationDoseAbsorbed;
import javax.measure.quantity.RadiationDoseEffective;
import javax.measure.quantity.RadioactiveActivity;
import javax.measure.quantity.SolidAngle;
import javax.measure.quantity.Temperature;
import javax.measure.quantity.Time;
import javax.measure.quantity.Velocity;
import javax.measure.quantity.Volume;

import javax.measure.unit.converter.MultiplyConverter;
import javax.measure.unit.converter.RationalConverter;

/**
 * <p> This class contains SI (Système International d'Unités) base units,
 *     and derived units.</p>
 *
 * <p> It also defines an inner class for the 20 SI prefixes used to form decimal multiples and
 *     submultiples of SI units. For example:[code]
 *     import static javax.measure.unit.SI.*; // Static import.
 *     import static javax.measure.unit.Prefix.Metric.*; // Static import.
 *     ...
 *     Unit<Pressure> HECTOPASCAL = HECTO(PASCAL);
 *     Unit<Length> KILOMETRE = KILO(METRE);
 *     [/code]</p>
 *
 * @author  <a href="mailto:jean-marie@dautelle.com">Jean-Marie Dautelle</a>
 * @author  <a href="mailto:jsr275@catmedia.us">Werner Keil</a>
 * @version 1.16 ($Revision: 169 $), $Date: 2010-02-21 18:48:40 +0100 (So, 21 Feb 2010) $
 * @see <a href="http://en.wikipedia.org/wiki/International_System_of_Units">Wikipedia: International System of Units</a>
 */
public final class MetricSystem extends SystemOfUnits {

    /**
     * Holds collection of SI units.
     */
    private static final HashSet<Unit<?>> UNITS = new HashSet<Unit<?>>();

    /**
     * The singleton instance of {@code SI}.
     */
    private static MetricSystem INSTANCE = null;
    
    /**
     * Private constructor for singleton
     */
    private MetricSystem() {
    	
    }

    /**
     * Returns the singleton instance of this class.
     *
     * @return the metric system instance.
     */
    public static final MetricSystem getInstance() {
    	if (INSTANCE == null) {
    		INSTANCE = new MetricSystem();
    	}
        return INSTANCE;
    }
    
    ////////////////
    // BASE UNITS //
    ////////////////
    /**
     * The base unit for electric current quantities (<code>A</code>).
     * The Ampere is that constant current which, if maintained in two straight
     * parallel conductors of infinite length, of negligible circular
     * cross-section, and placed 1 meter apart in vacuum, would produce between
     * these conductors a force equal to 2 * 10-7 newton per meter of length.
     * It is named after the French physicist Andre Ampere (1775-1836).
     */
    public static final BaseUnit<ElectricCurrent> AMPERE = addUnit(new BaseUnit<ElectricCurrent>(
            "A"));

    /**
     * The base unit for luminous intensity quantities (<code>cd</code>).
     * The candela is the luminous intensity, in a given direction,
     * of a source that emits monochromatic radiation of frequency
     * 540 * 1012 hertz and that has a radiant intensity in that
     * direction of 1/683 watt per steradian
     * @see <a href="http://en.wikipedia.org/wiki/Candela">
     *      Wikipedia: Candela</a>
     */
    public static final BaseUnit<LuminousIntensity> CANDELA = addUnit(new BaseUnit<LuminousIntensity>(
            "cd"));

    /**
     * The base unit for thermodynamic temperature quantities (<code>K</code>).
     * The kelvin is the 1/273.16th of the thermodynamic temperature of the
     * triple point of water. It is named after the Scottish mathematician and
     * physicist William Thomson 1st Lord Kelvin (1824-1907)
     */
    public static final BaseUnit<Temperature> KELVIN = addUnit(new BaseUnit<Temperature>(
            "K"));

    /**
     * The base unit for mass quantities (<code>kg</code>).
     * It is the only SI unit with a prefix as part of its name and symbol.
     * The kilogram is equal to the mass of an international prototype in the
     * form of a platinum-iridium cylinder kept at Sevres in France.
     * @see   #GRAM
     */
    public static final BaseUnit<Mass> KILOGRAM = addUnit(new BaseUnit<Mass>("kg"));

    /**
     * The base unit for length quantities (<code>m</code>).
     * One metre was redefined in 1983 as the distance traveled by light in
     * a vacuum in 1/299,792,458 of a second.
     */
    public static final BaseUnit<Length> METRE = addUnit(new BaseUnit<Length>("m"));

    /**
     * The base unit for amount of substance quantities (<code>mol</code>).
     * The mole is the amount of substance of a system which contains as many
     * elementary entities as there are atoms in 0.012 kilogram of carbon 12.
     */
    public static final BaseUnit<AmountOfSubstance> MOLE = addUnit(new BaseUnit<AmountOfSubstance>(
            "mol"));

    /**
     * The base unit for duration quantities (<code>s</code>).
     * It is defined as the duration of 9,192,631,770 cycles of radiation
     * corresponding to the transition between two hyperfine levels of
     * the ground state of cesium (1967 Standard).
     */
    public static final BaseUnit<Time> SECOND = addUnit(new BaseUnit<Time>(
            "s"));
    ////////////////////////////////
    // SI DERIVED ALTERNATE UNITS //
    ////////////////////////////////

    /**
     * The unit for magnetomotive force (<code>At</code>).
     */
    public static final Unit<MagnetomotiveForce> AMPERE_TURN = addUnit(new AlternateUnit<MagnetomotiveForce>("At", MetricSystem.AMPERE));

    /**
     * The derived unit for mass quantities (<code>g</code>).
     * The base unit for mass quantity is {@link #KILOGRAM}.
     */
    public static final Unit<Mass> GRAM = KILOGRAM.divide(1000);

    /**
     * The unit for plane angle quantities (<code>rad</code>).
     * One radian is the angle between two radii of a circle such that the
     * length of the arc between them is equal to the radius.
     */
    public static final Unit<Angle> RADIAN = addUnit(new AlternateUnit<Angle>(
            "rad", Unit.ONE));

    /**
     * The unit for solid angle quantities (<code>sr</code>).
     * One steradian is the solid angle subtended at the center of a sphere by
     * an area on the surface of the sphere that is equal to the radius squared.
     * The total solid angle of a sphere is 4*Pi steradians.
     */
    public static final Unit<SolidAngle> STERADIAN = addUnit(new AlternateUnit<SolidAngle>(
            "sr", Unit.ONE));

    /**
     * The unit for binary information (<code>bit</code>).
     */
    public static final Unit<DataAmount> BIT = addUnit(new AlternateUnit<DataAmount>(
            "bit", Unit.ONE));

    /**
     * The derived unit for frequency (<code>Hz</code>).
     * A unit of frequency equal to one cycle per second.
     * After Heinrich Rudolf Hertz (1857-1894), German physicist who was the
     * first to produce radio waves artificially.
     */
    public static final Unit<Frequency> HERTZ = addUnit(new AlternateUnit<Frequency>(
            "Hz", Unit.ONE.divide(SECOND)));

    /**
     * The derived unit for force (<code>N</code>).
     * One newton is the force required to give a mass of 1 kilogram an Force
     * of 1 metre per second per second. It is named after the English
     * mathematician and physicist Sir Isaac Newton (1642-1727).
     */
    public static final Unit<Force> NEWTON = addUnit(new AlternateUnit<Force>(
            "N", METRE.multiply(KILOGRAM).divide(SECOND.pow(2))));

    /**
     * The derived unit for pressure, stress (<code>Pa</code>).
     * One pascal is equal to one newton per square meter. It is named after
     * the French philosopher and mathematician Blaise Pascal (1623-1662).
     */
    public static final Unit<Pressure> PASCAL = addUnit(new AlternateUnit<Pressure>(
            "Pa", NEWTON.divide(METRE.pow(2))));

    /**
     * The derived unit for energy, work, quantity of heat (<code>J</code>).
     * One joule is the amount of work done when an applied force of 1 newton
     * moves through a distance of 1 metre in the direction of the force.
     * It is named after the English physicist James Prescott Joule (1818-1889).
     */
    public static final Unit<Energy> JOULE = addUnit(new AlternateUnit<Energy>(
            "J", NEWTON.multiply(METRE)));

    /**
     * The derived unit for power, radiant, flux (<code>W</code>).
     * One watt is equal to one joule per second. It is named after the British
     * scientist James Watt (1736-1819).
     */
    public static final Unit<Power> WATT = addUnit(new AlternateUnit<Power>(
            "W", JOULE.divide(SECOND)));

    /**
     * The derived unit for electric charge, quantity of electricity
     * (<code>C</code>).
     * One Coulomb is equal to the quantity of charge transferred in one second
     * by a steady current of one ampere. It is named after the French physicist
     * Charles Augustin de Coulomb (1736-1806).
     */
    public static final Unit<ElectricCharge> COULOMB = addUnit(new AlternateUnit<ElectricCharge>(
            "C", SECOND.multiply(AMPERE)));

    /**
     * The derived unit for electric potential difference, electromotive force
     * (<code>V</code>).
     * One Volt is equal to the difference of electric potential between two
     * points on a conducting wire carrying a constant current of one ampere
     * when the power dissipated between the points is one watt. It is named
     * after the Italian physicist Count Alessandro Volta (1745-1827).
     */
    public static final Unit<ElectricPotential> VOLT = addUnit(new AlternateUnit<ElectricPotential>(
            "V", WATT.divide(AMPERE)));

    /**
     * The derived unit for capacitance (<code>F</code>).
     * One Farad is equal to the capacitance of a capacitor having an equal
     * and opposite charge of 1 coulomb on each plate and a potential difference
     * of 1 volt between the plates. It is named after the British physicist
     * and chemist Michael Faraday (1791-1867).
     */
    public static final Unit<ElectricCapacitance> FARAD = addUnit(new AlternateUnit<ElectricCapacitance>(
            "F", COULOMB.divide(VOLT)));

    /**
     * The derived unit for electric resistance (<code>Ohm</code>).
     * One Ohm is equal to the resistance of a conductor in which a current of
     * one ampere is produced by a potential of one volt across its terminals.
     * It is named after the German physicist Georg Simon Ohm (1789-1854).
     */
    public static final Unit<ElectricResistance> OHM = addUnit(new AlternateUnit<ElectricResistance>(
            "Ω", VOLT.divide(AMPERE)));

    /**
     * The derived unit for electric conductance (<code>S</code>).
     * One Siemens is equal to one ampere per volt. It is named after
     * the German engineer Ernst Werner von Siemens (1816-1892).
     */
    public static final Unit<ElectricConductance> SIEMENS = addUnit(new AlternateUnit<ElectricConductance>(
            "S", AMPERE.divide(VOLT)));

    /**
     * The derived unit for magnetic flux (<code>Wb</code>).
     * One Weber is equal to the magnetic flux that in linking a circuit of one
     * turn produces in it an electromotive force of one volt as it is uniformly
     * reduced to zero within one second. It is named after the German physicist
     * Wilhelm Eduard Weber (1804-1891).
     */
    public static final Unit<MagneticFlux> WEBER = addUnit(new AlternateUnit<MagneticFlux>(
            "Wb", VOLT.multiply(SECOND)));

    /**
     * The derived unit for magnetic flux density (<code>T</code>).
     * One Tesla is equal equal to one weber per square metre. It is named
     * after the Serbian-born American electrical engineer and physicist
     * Nikola Tesla (1856-1943).
     */
    public static final Unit<MagneticFluxDensity> TESLA = addUnit(new AlternateUnit<MagneticFluxDensity>(
            "T", WEBER.divide(METRE.pow(2))));

    /**
     * The derived unit for inductance (<code>H</code>).
     * One Henry is equal to the inductance for which an induced electromotive
     * force of one volt is produced when the current is varied at the rate of
     * one ampere per second. It is named after the American physicist
     * Joseph Henry (1791-1878).
     */
    public static final Unit<ElectricInductance> HENRY = addUnit(new AlternateUnit<ElectricInductance>(
            "H", WEBER.divide(AMPERE)));

    /**
     * The derived unit for Celsius temperature (<code>Cel</code>).
     * This is a unit of temperature such as the freezing point of water
     * (at one atmosphere of pressure) is 0 Cel, while the boiling point is
     * 100 Cel.
     */
    public static final Unit<Temperature> CELSIUS = addUnit(KELVIN.add(273.15));

    /**
     * The derived unit for luminous flux (<code>lm</code>).
     * One Lumen is equal to the amount of light given out through a solid angle
     * by a source of one candela intensity radiating equally in all directions.
     */
    public static final Unit<LuminousFlux> LUMEN = addUnit(new AlternateUnit<LuminousFlux>(
            "lm", CANDELA.multiply(STERADIAN)));

    /**
     * The derived unit for illuminance (<code>lx</code>).
     * One Lux is equal to one lumen per square metre.
     */
    public static final Unit<Illuminance> LUX = addUnit(new AlternateUnit<Illuminance>(
            "lx", LUMEN.divide(METRE.pow(2))));

    /**
     * The derived unit for activity of a radionuclide (<code>Bq</code>).
     * One becquerel is the radiation caused by one disintegration per second.
     * It is named after the French physicist, Antoine-Henri Becquerel
     * (1852-1908).
     */
    public static final Unit<RadioactiveActivity> BECQUEREL = addUnit(new AlternateUnit<RadioactiveActivity>(
            "Bq", Unit.ONE.divide(SECOND)));

    /**
     * The derived unit for absorbed dose, specific energy (imparted), kerma
     * (<code>Gy</code>).
     * One gray is equal to the dose of one joule of energy absorbed per one
     * kilogram of matter. It is named after the British physician
     * L. H. Gray (1905-1965).
     */
    public static final Unit<RadiationDoseAbsorbed> GRAY = addUnit(new AlternateUnit<RadiationDoseAbsorbed>(
            "Gy", JOULE.divide(KILOGRAM)));

    /**
     * The derived unit for dose equivalent (<code>Sv</code>).
     * One Sievert is equal  is equal to the actual dose, in grays, multiplied
     * by a "quality factor" which is larger for more dangerous forms of
     * radiation. It is named after the Swedish physicist Rolf Sievert
     * (1898-1966).
     */
    public static final Unit<RadiationDoseEffective> SIEVERT = addUnit(new AlternateUnit<RadiationDoseEffective>(
            "Sv", JOULE.divide(KILOGRAM)));

    /**
     * The derived unit for catalytic activity (<code>kat</code>).
     */
    public static final Unit<CatalyticActivity> KATAL = addUnit(new AlternateUnit<CatalyticActivity>(
            "kat", MOLE.divide(SECOND)));
    //////////////////////////////
    // SI DERIVED PRODUCT UNITS //
    //////////////////////////////

    /**
     * The metric unit for velocity quantities (<code>m/s</code>).
     *
     */
    public static final Unit<Velocity> METRES_PER_SECOND = addUnit(new ProductUnit<Velocity>(
            METRE.divide(SECOND)));

    /**
     * The metric unit for acceleration quantities (<code>m/s2</code>).
     */
    public static final Unit<Acceleration> METRES_PER_SQUARE_SECOND = addUnit(new ProductUnit<Acceleration>(
            METRES_PER_SECOND.divide(SECOND)));

    /**
     * The metric unit for area quantities (<code>m2</code>).
     */
    public static final Unit<Area> SQUARE_METRE = addUnit(new ProductUnit<Area>(
            METRE.multiply(METRE)));

    /**
     * The metric unit for volume quantities (<code>m3</code>).
     */
    public static final Unit<Volume> CUBIC_METRE = addUnit(new ProductUnit<Volume>(
            SQUARE_METRE.multiply(METRE)));

   

    /////////////////////
    // Collection View //
    /////////////////////
    /**
     * Returns a read only view over theunits defined in this class.
     *
     * @return the collection of SI units.
     */
    public Set<Unit<?>> getUnits() {
        return Collections.unmodifiableSet(UNITS);
    }

    /**
     * Adds a new unit to the collection.
     *
     * @param  unit the unit being added.
     * @return <code>unit</code>.
     */
    private static <U extends Unit<?>>  U addUnit(U unit) {
        UNITS.add(unit);
        return unit;
    }

    /**
     * Returns the specified unit multiplied by the factor
     * <code>10<sup>24</sup></code>
     *
     * @param <Q> The type of the quantity measured by the unit.
     * @param unit any unit.
     * @return <code>unit.times(1e24)</code>.
     */
    public static <Q extends Quantity<Q>> Unit<Q> YOTTA(Unit<Q> unit) {
        return unit.transform(E24);
    }

    /**
     * Returns the specified unit multiplied by the factor
     * <code>10<sup>21</sup></code>
     *
     * @param <Q> The type of the quantity measured by the unit.
     * @param unit any unit.
     * @return <code>unit.times(1e21)</code>.
     */
    public static <Q extends Quantity<Q>> Unit<Q> ZETTA(Unit<Q> unit) {
        return unit.transform(E21);
    }

    /**
     * Returns the specified unit multiplied by the factor
     * <code>10<sup>18</sup></code>
     *
     * @param <Q> The type of the quantity measured by the unit.
     * @param unit any unit.
     * @return <code>unit.times(1e18)</code>.
     */
    public static <Q extends Quantity<Q>> Unit<Q> EXA(Unit<Q> unit) {
        return unit.transform(E18);
    }

    /**
     * Returns the specified unit multiplied by the factor
     * <code>10<sup>15</sup></code>
     *
     * @param <Q> The type of the quantity measured by the unit.
     * @param unit any unit.
     * @return <code>unit.times(1e15)</code>.
     */
    public static <Q extends Quantity<Q>> Unit<Q> PETA(Unit<Q> unit) {
        return unit.transform(E15);
    }

    /**
     * Returns the specified unit multiplied by the factor
     * <code>10<sup>12</sup></code>
     *
     * @param <Q> The type of the quantity measured by the unit.
     * @param unit any unit.
     * @return <code>unit.times(1e12)</code>.
     */
    public static <Q extends Quantity<Q>> Unit<Q> TERA(Unit<Q> unit) {
        return unit.transform(E12);
    }

    /**
     * Returns the specified unit multiplied by the factor
     * <code>10<sup>9</sup></code>
     *
     * @param <Q> The type of the quantity measured by the unit.
     * @param unit any unit.
     * @return <code>unit.times(1e9)</code>.
     */
    public static <Q extends Quantity<Q>> Unit<Q> GIGA(Unit<Q> unit) {
        return unit.transform(E9);
    }

    /**
     * Returns the specified unit multiplied by the factor
     * <code>10<sup>6</sup></code>
     *
     * @param <Q> The type of the quantity measured by the unit.
     * @param unit any unit.
     * @return <code>unit.times(1e6)</code>.
     */
    public static <Q extends Quantity<Q>> Unit<Q> MEGA(Unit<Q> unit) {
        return unit.transform(E6);
    }

    /**
     * Returns the specified unit multiplied by the factor
     * <code>10<sup>3</sup></code>
     *
     * @param <Q> The type of the quantity measured by the unit.
     * @param unit any unit.
     * @return <code>unit.times(1e3)</code>.
     */
    public static <Q extends Quantity<Q>> Unit<Q> KILO(Unit<Q> unit) {
        return unit.transform(E3);
    }

    /**
     * Returns the specified unit multiplied by the factor
     * <code>10<sup>2</sup></code>
     *
     * @param <Q> The type of the quantity measured by the unit.
     * @param unit any unit.
     * @return <code>unit.times(1e2)</code>.
     */
    public static <Q extends Quantity<Q>> Unit<Q> HECTO(Unit<Q> unit) {
        return unit.transform(E2);
    }

    /**
     * Returns the specified unit multiplied by the factor
     * <code>10<sup>1</sup></code>
     *
     * @param <Q> The type of the quantity measured by the unit.
     * @param unit any unit.
     * @return <code>unit.times(1e1)</code>.
     */
    public static <Q extends Quantity<Q>> Unit<Q> DEKA(Unit<Q> unit) {
        return unit.transform(E1);
    }

    /**
     * Returns the specified unit multiplied by the factor
     * <code>10<sup>-1</sup></code>
     *
     * @param <Q> The type of the quantity measured by the unit.
     * @param unit any unit.
     * @return <code>unit.times(1e-1)</code>.
     */
    public static <Q extends Quantity<Q>> Unit<Q> DECI(Unit<Q> unit) {
        return unit.transform(Em1);
    }

    /**
     * Returns the specified unit multiplied by the factor
     * <code>10<sup>-2</sup></code>
     *
     * @param <Q> The type of the quantity measured by the unit.
     * @param unit any unit.
     * @return <code>unit.times(1e-2)</code>.
     */
    public static <Q extends Quantity<Q>> Unit<Q> CENTI(Unit<Q> unit) {
        return unit.transform(Em2);
    }

    /**
     * Returns the specified unit multiplied by the factor
     * <code>10<sup>-3</sup></code>
     *
     * @param <Q> The type of the quantity measured by the unit.
     * @param unit any unit.
     * @return <code>unit.times(1e-3)</code>.
     */
    public static <Q extends Quantity<Q>> Unit<Q> MILLI(Unit<Q> unit) {
        return unit.transform(Em3);
    }

    /**
     * Returns the specified unit multiplied by the factor
     * <code>10<sup>-6</sup></code>
     *
     * @param <Q> The type of the quantity measured by the unit.
     * @param unit any unit.
     * @return <code>unit.times(1e-6)</code>.
     */
    public static <Q extends Quantity<Q>> Unit<Q> MICRO(Unit<Q> unit) {
        return unit.transform(Em6);
    }

    /**
     * Returns the specified unit multiplied by the factor
     * <code>10<sup>-9</sup></code>
     *
     * @param <Q> The type of the quantity measured by the unit.
     * @param unit any unit.
     * @return <code>unit.times(1e-9)</code>.
     */
    public static <Q extends Quantity<Q>> Unit<Q> NANO(Unit<Q> unit) {
        return unit.transform(Em9);
    }

    /**
     * Returns the specified unit multiplied by the factor
     * <code>10<sup>-12</sup></code>
     *
     * @param <Q> The type of the quantity measured by the unit.
     * @param unit any unit.
     * @return <code>unit.times(1e-12)</code>.
     */
    public static <Q extends Quantity<Q>> Unit<Q> PICO(Unit<Q> unit) {
        return unit.transform(Em12);
    }

    /**
     * Returns the specified unit multiplied by the factor
     * <code>10<sup>-15</sup></code>
     *
     * @param <Q> The type of the quantity measured by the unit.
     * @param unit any unit.
     * @return <code>unit.times(1e-15)</code>.
     */
    public static <Q extends Quantity<Q>> Unit<Q> FEMTO(Unit<Q> unit) {
        return unit.transform(Em15);
    }

    /**
     * Returns the specified unit multiplied by the factor
     * <code>10<sup>-18</sup></code>
     *
     * @param <Q> The type of the quantity measured by the unit.
     * @param unit any unit.
     * @return <code>unit.times(1e-18)</code>.
     */
    public static final <Q extends Quantity<Q>> Unit<Q> ATTO(Unit<Q> unit) {
        return unit.transform(Em18);
    }

    /**
     * Returns the specified unit multiplied by the factor
     * <code>10<sup>-21</sup></code>
     *
     * @param <Q> The type of the quantity measured by the unit.
     * @param unit any unit.
     * @return <code>unit.times(1e-21)</code>.
     */
    public static <Q extends Quantity<Q>> Unit<Q> ZEPTO(Unit<Q> unit) {
        return unit.transform(Em21);
    }

    /**
     * Returns the specified unit multiplied by the factor
     * <code>10<sup>-24</sup></code>
     *
     * @param <Q> The type of the quantity measured by the unit.
     * @param unit any unit.
     * @return <code>unit.times(1e-24)</code>.
     */
    public static <Q extends Quantity<Q>> Unit<Q> YOCTO(Unit<Q> unit) {
        return unit.transform(Em24);
    }
    // Holds prefix converters (optimization).
    private static final MultiplyConverter E24 = new MultiplyConverter(
            BigInteger.TEN.pow(24).doubleValue());

    private static final MultiplyConverter E21 = new MultiplyConverter(
            BigInteger.TEN.pow(21).doubleValue());

    private static final MultiplyConverter E18 = new MultiplyConverter(
            BigInteger.TEN.pow(18).doubleValue());

    private static final MultiplyConverter E15 = new MultiplyConverter(
            BigInteger.TEN.pow(15).doubleValue());

    private static final MultiplyConverter E12 = new MultiplyConverter(
            BigInteger.TEN.pow(12).doubleValue());

    private static final MultiplyConverter E9 = new MultiplyConverter(
            BigInteger.TEN.pow(9).doubleValue());

    private static final MultiplyConverter E6 = new MultiplyConverter(
            BigInteger.TEN.pow(6).doubleValue());

    private static final MultiplyConverter E3 = new MultiplyConverter(
            BigInteger.TEN.pow(3).doubleValue());

    private static final MultiplyConverter E2 = new MultiplyConverter(
            BigInteger.TEN.pow(2).doubleValue());

    private static final MultiplyConverter E1 = new MultiplyConverter(
            BigInteger.TEN.pow(1).doubleValue());

    private static final RationalConverter Em1 = new RationalConverter(
            BigInteger.ONE, BigInteger.TEN.pow(1));

    private static final RationalConverter Em2 = new RationalConverter(
            BigInteger.ONE, BigInteger.TEN.pow(2));

    private static final RationalConverter Em3 = new RationalConverter(
            BigInteger.ONE, BigInteger.TEN.pow(3));

    private static final RationalConverter Em6 = new RationalConverter(
            BigInteger.ONE, BigInteger.TEN.pow(6));

    private static final RationalConverter Em9 = new RationalConverter(
            BigInteger.ONE, BigInteger.TEN.pow(9));

    private static final RationalConverter Em12 = new RationalConverter(
            BigInteger.ONE, BigInteger.TEN.pow(12));

    private static final RationalConverter Em15 = new RationalConverter(
            BigInteger.ONE, BigInteger.TEN.pow(15));

    private static final RationalConverter Em18 = new RationalConverter(
            BigInteger.ONE, BigInteger.TEN.pow(18));

    private static final RationalConverter Em21 = new RationalConverter(
            BigInteger.ONE, BigInteger.TEN.pow(21));

    private static final RationalConverter Em24 = new RationalConverter(
            BigInteger.ONE, BigInteger.TEN.pow(24));

}

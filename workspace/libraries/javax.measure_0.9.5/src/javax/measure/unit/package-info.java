/**
 *   Copyright (c) 2005-2010, Jean-Marie Dautelle, Werner Keil
 *   All rights reserved.
 *
 *   See LICENSE.txt for the Specification License
 */
/**
 * Provides support for programatic unit handling.
 *
 * <h3> Standard/NonStandard Units</h3>
 *      Standard units and prefixes are provided by the
 *      {@link javax.measure.unit.MetricSystem} class (Système International d'Unités) and
 *      about 50 non-standard units are available through the
 *      {@link javax.measure.unit.USCustomarySystem} class.
 *
 * <h3>Usage examples:</h3>
 * [code]
 *
 * import static javax.measure.unit.MetricSystem.*;
 * import static javax.measure.unit.USCustomarySystem.*;

 * public class Main {
 *     public void main(String[] args) {
 *
 *         // Conversion between units (explicit way).
 *         Unit<Length> sourceUnit = KILO(METRE);
 *         Unit<Length> targetUnit = MILE;
 *         UnitConverter uc = sourceUnit.getConverterTo(targetUnit);
 *         System.out.println(uc.convert(10)); // Converts 10 km to miles.
 *
 *         // Same conversion than above, packed in one line.
 *         System.out.println(KILO(METRE).getConverterTo(MILE).convert(10));
 *
 *         // Retrieval of the system unit (identifies the measurement type).
 *         System.out.println(REVOLUTION.divide(MINUTE).toMetric());
 *
 *         // Dimension checking (allows/disallows conversions)
 *         System.out.println(ELECTRON_VOLT.isCompatible(WATT.times(HOUR)));
 *
 *         // Retrieval of the unit dimension (depends upon the current model).
 *         System.out.println(ELECTRON_VOLT.getDimension());
 *     }
 * }
 *
 * > 6.2137119223733395
 * > 6.2137119223733395
 * > rad/s
 * > true
 * > [L]²·[M]/[T]²
 * [/code]
 *
 * <h3>Unit Parameterization</h3>
 *
 *     Units are parameterized (e.g. &lt;Time extends {@link javax.measure.quantity.Quantity Quantity}>) to
 *     enforce compile-time checks of units/measures consistency, for example:[code]
 *
 *     Unit<Time> MINUTE = SECOND.times(60); // Ok.
 *     Unit<Time> MINUTE = METRE.times(60); // Compile error.
 *
 *     Unit<Pressure> HECTOPASCAL = HECTO(PASCAL); // Ok.
 *     Unit<Pressure> HECTOPASCAL = HECTO(NEWTON); // Compile error.
 *
 *     Time duration = QuantityFactory.getInstance(Time.class).create(2, MINUTE); // Ok.
 *     Time duration = QuantityFactory.getInstance(Time.class).create(2, CELSIUS); // Compile error.
 *
 *     long milliseconds = duration.longValue(MILLI(SECOND)); // Ok.
 *     long milliseconds = duration.longValue(POUND); // Compile error.
 *     [/code]
 *
 *     Runtime checks of dimension consistency can be done for more complex cases.
 *
 *     [code]
 *     Unit<Area> SQUARE_FOOT = FOOT.times(FOOT).asType(Area.class); // Ok.
 *     Unit<Area> SQUARE_FOOT = FOOT.times(KELVIN).asType(Area.class); // Runtime error.
 *
 *     Unit<Temperature> KELVIN = Unit.valueOf("K").asType(Temperature.class); // Ok.
 *     Unit<Temperature> KELVIN = Unit.valueOf("kg").asType(Temperature.class); // Runtime error.
 *     [/code]
 *     </p>
 *
 * @author <a href="mailto:jean-marie@dautelle.com">Jean-Marie Dautelle</a>
 * @version 0.9.5, February 24, 2010
 */
package javax.measure.unit;

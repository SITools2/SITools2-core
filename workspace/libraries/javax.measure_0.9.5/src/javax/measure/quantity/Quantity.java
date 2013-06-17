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
package javax.measure.quantity;

import javax.measure.unit.Unit;

/**
 * <p> Represents a quantitative properties or attributes of thing.
 *     Mass, time, distance, heat, and angular separation
 *     are among the familiar examples of quantitative properties.</p>
 *
 * <p> {@link QuantityFactory} can be used to generate simple instances of this class.[code]
 *         Time duration = QuantityFactory.getInstance(Time.class).create(12, MILLI(SECOND));
 *         Length distance = QuantityFactory.getInstance(Length.class).create(37.4, MILES);
 *     [/code]</p>
 *
 * <p> Quantities are used to specify the quantitative property associated
 *     to a class through class parameterization.[code]
 *          Unit<Mass> pound = ...
 *          Sensor<Temperature> thermometer = ...
 *          Vector3D<Velocity> aircraftSpeed = ...
 *     [/code] </p>
 *
 * @param <Q> The type of the quantity.
 *
 * @author  <a href="mailto:desruisseaux@users.sourceforge.net">Martin Desruisseaux</a>
 * @author  <a href="mailto:jsr275@catmedia.us">Werner Keil</a>
 * @author  <a href="mailto:jean-marie@dautelle.com">Jean-Marie Dautelle</a>
 * @version 1.7 ($Revision: 198 $), $Date: 2010-02-24 19:53:02 +0100 (Mi, 24 Feb 2010) $
 * @see <a href="http://en.wikipedia.org/wiki/Quantity">Wikipedia: Quantity</a>
 */
public interface Quantity<Q extends Quantity<Q>> extends Comparable<Quantity<Q>> {

    /**
     * Returns the magnitude or multitude value of this quantity stated in this
     * quantity {@linkplain #getUnit() unit}.
     *
     * @return the numeric value of this quantity in the units returned by {@link #getUnit()}.
     */
    Number getValue();
    
    /**
     * Returns the original unit of this quantity (the one specified at creation).
     *
     * @return the original unit of this quantity.
     */
    Unit<Q> getUnit();

    /**
     * Returns the value of this quantity stated in the specified unit. 
     * This method is recommended over <code>
     * q.getUnit().getConverterTo(unit).convert(q.getValue())</code>,
     * it is not only faster but also detects potential overflow if the
     * value stated in the specific unit cannot be represented or rounded.
     *
     * @param unit the unit in which the returned value is stated.
     * @return the value of this quantity when stated in the specified unit.
     * @throws ArithmeticException if this quantity cannot be converted to
     *         when stated in the specified unit (e.g. overflow).
     */
    Number getValue(Unit<Q> unit) throws ArithmeticException;

    /**
     * Returns the value of this quantity as <code>double</code> stated
     * in the specified unit. This method is recommended over <code>
     * q.getUnit().getConverterTo(unit).convert(q.getValue()).doubleValue()</code>
     *
     * @param unit the unit in which the returned value is stated.
     * @return the value of this quantity when stated in the specified unit.
     */
    double doubleValue(Unit<Q> unit);

    /**
     * Returns the value of this quantity as <code>long</code> stated
     * in the specified unit. This method is recommended over <code>
     * q.getUnit().getConverterTo(unit).convert(q.getValue()).longValue()</code>,
     * it is not only faster but also detects potential overflow if the
     * value stated in the specific unit cannot be represented as
     * <code>long</code>.
     *
     * @param unit the unit in which the returned value is stated.
     * @return the value of this quantity when stated in the specified unit.
     * @throws ArithmeticException if this quantity cannot represented as
     *         a <code>long</code> when stated in the specified unit (e.g. overflow).
     */
    long longValue(Unit<Q> unit) throws ArithmeticException;
}

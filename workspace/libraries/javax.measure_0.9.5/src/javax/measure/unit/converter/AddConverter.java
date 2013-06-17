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
package javax.measure.unit.converter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import javax.measure.unit.UnitConverter;


/**
 * <p> This class represents a converter adding a constant offset
 *     to numeric values (<code>double</code> based).</p>
 *
 * <p> Instances of this class are immutable.</p>
 *
 * @author  <a href="mailto:jean-marie@dautelle.com">Jean-Marie Dautelle</a>
 * @author  <a href="mailto:jsr275@catmedia.us">Werner Keil</a>
 *
 * @version 1.0.1 ($Revision: 169 $), $Date: 2010-02-21 18:48:40 +0100 (So, 21 Feb 2010) $
 */
public final class AddConverter extends UnitConverter {

    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = 8088797685241019815L;

    /**
     * Holds the offset.
     */
    private final double offset;

    /**
     * Creates an add converter with the specified offset.
     *
     * @param  offset the offset value.
     * @throws IllegalArgumentException if offset is <code>0.0</code>
     *         (would result in identity converter).
     */
    public AddConverter(double offset) {
        if (offset == 0.0) {
            throw new IllegalArgumentException("Would result in identity converter");
        }
        this.offset = offset;
    }

    /**
     * Returns the offset value for this add converter.
     *
     * @return the offset value.
     */
    public double getOffset() {
        return offset;
    }

    @Override
    public UnitConverter concatenate(UnitConverter converter) {
        if (converter instanceof AddConverter) {
            double newOffset = offset + ((AddConverter) converter).offset;
            return newOffset == 0.0 ? IDENTITY : new AddConverter(newOffset);
        } else {
            return super.concatenate(converter);
        }
    }

    @Override
    public AddConverter inverse() {
        return new AddConverter(-offset);
    }

    @Override
    public double convert(double value) {
        return value + offset;
    }

    @Override
    public Number convert(Number value, MathContext ctx) throws ArithmeticException {
        if (value instanceof BigDecimal) {
        	return ((BigDecimal)value).add(BigDecimal.valueOf(offset), ctx);
        } else if (value instanceof BigInteger) {
        	return ((BigInteger)value).add(BigInteger.valueOf((long) offset));
        }
        return convert(value.doubleValue());
    }

    @Override
    public final String toString() {
        return "AddConverter(" + offset + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof AddConverter)) {
            return false;
        }
        AddConverter that = (AddConverter) obj;
        return this.offset == that.offset;
    }

    @Override
    public int hashCode() {
        long bits = Double.doubleToLongBits(offset);
        return (int) (bits ^ (bits >>> 32));
    }

    @Override
    public boolean isLinear() {
        return false;
    }
}

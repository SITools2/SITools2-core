/**
 *   Copyright (c) 2005-2010, Jean-Marie Dautelle, Werner Keil
 *   All rights reserved.
 *
 *   See LICENSE.txt for the Specification License
 */
package javax.measure.unit.converter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import javax.measure.unit.UnitConverter;


/**
 * <p> This class represents a converter multiplying numeric values by a
 *     constant scaling factor (<code>double</code> based).</p>
 *
 * <p> Instances of this class are immutable.</p>
 *
 * @author  <a href="mailto:jean-marie@dautelle.com">Jean-Marie Dautelle</a>
 * @author  <a href="mailto:jsr275@catmedia.us">Werner Keil</a>
 * @version 1.4 ($Revision: 169 $), $Date: 2010-02-21 18:48:40 +0100 (So, 21 Feb 2010) $
 */
public final class MultiplyConverter extends UnitConverter {
    /** The serialVersionUID */
    private static final long serialVersionUID = 6497743504427978825L;

    /**
     * Holds the scale factor.
     */
    private final double factor;

    /**
     * Creates a multiply converter with the specified scale factor.
     *
     * @param  factor the scaling factor.
     * @throws IllegalArgumentException if coefficient is <code>1.0</code>
     *        (would result in identity converter)
     */
    public MultiplyConverter(double factor) {
        if (factor == 1.0)
            throw new IllegalArgumentException("Would result in identity converter");
        this.factor = factor;
    }

    /**
     * Returns the scale factor of this converter.
     *
     * @return the scale factor.
     */
    public double getFactor() {
        return factor;
    }

    @Override
    public UnitConverter concatenate(UnitConverter converter) {
        if (converter instanceof MultiplyConverter) {
            double newfactor = factor * ((MultiplyConverter) converter).factor;
            return newfactor == 1.0 ? IDENTITY : new MultiplyConverter(newfactor);
        } else
            return super.concatenate(converter);
    }

    @Override
    public MultiplyConverter inverse() {
        return new MultiplyConverter(1.0 / factor);
    }

    @Override
    public double convert(double value) {
        return value * factor;
    }

    @Override
    public Number convert(Number value, MathContext ctx) throws ArithmeticException {
        if (value instanceof BigDecimal) {
        	return ((BigDecimal)value).multiply(BigDecimal.valueOf(factor), ctx);
        } else if (value instanceof BigInteger) {
        	return ((BigInteger)value).multiply(BigInteger.valueOf((long) factor));
        }
        return convert(value.doubleValue());
    }

    @Override
    public final String toString() {
        return "MultiplyConverter("+ factor + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof MultiplyConverter))
            return false;
        MultiplyConverter that = (MultiplyConverter) obj;
        return this.factor == that.factor;
    }

    @Override
    public int hashCode() {
        long bits = Double.doubleToLongBits(factor);
    return (int)(bits ^ (bits >>> 32));
    }

    @Override
    public boolean isLinear() {
        return true;
    }
}

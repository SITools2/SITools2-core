/**
 *   Copyright (c) 2005-2010, Jean-Marie Dautelle, Werner Keil
 *   All rights reserved.
 *
 *   See LICENSE.txt for the Specification License
 */
package javax.measure.unit.converter;

import java.math.BigDecimal;
import java.math.MathContext;
import javax.measure.unit.UnitConverter;


/**
 * <p> This class represents a exponential converter of limited precision.
 *     Such converter  is typically used to create inverse of logarithmic unit.
 *
 * <p> Instances of this class are immutable.</p>
 *
 * @author  <a href="mailto:jean-marie@dautelle.com">Jean-Marie Dautelle</a>
 * @author  <a href="mailto:jsr275@catmedia.us">Werner Keil</a>
 * @version 1.0.1 ($Revision: 169 $), $Date: 2010-02-21 18:48:40 +0100 (So, 21 Feb 2010) $
 */
public final class ExpConverter extends UnitConverter {

    /** The serialVersionUID */
    private static final long serialVersionUID = -1862583888012861945L;

    /**
     * Holds the logarithmic base.
     */
    private final double base;

    /**
     * Holds the natural logarithm of the base.
     */
    private final double logOfBase;

    /**
     * Creates a logarithmic converter having the specified base.
     *
     * @param  base the logarithmic base (e.g. <code>Math.E</code> for
     *         the Natural Logarithm).
     */
    public ExpConverter(double base) {
        this.base = base;
        this.logOfBase = Math.log(base);
    }

    /**
     * Returns the exponential base of this converter.
     *
     * @return the exponential base (e.g. <code>Math.E</code> for
     *         the Natural Exponential).
     */
    public double getBase() {
        return base;
    }

    @Override
    public UnitConverter inverse() {
        return new LogConverter(base);
    }

    @Override
    public final String toString() {
        return "ExpConverter("+ base + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ExpConverter))
            return false;
        ExpConverter that = (ExpConverter) obj;
        return this.base == that.base;
    }

    @Override
    public int hashCode() {
        long bits = Double.doubleToLongBits(base);
        return (int) (bits ^ (bits >>> 32));
    }

    @Override
    public double convert(double amount) {
            return Math.exp(logOfBase * amount);
    }

    @Override
    public Number convert(Number value, MathContext ctx) throws ArithmeticException {
        return BigDecimal.valueOf(convert(value.doubleValue())); // Reverts to double conversion.
    }

    @Override
    public boolean isLinear() {
        return false;
    }

}

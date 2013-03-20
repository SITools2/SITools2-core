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
 * <p> This class represents a logarithmic converter of limited precision.
 *     Such converter  is typically used to create logarithmic unit.
 *     For example:[code]
 *     Unit<Dimensionless> BEL = Unit.ONE.transform(new LogConverter(10).inverse());
 *     [/code]</p>
 *
 * <p> Instances of this class are immutable.</p>
 *
 * @author  <a href="mailto:jean-marie@dautelle.com">Jean-Marie Dautelle</a>
 * @author  <a href="mailto:jsr275@catmedia.us">Werner Keil</a>
 * @version 1.3 ($Revision: 169 $), $Date: 2010-02-21 18:48:40 +0100 (So, 21 Feb 2010) $
 */
public final class LogConverter extends UnitConverter {

    /** The serialVersionUID */
    private static final long serialVersionUID = -5581266460675123322L;

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
    public LogConverter(double base) {
        this.base = base;
        logOfBase = Math.log(base);
    }

    /**
     * Returns the logarithmic base of this converter.
     *
     * @return the logarithmic base (e.g. <code>Math.E</code> for
     *         the Natural Logarithm).
     */
    public double getBase() {
        return base;
    }

    @Override
    public UnitConverter inverse() {
        return new ExpConverter(base);
    }

    @Override
    public final String toString() {
        return "LogConverter("+ base + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof LogConverter))
            return false;
        LogConverter that = (LogConverter) obj;
        return this.base == that.base;
    }

    @Override
    public int hashCode() {
        long bits = Double.doubleToLongBits(base);
        return (int) (bits ^ (bits >>> 32));
    }

    @Override
    public double convert(double amount) {
        return Math.log(amount) / logOfBase;
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

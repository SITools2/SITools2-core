/**
 *   Copyright (c) 2005-2010, Jean-Marie Dautelle, Werner Keil
 *   All rights reserved.
 *
 *   See LICENSE.txt for the Specification License
 */
package javax.measure.unit.converter;

import java.math.BigInteger;
import java.math.MathContext;

import javax.measure.unit.UnitConverter;


/**
 * <p> This class represents a converter multiplying numeric values by an
 *     exact scaling factor (represented as the quotient of two
 *     <code>BigInteger</code> numbers).</p>
 *
 * <p> Instances of this class are immutable.</p>
 *
 * @author  <a href="mailto:jean-marie@dautelle.com">Jean-Marie Dautelle</a>
 * @author  <a href="mailto:jsr275@catmedia.us">Werner Keil</a>
 * @version 1.1 ($Revision: 178 $), $Date: 2010-02-22 20:18:18 +0100 (Mo, 22 Feb 2010) $
 */
public final class RationalConverter extends UnitConverter {

    /** The serialVersionUID */
    private static final long serialVersionUID = 5313011404391445406L;

    /**
     * Holds the converter dividend.
     */
    private final BigInteger dividend;
    /**
     * Holds the converter divisor (always positive).
     */
    private final BigInteger divisor;

    /**
     * Creates a rational converter with the specified dividend and
     * divisor.
     *
     * @param dividend the dividend.
     * @param divisor the positive divisor.
     * @throws IllegalArgumentException if <code>divisor &lt;= 0</code>
     * @throws IllegalArgumentException if <code>dividend == divisor</code>
     */
    public RationalConverter(BigInteger dividend, BigInteger divisor) {
          if (divisor.compareTo(BigInteger.ZERO) <= 0)
            throw new IllegalArgumentException("Negative or zero divisor");
        if (dividend.equals(divisor))
            throw new IllegalArgumentException("Would result in identity converter");
        this.dividend = dividend; // Exact conversion.
        this.divisor = divisor; // Exact conversion.
    }

    /**
     * Returns the integer dividend for this rational converter.
     *
     * @return this converter dividend.
     */
    public BigInteger getDividend() {
        return dividend;
    }

    /**
     * Returns the integer (positive) divisor for this rational converter.
     *
     * @return this converter divisor.
     */
    public BigInteger getDivisor() {
        return divisor;
    }

    @Override
    public double convert(double value) {
        return value * toDouble(dividend) / toDouble(divisor);
    }

    // Optimization of BigInteger.doubleValue() (implementation too inneficient).
    private static double toDouble(BigInteger integer) {
        return (integer.bitLength() < 64) ? integer.longValue() : integer.doubleValue();
    }

    @Override
    public Number convert(Number value, MathContext ctx) throws ArithmeticException {
    	// Patch SITools2
    	return ((Double) value.doubleValue()) * dividend.doubleValue() / divisor.doubleValue();
//        if (value instanceof BigInteger) {
//        	return ((BigInteger)value).multiply(dividend).divide(divisor);
//        } else if (value instanceof BigDecimal){
//        	return ((BigDecimal)value).toBigInteger().multiply(dividend).divide(divisor);
//        } else {
//        	return BigInteger.valueOf(value.longValue()).multiply(dividend).divide(divisor);
//        }
    }

    @Override
    public UnitConverter concatenate(UnitConverter converter) {
        if (converter instanceof RationalConverter) {
            RationalConverter that = (RationalConverter) converter;
            BigInteger dividend = this.getDividend().multiply(that.getDividend());
            BigInteger divisor = this.getDivisor().multiply(that.getDivisor());
            BigInteger gcd = dividend.gcd(divisor);
            dividend = dividend.divide(gcd);
            divisor = divisor.divide(gcd);
            return (dividend.equals(BigInteger.ONE) && divisor.equals(BigInteger.ONE))
                    ? IDENTITY : new RationalConverter(dividend, divisor);
        } else
            return super.concatenate(converter);
    }

    @Override
    public RationalConverter inverse() {
        return dividend.signum() == -1 ? new RationalConverter(getDivisor().negate(), getDividend().negate())
                : new RationalConverter(getDivisor(), getDividend());
    }

    @Override
    public final String toString() {
        return "RationalConverter("+ dividend +  "," + divisor + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof RationalConverter))
            return false;
        RationalConverter that = (RationalConverter) obj;
        return this.dividend.equals(that.dividend) &&
                this.divisor.equals(that.divisor);
    }

    @Override
    public int hashCode() {
        return dividend.hashCode() + divisor.hashCode();
    }

    @Override
    public boolean isLinear() {
        return true;
    }

}

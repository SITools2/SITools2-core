/**
 *   Copyright (c) 2005-2010, Jean-Marie Dautelle, Werner Keil
 *   All rights reserved.
 *
 *   See LICENSE.txt for the Specification License
 */
package javax.measure.unit;

import java.io.Serializable;
import java.math.MathContext;

/**
 * <p> This class represents a converter of numeric values.</p>
 *
 * <p> It is not required for sub-classes to be immutable
 *     (e.g. currency converter).</p>
 *
 * <p> Sub-classes must ensure unicity of the {@linkplain #IDENTITY identity}
 *     converter. In other words, if the result of an operation is equivalent
 *     to the identity converter, then the unique {@link #IDENTITY} instance
 *     should be returned.</p>
 *
 * @author  <a href="mailto:jean-marie@dautelle.com">Jean-Marie Dautelle</a>
 * @author  <a href="mailto:jsr275@catmedia.us">Werner Keil</a>
 * @version 1.2 ($Revision: 188 $), $Date: 2010-02-24 13:07:13 +0100 (Mi, 24 Feb 2010) $
 */
public abstract class UnitConverter implements Serializable {

    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = 2557410026012911803L;

    /**
     * Holds the identity converter (unique). This converter does nothing
     * (<code>ONE.convert(x) == x</code>). This instance is unique.
     */
    public static final UnitConverter IDENTITY = new Identity();

    /**
     * Default constructor.
     */
    protected UnitConverter() {
    }

    /**
     * Returns the inverse of this converter. If <code>x</code> is a valid
     * value, then <code>x == inverse().convert(convert(x))</code> to within
     * the accuracy of computer arithmetic.
     *
     * @return the inverse of this converter.
     */
    public abstract UnitConverter inverse();

    /**
     * Converts a <code>double</code> value.
     *
     * @param  value the numeric value to convert.
     * @return the <code>double</code> value after conversion.
     */
    public abstract double convert(double value);

    /**
     * Converts a {@link Number} value.
     *
     * @param value the numeric value to convert.
     * @param ctx the math context being used for conversion.
     * @return the decimal value after conversion.
     * @throws ArithmeticException if the result is inexact but the
     *         rounding mode is <code>MathContext.UNLIMITED</code> or
     *         <code>mathContext.precision == 0</code> and the quotient has a
     *         non-terminating decimal expansion.
     */
    public abstract Number convert(Number value, MathContext ctx) throws ArithmeticException;

    /**
     * Converts a {@link Number} value.
     *
     * @param value the numeric value to convert.
     * @return the decimal value after conversion.
     * @throws ArithmeticException if the result is inexact but the
     *         rounding mode is <code>MathContext.UNLIMITED</code> or
     *         <code>mathContext.precision == 0</code> and the quotient has a
     *         non-terminating decimal expansion.
     */
    public Number convert(Number value) throws ArithmeticException {
    	return convert(value, MathContext.UNLIMITED);
    }
    
    /**
     * Indicates whether this converter is considered to be the the same as the
     * one specified.
     *
     * @param  cvtr the converter with which to compare.
     * @return <code>true</code> if the specified object is a converter
     *         considered equals to this converter;<code>false</code> otherwise.
     */
    @Override
    public abstract boolean equals(Object cvtr);

    /**
     * Returns a hash code value for this converter. Equals object have equal
     * hash codes.
     *
     * @return this converter hash code value.
     * @see    #equals
     */
    @Override
    public abstract int hashCode();

    /**
     * Concatenates this converter with another converter. The resulting
     * converter is equivalent to first converting by the specified converter
     * (right converter), and then converting by this converter (left converter).
     *
     * <p>Note: Implementations must ensure that the {@link #IDENTITY} instance
     *          is returned if the resulting converter is an identity
     *          converter.</p>
     *
     * @param  converter the other converter.
     * @return the concatenation of this converter with the other converter.
     */
    public UnitConverter concatenate(UnitConverter converter) {
        return (converter == IDENTITY) ? this : new CompoundImpl(this, converter);
    }

    /**
     * Indicates if this converter is linear. A converter is linear if
     * <code>convert(u + v) == convert(u) + convert(v)</code> and
     * <code>convert(r * u) == r * convert(u)</code>. For linear converters the
     * following property always hold:[code]
     *     y1 = c1.convert(x1);
     *     y2 = c2.convert(x2);
     *     // then y1*y2 == c1.concatenate(c2).convert(x1*x2)
     * [/code] </p>
     *
     * @return <code>true</code> if this converter is made of distinct converter;
     *         <code>false</code> otherwise.
     */
    public abstract boolean isLinear();

    /**
     * This interface is implemented by converters made up of two
     * separate converters (in matrix notation
     * <code>[compound] = [left] x [right]</code>).
     */
    interface Compound {

        /**
         * Returns the left converter of this compound converter
         * (the last one performing the conversion).
         *
         * @return the left converter.
         */
        UnitConverter getLeft();

        /**
         * Returns the right converter of this compound converter
         * (the first one performing the conversion).
         *
         * @return the right converter.
         */
        UnitConverter getRight();
    }

    /**
     * This inner class represents the identity converter (singleton).
     */
    private static final class Identity extends UnitConverter {

        /**
         * For cross-version compatibility.
         */
        private static final long serialVersionUID = 7675901502919547460L;

        @Override
        public Identity inverse() {
            return this;
        }

        @Override
        public double convert(double value) {
            return value;
        }

        @Override
        public Number convert(Number value, MathContext ctx) {
            return value;
        }

        @Override
        public UnitConverter concatenate(UnitConverter converter) {
            return converter;
        }

        @Override
        public boolean equals(Object cvtr) {
            return this == cvtr; // Unique instance.
        }

        @Override
        public int hashCode() {
            return 0;
        }

        @Override
        public boolean isLinear() {
            return true;
        }
    }

    /**
     * This inner class represents a compound converter (non-linear).
     */
    private static final class CompoundImpl extends UnitConverter implements Compound {

        /**
         * For cross-version compatibility.
         */
        private static final long serialVersionUID = 2242882007946934958L;

        /**
         * Holds the first converter.
         */
        private final UnitConverter left;

        /**
         * Holds the second converter.
         */
        private final UnitConverter right;

        /**
         * Creates a compound converter resulting from the combined
         * transformation of the specified converters.
         *
         * @param  left the left converter.
         * @param  right the right converter.
         */
        private CompoundImpl(UnitConverter left, UnitConverter right) {
            this.left = left;
            this.right = right;
        }

        @Override
        public UnitConverter inverse() {
            return new CompoundImpl(right.inverse(), left.inverse());
        }

        @Override
        public double convert(double value) {
            return left.convert(right.convert(value));
        }

        @Override
        public Number convert(Number value, MathContext ctx) {
            return left.convert(right.convert(value, ctx), ctx);
        }

        @Override
        public boolean equals(Object cvtr) {
            if (this == cvtr)
                return true;
            if (!(cvtr instanceof Compound))
                return false;
            Compound that = (Compound) cvtr;
            return (this.left.equals(that.getLeft()))
                    && (this.right.equals(that.getRight()));
        }

        @Override
        public int hashCode() {
            return left.hashCode() + right.hashCode();
        }

        @Override
        public boolean isLinear() {
            return left.isLinear() && right.isLinear();
        }

        public UnitConverter getLeft() {
            return left;
        }

        public UnitConverter getRight() {
            return right;
        }
    }
}

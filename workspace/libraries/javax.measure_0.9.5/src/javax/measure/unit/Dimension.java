/**
 *   Copyright (c) 2005-2010, Jean-Marie Dautelle, Werner Keil
 *   All rights reserved.
 *
 *   See LICENSE.txt for the Specification License
 */
package javax.measure.unit;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.measure.quantity.Dimensionless;

/**
 * <p> This class represents the dimension of an unit. Two units <code>u1</code>
 *     and <code>u2</code> are {@linkplain Unit#isCompatible compatible} if and
 *     only if <code>(u1.getDimension().equals(u2.getDimension())))</code>
 *     </p>
 *
 * <p> Instances of this class are immutable.</p>
 *
 * @author  <a href="mailto:jean-marie@dautelle.com">Jean-Marie Dautelle</a>
 * @author  <a href="mailto:jsr275@catmedia.us">Werner Keil</a>
 *
 * @version 1.0.3 ($Revision: 176 $), $Date: 2010-02-22 19:09:37 +0100 (Mo, 22 Feb 2010) $
 * @see <a href="http://www.bipm.org/en/si/si_brochure/chapter1/1-3.html">
 *      BIPM: SI Brochure Chapter 1.3</a>
 */
public final class Dimension implements Serializable {

    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = 2377803885472362640L;

    /**
     * Holds the current physical model.
     */
    private static Model model = Model.STANDARD;

    /**
     * Holds dimensionless.
     */
    public static final Dimension NONE = new Dimension(Unit.ONE);

    /**
     * Holds length dimension (L).
     */
    public static final Dimension LENGTH = new Dimension('L');

    /**
     * Holds mass dimension (M).
     */
    public static final Dimension MASS = new Dimension('M');

    /**
     * Holds time dimension (T).
     */
    public static final Dimension TIME = new Dimension('T');

    /**
     * Holds electric current dimension (I).
     */
    public static final Dimension ELECTRIC_CURRENT = new Dimension('I');

    /**
     * Holds temperature dimension (T).
     */
    public static final Dimension TEMPERATURE = new Dimension('H');

    /**
     * Holds amount of substance dimension (N).
     */
    public static final Dimension AMOUNT_OF_SUBSTANCE = new Dimension('N');

    /**
     * Holds luminous intensity dimension (J).
     */
    public static final Dimension LUMINOUS_INTENSITY = new Dimension('J');

    /**
     * Holds the pseudo unit associated to this dimension.
     */
    private final Unit<?> pseudoUnit;

    /**
     * Creates a new dimension associated to the specified symbol.
     *
     * @param symbol the associated symbol.
     */
    private Dimension(char symbol) {
        pseudoUnit = new BaseUnit<Dimensionless>("[" + symbol + "]");
    }

    /**
     * Creates a dimension having the specified pseudo-unit
     * (base unit or product of base unit).
     *
     * @param pseudoUnit the pseudo-unit identifying this dimension.
     */
    private Dimension(Unit<?> pseudoUnit) {
        this.pseudoUnit = pseudoUnit;
    }

    /**
     * Returns the product of this dimension with the one specified.
     *
     * @param  that the dimension multiplicand.
     * @return <code>this * that</code>
     */
    public final Dimension multiply(Dimension that) {
        return new Dimension(this.pseudoUnit.multiply(that.pseudoUnit));
    }

    /**
     * Returns the quotient of this dimension with the one specified.
     *
     * @param  that the dimension divisor.
     * @return <code>this / that</code>
     */
    public final Dimension divide(Dimension that) {
        return new Dimension(this.pseudoUnit.divide(that.pseudoUnit));
    }

    /**
     * Returns this dimension raised to an exponent.
     *
     * @param  n the exponent.
     * @return the result of raising this dimension to the exponent.
     */
    public final Dimension pow(int n) {
        return new Dimension(this.pseudoUnit.pow(n));
    }

    /**
     * Returns the given root of this dimension.
     *
     * @param  n the root's order.
     * @return the result of taking the given root of this dimension.
     * @throws ArithmeticException if <code>n == 0</code>.
     */
    public final Dimension root(int n) {
        return new Dimension(this.pseudoUnit.root(n));
    }

    /**
     * Returns the fundamental dimensions and their exponent whose product is
     * this dimension or <code>null</code> if this dimension is a fundamental
     * dimension.
     *
     * @return the mapping between the fundamental dimensions and their exponent.
     */
    Map<Dimension, Integer> getProductDimensions() {
        Map<Unit<?>, Integer> pseudoUnits = pseudoUnit.getProductUnits();
        if (pseudoUnit == null) return null;
        Map<Dimension, Integer> fundamentalDimensions = new HashMap<Dimension, Integer>();
        for (Map.Entry<Unit<?>, Integer> entry : pseudoUnits.entrySet()) {
            fundamentalDimensions.put(new Dimension(entry.getKey()), entry.getValue());
        }
        return fundamentalDimensions;
    }

    /**
     * Returns the representation of this dimension.
     *
     * @return the representation of this dimension.
     */
    @Override
    public String toString() {
        return pseudoUnit.toString();
    }

    /**
     * Indicates if the specified dimension is equals to the one specified.
     *
     * @param that the object to compare to.
     * @return <code>true</code> if this dimension is equals to that dimension;
     *         <code>false</code> otherwise.
     */
    @Override
    public boolean equals(Object that) {
        if (this == that)
            return true;
        return (that instanceof Dimension) && pseudoUnit.equals(((Dimension) that).pseudoUnit);
    }

    /**
     * Returns the hash code for this dimension.
     *
     * @return this dimension hashcode value.
     */
    @Override
    public int hashCode() {
        return pseudoUnit.hashCode();
    }

    /**
     * Sets the model used to determinate the units dimensions.
     *
     * @param model the new model to be used when calculating unit dimensions.
     */
    public static void setModel(Model model) {
        Dimension.model = model;
    }

    /**
     * Returns the model used to determinate the units dimensions
     * (default {@link Model#STANDARD STANDARD}).
     *
     * @return the model used when calculating unit dimensions.
     */
    public static Model getModel() {
        return Dimension.model;
    }

    /**
     * This interface represents the mapping between {@linkplain BaseUnit base units}
     * and {@linkplain Dimension dimensions}. Custom models may allow
     * conversions not possible using the {@linkplain #STANDARD standard} model.
     * For example:[code]
     * public static void main(String[] args) {
     *     Dimension.Model relativistic = new Dimension.Model() {
     *         RationalConverter metreToSecond = new RationalConverter(BigInteger.ONE, BigInteger.valueOf(299792458)); // 1/c
     *
     *         public Dimension getDimension(BaseUnit unit) {
     *             if (unit.equals(METRE)) return Dimension.TIME;
     *             return Dimension.Model.STANDARD.getDimension(unit);
     *         }
     *
     *         public UnitConverter getTransform(BaseUnit unit) {
     *             if (unit.equals(METRE)) return metreToSecond;
     *             return Dimension.Model.STANDARD.getTransform(unit);
     *         }};
     *     Dimension.setModel(relativistic);
     *
     *     // Converts 1.0 GeV (energy) to kg (mass).
     *     System.out.println(Unit.valueOf("GeV").getConverterTo(KILOGRAM).convert(1.0));
     * }
     *
     * > 1.7826617302520883E-27[/code]
     */
    public interface Model {

        /**
         * Holds the standard model (default).
         */
        public Model STANDARD = new Model() {

            public Dimension getDimension(BaseUnit<?> unit) {
                if (unit.equals(MetricSystem.METRE))
                    return Dimension.LENGTH;
                if (unit.equals(MetricSystem.KILOGRAM))
                    return Dimension.MASS;
                if (unit.equals(MetricSystem.KELVIN))
                    return Dimension.TEMPERATURE;
                if (unit.equals(MetricSystem.SECOND))
                    return Dimension.TIME;
                if (unit.equals(MetricSystem.AMPERE))
                    return Dimension.ELECTRIC_CURRENT;
                if (unit.equals(MetricSystem.MOLE))
                    return Dimension.AMOUNT_OF_SUBSTANCE;
                if (unit.equals(MetricSystem.CANDELA))
                    return Dimension.LUMINOUS_INTENSITY;
                return new Dimension(new BaseUnit<Dimensionless>("[" + unit.getSymbol() + "]"));
            }

            public UnitConverter getTransform(BaseUnit<?> unit) {
                return UnitConverter.IDENTITY;
            }
        };

        /**
         * Returns the dimension of the specified base unit (a dimension
         * particular to the base unit if the base unit is not recognized).
         *
         * @param unit the base unit for which the dimension is returned.
         * @return the dimension of the specified unit.
         */
        Dimension getDimension(BaseUnit<?> unit);

        /**
         * Returns the normalization transform of the specified base unit
         * ({@link UnitConverter#IDENTITY IDENTITY} if the base unit is
         * not recognized).
         *
         * @param unit the base unit for which the transform is returned.
         * @return the normalization transform.
         */
        UnitConverter getTransform(BaseUnit<?> unit);
    }
}

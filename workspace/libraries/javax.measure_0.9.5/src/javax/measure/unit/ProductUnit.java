 /*******************************************************************************
 * Copyright 2010-2014 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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
package javax.measure.unit;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.measure.quantity.Quantity;

/**
 * <p>  This class represents units formed by the product of rational powers of
 *      existing units.</p>
 *
 * <p> This class maintains the canonical form of this product (simplest form
 *     after factorization). For example: <code>METRE.pow(2).divide(METRE)</code>
 *     returns <code>METRE</code>.</p>
 *
 * @param <Q> The type of the quantity measured by this unit.
 *
 * @author <a href="mailto:jean-marie@dautelle.com">Jean-Marie Dautelle</a>
 * @author  <a href="mailto:jsr275@catmedia.us">Werner Keil</a>
 * @version 1.1 ($Revision: 195 $), $Date: 2010-02-24 18:40:34 +0100 (Mi, 24 Feb 2010) $
 * @see Unit#multiply(Unit)
 * @see Unit#divide(Unit)
 * @see Unit#pow(int)
 * @see Unit#root(int)
 */
public final class ProductUnit<Q extends Quantity<Q>> extends Unit<Q> {

    /**
	 * For cross-version compatibility.
	 */
	private static final long serialVersionUID = -736056598162783537L;

	/**
     * Holds the units composing this product unit.
     */
    private final Element[] elements;

    /**
     * Holds the hashcode (optimization).
     */
    private int hashCode;

    /**
     * Default constructor (used solely to create <code>ONE</code> instance).
     */
    public ProductUnit() {
        elements = new Element[0];
    }

    /**
     * Copy constructor (allows for parameterization of product units).
     *
     * @param productUnit the product unit source.
     * @throws ClassCastException if the specified unit is not a product unit.
     */
    public ProductUnit(Unit<?> productUnit) {
        this.elements = ((ProductUnit<?>) productUnit).elements;
    }

    /**
     * Product unit constructor.
     *
     * @param elements the product elements.
     */
    private ProductUnit(Element[] elements) {
        this.elements = elements;
    }

    /**
     * Returns the unit defined from the product of the specified elements.
     *
     * @param leftElems left multiplicand elements.
     * @param rightElems right multiplicand elements.
     * @return the corresponding unit.
     */
    @SuppressWarnings("unchecked")
    private static Unit<? extends Quantity<?>> getInstance(Element[] leftElems,
            Element[] rightElems) {

        // Merges left elements with right elements.
        Element[] result = new Element[leftElems.length + rightElems.length];
        int resultIndex = 0;
        for (int i = 0; i < leftElems.length; i++) {
            Unit<?> unit = leftElems[i].unit;
            int p1 = leftElems[i].pow;
            int r1 = leftElems[i].root;
            int p2 = 0;
            int r2 = 1;
            for (int j = 0; j < rightElems.length; j++) {
                if (unit.equals(rightElems[j].unit)) {
                    p2 = rightElems[j].pow;
                    r2 = rightElems[j].root;
                    break; // No duplicate.
                }
            }
            int pow = (p1 * r2) + (p2 * r1);
            int root = r1 * r2;
            if (pow != 0) {
                int gcd = gcd(Math.abs(pow), root);
                result[resultIndex++] = new Element(unit, pow / gcd, root / gcd);
            }
        }

        // Appends remaining right elements not merged.
        for (int i = 0; i < rightElems.length; i++) {
            Unit<?> unit = rightElems[i].unit;
            boolean hasBeenMerged = false;
            for (int j = 0; j < leftElems.length; j++) {
                if (unit.equals(leftElems[j].unit)) {
                    hasBeenMerged = true;
                    break;
                }
            }
            if (!hasBeenMerged)
                result[resultIndex++] = rightElems[i];
        }

        // Returns or creates instance.
        if (resultIndex == 0)
            return ONE;
        else if ((resultIndex == 1) && (result[0].pow == result[0].root))
            return result[0].unit;
        else {
            Element[] elems = new Element[resultIndex];
            for (int i = 0; i < resultIndex; i++) {
                elems[i] = result[i];
            }
            return new ProductUnit(elems);
        }
    }

    /**
     * Returns the product of the specified units.
     *
     * @param left the left unit operand.
     * @param right the right unit operand.
     * @return <code>left * right</code>
     */
    public static Unit<? extends Quantity<?>> getProductInstance(Unit<?> left,
            Unit<?> right) {
        Element[] leftElems;
        if (left instanceof ProductUnit<?>)
            leftElems = ((ProductUnit<?>) left).elements;
        else
            leftElems = new Element[]{new Element(left, 1, 1)};
        Element[] rightElems;
        if (right instanceof ProductUnit<?>)
            rightElems = ((ProductUnit<?>) right).elements;
        else
            rightElems = new Element[]{new Element(right, 1, 1)};
        return getInstance(leftElems, rightElems);
    }

    /**
     * Returns the quotient of the specified units.
     *
     * @param left the dividend unit operand.
     * @param right the divisor unit operand.
     * @return <code>dividend / divisor</code>
     */
    public static Unit<? extends Quantity<?>> getQuotientInstance(Unit<?> left,
            Unit<?> right) {
        Element[] leftElems;
        if (left instanceof ProductUnit<?>)
            leftElems = ((ProductUnit<?>) left).elements;
        else
            leftElems = new Element[]{new Element(left, 1, 1)};
        Element[] rightElems;
        if (right instanceof ProductUnit<?>) {
            Element[] elems = ((ProductUnit<?>) right).elements;
            rightElems = new Element[elems.length];
            for (int i = 0; i < elems.length; i++) {
                rightElems[i] = new Element(elems[i].unit, -elems[i].pow,
                        elems[i].root);
            }
        } else
            rightElems = new Element[]{new Element(right, -1, 1)};
        return getInstance(leftElems, rightElems);
    }

    /**
     * Returns the product unit corresponding to the specified root of the
     * specified unit.
     *
     * @param unit the unit.
     * @param n the root's order (n &gt; 0).
     * @return <code>unit^(1/nn)</code>
     * @throws ArithmeticException if <code>n == 0</code>.
     */
    public static Unit<? extends Quantity<?>> getRootInstance(Unit<?> unit, int n) {
        Element[] unitElems;
        if (unit instanceof ProductUnit<?>) {
            Element[] elems = ((ProductUnit<?>) unit).elements;
            unitElems = new Element[elems.length];
            for (int i = 0; i < elems.length; i++) {
                int gcd = gcd(Math.abs(elems[i].pow), elems[i].root * n);
                unitElems[i] = new Element(elems[i].unit, elems[i].pow / gcd,
                        elems[i].root * n / gcd);
            }
        } else
            unitElems = new Element[]{new Element(unit, 1, n)};
        return getInstance(unitElems, new Element[0]);
    }

    /**
     * Returns the product unit corresponding to this unit raised to the
     * specified exponent.
     *
     * @param unit the unit.
     * @param nn the exponent (nn &gt; 0).
     * @return <code>unit^n</code>
     */
    static Unit<? extends Quantity<?>> getPowInstance(Unit<?> unit, int n) {
        Element[] unitElems;
        if (unit instanceof ProductUnit<?>) {
            Element[] elems = ((ProductUnit<?>) unit).elements;
            unitElems = new Element[elems.length];
            for (int i = 0; i < elems.length; i++) {
                int gcd = gcd(Math.abs(elems[i].pow * n), elems[i].root);
                unitElems[i] = new Element(elems[i].unit, elems[i].pow * n / gcd, elems[i].root / gcd);
            }
        } else
            unitElems = new Element[]{new Element(unit, n, 1)};
        return getInstance(unitElems, new Element[0]);
    }

    /**
     * Returns the number of unit elements in this product.
     *
     * @return the number of unit elements.
     */
    public int getUnitCount() {
        return elements.length;
    }

    /**
     * Returns the unit element at the specified position.
     *
     * @param index the index of the unit element to return.
     * @return the unit element at the specified position.
     * @throws IndexOutOfBoundsException if index is out of range
     *         <code>(index &lt; 0 || index &gt;= getUnitCount())</code>.
     */
    public Unit<? extends Quantity<?>> getUnit(int index) {
        return elements[index].getUnit();
    }

    /**
     * Returns the power exponent of the unit element at the specified position.
     *
     * @param index the index of the unit element.
     * @return the unit power exponent at the specified position.
     * @throws IndexOutOfBoundsException if index is out of range
     *         <code>(index &lt; 0 || index &gt;= getUnitCount())</code>.
     */
    public int getUnitPow(int index) {
        return elements[index].getPow();
    }

    /**
     * Returns the root exponent of the unit element at the specified position.
     *
     * @param index the index of the unit element.
     * @return the unit root exponent at the specified position.
     * @throws IndexOutOfBoundsException if index is out of range
     *         <code>(index &lt; 0 || index &gt;= getUnitCount())</code>.
     */
    public int getUnitRoot(int index) {
        return elements[index].getRoot();
    }

    @Override
    public Map<Unit<?>, Integer> getProductUnits() {
        HashMap<Unit<?>, Integer> units = new HashMap<Unit<?>, Integer>();
        for (int i = 0; i < getUnitCount(); i++) {
            units.put(getUnit(i), getUnitPow(i));
        }
        return units;
    }

    @Override
    public boolean equals(Object that) {
        if (this == that)
            return true;
        if (!(that instanceof ProductUnit<?>))
            return false;
        // Two products are equals if they have the same elements
        // regardless of the elements' order.
        Element[] elems = ((ProductUnit<?>) that).elements;
        if (elements.length != elems.length)
            return false;
        for (int i = 0; i < elements.length; i++) {
            boolean unitFound = false;
            Element e = elements[i];
            for (int j = 0; j < elems.length; j++) {
                if (e.unit.equals(elems[j].unit))
                    if ((e.pow != elems[j].pow) || (e.root != elems[j].root))
                        return false;
                    else {
                        unitFound = true;
                        break;
                    }
            }
            if (!unitFound)
                return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        if (this.hashCode != 0)
            return this.hashCode;
        int code = 0;
        for (int i = 0; i < elements.length; i++) {
            code += elements[i].unit.hashCode() * (elements[i].pow * 3 - elements[i].root * 2);
        }
        this.hashCode = code;
        return code;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Unit<Q> toMetric() {
        if (hasOnlyUnscaledMetricUnits())
            return this;
        Unit<?> systemUnit = ONE;
        for (int i = 0; i < elements.length; i++) {
            Unit<?> unit = elements[i].unit.toMetric();
            unit = unit.pow(elements[i].pow);
            unit = unit.root(elements[i].root);
            systemUnit = systemUnit.multiply(unit);
        }
        return (Unit<Q>) systemUnit;
    }

    @Override
    public final UnitConverter getConverterToMetric() {
        if (hasOnlyUnscaledMetricUnits()) // Product of standard units is a standard unit itself.
            return UnitConverter.IDENTITY;
        UnitConverter converter = UnitConverter.IDENTITY;
        for (int i = 0; i < elements.length; i++) {
            Element e = elements[i];
            UnitConverter cvtr = e.unit.getConverterToMetric();
            if (!(cvtr.isLinear()))
                throw new UnsupportedOperationException(e.unit + " is non-linear, cannot convert");
            if (e.root != 1)
                throw new UnsupportedOperationException(e.unit + " holds a base unit with fractional exponent");
            int pow = e.pow;
            if (pow < 0) { // Negative power.
                pow = -pow;
                cvtr = cvtr.inverse();
            }
            for (int j = 0; j < pow; j++) {
                converter = converter.concatenate(cvtr);
            }
        }
        return converter;
    }

    /**
     * Indicates if this product unit is a standard unit.
     *
     * @return <code>true</code> if all elements are standard units;
     *         <code>false</code> otherwise.
     */
    private boolean hasOnlyUnscaledMetricUnits() {
        for (int i = 0; i < elements.length; i++) {
            Unit<?> u = elements[i].unit;
            if (!u.isUnscaledMetric())
                return false;
        }
        return true;
    }

    @Override
    public Dimension getDimension() {
        Dimension dimension = Dimension.NONE;
        for (int i = 0; i < this.getUnitCount(); i++) {
            Unit<?> unit = this.getUnit(i);
            Dimension d = unit.getDimension().pow(this.getUnitPow(i)).root(this.getUnitRoot(i));
            dimension = dimension.multiply(d);
        }
        return dimension;
    }

    @Override
    public UnitConverter getDimensionalTransform() {
        UnitConverter converter = UnitConverter.IDENTITY;
        for (int i = 0; i < this.getUnitCount(); i++) {
            Unit<?> unit = this.getUnit(i);
            UnitConverter cvtr = unit.getDimensionalTransform();
            if (!(cvtr.isLinear()))
                throw new UnsupportedOperationException(cvtr.getClass() + " is non-linear, cannot convert product unit");
            if (this.getUnitRoot(i) != 1)
                throw new UnsupportedOperationException(this + " holds a unit with fractional exponent");
            int pow = this.getUnitPow(i);
            if (pow < 0) { // Negative power.
                pow = -pow;
                cvtr = cvtr.inverse();
            }
            for (int j = 0; j < pow; j++) {
                converter = converter.concatenate(cvtr);
            }
        }
        return converter;
    }

    /**
     * Returns the greatest common divisor (Euclid's algorithm).
     *
     * @param m the first number.
     * @param nn the second number.
     * @return the greatest common divisor.
     */
    private static int gcd(int m, int n) {
        if (n == 0)
            return m;
        else
            return gcd(n, m % n);
    }

    /**
     * Inner product element represents a rational power of a single unit.
     */
    private final static class Element implements Serializable {

        /**
         *
         */
        private static final long serialVersionUID = 1649532173171667701L;

        /**
         * Holds the single unit.
         */
        private final Unit<?> unit;

        /**
         * Holds the power exponent.
         */
        private final int pow;

        /**
         * Holds the root exponent.
         */
        private final int root;

        /**
         * Structural constructor.
         *
         * @param unit the unit.
         * @param pow the power exponent.
         * @param root the root exponent.
         */
        private Element(Unit<?> unit, int pow, int root) {
            this.unit = unit;
            this.pow = pow;
            this.root = root;
        }

        /**
         * Returns this element's unit.
         *
         * @return the single unit.
         */
        public Unit<?> getUnit() {
            return unit;
        }

        /**
         * Returns the power exponent. The power exponent can be negative but is
         * always different from zero.
         *
         * @return the power exponent of the single unit.
         */
        public int getPow() {
            return pow;
        }

        /**
         * Returns the root exponent. The root exponent is always greater than
         * zero.
         *
         * @return the root exponent of the single unit.
         */
        public int getRoot() {
            return root;
        }
    }
}

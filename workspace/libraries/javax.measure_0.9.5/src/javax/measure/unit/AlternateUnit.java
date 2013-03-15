/**
 *   Copyright (c) 2005-2010, Jean-Marie Dautelle, Werner Keil
 *   All rights reserved.
 *
 *   See LICENSE.txt for the Specification License
 */
package javax.measure.unit;

import javax.measure.quantity.Quantity;

/**
 * <p> This class represents metric units used in expressions to distinguish
 *     between quantities of a different nature but of the same dimensions.
 *     Alternate units are always unscaled metric units.</p>
 *
 * <p> Instances of this class are created through the
 *     {@link Unit#alternate(String)} method.</p>
 *
 * @param <Q> The type of the quantity measured by this unit.
 *
 * @author  <a href="mailto:jean-marie@dautelle.com">Jean-Marie Dautelle</a>
 * @version 1.1, $Date: 2010-02-24 18:40:34 +0100 (Mi, 24 Feb 2010) $
 */
public class AlternateUnit<Q extends Quantity<Q>> extends Unit<Q> {
 
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * Holds the parent unit (a system unit).
     */
    private final Unit<?> parent;

    /**
     * Holds the parent unit (a system unit).
     */
    private final String symbol;

    /**
     * Creates an alternate unit for the specified unit identified by the
     * specified name and symbol.
     *
     * @param symbol the symbol for this alternate unit.
     * @param parent the system unit from which this alternate unit is
     *        derived.
     * @throws UnsupportedOperationException if the parent is not
     *         an unscaled metric unit.
     * @throws IllegalArgumentException if the specified symbol is
     *         associated to a different unit.
     */
    public AlternateUnit(String symbol, Unit<?> parent) {
        if (!parent.isUnscaledMetric())
            throw new UnsupportedOperationException(parent + " is not an unscaled metric unit");
        this.parent = parent;
        this.symbol = symbol;
        // Checks if the symbol is associated to a different unit.
        synchronized (Unit.SYMBOL_TO_UNIT) {
            Unit<?> unit = Unit.SYMBOL_TO_UNIT.get(symbol);
            if (unit == null) {
                Unit.SYMBOL_TO_UNIT.put(symbol, this);
                return;
            }
            if (unit instanceof AlternateUnit<?>) {
                AlternateUnit<?> existingUnit = (AlternateUnit<?>) unit;
                if (symbol.equals(existingUnit.getSymbol()) && this.parent.equals(existingUnit.parent))
                    return; // OK, same unit.
            }
            throw new IllegalArgumentException("Symbol " + symbol + " is associated to a different unit");
        }
    }

    @Override
    public String getSymbol() {
        return symbol;
    }

    @Override
    public final Unit<Q> toMetric() {
        return this;
    }

    @Override
    public final UnitConverter getConverterToMetric() {
        return UnitConverter.IDENTITY;
    }

    @Override
    public boolean equals(Object that) {
        if (this == that)
            return true;
        if (!(that instanceof AlternateUnit<?>))
            return false;
        AlternateUnit<?> thatUnit = (AlternateUnit<?>) that;
        return this.symbol.equals(thatUnit.symbol); // Symbols are unique.
    }

    @Override
    public Dimension getDimension() {
        return parent.getDimension();
    }

    @Override
    public UnitConverter getDimensionalTransform() {
        return parent.getDimensionalTransform();
    }

    @Override
    public int hashCode() {
        return symbol.hashCode();
    }
}

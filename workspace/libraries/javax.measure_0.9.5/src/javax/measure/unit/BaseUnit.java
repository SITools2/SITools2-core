/**
 *   Copyright (c) 2005-2010, Jean-Marie Dautelle, Werner Keil
 *   All rights reserved.
 *
 *   See LICENSE.txt for the Specification License
 */
package javax.measure.unit;

import javax.measure.quantity.Quantity;

/**
 * <p> This class represents the building blocks on top of which all others
 *     units are created. Base units are always unscaled metric units.</p>
 * 
 * <p> When using the {@linkplain Dimension.Model#STANDARD standard} model
 *     (default), all seven base units are dimensionally independent.</p>
 *
 * @param <Q> The type of the quantity measured by this unit.
 *
 * @author  <a href="mailto:jean-marie@dautelle.com">Jean-Marie Dautelle</a>
 * @author  <a href="mailto:jsr275@catmedia.us">Werner Keil</a>
 * @version 1.8 ($Revision: 195 $), $Date: 2010-02-24 18:40:34 +0100 (Mi, 24 Feb 2010) $
 * @see <a href="http://en.wikipedia.org/wiki/SI_base_unit">
 *       Wikipedia: SI base unit</a>
 */
public class BaseUnit<Q extends Quantity<Q>> extends Unit<Q> {

    /**
     * Holds the symbol.
     */
    private transient final String symbol;

    /** The serialVersionUID */
    private static final long serialVersionUID = 1234567654321265167L;

    /**
     * Constructor from other unit (copy constructor)
     * @param u the unit
     */
    public BaseUnit(Unit<?> u) {
    	this.symbol = u.getSymbol();
    }
    
    /**
     * Creates a base unit having the specified symbol.
     *
     * @param symbol the symbol of this base unit.
     * @throws IllegalArgumentException if the specified symbol is
     *         associated to a different unit.
     */
    public BaseUnit(String symbol) {
        this.symbol = symbol;
        // Checks if the symbol is associated to a different unit.
        synchronized (Unit.SYMBOL_TO_UNIT) {
            Unit<?> unit = Unit.SYMBOL_TO_UNIT.get(symbol);
            if (unit == null) {
                Unit.SYMBOL_TO_UNIT.put(symbol, this);
                return;
            }
            if (!(unit instanceof BaseUnit<?>))
                throw new IllegalArgumentException("Symbol " + symbol + " is associated to a different unit");
        }
    }

    @Override
    public String getSymbol() {
        return symbol;
    }

    @Override
    public boolean equals(Object that) {
        if (this == that)
            return true;
        if (!(that instanceof BaseUnit<?>))
            return false;
        BaseUnit<?> thatUnit = (BaseUnit<?>) that;
        return this.symbol.equals(thatUnit.symbol);
    }

    @Override
    public int hashCode() {
        return symbol.hashCode();
    }

    @Override
    public Unit<Q> toMetric() {
        return this;
    }

    @Override
    public UnitConverter getConverterToMetric() {
        return UnitConverter.IDENTITY;
    }

    @Override
    public Dimension getDimension() {
        return Dimension.getModel().getDimension(this);
    }

    @Override
    public UnitConverter getDimensionalTransform() {
        return Dimension.getModel().getTransform(this);
    }
}

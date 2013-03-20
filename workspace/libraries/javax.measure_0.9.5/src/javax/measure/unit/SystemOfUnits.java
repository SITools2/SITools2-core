/**
 *   Copyright (c) 2005-2010, Jean-Marie Dautelle, Werner Keil
 *   All rights reserved.
 *
 *   See LICENSE.txt for the Specification License
 */
package javax.measure.unit;

import java.util.HashSet;
import java.util.Set;

/**
 * <p> This class represents a system of units, it groups units together
 *     for historical or cultural reasons. Nothing prevents a unit from
 *     belonging to several system of units at the same time
 *     (for example an imperial system would have many of the units
 *     held by {@link USCustomarySystem}).</p>
 *
 * @author  <a href="mailto:jean-marie@dautelle.com">Jean-Marie Dautelle</a>
 * @author  <a href="mailto:jsr275@catmedia.us">Werner Keil</a>
 * @version 1.1 ($Revision: 169 $), $Date: 2010-02-21 18:48:40 +0100 (So, 21 Feb 2010) $
 */
public abstract class SystemOfUnits {

    /**
     * Returns a read only view over the units defined in this system.
     *
     * @return the collection of units.
     */
    public abstract Set<Unit<?>> getUnits();


    /**
     * Returns the units defined in this system having the specified
     * dimension (convenience method). This method returns all the units
     * in this system of units having the specified dimension.
     *
     * @param dimension the dimension of the units to be returned.
     * @return the collection of units of specified dimension.
     */
    public Set<Unit<?>> getUnits(Dimension dimension) {
         final Set<Unit<?>> units = new HashSet<Unit<?>>();
         for (Unit<?> unit : getUnits()) {
             if (unit.getDimension().equals(dimension)) {
                 units.add(unit);
             }
         }
        return units;
    }
}

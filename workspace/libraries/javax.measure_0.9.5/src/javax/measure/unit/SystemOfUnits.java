 /*******************************************************************************
 * Copyright 2010-2013 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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

/*******************************************************************************
 * Copyright 2011, 2012 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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
package fr.cnes.sitools.units.astronomy;

import javax.measure.quantity.Quantity;
import javax.measure.unit.TransformedUnit;
import javax.measure.unit.Unit;
import javax.measure.unit.UnitConverter;
import javax.measure.unit.format.LocalFormat;

/**
 * Class to define a unit used in astronomy
 * @author m.marseille
 * @param <Q>
 */
public class AstronomicUnit<Q extends Quantity<Q>> extends TransformedUnit<Q>  {
  
  /**
   * Long for serialization
   */
  private static final long serialVersionUID = 1L;
  
  /**
   * Holds the symbol.
   */
  private final transient String symbol;

  /**
   * Constructor with symbol
   * @param symbol the symbol used for the unit
   * @param parent the base unit definition
   * @param converter the converter to the parent
   */
  public AstronomicUnit(String symbol, Unit<Q> parent, UnitConverter converter) {
    super(parent, converter);
    this.symbol = symbol;
    LocalFormat.getInstance().getSymbolMap().label(this, symbol);
  }

  /**
   * Get the symbol
   * @return the symbol
   */
  public final String getSymbol() {
    return symbol;
  }

}

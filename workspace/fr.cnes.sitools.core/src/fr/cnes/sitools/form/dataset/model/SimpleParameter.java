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
package fr.cnes.sitools.form.dataset.model;

import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import fr.cnes.sitools.form.model.AbstractParameter;
import fr.cnes.sitools.form.model.Value;

/**
 * Single selection
 * 
 * @author BAILLAGOU
 * @version 1.0 01-09-2010 08:59:03
 */
@XStreamAlias("SimpleParameter")
public class SimpleParameter extends AbstractParameter {

  /**
   * Type of single selection
   */
  private String type;

  /**
   * List of values in the selection
   */
  private List<Value> values;

  /**
   * Constructor
   */
  public SimpleParameter() {
    super();
  }

  /**
   * Get the type of the selection
   * 
   * @return the type
   */
  public final String getType() {
    return type;
  }

  /**
   * Set the type of the selection
   * 
   * @param type
   *          the type to set
   */
  public final void setType(final String type) {
    this.type = type;
  }

  /**
   * Get the values associated
   * 
   * @return the values
   */
  public final List<Value> getValues() {
    return values;
  }

  /**
   * Set the values associated
   * 
   * @param values
   *          the values to set
   */
  public final void setValues(List<Value> values) {
    this.values = values;
  }

}

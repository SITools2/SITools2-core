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
package fr.cnes.sitools.common.validator;

import java.util.Set;

import org.restlet.Context;

/**
 * Validate a <T> object
 * 
 * @param <T>
 *          the type of object to validate
 * 
 * @author m.gond
 */
public abstract class Validator<T extends Validable> {
  /** The Context */
  private Context context;
  
  /**
   * Validate a given <T> Object
   * 
   * @param item
   *          the object to validate
   * @return a Set of ContraintViolation representing the list of violation on the object
   */
  public abstract Set<ConstraintViolation> validate(T item);

  /**
   * Sets the value of context
   * 
   * @param context
   *          the context to set
   */
  public void setContext(Context context) {
    this.context = context;
  }

  /**
   * Gets the context value
   * 
   * @return the context
   */
  public Context getContext() {
    return context;
  }

}

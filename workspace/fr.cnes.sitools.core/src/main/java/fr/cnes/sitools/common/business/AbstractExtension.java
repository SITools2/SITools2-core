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
package fr.cnes.sitools.common.business;

import org.restlet.Context;

import fr.cnes.sitools.common.model.ExtensionModel;
import fr.cnes.sitools.common.model.ExtensionParameter;

/**
 * Base class for abstract base class for extensions
 * 
 * @param <E>
 *          the class of parameters
 * @author m.marseille (AKKA Technologies)
 */
public abstract class AbstractExtension<E extends ExtensionParameter> extends ExtensionModel<E> {

  /**
   * converter context
   */
  private Context context;

  /**
   * Constructor
   */
  public AbstractExtension() {
    super();
  }

  /**
   * Constructor with Context parameter
   * 
   * @param ctx
   *          The Context to instantiate the Converter with, contains the DataSet
   */
  public AbstractExtension(Context ctx) {
    super();
  }

  /**
   * Sets the value of context
   * 
   * @param context
   *          the context to set
   */
  public final void setContext(Context context) {
    this.context = context;
  }

  /**
   * Gets the context value
   * 
   * @return the context
   */
  public final Context getContext() {
    return context;
  }

}

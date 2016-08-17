    /*******************************************************************************
 * Copyright 2010-2016 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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
package fr.cnes.sitools.inscription;

import org.restlet.Context;
import org.restlet.representation.Representation;

import fr.cnes.sitools.common.application.ContextAttributes;
import fr.cnes.sitools.common.application.SitoolsApplication;
import fr.cnes.sitools.common.store.SitoolsStore;
import fr.cnes.sitools.inscription.model.Inscription;

/**
 * Base class for Inscription management
 * 
 * @author jp.boignard (AKKA Technologies)
 * 
 */
public abstract class AbstractInscriptionApplication extends SitoolsApplication {

  /** Store */
  private InscriptionStoreInterface store = null;

  /**
   * Constructor with context
   * 
   * @param context
   *          Restlet host context
   */
  @SuppressWarnings("unchecked")
  public AbstractInscriptionApplication(Context context) {
    super(context);
    this.store = (InscriptionStoreInterface) context.getAttributes().get(ContextAttributes.APP_STORE);
  }

  /**
   * Constructor with context and wadl configuration
   * 
   * @param arg0
   *          Context
   * @param arg1
   *          wadl configuration
   */
  public AbstractInscriptionApplication(Context arg0, Representation arg1) {
    super(arg0, arg1);
  }

  /**
   * Gets the store
   * 
   * @return InscriptionStore
   */
  public final InscriptionStoreInterface getStore() {
    return store;
  }

}

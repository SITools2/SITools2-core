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
package fr.cnes.sitools.inscription;

import org.restlet.data.MediaType;
import org.restlet.representation.Variant;

import com.thoughtworks.xstream.XStream;

import fr.cnes.sitools.common.SitoolsResource;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.inscription.model.Inscription;

/**
 * Class Resource for managing single Inscription object (GET, PUT, DELETE)
 * 
 * @author jp.boignard (AKKA Technologies)
 */
public abstract class InscriptionResource extends SitoolsResource {

  /** store */
  private InscriptionStoreInterface store = null;

  /** inscription id request parameter */
  private String inscriptionId = null;
  
  /** parent application */
  private AbstractInscriptionApplication application = null;

  @Override
  public final void doInit() {
    super.doInit();

    // Declares the two variants supported
    addVariant(new Variant(MediaType.APPLICATION_XML));
    addVariant(new Variant(MediaType.APPLICATION_JSON));
    addVariant(new Variant(MediaType.APPLICATION_JAVA_OBJECT));

    application = (AbstractInscriptionApplication) getApplication();
    store = application.getStore();

    inscriptionId = (String) this.getRequest().getAttributes().get("inscriptionId");
  }

  /**
   * Configure XStream for object serialization
   * 
   * @param xstream
   *          XStream serializer
   * @param response
   *          Response object
   */
  public final void configure(XStream xstream, Response response) {
    if (response == null) {
      return;
    }
    super.configure(xstream, response);
    xstream.alias("inscription", Inscription.class);
    xstream.omitField(Inscription.class, "name");
    xstream.omitField(Inscription.class, "description");
  }
  
  /**
   * Return the application
   * @return the application
   */
  public final AbstractInscriptionApplication getInscriptionApplication() {
    return this.application;
  }

  /**
   * Gets the store value
   * @return the store
   */
  public final InscriptionStoreInterface getStore() {
    return store;
  }

  /**
   * Gets the inscriptionId value
   * @return the inscriptionId
   */
  public final String getInscriptionId() {
    return inscriptionId;
  }
  
  

}

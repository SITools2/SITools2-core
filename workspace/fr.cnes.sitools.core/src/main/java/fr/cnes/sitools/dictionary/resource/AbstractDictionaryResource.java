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
package fr.cnes.sitools.dictionary.resource;

import java.io.IOException;
import java.util.Map;

import fr.cnes.sitools.dictionary.DictionaryAdministration;
import fr.cnes.sitools.dictionary.store.DictionaryStoreInterface;
import org.restlet.data.MediaType;
import org.restlet.ext.jackson.JacksonRepresentation;
import org.restlet.ext.xstream.XstreamRepresentation;
import org.restlet.representation.ObjectRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;

import com.thoughtworks.xstream.XStream;

import fr.cnes.sitools.common.SitoolsResource;
import fr.cnes.sitools.common.XStreamFactory;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.dictionary.model.Dictionary;

/**
 * Base resource for dictionary management
 * 
 * @author jp.boignard (AKKA Technologies)
 * 
 */
public abstract class AbstractDictionaryResource extends SitoolsResource {

  /** Store */
  private DictionaryStoreInterface store = null;

  @Override
  public void doInit() {
    super.doInit();
    store = ((DictionaryAdministration) getApplication()).getStore();
  }

  /**
   * Gets the store value
   * 
   * @return the store
   */
  public final DictionaryStoreInterface getStore() {
    return store;
  }
}

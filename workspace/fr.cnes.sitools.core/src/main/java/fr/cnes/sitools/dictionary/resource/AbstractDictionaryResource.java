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

  /** Dictionary id in the request */
  private String dictionaryId = null;

  /** Concept id in the request */
  private String conceptId = null;

  @Override
  public void doInit() {
    super.doInit();

    // Declares the two variants supported
    addVariant(new Variant(MediaType.APPLICATION_XML));
    addVariant(new Variant(MediaType.APPLICATION_JSON));
    addVariant(new Variant(MediaType.APPLICATION_JAVA_OBJECT));

    store = ((DictionaryAdministration) getApplication()).getStore();

    Map<String, Object> requestAttributes = this.getRequestAttributes();
    if (requestAttributes != null) {
      dictionaryId = (String) requestAttributes.get("dictionaryId");
      conceptId = (String) requestAttributes.get("conceptId");
    }
  }

  /**
   * Decodes a representation to a Dictionary object.
   * 
   * @param representation
   *          Representation
   * @param variant
   *          Variant
   * @return Dictionary
   * @throws IOException
   *           if there is an error while parsing the serialize java object
   */
  public final Dictionary getObject(Representation representation, Variant variant) throws IOException {
    Dictionary dictionaryInput = null;
    if (representation.getMediaType().isCompatible(MediaType.APPLICATION_JAVA_OBJECT)) {
      @SuppressWarnings("unchecked")
      ObjectRepresentation<Dictionary> obj = (ObjectRepresentation<Dictionary>) representation;
      dictionaryInput = obj.getObject();
    }
    else if (MediaType.APPLICATION_XML.isCompatible(representation.getMediaType()) ||
             MediaType.APPLICATION_JSON.isCompatible(representation.getMediaType())) {
      // Parse the XML/JSON representation to get the bean
      dictionaryInput = new JacksonRepresentation<Dictionary>(representation, Dictionary.class).getObject();
    }

    return dictionaryInput;
  }

  /**
   * Encode a response into a Representation according to the given media type.
   * 
   * @param response
   *          Response
   * @param media
   *          Response
   * @return Representation
   */
  public Representation getRepresentation(Response response, MediaType media) {
    if (media.isCompatible(MediaType.APPLICATION_JAVA_OBJECT)) {
      return new ObjectRepresentation<Response>(response);
    }

    XStream xstream = XStreamFactory.getInstance().getXStream(media, getContext());
    configure(xstream, response);
    XstreamRepresentation<Response> rep = new XstreamRepresentation<Response>(media, response);
    rep.setXstream(xstream);
    return rep;
  }

  /**
   * Gets the conceptId value
   * 
   * @return the conceptId
   */
  public String getConceptId() {
    return conceptId;
  }

  /**
   * Gets the dictionaryId value
   * 
   * @return the dictionaryId
   */
  public final String getDictionaryId() {
    return dictionaryId;
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

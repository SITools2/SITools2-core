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
package fr.cnes.sitools.dictionary;

import java.io.IOException;

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
import fr.cnes.sitools.common.store.SitoolsStore;
import fr.cnes.sitools.dictionary.model.Concept;
import fr.cnes.sitools.dictionary.model.Dictionary;

/**
 * Base resource for dictionary management
 * 
 * @author jp.boignard (AKKA Technologies)
 * 
 */
public abstract class AbstractDictionaryResource extends SitoolsResource {

  /** Store */
  private SitoolsStore<Dictionary> store = null;

  /** Dictionary id in the request */
  private String dictionaryId = null;

  /** Notion id in the request */
  private String notionId = null;

  /** Concept id in the request */
  private String conceptId = null;

  @Override
  public void doInit() {
    super.doInit();

    // Declares the two variants supported
    getVariants().add(new Variant(MediaType.APPLICATION_XML));
    getVariants().add(new Variant(MediaType.APPLICATION_JSON));
    getVariants().add(new Variant(MediaType.APPLICATION_JAVA_OBJECT));

    store = ((DictionaryAdministration) getApplication()).getStore();

    dictionaryId = (String) this.getRequest().getAttributes().get("dictionaryId");
    notionId = (String) this.getRequest().getAttributes().get("notionId");
    conceptId = (String) this.getRequest().getAttributes().get("conceptId");
  }

  /**
   * Configure XStream mapping for xml and json serialization
   * 
   * TODO Optimisation possible au lieu de créer à chaque fois une instance de XStream conserver 4 instances d'XStream
   * correspondant aux 4 combinaisons possibles : classe retournée (Dictionary, Notion) et type (data / item)
   * 
   * @param xstream
   *          XStream
   * @param response
   *          Response
   */
  @Override
  public void configure(XStream xstream, Response response) {
    super.configure(xstream, response);
    xstream.alias("dictionary", Dictionary.class);
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
    else if (MediaType.APPLICATION_XML.isCompatible(representation.getMediaType())) {
      // Parse the XML representation to get the dictionary bean
      // Default parsing
      XstreamRepresentation<Dictionary> repXML = new XstreamRepresentation<Dictionary>(representation);
      XStream xstream = XStreamFactory.getInstance().getXStreamReader(MediaType.APPLICATION_XML);
      xstream.autodetectAnnotations(false);
      xstream.alias("dictionary", Dictionary.class);
      xstream.alias("concept", Concept.class);
      repXML.setXstream(xstream);
      dictionaryInput = repXML.getObject();
    }
    else if (MediaType.APPLICATION_JSON.isCompatible(representation.getMediaType())) {
      // Parse the JSON representation to get the bean
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
    getLogger().info(media.toString());
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
   * Gets the notionId value
   * 
   * @return the notionId
   */
  public final String getNotionId() {
    return notionId;
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
  public final SitoolsStore<Dictionary> getStore() {
    return store;
  }
}

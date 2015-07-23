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

import fr.cnes.sitools.dictionary.ConceptTemplateAdministration;
import fr.cnes.sitools.dictionary.Store.ConceptTemplateStoreInterface;
import org.restlet.data.MediaType;
import org.restlet.ext.jackson.JacksonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;

import fr.cnes.sitools.common.SitoolsResource;
import fr.cnes.sitools.dictionary.model.ConceptTemplate;

/**
 * Base resource for concept template management
 * 
 * @author c
 * 
 */
public abstract class AbstractConceptTemplateResource extends SitoolsResource {
  
  /** Store */
  private ConceptTemplateStoreInterface store = null;
  
  /** Template id in the request */
  private String templateId = null;
  
  /** Property id in the request */
  private String propertyId = null;

  @Override
  public void doInit() {
    super.doInit();

    // Declares the two variants supported
    getVariants().add(new Variant(MediaType.APPLICATION_XML));
    getVariants().add(new Variant(MediaType.APPLICATION_JSON));
    getVariants().add(new Variant(MediaType.APPLICATION_JAVA_OBJECT));

    store = ((ConceptTemplateAdministration) getApplication()).getStore();

    templateId = (String) this.getRequest().getAttributes().get("templateId");
    propertyId = (String) this.getRequest().getAttributes().get("propertyId");
  }

  /**
   * Decodes a representation to a ConceptTemplate object.
   * 
   * @param representation
   *          Representation
   * @param variant
   *          Variant
   * @return ConceptTemplate
   */
  public final ConceptTemplate getObject(Representation representation, Variant variant) {
    ConceptTemplate templateInput = null;
    if (MediaType.APPLICATION_XML.isCompatible(representation.getMediaType()) ||
            MediaType.APPLICATION_JSON.isCompatible(representation.getMediaType())) {
      // Parse the JSON representation to get the bean
      templateInput = new JacksonRepresentation<ConceptTemplate>(representation, ConceptTemplate.class).getObject();
    }

    return templateInput;
  }

  /**
   * Gets the propertyId value
   * @return the propertyId
   */
  public final String getPropertyId() {
    return propertyId;
  }

  /**
   * Gets the templateId value
   * @return the templateId
   */
  public final String getConceptTemplateId() {
    return templateId;
  }

  /**
   * Gets the store value
   * @return the store
   */
  public final ConceptTemplateStoreInterface getStore() {
    return store;
  }
}

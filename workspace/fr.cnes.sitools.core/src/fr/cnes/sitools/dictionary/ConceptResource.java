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
package fr.cnes.sitools.dictionary;

import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

import org.restlet.data.Status;
import org.restlet.ext.wadl.MethodInfo;
import org.restlet.ext.wadl.ParameterInfo;
import org.restlet.ext.wadl.ParameterStyle;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Get;
import org.restlet.resource.ResourceException;

import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.dictionary.model.Concept;
import fr.cnes.sitools.dictionary.model.Dictionary;

/**
 * Concept. parent object is fully returned with its table of items. No pagination service, to be managed on client
 * side.
 * 
 * @author m.gond (AKKA Technologies)
 * 
 */
public class ConceptResource extends AbstractDictionaryResource {

  @Override
  public void sitoolsDescribe() {
    setName("ConceptResource");
    setDescription("Resource for managing concepts in a dictionary");
  }

  /**
   * Gets the required Concept representation
   * 
   * @param variant
   *          client preference for Response media type
   * @return Representation
   */
  @Get
  public Representation getRepresentation(Variant variant) {
    try {
      Dictionary dictionary = getStore().retrieve(getDictionaryId());
      Concept conceptFound = null;

      Response response = null;
      if (dictionary == null) {
        response = new Response(false, "DICTIONARY_NOT_FOUND");
      }
      else {
        List<Concept> concepts = dictionary.getConcepts();
        for (Iterator<Concept> iterator = concepts.iterator(); iterator.hasNext();) {
          Concept concept = iterator.next();
          if (concept.getId().equals(getConceptId())) {
            conceptFound = concept;
            break;
          }
        }

        if (conceptFound == null) {
          response = new Response(false, "NOTION_NOT_FOUND");
        }
        else {
          response = new Response(true, conceptFound, Concept.class, "concept");

        }
      }
      return getRepresentation(response, variant);

    }
    catch (ResourceException e) {
      getLogger().log(Level.INFO, null, e);
      throw e;
    }
    catch (Exception e) {
      getLogger().log(Level.SEVERE, null, e);
      throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
    }
  }

  @Override
  protected void describeGet(MethodInfo info) {

    // -> Global method info
    info.setIdentifier("get_dictionary");
    info.setDocumentation("Get a single dictionary from its ID");

    this.addStandardGetRequestInfo(info);
    ParameterInfo param = new ParameterInfo("dictionaryId", true, "xs:string", ParameterStyle.TEMPLATE,
        "Identifier of the dictionary to work with.");
    info.getRequest().getParameters().add(param);
    ParameterInfo paramConcept = new ParameterInfo("conceptId", true, "xs:string", ParameterStyle.TEMPLATE,
        "Identifier of the concept to get.");
    info.getRequest().getParameters().add(param);
    info.getRequest().getParameters().add(paramConcept);
    this.addStandardResponseInfo(info);
    this.addStandardInternalServerErrorInfo(info);

  }

}

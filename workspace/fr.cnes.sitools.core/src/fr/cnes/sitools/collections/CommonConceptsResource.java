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
package fr.cnes.sitools.collections;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.restlet.ext.wadl.MethodInfo;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Get;

import fr.cnes.sitools.collections.model.Collection;
import fr.cnes.sitools.common.SitoolsSettings;
import fr.cnes.sitools.common.application.ContextAttributes;
import fr.cnes.sitools.common.model.Resource;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.dataset.model.ColumnConceptMapping;
import fr.cnes.sitools.dataset.model.DataSet;
import fr.cnes.sitools.dataset.model.DictionaryMapping;
import fr.cnes.sitools.dictionary.model.Concept;
import fr.cnes.sitools.dictionary.model.Dictionary;
import fr.cnes.sitools.server.Consts;
import fr.cnes.sitools.util.RIAPUtils;

/**
 * Resource which retrieve the list of common concepts for a Collection of DataSets for a particular Dictionary
 * 
 * 
 * @author m.gond
 */
public class CommonConceptsResource extends AbstractCollectionsResource {

  /** The id of the dictionary */
  private String dictionaryId;

  @Override
  public void sitoolsDescribe() {
    setName("CommonConceptsResource");
    setDescription("Retrieves the list of common concepts for a collection of datasets");
  }

  @Override
  public void doInit() {
    super.doInit();
    dictionaryId = (String) this.getRequest().getAttributes().get("dictionaryId");
  }

  /**
   * Retrieve common concepts
   * 
   * @param variant
   *          client preferred media type
   * @return Representation
   */
  @Get
  public Representation retrieveCommonConcepts(Variant variant) {
    Response response = null;
    if (dictionaryId != null) {
      Dictionary dictionary = getDictionary(dictionaryId);
      if (dictionary != null) {
        Collection collection = getStore().retrieve(getCollectionId());
        if (collection != null) {
          Set<String> commonConcepts = new HashSet<String>();
          boolean first = true;
          List<Resource> datasets = collection.getDataSets();
          if (datasets != null) {
            for (Resource datasetRes : datasets) {
              DataSet dataset = getDataset(datasetRes.getId());
              if (dataset != null) {
                DictionaryMapping dicoMapping = dataset.getDictionaryMapping(dictionaryId);
                if (dicoMapping != null) {
                  Set<String> datasetConcepts = new HashSet<String>();
                  List<ColumnConceptMapping> colConceptMapping = dicoMapping.getMapping();
                  for (ColumnConceptMapping columnConceptMapping : colConceptMapping) {
                    // On the first dataset, we fill the list with all the concepts
                    if (first) {
                      commonConcepts.add(columnConceptMapping.getConceptId());
                    }
                    // on the other datasets, we will another list, which will be compare with the common concept list
                    else {
                      datasetConcepts.add(columnConceptMapping.getConceptId());
                    }
                  }
                  if (!first) {
                    commonConcepts = mergeLists(commonConcepts, datasetConcepts);
                  }
                  first = false;
                }
              }
              // if the mapping is empty for a dataset, no concept are in common let's clear the list and leave the for
              else {
                commonConcepts.clear();
                break;
              }
            }
          }

          List<Concept> commonConceptsList = new ArrayList<Concept>();
          for (String conceptId : commonConcepts) {
            Concept concept = getDictionaryConcept(dictionary, conceptId);
            commonConceptsList.add(concept);
          }
          response = new Response(true, commonConceptsList, Concept.class);

        }
        else {
          response = new Response(false, "no collection found for the given id");
        }
      }
      else {
        response = new Response(false, "no dictionary found for the given id");
      }
    }
    else {
      response = new Response(false, "no dictionary given");
    }
    return getRepresentation(response, variant);

  }

  @Override
  public final void describeGet(MethodInfo info) {
    info.setDocumentation("Method to retrieve the list of common concepts for a collection of datasets");
    this.addStandardGetRequestInfo(info);
    this.addStandardResponseInfo(info);
    this.addStandardInternalServerErrorInfo(info);
  }

  /**
   * Create a new list with the entries contained in the two lists, all other entries are deleted
   * 
   * @param commonConcepts
   *          a list of String
   * @param datasetConcepts
   *          another list of String
   * @return a new List with all the common String
   */
  private Set<String> mergeLists(Set<String> commonConcepts, Set<String> datasetConcepts) {
    for (Iterator<String> iterator = commonConcepts.iterator(); iterator.hasNext();) {
      String conceptId = iterator.next();
      if (!datasetConcepts.contains(conceptId)) {
        iterator.remove();
      }
    }
    return commonConcepts;
  }

  /**
   * Get a Concept in a Dictionary from its id
   * 
   * @param dictionary
   *          the Dictionary
   * @param conceptId
   *          the id of the Concept
   * @return the Concept in the Dictionary corresponding to the given concept id
   */
  private Concept getDictionaryConcept(Dictionary dictionary, String conceptId) {
    Concept conceptReturn = null;
    List<Concept> dictionaryConcept = dictionary.getConcepts();
    for (Iterator<Concept> iterator = dictionaryConcept.iterator(); iterator.hasNext() && conceptReturn == null;) {
      Concept concept = iterator.next();
      if (concept.getId().equals(conceptId)) {
        conceptReturn = concept;
      }
    }
    return conceptReturn;
  }

  /**
   * Get Dictionary object from its id
   * 
   * @param id
   *          the id of the Dictionary
   * @return a Dictionary object
   */
  private Dictionary getDictionary(String id) {
    SitoolsSettings settings = (SitoolsSettings) getContext().getAttributes().get(ContextAttributes.SETTINGS);
    return RIAPUtils.getObject(id, settings.getString(Consts.APP_DICTIONARIES_URL), getContext());
  }
}

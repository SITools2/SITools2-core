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
package fr.cnes.sitools.dataset;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.restlet.data.Status;
import org.restlet.ext.wadl.MethodInfo;
import org.restlet.representation.ObjectRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.representation.Variant;
import org.restlet.resource.Put;
import org.restlet.resource.ResourceException;

import fr.cnes.sitools.dataset.model.ColumnConceptMapping;
import fr.cnes.sitools.dataset.model.DataSet;
import fr.cnes.sitools.dataset.model.DictionaryMapping;
import fr.cnes.sitools.dictionary.model.Concept;
import fr.cnes.sitools.dictionary.model.Dictionary;
import fr.cnes.sitools.notification.model.Notification;

/**
 * Resource to handle Notifications calls on Dataset. Use to update DictionaryMappings
 * 
 * 
 * @author m.gond (Akka Technologies)
 */
public class DataSetNotificationResource extends AbstractDataSetResource {

  @Override
  public void sitoolsDescribe() {
    setName("DataSetNotificationResource");
    setDescription("Manage notification of dataset resources updating");
    setNegotiated(true);
  }

  /**
   * Update / Validate existing project
   * 
   * @param representation
   *          Project representation
   * @param variant
   *          client preferred media type
   * @return Representation
   */
  @Put
  public Representation notification(Representation representation, Variant variant) {
    try {
      Notification notification = null;
      if (representation != null) {
        notification = getObjectNotification(representation);
      }
      // if the dictionary is deleted
      if ((notification != null) && "DICTIONARY_DELETED".equals(notification.getEvent())) {
        // Business service
        DataSet dataset = store.retrieve(datasetId);
        boolean updated = false;
        Dictionary dicoDeleted = (Dictionary) notification.getEventSource();
        List<DictionaryMapping> mappings = dataset.getDictionaryMappings();
        for (Iterator<DictionaryMapping> iterator = mappings.iterator(); iterator.hasNext() && !updated;) {
          DictionaryMapping mapping = iterator.next();
          if (mapping.getDictionaryId().equals(dicoDeleted.getId())) {
            iterator.remove();
            updated = true;
          }
        }
        if (updated) {
          store.update(dataset);
          return new StringRepresentation("OK");
        }
        return new StringRepresentation("DEPRECATED");
      }

      // if the dictionary is updated
      if ((notification != null) && "DICTIONARY_UPDATED".equals(notification.getEvent())) {
        @SuppressWarnings("unchecked")
        Map<String, Dictionary> map = (HashMap<String, Dictionary>) notification.getEventSource();
        List<Concept> conceptsDeleted = getConceptsDeleted(map.get("oldDictionary"), map.get("newDictionary"));
        boolean updated = false;
        // if there are some concepts deleted let's continue
        if (!conceptsDeleted.isEmpty()) {
          DataSet dataset = store.retrieve(datasetId);
          // get the DictionaryMapping corresponding to the oldDictionary
          DictionaryMapping mapping = dataset.getDictionaryMapping(map.get("oldDictionary").getId());
          if (mapping.getMapping() != null) {
            // loop through the deleted concepts
            for (Iterator<Concept> itConcDel = conceptsDeleted.iterator(); itConcDel.hasNext();) {
              Concept concept = (Concept) itConcDel.next();
              // loop through the ColumnConceptsMapping
              for (Iterator<ColumnConceptMapping> itConceptsMapping = mapping.getMapping().iterator(); itConceptsMapping
                  .hasNext();) {
                ColumnConceptMapping colConcMapping = itConceptsMapping.next();
                String conceptId = colConcMapping.getConceptId();
                if (conceptId.equals(concept.getId())) {
                  itConceptsMapping.remove();
                  updated = true;
                }
              }
            }
          }
          if (updated) {
            store.update(dataset);
            return new StringRepresentation("OK");
          }
          return new StringRepresentation("DEPRECATED");
        }
        return new StringRepresentation("DEPRECATED");
      }

      if ((notification != null) && "GUI_SERVICE_PLUGIN_ADDED".equals(notification.getEvent())) {
        System.out.println("##################################### COOL");
        return new StringRepresentation("OK");
      }
      else {
        // Others status
        return new StringRepresentation("OK");
      }
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
  public void describePut(MethodInfo info) {
    info.setDocumentation("Method to handle notification from observed objects.");
    this.addStandardNotificationInfo(info);
  }

  /**
   * Get Notification object
   * 
   * @param representation
   *          the representation to use
   * 
   * @return Notification
   */
  public Notification getObjectNotification(Representation representation) {
    try {
      ObjectRepresentation<Notification> or;
      try {
        or = new ObjectRepresentation<Notification>(representation);
        return or.getObject();
      }
      catch (IllegalArgumentException e) {
        throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
      }
      catch (ClassNotFoundException e) {
        throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
      }

    }
    catch (IOException e) {
      getLogger().log(Level.WARNING, "Bad representation of project resource updating notification", e);
      throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
    }
  }

  /**
   * Get the concepts deleted between old and new dictionary
   * 
   * @param oldDictionary
   *          the old dictionary
   * @param newDictionary
   *          the new dictionary
   * @return ArrayList<Concept> : concepts deleted between old and new dictionary
   */
  private ArrayList<Concept> getConceptsDeleted(Dictionary oldDictionary, Dictionary newDictionary) {
    // Dictionary dico = (Dictionary) notification.getEventSource();
    ArrayList<Concept> conceptsDeleted = new ArrayList<Concept>();

    for (Concept conceptOldDico : oldDictionary.getConcepts()) {
      Boolean found = false;
      for (Concept conceptNewDico : newDictionary.getConcepts()) {
        if (conceptOldDico.getId() != null && conceptOldDico.getId().equals(conceptNewDico.getId())) {
          found = true;
          break;
        }
      }
      if (!found) {
        conceptsDeleted.add(conceptOldDico);
      }
    }
    return conceptsDeleted;

  }

}

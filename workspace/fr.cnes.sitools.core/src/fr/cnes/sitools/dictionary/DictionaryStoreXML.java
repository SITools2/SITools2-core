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
package fr.cnes.sitools.dictionary;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.restlet.Context;

import fr.cnes.sitools.common.store.SitoolsStoreXML;
import fr.cnes.sitools.dictionary.model.Concept;
import fr.cnes.sitools.dictionary.model.Dictionary;

/**
 * Implementation of DictionaryStore with XStream FilePersistenceStrategy
 * 
 * @author AKKA
 * 
 */
@Deprecated
public final class DictionaryStoreXML extends SitoolsStoreXML<Dictionary> {

  /** default location for file persistence */
  private static final String COLLECTION_NAME = "dictionaries";

  /**
   * Constructor with the XML file location
   * 
   * @param location
   *          directory for file persistence
   * @param context
   *          the Restlet Context
   */
  public DictionaryStoreXML(File location, Context context) {
    super(Dictionary.class, location, context);
  }

  /**
   * Default Constructor
   * 
   * @param context
   *          the Restlet Context
   */
  public DictionaryStoreXML(Context context) {
    super(Dictionary.class, context);
    File defaultLocation = new File(COLLECTION_NAME);
    init(defaultLocation);
  }

  /**
   * XStream FilePersistenceStrategy initialization
   * 
   * @param location
   *          Directory
   */
  public void init(File location) {
    Map<String, Class<?>> aliases = new ConcurrentHashMap<String, Class<?>>();
    aliases.put("dictionary", Dictionary.class);
    this.init(location, aliases);
  }

  @Override
  public Dictionary update(Dictionary dictionary) {
    Dictionary result = null;
    for (Iterator<Dictionary> it = getRawList().iterator(); it.hasNext();) {
      Dictionary current = it.next();
      if (current.getId().equals(dictionary.getId())) {
        getLog().info("Updating dictionary");
        result = current;
        current.setName(dictionary.getName());
        current.setDescription(dictionary.getDescription());
        current.setConceptTemplate(dictionary.getConceptTemplate());
        current.setConcepts(dictionary.getConcepts());
        // loop through the concepts and assign an id for each
        int i = 0;
        if (current.getConcepts() != null) {
          for (Iterator<Concept> iterator = current.getConcepts().iterator(); iterator.hasNext();) {
            Concept concept = iterator.next();
            concept.setId(new Integer(i++).toString());
          }
        }
        it.remove();

        break;
      }
    }
    if (result != null) {
      getRawList().add(result);
    }
    else {
      getLog().info("Dictionary not found.");
    }

    return result;
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.cnes.sitools.common.store.SitoolsStoreXML#create(fr.cnes.sitools.common.model.IResource)
   */
  @Override
  public Dictionary create(Dictionary resource) {
    if (resource.getConcepts() != null) {
      // loop through the concepts and assign an id for each
      int i = 0;
      for (Iterator<Concept> iterator = resource.getConcepts().iterator(); iterator.hasNext();) {
        Concept concept = iterator.next();
        concept.setId(new Integer(i++).toString());
      }
    }
    return super.create(resource);
  }

  @Override
  public List<Dictionary> retrieveByParent(String id) {
    // A dictionary has no parent
    return null;
  }

  @Override
  public String getCollectionName() {
    return "dictionary";
  }

}

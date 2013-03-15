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

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.restlet.Context;

import fr.cnes.sitools.common.store.SitoolsStoreXML;
import fr.cnes.sitools.dictionary.model.ConceptTemplate;

/**
 * Implementation of ConceptTemplateStore with XStream FilePersistenceStrategy
 * 
 * @author AKKA
 * 
 */
public final class ConceptTemplateStoreXML extends SitoolsStoreXML<ConceptTemplate> {

  /** default location for file persistence */
  private static final String COLLECTION_NAME = "templates";

  /**
   * Constructor with the XML file location
   * 
   * @param location
   *          directory for file persistence
   */
  public ConceptTemplateStoreXML(File location, Context context) {
    super(ConceptTemplate.class, location, context);
  }

  /**
   * Default Constructor
   */
  public ConceptTemplateStoreXML(Context context) {
    super(ConceptTemplate.class, context);
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
    aliases.put("ConceptTemplate", ConceptTemplate.class);
    this.init(location, aliases);
  }

  @Override
  public ConceptTemplate update(ConceptTemplate conceptTemplate) {
    ConceptTemplate result = null;
    for (Iterator<ConceptTemplate> it = getRawList().iterator(); it.hasNext();) {
      ConceptTemplate current = it.next();
      if (current.getId().equals(conceptTemplate.getId())) {
        getLog().info("Updating ConceptTemplate");
        result = current;
        current.setName(conceptTemplate.getName());
        current.setDescription(conceptTemplate.getDescription());
        current.setProperties(conceptTemplate.getProperties());

        it.remove();

        break;
      }
    }
    if (result != null) {
      getRawList().add(result);
    }
    else {
      getLog().info("ConceptTemplate not found.");
    }
    return result;
  }

  @Override
  public List<ConceptTemplate> retrieveByParent(String id) {
    // A ConceptTemplate has no parent
    return null;
  }

  @Override
  public String getCollectionName() {
    return "ConceptTemplate";
  }

}

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
package fr.cnes.sitools.dataset.opensearch;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import org.restlet.Context;
import org.restlet.engine.Engine;

import fr.cnes.sitools.common.store.SitoolsStoreXML;
import fr.cnes.sitools.dataset.opensearch.model.Opensearch;
import fr.cnes.sitools.dataset.opensearch.model.OpensearchColumn;

/**
 * Implementation of OpenSearchStore with XStream FilePersistenceStrategy
 * 
 * @author AKKA
 * 
 */
public final class OpenSearchStoreXML extends SitoolsStoreXML<Opensearch> {

  /** default location for file persistence */
  private static final String COLLECTION_NAME = "opensearch";

  /** static logger for this store implementation */
  private static Logger log = Engine.getLogger(OpenSearchStoreXML.class.getName());

  /**
   * Constructor with the XML file location
   * 
   * @param location
   *          directory of FilePersistenceStrategy
   * @param context
   *          the context
   */
  public OpenSearchStoreXML(File location, Context context) {
    super(Opensearch.class, location, context);
  }

  /**
   * Default constructor
   * 
   * @param context
   *          the context
   */
  public OpenSearchStoreXML(Context context) {
    super(Opensearch.class, context);
    File defaultLocation = new File(COLLECTION_NAME);
    log.info("Store location " + defaultLocation.getAbsolutePath());
    init(defaultLocation);
  }

  @Override
  public synchronized Opensearch update(Opensearch os) {
    Opensearch result = null;
    for (Iterator<Opensearch> it = getRawList().iterator(); it.hasNext();) {
      Opensearch current = it.next();
      if (current.getId().equals(os.getId())) {
        log.info("Updating Opensearch");

        result = current;
        current.setName(os.getName());
        current.setDescription(os.getDescription());
        current.setIndexedColumns(os.getIndexedColumns());
        current.setDescriptionField(os.getDescriptionField());
        current.setPubDateField(os.getPubDateField());
        current.setLinkField(os.getLinkField());
        current.setGuidField(os.getGuidField());
        current.setTitleField(os.getTitleField());
        current.setStatus(os.getStatus());
        current.setImage(os.getImage());
        current.setDefaultSearchField(os.getDefaultSearchField());
        current.setUniqueKey(os.getUniqueKey());
        current.setKeywordColumns(os.getKeywordColumns());
        current.setLastImportDate(os.getLastImportDate());
        current.setErrorMsg(os.getErrorMsg());
        current.setParentUrl(os.getParentUrl());
        current.setLinkFieldRelative(os.isLinkFieldRelative());
        it.remove();

        break;
      }
    }
    if (result != null) {
      getRawList().add(result);
    }
    return result;
  }

  /**
   * XStream FilePersistenceStrategy initialization
   * 
   * @param location
   *          Directory
   */
  public void init(File location) {
    Map<String, Class<?>> aliases = new ConcurrentHashMap<String, Class<?>>();
    aliases.put("opensearch", Opensearch.class);
    aliases.put("opensearchColumn", OpensearchColumn.class);
    this.init(location, aliases);
  }

  @Override
  public List<Opensearch> retrieveByParent(String id) {
    return null;
  }

  @Override
  public String getCollectionName() {
    return COLLECTION_NAME;
  }

}

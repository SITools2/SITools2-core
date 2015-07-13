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
package fr.cnes.sitools.dataset.opensearch;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import org.restlet.Context;
import org.restlet.engine.Engine;

import fr.cnes.sitools.dataset.opensearch.model.Opensearch;
import fr.cnes.sitools.dataset.opensearch.model.OpensearchColumn;
import fr.cnes.sitools.persistence.XmlMapStore;


public class OpenSearchStoreXMLMap extends XmlMapStore<Opensearch> implements OpenSearchStoreInterface {

  /** default location for file persistence */
  private static final String COLLECTION_NAME = "opensearch";

  /** static logger for this store implementation */
  private static Logger log = Engine.getLogger(OpenSearchStoreXMLMap.class.getName());

  /**
   * Constructor with the XML file location
   * 
   * @param location
   *          directory of FilePersistenceStrategy
   * @param context
   *          the context
   */
  public OpenSearchStoreXMLMap(File location, Context context) {
    super(Opensearch.class, location, context);
  }

  /**
   * Default constructor
   * 
   * @param context
   *          the context
   */
  public OpenSearchStoreXMLMap(Context context) {
    super(Opensearch.class, context);
    File defaultLocation = new File(COLLECTION_NAME);
    log.info("Store location " + defaultLocation.getAbsolutePath());
    init(defaultLocation);
  }

  public List<Opensearch> retrieveByParent(String id) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String getCollectionName() {
    return COLLECTION_NAME;
  }

  @Override
  public void init(File location) {
    Map<String, Class<?>> aliases = new ConcurrentHashMap<String, Class<?>>();
    aliases.put("opensearch", Opensearch.class);
    aliases.put("opensearchColumn", OpensearchColumn.class);
    this.init(location, aliases);
    
  }


}

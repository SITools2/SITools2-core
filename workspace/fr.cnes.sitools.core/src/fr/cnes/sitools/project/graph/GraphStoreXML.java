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
package fr.cnes.sitools.project.graph;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.restlet.Context;

import fr.cnes.sitools.common.store.SitoolsStoreXML;
import fr.cnes.sitools.project.graph.model.Graph;

/**
 * Implementation of GraphStore with XStream FilePersistenceStrategy
 * 
 * @author AKKA
 * 
 */
public final class GraphStoreXML extends SitoolsStoreXML<Graph> {

  /** default location for file persistence */
  private static final String COLLECTION_NAME = "graph";


  /**
   * Constructor with the XML file location
   * 
   * @param location
   *          directory of FilePersistenceStrategy
   */
  public GraphStoreXML(File location, Context context) {
    super(Graph.class, location, context);
  }

  /**
   * Default constructor
   */
  public GraphStoreXML(Context context) {
    super(Graph.class, context);
    File defaultLocation = new File(COLLECTION_NAME);
    getLog().info("Store location " + defaultLocation.getAbsolutePath());
    init(defaultLocation);
  }

  @Override
  public Graph update(Graph graph) {
    Graph result = null;
    for (Iterator<Graph> it = getRawList().iterator(); it.hasNext();) {
      Graph current = it.next();
      if (current.getId().equals(graph.getId())) {
        getLog().info("Updating Graph");

        result = current;
        current.setName(graph.getName());
        current.setId(graph.getId());
        current.setParent(graph.getParent());
        current.setNodeList(graph.getNodeList());

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
    aliases.put("graph", Graph.class);
    this.init(location, aliases);
  }

  @Override
  public List<Graph> retrieveByParent(String id) {
    return null;
  }

  @Override
  public String getCollectionName() {
    return COLLECTION_NAME;
  }


}

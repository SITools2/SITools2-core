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
package fr.cnes.sitools.project.graph;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.restlet.Context;

import fr.cnes.sitools.persistence.XmlMapStore;
import fr.cnes.sitools.project.graph.model.Graph;

/**
 * Implementation of GraphStore with XStream FilePersistenceStrategy
 * 
 * @author AKKA
 * 
 */
public class GraphStoreXMLMap extends XmlMapStore<Graph> implements GraphStoreInterface {

  /** default location for file persistence */
  private static final String COLLECTION_NAME = "graph";

  /**
   * Constructor with the XML file location
   * 
   * @param location
   *          directory of FilePersistenceStrategy
   * @param context
   *          the Restlet Context
   */
  public GraphStoreXMLMap(File location, Context context) {
    super(Graph.class, location, context);
  }

  /**
   * Default constructor
   * 
   * @param context
   *          the Restlet Context
   */
  public GraphStoreXMLMap(Context context) {
    super(Graph.class, context);
    File defaultLocation = new File(COLLECTION_NAME);
    getLog().info("Store location " + defaultLocation.getAbsolutePath());
    init(defaultLocation);
  }

  public List<Graph> retrieveByParent(String id) {
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
    aliases.put("graph", Graph.class);
    this.init(location, aliases);
  }

  @Override
  public Graph update(Graph graph) {
    Graph result = null;

    Map<String, Graph> map = getMap();
    Graph current = map.get(graph.getId());
    if (current == null) {
      getLog().warning("Cannot update " + COLLECTION_NAME + " that doesn't already exists");
      return null;
    }
    getLog().info("Updating Graph");

    result = current;
    current.setName(graph.getName());
    current.setId(graph.getId());
    current.setParent(graph.getParent());
    current.setNodeList(graph.getNodeList());

    map.put(graph.getId(), current);

    return result;
  }

}

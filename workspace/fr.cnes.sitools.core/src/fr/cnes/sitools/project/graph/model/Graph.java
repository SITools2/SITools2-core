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
package fr.cnes.sitools.project.graph.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import fr.cnes.sitools.common.model.IResource;
import fr.cnes.sitools.dataset.model.DataSet;

/**
 * Class for definition of a research graph on a dataset
 * 
 * @author m.gond (AKKA Technologies)
 */
public final class Graph implements IResource {
  /**
   * Graph identifier
   */
  private String id = null;

  /**
   * Graph name
   */
  private String name = null;

  /**
   * Graph description
   */
  private String description = null;

  /**
   * the node List
   */
  private List<GraphNodeComplete> nodeList;

  /**
   * DataSet id
   */
  private String parent = null;

  /**
   * Gets the id value
   * 
   * @return the id
   */
  public String getId() {
    return id;
  }

  /**
   * Sets the value of id
   * 
   * @param id
   *          the id to set
   */
  public void setId(String id) {
    this.id = id;
  }

  /**
   * Gets the name value
   * 
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * Sets the value of name
   * 
   * @param name
   *          the name to set
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Gets the description value
   * 
   * @return the description
   */
  public String getDescription() {
    return description;
  }

  /**
   * Sets the value of description
   * 
   * @param description
   *          the description to set
   */
  public void setDescription(String description) {
    this.description = description;
  }

  /**
   * Gets the nodeList value
   * 
   * @return the nodeList
   */
  public List<GraphNodeComplete> getNodeList() {
    return nodeList;
  }

  /**
   * Sets the value of nodeList
   * 
   * @param nodeList
   *          the nodeList to set
   */
  public void setNodeList(List<GraphNodeComplete> nodeList) {
    this.nodeList = nodeList;
  }

  /**
   * Sets the value of parent
   * 
   * @param parent
   *          the parent to set
   */
  public void setParent(String parent) {
    this.parent = parent;
  }

  /**
   * Gets the parent value
   * 
   * @return the parent
   */
  public String getParent() {
    return parent;
  }

  /**
   * Gett All datasets under a specified list of nodes
   * 
   * @param nodes
   *          the nodes
   * @return tje list of datasets as graphNodeComplete
   */
  public List<GraphNodeComplete> getAllDatasets(List<GraphNodeComplete> nodes) {
    List<GraphNodeComplete> result = new ArrayList<GraphNodeComplete>();

    for (Iterator<GraphNodeComplete> iterator = nodes.iterator(); iterator.hasNext();) {
      GraphNodeComplete node = iterator.next();
      if (GraphNodeComplete.NODE_TYPE_DATASET.equals(node.getType())) {
        result.add(node);
      }
      else {
        result.addAll(getAllDatasets(node.getChildren()));
      }

    }

    return result;

  }

  /**
   * Updates all children corresponding to the dataset param
   * 
   * @param nodes
   *          : a nodeList
   * @param ds
   *          : the dataset to update
   * @return the list of children updated
   */
  public List<GraphNodeComplete> updateDatasetChildren(List<GraphNodeComplete> nodes, DataSet ds) {
    for (Iterator<GraphNodeComplete> iterator = nodes.iterator(); iterator.hasNext();) {
      GraphNodeComplete node = iterator.next();
      if (GraphNodeComplete.NODE_TYPE_DATASET.equals(node.getType())) {
        if (node.getDatasetId().equals(ds.getId())) {
          node.setNbRecord(ds.getNbRecords());
          node.setReadme(ds.getDescriptionHTML());
          node.setImageDs(ds.getImage().getUrl());
          node.setVisible(ds.isVisible());
          node.setStatus(ds.getStatus());
          node.setUrl(ds.getSitoolsAttachementForUsers());
        }
      }
      else {
        node.setChildren(updateDatasetChildren(node.getChildren(), ds));
      }

    }

    return nodes;
  }

  /**
   * Delete all children corresponding to the datasetId
   * 
   * @param nodes
   *          : a nodeList
   * @param datasetId
   *          : the id of the dataset to delete
   * @return the list of children without the deleted dataset
   */
  public List<GraphNodeComplete> deleteDatasetChildren(List<GraphNodeComplete> nodes, String datasetId) {
    for (Iterator<GraphNodeComplete> iterator = nodes.iterator(); iterator.hasNext();) {
      GraphNodeComplete node = iterator.next();
      if (GraphNodeComplete.NODE_TYPE_DATASET.equals(node.getType())) {
        if (node.getDatasetId().equals(datasetId)) {
          iterator.remove();
        }
      }
      else {
        node.setChildren(deleteDatasetChildren(node.getChildren(), datasetId));
      }

    }

    return nodes;
  }

}

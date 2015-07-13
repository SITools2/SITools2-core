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
package fr.cnes.sitools.project.graph.model;

/**
 * DTO definition of a Graph node.
 * @author m.gond (AKKA Technologies)
 */
public class AbstractGraphNode {
  /**
   * node types constants node
   */
  public static final String NODE_TYPE_NODE = "node";
  /**
   * node types constants dataset
   */
  public static final String NODE_TYPE_DATASET = "dataset";
  /**
   * The text of the node
   */
  private String text;
  /**
   * is this node a leaf
   */
  private boolean leaf;
  /**
   * The type of the node
   */
  private String type;
  /**
   * The description of the node
   */
  private String description;

  /**
   * Gets the text value
   * 
   * @return the text
   */
  public final String getText() {
    return text;
  }

  /**
   * Sets the value of text
   * 
   * @param text
   *          the text to set
   */
  public void setText(String text) {
    this.text = text;
  }

  /**
   * Gets the leaf value
   * 
   * @return the leaf
   */
  public boolean isLeaf() {
    return leaf;
  }

  /**
   * Sets the value of leaf
   * 
   * @param leaf
   *          the leaf to set
   */
  public void setLeaf(boolean leaf) {
    this.leaf = leaf;
  }

  /**
   * Gets the type value
   * 
   * @return the type
   */
  public String getType() {
    return type;
  }

  /**
   * Sets the value of type
   * 
   * @param type
   *          the type to set
   */
  public void setType(String type) {
    this.type = type;
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
   * Gets the description value
   * 
   * @return the description
   */
  public String getDescription() {
    return description;
  }

}

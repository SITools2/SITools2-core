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
package fr.cnes.sitools.project.graph.model;

import java.util.ArrayList;

import fr.cnes.sitools.common.model.Resource;

/**
 * GraphNode
 * 
 * @author m.gond (AKKA Technologies)
 */
public final class GraphNode extends AbstractGraphNode {
  /**
   * the image
   */
  private Resource image;
  /**
   * the children's list
   */
  private ArrayList<AbstractGraphNode> children;

  /**
   * Gets the image value
   * 
   * @return the image
   */
  public Resource getImage() {
    return image;
  }

  /**
   * Sets the value of image
   * 
   * @param image
   *          the image to set
   */
  public void setImage(Resource image) {
    this.image = image;
  }

  /**
   * Gets the children value
   * 
   * @return the children
   */
  public ArrayList<AbstractGraphNode> getChildren() {
    return children;
  }

  /**
   * Sets the value of children
   * 
   * @param children
   *          the children to set
   */
  public void setChildren(ArrayList<AbstractGraphNode> children) {
    this.children = children;
  }

}

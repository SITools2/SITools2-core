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
package fr.cnes.sitools.dataset.model.structure;

import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import fr.cnes.sitools.dataset.model.Predicat;
import fr.cnes.sitools.datasource.jdbc.model.Table;

/**
 * "Complete" bean for node and leaf description.
 * 
 * @author D.Arpin (AKKA Technologies)
 */
@XStreamAlias("StructureNodeComplete")
public final class StructureNodeComplete {

  /** =========================== ABSTRACT ATTRIBUTES ====================== */
  /**
   * node types constants node
   */
  public static final String TABLE_NODE = "table";
  /**
   * node types constants dataset
   */
  public static final String JOIN_CONDITION_NODE = "join";
  /**
   * The text of the node
   */
  private String text;
  /**
   * is this node a leaf
   */
  private Boolean leaf;
  /**
   * The type of the node
   */
  private String type;
  /**
   * The description of the node
   */
  private String description;
  
  /** =========================== TABLE NODE ATTRIBUTES ====================== */

  /**
   * the Table
   */
  private Table table;
  
  /**
   * le type de typeJointure
   */
  private TypeJointure typeJointure;
  /**
   * the children's list
   */
  private List<StructureNodeComplete> children;
  

  /** =========================== JOINTURE_NODE ATTRIBUTES ====================== */

  /**
   * The join Predicats
   */
  private Predicat predicat;

  /** =========================== GETTERS / SETTERS ====================== */

  /**
   * Gets the text value
   * 
   * @return the text
   */
  public String getText() {
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
  public Boolean isLeaf() {
    return leaf;
  }

  /**
   * Sets the value of leaf
   * 
   * @param leaf
   *          the leaf to set
   */
  public void setLeaf(Boolean leaf) {
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
   * Gets the children value
   * 
   * @return the children
   */
  public List<StructureNodeComplete> getChildren() {
    return children;
  }

  /**
   * Sets the value of children
   * 
   * @param children
   *          the children to set
   */
  public void setChildren(List<StructureNodeComplete> children) {
    this.children = children;
  }


  /**
   * Gets the table value
   * @return the table
   */
  public Table getTable() {
    return table;
  }

  /**
   * Sets the value of table
   * @param table the table to set
   */
  public void setTable(Table table) {
    this.table = table;
  }

  /**
   * Gets the typeJointure value
   * @return the typeJointure
   */
  public TypeJointure getTypeJointure() {
    return typeJointure;
  }

  /**
   * Sets the value of typeJointure
   * @param typeJointure the typeJointure to set
   */
  public void setTypeJointure(TypeJointure typeJointure) {
    this.typeJointure = typeJointure;
  }

  /**
   * Gets the predicat value
   * @return the predicat
   */
  public Predicat getPredicat() {
    return predicat;
  }

  /**
   * Sets the value of predicat
   * @param predicat the predicat to set
   */
  public void setPredicat(Predicat predicat) {
    this.predicat = predicat;
  }

  
  
  

}

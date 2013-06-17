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
package fr.cnes.sitools.dataset.model.structure;

import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import fr.cnes.sitools.datasource.jdbc.model.Table;

/**
 * 
 * Class for definition of a generic structure from a data source.
 * Multiple types : JDBC Table / JDBC View ...
 * 
 * @author D.Arpin (AKKA Technologies)
 * 
 */
@XStreamAlias("SitoolsStructure")
public final class SitoolsStructure {
  
  /** Main table */
  private Table mainTable;
  
  /**
   * the node List
   */
  private List<StructureNodeComplete> nodeList;

 

  /**
   * Gets the nodeList value
   * 
   * @return the nodeList
   */
  public List<StructureNodeComplete> getNodeList() {
    return nodeList;
  }

  /**
   * Sets the value of nodeList
   * 
   * @param nodeList
   *          the nodeList to set
   */
  public void setNodeList(List<StructureNodeComplete> nodeList) {
    this.nodeList = nodeList;
  }

  /**
   * Gets the mainTable value
   * @return the mainTable
   */
  public Table getMainTable() {
    return mainTable;
  }

  /**
   * Sets the value of mainTable
   * @param mainTable the mainTable to set
   */
  public void setMainTable(Table mainTable) {
    this.mainTable = mainTable;
  }



}

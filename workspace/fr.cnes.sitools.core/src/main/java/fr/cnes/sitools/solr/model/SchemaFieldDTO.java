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
package fr.cnes.sitools.solr.model;

import java.io.Serializable;

/*
 * 
 * Store fields for schema.xml
 * 
 * Mandatory fields: # type The type of the field # name The name of the field
 * 
 * # default The default value for this field if none is provided while adding documents # indexed=true|false True if
 * this field should be "indexed". If (and only if) a field is indexed, then it is searchable, sortable, and facetable.
 * # stored=true|false True if the value of the field should be retrievable during a search # compressed=true|false True
 * if this field should be stored using gzip compression. (This will only apply if the field type is compressable; among
 * the standard field types, only TextField and StrField are.)
 * 
 */ 
/** 
 * Store fields for schema.xml
 * @author m.gond (AKKA Technologies)
 * 
 */
public final class SchemaFieldDTO implements Serializable {

  /**
   * serialVersionUID
   */
  private static final long serialVersionUID = 5730261504538422780L;
  /**
   * The name of the field
   */
  private String name = null;
  /**
   * The type of the field
   */
  private String type = null;
  /**
   * The default value for this field if none is provided while adding documents
   */
  private String defaultValue = null;
  /**
   * True if this field should be "indexed". If (and only if) a field is indexed, then it is searchable, sortable, and
   * facetable.
   */
  private boolean indexed = false;
  /**
   * True if the value of the field should be retrievable during a search
   */
  private boolean stored = false;
  /**
   * The column id
   */
  private String idCol = null;

  /**
   * True if this field should be stored using gzip compression. (This will only apply if the field type is
   * compressable; among the standard field types, only TextField and StrField are.)
   */
  private boolean compressed;

  /**
   * SchemaFieldDTO constructor
   */
  public SchemaFieldDTO() {
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
   * Gets the defaultValue value
   * 
   * @return the defaultValue
   */

  public String getDefaultValue() {
    return defaultValue;
  }

  /**
   * Sets the value of defaultValue
   * 
   * @param defaultValue
   *          the defaultValue to set
   */

  public void setDefaultValue(String defaultValue) {
    this.defaultValue = defaultValue;
  }

  /**
   * Gets the indexed value
   * 
   * @return the indexed
   */

  public boolean isIndexed() {
    return indexed;
  }

  /**
   * Sets the value of indexed
   * 
   * @param indexed
   *          the indexed to set
   */

  public void setIndexed(boolean indexed) {
    this.indexed = indexed;
  }

  /**
   * Gets the stored value
   * 
   * @return the stored
   */

  public boolean isStored() {
    return stored;
  }

  /**
   * Sets the value of stored
   * 
   * @param stored
   *          the stored to set
   */

  public void setStored(boolean stored) {
    this.stored = stored;
  }

  /**
   * Gets the compressed value
   * 
   * @return the compressed
   */

  public boolean isCompressed() {
    return compressed;
  }

  /**
   * Sets the value of compressed
   * 
   * @param compressed
   *          the compressed to set
   */

  public void setCompressed(boolean compressed) {
    this.compressed = compressed;
  }

  /**
   * Gets the idCol value
   * 
   * @return the idCol
   */
  public String getIdCol() {
    return idCol;
  }

  /**
   * Sets the value of idCol
   * 
   * @param idCol
   *          the idCol to set
   */
  public void setIdCol(String idCol) {
    this.idCol = idCol;
  }

}

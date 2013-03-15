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
package fr.cnes.sitools.solr.model;

import java.io.Serializable;
import java.util.List;

/*
 * Infos a modifier dans le fichier schema.xml
 * 
 * Template freemarker de schema.xml :
 * 
 * <code>
 * <field name="prop_id" type="string" indexed="true" stored="true" required="true" /> 
 * <field name="cycle" type="string" indexed="true" stored="true" required="true" />
 * <field name="title" type="string" indexed="true" stored="true"/>
 * <field name="fname" type="text" indexed="true" stored="true"/> 
 * <field name="lname" type="text" indexed="true" stored="true"/>
 * <field name="institution" type="text" indexed="true" stored="true"/> 
 * <field name="abstract" type="text" indexed="true" stored="true"/>
 * </code>
 * 
 * <uniqueKey>prop_id< /uniqueKey>
 * 
 * The <uniqueKey> declaration can be used to inform Solr that there is a field in your index which should be unique for
 * all documents. If a document is added that contains the same value for this field as an existing document, the old
 * document will be deleted. It is not mandatory for a schema to have a uniqueKey field.
 * 
 * 
 * <!-- field for the QueryParser to use when an explicit fieldname is absent -->
 * <defaultSearchField>abstract</defaultSearchField>
 */

/**
 * Schema configuration class 
 * @author jp.boignard (AKKA Technologies)
 * 
 */
public final class SchemaConfigDTO implements Serializable {
  /**
   * serialVersionUID
   */
  private static final long serialVersionUID = 4777889116414262816L;

  /**
   * Document name
   */
  private String document;

  /**
   * unique Key field
   */
  private String uniqueKey;

  /**
   * defaultSearchField field
   */
  private String defaultSearchField;

  /**
   * Field list
   */
  private List<SchemaFieldDTO> fields;

  /**
   * SchemaConfigDTO constructor
   */
  public SchemaConfigDTO() {
    super();
  }

  /**
   * Gets the uniqueKey value
   * 
   * @return the uniqueKey
   */

  public String getUniqueKey() {
    return uniqueKey;
  }

  /**
   * Sets the value of uniqueKey
   * 
   * @param uniqueKey
   *          the uniqueKey to set
   */

  public void setUniqueKey(String uniqueKey) {
    this.uniqueKey = uniqueKey;
  }

  /**
   * Gets the defaultSearchField value
   * 
   * @return the defaultSearchField
   */

  public String getDefaultSearchField() {
    return defaultSearchField;
  }

  /**
   * Sets the value of defaultSearchField
   * 
   * @param defaultSearchField
   *          the defaultSearchField to set
   */

  public void setDefaultSearchField(String defaultSearchField) {
    this.defaultSearchField = defaultSearchField;
  }

  /**
   * Gets the fields value
   * 
   * @return the fields
   */

  public List<SchemaFieldDTO> getFields() {
    return fields;
  }

  /**
   * Sets the value of fields
   * 
   * @param fields
   *          the fields to set
   */

  public void setFields(List<SchemaFieldDTO> fields) {
    this.fields = fields;
  }

  /**
   * Gets the document value
   * 
   * @return the document
   */

  public String getDocument() {
    return document;
  }

  /**
   * Sets the value of document
   * 
   * @param document
   *          the document to set
   */

  public void setDocument(String document) {
    this.document = document;
  }

}

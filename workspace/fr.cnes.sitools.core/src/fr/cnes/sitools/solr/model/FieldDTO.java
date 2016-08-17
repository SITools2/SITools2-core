    /*******************************************************************************
 * Copyright 2010-2016 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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
 * Les noms des champs solr doivent etre suffixes selon leur type pour qu'ils puissent etre indexes et stockes
 * automatiquement
 * 
 * sinon il faudrait modifier le fichier schema.xml dans la config solr. cf SchemaConfigDTO
 * 
 * <dynamicField name="*_i" type="sint" indexed="true" stored="true"/> <dynamicField name="*_s" type="string"
 * indexed="true" stored="true"/> <dynamicField name="*_l" type="slong" indexed="true" stored="true"/> <dynamicField
 * name="*_t" type="text" indexed="true" stored="true"/> <dynamicField name="*_b" type="boolean" indexed="true"
 * stored="true"/> <dynamicField name="*_f" type="sfloat" indexed="true" stored="true"/> <dynamicField name="*_d"
 * type="sdouble" indexed="true" stored="true"/> <dynamicField name="*_dt" type="date" indexed="true" stored="true"/>
 */

/**
 * Field DTO class
 * @author jp.boignard (AKKA Technologies)
 * 
 */

public final class FieldDTO implements Serializable {

  /**
   * serialVersionUID
   */
  private static final long serialVersionUID = -513124750417046900L;
  /**
   * The column name
   */
  private String column;
  /**
   * The column indexed name
   */
  private String name;
  /**
   * The optionnal template string
   */
  private String template;

  /**
   * FieldDTO constructor
   */
  public FieldDTO() {
    super();

  }

  /**
   * Gets the column value
   * 
   * @return the column
   */
  public String getColumn() {
    return column;
  }

  /**
   * Sets the value of column
   * 
   * @param column
   *          the column to set
   */
  public void setColumn(String column) {
    this.column = column;
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
   * Sets the value of template
   * @param template the template to set
   */
  public void setTemplate(String template) {
    this.template = template;
  }

  /**
   * Gets the template value
   * @return the template
   */
  public String getTemplate() {
    return template;
  }

}

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
package fr.cnes.sitools.form.project.dto;

import java.util.ArrayList;
import java.util.List;

import fr.cnes.sitools.form.dataset.dto.ParameterDTO;
import fr.cnes.sitools.form.model.AbstractParameter;
import fr.cnes.sitools.form.project.model.FormPropertyParameter;

/**
 * Object to store Form property search parameter
 * 
 * 
 * @author m.gond
 */
public class FormPropertyParameterDTO {
  /** The name of the property */
  private String name;
  /** The type of search to perform on that component */
  private String type;

  /**
   * Default constructor
   */
  public FormPropertyParameterDTO() {
    super();
  }

  /**
   * Constructor with name of type attributes
   * 
   * @param name
   *          the name of the property
   * @param type
   *          the type of search to perform on that property
   */
  public FormPropertyParameterDTO(String name, String type) {
    this.name = name;
    this.type = type;
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

  public static List<FormPropertyParameterDTO> propertiesToDTO(List<FormPropertyParameter> properties) {

    List<FormPropertyParameterDTO> propertiesDTO = new ArrayList<FormPropertyParameterDTO>();
    
    for (FormPropertyParameter fp : properties){
      propertiesDTO.add(formPropertyToDTO(fp));
    }
    
    return propertiesDTO;
  }

  private static FormPropertyParameterDTO formPropertyToDTO(FormPropertyParameter fp) {

    
    FormPropertyParameterDTO formpropDTO = new FormPropertyParameterDTO();
    
    formpropDTO.setName(fp.getName());
    formpropDTO.setType(fp.getType());
    
    return formpropDTO;
  }
  

  

}

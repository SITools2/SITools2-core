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
package fr.cnes.sitools.form.project.services;

import fr.cnes.sitools.plugins.resources.model.ResourceModel;
import fr.cnes.sitools.plugins.resources.model.ResourceParameter;
import fr.cnes.sitools.plugins.resources.model.ResourceParameterType;
import fr.cnes.sitools.project.ProjectApplication;

/**
 * Multidataset properties search service Resource Model
 * 
 * 
 * @author m.gond
 */
public class ServicePropertiesSearchResourceModel extends ResourceModel {
  
  /**
   * Default constructor, only overrides name, description and resourceClassName
   */
  public ServicePropertiesSearchResourceModel() {
    super();
    setClassAuthor("AKKA Technologies");
    setClassOwner("CNES");
    setClassVersion("0.1");
    setName("ServicePropertiesSearchResourceModel");
    setDescription("Service to get the list of dataset which corresponds to some properties");

    setResourceClassName("fr.cnes.sitools.form.project.services.ServicePropertiesSearchResource");

    ResourceParameter dictionary = new ResourceParameter("dictionary", "The id of the dictionary",
        ResourceParameterType.PARAMETER_INTERN);
    /** Type de paramètre pour lister les colonnes du dataset */
    dictionary.setValueType("xs:string");
    ResourceParameter collection = new ResourceParameter("collection", "The id of the collection to use",
        ResourceParameterType.PARAMETER_USER_INPUT);
    /** Type de colonne booléen */
    collection.setValueType("xs:string");
    
    this.addParam(dictionary);
    this.addParam(collection);

    this.setApplicationClassName(ProjectApplication.class.getName());

  }

}

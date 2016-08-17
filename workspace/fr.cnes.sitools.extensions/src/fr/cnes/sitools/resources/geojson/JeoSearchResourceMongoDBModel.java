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
package fr.cnes.sitools.resources.geojson;

import fr.cnes.sitools.dataset.DataSetApplication;
import fr.cnes.sitools.plugins.resources.model.DataSetSelectionType;
import fr.cnes.sitools.plugins.resources.model.ResourceModel;
import fr.cnes.sitools.plugins.resources.model.ResourceParameter;
import fr.cnes.sitools.plugins.resources.model.ResourceParameterType;

/**
 * Resource model class for Jeobrowser search
 * 
 * 
 * @author m.gond
 */
public class JeoSearchResourceMongoDBModel extends ResourceModel {

  /** The name of the concept to look for in the dictionary mapping */
  public static final String CONCEPT_NAME = "commonGeoWKTField";

  /** The name of the concept to look for in the dictionary mapping for the type of the geometry */
  public static final String CONCEPT_NAME_TYPE = "commonGeoWKTTypeField";

  /** The name of the parameter for dictionary name */
  public static final String DICO_PARAM_NAME = "dictionary_name";

  /**
   * JeoSearchResourceModel constructor
   */
  public JeoSearchResourceMongoDBModel() {
    super();
    setClassAuthor("AKKA Tecnologies");
    setClassOwner("CNES");
    setClassVersion("0.1");
    setName("JeoSearchResourceModel MongoDB");
    setDescription("GEO JSON export on the fly on mongoDB dataset");
    setResourceClassName("fr.cnes.sitools.resources.geojson.JeoSearchResourceMongoDB");

    ResourceParameter param1 = new ResourceParameter(DICO_PARAM_NAME, "The name of the dictionary to use",
        ResourceParameterType.PARAMETER_INTERN);
    param1.setValue("JeoDictionary");
    param1.setValueType("xs:dictionary");
    this.addParam(param1);

    this.setApplicationClassName(DataSetApplication.class.getName());
    this.setDataSetSelection(DataSetSelectionType.MULTIPLE);
    this.getParameterByName("methods").setValue("GET");
    this.getParameterByName("url").setValue("/jeo/opensearch/search");

  }

}

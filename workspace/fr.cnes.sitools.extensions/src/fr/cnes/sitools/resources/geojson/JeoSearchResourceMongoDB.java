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
package fr.cnes.sitools.resources.geojson;

import java.util.List;
import java.util.logging.Level;

import org.restlet.Context;
import org.restlet.representation.Representation;

import fr.cnes.sitools.dataset.DataSetApplication;
import fr.cnes.sitools.dataset.converter.business.ConverterChained;
import fr.cnes.sitools.dataset.database.DatabaseRequestParameters;
import fr.cnes.sitools.dataset.dto.DictionaryMappingDTO;
import fr.cnes.sitools.dataset.model.DataSet;
import fr.cnes.sitools.resources.geojson.representations.GeoJsonMongoDBRepresentation;

/**
 * Classic implementation of JeoSearch resource
 * 
 * 
 * @author m.gond
 */
public class JeoSearchResourceMongoDB extends AbstractJeoSearchResource {

  @Override
  public Representation getRepresentation(DatabaseRequestParameters params, String geometryColName,
      ConverterChained converterChained, DataSet dataset, Context context) {
    String geometryTypeColName = getColumnNameFromConcept(JeoSearchResourceMongoDBModel.DICO_PARAM_NAME,
        JeoSearchResourceMongoDBModel.CONCEPT_NAME_TYPE);
    return new GeoJsonMongoDBRepresentation(params, geometryColName, geometryTypeColName, converterChained, dataset,
        getContext());
  }

  /**
   * Get the columnName corresponding to the given conceptName and dictionary name in the dataset
   * 
   * @param dicoParamName
   *          the name of the resource parameter which contains the dictionary name
   * @param conceptName
   *          the name of the concept
   * @return the columnName or null if not found
   */
  public String getColumnNameFromConcept(String dicoParamName, String conceptName) {
    String columnName = null;
    DataSetApplication datasetApp = (DataSetApplication) getApplication();
    DataSet dataset = datasetApp.getDataSet();
    String dicoName = this.getParameterValue(dicoParamName);
    if (dicoName != null) {
      DictionaryMappingDTO dico = datasetApp.getColumnConceptMappingDTO(dicoName);
      if (dico != null) {
        // gets the dictionaryMapping
        List<String> columnsAlias = dico.getListColumnAliasMapped(conceptName);
        if (columnsAlias.size() == 0) {
          getLogger().log(Level.INFO, dataset.getName() + " no column mapped for concept " + conceptName);
        }
        else if (columnsAlias.size() > 1) {
          getLogger().log(Level.INFO, dataset.getName() + " too many columns mapped for concept " + conceptName);
        }
        else {
          columnName = columnsAlias.get(0);
        }
      }
    }
    return columnName;
  }
}

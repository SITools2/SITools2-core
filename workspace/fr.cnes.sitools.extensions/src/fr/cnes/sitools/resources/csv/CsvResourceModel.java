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
package fr.cnes.sitools.resources.csv;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import fr.cnes.sitools.common.validator.ConstraintViolation;
import fr.cnes.sitools.common.validator.ConstraintViolationLevel;
import fr.cnes.sitools.common.validator.Validator;
import fr.cnes.sitools.dataset.DataSetApplication;
import fr.cnes.sitools.plugins.resources.model.DataSetSelectionType;
import fr.cnes.sitools.plugins.resources.model.ResourceModel;
import fr.cnes.sitools.plugins.resources.model.ResourceParameter;
import fr.cnes.sitools.plugins.resources.model.ResourceParameterType;

/**
 * The Model for Csv export Resource
 * 
 * 
 * @author m.gond
 */
public class CsvResourceModel extends ResourceModel {

  /**
   * Constructor
   */
  public CsvResourceModel() {

    super();
    setClassAuthor("AKKA Technologies");
    setClassOwner("CNES");
    setClassVersion("0.1");
    setName("CsvResourceModel");
    setDescription("CSV export of datasets");
    setClassName("fr.cnes.sitools.resources.csv.CsvResourceModel");
    /** Resource Facade */
    setResourceClassName(fr.cnes.sitools.resources.csv.CsvResource.class.getName());
    /** Paramètres */
    ResourceParameter maxRows = new ResourceParameter("max_rows", "Set max_rows=-1 to export everything",
        ResourceParameterType.PARAMETER_INTERN);
    maxRows.setValue("-1");

    this.addParam(maxRows);
    this.setApplicationClassName(DataSetApplication.class.getName());
    this.setDataSetSelection(DataSetSelectionType.MULTIPLE);
    this.getParameterByName("methods").setValue("GET");
    this.completeAttachUrlWith("/csv");
  }

  @Override
  public Validator<ResourceModel> getValidator() {
    return new Validator<ResourceModel>() {

      @Override
      public Set<ConstraintViolation> validate(ResourceModel item) {
        Set<ConstraintViolation> constraints = new HashSet<ConstraintViolation>();
        Map<String, ResourceParameter> params = item.getParametersMap();
        ResourceParameter param = params.get("max_rows");

        String value = param.getValue();
        try {
          double maxrows = Double.valueOf(value);
          if (maxrows == 0) {
            ConstraintViolation constraint = new ConstraintViolation();
            constraint.setMessage("0 is not a coherent value");
            constraint.setLevel(ConstraintViolationLevel.CRITICAL);
            constraint.setInvalidValue(value);
            constraint.setValueName(param.getName());
            constraints.add(constraint);
          }
        }
        catch (NumberFormatException ex) {
          ConstraintViolation constraint = new ConstraintViolation();
          constraint.setMessage(ex.getMessage());
          constraint.setLevel(ConstraintViolationLevel.CRITICAL);
          constraint.setInvalidValue(value);
          constraint.setValueName(param.getName());
          constraints.add(constraint);
        }
        return constraints;
      }
    };
  }
}

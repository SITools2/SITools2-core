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
package fr.cnes.sitools.resources.order;

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
import fr.cnes.sitools.tasks.model.TaskResourceModel;
import fr.cnes.sitools.tasks.model.TaskRunTypeAdministration;

/**
 * Model for OrderResource
 * 
 * 
 * @author m.gond
 */
public class OrderResourceModel extends TaskResourceModel {

  /**
   * Constructor
   */
  public OrderResourceModel() {

    super();
    setClassAuthor("AKKA Technologies");
    setClassOwner("CNES");
    setClassVersion("0.1");
    setName("OrderResourceModel");
    setDescription("Order resources associated to metadata and save it in user storage. (Can also create a ZIP, TAR or TAR.GZ 'on the fly')");
    /** Resource facade */
    setResourceClassName("fr.cnes.sitools.resources.order.OrderResourceFacade");
    /** Resource d'implémentation */
    setResourceImplClassName("fr.cnes.sitools.resources.order.OrderResource");

    setRunTypeAdministration(TaskRunTypeAdministration.TASK_DEFAULT_RUN_ASYNC);

    ResourceParameter paramColUrl = new ResourceParameter("colUrl", "Colum containing data url for order",
        ResourceParameterType.PARAMETER_INTERN);
    /** Type de paramètre pour lister les colonnes du dataset */
    paramColUrl.setValueType("xs:dataset.columnAlias");
    ResourceParameter param2 = new ResourceParameter("zip",
        "(true or false) If the data needs to be zipped at the end", ResourceParameterType.PARAMETER_USER_INPUT);
    param2.setValue("false");
    /** Type de colonne booléen */
    param2.setValueType("xs:boolean");

    this.addParam(paramColUrl);
    this.addParam(param2);

    this.setApplicationClassName(DataSetApplication.class.getName());
    this.getParameterByName("methods").setValue("POST");
    this.setDataSetSelection(DataSetSelectionType.ALL);

    // paramètres pour la limitation du nombre de fichiers
    ResourceParameter paramMaxWarningThreshold = new ResourceParameter("max_warning_threshold",
        "Maximum number of files allowed to be downloaded before client warning, download is still allowed",
        ResourceParameterType.PARAMETER_USER_GUI);
    paramMaxWarningThreshold.setValueType("xs:integer");

    ResourceParameter paramTooManySelectedThreshold = new ResourceParameter("too_many_selected_threshold",
        "Maximum number of files allowed to be downloaded (-1 to set no limit)", ResourceParameterType.PARAMETER_INTERN);
    paramTooManySelectedThreshold.setValueType("xs:integer");
    paramTooManySelectedThreshold.setValue("-1");

    ResourceParameter paramMaxWarningThresholdText = new ResourceParameter("max_warning_threshold_text",
        "Text to display to the user when Warning threshold is reached", ResourceParameterType.PARAMETER_USER_GUI);
    paramMaxWarningThresholdText.setValueType("xs:string");

    ResourceParameter paramTooManySelectedThresholdText = new ResourceParameter("too_many_selected_threshold_text",
        "Text to display to the user when TooMaxySelected threshold is reached",
        ResourceParameterType.PARAMETER_USER_GUI);
    paramTooManySelectedThresholdText.setValueType("xs:string");

    this.addParam(paramMaxWarningThreshold);
    this.addParam(paramTooManySelectedThreshold);
    this.addParam(paramMaxWarningThresholdText);
    this.addParam(paramTooManySelectedThresholdText);

    this.getParameterByName("fileName").setValue("dataset_order_" + "${date:yyyy-MM-dd HH_mm_ss}");

  }

  @Override
  public Validator<ResourceModel> getValidator() {
    return new Validator<ResourceModel>() {

      @Override
      public Set<ConstraintViolation> validate(ResourceModel item) {
        Set<ConstraintViolation> constraints = new HashSet<ConstraintViolation>();
        Map<String, ResourceParameter> params = item.getParametersMap();
        ResourceParameter param = params.get("colUrl");
        String value = param.getValue();
        if (value == null || value.equals("")) {
          ConstraintViolation constraint = new ConstraintViolation();
          constraint.setMessage("An attribute of the dataset must be choosen");
          constraint.setLevel(ConstraintViolationLevel.CRITICAL);
          constraint.setValueName(param.getName());
          constraints.add(constraint);
        }
        param = params.get("zip");
        if (param != null) {
          value = param.getValue();
          if (value == null || (!"false".equals(value) && !"true".equals(value))) {
            ConstraintViolation constraint = new ConstraintViolation();
            constraint.setMessage("Must be a boolean");
            constraint.setLevel(ConstraintViolationLevel.CRITICAL);
            constraint.setInvalidValue(value);
            constraint.setValueName(param.getName());
            constraints.add(constraint);
          }
        }
        param = params.get("too_many_selected_threshold");
        value = param.getValue();
        if (value == null || "".equals(value)) {
          ConstraintViolation constraint = new ConstraintViolation();
          constraint.setMessage("Cannot be null");
          constraint.setLevel(ConstraintViolationLevel.CRITICAL);
          constraint.setInvalidValue(value);
          constraint.setValueName(param.getName());
          constraints.add(constraint);
        }
        else {
          try {
            Integer.parseInt(value);
          }
          catch (NumberFormatException e) {
            ConstraintViolation constraint = new ConstraintViolation();
            constraint.setMessage("Must be an integer value");
            constraint.setLevel(ConstraintViolationLevel.CRITICAL);
            constraint.setInvalidValue(value);
            constraint.setValueName(param.getName());
            constraints.add(constraint);
          }
        }
        return constraints;
      }
    };
  }

}

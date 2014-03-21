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
package fr.cnes.sitools.resources.order.cart;

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
 * The Model for order export Resource
 */
public class CartResourceModel extends ResourceModel {

  /**
   * Constructor
   */
  public CartResourceModel() {

    super();
    setClassAuthor("AKKA Technologies");
    setClassOwner("CNES");
    setClassVersion("0.2");
    setName("CartResourceModel");
    setDescription("export of datasets");
    setClassName("fr.cnes.sitools.resources.cart.CartResourceModel");
    /** Resource Facade */
    setResourceClassName(fr.cnes.sitools.resources.order.cart.CartResource.class.getName());
    /** Paramètres */
    ResourceParameter param1 = new ResourceParameter("title", "htmlTitle", ResourceParameterType.PARAMETER_INTERN);
    ResourceParameter param2 = new ResourceParameter("max_rows", "Set max_rows=-1 to export everything",
        ResourceParameterType.PARAMETER_INTERN);
    param2.setValue("-1");

    this.addParam(param1);
    this.addParam(param2);
    this.setApplicationClassName(DataSetApplication.class.getName());
    this.setDataSetSelection(DataSetSelectionType.MULTIPLE);
    this.getParameterByName("methods").setValue("GET");
//    this.completeAttachUrlWith("/htmlexport");
    this.completeAttachUrlWith("/cart");
  }

  @Override
  public Validator<ResourceModel> getValidator() {
    return new Validator<ResourceModel>() {

      @Override
      public Set<ConstraintViolation> validate(ResourceModel item) {
        Set<ConstraintViolation> constraints = new HashSet<ConstraintViolation>();
        Map<String, ResourceParameter> params = item.getParametersMap();
        ResourceParameter param = params.get("title");
        String value = param.getValue();
        if (value.equals("")) {
          ConstraintViolation constraint = new ConstraintViolation();
          constraint.setMessage("There is not title");
          constraint.setLevel(ConstraintViolationLevel.WARNING);
          constraint.setValueName(param.getName());
          constraints.add(constraint);
        }
        param = params.get("max_rows");

        value = param.getValue();
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

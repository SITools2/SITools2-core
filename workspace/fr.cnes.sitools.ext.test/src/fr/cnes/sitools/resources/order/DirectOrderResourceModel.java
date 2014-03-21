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
 * {@link ResourceModel} to order files and create an archive file 'on the fly' Multiple archive format are available
 * including ZIP, TAR or TAR.GZ
 * 
 * 
 * @author m.gond
 */
public class DirectOrderResourceModel extends OrderResourceModel {
  /**
   * Constructor for {@link DirectOrderResource}, initialise the properties and parameters of the ResourceModel
   */
  public DirectOrderResourceModel() {
    super();

    setResourceImplClassName("fr.cnes.sitools.resources.order.DirectOrderResource");

    setClassAuthor("Akka Technologies");
    setClassOwner("CNES");
    setClassVersion("0.9.3");
    setName("DirectOrderResourceModel");
    setDescription("Order resources associated to metadata, creating a ZIP, TAR or TAR.GZ 'on the fly' and download it.");
    this.setApplicationClassName(DataSetApplication.class.getName());
    this.setDataSetSelection(DataSetSelectionType.MULTIPLE);
    this.getParameterByName("methods").setValue("GET");
    this.getParameterByName(TaskResourceModel.RUN_TYPE_PARAM_NAME_ADMINISTATION).setValue(
        TaskRunTypeAdministration.TASK_FORCE_RUN_SYNC.toString());

    ResourceParameter archiveType = new ResourceParameter("archiveType", "The type of archive needed",
        ResourceParameterType.PARAMETER_USER_INPUT);
    archiveType.setValueType("xs:enum[zip,tar.gz,tar]");
    this.addParam(archiveType);
    // remove the ZIP parameter, not used in this Order
    this.getParametersMap().remove("zip");

  }

  @Override
  public Validator<ResourceModel> getValidator() {

    final Validator<ResourceModel> parent = super.getValidator();

    return new Validator<ResourceModel>() {

      @Override
      public Set<ConstraintViolation> validate(ResourceModel item) {
        Set<ConstraintViolation> constraints = parent.validate(item);
        if (constraints == null) {
          constraints = new HashSet<ConstraintViolation>();
        }
        Map<String, ResourceParameter> params = item.getParametersMap();
        ResourceParameter param = params.get("archiveType");
        String value = param.getValue();
        if (value == null || value.equals("")) {
          ConstraintViolation constraint = new ConstraintViolation();
          constraint.setMessage("An Archive Type must be defined");
          constraint.setLevel(ConstraintViolationLevel.CRITICAL);
          constraint.setValueName(param.getName());
          constraints.add(constraint);
        }

        return constraints;
      }
    };
  }

}

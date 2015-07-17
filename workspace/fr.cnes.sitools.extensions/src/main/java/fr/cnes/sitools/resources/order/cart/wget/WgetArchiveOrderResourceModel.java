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
/**
 * 
 */
package fr.cnes.sitools.resources.order.cart.wget;

import java.util.Set;

import fr.cnes.sitools.common.validator.ConstraintViolation;
import fr.cnes.sitools.common.validator.Validator;
import fr.cnes.sitools.plugins.resources.model.ResourceModel;
import fr.cnes.sitools.plugins.resources.model.ResourceParameter;
import fr.cnes.sitools.plugins.resources.model.ResourceParameterType;
import fr.cnes.sitools.project.ProjectApplication;
import fr.cnes.sitools.resources.order.cart.common.AbstractCartOrderResourceModel;
import fr.cnes.sitools.tasks.model.TaskResourceModel;
import fr.cnes.sitools.tasks.model.TaskRunTypeAdministration;

/**
 * @author tx.chevallier
 * 
 * @version
 * 
 */
public class WgetArchiveOrderResourceModel extends AbstractCartOrderResourceModel {

  public WgetArchiveOrderResourceModel() {

    super();

    setResourceImplClassName("fr.cnes.sitools.resources.order.cart.wget.WgetArchiveOrderResource");

    setClassAuthor("Akka Technologies");
    setClassOwner("CNES");
    setClassVersion("0.1");
    setName("WgetArchiveOrderResourceModel");
    setDescription("Order resources associated to metadata, copying all files to a userstorage");

    this.setApplicationClassName(ProjectApplication.class.getName());
    this.getParameterByName("methods").setValue("POST");

    this.getParameterByName(TaskResourceModel.RUN_TYPE_PARAM_NAME_ADMINISTATION).setValue(
        TaskRunTypeAdministration.TASK_FORCE_RUN_ASYNC.toString());
    
    ResourceParameter archiveType = new ResourceParameter("archiveType", "The type of archive needed",
        ResourceParameterType.PARAMETER_USER_INPUT);
    archiveType.setValueType("xs:enum[zip,tar.gz,tar]");
    this.addParam(archiveType);

  }

  @Override
  public Validator<ResourceModel> getValidator() {

    final Validator<ResourceModel> parent = super.getValidator();

    return new Validator<ResourceModel>() {
      @Override
      public Set<ConstraintViolation> validate(ResourceModel item) {
        return parent.validate(item);
      }
    };
  }

}

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
package fr.cnes.sitools.resources.order.cart;

import java.util.HashSet;
import java.util.Set;

import fr.cnes.sitools.common.validator.ConstraintViolation;
import fr.cnes.sitools.common.validator.Validator;
import fr.cnes.sitools.plugins.resources.model.ResourceModel;
import fr.cnes.sitools.tasks.TaskUtils;
import fr.cnes.sitools.tasks.model.TaskResourceModel;
import fr.cnes.sitools.tasks.model.TaskRunTypeAdministration;

public class CartOrderResourceModel extends TaskResourceModel {
  
  

  /**
   * Constructor
   */
  public CartOrderResourceModel() {

    super();
    setClassAuthor("AKKA Technologies");
    setClassOwner("CNES");
    setClassVersion("0.1");
    setName("CartOrderResourceModel");
    setDescription("Cart Order resources associated to metadata and save it in user storage. (Can also create a ZIP, TAR or TAR.GZ 'on the fly')");
    /** Resource facade */
    setResourceClassName("fr.cnes.sitools.resources.order.cart.CartOrderResourceFacade");
    /** Resource d'implémentation */
    setResourceImplClassName("fr.cnes.sitools.resources.order.cart.CartOrderResource");

    setRunTypeAdministration(TaskRunTypeAdministration.TASK_DEFAULT_RUN_ASYNC);

    this.getParameterByName("methods").setValue("POST");

    this.getParameterByName("fileName").setValue("cart_order_" + "${date:" + TaskUtils.getTimestampPattern()+"}");

  }

  @Override
  public Validator<ResourceModel> getValidator() {
    return new Validator<ResourceModel>() {

      @Override
      public Set<ConstraintViolation> validate(ResourceModel item) {
        Set<ConstraintViolation> constraints = new HashSet<ConstraintViolation>();
        return constraints;
      }
    };
  }


}

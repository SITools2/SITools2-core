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
package fr.cnes.sitools.resources.tasks.test;

import fr.cnes.sitools.plugins.resources.model.ResourceParameter;
import fr.cnes.sitools.plugins.resources.model.ResourceParameterType;
import fr.cnes.sitools.tasks.model.TaskResourceModel;
import fr.cnes.sitools.tasks.model.TaskRunTypeAdministration;

/**
 * TaskResource Model for tests
 * 
 * 
 * @author m.gond
 */
public class MyTaskResourceFacadeModel extends TaskResourceModel {

  /**
   * Constructor
   */
  public MyTaskResourceFacadeModel() {

    super();
    setClassAuthor("AKKA Technologies");
    setClassOwner("CNES");
    setClassVersion("0.1");
    setName("MyTaskResourceFacadeModel");
    setDescription("MyTaskResourceFacadeModel");
    setResourceClassName(MyTaskResourceFacade.class.getName());
    setResourceImplClassName(MyTaskResourceImpl.class.getName());

    setRunTypeAdministration(TaskRunTypeAdministration.TASK_DEFAULT_RUN_SYNC);

    // parameter used as test to wait a bit while calling the resource
    // asynchronously
    ResourceParameter param = new ResourceParameter("async",
        "true if the resource is call asynchronously, false otherwise", ResourceParameterType.PARAMETER_INTERN);
    param.setValue("false");
    this.addParam(param);

    // parameter used as test to send an error
    ResourceParameter paramError = new ResourceParameter("error",
        "true if the resource sends an error, false otherwise", ResourceParameterType.PARAMETER_INTERN);
    param.setValue("false");
    this.addParam(paramError);

    // parameter for the message to send because of an error
    ResourceParameter paramErrorMessage = new ResourceParameter("error_message",
        "The error message to send in case of an error", ResourceParameterType.PARAMETER_INTERN);
    param.setValue("Error message");
    this.addParam(paramErrorMessage);
  }
}

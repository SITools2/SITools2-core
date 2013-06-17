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
package fr.cnes.sitools.resources.programs;

import fr.cnes.sitools.plugins.resources.model.DataSetSelectionType;
import fr.cnes.sitools.plugins.resources.model.ResourceModel;
import fr.cnes.sitools.plugins.resources.model.ResourceParameter;
import fr.cnes.sitools.plugins.resources.model.ResourceParameterType;

/**
 * Model for the command line service resource
 * 
 * @author jp.boignard (AKKA Technologies)
 *
 */
public class CommandLineServiceModel extends ResourceModel {
  
  /**
   * Constructor
   */
  public CommandLineServiceModel() {
    
    super();
    
    setName("CommandLineServiceModel");
    setDescription("Execute a external command like a script, batch or command system.");
    setClassAuthor("AKKA Technologies");
    setClassOwner("CNES");
    setClassVersion("0.1");
    setResourceClassName("fr.cnes.sitools.resources.programs.CommandLineService");
    
    setDataSetSelection(DataSetSelectionType.NONE);

    ResourceParameter url = new ResourceParameter("url", "Attachment URL", ResourceParameterType.PARAMETER_ATTACHMENT);
    
    /**
     * Parameter for analog directory
     */
    ResourceParameter command = new ResourceParameter("command", "Command", ResourceParameterType.PARAMETER_INTERN);
   
    /**
     * Parameter for analog directory
     */
    ResourceParameter media = new ResourceParameter("media", "Result media type", ResourceParameterType.PARAMETER_INTERN);
    
    
    /**
     * Parameter for directory
     */
    ResourceParameter outputDir = new ResourceParameter("outputDir", "Output directory", ResourceParameterType.PARAMETER_INTERN);
    
    
    /**
     * Parameter for analog directory
     */
    ResourceParameter outputFile = new ResourceParameter("outputFile", "Output file", ResourceParameterType.PARAMETER_INTERN);
    
//    /**
//     * Parameter for result type
//     */
//    ParameterizedResourcesParameter resultType = new ParameterizedResourcesParameter("resultType", "Result type", ParameterizedResourcesParameterType.PARAMETER_USER_INPUT);
//    
    /**
     * Adding parameters
     */
    addParam(url);
    addParam(command);
    addParam(media);
    addParam(outputDir);
    addParam(outputFile);
    // addParam(resultType);
    
  }

}

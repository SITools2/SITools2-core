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
package fr.cnes.sitools.resources.programs;

import fr.cnes.sitools.plugins.resources.model.DataSetSelectionType;
import fr.cnes.sitools.plugins.resources.model.ResourceModel;
import fr.cnes.sitools.plugins.resources.model.ResourceParameter;
import fr.cnes.sitools.plugins.resources.model.ResourceParameterType;

/**
 * Model for the Analog service resource
 * 
 * @author m.marseille (AKKA Technologies)
 */
public class SitoolsAnalogServiceModel extends ResourceModel {

  /**
   * Constructor
   */
  public SitoolsAnalogServiceModel() {

    super();

    setName("AnalogService");
    setDescription("Analog service producing an HTML log analysis report.");
    setClassAuthor("AKKA Technologies");
    setClassOwner("CNES");
    setClassVersion("0.2");
    setResourceClassName("fr.cnes.sitools.resources.programs.SitoolsAnalogService");
    setDataSetSelection(DataSetSelectionType.NONE);
    // ResourceParameter url = new ResourceParameter("url", "Attachment URL",
    // ResourceParameterType.PARAMETER_ATTACHMENT);

    /**
     * Parameter for analog directory
     */
    ResourceParameter analogExe = new ResourceParameter("analogexe", "Absolute path to the analog executable",
        ResourceParameterType.PARAMETER_INTERN);
    analogExe.setValueType("xs:path");

    /**
     * Parameter for the log directory
     */
    ResourceParameter logDir = new ResourceParameter("logdir", "Log directory", ResourceParameterType.PARAMETER_INTERN);
    logDir.setValueType("xs:path");
    /**
     * Parameter for output
     */
    ResourceParameter outputDir = new ResourceParameter("outputdir", "Output directory",
        ResourceParameterType.PARAMETER_INTERN);
    outputDir.setValueType("xs:path");

    /**
     * Parameter for output
     */
    ResourceParameter outputUrl = new ResourceParameter("outputurl", "Url of result",
        ResourceParameterType.PARAMETER_INTERN);
    outputUrl.setValueType("xs:url");

    /**
     * Parameter for analog images directory
     */
    ResourceParameter imageUrl = new ResourceParameter("imageurl", "Analog images url",
        ResourceParameterType.PARAMETER_INTERN);

    this.getParameterByName("url").setValue("/plugin/analog");

    /**
     * Added parameters
     */
    addParam(analogExe);
    addParam(logDir);
    addParam(outputDir);
    addParam(outputUrl);
    addParam(imageUrl);
  }

}

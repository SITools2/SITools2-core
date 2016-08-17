 /*******************************************************************************
 * Copyright 2010-2016 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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
 * Model for program resources
 * @author m.marseille (AKKA Technologies)
 */
public class UlisseTalendJobResourceModel extends ResourceModel {
  
  /**
   * Constructor
   */
  public UlisseTalendJobResourceModel() {
    
    super();
    
    setName("ProgramResourceModel");
    setDescription("Program resource model");
    setClassVersion("0.3");
    setClassAuthor("AKKA Technologies");
    setClassOwner("CNES");
    setResourceClassName("fr.cnes.sitools.resources.programs.UlisseTalendJobResource");
    setDataSetSelection(DataSetSelectionType.NONE);
    /**
     * Parameter for attachment
     */
    ResourceParameter url = new ResourceParameter();
    url.setName("url");
    url.setDescription("Attachment URL");
    url.setType(ResourceParameterType.PARAMETER_ATTACHMENT);
    url.setValue("/program");
    url.setValueType("string");
    
    /**
     * Parameter for command line
     */
    ResourceParameter cmdLine = new ResourceParameter();
    cmdLine.setName("cmdline");
    cmdLine.setDescription("The command line to execute");
    cmdLine.setType(ResourceParameterType.PARAMETER_INTERN);
    cmdLine.setValue("MainJob_run.bat");
    cmdLine.setValueType("string");
    
    /**
     * Parameter for arguments
     */
    ResourceParameter workdir = new ResourceParameter();
    workdir.setName("workdir");
    workdir.setDescription("Working directory");
    workdir.setType(ResourceParameterType.PARAMETER_INTERN);
    workdir.setValue("");
    workdir.setValueType("path");
    
    /**
     * Parameter for arguments
     */
    ResourceParameter basedir = new ResourceParameter();
    basedir.setName("basedir");
    basedir.setDescription("Base directory for all ULISSE .xml files");
    basedir.setType(ResourceParameterType.PARAMETER_INTERN);
    basedir.setValue("");
    basedir.setValueType("path");
    
    /**
     * Parameter for arguments
     */
    ResourceParameter homedir = new ResourceParameter();
    homedir.setName("homedir");
    homedir.setDescription("Home directory");
    homedir.setType(ResourceParameterType.PARAMETER_INTERN);
    homedir.setValue("");
    homedir.setValueType("path");
    
    /**
     * Parameter for arguments
     */
    ResourceParameter xmlbase = new ResourceParameter();
    xmlbase.setName("xmlbase");
    xmlbase.setDescription("XML instance base file");
    xmlbase.setType(ResourceParameterType.PARAMETER_INTERN);
    xmlbase.setValue("");
    xmlbase.setValueType("path");
    
    /**
     * Parameter for arguments
     */
    ResourceParameter dbServerUrl = new ResourceParameter("dburl", "Server URL", ResourceParameterType.PARAMETER_INTERN);
    ResourceParameter dbServerPort = new ResourceParameter("dbport", "Server Port", ResourceParameterType.PARAMETER_INTERN);
    ResourceParameter dbServerBase = new ResourceParameter("dbbase", "Server Base", ResourceParameterType.PARAMETER_INTERN);
    ResourceParameter dbServerSchema = new ResourceParameter("dbschema", "Server Schema", ResourceParameterType.PARAMETER_INTERN);
    ResourceParameter dbServerLogin = new ResourceParameter("dblogin", "Server Login", ResourceParameterType.PARAMETER_INTERN);
    ResourceParameter dbServerPassword = new ResourceParameter("dbpasswd", "Server Password", ResourceParameterType.PARAMETER_INTERN);
    
    /**
     * Adding parameters to the model
     */
    addParam(url);
    addParam(cmdLine);
    addParam(workdir);
    addParam(basedir);
    addParam(homedir);
    addParam(xmlbase);
    addParam(dbServerUrl);
    addParam(dbServerPort);
    addParam(dbServerBase);
    addParam(dbServerSchema);
    addParam(dbServerLogin);
    addParam(dbServerPassword);
    
  }

}

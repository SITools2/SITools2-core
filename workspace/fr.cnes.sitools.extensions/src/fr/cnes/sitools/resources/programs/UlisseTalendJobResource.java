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
package fr.cnes.sitools.resources.programs;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import org.restlet.data.MediaType;
import org.restlet.ext.wadl.MethodInfo;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;

import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.common.resource.SitoolsParameterizedResource;

/**
 * Program resource to launch programs
 * 
 * @author m.marseille (AKKA Technologies)
 * 
 */
public class UlisseTalendJobResource extends SitoolsParameterizedResource {

  /**
   * Resource program status
   */
  private String status = "READY";

  /**
   * Action on the command line : start/stop/status
   */
  private String action;

  /**
   * Command log
   */
  private String commandLog;

  /**
   * Option : nowait (default) | wait => no response until job ends
   */
  private String option;

  /**
   * Exit value
   */
  private Integer exitValue;

  @Override
  public void doInit() {
    super.doInit();
    action = (String) getQuery().getFirstValue("action");
    option = (String) getQuery().getFirstValue("option");
  }

  @Get
  @Override
  public Representation get() {
    Response response = null;
    if (action != null && action.equalsIgnoreCase("start")) { // Start handling
      if (!status.equals("RUNNING")) { // Not already running

        status = "STARTING";
        Runtime rt = Runtime.getRuntime();

        // Getting parameters
        String cmd = "\"" + getModel().getParametersMap().get("workdir").getValue();
        cmd += "\\" + getModel().getParametersMap().get("cmdline").getValue() + "\"";

        // Working directory (where is the script)
        File dir = new File(getModel().getParametersMap().get("workdir").getValue());

        // Arguments
        String[] contextParamsNames = {"BASE_DIRECTORY", "HOME_PATH", "MAIN_INSTANCE_PATH", "odysseus2_Server",
          "odysseus2_Port", "odysseus2_Database", "odysseus2_Schema", "odysseus2_Login", "odysseus2_Password"};
        
        int i = 0;
        String param = getModel().getParametersMap().get("basedir").getValue();
        if (param != null && !param.equals("")) {
          cmd += " --context_param " + contextParamsNames[i++] + "=" + param;
        }
        param = getModel().getParametersMap().get("homedir").getValue();
        if (param != null && !param.equals("")) {
          cmd += " --context_param " + contextParamsNames[i++] + "=" + param;
        }
        param = getModel().getParametersMap().get("xmlbase").getValue();
        if (param != null && !param.equals("")) {
          cmd += " --context_param " + contextParamsNames[i++] + "=" + param;
        }
        param = getModel().getParametersMap().get("dburl").getValue();
        if (param != null && !param.equals("")) {
          cmd += " --context_param " + contextParamsNames[i++] + "=" + param;
        }
        param = getModel().getParametersMap().get("dbport").getValue();
        if (param != null && !param.equals("")) {
          cmd += " --context_param " + contextParamsNames[i++] + "=" + param;
        }
        param = getModel().getParametersMap().get("dbbase").getValue();
        if (param != null && !param.equals("")) {
          cmd += " --context_param " + contextParamsNames[i++] + "=" + param;
        }
        param = getModel().getParametersMap().get("dbschema").getValue();
        if (param != null && !param.equals("")) {
          cmd += " --context_param " + contextParamsNames[i++] + "=" + param;
        }
        param = getModel().getParametersMap().get("dblogin").getValue();
        if (param != null && !param.equals("")) {
          cmd += " --context_param " + contextParamsNames[i++] + "=" + param;
        }
        param = getModel().getParametersMap().get("dbpasswd").getValue();
        if (param != null && !param.equals("")) {
          cmd += " --context_param " + contextParamsNames[i++] + "=" + param;
        }
        commandLog += cmd + "\n";

        // Launching
        try {
          Process pr = rt.exec(cmd, null, dir);
          if (option != null && option.equals("wait")) {
            BufferedReader input = new BufferedReader(new InputStreamReader(pr.getInputStream()));
            String line = null;
            while ((line = input.readLine()) != null) {
              commandLog += line + "\n";
              status = "RUNNING";
            }
            exitValue = pr.waitFor();
            if (exitValue == 0) {
              response = new Response(true, commandLog);
            }
            else {
              response = new Response(false, commandLog);
            }
            status = "READY";
            return getRepresentation(response, MediaType.APPLICATION_XML);
          }
        }
        catch (IOException e) {
          getLogger().warning(e.getMessage());
          response = new Response(false, "program.execution.failed");
        }
        catch (InterruptedException e) {
          getLogger().warning(e.getMessage());
          response = new Response(false, "program.execution.failed");
        }
        response = new Response(false, "program.execution.sent");
        return getRepresentation(response, MediaType.APPLICATION_XML);
      }
      else { // Already running
        response = new Response(false, "program.already.running");
        return getRepresentation(response, MediaType.APPLICATION_XML);
      }

    }
    response = new Response(false, "error.action.missing");
    return getRepresentation(response, MediaType.APPLICATION_XML);
  }

  @Override
  public void sitoolsDescribe() {
    setName("ProgramResource");
    setDescription("Resource for program start / stop / status");
    setNegotiated(false);
  }
  
  @Override
  protected void describeGet(MethodInfo info) {
    this.addInfo(info);
  }

}

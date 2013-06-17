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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;

import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.ext.wadl.MethodInfo;
import org.restlet.representation.FileRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Get;
import org.restlet.resource.Put;
import org.restlet.resource.ResourceException;

import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.common.resource.SitoolsParameterizedResource;
import fr.cnes.sitools.util.Util;

/**
 * Sitools analysis log service with Analog
 * 
 * @author m.marseille (AKKA Technologies)
 * 
 */
public class SitoolsAnalogService extends SitoolsParameterizedResource {

  @Override
  public void sitoolsDescribe() {
    setName("AnalogService");
    setDescription("Resource for program Analog");
    setNegotiated(false);
  }

  @Override
  public void doInit() {
    super.doInit();
  }

  @Get
  @Override
  public Representation get() {

    String paramOutputDir = getOverrideParameterValue("outputdir");

    String outputUrl = getOverrideParameterValue("outputurl");
    if (Util.isNotEmpty(outputUrl)) {
      getResponse().redirectTemporary(outputUrl);
      // getResponse().setLocationRef(outputUrl);
      // throw new ResourceException(Status.REDIRECTION_FOUND);
      throw new ResourceException(Status.REDIRECTION_TEMPORARY);
    }

    FileRepresentation repr = new FileRepresentation(paramOutputDir + "/report.html", MediaType.TEXT_HTML);
    return repr;
  }

  @Override
  protected void describeGet(MethodInfo info) {
    this.addInfo(info);
    info.setDocumentation("Method to retrieve a previously generated report");
    info.setIdentifier("retrieve_report");
    addStandardGetRequestInfo(info);
    addStandardResponseInfo(info);
    addStandardInternalServerErrorInfo(info);
  }

  /**
   * reportRepresentation method
   * 
   * @param entity
   *          no Representation needed for that Method
   * @param variant
   *          the Variant needed in return
   * @return A {@link Representation} of a {@link Response} indicating whether or not the report was generated
   */
  @Put
  public Representation generateReport(Representation entity, Variant variant) {
    Response response = null;
    Runtime rt = Runtime.getRuntime();

    // Getting parameters
    String cmd = getOverrideParameterValue("analogexe");
    ArrayList<String> params = new ArrayList<String>();
    params.add(cmd);

    // Arguments
    String param = getOverrideParameterValue("logdir");
    // System.out.println("CLOGFILE = " + param);
    if (param != null && !param.equals("")) {
      params
          .add("+CLOGFORMAT (%Y-%m-%dT%h:%n:%j\\t%j\\t%S\\t%u\\t%j\\t%j\\t%j\\t%r\\t%q\\t%c\\t%b\\t%j\\t%T\\t%v\\t%B\\t%f)");
      params.add("+CLOGFILE " + param);
    }
    else {
      response = new Response(false, "program.missing.parameter");
      return getRepresentation(response, variant);
    }

    String paramImageDir = getOverrideParameterValue("imageurl");
    if (paramImageDir != null && !paramImageDir.equals("")) {
      params.add("+CIMAGEDIR '" + paramImageDir + "'");
    }
    else {
      response = new Response(false, "program.missing.parameter");
      return getRepresentation(response, variant);
    }

    String paramOutputDir = getOverrideParameterValue("outputdir");

    params.add("+CLOCALCHARTDIR " + paramOutputDir + "/");

    File outputdir = new File(paramOutputDir);
    if (!outputdir.exists()) {
      response = new Response(false, "program.parameter.output.notfound");
      return getRepresentation(response, variant);
    }
    if (!outputdir.isDirectory()) {
      response = new Response(false, "program.parameter.output.notdirectory");
      return getRepresentation(response, variant);
    }

    File outputfile = new File(outputdir, "report.html");

    String outputUrl = getOverrideParameterValue("outputurl");
    if (Util.isNotEmpty(outputUrl)) {
      Reference ref = new Reference(outputUrl);
      ref.setLastSegment("");
      params.add("+CCHARTDIR " + ref);

    }
    params.add("+CHOSTNAME SITOOLS2");
    params.add("+CHOSTURL 'none'");
    params.add("+CALLCHART ON");
    params.add("+CPAGEINCLUDE *");

    params.add("+COUTFILE '" + outputfile.getPath() + "'");

    // reports list
    // get all reports
    params.add("+CALL ON");
    // remove some of the reports which are unwanted
    // cf http://www.analog.cx/docs/output.html for the list of reports
    params.add("+CQUARTERREP OFF");
    params.add("+CQUARTERSUM OFF");
    params.add("+CFIVEREP OFF");
    params.add("+CFIVESUM OFF");
    params.add("+CREDIR OFF");
    params.add("+CREDIRREF OFF");
    params.add("+CFAILREF OFF");
    params.add("+CVHOST OFF");
    params.add("+CREDIRVHOST OFF");
    params.add("+CHOURLYREP OFF");

    params.add("+CDEBUG ON");
    params.add("+COUTPUT XHTML");

    String commandLog = cmd + "\n";

    getLogger().log(Level.INFO, "ANALOG COMMAND : " + cmd);

    // Launching
    try {
      Process pr = rt.exec(params.toArray(new String[0]), null, null);

      // Log the InputStream and the ErrorStream using a StreamLogGobbler
      StreamLogGobbler outputGobbler = new StreamLogGobbler(pr.getInputStream(), getLogger(), Level.INFO);
      StreamLogGobbler errorGobbler = new StreamLogGobbler(pr.getErrorStream(), getLogger(), Level.WARNING);
      outputGobbler.start();
      errorGobbler.start();

      int exitValue = pr.waitFor();
      if (exitValue == 0) {
        response = new Response(true, "program.execution.success");
      }
      else {
        response = new Response(false, commandLog);
      }
      return getRepresentation(response, variant);
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
    return getRepresentation(response, variant);
  }

  @Override
  public void describePut(MethodInfo info) {
    info.setDocumentation("Method to generate an Analog report");
    info.setIdentifier("generate_report");
    addStandardPostOrPutRequestInfo(info);
    addStandardResponseInfo(info);
    addStandardInternalServerErrorInfo(info);
  }

}

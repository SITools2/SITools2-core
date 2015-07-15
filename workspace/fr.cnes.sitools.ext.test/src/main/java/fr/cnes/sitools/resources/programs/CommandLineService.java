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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

import org.restlet.data.MediaType;
import org.restlet.ext.wadl.MethodInfo;
import org.restlet.representation.FileRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;

import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.common.resource.SitoolsParameterizedResource;

/**
 * Generic Command line service incubator
 * 
 * TODO Improving file management (without files in pure streaming ?) TODO Unit testing
 * 
 * @author jp.boignard (AKKA Technologies)
 * 
 */
public class CommandLineService extends SitoolsParameterizedResource {

  /**
   * Action = start to execute the program
   */
  private String action;

  /**
   * Command log
   */
  private String command = "";

  @Override
  public void doInit() {
    super.doInit();
    action = (String) getQuery().getFirstValue("action");
  }

  @Override
  public void sitoolsDescribe() {
    setName("ProgramResource");
    setDescription("Resource for program start / stop / status");
    setNegotiated(false);
  }

  /**
   * Transform request text in result
   * 
   * @param rep
   *          a Representation
   * @return FileRepresentation
   */
  @Override
  @Post
  public Representation post(Representation rep) {
    if (action != null && action.equalsIgnoreCase("start")) { // Start handling
      return doStart();
    }
    else {
      return new StringRepresentation("You must use POST ?action=start to execute this command", MediaType.TEXT_ALL);
    }
  }

  @Override
  @Get
  public Representation get() {
    if (action != null && action.equalsIgnoreCase("start")) { // Start handling
      return doStart();
    }
    else {
      return new StringRepresentation("You must use GET ?action=start to execute this command", MediaType.TEXT_ALL);
    }
  }

  /**
   * doStart method
   * 
   * @return Representation
   */
  protected Representation doStart() {
    Response response = null;

    Runtime rt = Runtime.getRuntime();

    // Getting parameters
    String cmd = getModel().getParametersMap().get("command").getValue();

    // Output file
    String outputDir = getModel().getParametersMap().get("outputDir").getValue();
    String outputFile = getModel().getParametersMap().get("outputFile").getValue();
    String resultFile = outputFile.replaceAll("\\$\\{random}", String.valueOf(new Random().nextInt()));

    final File result = new File(outputDir, resultFile);
    final File resultErr = new File(outputDir, resultFile + ".txt");

    File dir = new File(outputDir);
    // Output media type
    MediaType resultMedia = MediaType.valueOf(getModel().getParametersMap().get("media").getValue());

    final byte[] cmdbytes = (cmd + "\n").getBytes();
    try {
      // Launching
      final Process process = rt.exec(cmd, null, dir);

      // Consommation de la sortie standard de l'application externe dans un
      // Thread separe
      new Thread() {
        @Override
        public void run() {
          try {
            byte[] buf = new byte[1024];
            int len;
            FileOutputStream output = new FileOutputStream(result);
            InputStream inputStream = process.getInputStream();
            while ((len = inputStream.read(buf)) > 0) {
              output.write(buf, 0, len);
            }
            output.close();
            inputStream.close();
          }
          catch (IOException ioe) {
            ioe.printStackTrace();
          }
        }
      }.start();

      // Consommation de la sortie d'erreur de l'application externe dans un
      // Thread separe
      new Thread() {
        public void run() {

          try {
            byte[] buf = new byte[1024];
            int len;
            FileOutputStream output = new FileOutputStream(resultErr);
            output.write(cmdbytes);
            InputStream inputStream = process.getErrorStream();
            while ((len = inputStream.read(buf)) > 0) {
              output.write(buf, 0, len);
            }
            output.close();
            inputStream.close();
          }
          catch (IOException ioe) {
            ioe.printStackTrace();
          }
        }
      }.start();

      // Wait the end of process to build and return the resource
      // Representation.
      int exitValue = process.waitFor();
      // OK and output file exists and not empty
      if ((exitValue == 0) && result.exists() && (result.length() > 0)) {
        FileRepresentation repr = new FileRepresentation(result, resultMedia);
        return repr;
      }
      else {
        // NOT and err output file exists and not empty
        if (resultErr.exists() && (resultErr.length() > 0)) {
          FileRepresentation repr = new FileRepresentation(resultErr, MediaType.TEXT_ALL);
          return repr;
        }
        else {
          response = new Response(exitValue == 0, command + " \n status:" + String.valueOf(exitValue) + "\n");
        }
      }
      return getRepresentation(response, MediaType.APPLICATION_XML);
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

  @Override
  protected void describeGet(MethodInfo info) {
    this.addInfo(info);
  }

  @Override
  protected void describePost(MethodInfo info) {
    this.addInfo(info);
  }

}

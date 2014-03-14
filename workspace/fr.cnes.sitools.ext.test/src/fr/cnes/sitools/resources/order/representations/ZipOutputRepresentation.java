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
package fr.cnes.sitools.resources.order.representations;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.restlet.Context;
import org.restlet.data.ClientInfo;
import org.restlet.data.Disposition;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.representation.OutputRepresentation;
import org.restlet.representation.Representation;

import fr.cnes.sitools.common.exception.SitoolsException;
import fr.cnes.sitools.resources.order.utils.ListReferencesAPI;
import fr.cnes.sitools.resources.order.utils.OrderResourceUtils;

/**
 * Representation used to create a Zip Archive from a List of {@link Reference}
 * pointing to some files
 * 
 * 
 * @author m.gond
 */
public class ZipOutputRepresentation extends OutputRepresentation {
  /**
   * the list of {@link Reference}
   */
  private List<Reference> references;
  /** The clientInfo */
  private ClientInfo clientInfo;
  /** The Context */
  private Context context;
  /** The source reference map */
  private Map<Reference, String> refMap;

  /**
   * Create a new {@link ZipOutputRepresentation}
   * 
   * @param referencesSource
   *          the list of {@link Reference} pointing the file to add to the zip
   *          archive
   * @param clientInfo
   *          the clientInfo of the current user
   * @param context
   *          the Context
   * @param fileName
   *          the complete fileName with extension
   */
  public ZipOutputRepresentation(List<Reference> referencesSource, ClientInfo clientInfo, Context context,
      String fileName) {
    super(MediaType.APPLICATION_ZIP);
    references = referencesSource;
    this.clientInfo = clientInfo;
    this.context = context;

    Disposition disp = new Disposition(Disposition.TYPE_ATTACHMENT);
    disp.setFilename(fileName);
    this.setDisposition(disp);
  }

  /**
   * 
   * @param refListAPI
   * @param clientInfo
   * @param context
   * @param fileName
   */
  public ZipOutputRepresentation(ListReferencesAPI refListAPI, ClientInfo clientInfo, Context context, String fileName) {
    super(MediaType.APPLICATION_ZIP);
    references = refListAPI.getReferencesSource();
    refMap = refListAPI.getRefSourceTarget();
    this.clientInfo = clientInfo;
    this.context = context;

    Disposition disp = new Disposition(Disposition.TYPE_ATTACHMENT);
    disp.setFilename(fileName);
    this.setDisposition(disp);
  }

  @Override
  public void write(OutputStream outputStream) throws IOException {

    try {
      // create a new ZipOutputStream

      ZipOutputStream zipOutput = new ZipOutputStream(outputStream);
      // loop through the References

      long totalArchiveSize = 0;
      int buffersize = 1024;
      byte[] buf = new byte[buffersize];
      for (Reference reference : references) {
        if (reference.getLastSegment() != null) {
          try {
            System.out.println("WRITE : " + reference + " into zip file");
            // try to get the file
            Representation repr = OrderResourceUtils.getFile(reference, clientInfo, context);
            // create a new ZipEntry with the name of the entry

            ZipEntry zipEntry;
            if (refMap != null) {
              if (refMap.get(reference) != null) {
                zipEntry = new ZipEntry((String) refMap.get(reference) + "/" + reference.getLastSegment());
              }
              else {
                zipEntry = new ZipEntry(reference.getLastSegment());
              }
            }
            else {
              zipEntry = new ZipEntry(reference.getLastSegment());
            }
            zipOutput.putNextEntry(zipEntry);
            InputStream stream = null;
            totalArchiveSize += zipEntry.getSize();

            try {
              // get the stream of the File, read it and write it to
              // the Zip stream
              stream = repr.getStream();
              int count = 0;
              while ((count = stream.read(buf, 0, buffersize)) != -1) {
                zipOutput.write(buf, 0, count);
              }

            }
            catch (Exception e) {
              System.out.println("TA MERE");
              e.printStackTrace();
            }
            finally {
              // close the entry and the file stream
              zipOutput.closeEntry();
              if (stream != null) {
                stream.close();
              }
            }
          }
          catch (SitoolsException ex) {
            // if the file canno't be found, log the error and go to the
            // next file
            context.getLogger().log(Level.INFO, "File " + reference + "canno't be found", ex);
          }
        }
      }
      // Log the zip estimated size
      context.getLogger().info("Total size (estimated) : " + totalArchiveSize + "kB");
      // close the zip stream
      zipOutput.close();
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }
}

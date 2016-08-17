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
package fr.cnes.sitools.resources.order.representations;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.zip.GZIPOutputStream;

import org.apache.tools.tar.TarEntry;
import org.apache.tools.tar.TarOutputStream;
import org.restlet.Context;
import org.restlet.data.ClientInfo;
import org.restlet.data.Disposition;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.representation.OutputRepresentation;
import org.restlet.representation.Representation;

//import com.ice.tar.TarEntry;
//import com.ice.tar.TarOutputStream;
import fr.cnes.sitools.common.exception.SitoolsException;
import fr.cnes.sitools.resources.order.utils.ListReferencesAPI;
import fr.cnes.sitools.resources.order.utils.OrderResourceUtils;

/**
 * Representation used to create a tar or a tar.gz Archive from a List of
 * {@link Reference} pointing to some files
 * 
 * 
 * @author m.gond
 */
public class TarOutputRepresentation extends OutputRepresentation {
  /** the list of {@link Reference} */
  private List<Reference> references;
  /** The clientInfo */
  private ClientInfo clientInfo;
  /** The Context */
  private Context context;
  /** Whether or not to use gzip compression on the tar */
  private boolean gzip;
  /** The source reference map */
  private Map<Reference, String> refMap;

  /**
   * Create a new {@link TarOutputRepresentation}
   * 
   * @param referencesSource
   *          the list of {@link Reference} pointing the file to add to the zip
   *          archive
   * @param clientInfo
   *          the clientInfo of the current user
   * @param context
   *          the Context * @param fileName the complete fileName with extension
   * @param gzip
   *          Whether or not to use gzip compression on the tar
   */
  public TarOutputRepresentation(List<Reference> referencesSource, ClientInfo clientInfo, Context context,
      String fileName, boolean gzip) {
    super(MediaType.APPLICATION_TAR);
    references = referencesSource;
    this.clientInfo = clientInfo;
    this.context = context;
    this.gzip = gzip;

    Disposition disp = new Disposition(Disposition.TYPE_ATTACHMENT);
    disp.setFilename(fileName);
    this.setDisposition(disp);
  }

  /**
   * TarOutputRepresentation
   * 
   * @param refListAPI
   * @param clientInfo
   * @param context
   * @param fileName
   * @param gzip
   */
  public TarOutputRepresentation(ListReferencesAPI refListAPI, ClientInfo clientInfo, Context context, String fileName,
      boolean gzip) {
    super(MediaType.APPLICATION_TAR);
    references = refListAPI.getReferencesSource();
    refMap = refListAPI.getRefSourceTarget();
    this.clientInfo = clientInfo;
    this.context = context;
    this.gzip = gzip;

    Disposition disp = new Disposition(Disposition.TYPE_ATTACHMENT);
    disp.setFilename(fileName);
    this.setDisposition(disp);
  }

  @Override
  public void write(OutputStream outputStream) throws IOException {
    // create a new TarOutputStream, if gzip, the stream is also compressed with
    // GZIPOutputStream
    TarOutputStream tarOutput;

    if (gzip) {
      tarOutput = new TarOutputStream(new GZIPOutputStream(outputStream));
    }
    else {
      tarOutput = new TarOutputStream(outputStream);
    }

    tarOutput.setLongFileMode(TarOutputStream.LONGFILE_GNU);
    // loop through the References
    long totalArchiveSize = 0;
    int buffersize = 1024;
    byte[] buf = new byte[buffersize];
    for (Reference reference : references) {
      if (reference.getLastSegment() != null) {
        try {
          // try to get the file
          Representation repr = OrderResourceUtils.getFile(reference, clientInfo, context);
          long fileSize = repr.getSize();
          totalArchiveSize += fileSize;

          // create a new TarEntry with the name of the entry
          TarEntry tarEntry;
          if (refMap != null) {
            if (refMap.get(reference) != null) {
              tarEntry = new TarEntry((String) refMap.get(reference) + "/" + reference.getLastSegment());
            }
            else {
              tarEntry = new TarEntry(reference.getLastSegment());
            }
          }
          else {
            tarEntry = new TarEntry(reference.getLastSegment());
          }

          // Set the tarEntry size with the same size as the file got before
          tarEntry.setSize(fileSize);
          tarOutput.putNextEntry(tarEntry);
          InputStream stream = null;

          try {
            int count = 0;
            // get the stream of the File, read it and write it to the Tar
            // stream
            stream = repr.getStream();
            while ((count = stream.read(buf, 0, buffersize)) != -1) {
              tarOutput.write(buf, 0, count);
            }
          }
          finally {
            // close the entry and the file stream
            tarOutput.closeEntry();
            if (stream != null) {
              stream.close();
            }
          }
        }
        catch (SitoolsException ex) {
          // if the file canno't be found, log the error and go to the next file
          context.getLogger().log(Level.INFO, "File " + reference + "canno't be found", ex);
        }
      }
    }
    // Log the zip estimated size
    context.getLogger().info("Total size (estimated, uncompressed) : " + totalArchiveSize + "kB");
    // close the zip stream
    tarOutput.close();
  }
}

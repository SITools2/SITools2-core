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
package fr.cnes.sitools.service.storage;

import java.io.File;
import java.util.ArrayList;
import java.util.UUID;
import java.util.logging.Level;

import org.restlet.data.LocalReference;
import org.restlet.ext.wadl.MethodInfo;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Get;
import org.restlet.resource.Post;

import fr.cnes.sitools.common.model.ResourceCollectionFilter;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.service.storage.model.StorageDirectory;

/**
 * Resource for Storage
 * 
 * @author jp.boignard (AKKA Technologies)
 * 
 */
public final class StorageCollectionResource extends AbstractStorageResource {

  @Override
  public void sitoolsDescribe() {
    setName("StorageCollectionResource");
    setDescription("Resource for managing storage directories collection");
    setNegotiated(false);
  }

  /**
   * Add a new directory
   * 
   * @param representation
   *          the representation of a StorageDirectory
   * @param variant
   *          the variant sent
   * @return representation of a response
   */
  @Post
  public Representation addDirectory(Representation representation, Variant variant) {

    Response response = new Response();
    
    // Get bean
    StorageDirectory inputdir = getStorageDirectory(representation);
    if (inputdir.getId() == null || "".equals(inputdir.getId())) {
      inputdir.setId(UUID.randomUUID().toString());
    }

    // Controle validite de la reference fichier
    File fichier = null;
    String localPath = getSettings().getFormattedString(inputdir.getLocalPath());
    if (localPath.startsWith("file://")) {
      try {
        fichier = new LocalReference(localPath).getFile();
      }
      catch (Exception e) {
        fichier = null;
      }
    }

    if (null == fichier || !fichier.exists()) {
      response = new Response(false, inputdir, StorageDirectory.class, "directory");
      response.setMessage("directory.failure.localpathnotfound");
      return getRepresentation(response, variant);
    }

    // Get if attachment already existing
    boolean alreadyAttached = isAlreadyAttached(inputdir);

    if (alreadyAttached) {
      // Create the response
      response = new Response(false, inputdir, StorageDirectory.class, "directory");
      response.setMessage("directory.failure.attachalreadyset");
      return getRepresentation(response, variant);
    }

    // Properties are supposed OK
    
    // Change status in order to automatically start/attach directory 
    inputdir.setStatus("STARTED");

    // Store it
    getStore().save(inputdir);
    
    if (getStorageApplication() != null) {
      getStorageApplication().initDirectory(inputdir);
    }
    
    // Return the response
    response = new Response(true, inputdir, StorageDirectory.class, "directory");
    response.setMessage("directory.creation.success");

    return getRepresentation(response, variant);

  }

  @Override
  public void describePost(MethodInfo info) {
    info.setDocumentation("Method to create a storage sending its representation.");
    this.addStandardPostOrPutRequestInfo(info);
    this.addStandardObjectResponseInfo(info);
  }

  /**
   * Get the list of directories that has been set up
   * 
   * @param variant
   *          the variant sent
   * @return a representation of the list as a response
   */
  @Get
  public Representation retrieveDirectories(Variant variant) {

    Response response = null;

    // Retrieve them all, using filter
    ArrayList<StorageDirectory> dirList = new ArrayList<StorageDirectory>();
    ResourceCollectionFilter filter = new ResourceCollectionFilter(getRequest());
    dirList.addAll(getStore().getList(filter));
    int total = dirList.size();

    ArrayList<StorageDirectory> dirListReturn = new ArrayList<StorageDirectory>();
    dirListReturn.addAll(getStore().getPage(filter, dirList));

    // Create response
    response = new Response(true, dirListReturn, StorageDirectory.class, "directories");
    response.setMessage("directory.retrieval.success");
    response.setTotal(total);

    trace(Level.FINE, "View available storages");

    // Return representation
    return getRepresentation(response, variant);
  }

  @Override
  public void describeGet(MethodInfo info) {
    info.setDocumentation("Method to get the list of all storages defined.");
    this.addStandardGetRequestInfo(info);
    this.addStandardObjectResponseInfo(info);
  }

}

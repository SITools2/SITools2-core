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
import java.util.logging.Level;

import org.restlet.data.LocalReference;
import org.restlet.data.Parameter;
import org.restlet.ext.wadl.MethodInfo;
import org.restlet.ext.wadl.ParameterInfo;
import org.restlet.ext.wadl.ParameterStyle;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.Put;

import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.notification.model.Notification;
import fr.cnes.sitools.service.storage.model.StorageDirectory;
import fr.cnes.sitools.util.FileCopyUtils;

/**
 * Storage resource
 * 
 * @author m.marseille (AKKA Technologies)
 * 
 */
public final class StorageResource extends AbstractStorageResource {

  @Override
  public void sitoolsDescribe() {
    setName("StorageResource");
    setDescription("Resource for storage directory definition");
    setNegotiated(false);
  }

  /**
   * Get a directory that has been set up
   * 
   * @param variant
   *          the variant sent
   * @return a representation of the list as a response
   */
  @Get
  public Representation retrieveDirectory(Variant variant) {

    Response response = null;

    String dirId = getDirectoryId();
    if (null == dirId || "".equals(dirId)) {
      response = new Response(false, "directory.id.missing");
      return getRepresentation(response, variant);
    }

    // Retrieve it
    StorageDirectory dir = getStore().get(dirId);
    if (dir == null) {
      response = new Response(false, "directory.not.found");
      return getRepresentation(response, variant);
    }

    // Create response
    response = new Response(true, dir, StorageDirectory.class, "directory");
    response.setMessage("directory.retrieval.success");
    trace(Level.INFO, "Edit configuration parameters of the storage");

    // Return representation
    return getRepresentation(response, variant);
  }

  @Override
  public void describeGet(MethodInfo info) {
    info.setDocumentation("Method to get a storage by ID");
    this.addStandardGetRequestInfo(info);
    ParameterInfo paramId = new ParameterInfo("directoryId", true, "xs:string", ParameterStyle.TEMPLATE,
        "Storage identifier");
    info.getRequest().getParameters().add(paramId);
    this.addStandardResponseInfo(info);
  }

  /**
   * Update a single directory
   * 
   * @param representation
   *          the representation of the updated directory
   * @param variant
   *          the variant sent by the request
   * @return a representation of the updated directory as a response
   */
  @Put
  public Representation updateDirectory(Representation representation, Variant variant) {

    Response response = null;

    StorageDirectory directory = getStore().get(getDirectoryId());

    if (directory == null) {
      response = new Response(false, "directory.not.found");
      return getRepresentation(response, variant);
    }

    // Check that action= is present
    Parameter parameter = this.getQuery().getFirst("action");
    if (parameter != null && parameter.getValue() != null && !parameter.getValue().equals("")
        && parameter.getValue().matches("(start|stop|create-index|delete-index|refresh-index|copy)")) {
      String action = parameter.getValue();

      boolean isStarted = "STARTED".equals(directory.getStatus());
      boolean isIndexed = directory.isIndexed();

      if (action.equals("start") && !isStarted) {
        if (getStorageApplication() != null) {
          getStorageApplication().startDirectory(directory);
        }
        response = new Response(true, directory, StorageDirectory.class, "directory");
        response.setMessage("directory.start.success");
        trace(Level.INFO, "Start the storage");
        
      }
      else if (action.equals("stop") && isStarted) {
        if (getStorageApplication() != null) {
          getStorageApplication().stopDirectory(directory);
        }
        response = new Response(true, directory, StorageDirectory.class, "directory");
        response.setMessage("directory.stop.success");
        trace(Level.INFO, "Stop the storage");
      }
      else if (action.equals("start") && isStarted) {
        response = new Response(false, "directory.already.started");
      }
      else if (action.equals("create-index") && !isIndexed) {
        // TODO create solr index
        response = new Response(true, "directory.indexed");
      }
      else if (action.equals("create-index") && isIndexed) {
        response = new Response(false, "directory.already.indexed");
      }
      else if (action.equals("refresh-index") && isIndexed) {
        // TODO refresh index solr
        response = new Response(true, "directory.indexed");
      }
      else if (action.equals("refresh-index") && !isIndexed) {
        response = new Response(false, "directory.not.indexed");
      }
      else if (action.equals("delete-index") && isIndexed) {
        // TODO delete index solr
        response = new Response(true, "directory.not.indexed");
      }
      else if (action.equals("delete-index") && !isIndexed) {
        response = new Response(false, "directory.not.indexed");
      }
      else if (action.equals("copy")) {
        Parameter idDest = this.getQuery().getFirst("idDest");

        if (idDest != null) {
          StorageDirectory directoryDest = getStore().get(idDest.getValue());

          File fichierSrc = new LocalReference(getSettings().getFormattedString(directory.getLocalPath())).getFile();
          File fichierDest = new LocalReference(directoryDest.getLocalPath()).getFile();

          FileCopyUtils.copyAFolder(fichierSrc, fichierDest.getPath(), true);
          response = new Response(true, "directory.copy");
        }
        else {
          response = new Response(false, "directory.not.found");
        }
      }
      else {
        response = new Response(false, "directory.already.stopped");
      }

      return getRepresentation(response, variant);
    }

    // UPDATING StorageDirectory

    // Getting Storage dir from representation
    StorageDirectory inputdir = getStorageDirectory(representation);

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

    // Getting the current directory to detach it
    StorageDirectory dirToDetach = directory;

    if (!inputdir.getId().equals(getDirectoryId())) {
      response = new Response(false, inputdir, StorageDirectory.class, "directory");
      response.setMessage("directory.failure.idmismatch");
      return getRepresentation(response, variant);
    }

    // detach directory if it was attached
    if (getStorageApplication() != null) {
      getStorageApplication().detachDirectory(dirToDetach);
    }

    // keep the current status
    inputdir.setStatus(dirToDetach.getStatus());

    // reattach directory if needed
    if (getStorageApplication() != null) {
      getStorageApplication().initDirectory(inputdir);
    }

    getStore().update(inputdir);
    response = new Response(true, inputdir, StorageDirectory.class, "directory");
    response.setMessage("directory.update.success");    
    trace(Level.INFO, "Update configuration parameters of the storage");

    return getRepresentation(response, variant);
  }

  @Override
  public void describePut(MethodInfo info) {
    info.setDocumentation("Method to modify a storage sending its new representation");
    this.addStandardPostOrPutRequestInfo(info);
    ParameterInfo paramId = new ParameterInfo("directoryId", true, "xs:string", ParameterStyle.TEMPLATE,
        "Storage identifier");
    info.getRequest().getParameters().add(paramId);
    ParameterInfo paramAction = new ParameterInfo("action", true, "xs:string", ParameterStyle.QUERY,
        "(start|stop) : starts or stops the directory resource.");
    info.getRequest().getParameters().add(paramAction);
    this.addStandardResponseInfo(info);
  }

  /**
   * Delete a directory
   * 
   * @param variant
   *          the variant sent
   * @return a response indicating that delete was successful
   */
  @Delete
  public Representation deleteDirectory(Variant variant) {

    Response response = null;

    // Getting the current directory to detach it
    StorageDirectory dirToDetach = getStore().get(getDirectoryId());

    if (dirToDetach == null) {
      response = new Response(true, dirToDetach, StorageDirectory.class, "directory");
      response.setMessage("directory.not.found");
      return getRepresentation(response, variant);
    }

    // Detach directory if needed
    if (getStorageApplication() != null) {
      getStorageApplication().detachDirectory(dirToDetach);
    }

    // Store
    getStore().delete(dirToDetach);

    // Notify observers
    Notification notification = new Notification();
    notification.setObservable(dirToDetach.getId());
    notification.setStatus(dirToDetach.getStatus());
    notification.setEvent("STORAGE_DELETED");
    notification.setMessage("storage deleted");
    getResponse().getAttributes().put(Notification.ATTRIBUTE, notification);

    // Return response
    response = new Response(true, "directory.delete.success");
    trace(Level.INFO, "Delete the storage");

    return getRepresentation(response, variant);
  }

  @Override
  public void describeDelete(MethodInfo info) {
    info.setDocumentation("Method to delete a storage directory by ID.");
    this.addStandardGetRequestInfo(info);
    ParameterInfo paramId = new ParameterInfo("directoryId", true, "xs:string", ParameterStyle.TEMPLATE,
        "Storage identifier");
    info.getRequest().getParameters().add(paramId);
    this.addStandardSimpleResponseInfo(info);
  }

}

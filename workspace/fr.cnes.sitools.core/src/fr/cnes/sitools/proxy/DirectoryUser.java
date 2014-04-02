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
package fr.cnes.sitools.proxy;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;
import java.util.logging.Level;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Reference;
import org.restlet.data.ReferenceList;
import org.restlet.data.Status;
import org.restlet.ext.jackson.JacksonRepresentation;
import org.restlet.ext.xml.DomRepresentation;
import org.restlet.ext.xml.XmlRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;

import fr.cnes.sitools.userstorage.UserStorageApplication;
import fr.cnes.sitools.userstorage.model.UserStorage;
import fr.cnes.sitools.userstorage.model.UserStorageStatus;

/**
 * User directory
 * 
 * @author jp.boignard (AKKA Technologies)
 * 
 */
public final class DirectoryUser extends DirectoryProxy {

  /**
   * User attribute
   */
  private String userAttribute;

  /**
   * Constructor with proxyBaseRef
   * 
   * @param context
   *          restlet context
   * @param rootLocalReference
   *          root local reference
   * @param proxyBaseRef
   *          proxy base reference
   * @param userAttribute
   *          user attribute
   */
  public DirectoryUser(Context context, Reference rootLocalReference, String proxyBaseRef, String userAttribute) {
    super(context, rootLocalReference, proxyBaseRef);
    if ((userAttribute != null) && !userAttribute.equals("")) {
      setTargetClass(DirectoryUserResource.class);
      this.userAttribute = userAttribute;
    }
  }

  /**
   * Constructor with proxyBaseRef
   * 
   * @param context
   *          restlet context
   * @param rootUri
   *          root URI
   * @param proxyBaseRef
   *          proxy base reference
   * @param userAttribute
   *          user attribute
   */
  public DirectoryUser(Context context, String rootUri, String proxyBaseRef, String userAttribute) {
    super(context, rootUri, proxyBaseRef);
    if ((userAttribute != null) && !userAttribute.equals("")) {
      setTargetClass(DirectoryUserResource.class);
      this.userAttribute = userAttribute;
    }
  }

  /**
   * To manage the POST JSON or XML representation (command/preferences/...) as a file into the user directory (creates
   * the directories path if needed).
   * 
   * @param request
   *          Request
   * @param response
   *          Response
   * 
   * @see org.restlet.resource.Directory#handle(org.restlet.Request, org.restlet.Response)
   */
  @Override
  public void handle(Request request, Response response) {
    String identifier = (String) request.getAttributes().get(userAttribute);
    UserStorageApplication application = (UserStorageApplication) getApplication();

    // TODO RESTLET OPTIMISATION - cache
    UserStorage storage = application.getStore().retrieve(identifier);
    if ((storage != null) && storage.getStatus().equals(UserStorageStatus.ACTIVE)
        && request.getMethod().equals(Method.POST) && (request.getEntity() != null)) {

      String mediaTypeEntity = request.getEntity().getMediaType().toString();
      if (MediaType.APPLICATION_JSON.toString().equals(mediaTypeEntity)) {
        Form form = request.getResourceRef().getQueryAsForm();
        try {
          // JsonRepresentation json = new JsonRepresentation(request.getEntity());
          String filename = form.getFirstValue("filename");
          String filepath = form.getFirstValue("filepath");
          String pathRootUser = this.getRootRef().getPath(true) + File.separator + identifier;

          if (filepath == null) {
            filepath = "";
          }
          else {
            mkdirPath(pathRootUser, filepath);
          }
          if (filename == null) {
            filename = UUID.randomUUID().toString() + ".json";
          }
          File cible = new File(pathRootUser + filepath, filename);
          if (cible.exists()) {
            cible.delete();
          }
          if (cible.createNewFile()) {
            FileOutputStream fos = new FileOutputStream(cible);
            fos.write(request.getEntity().getText().getBytes());
            fos.flush();
            fos.close();
          }
          fr.cnes.sitools.common.model.Response responseResult = new fr.cnes.sitools.common.model.Response(true,
              "Saved");
          response.setEntity(new JacksonRepresentation<fr.cnes.sitools.common.model.Response>(
              MediaType.APPLICATION_JSON, responseResult));
        }
        catch (IOException e) {
          getLogger().log(Level.INFO, null, e);
        }
      }
      else if (MediaType.APPLICATION_XML.toString().equals(mediaTypeEntity)
          || MediaType.TEXT_XML.toString().equals(mediaTypeEntity)) {
        Form form = request.getResourceRef().getQueryAsForm();
        try {
          XmlRepresentation xml = new DomRepresentation(request.getEntity());
          String filename = form.getFirstValue("filename");
          String filepath = form.getFirstValue("filepath");
          String pathRootUser = this.getRootRef().getPath(true) + File.separator + identifier;

          if (filepath == null) {
            filepath = "";
          }
          else {
            mkdirPath(pathRootUser, filepath);
          }
          if (filename == null) {
            filename = UUID.randomUUID().toString() + ".xml";
          }
          File cible = new File(pathRootUser + filepath, filename);
          if (cible.exists()) {
            cible.delete();
          }
          if (cible.createNewFile()) {
            FileOutputStream fos = new FileOutputStream(cible);
            fos.write(xml.getText().getBytes());
            fos.flush();
            fos.close();
          }

          fr.cnes.sitools.common.model.Response responseResult = new fr.cnes.sitools.common.model.Response(true,
              "Saved");
          response.setEntity(new JacksonRepresentation<fr.cnes.sitools.common.model.Response>(
              MediaType.APPLICATION_JSON, responseResult));
        }
        catch (IOException e) {
          getLogger().log(Level.INFO, null, e);
        }
      }

    }
    else {
      if ((storage != null) && storage.getStatus().equals(UserStorageStatus.ACTIVE)) {
        super.handle(request, response);
      }
      else {
        // TODO Representations XML, HTML, Redirection page status error
        // response.setEntity(new JsonRepresentation(new fr.cnes.sitools.common.model.Response(false, "no storage")
        throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND);
      }
    }
  }

  /**
   * make directory path
   * 
   * @param root
   *          root path
   * @param filepath
   *          file path
   */
  private void mkdirPath(String root, String filepath) {
    String[] directories = filepath.split("/");
    String path = root;
    for (int i = 0; i < directories.length; i++) {
      path += File.separator + directories[i];
      File file = new File(path);
      if (!file.exists()) {
        file.mkdir();
      }
    }
  }

  /**
   * Gets the userAttribute value
   * 
   * @return the userAttribute
   */
  public String getUserAttribute() {
    return userAttribute;
  }

  /**
   * Sets the value of userAttribute
   * 
   * @param userAttribute
   *          the userAttribute to set
   */
  public void setUserAttribute(String userAttribute) {
    this.userAttribute = userAttribute;
  }

  /**
   * Get JSon from reference overrides DirectoryProxy getJsonRepresentation for producing a specific representation of a
   * user directory
   * 
   * @param reference
   *          the reference used
   * @return a JSON representation
   */
  @Override
  protected Representation getJsonRepresentation(ReferenceList reference) {
    return getAdvancedJsonRepresentation(reference);
  }

}

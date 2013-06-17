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
package fr.cnes.sitools.userstorage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.ext.jackson.JacksonRepresentation;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.ext.wadl.MethodInfo;
import org.restlet.ext.wadl.ParameterInfo;
import org.restlet.ext.wadl.ParameterStyle;
import org.restlet.ext.xstream.XstreamRepresentation;
import org.restlet.representation.EmptyRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Delete;
import org.restlet.resource.Post;
import org.restlet.resource.Put;
import org.restlet.resource.ResourceException;

import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.userstorage.business.UserStorageManager;
import fr.cnes.sitools.userstorage.model.UserStorage;

/**
 * Resource for managing single user disk space
 * 
 * @author jp.boignard (AKKA Technologies)
 */
public final class UserStorageResource extends AbstractUserStorageResource {

  @Override
  public void sitoolsDescribe() {
    setName("UserStorageResource");
    setDescription("Resource to manage user storage");
    setNegotiated(false);
  }

  @Override
  protected Representation get(Variant variant) {

    UserStorage storage = getStore().retrieve(getIdentifier());

    if (storage != null) {

      UserStorageManager.refresh(getContext(), storage);
      getStore().update(storage);

      Response response = new Response(true, storage, UserStorage.class, "userstorage");
      return getRepresentation(response, variant);
    }
    else {
      Response response = new Response(false, "USERSTORAGE_NOTFOUND");
      return getRepresentation(response, variant);
    }
  }

  @Override
  public void describeGet(MethodInfo info) {
    info.setDocumentation("Method to get representation of a user storage.");
    this.addStandardGetRequestInfo(info);
    ParameterInfo paramUserId = new ParameterInfo(IDENTIFIER_PARAM_NAME, true, "xs:string", ParameterStyle.TEMPLATE,
      "Identifier of the user to deal with.");
    info.getRequest().getParameters().add(paramUserId);
    this.addStandardResponseInfo(info);
  }

  /**
   * Changement de quota ... que faire ? Changement de repertoire ... que faire ? Changement de userId >> erreur ?
   * changement de login.
   * */

  /**
   * POST treatment
   * 
   * @param representation
   *          the representation as input
   * @param variant
   *          the variant used
   * @return Representation
   */
  @Post
  public Representation doPost(Representation representation, Variant variant) {
    return doIt(representation);
  }

  @Override
  public void describePost(MethodInfo info) {
    info.setDocumentation("Method to create a new User Storage for the specified user.");
    this.addStandardPostOrPutRequestInfo(info);
    ParameterInfo paramUserId = new ParameterInfo(IDENTIFIER_PARAM_NAME, true, "xs:string", ParameterStyle.TEMPLATE,
      "Identifier of the user to deal with.");
    info.getRequest().getParameters().add(paramUserId);
    this.addStandardResponseInfo(info);
  }

  /**
   * DELETE
   * 
   * @param representation
   *          the representation as input
   * @param variant
   *          the variant used
   * @return Representation
   */
  @Delete
  public Representation doDelete(Representation representation, Variant variant) {

    // TODO DELETE CONTENT ?
    // Peut être fait par l'action Clean.
    // try {
    // UserStorage userStorage = getStore().retrieve(getIdentifier());
    // UserStorageManager.delete(getContext(), userStorage);
    // }
    // catch (Exception e) {
    // getContext().getLogger().log(Level.FINE, "unable to delete UserStorage", e);
    // }

    getStore().delete(getIdentifier());
    Response response = new Response(true, "userstorage.delete.success");

    return getRepresentation(response, variant);

  }

  @Override
  public void describeDelete(MethodInfo info) {
    info.setDocumentation("Method to delete the userstorage the specified user.");
    this.addStandardGetRequestInfo(info);
    ParameterInfo paramUserId = new ParameterInfo(IDENTIFIER_PARAM_NAME, true, "xs:string", ParameterStyle.TEMPLATE,
      "Identifier of the user to deal with.");
    info.getRequest().getParameters().add(paramUserId);
    this.addStandardResponseInfo(info);
  }

  /**
   * PUT treatment
   * 
   * @param representation
   *          the representation used
   * @param variant
   *          the variant used
   * @return Representation
   */
  @Put
  public Representation doPut(Representation representation, Variant variant) {
    Response response;
    if (getIdentifier() != null) {
      // update de l'objet userstorage (quota ...)

      if (representation == null) {
        throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "USER_STORAGE_REPRESENTATION_REQUIRED");
      }

      UserStorage userStorageInput = null;
      if (MediaType.APPLICATION_XML.isCompatible(representation.getMediaType())) {
        // Parse the XML representation to get the bean
        userStorageInput = new XstreamRepresentation<UserStorage>(representation).getObject();

      }
      else if (MediaType.APPLICATION_JSON.isCompatible(representation.getMediaType())) {
        // Parse the JSON representation to get the bean
        userStorageInput = new JacksonRepresentation<UserStorage>(representation, UserStorage.class).getObject();
      }

      if (userStorageInput == null) {
        throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "USER_STORAGE_REPRESENTATION_REQUIRED");
      }

      UserStorage storage = getStore().retrieve(getIdentifier());
      if (storage != null) {
        storage.getStorage().setQuota(userStorageInput.getStorage().getQuota());
        storage.getStorage().setLastUpdate(null);
        UserStorage storageOut = getStore().update(storage);
        response = new Response(true, storageOut, UserStorage.class, "userstorage");
      }
      else {
        response = new Response(false, "userstorage.not.found");
      }

    }
    else {
      response = new Response(false, "user.id.not.specified");
    }

    return getRepresentation(response, variant);
  }

  @Override
  public void describePut(MethodInfo info) {
    info.setDocumentation("Method to modify the userstorage of the specified user.");
    this.addStandardPostOrPutRequestInfo(info);
    ParameterInfo paramUserId = new ParameterInfo(IDENTIFIER_PARAM_NAME, true, "xs:string", ParameterStyle.TEMPLATE,
      "Identifier of the user to deal with.");
    info.getRequest().getParameters().add(paramUserId);
    this.addStandardResponseInfo(info);
  }

  /**
   * Method for user storage creation
   * 
   * @param representation
   *          the representation as input
   * @return Representation
   */
  protected Representation doIt(Representation representation) {
    Form form = getQuery();
    try {
      JsonRepresentation json = new JsonRepresentation(representation);

      String filename = form.getFirstValue("filename");
      String filepath = form.getFirstValue("filepath");

      UserStorage storage = getStore().retrieve(getIdentifier());

      // Format UserStorage Path
      String formattedUserStoragePath = getSettings().getFormattedString(storage.getStorage().getUserStoragePath());
      File cible = new File(formattedUserStoragePath + filepath, filename);
      FileOutputStream fos = new FileOutputStream(cible);
      fos.write(json.getText().getBytes());
      fos.flush();
      fos.close();
      getResponse().redirectPermanent(new Reference(getReference().getBaseRef() + filepath + filename));

    }
    catch (IOException e) {
      throw new ResourceException(Status.CLIENT_ERROR_FAILED_DEPENDENCY, e);
    }
    return new EmptyRepresentation();
  }

}

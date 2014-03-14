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
package fr.cnes.sitools.userstorage;

import java.util.List;
import java.util.logging.Level;

import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.ext.jackson.JacksonRepresentation;
import org.restlet.ext.wadl.MethodInfo;
import org.restlet.ext.wadl.ParameterInfo;
import org.restlet.ext.wadl.ParameterStyle;
import org.restlet.ext.xstream.XstreamRepresentation;
import org.restlet.representation.ObjectRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ResourceException;

import fr.cnes.sitools.common.exception.SitoolsException;
import fr.cnes.sitools.common.model.ResourceCollectionFilter;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.userstorage.business.UserStorageManager;
import fr.cnes.sitools.userstorage.model.DiskStorage;
import fr.cnes.sitools.userstorage.model.UserStorage;

/**
 * Class Resource for managing UserStorage Collection (GET, POST)
 * 
 * @author jp.boignard (AKKA Technologies)
 */
public final class UserStorageCollectionResource extends AbstractUserStorageResource {

  @Override
  public void sitoolsDescribe() {
    setName("UserStorageCollectionResource");
    setDescription("Resource for managing userStorage collection");
    setNegotiated(false);
  }

  /**
   * Update / Validate existing userStorage
   * 
   * @param representation
   *          UserStorage representation
   * @param variant
   *          client preferred media type
   * @return Representation
   */
  @Post
  public Representation newUserStorage(Representation representation, Variant variant) {
    if (representation == null) {
      throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "USER_STORAGE_REPRESENTATION_REQUIRED");
    }
    try {
      UserStorage userStorageInput = null;
      if (MediaType.APPLICATION_XML.isCompatible(representation.getMediaType())) {
        // Parse the XML representation to get the bean
        userStorageInput = new XstreamRepresentation<UserStorage>(representation).getObject();

      }
      else if (MediaType.APPLICATION_JSON.isCompatible(representation.getMediaType())) {
        // Parse the JSON representation to get the bean
        userStorageInput = new JacksonRepresentation<UserStorage>(representation, UserStorage.class).getObject();
      }
      else if (representation.getMediaType().isCompatible(MediaType.APPLICATION_JAVA_OBJECT)) {
        @SuppressWarnings("unchecked")
        ObjectRepresentation<UserStorage> obj = (ObjectRepresentation<UserStorage>) representation;
        userStorageInput = obj.getObject();

      }
      // Having free space not null at creation, should be done more cleanly maybe ...
      Long quota = userStorageInput.getStorage().getQuota();
      DiskStorage storage = userStorageInput.getStorage();
      storage.setFreeUserSpace(quota);
      if (storage.getBusyUserSpace() == null) {
        storage.setBusyUserSpace(Long.parseLong("0"));
      }
      userStorageInput.setStorage(storage);

      // Business service
      UserStorage userStorageOutput = null;
      try {
        userStorageOutput = getStore().create(userStorageInput);
      }
      catch (SitoolsException e) {
        Response response = new Response(false, e.getMessage());
        return getRepresentation(response, variant);
      }
      if (userStorageOutput != null) {
 
        String path = getUserStorageManagement().getRootDirectory() + "/" + userStorageOutput.getUserId(); // File.separator
        userStorageOutput.getStorage().setUserStoragePath(path);
        UserStorageManager.build(getContext(), userStorageOutput);
        getStore().update(userStorageOutput);
      }

      Response response = new Response(true, userStorageOutput, UserStorage.class, "userstorage");
      return getRepresentation(response, variant);

    }
    catch (ResourceException e) {
      getLogger().log(Level.INFO, null, e);
      throw e;
    }
    catch (Exception e) {
      getLogger().log(Level.SEVERE, null, e);
      throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
    }
  }

  @Override
  public void describePost(MethodInfo info) {
    info.setDocumentation("Method to create a new user storage according to the representation sent.");
    this.addStandardPostOrPutRequestInfo(info);
    this.addStandardResponseInfo(info);
    this.addStandardInternalServerErrorInfo(info);
  }

  /**
   * get all userStorages
   * 
   * @param variant
   *          client preferred media type
   * @return Representation
   */
  @Get
  public Representation retrieveUserStorage(Variant variant) {
    try {
      ResourceCollectionFilter filter = new ResourceCollectionFilter(this.getRequest());
      List<UserStorage> userStorages = getStore().getList(filter);
      int total = userStorages.size();
      userStorages = getStore().getPage(filter, userStorages);
      Response response = new Response(true, userStorages, UserStorage.class, "userStorages");
      response.setTotal(total);
      return getRepresentation(response, variant);
    }
    catch (ResourceException e) {
      getLogger().log(Level.INFO, null, e);
      throw e;
    }
    catch (Exception e) {
      getLogger().log(Level.SEVERE, null, e);
      throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
    }
  }

  @Override
  public void describeGet(MethodInfo info) {
    info.setDocumentation("Method to create one or all user storages, depending if user identifier is given or not.");
    this.addStandardGetRequestInfo(info);
    ParameterInfo paramUserId = new ParameterInfo(IDENTIFIER_PARAM_NAME, false, "xs:string", ParameterStyle.TEMPLATE,
        "Identifier of the user to deal with.");
    info.getRequest().getParameters().add(paramUserId);
    this.addStandardResponseInfo(info);
    this.addStandardInternalServerErrorInfo(info);
  }

}

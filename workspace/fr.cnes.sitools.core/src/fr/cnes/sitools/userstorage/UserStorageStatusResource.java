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
package fr.cnes.sitools.userstorage;

import org.restlet.data.MediaType;
import org.restlet.data.Parameter;
import org.restlet.ext.wadl.MethodInfo;
import org.restlet.ext.wadl.ParameterInfo;
import org.restlet.ext.wadl.ParameterStyle;
import org.restlet.ext.xstream.XstreamRepresentation;
import org.restlet.representation.ObjectRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Get;

import com.thoughtworks.xstream.XStream;

import fr.cnes.sitools.common.SitoolsResource;
import fr.cnes.sitools.common.XStreamFactory;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.userstorage.business.UserStorageManager;
import fr.cnes.sitools.userstorage.model.DiskStorage;
import fr.cnes.sitools.userstorage.model.UserStorage;

/**
 * Gets the status of a UserStorage for a particular User
 * 
 * @author m.gond
 */
public class UserStorageStatusResource extends SitoolsResource {
  /** The identifier of the user */
  private String userIdentifier;
  /** The userstorage of the user */
  private UserStorage userStorage;
  /** The UserStorageStore */
  private UserStorageStoreInterface store;

  @Override
  public void sitoolsDescribe() {
    setName("UserStorageStatusResource");
    setDescription("Gets the status of a UserStorage for a particular User");
  }

  @Override
  protected void doInit() {
    super.doInit();
    userIdentifier = (String) this.getRequest().getAttributes().get("identifier");
    UserStorageApplication application = (UserStorageApplication) getApplication();
    store = application.getStore();
    userStorage = store.retrieve(userIdentifier);

  }

  /**
   * Gets a userstorage Representation without the userstoragePath attribute
   * 
   * @param variant
   *          The Variant needed
   * @return a Representation of the userstorage with the asked variant
   */
  @Get
  public Representation getStatus(Variant variant) {
    Response response;
    Boolean forceRefresh = false;

    if (userStorage == null) {
      response = new Response(false, "No user storage defined for that user");
    }
    else {
      Parameter parameter = getRequest().getResourceRef().getQueryAsForm().getFirst("forceRefresh");

      if (parameter != null && parameter.getValue() != null) {
        forceRefresh = Boolean.valueOf(parameter.getValue());
      }

      UserStorageManager.refresh(getContext(), userStorage, forceRefresh);
      store.update(userStorage);
      response = new Response(true, userStorage, UserStorage.class, "userstorage");
    }
    return getRepresentation(response, variant);
  }

  @Override
  public void describeGet(MethodInfo info) {
    info.setDocumentation("Method to retrieve the status of a userstorage");
    info.setIdentifier("retrieve_userstorage_status");
    addStandardGetRequestInfo(info);
    ParameterInfo pic = new ParameterInfo("identifier", true, "xs:string", ParameterStyle.TEMPLATE,
        "User identifier owner of the userstorage");
    info.getRequest().getParameters().add(pic);
    addStandardResponseInfo(info);
    addStandardInternalServerErrorInfo(info);
    addStandardResourceCollectionFilterInfo(info);
  }

  /**
   * Get a representation of the object
   * 
   * @param response
   *          the response to treat
   * @param media
   *          the media to use
   * @return Representation
   */
  public final Representation getRepresentation(Response response, MediaType media) {
    if (media.isCompatible(MediaType.APPLICATION_JAVA_OBJECT)) {
      return new ObjectRepresentation<Response>(response);
    }

    XStream xstream = XStreamFactory.getInstance().getXStream(media, getContext());
    configure(xstream, response);
    xstream.alias("userstorage", UserStorage.class);
    xstream.alias("diskstorage", DiskStorage.class);
    // omit the userstoragePath because this resource is aim to be accessed by logged user and not only administrator
    xstream.omitField(DiskStorage.class, "userStoragePath");

    XstreamRepresentation<Response> rep = new XstreamRepresentation<Response>(media, response);
    rep.setXstream(xstream);
    return rep;
  }

}

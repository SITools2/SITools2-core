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

import java.io.IOException;

import org.restlet.data.MediaType;
import org.restlet.ext.jackson.JacksonRepresentation;
import org.restlet.ext.xstream.XstreamRepresentation;
import org.restlet.representation.ObjectRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;

import com.thoughtworks.xstream.XStream;

import fr.cnes.sitools.common.SitoolsResource;
import fr.cnes.sitools.common.XStreamFactory;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.userstorage.model.DiskStorage;
import fr.cnes.sitools.userstorage.model.UserStorage;

/**
 * Resource for user storage
 * @author AKKA
 *
 */
public abstract class AbstractUserStorageResource extends SitoolsResource {
  
  /**
   * The name of the project ID parameter name
   */
  public static final String IDENTIFIER_PARAM_NAME = "identifier";
  
  /** Application */
  private UserStorageManagement application = null;
  
  /** Store */
  private UserStorageStoreInterface store = null;
  
  /** User id in the request */
  private String identifier = null;
  
  /** Notion id in the request */
  private String action = null;

  @Override
  public final void doInit() {
    super.doInit();

    // Declares the two variants supported
    getVariants().add(new Variant(MediaType.APPLICATION_XML));
    getVariants().add(new Variant(MediaType.APPLICATION_JSON));

    application = (UserStorageManagement) getApplication();
    store = application.getStore();

    identifier = (String) this.getRequest().getAttributes().get(IDENTIFIER_PARAM_NAME);
    action = (String) this.getRequest().getAttributes().get("action");
  }

  /**
   * Get a representation of the object
   * @param response the response to treat
   * @param media the media to use
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
    
    XstreamRepresentation<Response> rep = new XstreamRepresentation<Response>(media, response);
    rep.setXstream(xstream);
    return rep;
  }
  
  /**
   * Get the action described in the API
   * @return the action
   */
  public final String getAction() {
    return this.action;
  }
  
  /**
   * Get the identifier given in the API
   * @return the identifier given
   */
  public final String getIdentifier() {
    return this.identifier;
  }
  
  /**
   * Get the store associated to the application
   * @return the store
   */
  public final UserStorageStoreInterface getStore() {
    return this.store;
  }
  
  /**
   * Get the application associated to the resource
   * @return the application
   */
  public final UserStorageManagement getUserStorageManagement() {
    return this.application;
  }

  /**
   * Gets UserStorage object from Representation
   * 
   * @param representation
   *          of a UserStorage
   * @return UserStorage
   * @throws IOException
   *           if there is an error while deserializing Java Object
   */
  protected final UserStorage getObject(Representation representation) throws IOException {
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
    return userStorageInput;
  }

  /**
   * Get the userID from a userstorage for trace only
   * 
   * @param userStorageInput
   *          the userstorage
   * @return the userID from a userstorage for trace only
   */
  protected String getUserIdAsString(UserStorage userStorageInput) {
    return (userStorageInput.getUserId() != null) ? userStorageInput.getUserId() : "<undefined user>";
  }

}

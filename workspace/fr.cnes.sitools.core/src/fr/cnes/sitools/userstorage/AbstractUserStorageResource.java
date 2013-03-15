/*******************************************************************************
 * Copyright 2011, 2012 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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

  /** Application */
  private UserStorageManagement application = null;
  
  /** Store */
  private UserStorageStore store = null;
  
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

    identifier = (String) this.getRequest().getAttributes().get("identifier");
    action = (String) this.getRequest().getAttributes().get("action");
  }

  /**
   * Get a representation of the object
   * @param response the response to treat
   * @param media the media to use
   * @return Representation
   */
  public final Representation getRepresentation(Response response, MediaType media) {
    getLogger().info(media.toString());
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
  public final UserStorageStore getStore() {
    return this.store;
  }
  
  /**
   * Get the application associated to the resource
   * @return the application
   */
  public final UserStorageManagement getUserStorageManagement() {
    return this.application;
  }

}

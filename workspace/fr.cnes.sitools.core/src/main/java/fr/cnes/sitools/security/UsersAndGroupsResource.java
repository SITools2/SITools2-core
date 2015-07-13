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
package fr.cnes.sitools.security;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.restlet.data.CharacterSet;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Reference;
import org.restlet.ext.jackson.JacksonRepresentation;
import org.restlet.ext.xstream.XstreamRepresentation;
import org.restlet.representation.ObjectRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;

import com.thoughtworks.xstream.XStream;

import fr.cnes.sitools.common.SitoolsResource;
import fr.cnes.sitools.common.XStreamFactory;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.security.model.Group;
import fr.cnes.sitools.security.model.User;
import fr.cnes.sitools.util.Property;

/**
 * Resource for managing users and groups
 * 
 * @author jp.boignard (AKKA Technologies)
 * 
 */
public abstract class UsersAndGroupsResource extends SitoolsResource {

  /** user and groups application */
  private UsersAndGroupsAdministration application = null;

  /** user and groups store */
  private UsersAndGroupsStore store = null;

  /** user ID */
  private volatile String userIdentifier = null;

  /** group name */
  private volatile String groupName = null;

  /** Attributes */
  private volatile Map<String, Object> attributes = null;

  /** List of media types */
  private ArrayList<MediaType> mediaTypes = new ArrayList<MediaType>();

  /**
   * Constructor
   */
  public UsersAndGroupsResource() {
    super();
    HashSet<Method> allowed = new HashSet<Method>();
    allowed.add(Method.HEAD);
    allowed.add(Method.GET);
    allowed.add(Method.PUT);
    allowed.add(Method.POST);
    allowed.add(Method.DELETE);
    allowed.add(Method.OPTIONS);
    setAllowedMethods(allowed);

    mediaTypes.add(MediaType.APPLICATION_JSON);
    mediaTypes.add(MediaType.APPLICATION_XML);
    mediaTypes.add(MediaType.APPLICATION_JAVA);

  }

  /**
   * Get the list of media types
   * 
   * @return a lit of media types
   */
  public List<MediaType> getMediaTypes() {

    return mediaTypes;
  }

  @Override
  public void doInit() {
    super.doInit();

    application = (UsersAndGroupsAdministration) this.getApplication();
    store = application.getStore();

    // target : database, table, record
    attributes = this.getRequest().getAttributes();

    this.userIdentifier = (attributes.get("user") != null) ? Reference.decode((String) attributes.get("user"),
        CharacterSet.UTF_8) : null;

    this.groupName = (attributes.get("group") != null) ? Reference.decode((String) attributes.get("group"),
        CharacterSet.UTF_8) : null;

    // Logs the variants supported
    List<Variant> variants = getVariants();
    String variantsString = "";
    for (Variant variant : variants) {
      variantsString += " " + variant.getMediaType().toString();
    }
    getLogger().log(Level.FINEST, "Variants : " + variantsString);

  }

  /**
   * Gets representation according to the specified media type.
   * 
   * 
   * @param response
   *          the response to use
   * @param media
   *          the media type
   * @return Representation the final representation of the response
   */
  public Representation getRepresentation(Response response, MediaType media) {
    if (media.isCompatible(MediaType.APPLICATION_JAVA_OBJECT)) {
      return new ObjectRepresentation<Response>(response);
    }

    XStream xstream = XStreamFactory.getInstance().getXStream(media, getContext());
    configure(xstream, response);
    xstream.alias("group", Group.class);
    xstream.alias("user", User.class);
    xstream.alias("property", Property.class);

    xstream.omitField(User.class, "secret");

    XstreamRepresentation<Response> rep = new XstreamRepresentation<Response>(media, response);
    rep.setXstream(xstream);
    return rep;
  }

  /**
   * Gets DataSet object from Representation
   * 
   * @param representation
   *          of a DataSet
   * @return DataSet
   * @throws IOException
   *           if there is an error while deserializing Java Object
   */
  public final Group getGroupObject(Representation representation) throws IOException {
    Group object = null;
    if (representation.getMediaType().isCompatible(MediaType.APPLICATION_JAVA_OBJECT)) {
      @SuppressWarnings("unchecked")
      ObjectRepresentation<Group> obj = (ObjectRepresentation<Group>) representation;
      object = obj.getObject();
    }
    else if (MediaType.APPLICATION_XML.isCompatible(representation.getMediaType())) {
      // Parse the XML representation to get the dataset bean
      object = new XstreamRepresentation<Group>(representation).getObject();

    }
    else if (MediaType.APPLICATION_JSON.isCompatible(representation.getMediaType())) {
      // Parse the JSON representation to get the bean
      object = new JacksonRepresentation<Group>(representation, Group.class).getObject();
    }

    return object;
  }

  /**
   * Get the group name indicated in the API
   * 
   * @return the group name
   */
  public final String getGroupName() {
    return this.groupName;
  }

  /**
   * Get the user identifier indicated in the API
   * 
   * @return the user
   */
  public final String getUserId() {
    return this.userIdentifier;
  }

  /**
   * Get the store associated to the application
   * 
   * @return the store associated
   */
  public final UsersAndGroupsStore getStore() {
    return this.store;
  }

  /**
   * Get the application attached to the resource
   * 
   * @return the application
   */
  public final UsersAndGroupsAdministration getUsersAndGroupsAdministration() {
    return this.application;
  }

  /**
   * Check if property name are duplicated
   * 
   * @param listProp
   *          user properties list to check
   * @return boolean
   */
  public boolean checkPropertiesName(List<Property> listProp) {
    if (listProp == null) {
      return true;
    }
    else {
      boolean ok = true;
      for (Property prop : listProp) {
        String name = prop.getName();
        int i = 0;
        for (Property prop2 : listProp) {
          if (name.equals(prop2.getName())) {
            i++;
          }
          if (i >= 2) {
            ok = false;
            break;
          }
        }
        if (!ok) {
          break;
        }
      }
      return ok;
    }
  }

}

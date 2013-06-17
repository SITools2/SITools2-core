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
package fr.cnes.sitools.security;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.restlet.Request;
import org.restlet.data.CharacterSet;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Preference;
import org.restlet.data.Reference;
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
import org.restlet.resource.Put;
import org.restlet.resource.ResourceException;

import fr.cnes.sitools.common.SitoolsResource;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.security.model.User;
import fr.cnes.sitools.server.Consts;
import fr.cnes.sitools.util.Property;
import fr.cnes.sitools.util.RIAPUtils;

/**
 * Class for Editing an User Profile
 * 
 * @author b.fiorito (AKKA Technologies)
 */
public class EditUserProfileResource extends SitoolsResource {

  /** Attributes */
  private volatile Map<String, Object> attributes = null;

  /** user ID */
  private volatile String userIdentifier = null;

  /**
   * EditUserProfileResource Describe
   * 
   */
  public void sitoolsDescribe() {
    setName("EditUserProfileResource");
    setDescription("Resource for modifying an user properties - Retrieve Update");
    setNegotiated(false);
  }

  /**
   * Get user profile representation
   * 
   * @param variant
   *          client preference for response media type
   * @return Representation if success
   */
  @Get
  public Representation get(Variant variant) {
    attributes = this.getRequest().getAttributes();

    this.userIdentifier = (attributes.get("user") != null) ? Reference.decode((String) attributes.get("user"),
        CharacterSet.UTF_8) : null;

    Response response = getUserResponse(this.userIdentifier);
    return getRepresentation(response, variant);
  }

  @Override
  public void describeGet(MethodInfo info) {
    info.setDocumentation("Method to retrieve the details of a user identified by its id");
    info.setIdentifier("retrieve_user");
    addStandardGetRequestInfo(info);
    ParameterInfo pic = new ParameterInfo("user", true, "xs:user", ParameterStyle.TEMPLATE,
        "Identifier of the user to update");
    info.getRequest().getParameters().add(pic);
    addStandardResponseInfo(info);
    addStandardInternalServerErrorInfo(info);
  }

  /**
   * Update user profile
   * 
   * @param representation
   *          the response to use
   * @param variant
   *          client preference for response media type
   * @return User Representation if success
   */
  @Put
  public Representation updateProfile(Representation representation, Variant variant) {
    if (representation == null) {
      throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "USER_REPRESENTATION_REQUIRED");
    }
    try {
      // Récupère les données utilisateur du profil
      User user = getObject(representation);
      Response response = null;
      String url = getSitoolsSetting(Consts.APP_SECURITY_URL) + "/users";
      User userDb = RIAPUtils.getObject(user.getIdentifier(), url, getContext());

      if (userDb != null) {
        if (userDb.getIdentifier().equals(user.getIdentifier()) && checkRulesProperty(userDb, user.getProperties())) {
          user.setProperties(mergeUserProperties(user.getProperties(), userDb.getProperties()));
          if (updateUser(user, url)) {
            response = new Response(true, "Profile updated");
          }
          else {
            response = new Response(false, "ERROR UPDATING USER ");
          }
        }
        else {
          response = new Response(false, "Invalid fields : Check the form. ");
        }
      }
      else {
        response = new Response(false, "User not found. ");
      }
      return getRepresentation(response, variant);
    }
    catch (ResourceException e) {
      getLogger().log(Level.INFO, null, e);
      throw e;
    }
  }

  @Override
  public void describePut(MethodInfo info) {
    info.setDocumentation("Method to update a user profile");
    info.setIdentifier("update_user_profile");
    addStandardPostOrPutRequestInfo(info);
    ParameterInfo pic = new ParameterInfo("user", true, "xs:user", ParameterStyle.TEMPLATE,
        "Identifier of the user to update");
    info.getRequest().getParameters().add(pic);
    addStandardResponseInfo(info);
    addStandardInternalServerErrorInfo(info);
  }

  /**
   * Get an user response
   * 
   * @param identifier
   *          the user login
   * @return Sitools.model.Response
   */
  private Response getUserResponse(String identifier) {
    Response response = null;
    try {
      String url = getSitoolsSetting(Consts.APP_SECURITY_URL) + "/users";
      User userDb = RIAPUtils.getObject(identifier, url, getContext());
      userDb.setProperties(setRulesProperty(userDb.getProperties()));
      response = new Response(true, userDb, User.class, "user");
    }
    catch (Exception e) {

      response = new Response(false, e.getMessage());
      throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
    }
    return response;
  }

  /**
   * Retrieve only ReadOnly and Editable properties
   * 
   * @param properties
   *          the user proerties
   * @return <List> Properties
   */
  private List<Property> setRulesProperty(List<Property> properties) {
    Iterator<Property> itProp = properties.iterator();
    while (itProp.hasNext()) {
      Property prop = itProp.next();
      if (prop.getScope() == null || prop.getScope().equalsIgnoreCase("Hidden")) {
        itProp.remove();
      }
    }
    return properties;
  }

  /**
   * Check if ReadOnly and Hidden properties were not modified by user
   * 
   * @param userDb
   *          the user from DataBase
   * @param properties
   *          the user properties modified
   * @return boolean
   */
  private boolean checkRulesProperty(User userDb, List<Property> properties) {
    for (Property propDb : userDb.getProperties()) {
      for (Property prop : properties) {
        if (prop.getScope().equals("ReadOnly")) {
          if (propDb.getScope().equals("ReadOnly") && (propDb.getName().equals(prop.getName()))) {
            if (!propDb.equals(prop)) {
              return false;
            }
          }
        }
        else if (prop.getScope().equals("Hidden")) {
          return false;
        }
      }
    }
    return true;
  }

  /**
   * Add Hidden properties to the user property list
   * 
   * @param listProp
   *          user properties modified from IHM
   * @param listPropDb
   *          original user properties from DataBase
   * @return properties
   */
  public List<Property> mergeUserProperties(List<Property> listProp, List<Property> listPropDb) {
    for (Property prop : listPropDb) {
      if (prop.getScope().equals("Hidden")) {
        listProp.add(prop);
      }
    }
    return listProp;
  }

  /**
   * Update user properties via RIAP
   * 
   * @param user
   *          the user to update
   * @param url
   *          the url to use
   * @return boolean
   */
  private boolean updateUser(User user, String url) {
    Request reqPUT = new Request(Method.PUT, "riap://component" + url + "/" + user.getIdentifier(),
        new ObjectRepresentation<User>(user));

    ArrayList<Preference<MediaType>> objectMediaType = new ArrayList<Preference<MediaType>>();
    objectMediaType.add(new Preference<MediaType>(MediaType.APPLICATION_JAVA_OBJECT));
    reqPUT.getClientInfo().setAcceptedMediaTypes(objectMediaType);
    org.restlet.Response response = getContext().getClientDispatcher().handle(reqPUT);

    if (response == null || Status.isError(response.getStatus().getCode())) {
      RIAPUtils.exhaust(response);
      return false;
    }
    else {
      RIAPUtils.exhaust(response);
      return true;
    }

  }

  /**
   * Get an User Object
   * 
   * @param representation
   *          the response to use
   * @return User Object
   */
  private User getObject(Representation representation) {
    User object = null;

    if (MediaType.APPLICATION_XML.isCompatible(representation.getMediaType())) {
      // Parse the XML representation to get the dataset bean
      object = new XstreamRepresentation<User>(representation).getObject();

    }
    else if (MediaType.APPLICATION_JSON.isCompatible(representation.getMediaType())) {
      // Parse the JSON representation to get the bean
      object = new JacksonRepresentation<User>(representation, User.class).getObject();
    }

    return object;
  }

  /**
   * Gets the userIdentifier value
   * 
   * @return the userIdentifier
   */
  public String getUserIdentifier() {
    return userIdentifier;
  }

  /**
   * Sets the value of userIdentifier
   * 
   * @param userIdentifier
   *          the userIdentifier to set
   */
  public void setUserIdentifier(String userIdentifier) {
    this.userIdentifier = userIdentifier;
  }

}

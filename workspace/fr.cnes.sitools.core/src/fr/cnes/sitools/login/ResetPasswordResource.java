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
package fr.cnes.sitools.login;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;

import org.restlet.Request;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Preference;
import org.restlet.data.Status;
import org.restlet.ext.jackson.JacksonRepresentation;
import org.restlet.ext.wadl.MethodInfo;
import org.restlet.ext.xstream.XstreamRepresentation;
import org.restlet.representation.ObjectRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Put;
import org.restlet.resource.ResourceException;

import fr.cnes.sitools.common.SitoolsResource;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.security.model.User;
import fr.cnes.sitools.server.Consts;
import fr.cnes.sitools.util.PasswordGenerator;
import fr.cnes.sitools.util.RIAPUtils;

/**
 * Resource to reset and generate an new user password with a sent email
 * 
 * @author AKKA Technologies
 * 
 */
public class ResetPasswordResource extends SitoolsResource {

  @Override
  public void sitoolsDescribe() {
    setName("ResetPasswordResource");
    setDescription("Resource for reset and generate a new user password");
  }

  /**
   * Reset an user Password
   * 
   * @param representation
   *          the response to use
   * @param variant
   *          client preference for response media type
   * @return Representation if success
   */
  @Put
  public Representation resetPass(Representation representation, Variant variant) {
    if (representation == null) {
      throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "USER_REPRESENTATION_REQUIRED");
    }
    try {
      User user = getObject(representation);
      Response response = null;
      String url = getSitoolsSetting(Consts.APP_SECURITY_URL) + "/users";
      User userDb = RIAPUtils.getObject(user.getIdentifier(), url, getContext());

      if (userDb != null) {
        if (userDb.getEmail().equals(user.getEmail())) {
          String password = PasswordGenerator.generate(10);
          userDb.setSecret(password);
          if (updateUser(userDb, url)) {
            response = new Response(true, user.getEmail());
          }
          else {
            response = new Response(false, "ERROR UPDATING USER ");
          }
        }
        else {
          response = new Response(false, "Invalid fields : email doesn't match the correct login ");
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
    catch (Exception e) {
      getLogger().log(Level.SEVERE, null, e);
      throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
    }
  }

  @Override
  public void describePut(MethodInfo info) {
    info.setDocumentation("Method to reset the password of a user");
    info.setIdentifier("reset_password");
    addStandardPostOrPutRequestInfo(info);
    addStandardResponseInfo(info);
    addStandardInternalServerErrorInfo(info);
  }

  /**
   * Update an user
   * 
   * @param user
   *          the user to update
   * @param url
   *          the url to use
   * @return boolean
   */
  private boolean updateUser(User user, String url) {
    Request reqPUT = new Request(Method.PUT, RIAPUtils.getRiapBase() + url + "/" + user.getIdentifier(),
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
   * Gets User object from Representation
   * 
   * @param representation
   *          of a User
   * @return DataSet
   * @throws IOException
   *           if there is an error while deserializing Java Object
   */
  private User getObject(Representation representation) throws IOException {
    User object = null;
    if (representation.getMediaType().isCompatible(MediaType.APPLICATION_JAVA_OBJECT)) {
      @SuppressWarnings("unchecked")
      ObjectRepresentation<User> obj = (ObjectRepresentation<User>) representation;
      object = obj.getObject();
    }
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

}

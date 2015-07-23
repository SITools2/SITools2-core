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
package fr.cnes.sitools.login;

import java.io.IOException;
import java.util.logging.Level;

import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.ext.jackson.JacksonRepresentation;
import org.restlet.ext.wadl.MethodInfo;
import org.restlet.ext.xstream.XstreamRepresentation;
import org.restlet.representation.ObjectRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Put;
import org.restlet.resource.ResourceException;

import fr.cnes.sitools.applications.PublicApplication;
import fr.cnes.sitools.common.SitoolsResource;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.security.challenge.ChallengeToken;
import fr.cnes.sitools.security.model.User;
import fr.cnes.sitools.common.Consts;
import fr.cnes.sitools.util.RIAPUtils;

/**
 * Resource to reset and generate an new user password with a sent email
 * 
 * @author AKKA Technologies
 * 
 */
public class ResetPasswordResource extends SitoolsResource {
  /**
   * The userLogin get from the challengeToken
   */
  private String userLogin;
  /** The challengeToken */
  private ChallengeToken challengeToken;
  /** The token */
  private String token;

  @Override
  public void sitoolsDescribe() {
    setName("ResetPasswordResource");
    setDescription("Resource to change the user password");
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.cnes.sitools.common.SitoolsResource#doInit()
   */
  @Override
  protected void doInit() {
    super.doInit();
    PublicApplication application = (PublicApplication) getApplication();
    challengeToken = application.getChallengeToken();

    token = getRequest().getResourceRef().getQueryAsForm().getFirstValue("cdChallengeMail", null);
    if (token == null) {
      throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "cdChallengeMail parameter mandatory");
    }

    userLogin = challengeToken.getTokenValue(token);
    if (userLogin == null) {
      throw new ResourceException(
          Status.CLIENT_ERROR_GONE,
          "You asked to change your password, but the request is no longer available. Please ask again to change your password on SITools2");
    }

  }

  /**
   * Reset an user Password
   * 
   * @param representation
   *          The new password
   * 
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
      Response response = null;
      String url = getSitoolsSetting(Consts.APP_SECURITY_URL) + "/users";
      User userDb = RIAPUtils.getObject(userLogin, url, getContext());

      if (userDb != null) {
        if (userLogin.equals(userDb.getIdentifier())) {
          User userPassword = getObject(representation);
          userDb.setSecret(userPassword.getSecret());
          if (updateUser(userDb, url)) {
            response = new Response(true, userDb.getEmail());
          }
          else {
            response = new Response(false, "ERROR UPDATING USER ");
          }
        }
        else {
          response = new Response(false, "User not found. ");
        }
      }
      else {
        response = new Response(false, "User not found. ");
      }
      challengeToken.invalidToken(token);
      return getRepresentation(response, variant);
    }
    catch (ResourceException e) {
      challengeToken.invalidToken(token);
      getLogger().log(Level.INFO, null, e);
      throw e;
    }
    catch (Exception e) {
      getLogger().log(Level.WARNING, null, e);
      challengeToken.invalidToken(token);
      throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
    }
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
  protected boolean updateUser(User user, String url) {
    return (RIAPUtils.updateObject(user, url + "/" + user.getIdentifier() + "?origin=user", getContext())) != null;
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
    else if (MediaType.APPLICATION_JSON.isCompatible(representation.getMediaType())) {
      // Parse the JSON representation to get the bean
      object = new JacksonRepresentation<User>(representation, User.class).getObject();
    }

    return object;
  }

  @Override
  public void describePut(MethodInfo info) {
    info.setDocumentation("Method to reset the password of a user");
    info.setIdentifier("reset_password");
    addStandardPostOrPutRequestInfo(info);
    addStandardResponseInfo(info);
    addStandardInternalServerErrorInfo(info);
  }

}

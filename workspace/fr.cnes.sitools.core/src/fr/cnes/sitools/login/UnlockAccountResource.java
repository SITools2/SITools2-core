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

import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.ext.jackson.JacksonRepresentation;
import org.restlet.ext.wadl.MethodInfo;
import org.restlet.ext.xstream.XstreamRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Put;
import org.restlet.resource.ResourceException;

import fr.cnes.sitools.applications.PublicApplication;
import fr.cnes.sitools.common.SitoolsSettings;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.security.challenge.ChallengeToken;
import fr.cnes.sitools.security.model.User;
import fr.cnes.sitools.server.Consts;
import fr.cnes.sitools.util.RIAPUtils;

/**
 * Resource to reset and generate an new user password with a sent email
 * 
 * @author AKKA Technologies
 * 
 */
public class UnlockAccountResource extends ResetPasswordResource {

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
    setName("UnBlacklistResource");
    setDescription("Resource to unblacklist a user and reset and generate a new user password");
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
          "You asked to unlock your account, but the request is no longer available. Please ask again to unlock your password on SITools2");
    }

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
  public Representation unBlacklistUser(Representation representation, Variant variant) {
    if (representation == null) {
      throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "USER_REPRESENTATION_REQUIRED");
    }

    SitoolsSettings settings = getSettings();

    // reset new password
    Response response = null;
    String url = getSitoolsSetting(Consts.APP_SECURITY_URL) + "/users";
    User userDb = RIAPUtils.getObject(userLogin, url, getContext());

    if (userDb != null) {
      if (userLogin.equals(userDb.getIdentifier())) {
        User userPassword = getObject(representation);
        // Unblacklist user
        String unblacklistUrl = settings.getString(Consts.APP_USER_BLACKLIST_URL) + "/" + userDb.getIdentifier();
        boolean result = RIAPUtils.deleteObject(unblacklistUrl, getContext());
        if (result) {
          userDb.setSecret(userPassword.getSecret());
          if (updateUser(userDb, url)) {
            response = new Response(true, "User account unlocked");
          }
          else {
            response = new Response(false, "Cannot unlock user account");
          }
        }
        else {
          response = new Response(false, "Cannot unlock user account");
        }

      }
      else {
        response = new Response(false, "Invalid fields : email doesn't match the correct login ");
      }
    }
    else {
      response = new Response(false, "User not found. ");
    }
    challengeToken.invalidToken(token);
    return getRepresentation(response, variant);
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
   * Get a user object
   * 
   * @param representation
   *          to use
   * @return user object
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

}

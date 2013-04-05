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
package fr.cnes.sitools.security.authentication;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.ChallengeResponse;
import org.restlet.data.ChallengeScheme;
import org.restlet.data.ClientInfo;
import org.restlet.data.Cookie;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.engine.security.AuthenticatorUtils;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.security.ChallengeAuthenticator;
import org.restlet.security.DelegatedChallengeAuthenticator;
import org.restlet.security.Enroler;
import org.restlet.security.Verifier;
import org.restlet.util.Series;

import fr.cnes.sitools.common.SitoolsRepresentations;
import fr.cnes.sitools.common.SitoolsSettings;
import fr.cnes.sitools.common.application.ContextAttributes;
import fr.cnes.sitools.util.RESTUtils;

/**
 * To enrole with public role.
 * 
 * @author jp.boignard (AKKA Technologies)
 */
public final class SitoolsChallengeAuthenticator extends DelegatedChallengeAuthenticator {

  /** SitoolsRealm with enroler, verifier and public role */
  private SitoolsRealm sitoolsRealm = null;

  /** Cookie authentication */
  private Boolean cookieAuthentication = false;
  /** The name of the authentication cookie */
  private String cookieAuthenticationName = null;

  /**
   * ClientInfo Agent to specialize forbidden response to return 403 + json representation instead of 403 standard HTML
   * representation
   */
  private String authenticationAgent = null;

  /**
   * Constructor with SitoolsRealm but other verifier and enroler
   * 
   * @param context
   *          Context
   * @param optional
   *          boolean
   * @param challengeScheme
   *          ChallengeScheme
   * @param realm
   *          String
   * @param sitoolsRealm
   *          SitoolsRealm
   * @param verifier
   *          the verifier
   * @param enroler
   *          the enroler
   * @param challenge
   *          the challenge authenticator
   */
  public SitoolsChallengeAuthenticator(Context context, boolean optional, ChallengeScheme challengeScheme,
      String realm, SitoolsRealm sitoolsRealm, Verifier verifier, Enroler enroler, ChallengeAuthenticator challenge) {
    super(context, optional, challengeScheme, realm, challenge);
    this.sitoolsRealm = sitoolsRealm;
    this.setVerifier(verifier);
    this.setEnroler(enroler);

    init(context);
  }

  /**
   * Constructor with SitoolsRealm
   * 
   * @param context
   *          Context
   * @param optional
   *          boolean
   * @param challengeScheme
   *          ChallengeScheme
   * @param realm
   *          String
   * @param sitoolsRealm
   *          SitoolsRealm
   * @param challenge
   *          the challenge authenticator
   */
  public SitoolsChallengeAuthenticator(Context context, boolean optional, ChallengeScheme challengeScheme,
      String realm, SitoolsRealm sitoolsRealm, ChallengeAuthenticator challenge) {
    super(context, optional, challengeScheme, realm, challenge);
    this.sitoolsRealm = sitoolsRealm;

    init(context);
  }

  /**
   * Init local attributes with context
   * 
   * @param context
   *          Context
   */
  private void init(Context context) {
    cookieAuthentication = (Boolean) context.getAttributes().get(ContextAttributes.COOKIE_AUTHENTICATION);
    if (null == cookieAuthentication) {
      cookieAuthentication = false;
    }

    cookieAuthenticationName = ((SitoolsSettings) context.getAttributes().get(ContextAttributes.SETTINGS))
        .getAuthenticationCOOKIE();

    authenticationAgent = ((SitoolsSettings) context.getAttributes().get(ContextAttributes.SETTINGS))
        .getAuthenticationAGENT();
  }

  /**
   * Pour les clients sitools, client-admin, client-user qui utilisent le x user agent dans leurs requêtes, on retourne
   * une reponse Sitools
   * 
   * @param response
   *          the response
   * @see org.restlet.security.ChallengeAuthenticator#forbid(org.restlet.Response)
   */
  @Override
  public void forbid(Response response) {

    // if (response.getRequest().getClientInfo().getAgent() != null
    // && response.getRequest().getClientInfo().getAgent().equals(authenticationAgent)) {

    String xUserAgent = (String) response.getRequest().getAttributes().get("X-User-Agent");
    if ((null != xUserAgent) && authenticationAgent.equals(xUserAgent)) {

      fr.cnes.sitools.common.model.Response resp = new fr.cnes.sitools.common.model.Response(false,
          "CLIENT_ERROR_FORBIDDEN");
      Representation rep = SitoolsRepresentations.getRepresentation(resp, new Variant(MediaType.APPLICATION_JSON),
          getContext());
      response.setEntity(rep);
    }
    else {
      super.forbid(response);
    }
  }

  @Override
  public void challenge(Response response, boolean stale) {
    // if (response.getRequest().getClientInfo().getAgent() != null
    // && response.getRequest().getClientInfo().getAgent().equals(authenticationAgent)) {
    String xUserAgent = (String) response.getRequest().getAttributes().get("X-User-Agent");
    if ((null != xUserAgent) && authenticationAgent.equals(xUserAgent)) {

      fr.cnes.sitools.common.model.Response resp = new fr.cnes.sitools.common.model.Response(false,
          "CLIENT_ERROR_UNAUTHORIZED");
      Representation rep = SitoolsRepresentations.getRepresentation(resp, new Variant(MediaType.APPLICATION_JSON),
          getContext());
      response.setEntity(rep);

      response.getChallengeRequests().add(createChallengeRequest(stale));
    }
    else {
      super.challenge(response, stale);
    }
  }

  @Override
  public boolean authenticate(Request request, Response response) {

    // ADD PUBLIC ROLE
    if (request.getClientInfo() == null) {
      request.setClientInfo(new ClientInfo());
    }

    request.getClientInfo().getRoles().add(sitoolsRealm.getPublicRole());

    // FORCE AUTHENTICATED IF RIAP
    if (request.getProtocol().getSchemeName().startsWith("riap")) {
      return true;
    }

    // COOKIE AUTHENTICATION IF CLASSIC AUTHENTICATION FAILED
    boolean firstAttempt = super.authenticate(request, response);
    if (firstAttempt) {
      return true;
    }

    if (!firstAttempt && cookieAuthentication) {
      Series<Cookie> cookies = request.getCookies();
      Cookie credentials = cookies.getFirst(cookieAuthenticationName);

      if (credentials == null) {
        return false;
      }
      String decoded = RESTUtils.decode(credentials.getValue());
      ChallengeResponse cr = AuthenticatorUtils.parseResponse(request, decoded, new Form());
      request.setChallengeResponse(cr);
      return super.authenticate(request, response);
    }
    else {
      return false;
    }
  }
}

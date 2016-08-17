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
package fr.cnes.sitools.login;

import java.util.logging.Level;

import org.restlet.Request;
import org.restlet.data.ChallengeRequest;
import org.restlet.data.Method;
import org.restlet.ext.wadl.MethodInfo;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Get;

import fr.cnes.sitools.common.SitoolsResource;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.util.RIAPUtils;

/**
 * Resource just for return a standard response when authentication succeed This resource can be attached in each
 * application to return user roles...
 * 
 * @author AKKA Technologies
 * 
 */
public final class LoginResource extends SitoolsResource {

  @Override
  public void sitoolsDescribe() {
    setName("LoginResource");
    setDescription("Resource for authentication");
  }

  /**
   * Login tentative
   * 
   * @param variant
   *          client preference for response media type
   * @return Representation if success
   */
  @Get
  public Representation login(Variant variant) {
    Response response = null;
    String referenceString = this.getReference().getBaseRef().toString();
    if (this.getRequest().getClientInfo().isAuthenticated()) {
      trace(Level.INFO, "User connected");
      response = new Response(true, "User authenticated.");
      return getRepresentation(response, variant.getMediaType());
    }
    else {
      response = new Response(false, "User not authenticated.");
    }

    String reference = this.getReference().toString();
    response.setUrl(reference);

    // SOLUTION A PARTIR DE LA LOGIN MANDATORY HTTP

    if (referenceString.endsWith("login")) {
      Request request = new Request(Method.GET, referenceString + "-mandatory");
      request.setChallengeResponse(this.getRequest().getChallengeResponse());
      org.restlet.Response responseMandatory = null;
      try {
        responseMandatory = this.getContext().getClientDispatcher().handle(request);
        if ((responseMandatory.getChallengeRequests() != null) && (responseMandatory.getChallengeRequests().size() > 0)) {
          
          if (responseMandatory.getStatus().isError() && this.getRequest().getChallengeResponse() != null) {
            trace(Level.INFO, "Can not connect the user: " + this.getRequest().getChallengeResponse().getIdentifier());
          }

          ChallengeRequest challengeRequest = responseMandatory.getChallengeRequests().get(0);

          SitoolsAuthenticationInfo sai = new SitoolsAuthenticationInfo();
          sai.setALGORITHM(challengeRequest.getDigestAlgorithm());
          sai.setNONCE(challengeRequest.getServerNonce());
          sai.setREALM(challengeRequest.getRealm());
          sai.setSCHEME(challengeRequest.getScheme().toString());
          response.setItem(sai);
          response.setItemClass(SitoolsAuthenticationInfo.class);
        }
      }
      finally {
        RIAPUtils.exhaust(responseMandatory);
      }
    }

    return getRepresentation(response, variant.getMediaType());
  }

  /**
   * Describe the GET method
   * 
   * @param info
   *          WADL method info
   */
  public void describeGet(MethodInfo info) {
    info.setDocumentation("Method to reach the Login process.");
    this.addStandardGetRequestInfo(info);
    this.addStandardResponseInfo(info);
  }

}

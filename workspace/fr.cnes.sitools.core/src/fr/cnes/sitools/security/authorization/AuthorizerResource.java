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
package fr.cnes.sitools.security.authorization;

import org.restlet.data.Status;
import org.restlet.ext.wadl.MethodInfo;
import org.restlet.ext.wadl.ParameterInfo;
import org.restlet.ext.wadl.ParameterStyle;
import org.restlet.representation.ObjectRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Get;
import org.restlet.resource.ResourceException;

import fr.cnes.sitools.security.authorization.client.ResourceAuthorization;

/**
 * Resource for getting a ResourceAuthorization ObjectRepresentation object given a resource identifier.
 * 
 * @author jp.boignard (AKKA Technologies)
 * 
 */
public class AuthorizerResource extends AbstractAuthorizationResource {

  @Override
  public void sitoolsDescribe() {
    setName("AuthorizerResource");
    setDescription("Return a java object authorizer if defined");
  }

  /**
   * Get an authorizer
   * 
   * @param variant
   *          client preferred media type but only APPLICATION_JAVA_OBJECT is returned
   * @return ObjectRepresentation
   */
  @Get
  public Representation retrieveResourceAuthorization(Variant variant) {
    // boolean basedOnDefault = false;
    ResourceAuthorization authorization = getStore().retrieve(getResId());
    if ((authorization != null) && (authorization.getRefId() != null) && (!authorization.getRefId().equals(""))) {
      authorization = getStore().retrieve(authorization.getRefId());
    }
    if ((authorization == null) && (getAuthorizationApplication().getDefaultAuthorization() != null)) {
      authorization = getStore().retrieve(getAuthorizationApplication().getDefaultAuthorization());
//      if (authorization != null) {
//        basedOnDefault = true;
//      }
    }
    
    if (authorization != null) {
      // TODO Optimisation : authorization.setBasedOnDefault(basedOnDefault);
      return new ObjectRepresentation<ResourceAuthorization>(authorization);
    }
    throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
  }
  
  @Override
  public void describeGet(MethodInfo info) {
    info.setDocumentation("Method to get the auhorizer linked to the authorization.");
    this.addStandardGetRequestInfo(info);
    ParameterInfo paramResId = new ParameterInfo("resId", true, "xs:string", ParameterStyle.TEMPLATE, "Identifier of the resource.");
    info.getRequest().getParameters().add(paramResId);
    this.addStandardObjectResponseInfo(info);
    this.addStandardInternalServerErrorInfo(info);
  }

}

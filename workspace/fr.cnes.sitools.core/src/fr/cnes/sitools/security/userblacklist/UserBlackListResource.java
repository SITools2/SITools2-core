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
package fr.cnes.sitools.security.userblacklist;

import java.util.logging.Level;

import org.restlet.data.Status;
import org.restlet.ext.wadl.MethodInfo;
import org.restlet.ext.wadl.ParameterInfo;
import org.restlet.ext.wadl.ParameterStyle;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.ResourceException;

import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.security.filter.RequestCounter;
import fr.cnes.sitools.server.Consts;

/**
 * UserBlackListModel resource
 * 
 * @author AKKA Technologies
 */
public final class UserBlackListResource extends AbstractUserBlackListResource {

  @Override
  public void sitoolsDescribe() {
    setName("UserBlackListResource");
    setDescription("Resource for managing an identified userBlackListModel");
    setNegotiated(false);
  }

  /**
   * get a single userBlackListModel by name
   * 
   * @param variant
   *          client preferred media type
   * @return Representation
   */
  @Get
  public Representation retrieveUserBlackListDetail(Variant variant) {
    UserBlackListModel userBlackListModel = getStore().retrieve(getUserId());
    Response response;
    if (userBlackListModel == null) {
      response = new Response(false, "user.not.blacklisted");
    }
    else {
      userBlackListModel.setUserExists(userExists(getUserId()));
      response = new Response(true, userBlackListModel, UserBlackListModel.class, "userBlackListModel");
    }
    return getRepresentation(response, variant);
  }

  @Override
  public void describeGet(MethodInfo info) {
    info.setDocumentation("Method to retrieve a single userBlackListModel by ID");
    this.addStandardGetRequestInfo(info);
    ParameterInfo paramProjectId = new ParameterInfo("projectId", true, "xs:string", ParameterStyle.TEMPLATE,
        "Name of the userBlackListModel");
    info.getRequest().getParameters().add(paramProjectId);
    this.addStandardObjectResponseInfo(info);
  }

  /**
   * Delete userBlackListModel
   * 
   * @param variant
   *          client preferred media type
   * @return Representation
   */
  @Delete
  public Representation deleteProject(Variant variant) {
    try {
      UserBlackListModel input = null;
      try {
        input = getStore().retrieve(getUserId());
      }
      catch (Exception e) {
        getLogger().log(Level.SEVERE, null, e);
        throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
      }

      Response response = null;

      if (input != null) {

        // Business service
        getStore().delete(getUserId());

        RequestCounter counter = (RequestCounter) getSettings().getStores().get(
            Consts.SECURITY_FILTER_USER_BLACKLIST_CONTAINER);
        counter.remove(getUserId());

        response = new Response(true, "userBlackListModel.delete.success");
      }
      else {
        response = new Response(false, "userBlackListModel.delete.notfound");
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
  public void describeDelete(MethodInfo info) {
    info.setDocumentation("Method to delete a single userBlackListModel by ID");
    this.addStandardGetRequestInfo(info);
    ParameterInfo paramProjectId = new ParameterInfo("projectId", true, "xs:string", ParameterStyle.TEMPLATE,
        "Identifier of the userBlackListModel");
    info.getRequest().getParameters().add(paramProjectId);
    this.addStandardSimpleResponseInfo(info);
  }

}

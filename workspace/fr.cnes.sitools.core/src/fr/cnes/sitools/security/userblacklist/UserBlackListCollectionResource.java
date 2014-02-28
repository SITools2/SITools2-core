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

import java.util.List;
import java.util.logging.Level;

import org.restlet.data.Status;
import org.restlet.ext.wadl.MethodInfo;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Get;
import org.restlet.resource.ResourceException;

import fr.cnes.sitools.common.model.ResourceCollectionFilter;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.security.model.User;
import fr.cnes.sitools.server.Consts;
import fr.cnes.sitools.util.RIAPUtils;

/**
 * Class Resource for managing UserBlackListModel Collection (GET, POST)
 * 
 * @author jp.boignard (AKKA Technologies)
 */
public final class UserBlackListCollectionResource extends AbstractUserBlackListResource {

  @Override
  public void sitoolsDescribe() {
    setName("UserBlackListCollectionResource");
    setDescription("Resource for managing User BlackList");
    setNegotiated(false);
  }

  /**
   * get all userBlackListModels
   * 
   * @param variant
   *          client preferred media type
   * @return Representation
   */
  @Get
  public Representation retrieveUserBlackList(Variant variant) {
    try {
      ResourceCollectionFilter filter = new ResourceCollectionFilter(this.getRequest());
      List<UserBlackListModel> userBlackListModels = getStore().getList(filter);
      int total = userBlackListModels.size();
      userBlackListModels = getStore().getPage(filter, userBlackListModels);
      setUsersExists(userBlackListModels);
      Response response = new Response(true, userBlackListModels, UserBlackListModel.class, "userBlackListModels");
      response.setTotal(total);
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
  public void describeGet(MethodInfo info) {
    info.setDocumentation("Method to the list of all userBlackListModels available in Sitools2.");
    this.addStandardGetRequestInfo(info);

    this.addStandardResourceCollectionFilterInfo(info);

    this.addStandardObjectResponseInfo(info);
    this.addStandardInternalServerErrorInfo(info);
    this.addStandardResourceCollectionFilterInfo(info);
  }

  private void setUsersExists(List<UserBlackListModel> blacklistedUsers) {
    String url = getSettings().getString(Consts.APP_SECURITY_URL) + "/users";
    List<User> users = RIAPUtils.getListOfObjects(url, getContext());

    for (UserBlackListModel blackListUser : blacklistedUsers) {
      for (User user : users) {
        if (user.getIdentifier().equals(blackListUser.getUsername())) {
          blackListUser.setUserExists(true);
          break;
        }
      }
      if (blackListUser.getUserExists() == null) {
        blackListUser.setUserExists(false);
      }
    }
  }
}

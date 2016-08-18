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
package fr.cnes.sitools.portal;

import org.apache.commons.lang.StringUtils;
import org.restlet.data.Method;
import org.restlet.data.Status;
import org.restlet.ext.wadl.MethodInfo;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Get;
import org.restlet.resource.ResourceException;
import org.restlet.security.User;

import fr.cnes.sitools.common.SitoolsResource;
import fr.cnes.sitools.common.application.SitoolsApplication;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.dataset.model.DataSet;
import fr.cnes.sitools.registry.AppRegistryApplication;
import fr.cnes.sitools.security.SecurityUtil;
import fr.cnes.sitools.server.Consts;
import fr.cnes.sitools.util.RIAPUtils;

/**
 * Get the list of datasets for the project with authorizations
 * @author m.gond (AKKA Technologies)
 */
public final class PortalDatasetResource extends SitoolsResource {

  private String name;
  
  @Override
  public void sitoolsDescribe() {
    setName("PortalDatasetResource");
    setDescription("Portal Dataset Exposition Resource");
  }

  @Override
  protected void doInit() {
    super.doInit();
    name = (String) this.getRequest().getAttributes().get("name");
  }
  
  /**
   * Find dataset by name
   * @param variant the variant asked
   * @return a representation of the dataset having the
   */
  @Get
  public Representation findByName(Variant variant) {
    boolean success = false;
    DataSet dataSet = null;
    if (StringUtils.isNotBlank(name)) {
      dataSet = RIAPUtils.getObjectFromName(getSettings().getString(Consts.APP_DATASETS_URL), name, getApplication().getContext());
      if (dataSet != null) {
        success = true;
        AppRegistryApplication appManager = ((SitoolsApplication) getApplication()).getSettings().getAppRegistry();
        SitoolsApplication myApp = appManager.getApplication(dataSet.getId());
        User user = this.getRequest().getClientInfo().getUser();
        String userIdentifier = (user == null) ? null : user.getIdentifier();
        boolean authorized = SecurityUtil.authorize(myApp, userIdentifier, Method.GET);
        if (!authorized) {
          throw new ResourceException(Status.CLIENT_ERROR_FORBIDDEN, "DATASET ACCESS NOT ALLOWED");
        }
      }
    }
    Response response = new Response(success, dataSet, DataSet.class, "data");
    return getRepresentation(response, variant);
  }

  @Override
  public void describeGet(MethodInfo info) {
    info.setDocumentation("Method to retrieve the dataset having a given name.");
    this.addStandardGetRequestInfo(info);
    this.addStandardObjectResponseInfo(info);
    this.addStandardInternalServerErrorInfo(info);
  }

}

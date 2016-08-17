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
package fr.cnes.sitools.plugins.resources;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.restlet.ext.wadl.MethodInfo;
import org.restlet.ext.wadl.ParameterInfo;
import org.restlet.ext.wadl.ParameterStyle;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Get;

import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.engine.SitoolsEngine;
import fr.cnes.sitools.plugins.resources.dto.ResourcePluginDescriptionDTO;
import fr.cnes.sitools.plugins.resources.model.ResourceModel;

/**
 * Resource handling the list of available dynamic resources
 * 
 * @author m.marseille (AKKA Technologies)
 */
public final class ResourceClassPluginCollectionResource extends AbstractResourcePluginResource {
  /**
   * The application className
   */
  private String applicationClassName;

  @Override
  public void sitoolsDescribe() {
    setName("ResourcePluginsCollectionResource");
    setDescription("Resource handling the list of available parameterized resources");
    setNegotiated(false);
  }

  /**
   * DoInit
   */
  public void doInit() {
    applicationClassName = getRequest().getResourceRef().getQueryAsForm().getFirstValue("appClassName");
  }

  /**
   * GET request
   * 
   * @param variant
   *          a RESTlet representation {@code Variant}
   * @return a RESTlet representation
   */
  @Get
  public Representation getResources(Variant variant) {

    Response response;
    SitoolsEngine sitoolsEng = SitoolsEngine.getInstance();
    List<ResourceModel> listResources = new ArrayList<ResourceModel>(sitoolsEng.getRegisteredParameterizedResources());
    List<ResourcePluginDescriptionDTO> listResourcesDTO = new ArrayList<ResourcePluginDescriptionDTO>();

    if (applicationClassName != null) {
      for (Iterator<ResourceModel> iterator = listResources.iterator(); iterator.hasNext();) {
        ResourceModel resourceModel = iterator.next();
        if (resourceModel.getApplicationClassName() == null
            || resourceModel.getApplicationClassName().equals(applicationClassName)) {
          ResourcePluginDescriptionDTO resPluginDTO = new ResourcePluginDescriptionDTO();
          resPluginDTO.setName(resourceModel.getName());
          resPluginDTO.setDescription(resourceModel.getDescription());
          resPluginDTO.setClassName(resourceModel.getClass().getCanonicalName());
          resPluginDTO.setClassAuthor(resourceModel.getClassAuthor());
          resPluginDTO.setClassVersion(resourceModel.getClassVersion());
          resPluginDTO.setClassOwner(resourceModel.getClassOwner());
          resPluginDTO.setResourceClassName(resourceModel.getResourceClassName());
          resPluginDTO.setApplicationClassName(resourceModel.getApplicationClassName());
          resPluginDTO.setDataSetSelection(resourceModel.getDataSetSelection());
          resPluginDTO.setBehavior(resourceModel.getBehavior());
          listResourcesDTO.add(resPluginDTO);
        }
      }
      response = new Response(true, listResourcesDTO, ResourcePluginDescriptionDTO.class, "resources");
    }
    else {
      response = new Response(false, "appClassName parameter is mandatory ");
    }

    return getRepresentation(response, variant);
  }

  /**
   * GET method description
   * 
   * @param info
   *          WADL method information
   */
  public void describeGet(MethodInfo info) {
    info.setDocumentation("Method to retrieve the list of available Resources in Sitools2.");
    this.addStandardGetRequestInfo(info);
    this.addStandardResponseInfo(info);

    ParameterInfo appClassNamePi = new ParameterInfo("appClassName", true, "xs:string", ParameterStyle.TEMPLATE,
        "Application class name filter");
    info.getRequest().getParameters().add(appClassNamePi);

  }

}

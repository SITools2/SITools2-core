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
/**
 * 
 */
package fr.cnes.sitools.plugins.resources;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.restlet.ext.wadl.MethodInfo;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Get;

import fr.cnes.sitools.common.application.SitoolsParameterizedApplication;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.plugins.resources.dto.ResourceModelDTO;
import fr.cnes.sitools.plugins.resources.model.ResourceModel;

/**
 * Resource to expose the list of plugins resources for a SitoolsParameterizedApplication
 * 
 * @author jp.boignard (AKKA Technologies)
 */
public class ListPluginExpositionResource extends AbstractPluginExpositionResource {

  @Override
  public void sitoolsDescribe() {
    setName("ListPluginResources");
    setDescription("Resource that returns the list of plugins resources");
  }

  /**
   * Get the list of resources for the dataset
   * 
   * @param variant
   *          the variant asked
   * @return a representation of the list of resources
   */
  @Get
  public Representation getResourcesList(Variant variant) {

    Representation rep = null;

    // Form query = this.getRequest().getResourceRef().getQueryAsForm();
    // String filterStr = query.getFirstValue("filter");

    ArrayList<ResourceModelDTO> resourcesList = new ArrayList<ResourceModelDTO>();

    Collection<ResourceModel> resourcesListObjet = ((SitoolsParameterizedApplication) getApplication()).getModelMap()
      .values();

    if (resourcesListObjet != null) {
      for (Iterator<ResourceModel> it = resourcesListObjet.iterator(); it.hasNext();) {
        ResourceModel object = (ResourceModel) it.next();
        ResourceModelDTO dto = getResourceModelDTO(object);
        applyRegExpOnParameters(dto.getParameters());
        resourcesList.add(dto);
      }
    }

    Response response = new Response(true, resourcesList, ResourceModelDTO.class);
    rep = getRepresentation(response, variant);

    return rep;
  }

  @Override
  public void describeGet(MethodInfo info) {
    info.setDocumentation("Method to retrieve the list of resources plugins");
    this.addStandardGetRequestInfo(info);
    this.addStandardObjectResponseInfo(info);
    this.addStandardInternalServerErrorInfo(info);
  }

}

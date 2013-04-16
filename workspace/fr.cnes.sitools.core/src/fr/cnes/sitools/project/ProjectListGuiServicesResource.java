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
package fr.cnes.sitools.project;

import java.util.List;

import org.restlet.ext.wadl.MethodInfo;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Get;

import fr.cnes.sitools.common.SitoolsSettings;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.plugins.guiservices.declare.model.GuiServiceModel;
import fr.cnes.sitools.server.Consts;
import fr.cnes.sitools.util.RIAPUtils;

/**
 * Get the list of datasetViews for the project with authorizations
 * 
 * @author m.gond (AKKA Technologies)
 */
public final class ProjectListGuiServicesResource extends AbstractProjectResource {

  @Override
  public void sitoolsDescribe() {
    setName("ProjectListGuiServicesResource");
    setDescription("List of guiServices for a project");
  }

  /**
   * Get the list of datasetViews for the project with authorizations
   * 
   * @param variant
   *          the variant asked
   * @return a representation containing the list of datasetViews
   */
  @Get
  public Representation getDatasetViewsList(Variant variant) {

    List<GuiServiceModel> outputGuiServices = getGuiServices();

    Response response = new Response(true, outputGuiServices, GuiServiceModel.class, "guiServices");
    return getRepresentation(response, variant);
  }

  /**
   * Get the all {@link DatasetViewModel} used in a project
   * 
   * @return the List<{@link DatasetViewModel}>
   */
  private List<GuiServiceModel> getGuiServices() {
    SitoolsSettings settings = getSettings();
    List<GuiServiceModel> listGuiServiceModel = RIAPUtils.getListOfObjects(
        settings.getString(Consts.APP_GUI_SERVICES_URL), getContext());
    return listGuiServiceModel;
  }

  @Override
  public void describeGet(MethodInfo info) {
    info.setDocumentation("Method to retrieve the list of guiservices associated to the project.");
    this.addStandardGetRequestInfo(info);
    this.addStandardObjectResponseInfo(info);
    this.addStandardInternalServerErrorInfo(info);
  }

}

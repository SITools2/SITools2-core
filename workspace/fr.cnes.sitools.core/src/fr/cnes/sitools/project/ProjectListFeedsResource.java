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
package fr.cnes.sitools.project;

import java.util.List;

import org.restlet.ext.wadl.MethodInfo;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Get;

import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.feeds.model.FeedModel;
import fr.cnes.sitools.project.model.Project;
import fr.cnes.sitools.server.Consts;
import fr.cnes.sitools.util.RIAPUtils;

/**
 * Gets the list of feeds for a project
 * 
 * @author m.gond
 */
public class ProjectListFeedsResource extends AbstractProjectResource {
  /**
   * The projectApplication
   */
  private ProjectApplication application;

  @Override
  public void sitoolsDescribe() {
    setName("ProjectListFeedsResource");
    setDescription("List of feeds for a project with authorization");
  }

  /**
   * Get the list of forms for the project with authorizations
   * 
   * @param variant
   *          the variant asked
   * @return a representation of the list of Forms
   */
  @Get
  public Representation getFormsList(Variant variant) {
    application = (ProjectApplication) getApplication();
    Project proj = application.getProject();

    List<FeedModel> feedList = getFeedList(proj.getId());

    FeedModel[] feedsOutput = new FeedModel[feedList.size()];
    feedsOutput = feedList.toArray(feedsOutput);
    Response response = new Response(true, feedsOutput);
    response.setTotal(feedsOutput.length);

    return getRepresentation(response, variant);
  }

  /**
   * Get the list of forms for a dataset
   * 
   * @param id
   *          the id of the dataset
   * @return a Response containing the list of Forms
   */
  private List<FeedModel> getFeedList(String id) {
    return RIAPUtils.getListOfObjects(application.getSettings().getString(Consts.APP_FEEDS_OBJECT_URL) + "/" + id
        + application.getSettings().getString(Consts.APP_FEEDS_URL), getContext());
  }

  @Override
  public void describeGet(MethodInfo info) {
    info.setDocumentation("Method to retrieve the list of feeds associated to the project.");
    this.addStandardGetRequestInfo(info);
    this.addStandardObjectResponseInfo(info);
    this.addStandardInternalServerErrorInfo(info);
  }

}

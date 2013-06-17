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
package fr.cnes.sitools.portal;

import java.util.Iterator;
import java.util.List;

import org.restlet.ext.wadl.MethodInfo;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Get;

import fr.cnes.sitools.common.SitoolsResource;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.feeds.model.FeedModel;
import fr.cnes.sitools.server.Consts;
import fr.cnes.sitools.util.RIAPUtils;

/**
 * Gets the list of visible feeds for a given portal
 * 
 * 
 * @author m.gond
 */
public class PortalListFeedsResource extends SitoolsResource {
  /**
   * The id of the portal
   */
  private String portalId;

  @Override
  public void sitoolsDescribe() {
    setName("PortalListFeedsResource");
    setDescription("Resource that return the list of visible feeds for a given portal");
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.cnes.sitools.common.SitoolsResource#doInit()
   */
  @Override
  protected void doInit() {
    super.doInit();
    portalId = (String) this.getRequest().getAttributes().get("portalId");
  }

  /**
   * Get the list of feeds visible for the portal
   * 
   * @param variant
   *          the variant asked
   * @return a representation of the list of Forms
   */
  @Get
  public Representation getFeedsList(Variant variant) {

    Representation rep = null;

    List<FeedModel> feedListOutput = getFeedList(portalId);

    for (Iterator<FeedModel> iterator = feedListOutput.iterator(); iterator.hasNext();) {
      FeedModel feedModel = iterator.next();
      if (!feedModel.isVisible()) {
        iterator.remove();
      }

    }

    FeedModel[] feedsOutput = new FeedModel[feedListOutput.size()];
    feedsOutput = feedListOutput.toArray(feedsOutput);
    Response response = new Response(true, feedsOutput);
    response.setTotal(feedsOutput.length);
    rep = getRepresentation(response, variant);

    return rep;
  }

  /**
   * Get the list of forms for a dataset
   * 
   * @param id
   *          the id of the dataset
   * @return a Response containing the list of Forms
   */
  private List<FeedModel> getFeedList(String id) {
    return RIAPUtils.getListOfObjects(getSitoolsSetting(Consts.APP_PORTAL_URL) + "/" + id
        + getSitoolsSetting(Consts.APP_FEEDS_URL), getContext());
  }

  @Override
  public void describeGet(MethodInfo info) {
    info.setDocumentation("Method to retrieve the list of feeds associated to the portal.");
    this.addStandardGetRequestInfo(info);
    this.addStandardObjectResponseInfo(info);
    this.addStandardInternalServerErrorInfo(info);
  }

}

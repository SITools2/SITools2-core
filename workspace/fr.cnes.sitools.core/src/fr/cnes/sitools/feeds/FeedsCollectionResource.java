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
package fr.cnes.sitools.feeds;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

import org.restlet.data.Status;
import org.restlet.ext.wadl.MethodInfo;
import org.restlet.ext.wadl.ParameterInfo;
import org.restlet.ext.wadl.ParameterStyle;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.Put;
import org.restlet.resource.ResourceException;

import com.google.common.base.Joiner;

import fr.cnes.sitools.common.application.SitoolsApplication;
import fr.cnes.sitools.common.model.ResourceCollectionFilter;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.feeds.model.FeedCollectionModel;
import fr.cnes.sitools.feeds.model.FeedModel;
import fr.cnes.sitools.server.Consts;

/**
 * Class Resource for managing Project Collection (GET, POST)
 * 
 * @author m.gond (AKKA Technologies)
 * 
 */
public final class FeedsCollectionResource extends AbstractFeedsResource {

  @Override
  public void sitoolsDescribe() {
    setName("FeedsCollectionResource");
    setDescription("Resource for managing feeds collection");
    this.setNegotiated(false);
  }

  /**
   * Create a new feed
   * 
   * @param representation
   *          Project representation
   * @param variant
   *          client preferred media type
   * @return Representation
   */
  @Post
  public Representation newFeed(Representation representation, Variant variant) {
    if (representation == null) {
      trace(Level.INFO, "Cannot create RSS feed - id: " + getFeedsId() + " for " + getTraceParentType() + " - id: "
          + getDataId());
      throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "FEEDS_REPRESENTATION_REQUIRED");
    }

    try {
      Response response;
      // Parse object representation
      FeedModel feedsInput = getObject(representation, variant);
      if (feedsInput != null) {
        // Check that the project does not already exists, or that attachment is not already assigned
        // First need an alphanumeric name for convenience
        response = this.validateFeed(feedsInput);
        if (response != null && !response.getSuccess()) {
          return getRepresentation(response, variant);
        }
        feedsInput.setParent(getDataId());

        AbstractFeedsResource.sortEntries(feedsInput);

        // Business service
        FeedModel feedOutput = getStore().create(feedsInput);

        // register observer
        registerObserver(feedOutput);
        response = new Response(true, feedOutput, FeedModel.class, "FeedModel");
        trace(Level.INFO, "Create RSS feed " + feedOutput.getName() + " for " + getTraceParentType() + " - id: "
            + getDataId());
      }
      else {
        trace(Level.INFO, "Cannot create RSS feed - id: " + getFeedsId() + " for " + getTraceParentType() + " - id: "
            + getDataId());
        response = new Response(false, "feed.create.error");
      }

      // Response
      return getRepresentation(response, variant, false);
    }
    catch (ResourceException e) {
      trace(Level.INFO, "Cannot create RSS feed - id: " + getFeedsId() + " for " + getTraceParentType() + " - id: "
          + getDataId());
      getLogger().log(Level.INFO, null, e);
      throw e;
    }
    catch (Exception e) {
      trace(Level.INFO, "Cannot create RSS feed - id: " + getFeedsId() + " for " + getTraceParentType() + " - id: "
          + getDataId());
      getLogger().log(Level.WARNING, null, e);
      throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
    }
  }

  @Override
  public void describePost(MethodInfo info) {
    info.setDocumentation("Method to create a new feed sending its representation.");
    this.addStandardPostOrPutRequestInfo(info);
    this.addStandardObjectResponseInfo(info);
    this.addStandardInternalServerErrorInfo(info);
  }

  /**
   * get all projects
   * 
   * @param variant
   *          client preferred media type
   * @return Representation
   */
  @Get
  public Representation retrieveFeeds(Variant variant) {

    // resource action settings
    String portalURL = ((SitoolsApplication) getApplication()).getSettings().getString(Consts.APP_PORTAL_URL);

    try {
      if (getFeedsId() != null) {
        FeedModel feed = getStore().retrieve(getFeedsId());
        Response response;
        if (feed != null) {
          response = new Response(true, feed, FeedModel.class, "FeedModel");
          trace(Level.FINE, "Edit RSS feed " + feed.getName() + " for " + getTraceParentType());
        }
        else {
          response = new Response(false, "feed.not.found");
          trace(Level.INFO, "Cannot edit RSS feed - id: " + getFeedsId() + "for " + getTraceParentType());
        }
        return getRepresentation(response, variant, false);
      }
      else if (getDataId() == null || getDataId().equals("idObject")) {
        ResourceCollectionFilter filter = new ResourceCollectionFilter(this.getRequest());
        FeedModel[] feedsOutput = getStore().getArray(filter);
        Response response = new Response(true, feedsOutput);
        Representation rep = getRepresentation(response, variant, true);
        trace(Level.FINE, "View available RSS feeds for " + getTraceParentType() + " - id: " + getDataId());
        return rep;
      }
      else {
        ResourceCollectionFilter filter = new ResourceCollectionFilter(this.getRequest());

        ResourceCollectionFilter rf = new ResourceCollectionFilter(getRequest());
        List<FeedModel> feeds = getStore().getList(filter);

        String request = this.getReference().getPath();
        // we check if we are in portal mode or not

        if (!request.contains(portalURL)) {
          // gets only the FeedModel associated to the parent id
          for (Iterator<FeedModel> iterator = feeds.iterator(); iterator.hasNext();) {
            FeedModel feedModel = iterator.next();
            if (!feedModel.getParent().equals(getDataId())) {
              iterator.remove();
            }
          }
        }

        int total = feeds.size();
        feeds = getStore().getPage(rf, feeds);

        FeedModel[] feedsOutput = new FeedModel[feeds.size()];
        feedsOutput = feeds.toArray(feedsOutput);
        Response response = new Response(true, feedsOutput);
        response.setTotal(total);
        trace(Level.FINE, "View available RSS feeds for " + getTraceParentType() + " - id: " + getDataId());
        return getRepresentation(response, variant, true);
      }
    }
    catch (ResourceException e) {
      trace(Level.INFO, "Cannot view available RSS feeds for " + getTraceParentType());
      getLogger().log(Level.INFO, null, e);
      throw e;
    }
    catch (Exception e) {
      trace(Level.INFO, "Cannot view available RSS feeds for " + getTraceParentType());
      getLogger().log(Level.WARNING, null, e);
      throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
    }
  }

  @Override
  public void describeGet(MethodInfo info) {
    info.setDocumentation("Retrieve one/all feeds.");
    this.addStandardGetRequestInfo(info);
    ParameterInfo param = new ParameterInfo("dataId", false, "ID", ParameterStyle.TEMPLATE, "Data ID");
    info.getRequest().getParameters().add(param);
    this.addStandardResponseInfo(info);
    this.addStandardInternalServerErrorInfo(info);
  }

  /**
   * Handle PUT method on feed collection for setting feeds visibility.
   * 
   * @param representation
   *          Representation
   * @param variant
   *          Variant
   * @return Representation
   */
  @Put
  public Representation setVisibleFeeds(Representation representation, Variant variant) {

    // resource action settings
    String portalURL = ((SitoolsApplication) getApplication()).getSettings().getString(Consts.APP_PORTAL_URL);

    Response response = null;
    if (representation == null) {
      trace(Level.INFO, "The new selection of RSS feeds cannot be performed on the portal : <undefined feeds>");
      throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "FEEDS_REPRESENTATION_REQUIRED");
    }
    try {
      // Parse object representation
      FeedCollectionModel portalFeedsList = getFeedCollectionModel(representation, variant);
      if (portalFeedsList != null) {
        List<FeedModel> feeds = portalFeedsList.getFeeds();
        List<String> visibleFeedsName = new ArrayList<String>();
        for (Iterator<FeedModel> iterator = feeds.iterator(); iterator.hasNext();) {
          FeedModel feedModel = iterator.next();
          // Store specific function

          ((FeedsStoreXML) getStore()).updateDetails(feedModel);
          if (feedModel.isVisible()) {
            visibleFeedsName.add(feedModel.getName());
          }
        }

        ResourceCollectionFilter filter = new ResourceCollectionFilter(this.getRequest());
        ResourceCollectionFilter rf = new ResourceCollectionFilter(getRequest());
        feeds = getStore().getList(filter);

        String request = this.getReference().getPath();
        // we check if we are in portal mode or not
        if (!request.contains(portalURL)) {
          // gets only the FeedModel associated to the parent id
          for (Iterator<FeedModel> iterator = feeds.iterator(); iterator.hasNext();) {
            FeedModel feedModel = iterator.next();
            if (!feedModel.getParent().equals(getDataId())) {
              iterator.remove();
            }
          }
        }
        int total = feeds.size();
        feeds = getStore().getPage(rf, feeds);

        FeedModel[] feedsOutput = new FeedModel[feeds.size()];
        feedsOutput = feeds.toArray(feedsOutput);
        trace(Level.INFO,
            "The following RSS feeds are displayed on the portal : " + Joiner.on(",").join(visibleFeedsName));
        response = new Response(true, feedsOutput);
        response.setTotal(total);

      }
      else {
        trace(Level.INFO, "The new selection of RSS feeds cannot be performed on the portal : <undefined feeds>");
        response = new Response(false, "BAD_PARAMETERS");
      }
      return getRepresentation(response, variant, true);
    }
    catch (ResourceException e) {
      trace(Level.INFO, "The new selection of RSS feeds cannot be performed on the portal : <undefined feeds>");
      getLogger().log(Level.INFO, null, e);
      throw e;
    }
    catch (Exception e) {
      trace(Level.INFO, "The new selection of RSS feeds cannot be performed on the portal : <undefined feeds>");
      getLogger().log(Level.WARNING, null, e);
      throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
    }

  }

  @Override
  public void describePut(MethodInfo info) {
    info.setDocumentation("Modifies a feed according to its ID, sending its new representation.");
    this.addStandardGetRequestInfo(info);
    ParameterInfo param = new ParameterInfo("dataId", false, "ID", ParameterStyle.TEMPLATE, "Data ID");
    info.getRequest().getParameters().add(param);
    this.addStandardResponseInfo(info);
    this.addStandardInternalServerErrorInfo(info);
    this.addStandardResourceCollectionFilterInfo(info);
  }

}

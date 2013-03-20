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
package fr.cnes.sitools.feeds;

import java.util.logging.Level;

import org.restlet.data.Status;
import org.restlet.ext.wadl.MethodInfo;
import org.restlet.ext.wadl.ParameterInfo;
import org.restlet.ext.wadl.ParameterStyle;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.Put;
import org.restlet.resource.ResourceException;

import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.feeds.model.FeedModel;

/**
 * Resource for managing RSS feeds.
 * 
 * @author m.gond (AKKA Technologies)
 */
public final class FeedsAdminResource extends AbstractFeedsResource {

  @Override
  public void sitoolsDescribe() {
    setName("FeedsAdminResource");
    setDescription("Resource for managing an identified feeds");
    this.setNegotiated(false);
  }

  /**
   * get all projects
   * 
   * @param variant
   *          client preferred media type
   * @return Representation
   */
  @Get
  public Representation retrieveFeedsModel(Variant variant) {
    if (getFeedsId() != null) {
      FeedModel project = getStore().retrieve(getFeedsId());
      Response response = new Response(true, project, FeedModel.class, "FeedModel");
      return getRepresentation(response, variant, false);
    }
    else {
      FeedModel[] feeds = getStore().getArray();
      Response response = new Response(true, feeds);
      return getRepresentation(response, variant, false);
    }
  }

  @Override
  public void describeGet(MethodInfo info) {
    info.setDocumentation("Retrieve all the feeds");
    this.addStandardGetRequestInfo(info);
    this.addStandardResponseInfo(info);
    ParameterInfo param = new ParameterInfo("feedsId", true, "ID", ParameterStyle.TEMPLATE, "Feeds ID");
    info.getRequest().getParameters().add(param);
  }

  /**
   * Update / Validate existing project
   * 
   * @param representation
   *          FeedsModel representation
   * @param variant
   *          client preferred media type
   * @return Representation
   */
  @Put
  public Representation updateFeedsModel(Representation representation, Variant variant) {
    FeedModel feedOutput = null;
    try {

      FeedModel feedInput = null;
      Response response;
      if (representation != null) {
        // Parse object representation
        feedInput = getObject(representation, variant);

        // Check that the project does not already exists, or that attachment is not already assigned
        // First need an alphanumeric name for convenience
        response = this.validateFeed(feedInput);
        if (response != null && !response.getSuccess()) {
          return getRepresentation(response, variant);
        }

        this.sortEntries(feedInput);

        // Business service
        feedOutput = getStore().update(feedInput);

        unregisterObserver(feedOutput);
        registerObserver(feedOutput);
      }

      response = new Response(true, feedOutput, FeedModel.class, "FeedModel");
      return getRepresentation(response, variant, false);

    }
    catch (ResourceException e) {

      getLogger().log(Level.INFO, null, e);
      throw e;
    }
    catch (Exception e) {
      getLogger().log(Level.INFO, null, e);
      throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
    }
  }

  @Override
  public void describePut(MethodInfo info) {
    info.setDocumentation("Validate/Modify a feed");
    this.addStandardPostOrPutRequestInfo(info);
    this.addStandardObjectResponseInfo(info);
    this.addStandardInternalServerErrorInfo(info);
    ParameterInfo param = new ParameterInfo("feedsId", true, "ID", ParameterStyle.TEMPLATE, "Feeds ID");
    info.getRequest().getParameters().add(param);
  }

  /**
   * Delete project
   * 
   * @param variant
   *          client preferred media type
   * @return Representation
   */
  @Delete
  public Representation deleteFeedsModel(Variant variant) {
    try {
      FeedModel feedModel = getStore().retrieve(getFeedsId());
      Response response = null;
      if (feedModel != null) {

        // Business service
        getStore().delete(getFeedsId());
        // unregister as observer
        unregisterObserver(feedModel);

        // Response
        response = new Response(true, "feed.delete.success");

      }
      else {
        // Response
        response = new Response(true, "feed.delete.failure");
      }
      return getRepresentation(response, variant, false);

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
    info.setDocumentation("Delete a single feed by ID");
    this.addStandardGetRequestInfo(info);
    ParameterInfo param = new ParameterInfo("feedsId", true, "ID", ParameterStyle.TEMPLATE, "Feeds ID");
    info.getRequest().getParameters().add(param);
    this.addStandardSimpleResponseInfo(info);
    this.addStandardInternalServerErrorInfo(info);
  }

}

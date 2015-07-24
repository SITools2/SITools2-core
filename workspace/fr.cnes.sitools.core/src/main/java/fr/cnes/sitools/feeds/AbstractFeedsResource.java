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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.restlet.data.MediaType;
import org.restlet.ext.jackson.JacksonRepresentation;
import org.restlet.ext.xstream.XstreamRepresentation;
import org.restlet.representation.ObjectRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;

import com.thoughtworks.xstream.XStream;

import fr.cnes.sitools.common.SitoolsResource;
import fr.cnes.sitools.common.XStreamFactory;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.feeds.model.FeedAuthorModel;
import fr.cnes.sitools.feeds.model.FeedCollectionModel;
import fr.cnes.sitools.feeds.model.FeedEntryModel;
import fr.cnes.sitools.feeds.model.FeedModel;
import fr.cnes.sitools.feeds.model.SitoolsFeedDateConverter;
import fr.cnes.sitools.notification.business.NotificationManager;
import fr.cnes.sitools.notification.model.RestletObserver;
import fr.cnes.sitools.common.Consts;
import fr.cnes.sitools.util.RIAPUtils;

/**
 * Abstract Resource class for Feeds management
 * 
 * @author m.gond (AKKA Technologies)
 * 
 */
public abstract class AbstractFeedsResource extends SitoolsResource {

  /** parent application */
  private FeedsApplication application = null;

  /** store */
  private FeedsStoreInterface store = null;
  /** feeds identifier parameter */

  private String feedsId = null;

  /** data identifier parameter (dataSet, Project or Archive) */
  private String dataId = null;

  @Override
  public final void doInit() {
    super.doInit();

    // Declares the two variants supported
    addVariant(new Variant(MediaType.APPLICATION_XML));
    addVariant(new Variant(MediaType.APPLICATION_JSON));
    addVariant(new Variant(MediaType.APPLICATION_JAVA_OBJECT));

    application = (FeedsApplication) getApplication();
    store = application.getStore();

    feedsId = (String) this.getRequest().getAttributes().get("feedsId");
    dataId = (String) this.getRequest().getAttributes().get("dataId");
  }

  /**
   * Gets representation according to the specified Variant if present. If variant is null (when content negotiation =
   * false) sets the variant to the first client accepted mediaType.
   * 
   * @param response
   *          Response
   * @param variant
   *          Variant
   * @param omitEntries
   *          boolean
   * @return Representation
   */
  public final Representation getRepresentation(Response response, Variant variant, boolean omitEntries) {
    MediaType defaultMediaType = getMediaType(variant);
    return getRepresentation(response, defaultMediaType, omitEntries);
  }

  /**
   * Response to Representation
   * 
   * @param response
   *          Response
   * @param media
   *          MediaType
   * @param omitEntries
   *          boolean
   * @return Representation FeedModel representation
   */
  public final Representation getRepresentation(Response response, MediaType media, boolean omitEntries) {
    if (media.isCompatible(MediaType.APPLICATION_JAVA_OBJECT)) {
      return new ObjectRepresentation<Response>(response);
    }

    XStream xstream = XStreamFactory.getInstance().getXStream(media, getContext());
    configure(xstream, response);
    xstream.alias("FeedModel", FeedModel.class);
    xstream.alias("FeedEntryModel", FeedEntryModel.class);
    xstream.alias("FeedAuthorModel", FeedAuthorModel.class);

    if (omitEntries) {
      xstream.omitField(FeedModel.class, "entries");
    }

    // Convertisseur Date / String ( de EEE MMM dd HH:mm:ss zzz yyyy (
    // date.toString) à DateUtils.FORMAT_RFC_3339 (avec T) )
    xstream.registerConverter(new SitoolsFeedDateConverter());

    XstreamRepresentation<Response> rep = new XstreamRepresentation<Response>(media, response);
    rep.setXstream(xstream);
    return rep;
  }

  /**
   * Gets the Object FeedsModel
   * 
   * @param representation
   *          the representation
   * @param variant
   *          the variant
   * @return an Object FeedsModel
   */
  public final FeedModel getObject(Representation representation, Variant variant) {
    FeedModel feedsInput = null;
    if (MediaType.APPLICATION_JSON.isCompatible(representation.getMediaType())) {
      // Parse the JSON representation to get the bean
      try {
        feedsInput = new JacksonRepresentation<FeedModel>(representation, FeedModel.class).getObject();
      } catch (IOException e) {
        getContext().getLogger().severe(e.getMessage());
      }
    }
    return feedsInput;
  }

  /**
   * Gets the Object FeedsModel
   * 
   * @param representation
   *          the representation
   * @param variant
   *          the variant
   * @return an Object FeedsModel
   */
  public final FeedCollectionModel getFeedCollectionModel(Representation representation, Variant variant) {
    FeedCollectionModel feedsInput = null;
    if (MediaType.APPLICATION_JSON.isCompatible(representation.getMediaType())) {
      // Parse the JSON representation to get the bean
      try {
        feedsInput = new JacksonRepresentation<FeedCollectionModel>(representation, FeedCollectionModel.class).getObject();
      } catch (IOException e) {
        getContext().getLogger().severe(e.getMessage());
      }
    }
    return feedsInput;
  }

  /**
   * Register as observer
   * 
   * @param input
   *          The ConverterChainedModel
   */
  public final void registerObserver(FeedModel input) {
    NotificationManager notificationManager = application.getSettings().getNotificationManager();
    if (notificationManager == null) {
      getLogger().warning("NotificationManager is null");
      return;
    }
    RestletObserver observer = new RestletObserver();
    String uriToNotify = RIAPUtils.getRiapBase() + application.getSettings().getString(Consts.APP_FEEDS_OBJECT_URL)
        + "/" + dataId + application.getSettings().getString(Consts.APP_FEEDS_URL) + "/" + input.getId() + "/notify";
    observer.setUriToNotify(uriToNotify);
    observer.setMethodToNotify("PUT");
    observer.setUuid(input.getId());

    notificationManager.addObserver(input.getParent(), observer);

  }

  /**
   * Unregister as Observer
   * 
   * @param input
   *          ConverterChainedModel Object
   */
  public final void unregisterObserver(FeedModel input) {
    NotificationManager notificationManager = application.getSettings().getNotificationManager();
    if (notificationManager == null) {
      getLogger().warning("NotificationManager is null");
      return;
    }
    notificationManager.removeObserver(input.getParent(), input.getId());
  }

  /**
   * Sort the entries of the specified feedInput
   * 
   * @param feedInput
   *          the feedInput to sort
   */
  public static final void sortEntries(FeedModel feedInput) {
    List<FeedEntryModel> entries = feedInput.getEntries();
    if (entries != null) {
      Collections.sort(entries, new Comparator<FeedEntryModel>() {
        /**
         * Compare 2 FeedEntryModel on there publishedDate. It is used to sort from the latest to the oldest
         * publishedDate
         * 
         * @param f1
         *          the first FeedEntryModel
         * @param f2
         *          the second FeedEntryModel
         * @return a negative integer, zero, or a positive integer as the first argument is less than, equal to, or
         *         greater than the second.
         */
        public int compare(FeedEntryModel f1, FeedEntryModel f2) {

          Date o1 = f1.getPublishedDate();
          Date o2 = f2.getPublishedDate();
          if (o1 == null || o2 == null) {
            return -1;
          }
          if (o1.before(o2)) {
            return 1;
          }
          else if (o1.after(o2)) {
            return -1;
          }
          else {
            return 0;
          }
        }

      });
    }
  }

  /**
   * Check that the name is unique over the feeds collection
   * 
   * @param feedsInput
   *          the feed to check with the collection
   * @return a Response with the error message if the checking fail, null otherwise
   */
  public final Response validateFeed(FeedModel feedsInput) {
    // Check that the project does not already exists, or that attachment is not already assigned
    // First need an alphanumeric name for convenience
    Response response = null;
    if (feedsInput.getName() == null || "".equals(feedsInput.getName())) {
      response = new Response(false, feedsInput, FeedModel.class, "FeedModel");
      response.setMessage("feed.name.mandatory");
      return response;
    }
    if (!feedsInput.getName().matches("^[a-zA-Z0-9\\-\\.\\_]+$")) {
      response = new Response(false, feedsInput, FeedModel.class, "FeedModel");
      response.setMessage("feed.name.invalid.for.regexp");
      return response;
    }
    List<FeedModel> storedFeeds = getStore().getList();
    List<String> storedFeedNames = new ArrayList<String>();
    if (storedFeeds != null) {
      for (FeedModel feed : storedFeeds) {
        if (!feed.getId().equals(feedsInput.getId())) {
          storedFeedNames.add(feed.getName());
        }
      }
      if (storedFeedNames.contains(feedsInput.getName())) {
        response = new Response(false, feedsInput, FeedModel.class, "FeedModel");
        response.setMessage("feed.name.already.assigned");
        return response;
      }
    }
    return response;
  }

  /**
   * Gets the application value
   * 
   * @return the application
   */
  public final FeedsApplication getFeedsApplication() {
    return application;
  }

  /**
   * Gets the store value
   * 
   * @return the store
   */
  public final FeedsStoreInterface getStore() {
    return store;
  }

  /**
   * Gets the feedsId value
   * 
   * @return the feedsId
   */
  public final String getFeedsId() {
    return feedsId;
  }

  /**
   * Gets the dataId value
   * 
   * @return the dataId
   */
  public final String getDataId() {
    return dataId;
  }

  protected final String getTraceParentType() {
    Object obj = getApplication().getContext().getAttributes().get("TRACE_PARENT_TYPE");
    if (obj != null) {
      return obj.toString();
    }
    else {
      return null;
    }
  }
}

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
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.restlet.Request;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Preference;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.ext.rome.SyndFeedRepresentation;
import org.restlet.ext.wadl.MethodInfo;
import org.restlet.ext.wadl.ParameterInfo;
import org.restlet.ext.wadl.ParameterStyle;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Get;
import org.restlet.resource.ResourceException;

import com.sun.syndication.feed.synd.SyndContentImpl;
import com.sun.syndication.feed.synd.SyndEnclosureImpl;
import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.feed.synd.SyndFeedImpl;
import com.sun.syndication.feed.synd.SyndPersonImpl;

import fr.cnes.sitools.common.SitoolsResource;
import fr.cnes.sitools.common.SitoolsSettings;
import fr.cnes.sitools.common.application.SitoolsApplication;
import fr.cnes.sitools.dataset.opensearch.model.Opensearch;
import fr.cnes.sitools.feeds.model.FeedEntryModel;
import fr.cnes.sitools.feeds.model.FeedModel;
import fr.cnes.sitools.server.Consts;
import fr.cnes.sitools.util.RIAPUtils;

/**
 * Resource to return RSS or ATOM representations
 * 
 * @author m.gond (AKKA Technologies)
 */
public final class FeedsClientResource extends SitoolsResource {
  /**
   * IdFree
   */
  private String feedsId = null;

  /** Default number of items is 20 */
  private int nbItems = 20;

  @Override
  public void sitoolsDescribe() {
    setName("FeedsClientResource");
    setDescription("Resource to send feeds to the client");
    this.setNegotiated(false);
  }

  @Override
  public void doInit() {
    super.doInit();
    feedsId = (String) this.getRequest().getAttributes().get("feedsId");

    String nbItemsTmp = SitoolsSettings.getInstance().getString("Starter.feed_nb_items_send");

    if (nbItemsTmp != null && !"!Starter.feed_nb_items_send!".equals(nbItemsTmp)) {
      nbItems = new Integer(nbItemsTmp);
    }
  }

  @Override
  @Get()
  public Representation get(Variant variant) {
    Representation represent = null;
    if (feedsId != null) {
      FeedModel feedModel = getFeedModel(feedsId);
      if (feedModel == null) {
        getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST, "The feed does not exists");
        return represent;
      }
      switch (feedModel.getFeedSource()) {
        case CLASSIC:
          represent = getClassicFeed(feedModel);
          break;
        case OPENSEARCH:
          represent = getOpensearchFeed(feedModel);
          break;
        case EXTERNAL:
          represent = getExternalFeed(feedModel);
          break;
        default:
          throw new ResourceException(Status.SERVER_ERROR_INTERNAL, "No Feed source or Unsuported Feed source ");
      }
      if (represent == null) {
        getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST, "The feed does not exists");
      }
    }
    return represent;
  }

  @Override
  public void describeGet(MethodInfo info) {
    info.setDocumentation("Method to retrieve feeds of a dataset");
    info.setIdentifier("retrieve_dataset_feeds");
    addStandardGetRequestInfo(info);
    ParameterInfo param = new ParameterInfo("feedsId", true, "xs:string", ParameterStyle.TEMPLATE,
        "identifier of the feeds");
    ParameterInfo paramPortal = new ParameterInfo("portalId", false, "xs:string", ParameterStyle.TEMPLATE,
        "identifier of the portal holding the feeds.");
    ParameterInfo paramProject = new ParameterInfo("projectId", false, "xs:string", ParameterStyle.TEMPLATE,
        "identifier of the project holding the feeds.");
    info.getRequest().getParameters().add(param);
    info.getRequest().getParameters().add(paramPortal);
    info.getRequest().getParameters().add(paramProject);
    addStandardResponseInfo(info);
    addStandardInternalServerErrorInfo(info);
  }

  /**
   * Get the feedModel object using RIAP
   * 
   * @param feedName
   *          : the feedModel object id
   * @return an feedModel model objet corresponding to the given id null if the is no feedModel object corresponding to
   *         the given id
   * 
   */
  private FeedModel getFeedModel(String feedName) {
    List<FeedModel> feeds = RIAPUtils.getListOfObjects(getSitoolsSetting(Consts.APP_FEEDS_OBJECT_URL) + "/idObject"
        + getSitoolsSetting(Consts.APP_FEEDS_URL) + "?query=" + feedName + "&mode=strict", getContext());

    if (feeds.size() > 0) {
      return feeds.get(0);
    }
    else {
      return null;
    }

  }

  /**
   * Create a representation of a Classic FeedModel
   * 
   * @param feedModel
   *          The FeedModel
   * @return a representation of a Classic FeedModel
   */
  private Representation getClassicFeed(FeedModel feedModel) {
    Date publishedDate = null;
    SyndFeedImpl syndFeed = new SyndFeedImpl();
    syndFeed.setTitle(feedModel.getTitle());
    syndFeed.setDescription(feedModel.getDescription());

    syndFeed.setEncoding(feedModel.getEncoding());
    syndFeed.setFeedType(feedModel.getFeedType());
    syndFeed.setLink(feedModel.getLink());
    syndFeed.setLinks(feedModel.getLinks());
    syndFeed.setTitle(feedModel.getTitle());
    syndFeed.setUri(feedModel.getUri());

    // Sets the author
    if (feedModel.getAuthor() != null) {
      SyndPersonImpl author = new SyndPersonImpl();
      author.setEmail(feedModel.getAuthor().getEmail());
      author.setName(feedModel.getAuthor().getName());
      ArrayList<SyndPersonImpl> authors = new ArrayList<SyndPersonImpl>();
      authors.add(author);
      syndFeed.setAuthors(authors);
    }
    // Sets the entries
    List<FeedEntryModel> list = feedModel.getEntries();
    if (list != null) {
      int i = 0;
      for (Iterator<FeedEntryModel> iterator = list.iterator(); iterator.hasNext() && i < nbItems;) {
        FeedEntryModel feedEntryModel = iterator.next();
        SyndEntryImpl entry = new SyndEntryImpl();
        entry.setTitle(feedEntryModel.getTitle());

        SyndContentImpl description = new SyndContentImpl();
        description.setValue(feedEntryModel.getDescription());
        entry.setDescription(description);

        if (feedEntryModel.getImage() != null) {
          SyndEnclosureImpl enc = new SyndEnclosureImpl();
          enc.setType(feedEntryModel.getImage().getType());
          enc.setUrl(getEnclosureUrl(feedEntryModel));
          List<SyndEnclosureImpl> enclosures = new ArrayList<SyndEnclosureImpl>();
          enclosures.add(enc);
          entry.setEnclosures(enclosures);
        }
        entry.setLink(feedEntryModel.getLink());

        entry.setLinks(feedEntryModel.getLinks());
        // Sets the author
        if (feedEntryModel.getAuthor() != null) {
          SyndPersonImpl authorEntry = new SyndPersonImpl();
          authorEntry.setEmail(feedEntryModel.getAuthor().getEmail());
          authorEntry.setName(feedEntryModel.getAuthor().getName());
          ArrayList<SyndPersonImpl> authors = new ArrayList<SyndPersonImpl>();
          authors.add(authorEntry);
          entry.setAuthors(authors);
        }

        entry.setUpdatedDate(feedEntryModel.getUpdatedDate());
        entry.setPublishedDate(feedEntryModel.getPublishedDate());
        entry.setUri(feedEntryModel.getUri());

        ((List<SyndEntryImpl>) syndFeed.getEntries()).add(entry);

        if (publishedDate == null || publishedDate.getTime() < entry.getPublishedDate().getTime()) {
          publishedDate = entry.getPublishedDate();

        }
        i++;
      }
      if (publishedDate != null) {
        syndFeed.setPublishedDate(publishedDate);
      }
    }
    return new SyndFeedRepresentation(syndFeed);
  }

  private String getEnclosureUrl(FeedEntryModel feedEntryModel) {
    String url = feedEntryModel.getImage().getUrl();
    if (url.startsWith("/")) {
      SitoolsSettings settings = ((SitoolsApplication) getApplication()).getSettings();
      return settings.getPublicHostDomain() + url;
    }
    return url;
  }

  /**
   * Get the {@link Representation} of an External FeedModel
   * 
   * @param feedModel
   *          the FeedModel
   * @return a {@link Representation} of an External FeedModel
   */
  private Representation getExternalFeed(FeedModel feedModel) {
    SitoolsSettings settings = ((SitoolsApplication) getApplication()).getSettings();
    Reference ref = new Reference(settings.getString(Consts.APP_CLIENT_PUBLIC_PATH));
    ref.addSegment("proxy");
    ref.addQueryParameter("external_url", feedModel.getExternalUrl());
    return RIAPUtils.handle(ref.toString(), Method.GET, MediaType.APPLICATION_ALL_XML, getContext());
  }

  /**
   * Return the dataset entries with an RSS representation
   * 
   * @param feed
   *          FeedModel Parameter
   * @return the entries as a RSS feed
   */
  public Representation getOpensearchFeed(FeedModel feed) {
    Representation represent = null;

    Opensearch os = getOpensearch(feed.getId());

    if (os == null || !"ACTIVE".equals(os.getStatus())) {
      return null;
    }

    String start = (this.getQuery().getFirstValue("start") != null) ? this.getQuery().getFirstValue("start") : "0";
    String limit = (this.getQuery().getFirstValue("limit") != null) ? this.getQuery().getFirstValue("limit") : "10";

    // '*:*' list every entry in the index
    String requestStr = "?q=*:*&start=" + start + "&limit=" + limit;

    Request reqGET = new Request(Method.GET, RIAPUtils.getRiapBase() + getSitoolsSetting(Consts.APP_SOLR_URL) + "/"
        + os.getId() + "/execute" + requestStr);

    ArrayList<Preference<MediaType>> objectMediaType = new ArrayList<Preference<MediaType>>();
    objectMediaType.add(new Preference<MediaType>(MediaType.APPLICATION_ALL_XML));
    reqGET.getClientInfo().setAcceptedMediaTypes(objectMediaType);
    org.restlet.Response response = getContext().getClientDispatcher().handle(reqGET);

    if (response == null || Status.isError(response.getStatus().getCode())) {

      throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
    }
    represent = response.getEntity();

    return represent;
  }

  /**
   * Get the opensearch model object using RIAP
   * 
   * @param id
   *          : the opensearch model object id
   * @return an opensearch model objet corresponding to the given id null if the is no opensearch object corresponding
   *         to the given id
   * 
   */
  private Opensearch getOpensearch(String id) {
    return RIAPUtils.getObject(getSitoolsSetting(Consts.APP_DATASETS_URL) + "/" + id
        + getSitoolsSetting(Consts.APP_OPENSEARCH_URL), getContext());
  }

}

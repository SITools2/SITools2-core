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

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import org.restlet.Context;
import org.restlet.engine.Engine;

import fr.cnes.sitools.common.store.SitoolsStoreXML;
import fr.cnes.sitools.feeds.model.FeedAuthorModel;
import fr.cnes.sitools.feeds.model.FeedEntryModel;
import fr.cnes.sitools.feeds.model.FeedModel;

/**
 * Implementation of FeedsModelStore with XStream FilePersistenceStrategy
 * 
 * @author m.gond (AKKA Technologies)
 * 
 */
@Deprecated
public final class FeedsStoreXML extends SitoolsStoreXML<FeedModel> {

  /** default location for file persistence */
  private static final String COLLECTION_NAME = "feeds";

  /** static logger for this store implementation */
  private static Logger log = Engine.getLogger(FeedsStoreXML.class.getName());

  /**
   * Constructor with the XML file location
   * 
   * @param location
   *          directory of FilePersistenceStrategy
   * @param context
   *          the Context
   */
  public FeedsStoreXML(File location, Context context) {
    super(FeedModel.class, location, context);
  }

  /**
   * Default constructor
   * 
   * @param context
   *          the Context
   */
  public FeedsStoreXML(Context context) {
    super(FeedModel.class, context);
    File defaultLocation = new File(COLLECTION_NAME);
    init(defaultLocation);
  }

  @Override
  public FeedModel update(FeedModel feed) {
    FeedModel result = null;
    for (Iterator<FeedModel> it = getRawList().iterator(); it.hasNext();) {
      FeedModel current = it.next();
      if (current.getId().equals(feed.getId())) {
        log.info("Updating FeedsModel");

        result = current;
        current.setEntries(feed.getEntries());
        current.setId(feed.getId());
        current.setDescription(feed.getDescription());
        current.setEncoding(feed.getEncoding());
        current.setFeedType(feed.getFeedType());
        current.setImage(feed.getImage());
        current.setLink(feed.getLink());
        current.setLinks(feed.getLinks());
        current.setTitle(feed.getTitle());
        current.setUri(feed.getUri());
        current.setVisible(feed.isVisible());
        current.setName(feed.getName());

        current.setAuthor(feed.getAuthor());
        current.setExternalUrl(feed.getExternalUrl());

        it.remove();

        break;
      }
    }
    if (result != null) {
      getRawList().add(result);
    }
    return result;
  }

  /**
   * XStream FilePersistenceStrategy initialization
   * 
   * @param location
   *          Directory
   */
  public void init(File location) {
    Map<String, Class<?>> aliases = new ConcurrentHashMap<String, Class<?>>();
    aliases.put("FeedModel", FeedModel.class);
    aliases.put("FeedEntryModel", FeedEntryModel.class);
    aliases.put("author", FeedAuthorModel.class);
    this.init(location, aliases);
  }

  /**
   * Update the feedDetails in the store
   * 
   * @param feed
   *          the {@link FeedModel} to get the details from
   * @return the updated {@link FeedModel}
   */
  public FeedModel updateDetails(FeedModel feed) {
    FeedModel result = null;
    for (Iterator<FeedModel> it = getRawList().iterator(); it.hasNext();) {
      FeedModel current = it.next();
      if (current.getId().equals(feed.getId())) {
        log.info("Updating FeedsModel details");

        result = current;
        current.setId(feed.getId());
        current.setDescription(feed.getDescription());
        current.setEncoding(feed.getEncoding());
        current.setFeedType(feed.getFeedType());
        current.setImage(feed.getImage());
        current.setLink(feed.getLink());
        current.setLinks(feed.getLinks());
        current.setTitle(feed.getTitle());
        current.setUri(feed.getUri());
        current.setVisible(feed.isVisible());

        current.setAuthor(feed.getAuthor());

        current.setExternalUrl(feed.getExternalUrl());

        it.remove();

        break;
      }
    }
    if (result != null) {
      getRawList().add(result);
    }
    return result;
  }

  @Override
  public List<FeedModel> retrieveByParent(String id) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String getCollectionName() {
    return COLLECTION_NAME;
  }

}

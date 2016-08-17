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
package fr.cnes.sitools.feeds.model;

import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import fr.cnes.sitools.common.model.IResource;
import fr.cnes.sitools.common.model.Resource;

/**
 * Model for ATOM or RSS feed
 * 
 * @author AKKA Technologies
 */
@XStreamAlias("FeedModel")
public final class FeedModel implements IResource {


  public static final String RSS_2_0 = "rss_2.0";
  public static final String ATOM_1_0 = "atom_1.0";

  /** Feed identifier */
  private String id = null;

  /** Name */
  private String name = null;

  /** The type of the feed, RSS or ATOM */
  private String feedType;

  /** Encoding */
  private String encoding;

  /** URI */
  private String uri;

  /** Title */
  private String title;

  /** Description */
  private String description;

  /** A link */
  private String link;

  /** A list of links */
  private List<String> links;

  /** Image = Resource */
  private Resource image;

  /**
   * List of Entries
   */
  private List<FeedEntryModel> entries;

  /** Id of the parent object */
  private String parent;

  /** author */
  private FeedAuthorModel author;

  /** if the Feed is visible or not */
  private boolean visible = true;

  /** The source of the feed ( Opensearch, classic or External) */
  private FeedSource feedSource;

  /** The URL of the feed if the feedSource is EXTERNAL */
  private String externalUrl;

  /**
   * Gets the id value
   * 
   * @return the id
   */
  public String getId() {
    return id;
  }

  /**
   * Sets the value of id
   * 
   * @param id
   *          the id to set
   */
  public void setId(String id) {
    this.id = id;
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.cnes.sitools.common.model.IResource#getName()
   */
  @Override
  public String getName() {
    return name;
  }

  /**
   * Sets the value of feedType
   * 
   * @param feedType
   *          the feedType to set
   */
  public void setFeedType(String feedType) {
    this.feedType = feedType;
  }

  /**
   * Gets the feedType value
   * 
   * @return the feedType
   */
  public String getFeedType() {
    return feedType;
  }

  /**
   * Gets the encoding value
   * 
   * @return the encoding
   */
  public String getEncoding() {
    return encoding;
  }

  /**
   * Sets the value of encoding
   * 
   * @param encoding
   *          the encoding to set
   */
  public void setEncoding(String encoding) {
    this.encoding = encoding;
  }

  /**
   * Gets the uri value
   * 
   * @return the uri
   */
  public String getUri() {
    return uri;
  }

  /**
   * Sets the value of uri
   * 
   * @param uri
   *          the uri to set
   */
  public void setUri(String uri) {
    this.uri = uri;
  }

  /**
   * Gets the title value
   * 
   * @return the title
   */
  public String getTitle() {
    return title;
  }

  /**
   * Sets the value of title
   * 
   * @param title
   *          the title to set
   */
  public void setTitle(String title) {
    this.title = title;
  }

  /**
   * Gets the description value
   * 
   * @return the description
   */
  public String getDescription() {
    return description;
  }

  /**
   * Sets the value of description
   * 
   * @param description
   *          the description to set
   */
  public void setDescription(String description) {
    this.description = description;
  }

  /**
   * Gets the link value
   * 
   * @return the link
   */
  public String getLink() {
    return link;
  }

  /**
   * Sets the value of link
   * 
   * @param link
   *          the link to set
   */
  public void setLink(String link) {
    this.link = link;
  }

  /**
   * Gets the links value
   * 
   * @return the links
   */
  public List<String> getLinks() {
    return links;
  }

  /**
   * Sets the value of links
   * 
   * @param links
   *          the links to set
   */
  public void setLinks(List<String> links) {
    this.links = links;
  }

  /**
   * Gets the image value
   * 
   * @return the image
   */
  public Resource getImage() {
    return image;
  }

  /**
   * Sets the value of image
   * 
   * @param image
   *          the image to set
   */
  public void setImage(Resource image) {
    this.image = image;
  }

  /**
   * Gets the entries value
   * 
   * @return the entries
   */
  public List<FeedEntryModel> getEntries() {
    return entries;
  }

  /**
   * Sets the value of entries
   * 
   * @param entries
   *          the entries to set
   */
  public void setEntries(List<FeedEntryModel> entries) {
    this.entries = entries;
  }

  /**
   * Sets the value of parent
   * 
   * @param parent
   *          the parent to set
   */
  public void setParent(String parent) {
    this.parent = parent;
  }

  /**
   * Gets the parent value
   * 
   * @return the parent
   */
  public String getParent() {
    return parent;
  }

  /**
   * Gets the author value
   * 
   * @return the author
   */
  public FeedAuthorModel getAuthor() {
    return author;
  }

  /**
   * Sets the value of author
   * 
   * @param author
   *          the author to set
   */
  public void setAuthor(FeedAuthorModel author) {
    this.author = author;
  }

  /**
   * Sets the value of visible
   * 
   * @param visible
   *          the visible to set
   */
  public void setVisible(boolean visible) {
    this.visible = visible;
  }

  /**
   * Gets the visible value
   * 
   * @return the visible
   */
  public boolean isVisible() {
    return visible;
  }

  /**
   * Sets the value of feedSource
   * 
   * @param feedSource
   *          the feedSource to set
   */
  public void setFeedSource(FeedSource feedSource) {
    this.feedSource = feedSource;
  }

  /**
   * Gets the feedSource value
   * 
   * @return the feedSource
   */
  public FeedSource getFeedSource() {
    return feedSource;
  }

  /**
   * Sets the value of name
   * 
   * @param name
   *          the name to set
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Sets the value of externalUrl
   * 
   * @param externalUrl
   *          the externalUrl to set
   */
  public void setExternalUrl(String externalUrl) {
    this.externalUrl = externalUrl;
  }

  /**
   * Gets the externalUrl value
   * 
   * @return the externalUrl
   */
  public String getExternalUrl() {
    return externalUrl;
  }

}

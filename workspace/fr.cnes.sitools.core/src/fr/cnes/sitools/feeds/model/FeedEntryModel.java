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
package fr.cnes.sitools.feeds.model;

import java.util.Date;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import fr.cnes.sitools.common.model.Resource;

/**
 * Model for RSS or ATOM entry
 * 
 * @author m.gond (AKKA Technologies)
 */
@XStreamAlias("FeedEntryModel")
public final class FeedEntryModel {
  /**
   * URI
   */
  private String uri;
  /**
   * Link
   */
  private String link;
  /**
   * UpdatedDate
   */
  private Date updatedDate;
  /**
   * PublishedDate
   */
  private Date publishedDate;
  /**
   * Title
   */
  private String title;
  /**
   * Description
   */
  private String description;
  /**
   * Links
   */
  private List<String> links;
  /**
   * Author
   */
  @XStreamAlias("author")
  private FeedAuthorModel author;

  /** l'image */
  private Resource image;
  
  /**
   * Gets the uri value
   * 
   * @return the uri
   */
  public String getUri() {
    return uri;
  }

  /**
   * Gets the publishedDate value
   * @return the publishedDate
   */
  public Date getPublishedDate() {
    return publishedDate;
  }

  /**
   * Sets the value of publishedDate
   * @param publishedDate the publishedDate to set
   */
  public void setPublishedDate(Date publishedDate) {
    this.publishedDate = publishedDate;
  }

  /**
   * Gets the author value
   * @return the author
   */
  public FeedAuthorModel getAuthor() {
    return author;
  }

  /**
   * Sets the value of author
   * @param author the author to set
   */
  public void setAuthor(FeedAuthorModel author) {
    this.author = author;
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
   * Gets the updatedDate value
   * 
   * @return the updatedDate
   */
  public Date getUpdatedDate() {
    return updatedDate;
  }

  /**
   * Sets the value of updatedDate
   * 
   * @param updatedDate
   *          the updatedDate to set
   */
  public void setUpdatedDate(Date updatedDate) {
    this.updatedDate = updatedDate;
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
   * @return the image
   */
  public Resource getImage() {
    return image;
  }

  /**
   * Sets the value of image
   * @param image the image to set
   */
  public void setImage(Resource image) {
    this.image = image;
  }

}

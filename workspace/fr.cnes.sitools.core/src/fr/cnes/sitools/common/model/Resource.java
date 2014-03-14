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
package fr.cnes.sitools.common.model;

import java.io.Serializable;
import java.util.ArrayList;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

import fr.cnes.sitools.util.Property;

/**
 * Base class for Resource. Resources are used in model objects to reference
 * others objects of different domain.
 * 
 * @author jp.boignard (AKKA Technologies)
 */
@XStreamAlias("resource")
public class Resource implements Serializable, IResource {

  /**
   * serialVersionUID.
   */
  @XStreamOmitField
  private static final long serialVersionUID = 5943645674576142663L;

  /**
   * TODO use urn for resource identifier. >> Each entity must be associated
   * with a unique urn. >> if urls change, urns stay. >> Class/Application urn
   * resolver to retrieve url of a resource by urn
   * 
   * @see http://fr.wikipedia.org/wiki/Uniform_Resource_Name
   * 
   *      Exemples : urn:instance-sitools:dataset:uniquename
   *      urn:instance-sitools:dictionary:global:notion:lat
   */
  private String urn = null;

  /** Object identifier. */
  private String id;

  /** Object name. */
  private String name;

  /** Object description. */
  private String description;

  /** Object type or category. */
  private String type = null;

  /** Category */
  private Category category = null;

  /** Contextual help. */
  private String tooltip = null;

  /** Resource URL. */
  private String url = null;

  /** Default mediaType. */
  private String mediaType = null;

  /** Author. */
  private String author = null;

  /** Owner. */
  private String owner = null;

  /** Resource last update. */
  private String lastUpdate = null;

  /** Status - not stored TODO see if Resource status is needed. */
  private volatile String status = null;

  /**
   * If the dataset is authorized not Stored, only for client communication
   */
  private volatile String authorized = null;

  /** Describes if the dataset is visible */
  private volatile Boolean visible;
  
  /** A list of properties */
  private ArrayList<Property> properties;

  /**
   * Default constructor.
   */
  public Resource() {
    super();
  }

  /**
   * Gets the urn value.
   * 
   * @return the urn
   */
  public final String getUrn() {
    return urn;
  }

  /**
   * Sets the value of urn.
   * 
   * @param urntoset
   *          the urn to set
   */
  public final void setUrn(String urntoset) {
    this.urn = urntoset;
  }

  /**
   * Gets the id value.
   * 
   * @return the id
   */
  public final String getId() {
    return id;
  }

  /**
   * Sets the value of id.
   * 
   * @param idToSet
   *          the id to set
   */
  public final void setId(String idToSet) {
    this.id = idToSet;
  }

  /**
   * Gets the name value.
   * 
   * @return the name
   */
  public final String getName() {
    return name;
  }

  /**
   * Sets the value of name.
   * 
   * @param nameToSet
   *          the name to set
   */
  public final void setName(String nameToSet) {
    this.name = nameToSet;
  }

  /**
   * Gets the description value.
   * 
   * @return the description
   */
  public final String getDescription() {
    return description;
  }

  /**
   * Sets the value of description.
   * 
   * @param desc
   *          the description to set
   */
  public final void setDescription(String desc) {
    this.description = desc;
  }

  /**
   * Gets the type value.
   * 
   * @return the type
   */
  public final String getType() {
    return type;
  }

  /**
   * Sets the value of type.
   * 
   * @param typeToSet
   *          the type to set
   */
  public final void setType(String typeToSet) {
    this.type = typeToSet;
  }

  /**
   * Gets the ToolTip value.
   * 
   * @return the ToolTip
   */
  public final String getTooltip() {
    return tooltip;
  }

  /**
   * Sets the value of ToolTip.
   * 
   * @param toolt
   *          the ToolTip to set
   */
  public final void setTooltip(String toolt) {
    this.tooltip = toolt;
  }

  /**
   * Gets the URL value.
   * 
   * @return the URL
   */
  public final String getUrl() {
    return url;
  }

  /**
   * Sets the value of URL.
   * 
   * @param urlToSet
   *          the URL to set
   */
  public final void setUrl(String urlToSet) {
    this.url = urlToSet;
  }

  /**
   * Gets the mediaType value.
   * 
   * @return the mediaType
   */
  public final String getMediaType() {
    return mediaType;
  }

  /**
   * Sets the value of mediaType.
   * 
   * @param mediat
   *          the mediaType to set
   */
  public final void setMediaType(String mediat) {
    this.mediaType = mediat;
  }

  /**
   * Gets the lastUpdate value.
   * 
   * @return the lastUpdate
   */
  public final String getLastUpdate() {
    return lastUpdate;
  }

  /**
   * Sets the value of lastUpdate.
   * 
   * @param lastUpd
   *          the lastUpdate to set
   */
  public final void setLastUpdate(String lastUpd) {
    this.lastUpdate = lastUpd;
  }

  /**
   * Gets the author value.
   * 
   * @return the author
   */
  public final String getAuthor() {
    return author;
  }

  /**
   * Sets the value of author.
   * 
   * @param aut
   *          the author to set
   */
  public final void setAuthor(String aut) {
    this.author = aut;
  }

  /**
   * Gets the owner value.
   * 
   * @return the owner
   */
  public final String getOwner() {
    return owner;
  }

  /**
   * Sets the value of owner.
   * 
   * @param own
   *          the owner to set
   */
  public final void setOwner(String own) {
    this.owner = own;
  }

  /**
   * Gets the status value.
   * 
   * @return the status
   */
  public final String getStatus() {
    return status;
  }

  /**
   * Sets the value of status
   * 
   * @param stat
   *          the status to set
   */
  public final void setStatus(String stat) {
    this.status = stat;
  }

  /**
   * Gets the category value
   * 
   * @return the category
   */
  public final Category getCategory() {
    return category;
  }

  /**
   * Sets the value of category
   * 
   * @param category
   *          the category to set
   */
  public final void setCategory(Category category) {
    this.category = category;
  }

  /**
   * Gets the authorized value
   * 
   * @return the authorized
   */
  public final String getAuthorized() {
    return authorized;
  }

  /**
   * Sets the value of authorized
   * 
   * @param authorized
   *          the authorized to set
   */
  public final void setAuthorized(String authorized) {
    this.authorized = authorized;
  }

  /**
   * Gets the visible value
   * @return the visible
   */
  public final Boolean getVisible() {
    return visible;
  }

  /**
   * Sets the value of visible
   * @param visible the visible to set
   */
  public final void setVisible(Boolean visible) {
    this.visible = visible;
  }

  /**
   * Gets the properties value
   * @return the properties
   */
  public final ArrayList<Property> getProperties() {
    return properties;
  }

  /**
   * Sets the value of properties
   * @param properties the properties to set
   */
  public final void setProperties(ArrayList<Property> properties) {
    this.properties = properties;
  }
  
  /**
   * Get the property by name
   * @param name name of the property
   * @return a Property
   */
  public final Property getPropertyByName(String name) {
    Property prop = null;
    for (Property property : properties) {
      if (name != null && name.equals(property.getName())) {
        return property;
      }
    }
    return prop;
    
  }
  

  
}

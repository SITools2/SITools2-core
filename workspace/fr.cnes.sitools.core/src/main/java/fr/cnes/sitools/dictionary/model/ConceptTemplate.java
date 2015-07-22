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
package fr.cnes.sitools.dictionary.model;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

import fr.cnes.sitools.common.model.IResource;
import fr.cnes.sitools.util.Property;

/**
 * Template of an entry / concept in a dictionary
 * 
 * @author jp.boignard (AKKA Technologies)
 */
public class ConceptTemplate implements IResource, Serializable {

  /** serialVersionUID */
  private static final long serialVersionUID = 4995392080123319157L;

  /** Object identifier */
  private String id;

  /** Object name */
  private String name;

  /** Object description, tooltip */
  private String description;

  /** Notion type */
  private String type;

  /** Notion type */
  private String url;

  /** Others properties */
  private List<Property> properties = null;

  /** Default constructor */
  public ConceptTemplate() {
    super();
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  /**
   * Set the name of the concept template
   * 
   * @param name
   *          the name to set
   */
  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  /**
   * Set the description of the concept
   * 
   * @param description
   *          the description to set
   */
  public void setDescription(String description) {
    this.description = description;
  }

  /**
   * Get the properties of the concept
   * 
   * @return the properties
   */
  public List<Property> getProperties() {
    return properties;
  }

  /**
   * Set the properties of the concept
   * 
   * @param properties
   *          the properties to set
   */
  public void setProperties(List<Property> properties) {
    this.properties = properties;
  }

  /**
   * Get the type of concept
   * 
   * @return the type
   */
  public String getType() {
    return type;
  }

  /**
   * Set the type of the concept
   * 
   * @param type
   *          the concept type
   */
  public void setType(String type) {
    this.type = type;
  }

  /**
   * Get the url of the concept
   * 
   * @return the url of the concept
   */
  public String getUrl() {
    return url;
  }

  /**
   * Set the url of the concept
   * 
   * @param url
   *          the url of the concept
   */
  public void setUrl(String url) {
    this.url = url;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((description == null) ? 0 : description.hashCode());
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    result = prime * result + ((properties == null) ? 0 : properties.hashCode());
    result = prime * result + ((type == null) ? 0 : type.hashCode());
    result = prime * result + ((url == null) ? 0 : url.hashCode());
    return result;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    ConceptTemplate other = (ConceptTemplate) obj;
    if (description == null) {
      if (other.description != null) {
        return false;
      }
    }
    else if (!description.equals(other.description)) {
      return false;
    }
    if (id == null) {
      if (other.id != null) {
        return false;
      }
    }
    else if (!id.equals(other.id)) {
      return false;
    }
    if (name == null) {
      if (other.name != null) {
        return false;
      }
    }
    else if (!name.equals(other.name)) {
      return false;
    }
    if (properties == null) {
      if (other.properties != null) {
        return false;
      }
    }
    else if (!properties.equals(other.properties)) {
      return false;
    }
    if (type == null) {
      if (other.type != null) {
        return false;
      }
    }
    else if (!type.equals(other.type)) {
      return false;
    }
    if (url == null) {
      if (other.url != null) {
        return false;
      }
    }
    else if (!url.equals(other.url)) {
      return false;
    }
    return true;
  }

  /**
   * Return a property from its name
   * 
   * @param propertyName
   *          the name of the property
   * @return the property of the following propertyName
   */
  public Property getPropertyFromName(String propertyName) {
    Property result = null;
    for (Iterator<Property> iterator = getProperties().iterator(); iterator.hasNext() && result == null;) {
      Property prop = iterator.next();
      if (prop.getName().equals(propertyName)) {
        result = prop;
      }
    }
    return result;
  }

}

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
package fr.cnes.sitools.common.model;

/**
 * Base class DTO for SITools plug-ins
 * 
 * @author AKKA technologies
 */
public abstract class AbstractPluginsDescriptionDTO {

  /**
   * Plug-in name
   */
  private String name;

  /**
   * Description
   */
  private String description;

  /**
   * Plug-in class name
   */
  private String className;
  /** The author of the class */
  private String classAuthor;
  /** The version of the class */
  private String classVersion;
  /** The owner of the class */
  private String classOwner;
  

  /**
   * Gets the name value
   * 
   * @return the name
   */
  public final String getName() {
    return name;
  }

  /**
   * Sets the value of name
   * 
   * @param name
   *          the name to set
   */
  public final void setName(String name) {
    this.name = name;
  }

  /**
   * Gets the description value
   * 
   * @return the description
   */
  public final String getDescription() {
    return description;
  }

  /**
   * Sets the value of description
   * 
   * @param description
   *          the description to set
   */
  public final void setDescription(String description) {
    this.description = description;
  }

  /**
   * Gets the className value
   * 
   * @return the className
   */
  public final String getClassName() {
    return className;
  }

  /**
   * Sets the value of className
   * 
   * @param className
   *          the className to set
   */
  public final void setClassName(String className) {
    this.className = className;
  }

  /**
   * Gets the classAuthor value
   * 
   * @return the classAuthor
   */
  public String getClassAuthor() {
    return classAuthor;
  }

  /**
   * Sets the value of classAuthor
   * 
   * @param classAuthor
   *          the classAuthor to set
   */
  public void setClassAuthor(String classAuthor) {
    this.classAuthor = classAuthor;
  }

  /**
   * Gets the classVersion value
   * 
   * @return the classVersion
   */
  public String getClassVersion() {
    return classVersion;
  }

  /**
   * Sets the value of classVersion
   * 
   * @param classVersion
   *          the classVersion to set
   */
  public void setClassVersion(String classVersion) {
    this.classVersion = classVersion;
  }

  /**
   * Gets the classOwner value
   * @return the classOwner
   */
  public String getClassOwner() {
    return classOwner;
  }

  /**
   * Sets the value of classOwner
   * @param classOwner the classOwner to set
   */
  public void setClassOwner(String classOwner) {
    this.classOwner = classOwner;
  }

}

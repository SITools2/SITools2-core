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
package fr.cnes.sitools.common.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.restlet.engine.Helper;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import fr.cnes.sitools.common.model.ExtensionParameter;
import fr.cnes.sitools.common.model.IResource;

/**
 * Base class for all plugin models
 * 
 * @param <E>
 *          the parameter extension
 * @author m.marseille (AKKA Technologies)
 */
public abstract class ExtensionModelDTO<E extends ExtensionParameter> extends Helper implements IResource, Serializable {
  /** serialVersionUID */
  private static final long serialVersionUID = -7971395419463412426L;

  /**
   * ID
   */
  private String id;

  /**
   * Identifier of the model.
   */
  private String name;

  /**
   * Description of the model.
   */
  private String description;

  /**
   * Converter className
   */
  private String className;

  /**
   * descriptionAction of the model
   */
  private String descriptionAction;

  /**
   * Converter class version
   */
  private String classVersion;

  /**
   * Converter class author
   */
  private String classAuthor;

  /**
   * Owner of the model
   */
  private String classOwner;

  /**
   * Converter current class version
   */
  private String currentClassVersion;

  /**
   * Converter current class author
   */
  private String currentClassAuthor;

  /**
   * List of parameters used for JSON serialization
   */
  @XStreamAlias("parameters")
  private List<E> parameters;

  /**
   * Constructor
   */
  public ExtensionModelDTO() {
    super();
    this.parameters = new ArrayList<E>();
  }

  /**
   * Sets the value of id
   * 
   * @param id
   *          the id to set
   */
  public final void setId(String id) {
    this.id = id;
  }

  /**
   * Gets the id value
   * 
   * @return the id
   */
  public final String getId() {
    return id;
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
   * Gets the name value
   * 
   * @return the name
   */
  public final String getName() {
    return name;
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
   * Gets the description value
   * 
   * @return the description
   */
  public final String getDescription() {
    return description;
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
   * Gets the className value
   * 
   * @return the className
   */
  public final String getClassName() {
    return className;
  }

  /**
   * Sets the value of descriptionAction
   * 
   * @param descriptionAction
   *          the descriptionAction to set
   */
  public final void setDescriptionAction(String descriptionAction) {
    this.descriptionAction = descriptionAction;
  }

  /**
   * Gets the descriptionAction value
   * 
   * @return the descriptionAction
   */
  public final String getDescriptionAction() {
    return descriptionAction;
  }

  /**
   * Sets the value of classVersion
   * 
   * @param classVersion
   *          the classVersion to set
   */
  public final void setClassVersion(String classVersion) {
    this.classVersion = classVersion;
  }

  /**
   * Gets the classVersion value
   * 
   * @return the classVersion
   */
  public final String getClassVersion() {
    return classVersion;
  }

  /**
   * Sets the value of classAuthor
   * 
   * @param classAuthor
   *          the classAuthor to set
   */
  public final void setClassAuthor(String classAuthor) {
    this.classAuthor = classAuthor;
  }

  /**
   * Gets the classAuthor value
   * 
   * @return the classAuthor
   */
  public final String getClassAuthor() {
    return classAuthor;
  }

  /**
   * Gets the classOwner value
   * 
   * @return the classOwner
   */
  public String getClassOwner() {
    return classOwner;
  }

  /**
   * Sets the value of classOwner
   * 
   * @param classOwner
   *          the classOwner to set
   */
  public void setClassOwner(String classOwner) {
    this.classOwner = classOwner;
  }

  /**
   * Sets the value of currentClassVersion
   * 
   * @param currentClassVersion
   *          the currentClassVersion to set
   */
  public final void setCurrentClassVersion(String currentClassVersion) {
    this.currentClassVersion = currentClassVersion;
  }

  /**
   * Gets the currentClassVersion value
   * 
   * @return the currentClassVersion
   */
  public final String getCurrentClassVersion() {
    return currentClassVersion;
  }

  /**
   * Sets the value of currentClassAuthor
   * 
   * @param currentClassAuthor
   *          the currentClassAuthor to set
   */
  public final void setCurrentClassAuthor(String currentClassAuthor) {
    this.currentClassAuthor = currentClassAuthor;
  }

  /**
   * Gets the currentClassAuthor value
   * 
   * @return the currentClassAuthor
   */
  public final String getCurrentClassAuthor() {
    return currentClassAuthor;
  }

  /**
   * Sets the value of parametersList
   * 
   * @param parametersList
   *          the parametersList to set
   */
  public final void setParameters(List<E> parametersList) {
    this.parameters = parametersList;
  }

  /**
   * Gets the parametersList value
   * 
   * @return the parametersList
   */
  public final List<E> getParameters() {
    return parameters;
  }

}

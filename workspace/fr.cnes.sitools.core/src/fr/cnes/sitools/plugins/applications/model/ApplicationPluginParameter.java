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
package fr.cnes.sitools.plugins.applications.model;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import fr.cnes.sitools.common.model.ExtensionParameter;

/**
 * Class representing a parameter for an ApplicationPluginModel
 * 
 * 
 * @author m.gond (AKKA Technologies)
 */
@XStreamAlias("ApplicationPluginParameter")
public final class ApplicationPluginParameter extends ExtensionParameter {

  /**
   * Default constructor
   */
  public ApplicationPluginParameter() {
    super();
    // TODO Auto-generated constructor stub
  }

  /**
   * Constructor with the name and the description of the parameter
   * 
   * @param name
   *          the name of the parameter
   * @param description
   *          the description of the parameter
   */
  public ApplicationPluginParameter(String name, String description) {
    super(name, description);
    // TODO Auto-generated constructor stub
  }

}

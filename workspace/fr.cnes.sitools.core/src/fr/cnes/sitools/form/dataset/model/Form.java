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
package fr.cnes.sitools.form.dataset.model;

import java.util.List;

import fr.cnes.sitools.form.model.AbstractFormModel;
import fr.cnes.sitools.form.model.AbstractParameter;

/**
 * Define a DataSet Form
 * 
 * @author jp.boignard (AKKA Technologies)
 */
public final class Form extends AbstractFormModel {

  /**
   * List of Form Components
   */
  private List<AbstractParameter> parameters;

  /**
   * Constructor
   */
  public Form() {
    super();
  }

  /**
   * Gets the parameters value
   * 
   * @return the parameters
   */
  public List<AbstractParameter> getParameters() {
    return parameters;
  }

  /**
   * Sets the value of parameters
   * 
   * @param parameters
   *          the parameters to set
   */
  public void setParameters(List<AbstractParameter> parameters) {
    this.parameters = parameters;
  }

}

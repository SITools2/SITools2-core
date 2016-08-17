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
package fr.cnes.sitools.form.dataset.model;

import java.util.List;

import fr.cnes.sitools.form.model.AbstractParameter;

/**
 * Real class of parameters
 * 
 * @author BAILLAGOU
 * @version 1.0 01-09-2010 08:59:02
 */
public final class Parameters {

  /**
   * List of single parameters
   */
  private List<AbstractParameter> parameters;

  /**
   * First parameter
   */
  private AbstractParameter startParameter;

  /**
   * Constructor
   */
  public Parameters() {
    super();
  }

  /**
   * Get the parameters
   * 
   * @return the abstractParameters
   */
  public List<AbstractParameter> getParameters() {
    return parameters;
  }

  /**
   * Set the parameters
   * 
   * @param parameters
   *          List<AbstractParameter> to set
   */
  public void setParameters(final List<AbstractParameter> parameters) {
    this.parameters = parameters;
  }

  /**
   * Get the first parameter
   * 
   * @return the startParameter
   */
  public AbstractParameter getStartParameter() {
    return startParameter;
  }

  /**
   * Set the first parameter
   * 
   * @param startParameter
   *          the startParameter to set
   */
  public void setStartParameter(AbstractParameter startParameter) {
    this.startParameter = startParameter;
  }

}

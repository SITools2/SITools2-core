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
package fr.cnes.sitools.applications.basic;

import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;

import fr.cnes.sitools.common.SitoolsResource;

/**
 * A basic App Resource attached to a basicApp Only used as an example
 * 
 * 
 * @author m.gond
 */
public class BasicResource extends SitoolsResource {
  /** A param */
  private String param;

  @Override
  public void sitoolsDescribe() {
    // TODO Auto-generated method stub

  }

  /*
   * (non-Javadoc)
   * 
   * @see org.restlet.resource.ServerResource#get()
   */
  @Override
  protected Representation get() {
    // TODO Auto-generated method stub
    return new StringRepresentation("OK OK ----- param = " + param);
  }

  /**
   * set the param value
   * 
   * @param param
   *          the param value to set
   * 
   * */
  public void setParam(String param) {
    this.param = param;
  }

}

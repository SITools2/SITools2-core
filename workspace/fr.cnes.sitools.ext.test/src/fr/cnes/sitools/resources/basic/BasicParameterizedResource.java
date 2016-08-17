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
package fr.cnes.sitools.resources.basic;

import org.restlet.ext.wadl.MethodInfo;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;

import fr.cnes.sitools.common.resource.SitoolsParameterizedResource;

/**
 * Test for parameterized resource
 * 
 * @author m.marseille (AKKA Technologies)
 * 
 */
public class BasicParameterizedResource extends SitoolsParameterizedResource {

  /** the text sent by url*/
  private String textSent;
  
  @Override
  public void doInit() {
    super.doInit();
    this.textSent = (String) this.getRequestAttributes().get("yourtext");
  }

  @Override
  @Get
  public Representation get() {
    return new StringRepresentation("This is a dynamic resource sending the text : \n"
        + getModel().getParametersMap().get("text").getValue() + "\n" + this.textSent);
  }

  @Override
  public void sitoolsDescribe() {
    setName("BasicParameterizedResource");
    setDescription("Test class for dynamic resource");
    setNegotiated(false);
  }
  
  @Override
  protected void describeGet(MethodInfo info) {
    this.addInfo(info);
  }
  
}

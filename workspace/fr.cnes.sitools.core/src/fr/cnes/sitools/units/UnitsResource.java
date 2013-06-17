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
package fr.cnes.sitools.units;

import org.restlet.ext.wadl.MethodInfo;
import org.restlet.ext.wadl.ParameterInfo;
import org.restlet.ext.wadl.ParameterStyle;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Get;

import fr.cnes.sitools.common.SitoolsResource;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.units.dimension.model.SitoolsUnit;
import fr.cnes.sitools.util.RESTUtils;

/**
 * 
 * Resource for single unit
 * 
 * @author jp.boignard (AKKA technologies)
 */
public final class UnitsResource extends SitoolsResource {

  /** scope request parameter */
  private volatile String unitParam = null;

  @Override
  public void sitoolsDescribe() {
    setName("UnitsResource");
    setDescription("Units resource");
  }

  @Override
  protected void doInit() {
    super.doInit();
    int unitStringLength = this.getRequest().getRootRef().toString().length() + 1;
    unitParam = RESTUtils.decode(this.getRequest().getResourceRef().toString()
        .substring(unitStringLength > 1 ? unitStringLength : 2));
  }

  /**
   * Get single unit representation
   * @param variant the restlet variant sent
   * @return a JSON single unit representation
   */
  @Get
  public Representation get(Variant variant) {
    Response response = null;
    Representation representation = null;
    SitoolsUnit unit = new SitoolsUnit(unitParam);
    if (unit.getUnit() != null) {
      response = new Response(true, unit, SitoolsUnit.class, "unit");
    }
    else {
      response = new Response(false, "unit.not.known");
    }
    representation = getRepresentation(response, variant);
    return representation;
  }
  
  @Override
  public void describeGet(MethodInfo info) {
    info.setDocumentation("Method to check if a unit is known or not");
    this.addStandardGetRequestInfo(info);
    ParameterInfo pi = new ParameterInfo("unit", true, "xs:string", ParameterStyle.TEMPLATE, "String representation of the unit following javax.measure framework rules, except ** for exponents.");
    info.getRequest().getParameters().add(pi);
    this.addStandardSimpleResponseInfo(info);
  }

}

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
package fr.cnes.sitools.units;

import java.util.ArrayList;
import java.util.List;

import javax.measure.unit.SystemOfUnits;
import javax.measure.unit.Unit;

import org.restlet.ext.wadl.MethodInfo;
import org.restlet.ext.wadl.ParameterInfo;
import org.restlet.ext.wadl.ParameterStyle;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Get;

import fr.cnes.sitools.common.SitoolsResource;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.engine.SitoolsEngine;
import fr.cnes.sitools.units.dimension.helper.DimensionHelper;

/**
 * Resource for collections of units
 * 
 * @author jp.boignard (AKKA technologies)
 */
public final class UnitsCollectionResource extends SitoolsResource {

  /** scope request parameter */
  private volatile String scope = null;

  @Override
  public void sitoolsDescribe() {
    setName("UnitsCollectionResource");
    setDescription("Units collection resource");
    setNegotiated(false);
  }

  @Override
  protected void doInit() {
    super.doInit();
    scope = (String) this.getRequest().getAttributes().get("scope");
  }

  @Override
  @Get
  public Representation get(Variant variant) {
    Response response = null;
    if (scope == null || scope.equals("")) {
      response = new Response(false, "system.scope.not.found");
    } 
    else {
      List<DimensionHelper> helpers = SitoolsEngine.getInstance().getRegisteredDimensionHelpers();
      List<String> systemNames = new ArrayList<String>();
      for (DimensionHelper helper : helpers) {
        systemNames.addAll(helper.getSystems());
      }
      
      // Filling in the units
      List<String> unitNames = new ArrayList<String>();
      if (systemNames.size() == 0) {
        response = new Response(false, "no.system.found");
        return getRepresentation(response, variant);
      }
      else if (scope.equalsIgnoreCase("all")) {
        for (String systemName : systemNames) {
          unitNames.addAll(getUnitsFromSystem(systemName));
        }
      }
      else if (systemNames.contains(scope)) {
        unitNames.addAll(getUnitsFromSystem(scope));
      }
      else {
        response = new Response(false, "scope.not.recognized");
        return getRepresentation(response, variant);
      }
      
      // Response
      response = new Response(true, unitNames, String.class, "units");
    }
    return getRepresentation(response, variant);
  }
  
  @Override
  public void describeGet(MethodInfo info) {
    info.setDocumentation("Method to get the list of available units considering the system defined by the scope");
    this.addStandardGetRequestInfo(info);
    ParameterInfo pi = new ParameterInfo("scope", true, "xs:string", ParameterStyle.TEMPLATE, "Unit system name to consider (ALL to get all of them)");
    pi.setDefaultValue("ALL");
    info.getRequest().getParameters().add(pi);
    this.addStandardSimpleResponseInfo(info);
  }

  /**
   * Method to get the list of unitnames in a system
   * @param systemName the name of the system
   * @return  the list of units as strings
   */
  private List<String> getUnitsFromSystem(String systemName) {
    List<String> unitNames = new ArrayList<String>();
    List<DimensionHelper> helpers = SitoolsEngine.getInstance().getRegisteredDimensionHelpers();
    for (DimensionHelper helper : helpers) {
      for (SystemOfUnits system : helper.getRegisteredSystems()) {
        if (system.getClass().getSimpleName().equals(systemName)) {
          for (Unit<?> unit : system.getUnits()) {
            String symb = unit.getSymbol();
            if (symb != null) {
              unitNames.add(symb);
            }
          }
        }
      }
    }
    return unitNames;
  }

}

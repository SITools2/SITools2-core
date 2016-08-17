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
package fr.cnes.sitools.units.dimension;

import java.util.ArrayList;
import java.util.List;

import org.restlet.ext.wadl.MethodInfo;
import org.restlet.ext.wadl.ParameterInfo;
import org.restlet.ext.wadl.ParameterStyle;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Get;

import com.thoughtworks.xstream.XStream;

import fr.cnes.sitools.common.SitoolsResource;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.engine.SitoolsEngine;
import fr.cnes.sitools.units.dimension.helper.DimensionHelper;

/**
 * Resource to handle the collection of unit converters implemented
 * 
 * @author m.marseille
 */
public final class UnitConvertersCollectionResource extends SitoolsResource {

  /** Helper class name */
  private String helperClassName;

  @Override
  public void sitoolsDescribe() {
    setName("UnitConvertersCollectionResource");
    setDescription("UnitConvertersCollectionResource");
    setNegotiated(false);
  }

  @Override
  public void doInit() {
    super.doInit();
    setHelperClassName((String) this.getRequest().getAttributes().get("helperClassName"));
  }

  /**
   * Get the Dimension helpers descriptions
   * 
   * @param variant
   *          restlet variant
   * @return a representation of the list of dimension helpers
   */
  @Get
  public Representation getResourceJSON(Variant variant) {
    Response response;
    SitoolsEngine sitoolsEng = SitoolsEngine.getInstance();
    List<DimensionHelper> listResources = new ArrayList<DimensionHelper>(sitoolsEng.getRegisteredDimensionHelpers());
    if (helperClassName == null || helperClassName.equals("")) {
      response = new Response(true, listResources, DimensionHelper.class, "dimensionHelpers");
    }
    else {
      for (DimensionHelper helper : listResources) {
        if (helper.getClass().getCanonicalName().equals(helperClassName)) {
          response = new Response(true, helper, DimensionHelper.class, "dimensionHelper");
          return getRepresentation(response, variant);
        }
      }
      response = new Response(false, "dimension.helper.not.found");
    }
    return getRepresentation(response, variant);
  }

  @Override
  public void describeGet(MethodInfo info) {
    info.setDocumentation("Method to retrieve the Dimension helpers descriptions");
    this.addStandardGetRequestInfo(info);
    ParameterInfo pid = new ParameterInfo("helperClassName", true, "xs:string", ParameterStyle.TEMPLATE,
        "Dimension helper class name");
    info.getRequest().getParameters().add(pid);
    this.addStandardSimpleResponseInfo(info);
  }

  @Override
  public void configure(XStream xstream, Response response) {
    super.configure(xstream, response);
    xstream.omitField(DimensionHelper.class, "convs");
    xstream.omitField(DimensionHelper.class, "sys");

  }

  /**
   * Sets the value of helperClassName
   * 
   * @param helperClassName
   *          the helperClassName to set
   */
  public void setHelperClassName(String helperClassName) {
    this.helperClassName = helperClassName;
  }

  /**
   * Gets the helperClassName value
   * 
   * @return the helperClassName
   */
  public String getHelperClassName() {
    return helperClassName;
  }

}

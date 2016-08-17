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
import java.util.logging.Level;

import javax.measure.unit.Unit;

import org.restlet.data.Status;
import org.restlet.ext.wadl.MethodInfo;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ResourceException;

import fr.cnes.sitools.common.model.ResourceCollectionFilter;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.units.dimension.model.SitoolsDimension;
import fr.cnes.sitools.units.dimension.model.SitoolsUnit;

/**
 * Class resource to handle dimension collection
 * 
 * @author m.marseille (AKKA technologies)
 */
public final class DimensionCollectionResource extends AbstractDimensionResource {

  @Override
  public void sitoolsDescribe() {
    setName("DimensionCollectionResource");
    setDescription("Resource to handle collection of dimension");
    setNegotiated(false);
  }

  /**
   * Create a new dimension
   * 
   * @param representation
   *          the dimension representation
   * @param variant
   *          the restlet variant
   * @return a response representation
   */
  @Post
  public Representation createDimension(Representation representation, Variant variant) {
    SitoolsDimension input = null;

    try {
      input = getObject(representation);

      if (getStore().retrieve(input.getName()) != null) {
        trace(Level.INFO, "Cannot create the unit system");
        Response response = new Response(false, "dimension.already.exists : " + input.getName());
        return getRepresentation(response, variant);
      }

      if (!input.getName().matches("^[a-zA-Z0-9\\-\\.\\_]+$")) {
        trace(Level.INFO, "Cannot create the unit system");
        Response response = new Response(false, "dimension.name.malformed : " + input.getName());
        return getRepresentation(response, variant);
      }

      input.setId(input.getName());

      // Test to recognize units
      for (SitoolsUnit unit : input.getUnits()) {
        unit.setUnitName(unit.getUnitName().replaceAll("\\.", "\\*"));
        unit.setUnitName(unit.getUnitName().replaceAll("\\*\\*", "\\^"));
        unit = new SitoolsUnit(unit.getUnitName());
        Unit<?> u = unit.getUnit();
        if (u == null) {
          trace(Level.INFO, "Cannot create the unit system");
          Response response = new Response(false, "unit.definition.not.recognized : " + unit.getUnitName());
          return getRepresentation(response, variant);
        }
      }

      SitoolsDimension output = getStore().create(input);

      trace(Level.INFO, "Create the unit system " + output.getName());

      Response response = new Response(true, output, SitoolsDimension.class, "dimension");
      return getRepresentation(response, variant);
    }
    catch (ResourceException e) {
      trace(Level.INFO, "Cannot create the unit system");
      getLogger().log(Level.INFO, null, e);
      throw e;
    }
    catch (Exception e) {
      trace(Level.INFO, "Cannot create the unit system");
      getLogger().log(Level.WARNING, null, e);
      throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
    }
  }

  @Override
  public void describePost(MethodInfo info) {
    info.setDocumentation("Method to create a new Dimension");
    this.addStandardPostOrPutRequestInfo(info);
    this.addStandardSimpleResponseInfo(info);
  }

  @Get
  @Override
  public Representation get(Variant variant) {

    ResourceCollectionFilter filter = new ResourceCollectionFilter(getRequest());
    List<SitoolsDimension> resourceList = getStore().getList(filter);
    List<SitoolsDimension> resourceArray = new ArrayList<SitoolsDimension>();
    for (SitoolsDimension resources : resourceList) {
      resources.setConsistent(resources.isConsistent());
      resourceArray.add(resources);
    }
    int total = resourceArray.size();
    resourceArray = getStore().getPage(filter, resourceArray);
    trace(Level.FINE, "View available unit systems");
    Response response = new Response(true, resourceArray, SitoolsDimension.class, "dimensions");
    response.setTotal(total);
    return getRepresentation(response, variant);

  }

  @Override
  public void describeGet(MethodInfo info) {
    info.setDocumentation("Method to retrieve all stored dimensions");
    this.addStandardGetRequestInfo(info);
    this.addStandardSimpleResponseInfo(info);
    this.addStandardResourceCollectionFilterInfo(info);
  }

}

/*******************************************************************************
 * Copyright 2010-2014 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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

import java.util.logging.Level;

import javax.measure.unit.Unit;

import org.restlet.data.Status;
import org.restlet.ext.wadl.MethodInfo;
import org.restlet.ext.wadl.ParameterInfo;
import org.restlet.ext.wadl.ParameterStyle;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.Put;
import org.restlet.resource.ResourceException;

import com.thoughtworks.xstream.XStream;

import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.units.dimension.model.SitoolsDimension;
import fr.cnes.sitools.units.dimension.model.SitoolsUnit;

/**
 * Resource to handle a single dimension
 * 
 * @author m.marseille (AKKA technologies)
 */
public final class DimensionResource extends AbstractDimensionResource {

  /** Identifier of the dimension */
  private String dimensionId;

  @Override
  public void sitoolsDescribe() {
    setName("DimensionResource");
    setDescription("Resource to handle a single dimension");
    setNegotiated(false);
  }

  @Override
  public void doInit() {
    super.doInit();
    dimensionId = (String) this.getRequest().getAttributes().get("dimensionId");
  }

  @Get
  @Override
  public Representation get(Variant variant) {
    Response response = null;
    if (dimensionId != null) {
      SitoolsDimension dimension = getStore().retrieve(dimensionId);
      if (dimension != null) {
        dimension.setConsistent(dimension.isConsistent());
        trace(Level.FINE, "Edit information about the unit system " + dimension.getName());
        response = new Response(true, dimension, SitoolsDimension.class, "dimension");
      }
      else {
        trace(Level.INFO, "Cannot ddit information about the unit system");
        response = new Response(false, "dimension.not.found");
      }
    }
    return getRepresentation(response, variant);
  }

  @Override
  public void describeGet(MethodInfo info) {
    info.setDocumentation("Method to retrieve a Dimension by ID");
    this.addStandardGetRequestInfo(info);
    ParameterInfo pid = new ParameterInfo("dimensionId", true, "xs:string", ParameterStyle.TEMPLATE,
        "Dimension identifier");
    info.getRequest().getParameters().add(pid);
    this.addStandardSimpleResponseInfo(info);
  }

  @Override
  public void configure(XStream xstream, Response response) {
    super.configure(xstream, response);
    xstream.alias("dimension", Object.class, SitoolsDimension.class);
  }

  @Put
  @Override
  public Representation put(Representation representation, Variant variant) {
    SitoolsDimension output = null;
    try {
      SitoolsDimension input = null;
      if (representation != null) {
        input = getObject(representation);

        // Test to recognize units
        for (SitoolsUnit unit : input.getUnits()) {
          unit.setUnitName(unit.getUnitName().replaceAll("\\.", "\\*"));
          unit.setUnitName(unit.getUnitName().replaceAll("\\*\\*", "\\^"));
          unit = new SitoolsUnit(unit.getUnitName());
          Unit<?> u = unit.getUnit();
          if (u == null) {
            trace(Level.INFO, "Cannot update information for the unit system " + input.getName());
            Response response = new Response(false, "unit.definition.not.recognized : " + unit.getUnitName());
            return getRepresentation(response, variant);
          }
        }

        // Business service
        input.setConsistent(input.isConsistent());
        output = getStore().update(input);

      }

      Response response = null;
      if (output != null) {
        output.setConsistent(output.isConsistent());
        trace(Level.INFO, "Update information for the unit system - id: " + getDimensionId());
        response = new Response(true, output, SitoolsDimension.class, "dimension");
      }
      else {
        trace(Level.INFO, "Cannot update information for the unit system - id: " + getDimensionId());
        response = new Response(false, "Can not validate dimension");
      }
      return getRepresentation(response, variant);

    }
    catch (ResourceException e) {
      trace(Level.INFO, "Cannot update information for the unit system - id: " + getDimensionId());
      getLogger().log(Level.INFO, null, e);
      throw e;
    }
    catch (Exception e) {
      trace(Level.INFO, "Cannot update information for the unit system - id: " + getDimensionId());
      getLogger().log(Level.WARNING, null, e);
      throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
    }
  }

  @Override
  public void describePut(MethodInfo info) {
    info.setDocumentation("Method to modify a Dimension by ID, sending its new representation");
    this.addStandardPostOrPutRequestInfo(info);
    ParameterInfo pid = new ParameterInfo("dimensionId", true, "xs:string", ParameterStyle.TEMPLATE,
        "Dimension identifier");
    info.getRequest().getParameters().add(pid);
    this.addStandardSimpleResponseInfo(info);
    this.addStandardInternalServerErrorInfo(info);
  }

  @Delete
  @Override
  public Representation delete(Variant variant) {
    try {
      SitoolsDimension resource = getStore().retrieve(dimensionId);
      Response response = null;
      if (resource != null) {
        // Business service
        getStore().delete(dimensionId);
        trace(Level.INFO, "Delete the unit system " + resource.getName());
        // Response
        response = new Response(true, "dimension.deleted.success");
      }
      else {
        trace(Level.INFO, "Cannot delete the unit system - id: " + getDimensionId());
        response = new Response(false, "resourceplugin.deleted.failure");
      }
      return getRepresentation(response, variant);
    }
    catch (ResourceException e) {
      trace(Level.INFO, "Cannot delete the unit system - id: " + getDimensionId());
      getLogger().log(Level.INFO, null, e);
      throw e;
    }
    catch (Exception e) {
      trace(Level.INFO, "Cannot delete the unit system - id: " + getDimensionId());
      getLogger().log(Level.WARNING, null, e);
      throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
    }
  }

  @Override
  public void describeDelete(MethodInfo info) {
    info.setDocumentation("Method to delete a Dimension by ID");
    this.addStandardGetRequestInfo(info);
    ParameterInfo pid = new ParameterInfo("dimensionId", true, "xs:string", ParameterStyle.TEMPLATE,
        "Dimension identifier");
    info.getRequest().getParameters().add(pid);
    this.addStandardSimpleResponseInfo(info);
    this.addStandardInternalServerErrorInfo(info);
  }

  /**
   * Gets the dimensionId value
   * 
   * @return the dimensionId
   */
  public String getDimensionId() {
    return dimensionId;
  }

}

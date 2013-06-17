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

import java.util.ArrayList;
import java.util.List;

import org.restlet.ext.wadl.MethodInfo;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Get;

import fr.cnes.sitools.common.SitoolsResource;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.engine.SitoolsEngine;
import fr.cnes.sitools.units.dimension.helper.DimensionHelper;

/**
 * Resource to get the collection of known system of units
 * @author m.marseille (AKKA technologies)
 */
public final class SystemsCollectionResource extends SitoolsResource {

  @Override
  public void sitoolsDescribe() {
    setName("SystemsCollectionResource");
    setDescription("Resource to get the collection of known systems");
    setNegotiated(false);    
  }
  
  @Override
  @Get
  public Representation get(Variant variant) {
    List<DimensionHelper> helpers = SitoolsEngine.getInstance().getRegisteredDimensionHelpers();
    List<String> systems = new ArrayList<String>();
    for (DimensionHelper helper : helpers) {
      systems.addAll(helper.getSystems());
    }
    Response response = new Response(true, systems, String.class, "systems");
    return getRepresentation(response, variant);
  }
  
  @Override
  public void describeGet(MethodInfo info) {
    info.setDocumentation("Resource to handle list of registered systems");
    this.addStandardGetRequestInfo(info);
    this.addStandardSimpleResponseInfo(info);
  }

}

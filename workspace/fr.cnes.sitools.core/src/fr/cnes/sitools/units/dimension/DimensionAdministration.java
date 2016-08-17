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

import org.restlet.Context;
import org.restlet.Restlet;
import org.restlet.routing.Router;

import fr.cnes.sitools.common.model.Category;

/**
 * Application to manage Dimensions
 * @author m.marseille (AKKA technologies)
 */
public final class DimensionAdministration extends AbstractDimensionApplication {

  /**
   * Constructor
   * @param appContext restlet context
   */
  public DimensionAdministration(Context appContext) {
    super(appContext);
  }

  @Override
  public void sitoolsDescribe() {
    setName("DimensionAdministration");
    setDescription("Application to manage Dimensions" +
    		"Administrator must have all rights on this application" +
    		"public user must have GET right to access the dimension from the client-user interface");
    setCategory(Category.USER);
  }
  
  @Override
  public Restlet createInboundRoot() {
    Router router = new Router(getContext());
    
    router.attach("/unithelpers", UnitConvertersCollectionResource.class);
    router.attach("/unithelpers/{helperClassName}", UnitConvertersCollectionResource.class);
    router.attach("/dimension", DimensionCollectionResource.class);
    router.attach("/dimension/{dimensionId}", DimensionResource.class);
    
    return router;
  }

}

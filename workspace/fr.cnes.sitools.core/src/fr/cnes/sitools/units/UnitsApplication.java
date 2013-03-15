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

import org.restlet.Context;
import org.restlet.Restlet;
import org.restlet.routing.Router;

import fr.cnes.sitools.common.application.SitoolsApplication;
import fr.cnes.sitools.common.model.Category;

/**
 * Application for unit utilities
 * @author jp.boignard (AKKA technologies)
 */
public final class UnitsApplication extends SitoolsApplication {

  /**
   * Constructor
   * @param context the context
   */
  public UnitsApplication(Context context) {
    super(context);
  }

  
  @Override
  public void sitoolsDescribe() {
    setName("UnitsApplication");
    setDescription("Application for unit utilities");
    setCategory(Category.USER);
  }

  @Override
  public Restlet createInboundRoot() {
    Router router = new Router(getContext());
    
    // To get all collections available
    router.attach("/systems", SystemsCollectionResource.class);
    // Filtering units by scope (scope empty gives all available units)
    router.attach("/systems/{scope}", UnitsCollectionResource.class);
    // Recognizing resource
    router.attach("/{unit}", UnitsResource.class, Router.MODE_BEST_MATCH);
    return router;
  }
  
}

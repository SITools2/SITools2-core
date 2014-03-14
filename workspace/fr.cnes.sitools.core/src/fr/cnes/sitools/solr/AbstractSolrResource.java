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
package fr.cnes.sitools.solr;

import java.util.logging.Logger;

import org.restlet.engine.Engine;

import fr.cnes.sitools.common.SitoolsResource;

/**
 * Abstract Resource class for Forms management
 * 
 * @author jp.boignard (AKKA Technologies)
 * 
 */
public abstract class AbstractSolrResource extends SitoolsResource {
  
  /**
   * The SolrApplication
   */
  protected SolrApplication application = null;
  
  /**
   * Logger
   */
  protected Logger log;
  
  /**
   * Opensearch Id
   */
  protected String osId;

  /*
   * (non-Javadoc)
   * 
   * @see org.restlet.resource.UniformResource#doInit()
   */
  @Override
  public final void doInit() {
    // TODO verify logger 
    log = Engine.getLogger(getApplication().getClass().getName());
    application = (SolrApplication) getApplication();
    if (this.getRequest().getAttributes().get("osId") != null) {
      osId = (String) this.getRequest().getAttributes().get("osId");
    }
  }

}

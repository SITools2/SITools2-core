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
package fr.cnes.sitools.registry;

import org.restlet.data.MediaType;
import org.restlet.ext.xstream.XstreamRepresentation;
import org.restlet.representation.ObjectRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;

import com.thoughtworks.xstream.XStream;

import fr.cnes.sitools.common.SitoolsResource;
import fr.cnes.sitools.common.XStreamFactory;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.common.store.SitoolsStore;
import fr.cnes.sitools.registry.model.AppRegistry;

/**
 * Base class for resources
 * 
 * @author jp.boignard (AKKA Technologies)
 * 
 */
public abstract class AppRegistryAbstractResource extends SitoolsResource {

  /** Application */
  private AppRegistryApplication application = null;

  /** Store */
  private SitoolsStore<AppRegistry> store = null;

  /** Resource id in the request */
  private String resourceId = null;

  @Override
  public final void doInit() {
    super.doInit();

    // Declares the two variants supported
    getVariants().add(new Variant(MediaType.APPLICATION_XML));
    getVariants().add(new Variant(MediaType.APPLICATION_JSON));

    application = (AppRegistryApplication) getApplication();
    store = application.getStore();
    resourceId = (String) this.getRequest().getAttributes().get("resourceId");
  }

  /**
   * Get object representation
   * 
   * @param response
   *          the server response
   * @param media
   *          the media used
   * @return Representation
   */
  public final Representation getRepresentation(Response response, MediaType media) {
    
    if (media.isCompatible(MediaType.APPLICATION_JAVA_OBJECT)) {
      return new ObjectRepresentation<Response>(response);
    }

    XStream xstream = XStreamFactory.getInstance().getXStream(media, getContext());
    configure(xstream, response);
    xstream.alias("ResourceManager", AppRegistry.class);

    XstreamRepresentation<Response> rep = new XstreamRepresentation<Response>(media, response);
    rep.setXstream(xstream);
    return rep;
  }

  /**
   * Get the resource identifier
   * 
   * @return the resource identifier
   */
  public final String getResourceId() {
    return this.resourceId;
  }

  /**
   * Get the store associated to the Application registry application
   * 
   * @return the store
   */
  public final SitoolsStore<AppRegistry> getStore() {
    return this.store;
  }

  /**
   * Get the Application registry application
   * 
   * @return the application
   */
  public final AppRegistryApplication getAppRegistryApplication() {
    return this.application;
  }

}

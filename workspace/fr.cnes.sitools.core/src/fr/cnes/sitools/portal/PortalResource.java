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
package fr.cnes.sitools.portal;

import java.util.ArrayList;
import java.util.logging.Level;

import org.restlet.data.Status;
import org.restlet.ext.wadl.MethodInfo;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Get;
import org.restlet.resource.Put;
import org.restlet.resource.ResourceException;

import fr.cnes.sitools.common.SitoolsSettings;
import fr.cnes.sitools.common.application.SitoolsApplication;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.portal.model.Portal;

/**
 * Class Resource for managing single Portal (GET UPDATE DELETE)
 * 
 * @author jp.boignard (AKKA Technologies)
 * 
 */
public final class PortalResource extends AbstractPortalResource {
  
  @Override
  public void sitoolsDescribe() {
    setName("PortalResource");
    setDescription("Resource for defining the portal");
    setNegotiated(false);
  }

  /**
   * get Portal
   * 
   * @param variant
   *          client preferred media type
   * @return Representation
   */
  @Get
  public Representation retrieve(Variant variant) {
    /*
     * try { if (portalId != null) { Portal portal = store.get(portalId); if
     * (portal == null) { portal = new Portal(); portal.setId(portalId); }
     * Response response = new Response(true, portal, Portal.class, "portal");
     * Representation rep = getRepresentation(response, variant.getMediaType());
     * return rep; } else { Collection<Portal> portalsList = store.getList();
     * ArrayList<Portal> portals = new ArrayList<Portal>(portalsList); Response
     * response = new Response(true, portals, Portal.class, "portal");
     * Representation rep = getRepresentation(response, variant.getMediaType());
     * return rep; } } catch (ResourceException e) { getLogger().log(Level.INFO,
     * null, e); throw e; } catch (Exception e) { getLogger().log(Level.SEVERE,
     * null, e); throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e); }
     */

    Portal portal = new Portal();
    
    SitoolsSettings settings = ((SitoolsApplication) getApplication()).getSettings();
    portal.setName(settings.getString("Portal.name"));
    portal.setDescription(settings.getString("Portal.description"));
    portal.setId(settings.getString("Portal.id"));
    
    ArrayList<Portal> portals = new ArrayList<Portal>();
    portals.add(portal);
    Response response = new Response(true, portals, Portal.class, "portal");
    return getRepresentation(response, variant);

  }
  
  @Override
  public void describeGet(MethodInfo info) {
    info.setDocumentation("Get the portal through this method.");
    this.addStandardGetRequestInfo(info);
    this.addStandardObjectResponseInfo(info);
  }

  /**
   * Update existing Portal
   * 
   * @param representation
   *          input Portal representation
   * @param variant
   *          client preferred media type
   * @return Representation
   */
  @Put
  public Representation updatePortal(Representation representation, Variant variant) {
    Portal portalOutput = null;
    try {
      if (representation != null) {
        try {
          // Parse object representation
          Portal portalInput = getObject(representation, variant);

          // unique instance.
          portalInput.setId(getPortalId());

          // Business service
          getStore().update(portalInput);

          portalOutput = portalInput;
        }
        catch (Exception e) {
          throw new ResourceException(Status.SERVER_ERROR_INTERNAL, null, e);
        }
      }

      if (portalOutput != null) {
        // Response
        Response response = new Response(true, portalOutput, Portal.class, "portal");
        return getRepresentation(response, variant.getMediaType());
      }
      else {
        // Response
        Response response = new Response(false, "Can not validate Portal"); 
        return getRepresentation(response, variant.getMediaType());
      }

    }
    catch (ResourceException e) {
      getLogger().log(Level.INFO, null, e);
      throw e;
    }
    catch (Exception e) {
      getLogger().log(Level.SEVERE, null, e);
      throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
    }
  }
  
  @Override
  public void describePut(MethodInfo info) {
    info.setDocumentation("Method to modify the portal sending its new representation.");
    this.addStandardPostOrPutRequestInfo(info);
    this.addStandardResponseInfo(info);
    this.addStandardInternalServerErrorInfo(info);
  }

}

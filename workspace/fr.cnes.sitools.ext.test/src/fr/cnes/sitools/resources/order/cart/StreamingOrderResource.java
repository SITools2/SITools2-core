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
/**
 * 
 */
package fr.cnes.sitools.resources.order.cart;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;

import fr.cnes.sitools.cart.utils.ListReferencesAPI;
import fr.cnes.sitools.cart.utils.OrderAPI;
import fr.cnes.sitools.common.exception.SitoolsException;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.plugins.resources.model.ResourceParameter;
import fr.cnes.sitools.resources.order.representations.TarOutputRepresentation;
import fr.cnes.sitools.resources.order.representations.ZipOutputRepresentation;

/**
 * @author tx.chevallier
 * 
 * @project fr.cnes.sitools.ext.test
 * @version 
 *
 */
public class StreamingOrderResource extends CartOrderResource {
  
  /** The type of archive to create */
  private String archiveType;

  @Override
  public void doInit() {
    super.doInit();
    // initialise the archiveType, first let's get it from the request parameters
    archiveType = getRequest().getResourceRef().getQueryAsForm().getFirstValue("archiveType");
    if (archiveType == null || "".equals(archiveType)) {
      // if it is not in the request parameters, let's get from the model
      ResourceParameter param = getModel().getParameterByName("archiveType");
      archiveType = param.getValue();
    }
  }
  

  
  @Override
  public Representation processOrder(ListReferencesAPI listReferences) throws SitoolsException {
  
    super.processOrder(listReferences);
    
    try {
      
      String fileName = getFileName();
      Representation repr = null;
      
      if ("zip".equals(archiveType)) {
        repr = new ZipOutputRepresentation(listReferences.getReferencesSource(), getClientInfo(), getContext(), fileName
            + ".zip");
      }
      else if ("tar.gz".equals(archiveType)) {
        repr = new TarOutputRepresentation(listReferences.getReferencesSource(), getClientInfo(), getContext(), fileName
            + ".tar.gz", true);
      }
      else if ("tar".equals(archiveType)) {
        repr = new TarOutputRepresentation(listReferences.getReferencesSource(), getClientInfo(), getContext(), fileName
            + ".tar", false);
      }
      else {
        getResponse().setStatus(Status.CLIENT_ERROR_NOT_ACCEPTABLE);
        return repr;
      }
      
      OrderAPI.terminateOrder(order, getContext());
      
      return repr;
      
    }    
    catch (Exception e) {
      getLogger().log(Level.SEVERE, null, e);
      if (order != null) {
        try {
          OrderAPI.orderFailed(order, getContext(), e.getMessage());
        }
        catch (SitoolsException e1) {
          throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
        }
      }
      Response response = new Response(false, "label.download_ko");
      return getRepresentation(response, MediaType.APPLICATION_JSON);
    }


  }
  
  

}

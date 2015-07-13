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
package fr.cnes.sitools.feeds;

import java.util.logging.Level;

import org.restlet.data.Status;
import org.restlet.ext.wadl.MethodInfo;
import org.restlet.ext.wadl.ParameterInfo;
import org.restlet.ext.wadl.ParameterStyle;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.representation.Variant;
import org.restlet.resource.Put;
import org.restlet.resource.ResourceException;

import fr.cnes.sitools.notification.model.Notification;
/**
 * Handle Notifications on the Forms
 * 
 * @author AKKA Technologies
 */
public final class FeedsNotificationResource extends AbstractFeedsResource {

  
  @Override
  public void sitoolsDescribe() {
    setName("FeedsNotificationResource");
    setDescription("Manage notifications of Feeds resources updates");
    setNegotiated(false);
  }

  /**
   * Handle notifications of observable Resource
   * In its case datasetResource
   * 
   * @param representation
   *          Form representation
   * @param variant
   *          client preferred media type
   * @return Representation
   */
  @Put
  public Representation notification(Representation representation, Variant variant) {
    try {
      Notification notification = null;
      if (representation != null) {
        notification = getNotificationObject(representation);
      }

      if ((notification != null) && "DELETED".equals(notification.getStatus())) {
        // Business service
        boolean ok = getStore().delete(getFeedsId());
        if (ok) {
          return new StringRepresentation("OK");
        }
        else {          
          return new StringRepresentation("DEPRECATED");
        }
      }
      else {
        // Others status
        return new StringRepresentation("OK");
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
    info.setDocumentation("Notification to observers that something happens to an object.");
    this.addStandardNotificationInfo(info);
    ParameterInfo param = new ParameterInfo("feedsId", true, "ID", ParameterStyle.TEMPLATE, "Feeds ID");
    info.getRequest().getParameters().add(param);
  }
}

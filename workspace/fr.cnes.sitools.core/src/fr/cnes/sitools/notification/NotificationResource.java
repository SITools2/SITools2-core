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
package fr.cnes.sitools.notification;

import java.util.logging.Level;

import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.ext.jackson.JacksonRepresentation;
import org.restlet.ext.wadl.MethodInfo;
import org.restlet.ext.wadl.ParameterInfo;
import org.restlet.ext.wadl.ParameterStyle;
import org.restlet.ext.xstream.XstreamRepresentation;
import org.restlet.representation.ObjectRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Post;
import org.restlet.resource.ResourceException;

import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.notification.model.Notification;

/**
 * Resource for resource subscription / notification
 * 
 * @author jp.boignard (AKKA Technologies)
 * 
 */
public final class NotificationResource extends NotificationAbstractResource {

  @Override
  public void sitoolsDescribe() {
    setName("NotificationResource");
    setDescription("Resource for observers notification");
  }

  /**
   * Register an application
   * 
   * @param representation
   *          Representation of a resource
   * @param variant
   *          required Variant (if negotiated)
   * @return Representation
   */
  @Post
  public Representation notifyObservers(Representation representation, Variant variant) {
    if (representation == null) {
      throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "RESOURCE_REPRESENTATION_REQUIRED");
    }
    try {
      Notification input = null;
      if (MediaType.APPLICATION_JAVA_OBJECT.isCompatible(representation.getMediaType())) {
        // ObjectRepresentation
        input = new ObjectRepresentation<Notification>(representation).getObject();

      }
      else if (MediaType.APPLICATION_XML.isCompatible(representation.getMediaType())) {
        // Parse the XML representation to get the bean
        input = new XstreamRepresentation<Notification>(representation).getObject();

      }
      else if (MediaType.APPLICATION_JSON.isCompatible(representation.getMediaType())) {
        // Parse the JSON representation to get the bean
        input = new JacksonRepresentation<Notification>(representation, Notification.class).getObject();
      }

      // Business service
      if (input != null) {
        ((NotificationApplication) getApplication()).getEngine().notifyObservers(getContext(), getObservableUUID(),
            input);
      }

      return getRepresentation(new Response(true, "notification.success"), variant);

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
  public void describePost(MethodInfo info, String path) {
    if (path.contains("{observerUUID}")) {
      info.setDocumentation("POST " + path + " : notify a specific observer.");
    }
    else {
      info.setDocumentation("POST " + path + " : notify all observers of an observable.");
    } 
    // info.setDocumentation("Method to notify an observer");
    this.addStandardPostOrPutRequestInfo(info);
    ParameterInfo param = new ParameterInfo("observableUUID", true, "xs:string", ParameterStyle.TEMPLATE,
        "Identifier of the observable to notify");
    ParameterInfo param2 = new ParameterInfo("observerUUID", false, "xs:string", ParameterStyle.TEMPLATE,
        "Identifier of the observer to notify");
    info.getRequest().getParameters().add(param);
    info.getRequest().getParameters().add(param2);
    this.addStandardSimpleResponseInfo(info);
  }

}

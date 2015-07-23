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
package fr.cnes.sitools.notification;

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
import org.restlet.resource.Delete;
import org.restlet.resource.Post;
import org.restlet.resource.ResourceException;

import com.thoughtworks.xstream.XStream;

import fr.cnes.sitools.common.XStreamFactory;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.notification.model.RestletObservable;

/**
 * Resource for managing observable declaration
 * 
 * @author jp.boignard (AKKA Technologies)
 */
public final class ObservableResource extends NotificationAbstractResource {

  @Override
  public void sitoolsDescribe() {
    setName("ObservableResource");
    setDescription("Resource for observable declaration");
    setNegotiated(false);
  }

  /**
   * Add an observable to the list
   * 
   * @param representation
   *          the representation
   * @param variant
   *          the variant
   * @return Representation
   */
  @Post
  public Representation addObservable(Representation representation, Variant variant) {
    if (null == getObservableUUID()) {
      throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "observable.post.error");
    }

    RestletObservable observable = getObject(representation);
    getNotificationApplication().getStore().addObservable(getObservableUUID(), observable);

    Variant var = (variant != null) ? variant : getPreferredVariant(getVariants());
    return getRepresentation(new Response(true, "observable.added"), var);
  }

  public void describePost(MethodInfo info, String path) {
    if (path.endsWith("{observableUUID}")) {
      info.setDocumentation("POST " + path + " : references a new observable with the given unique ID.");
      this.addStandardPostOrPutRequestInfo(info);
      ParameterInfo param = new ParameterInfo("observableUUID", false, "xs:string", ParameterStyle.TEMPLATE,
          "Identifier of the observable to add");
      info.getRequest().getParameters().add(param);
      this.addStandardSimpleResponseInfo(info);
    }
    else {
      info.setDocumentation("POST " + path + " : Not implemented. Use /{observableUUID} signature.");
    }
  }

  /**
   * Delete an observable
   * 
   * @param representation
   *          the representation
   * @param variant
   *          the variant
   * @return Representation
   */
  @Delete
  public Representation deleteObservable(Representation representation, Variant variant) {
    if (null != getObservableUUID()) {
      getNotificationApplication().getStore().removeObservable(getObservableUUID());
      Variant var = (variant != null) ? variant : getPreferredVariant(getVariants());
      return getRepresentation(new Response(true, "observable.deleted"), var);
    }
    else {
      // SUPPRESSION DE TOUS LES OBSERVABLES en une seule fois :  non implémentée.
      throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "observable.delete.error");
    }
  }

  public void describeDelete(MethodInfo info, String path) {
    if (path.endsWith("{observableUUID}")) {
      info.setDocumentation("DELETE " + path
          + " : deletes an observable and its registered observers. No events will be sent.");
      this.addStandardGetRequestInfo(info);
      ParameterInfo param = new ParameterInfo("observableUUID", true, "xs:string", ParameterStyle.TEMPLATE,
          "Identifier of the observable to delete");
      info.getRequest().getParameters().add(param);
      this.addStandardSimpleResponseInfo(info);
    }
    else {
      info.setDocumentation("DELETE " + path + " : Not implemented.");
    }
  }

  /**
   * Get RestletObservable from representation
   * 
   * @param representation
   *          Representation
   * @return RestletObservable
   */
  @SuppressWarnings("unchecked")
  private RestletObservable getObject(Representation representation) {
    RestletObservable observable = null;
    try {
      if (representation instanceof ObjectRepresentation<?>) {
        observable = ((ObjectRepresentation<RestletObservable>) representation).getObject();
        return observable;
      }

      if (MediaType.APPLICATION_JSON.isCompatible(representation.getMediaType())) {
        // Parse the JSON representation to get the bean
        observable = new JacksonRepresentation<RestletObservable>(representation, RestletObservable.class).getObject();
        return observable;
      }

    }
    catch (Exception e) {
      getLogger().warning("getObject(representation) ERROR");
      throw new ResourceException(Status.SERVER_ERROR_INTERNAL, "getObject(representation) ERROR", e);
    }

    throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "observable.representation.error");
  }

}

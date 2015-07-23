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
import fr.cnes.sitools.notification.model.RestletObserver;

/**
 * Resource for managing observers subscription
 * 
 * @author jp.boignard (AKKA Technologies)
 */
public final class ObserverResource extends NotificationAbstractResource {

  @Override
  public void sitoolsDescribe() {
    setName("ObserverResource");
    setDescription("Resource for observers subscription");
  }

  /**
   * Add an observer
   * 
   * @param representation
   *          the representation
   * @param variant
   *          the variant
   * @return Representation
   */
  @Post
  public Representation addObserver(Representation representation, Variant variant) {
    if (getObserverUUID() != null) {
      throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "observer.post.error");
    }

    if (null != getObservableUUID()) {

      RestletObserver observer = getObject(representation);
      RestletObservable observable = getNotificationApplication().getStore().getObservable(getObservableUUID());
      if (observable != null) {
        observable.addObserver(observer);
      }
      else {
        return getRepresentation(new Response(false, "obervable.notfound"), variant);
      }
      return getRepresentation(new Response(true, "observer.added"), variant);
    }
    else {
      throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "observer.post.error");
    }
  }

  public void describePost(MethodInfo info, String path) {
    if (path.endsWith("{observerUUID}")) {
      info.setDocumentation("POST " + path + " : references a new observer with the given unique ID.");
      this.addStandardPostOrPutRequestInfo(info);
      ParameterInfo param = new ParameterInfo("observableUUID", true, "xs:string", ParameterStyle.TEMPLATE,
          "Identifier of the observable wherer to add the observer");
      ParameterInfo param2 = new ParameterInfo("observerUUID", false, "xs:string", ParameterStyle.TEMPLATE,
          "Identifier of the observer to add");
      info.getRequest().getParameters().add(param);
      info.getRequest().getParameters().add(param2);
      this.addStandardSimpleResponseInfo(info);
    }
    else {
      info.setDocumentation("POST " + path + " : Not implemented. Use /{observerUUID} signature.");
    }

  }

  /**
   * Delete an obsever
   * 
   * @param representation
   *          the representation
   * @param variant
   *          the variant
   * @return Representation
   */
  @Delete
  public Representation deleteObserver(Representation representation, Variant variant) {
    if ((null != getObservableUUID()) && (null != getObserverUUID())) {
      RestletObservable observable = getNotificationApplication().getStore().getObservable(getObservableUUID());
      if (observable != null) {
        observable.removeObserver(getObserverUUID());
        return getRepresentation(new Response(true, "observer.deleted"), variant);
      }
      else {
        return getRepresentation(new Response(false, "observable.notfound"), variant);
      }
    }
    else {
      throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "observer.delete.error");
    }
  }

  public void describeDelete(MethodInfo info, String path) {
    if (path.endsWith("{observerUUID}")) {
      info.setDocumentation("DELETE " + path + " : deletes an observer. No events will be sent.");
      this.addStandardGetRequestInfo(info);
      ParameterInfo param = new ParameterInfo("observableUUID", true, "xs:string", ParameterStyle.TEMPLATE,
          "Identifier of the observable where to delete the observer");
      ParameterInfo param2 = new ParameterInfo("observerUUID", false, "xs:string", ParameterStyle.TEMPLATE,
          "Identifier of the observer to delete");
      info.getRequest().getParameters().add(param);
      info.getRequest().getParameters().add(param2);
      this.addStandardSimpleResponseInfo(info);
    }
    else {
      info.setDocumentation("DELETE " + path + " : Not implemented.");
    }
  }

  /**
   * Get RestletObserver from representation
   * 
   * @param representation
   *          Representation
   * @return RestletObserver
   */
  @SuppressWarnings("unchecked")
  private RestletObserver getObject(Representation representation) {
    try {
      RestletObserver observer = null;

      // ObjectRepresentation
      if (representation instanceof ObjectRepresentation<?>) {
        observer = ((ObjectRepresentation<RestletObserver>) representation).getObject();
        return observer;
      }

      if (MediaType.APPLICATION_JSON.isCompatible(representation.getMediaType())) {
        // Parse the JSON representation to get the bean
        observer = new JacksonRepresentation<RestletObserver>(representation, RestletObserver.class).getObject();
        return observer;
      }
    }
    catch (Exception e) {
      getLogger().warning("getObject(representation) ERROR");
      throw new ResourceException(Status.SERVER_ERROR_INTERNAL, null, e);
    }
    throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "observer.representation.error");

  }

}

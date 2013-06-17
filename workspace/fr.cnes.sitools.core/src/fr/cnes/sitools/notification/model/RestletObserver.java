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
package fr.cnes.sitools.notification.model;

import java.io.IOException;
import java.io.Serializable;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.data.Method;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;

import fr.cnes.sitools.proxy.ProxySettings;

/**
 * RestletObserver acts like an observer proxy to send notify event by a Restlet request to a distant resource with the
 * specified method. A notify event is a Notification.class object.
 * 
 * @author jp.boignard (AKKA Technologies)
 * 
 */
public final class RestletObserver implements Serializable { // java.util.Observer,

  /** serialVersionUID */
  private static final long serialVersionUID = 6121649214628657269L;

  // TODO create Method.NOTIFY for this purpose ?

  /** default method to notify */
  private static final Method DEFAULT_METHOD_TO_NOTIFY = Method.POST;

  /** Observer uid */
  private String uuid;

  /** URL utilisée pour notifier l'observer d'un événement sur l'observable */
  private String uriToNotify;

  /** method to use with uriToNotify - a priori PUT but could be DELETE when observable is DELETED */
  private String methodToNotify = null; //

  /** mediaType to use for notification representation */
  private String mediaTypeToNotify = null;
  
  // private String observableStatus = null; // when observable takes Status ?

  /**
   * Default constructor
   */
  public RestletObserver() {
    super();
  }

  /**
   * Gets the uuid value
   * 
   * @return the uuid
   */
  public String getUuid() {
    return uuid;
  }

  /**
   * Sets the value of uuid
   * 
   * @param uuid
   *          the uuid to set
   */
  public void setUuid(String uuid) {
    this.uuid = uuid;
  }

  /**
   * Gets the uriToNotify value
   * 
   * @return the uriToNotify
   */
  public String getUriToNotify() {
    return uriToNotify;
  }

  /**
   * Sets the value of uriToNotify
   * 
   * @param uriToNotify
   *          the uriToNotify to set
   */
  public void setUriToNotify(String uriToNotify) {
    this.uriToNotify = uriToNotify;
  }

  /**
   * Gets the methodToNotify value
   * 
   * @return the methodToNotify
   */
  public String getMethodToNotify() {
    return methodToNotify;

  }

  /**
   * Sets the value of methodToNotify
   * 
   * @param methodToNotify
   *          the methodToNotify to set
   */
  public void setMethodToNotify(String methodToNotify) {
    this.methodToNotify = methodToNotify;
  }

  /**
   * Gets the mediaTypeToNotify value
   * 
   * @return the mediaTypeToNotify
   */
  public String getMediaTypeToNotify() {
    return mediaTypeToNotify;
  }

  /**
   * Sets the value of mediaTypeToNotify
   * 
   * @param mediaTypeToNotify
   *          the mediaTypeToNotify to set
   */
  public void setMediaTypeToNotify(String mediaTypeToNotify) {
    this.mediaTypeToNotify = mediaTypeToNotify;
  }

  /**
   * Update to notify
   * 
   * @param o
   *          restlet observable
   * @param arg
   *          object
   */
  // @Override
  public void update(RestletObservable o, Object arg) {
    if (arg instanceof Context) {
      Context context = (Context) arg;

      Method method = DEFAULT_METHOD_TO_NOTIFY;
      if ((methodToNotify != null) || (Method.valueOf(methodToNotify) != null)) {
        method = Method.valueOf(methodToNotify);
      }

      Request request = new Request(method, this.getUriToNotify());
      
      // Passage du proxy
      if (this.getUriToNotify().startsWith("http") && ProxySettings.isWithProxy()) {
        request.setChallengeResponse(ProxySettings.getProxyAuthentication());
      }
      
      // Récupération des éléments du contexte : Representation de la notification
      if (arg instanceof Context) {
        Representation entity = (Representation) ((Context) arg).getAttributes().get("entity");
        if (entity != null) {
          request.setEntity(entity);
        }
      }

      // no response expected
      Restlet callback = new Restlet() {

        @Override
        public void handle(Request request, Response response) {
          if (response.getStatus().isError()) {
            getLogger().info("Error when notifying observer at URL " + request.getResourceRef().toString()); 
            
          }
          try {
            if (response.getEntity() != null) {
              response.getEntity().exhaust();
              // response should not be released ?
            }
          }
          catch (IOException e) {
            throw new ResourceException(Status.SERVER_ERROR_INTERNAL, null, e);
          }
        }

      };

      context.getClientDispatcher().handle(request, callback);

    }

  }

}

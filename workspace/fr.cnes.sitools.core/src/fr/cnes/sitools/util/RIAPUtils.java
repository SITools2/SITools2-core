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
package fr.cnes.sitools.util;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Preference;
import org.restlet.data.Status;
import org.restlet.representation.ObjectRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;

import fr.cnes.sitools.common.model.IResource;
import fr.cnes.sitools.common.model.Response;

/**
 * Utils class for RIAP calls
 * 
 * 
 * @author m.gond (AKKA Technologies)
 */
public final class RIAPUtils {

  /**
   * Private constructor
   */
  private RIAPUtils() {
    super();
  }

  /**
   * Get an Object of class <T> with the specified <code>id</code>, at the specified <code>url</code> using the RIAP
   * protocol. The <code>context</code> is required in order to make an RIAP call
   * 
   * @param <T>
   *          the class of the object
   * @param id
   *          the id of the object
   * @param url
   *          the url of the object
   * @param context
   *          the context
   * @return an <T> object
   */
  @SuppressWarnings("unchecked")
  public static <T> T getObject(String id, String url, Context context) {
    return (T) getObject(url + "/" + id, context, MediaType.APPLICATION_JAVA_OBJECT);
  }

  /**
   * Get an Object of class <T> with the specified <code>id</code>, at the specified <code>url</code> using the RIAP
   * protocol. The <code>context</code> is required in order to make an RIAP call
   * 
   * @param <T>
   *          the class of the object
   * @param id
   *          the id of the object
   * @param url
   *          the url of the object
   * @param context
   *          the context
   * @param mediaType
   *          the mediaType needed (Must be a Java object serialize media type)
   * @return an <T> object
   */
  @SuppressWarnings("unchecked")
  public static <T> T getObject(String id, String url, Context context, MediaType mediaType) {
    return (T) getObject(url + "/" + id, context, mediaType);
  }

  /**
   * Get an Object of class <T> with the specified <code>id</code>, at the specified <code>url</code> using the RIAP
   * protocol. The <code>context</code> is required in order to make an RIAP call
   * 
   * @param <T>
   *          the class of the object
   * @param url
   *          the url of the object
   * @param context
   *          the context
   * @return an <T> object
   */
  @SuppressWarnings("unchecked")
  public static <T> T getObject(String url, Context context) {
    return (T) getObject(url, context, MediaType.APPLICATION_JAVA_OBJECT);

  }

  /**
   * Get an Object of class <T> with the specified <code>id</code>, at the specified <code>url</code> using the RIAP
   * protocol. The <code>context</code> is required in order to make an RIAP call
   * 
   * @param <T>
   *          the class of the object
   * @param url
   *          the url of the object
   * @param context
   *          the context
   * @param mediaType
   *          the mediaType needed (Must be a Java object serialize media type)
   * @return an <T> object
   */
  public static <T> T getObject(String url, Context context, MediaType mediaType) {

    Request reqGET = new Request(Method.GET, getRiapBase() + url);
    ArrayList<Preference<MediaType>> objectMediaType = new ArrayList<Preference<MediaType>>();
    objectMediaType.add(new Preference<MediaType>(mediaType));
    reqGET.getClientInfo().setAcceptedMediaTypes(objectMediaType);
    org.restlet.Response response = null;

    response = context.getClientDispatcher().handle(reqGET);

    if (response == null || Status.isError(response.getStatus().getCode())) {
      RIAPUtils.exhaust(response);
      return null;
    }

    @SuppressWarnings("unchecked")
    ObjectRepresentation<Response> or = (ObjectRepresentation<Response>) response.getEntity();
    try {
      Response resp = or.getObject();
      // check if there is an object in the response, if not return null
      if (resp == null) {
        return null;
      }
      @SuppressWarnings("unchecked")
      T returnObj = (T) resp.getItem();
      return returnObj;

    }
    catch (IOException e) { // marshalling error
      throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
    }
  }

  /**
   * Get an Object of class <T> with the specified <code>name</code>, at the specified <code>url</code> using the RIAP
   * protocol. The <code>context</code> is required in order to make an RIAP call Return null if there is no or multiple
   * <T> Object found
   * 
   * @param <T>
   *          the class of the object
   * @param url
   *          the url of the object
   * @param name
   *          the name of the object
   * @param context
   *          the context
   * @return an <T> object
   */
  public static <T> T getObjectFromName(String url, String name, Context context) {
    List<T> objects = RIAPUtils.getListOfObjects(url + "?query=" + name + "&mode=strict", context);
    if (objects != null && objects.size() == 1) {
      return objects.get(0);
    }
    else {
      return null;
    }

  }

  /**
   * Get an Object of class <T> with the specified <code>id</code>, at the specified <code>url</code> using the RIAP
   * protocol. The <code>context</code> is required in order to make an RIAP call
   * 
   * @param <T>
   *          the class of the object
   * @param url
   *          the url of the object
   * @param context
   *          the context
   * @return an List<T> object
   */
  public static <T> List<T> getListOfObjects(String url, Context context) {
    Request reqGET = new Request(Method.GET, getRiapBase() + url);
    ArrayList<Preference<MediaType>> objectMediaType = new ArrayList<Preference<MediaType>>();
    objectMediaType.add(new Preference<MediaType>(MediaType.APPLICATION_JAVA_OBJECT));
    reqGET.getClientInfo().setAcceptedMediaTypes(objectMediaType);
    org.restlet.Response response = null;

    response = context.getClientDispatcher().handle(reqGET);

    if (response == null || Status.isError(response.getStatus().getCode())) {
      RIAPUtils.exhaust(response);
      return null;
    }

    @SuppressWarnings("unchecked")
    ObjectRepresentation<Response> or = (ObjectRepresentation<Response>) response.getEntity();
    try {
      Response resp = or.getObject();
      // check if there is an object in the response, if not return null
      if (resp == null) {
        return null;
      }
      ArrayList<T> listT = new ArrayList<T>();
      ArrayList<Object> list = resp.getData();
      for (Iterator<Object> iterator = list.iterator(); iterator.hasNext();) {
        @SuppressWarnings("unchecked")
        T obj = (T) iterator.next();
        listT.add(obj);
      }
      return listT;

    }
    catch (IOException e) { // marshalling error
      throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
    }
  }

  /**
   * Persist a given T object to the given url
   * 
   * @param <T>
   *          the type of object to persist, must implements {@link Serializable}
   * @param object
   *          the object to persist
   * @param url
   *          the url
   * @param context
   *          the {@link Context}
   * @return the persisted object
   */
  @SuppressWarnings("unchecked")
  public static <T extends Serializable> T persistObject(T object, String url, Context context) {
    // create the core
    Request reqPOST = new Request(Method.POST, RIAPUtils.getRiapBase() + url, new ObjectRepresentation<T>(object));

    ArrayList<Preference<MediaType>> objectMediaType = new ArrayList<Preference<MediaType>>();
    objectMediaType.add(new Preference<MediaType>(MediaType.APPLICATION_JAVA_OBJECT));
    reqPOST.getClientInfo().setAcceptedMediaTypes(objectMediaType);

    org.restlet.Response response = null;
    T objectOut = null;
    try {
      response = context.getClientDispatcher().handle(reqPOST);

      if (response == null || Status.isError(response.getStatus().getCode())) {
        throw new ResourceException(response.getStatus());
      }
      ObjectRepresentation<Response> or = (ObjectRepresentation<Response>) response.getEntity();
      Response resp;
      try {
        resp = (Response) or.getObject();
        if (resp.isSuccess()) {
          objectOut = (T) resp.getItem();
        }
        return objectOut;
      }
      catch (IOException e) {
        throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
      }
    }
    finally {
      RIAPUtils.exhaust(response);
    }
  }

  /**
   * Persist a given T object to the given url
   * 
   * @param <T>
   *          the type of object to persist, must implements {@link Serializable}
   * @param object
   *          the object to persist
   * @param url
   *          the url
   * @param context
   *          the {@link Context}
   * @return the persisted object
   */
  @SuppressWarnings("unchecked")
  public static <T extends Serializable> T updateObject(T object, String url, Context context) {
    // create the core
    Request reqPOST = new Request(Method.PUT, RIAPUtils.getRiapBase() + url, new ObjectRepresentation<T>(object));

    ArrayList<Preference<MediaType>> objectMediaType = new ArrayList<Preference<MediaType>>();
    objectMediaType.add(new Preference<MediaType>(MediaType.APPLICATION_JAVA_OBJECT));
    reqPOST.getClientInfo().setAcceptedMediaTypes(objectMediaType);

    org.restlet.Response response = null;
    T objectOut = null;
    try {
      response = context.getClientDispatcher().handle(reqPOST);

      if (response == null || Status.isError(response.getStatus().getCode())) {
        throw new ResourceException(response.getStatus());
      }
      ObjectRepresentation<Response> or = (ObjectRepresentation<Response>) response.getEntity();
      Response resp;
      try {
        resp = (Response) or.getObject();
        if (resp.isSuccess()) {
          objectOut = (T) resp.getItem();
        }
        return objectOut;
      }
      catch (IOException e) {
        throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
      }
    }
    finally {
      RIAPUtils.exhaust(response);
    }
  }

  /**
   * Delete a T object at the specified url
   * 
   * @param <T>
   *          object implementing IResource
   * @param object
   *          the object to delete
   * @param url
   *          the url where to delete the object, without the object id
   * @param context
   *          the Context
   * @return true if the object was correctly deleted, false otherwise
   */
  public static <T extends IResource> boolean deleteObject(T object, String url, Context context) {
    return deleteObject(url + "/" + object.getId(), context);
  }

  /**
   * Delete a, object at the specified url
   * 
   * @param url
   *          the url of the object, with the id of the object
   * @param context
   *          the Context
   * @return true if the object was correctly deleted, false otherwise
   */
  public static boolean deleteObject(String url, Context context) {
    Request reqGET = new Request(Method.DELETE, getRiapBase() + url);
    ArrayList<Preference<MediaType>> objectMediaType = new ArrayList<Preference<MediaType>>();
    objectMediaType.add(new Preference<MediaType>(MediaType.APPLICATION_JAVA_OBJECT));
    reqGET.getClientInfo().setAcceptedMediaTypes(objectMediaType);
    org.restlet.Response response = null;

    response = context.getClientDispatcher().handle(reqGET);

    if (response == null || Status.isError(response.getStatus().getCode())) {
      RIAPUtils.exhaust(response);

    }

    @SuppressWarnings("unchecked")
    ObjectRepresentation<Response> or = (ObjectRepresentation<Response>) response.getEntity();
    try {
      Response resp = or.getObject();
      // check if there is an object in the response, if not return null
      return (!(resp == null || !resp.getSuccess()));
    }
    catch (IOException e) { // marshalling error
      throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
    }
  }

  /**
   * Handle a call with the RIAP protocol without entity.
   * 
   * @param url
   *          the url
   * @param method
   *          the Method to perform
   * @param mediaType
   *          the accepted mediaType
   * @param context
   *          the Context
   * @return the returned Representation
   */
  public static Representation handle(String url, Method method, MediaType mediaType, Context context) {

    Request req = new Request(method, getRiapBase() + url);
    ArrayList<Preference<MediaType>> acceptedMediaTypes = new ArrayList<Preference<MediaType>>();
    acceptedMediaTypes.add(new Preference<MediaType>(mediaType));
    req.getClientInfo().setAcceptedMediaTypes(acceptedMediaTypes);
    org.restlet.Response response = null;

    response = context.getClientDispatcher().handle(req);

    if (response == null || Status.isError(response.getStatus().getCode())) {
      RIAPUtils.exhaust(response);
      throw new ResourceException(response.getStatus());
    }
    return response.getEntity();

  }

  /**
   * Handle a call with the RIAP protocol with entity.
   * 
   * @param url
   *          the url
   * @param entity
   *          the entity of the request
   * @param method
   *          the Method to perform
   * @param mediaType
   *          the accepted mediaType
   * @param context
   *          the Context
   * @return the returned Representation
   */
  public static Representation handle(String url, Representation entity, Method method, MediaType mediaType,
      Context context) {

    Request req = new Request(method, getRiapBase() + url, entity);
    ArrayList<Preference<MediaType>> acceptedMediaTypes = new ArrayList<Preference<MediaType>>();
    acceptedMediaTypes.add(new Preference<MediaType>(mediaType));
    req.getClientInfo().setAcceptedMediaTypes(acceptedMediaTypes);
    org.restlet.Response response = null;

    response = context.getClientDispatcher().handle(req);

    if (response == null || Status.isError(response.getStatus().getCode())) {
      RIAPUtils.exhaust(response);
      throw new ResourceException(response.getStatus());
    }
    return response.getEntity();

  }

  /**
   * Exhaust response properly
   * 
   * @param response
   *          the response to exhaust
   * @throws Exception
   */
  public static void exhaust(org.restlet.Response response) {
    try {
      if (response != null && response.getEntity() != null) {
        response.getEntity().exhaust();
      }
    }
    catch (IOException e) {
      e.printStackTrace();
    }

    if (response != null) {
      response.release();
    }

  }

  /**
   * Exhaust representation properly
   * 
   * @param representation
   *          the representation to exhaust
   * @throws Exception
   */
  public static void exhaust(org.restlet.representation.Representation representation) {
    try {
      if (representation != null) {
        representation.exhaust();
      }
    }
    catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Get the RIAP base URl
   * 
   * @return the RIAP base url
   */
  public static String getRiapBase() {
    return "riap://component";
  }

}

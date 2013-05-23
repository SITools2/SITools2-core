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
package fr.cnes.sitools.common;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;

import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.ext.wadl.MethodInfo;
import org.restlet.ext.wadl.ParameterInfo;
import org.restlet.ext.wadl.ParameterStyle;
import org.restlet.ext.wadl.RepresentationInfo;
import org.restlet.ext.wadl.RequestInfo;
import org.restlet.ext.wadl.ResponseInfo;
import org.restlet.ext.xstream.XstreamRepresentation;
import org.restlet.representation.ObjectRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;

import com.thoughtworks.xstream.XStream;

import fr.cnes.sitools.common.application.SitoolsApplication;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.common.resource.AbstractSitoolsResource;
import fr.cnes.sitools.notification.model.Notification;

/**
 * Standard representation of a response (XML / JSON) TODO standard HTML response with a FreeMarker template ...
 * 
 * @author AKKA Technologies
 * 
 */
public abstract class SitoolsResource extends AbstractSitoolsResource {

  /** Default Mediatype in representations */
  public static final String DEFAULT_MEDIATYPE = "application/xml";

  /**
   * Initiate the resource
   */
  @Override
  protected void doInit() {
    super.doInit();
    this.setAutoDescribing(false);
  }

  /**
   * Get a representation of the object
   * 
   * @param response
   *          the response to treat
   * @param media
   *          the media to use
   * @return Representation
   */
  public Representation getRepresentation(Response response, MediaType media) {
    getLogger().info(media.toString());
    if (media.isCompatible(MediaType.APPLICATION_JAVA_OBJECT)
        || media.isCompatible(SitoolsMediaType.APPLICATION_JAVA_OBJECT_SITOOLS_MODEL)) {
      return new ObjectRepresentation<Response>(response);
    }

    XStream xstream = XStreamFactory.getInstance().getXStream(media, getContext());
    configure(xstream, response);

    XstreamRepresentation<Response> rep = new XstreamRepresentation<Response>(media, response);
    rep.setXstream(xstream);
    return rep;
  }

  /**
   * Configure the XStream
   * 
   * @param xstream
   *          the XStream to treat
   * @param response
   *          the response used
   */
  public void configure(XStream xstream, Response response) {
    xstream.autodetectAnnotations(false);
    xstream.alias("response", Response.class);

    // Because annotations are apparently missed
    xstream.omitField(Response.class, "itemName");
    xstream.omitField(Response.class, "itemClass");

    // If a class is present inside the response, link the item alias with this class
    if (response.getItemClass() != null) {
      xstream.alias("item", Object.class, response.getItemClass());
    }

    // If the object has a name, associate its name instead of item in the response
    if (response.getItemName() != null) {
      xstream.aliasField(response.getItemName(), Response.class, "item");
    }

  }

  /**
   * Gets representation according to the specified Variant if present. If variant is null (when content negociation =
   * false) sets the variant to the first client accepted mediaType.
   * 
   * @param response
   *          the response to use
   * @param variant
   *          the variant to use
   * @return Representation the final representation of the response
   */
  public Representation getRepresentation(Response response, Variant variant) {
    MediaType defaultMediaType = this.getMediaType(variant);
    return this.getRepresentation(response, defaultMediaType);
  }

  /**
   * Get Notification object
   * 
   * @param representation
   *          Notification Representation
   * 
   * @return Notification Notification Object
   */
  public Notification getNotificationObject(Representation representation) {
    try {
      ObjectRepresentation<Notification> or;
      try {
        or = new ObjectRepresentation<Notification>(representation);
        return or.getObject();
      }
      catch (IllegalArgumentException e) {
        getLogger().log(Level.INFO, null, e);
      }
      catch (ClassNotFoundException e) {
        getLogger().log(Level.INFO, null, e);
      }

    }
    catch (IOException e) {
      getLogger().log(Level.WARNING, "Bad representation of resource updating notification", e);      
    }
    return null;
  }

  /**
   * Configure the information to add a standard Sitools2 response
   * 
   * @param info
   *          the WADL method information
   */
  public void addStandardResponseInfo(MethodInfo info) {
    ResponseInfo responseInfo = new ResponseInfo(
        "Returns a SITools2 response, indicating if the object retrieval was OK or not, and containing the object if yes.");
    responseInfo.getStatuses().add(Status.SUCCESS_OK);
    responseInfo.getRepresentations().clear();
    RepresentationInfo repInfo;
    ArrayList<String> references = ((SitoolsApplication) getApplication()).getRepresentationInfoReferences();
    for (String ref : references) {
      repInfo = new RepresentationInfo();
      if (ref.endsWith("out")) {
        repInfo.setReference(ref);
        responseInfo.getRepresentations().add(repInfo);
      }
    }
    info.getResponses().add(responseInfo);

    ResponseInfo responseInfo403 = new ResponseInfo(
        "Request not authorized due to roles and methods access security configuration for this user.");
    responseInfo403.getStatuses().add(Status.CLIENT_ERROR_FORBIDDEN);
    info.getResponses().add(responseInfo403);
  }

  /**
   * Configure the information to add a standard Sitools2 request
   * 
   * @param info
   *          the WADL method information
   */
  public final void addStandardGetRequestInfo(MethodInfo info) {
    RequestInfo requestInfo = new RequestInfo("Ask for an object or a list of objects.");
    requestInfo.setDocumentation("Request can ask for XML or JSON responses");
    ParameterInfo media = new ParameterInfo("media", false, "media type", ParameterStyle.QUERY,
        "choosing response mediatype (application/json, application/xml)");
    media.setDefaultValue(DEFAULT_MEDIATYPE);
    requestInfo.getParameters().add(media);
    info.setRequest(requestInfo);
  }

  /**
   * Configure the information to add a standard Sitools2 request for PUT or POST
   * 
   * @param info
   *          the WADL method information
   */
  public final void addStandardPostOrPutRequestInfo(MethodInfo info) {
    RequestInfo requestInfo = new RequestInfo("Creates or modifies an object by sending its representation.");
    requestInfo.setDocumentation("Request can ask for XML or JSON responses");
    ParameterInfo paramInfo = new ParameterInfo("media", false, "media type", ParameterStyle.QUERY,
        "choosing response mediatype (application/json, application/xml)");
    paramInfo.setDefaultValue(DEFAULT_MEDIATYPE);
    requestInfo.getParameters().add(paramInfo);
    requestInfo.getRepresentations().clear();
    RepresentationInfo repInfo = new RepresentationInfo();
    repInfo.setReference("xml_object_in");
    requestInfo.getRepresentations().add(repInfo);
    repInfo = new RepresentationInfo();
    repInfo.setReference("json_object_in");
    requestInfo.getRepresentations().add(repInfo);
    info.setRequest(requestInfo);
  }

  /**
   * Configure the information to add a standard Sitools2 response
   * 
   * @param info
   *          the WADL method information
   */
  public final void addStandardSimpleResponseInfo(MethodInfo info) {
    ResponseInfo responseInfo = new ResponseInfo(
        "Returns a SITools2 response, indicating if the action went OK or not.");
    responseInfo.getStatuses().add(Status.SUCCESS_OK);
    responseInfo.getRepresentations().clear();
    RepresentationInfo repInfo;
    ArrayList<String> references = ((SitoolsApplication) getApplication()).getRepresentationInfoReferences();
    for (String ref : references) {
      repInfo = new RepresentationInfo();
      if (ref.endsWith("response_out")) {
        repInfo.setReference(ref);
        responseInfo.getRepresentations().add(repInfo);
      }
    }
    info.getResponses().add(responseInfo);
  }

  /**
   * Configure the information to add a standard Sitools2 response
   * 
   * @param info
   *          the WADL method information
   */
  public final void addStandardObjectResponseInfo(MethodInfo info) {
    ResponseInfo responseInfo = new ResponseInfo("Returns a SITools2 response, containing the object of interest.");
    responseInfo.getStatuses().add(Status.SUCCESS_OK);
    responseInfo.getRepresentations().clear();
    RepresentationInfo repInfo;
    ArrayList<String> references = ((SitoolsApplication) getApplication()).getRepresentationInfoReferences();
    for (String ref : references) {
      repInfo = new RepresentationInfo();
      if (ref.contains("object")) {
        repInfo.setReference(ref);
        responseInfo.getRepresentations().add(repInfo);
      }
    }
    info.getResponses().add(responseInfo);
  }

  /**
   * Method to give WADL information for notification API
   * 
   * @param info
   *          the WADL method information
   */
  public final void addStandardNotificationInfo(MethodInfo info) {

    RepresentationInfo stringIn = new RepresentationInfo(MediaType.APPLICATION_JAVA_OBJECT);
    RepresentationInfo stringOut = new RepresentationInfo(MediaType.APPLICATION_JAVA_OBJECT);
    stringIn.setDocumentation("Message received by the object from observed objects.");
    stringOut.setDocumentation("Message sent back to notifier.");

    // Method
    info.setDocumentation("This method handles the message sent by notifiers");
    info.setIdentifier("notify_converterschained");

    // Request
    RequestInfo reqInfo = new RequestInfo("The request contains the message sent by observed objects.");
    reqInfo.getRepresentations().add(stringIn);
    info.setRequest(reqInfo);

    // Response 200
    ResponseInfo response = new ResponseInfo("Response sent to indicate that handling occured");
    response.getStatuses().add(Status.SUCCESS_OK);
    response.getRepresentations().add(stringOut);
    info.getResponses().add(response);

    // Response 500
    response = new ResponseInfo("Response when internal server error occurs");
    response.getStatuses().add(Status.SERVER_ERROR_INTERNAL);
    info.getResponses().add(response);
  }

  /**
   * Method to give WADL information for Internal server error response
   * 
   * @param info
   *          the WADL method information
   */
  public final void addStandardInternalServerErrorInfo(MethodInfo info) {
    ResponseInfo responseInfo = new ResponseInfo();
    responseInfo.getStatuses().add(Status.SERVER_ERROR_INTERNAL);
    responseInfo.setDocumentation("Server internal error occurred");
    info.getResponses().add(responseInfo);
  }

  /**
   * Add the parameters used in the ResourceCollectionFilter
   * 
   * @param info
   *          the WADL method information
   */
  public final void addStandardResourceCollectionFilterInfo(MethodInfo info) {

    ParameterInfo paramStart = new ParameterInfo("start", false, "xs:string", ParameterStyle.QUERY, "The start index");
    info.getRequest().getParameters().add(paramStart);

    ParameterInfo paramLimit = new ParameterInfo("limit", false, "xs:string", ParameterStyle.QUERY,
        "The number of records");
    info.getRequest().getParameters().add(paramLimit);

    ParameterInfo paramQuery = new ParameterInfo("query", false, "xs:string", ParameterStyle.QUERY, "The query filter");
    info.getRequest().getParameters().add(paramQuery);

    ParameterInfo paramSort = new ParameterInfo("sort", false, "xs:string", ParameterStyle.QUERY, "The sorting field");
    info.getRequest().getParameters().add(paramSort);

    ParameterInfo paramDir = new ParameterInfo("dir", false, "xs:string", ParameterStyle.QUERY,
        "The sorting direction (ASC or DESC)");
    info.getRequest().getParameters().add(paramDir);

    ParameterInfo paramMode = new ParameterInfo("mode", false, "xs:string", ParameterStyle.QUERY,
        "The query mode (strict or startwith)");
    paramMode.setDefaultValue("startwith");
    info.getRequest().getParameters().add(paramMode);
  }

  /**
   * Get the Sitools application that handles this resource
   * 
   * @return the Sitools application that handle this resource
   */
  public final SitoolsApplication getSitoolsApplication() {
    return (SitoolsApplication) getApplication();
  }

  /**
   * Get the Sitools property
   * 
   * @param property
   *          the property to reach
   * @return the property wanted
   */
  public final String getSitoolsSetting(String property) {
    return getSitoolsApplication().getSettings().getString(property);
  }

  /**
   * Get the SitoolsSettings for the application or the defaut settings if null.
   * 
   * @return SitoolsSettings
   */
  public final SitoolsSettings getSettings() {
    SitoolsSettings settings = getSitoolsApplication().getSettings();
    if (settings == null) {
      settings = SitoolsSettings.getInstance();
    }
    return settings;
  }

}

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
package fr.cnes.sitools.common.application;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

import fr.cnes.sitools.security.filter.AuthenticatorFilter;
import org.restlet.Application;
import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Preference;
import org.restlet.data.Status;
import org.restlet.ext.wadl.ApplicationInfo;
import org.restlet.ext.wadl.DocumentationInfo;
import org.restlet.ext.wadl.ExtendedWadlApplication;
import org.restlet.ext.wadl.RepresentationInfo;
import org.restlet.representation.ObjectRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.routing.Filter;
import org.restlet.security.Authorizer;
import org.restlet.security.ChallengeAuthenticator;
import org.restlet.security.DelegatedAuthorizer;

import fr.cnes.sitools.common.SitoolsSettings;
import fr.cnes.sitools.common.model.Category;
import fr.cnes.sitools.common.model.Resource;
import fr.cnes.sitools.security.authentication.AuthenticatorFactory;
import fr.cnes.sitools.security.authentication.SitoolsRealm;
import fr.cnes.sitools.security.authorization.business.SitoolsOrAuthorizer;
import fr.cnes.sitools.security.authorization.business.SitoolsUserAuthorizer;
import fr.cnes.sitools.security.authorization.client.ResourceAuthorization;
import fr.cnes.sitools.security.filter.NotAuthenticatedFilter;
import fr.cnes.sitools.security.filter.UserBlackListFilter;
import fr.cnes.sitools.server.Consts;
import fr.cnes.sitools.util.RIAPUtils;

/**
 * Root abstract class for SITools applications.
 * 
 * @author jp.boignard (AKKA Technologies)
 * 
 */
public abstract class SitoolsApplication extends ExtendedWadlApplication {

  /** List of representations */
  protected static Map<String, RepresentationInfo> representationInfos = new HashMap<String, RepresentationInfo>();

  /** Automatic registration done after describe. */
  private Boolean autoRegistration = false;

  /** If an authorizer has been set. */
  private boolean authorizationSecure = false;

  /** Default URL attachment. */
  private String attachementRef;

  /** Complete public URL. */
  // private String publicBaseRef;

  /** Realm authenticatorRealm. */
  private SitoolsRealm authenticationRealm = null;

  /** Authorizer */
  private Authorizer authorizer = null;

  /** Id. */
  private String id;

  /** The type of the application */
  private String type = null;

  /** Settings */
  private SitoolsSettings settings = null;

  /** Category */
  private Category category = Category.USER;

  /** info */
  private SitoolsApplicationInfo sitoolsApplicationInfo = null;

  /** True to set that the application needs a user authentication, false otherwise */
  private boolean isUserAuthenticationNeeded = true;

  static {

    RepresentationInfo riXmlObjIn = new RepresentationInfo(MediaType.APPLICATION_XML);
    riXmlObjIn.setIdentifier("xml_object_in");
    DocumentationInfo docInfo = new DocumentationInfo();
    docInfo.setTitle("XML object input representation");
    docInfo.setTextContent("XML object representation sent to the server");
    riXmlObjIn.setDocumentation(docInfo);
    representationInfos.put("xml_object_in", riXmlObjIn);

    RepresentationInfo riXmlObjOut = new RepresentationInfo(MediaType.APPLICATION_XML);
    riXmlObjOut.setIdentifier("xml_object_out");
    docInfo = new DocumentationInfo();
    docInfo.setTitle("XML object output representation");
    docInfo.setTextContent("XML object representation sent back by the server");
    riXmlObjOut.setDocumentation(docInfo);
    representationInfos.put("xml_object_out", riXmlObjOut);

    RepresentationInfo riXmlResOut = new RepresentationInfo(MediaType.APPLICATION_XML);
    riXmlResOut.setIdentifier("xml_response_out");
    docInfo = new DocumentationInfo();
    docInfo.setTitle("XML response output representation");
    docInfo.setTextContent("XML basic response representation sent back by the server");
    riXmlResOut.setDocumentation(docInfo);
    representationInfos.put("xml_response_out", riXmlResOut);

    RepresentationInfo riJsonObjIn = new RepresentationInfo(MediaType.APPLICATION_JSON);
    riJsonObjIn.setIdentifier("json_object_in");
    docInfo = new DocumentationInfo();
    docInfo.setTitle("JSON object input representation");
    docInfo.setTextContent("JSON object representation sent to the server");
    riJsonObjIn.setDocumentation(docInfo);
    representationInfos.put("json_object_in", riJsonObjIn);

    RepresentationInfo riJsonObjOut = new RepresentationInfo(MediaType.APPLICATION_JSON);
    riJsonObjOut.setIdentifier("json_object_out");
    docInfo = new DocumentationInfo();
    docInfo.setTitle("JSON object output representation");
    docInfo.setTextContent("JSON object representation sent back by the server");
    riJsonObjOut.setDocumentation(docInfo);
    representationInfos.put("json_object_out", riJsonObjOut);

    RepresentationInfo riJsonResOut = new RepresentationInfo(MediaType.APPLICATION_JSON);
    riJsonResOut.setIdentifier("json_response_out");
    docInfo = new DocumentationInfo();
    docInfo.setTitle("JSON response output representation");
    docInfo.setTextContent("JSON basic response representation sent back by the server");
    riJsonResOut.setDocumentation(docInfo);
    representationInfos.put("json_response_out", riJsonResOut);

    RepresentationInfo riHtmlFreemarker = new RepresentationInfo(MediaType.TEXT_HTML);
    riHtmlFreemarker.setIdentifier("html_freemarker");
    docInfo = new DocumentationInfo();
    docInfo.setTitle("HTML Freemarker");
    docInfo.setTextContent("HTML Freemarker template for static web applications on client side.");
    riHtmlFreemarker.setDocumentation(docInfo);
    representationInfos.put("html_freemarker", riHtmlFreemarker);

  }

  /**
   * Default constructor.
   */
  public SitoolsApplication() {
    super();
    defaultDescribe();
  }

  /**
   * Constructor with context.
   * 
   * @param context
   *          Restlet host context
   */
  public SitoolsApplication(Context context) {
    super(context);

    settings = (SitoolsSettings) context.getAttributes().get(ContextAttributes.SETTINGS);

    attachementRef = (String) context.getAttributes().get(ContextAttributes.APP_ATTACH_REF);
    // publicBaseRef = (String) context.getAttributes().get(ContextAttributes.APP_BASE_REF);
    autoRegistration = (Boolean) context.getAttributes().get(ContextAttributes.APP_REGISTER);
    id = (String) context.getAttributes().get(ContextAttributes.APP_ID);

    defaultDescribe();

    if (id == null) {
      id = "urn:uuid:" + this.getName() + ":type:" + this.getClass().getName();
    }

    authenticationRealm = (SitoolsRealm) context.getAttributes().get(ContextAttributes.APP_REALM);

    if (authenticationRealm == null) {
      getLogger().config("No authenticationRealm in context for application " + getName() + "\nUse default.");
      authenticationRealm = settings.getAuthenticationRealm();
    }

    boolean methodTunnel = Boolean.parseBoolean(settings.getString("Starter.TunnelService.MethodTunnel"));
    this.getTunnelService().setMethodTunnel(methodTunnel);
  }

  /**
   * Constructor with context and representation of the application configuration.
   * 
   * @param arg0
   *          Restlet context
   * @param arg1
   *          wadl representation
   */
  public SitoolsApplication(Context arg0, Representation arg1) {
    super(arg0, arg1);
    defaultDescribe();
  }

  /**
   * Abstract method for thinking about implementing it for each concrete application describe is the RESTlet method,
   * but overriding, prevents the self-describing functionality.
   */
  public abstract void sitoolsDescribe();

  /**
   * Default description.
   */
  public final void defaultDescribe() {
    setAuthor("AKKA Technologies");
    setOwner("CNES");

    setName(this.getClass().getCanonicalName());
    // IF the class is anonymous, we take the superclass
    if (this.getClass().isAnonymousClass()) {
      // set type, superClass
      setType(this.getClass().getSuperclass().getName());
    }
    else {
      // set type class
      setType(this.getClass().getName());
    }

    sitoolsDescribe();

    setAutoDescribing(true);

    if ((null != autoRegistration) && autoRegistration) {
      register();
    }
  }

  /**
   * Application registration.
   */
  public final void register() {
    Resource resource = this.wrapToResource();

    ArrayList<Preference<MediaType>> objectMediaType = new ArrayList<Preference<MediaType>>();
    objectMediaType.add(new Preference<MediaType>(MediaType.APPLICATION_JAVA_OBJECT));
    Request reqPOST = new Request(Method.POST, RIAPUtils.getRiapBase()
        + settings.getString(Consts.APP_APPLICATIONS_URL), new ObjectRepresentation<Resource>(resource));
    reqPOST.getClientInfo().setAcceptedMediaTypes(objectMediaType);
    org.restlet.Response r = getContext().getClientDispatcher().handle(reqPOST);

    if (r == null) {
      throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
    }
    if (Status.isError(r.getStatus().getCode())) {
      getLogger().warning("SitoolsApplication.register " + r.getStatus().getDescription());
      // echec access User application
      throw new ResourceException(r.getStatus());
    }

    try {
      @SuppressWarnings("unchecked")
      Resource repPOST = ((ObjectRepresentation<Resource>) r.getEntity()).getObject();
      getLogger().info("Application " + repPOST.getName() + " registered.");
    }
    catch (IOException e) {
      // marshalling error
      throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
    }
  }

  /**
   * Application registration.
   */
  public final void unregister() {

    Request reqDELETE = new Request(Method.DELETE, RIAPUtils.getRiapBase()
        + settings.getString(Consts.APP_APPLICATIONS_URL) + "/" + this.getId());
    org.restlet.Response r = getContext().getClientDispatcher().handle(reqDELETE);

    if (r == null) {
      throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
    }
    if (Status.isError(r.getStatus().getCode())) {
      getLogger().warning("SitoolsApplication.unregister " + r.getStatus().getDescription());
      throw new ResourceException(r.getStatus());
    }
  }

  /**
   * Gets authorizer for an object in context application.
   * 
   * @param objectUUID
   *          unique identifier for object
   * @return Authorizer
   */
  public final DelegatedAuthorizer getAuthorizer(String objectUUID) {

    Request reqGET = new Request(Method.GET, RIAPUtils.getRiapBase()
        + settings.getString(Consts.APP_AUTHORIZATIONS_URL) + "/" + objectUUID + "/authorizer");
    ArrayList<Preference<MediaType>> objectMediaType = new ArrayList<Preference<MediaType>>();
    objectMediaType.add(new Preference<MediaType>(MediaType.APPLICATION_JAVA_OBJECT));
    reqGET.getClientInfo().setAcceptedMediaTypes(objectMediaType);
    org.restlet.Response response = getContext().getClientDispatcher().handle(reqGET);

    if (response == null || Status.isError(response.getStatus().getCode())) {
      return null;
    }

    @SuppressWarnings("unchecked")
    ObjectRepresentation<ResourceAuthorization> or = (ObjectRepresentation<ResourceAuthorization>) response.getEntity();
    try {
      ResourceAuthorization myObj = or.getObject();
      DelegatedAuthorizer result = myObj.wrap(this.getAuthenticationRealm().getReferenceRoles(), this);

      return result;
    }
    catch (IOException e) { // marshalling error
      throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
    }
  }

  /**
   * Gets authorizer for an application.
   * 
   * @param context
   *          RESTlet application context
   * @param reference
   *          ConcurrentHashMap of Role objects to be used in the Authorizer.
   * @return Authorizer
   */
  public final DelegatedAuthorizer getAuthorizer(Context context,
      ConcurrentHashMap<String, org.restlet.security.Role> reference) {

    Request reqGET = new Request(Method.GET, RIAPUtils.getRiapBase()
        + settings.getString(Consts.APP_AUTHORIZATIONS_URL) + "/" + this.wrapToResource().getId() + "/authorizer");
    ArrayList<Preference<MediaType>> objectMediaType = new ArrayList<Preference<MediaType>>();
    objectMediaType.add(new Preference<MediaType>(MediaType.APPLICATION_JAVA_OBJECT));
    reqGET.getClientInfo().setAcceptedMediaTypes(objectMediaType);
    org.restlet.Response response = context.getClientDispatcher().handle(reqGET);

    if (response == null || Status.isError(response.getStatus().getCode())) {
      return null;
    }

    @SuppressWarnings("unchecked")
    ObjectRepresentation<ResourceAuthorization> or = (ObjectRepresentation<ResourceAuthorization>) response.getEntity();
    try {
      ResourceAuthorization myObj = or.getObject();
      DelegatedAuthorizer result = myObj.wrap(reference, this);

      return result;
    }
    catch (IOException e) { // marshalling error
      throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
    }
  }

  /**
   * Authorization management with URI ...
   * 
   * @param restlet
   *          internal router
   * @param userRequestAttribute
   *          user request attribute
   * @param methodsForPublic
   *          the List of methods allowed for public user
   * @return RESTlet with security
   */
  public final Restlet addSecurity(Restlet restlet, String userRequestAttribute, List<Method> methodsForPublic) {
    if (this.authenticationRealm == null) {
      this.authorizationSecure = false;
      return addSecurityFilter(getContext(), restlet);      
    }

    Authorizer authorizer = this.getUserAuthorizer(restlet.getContext(), this.authenticationRealm.getReferenceRoles(),
        userRequestAttribute, this, methodsForPublic);
    if ((authorizer == null) || (authorizer == Authorizer.ALWAYS)) {
      this.getLogger().warning("No security configuration for " + this.getName());
      this.authorizationSecure = false;

      NotAuthenticatedFilter notAuthenticatedFilter = new NotAuthenticatedFilter();
      if("TRUE".equals(restlet.getContext().getAttributes().get(ContextAttributes.AUTHENTICATED_FILTER))){
        AuthenticatorFilter authenticatorFilter = new AuthenticatorFilter(getContext());
        authenticatorFilter.setNext(restlet);
        // attach a filter to block bad authentication
        notAuthenticatedFilter.setNext(authenticatorFilter);
      }
      else {
        // attach a filter to block bad authentication
        notAuthenticatedFilter.setNext(restlet);
      }
          
      // attach a filter to block bad authentication
      UserBlackListFilter userBlackListFilter = new UserBlackListFilter(getContext());
      userBlackListFilter.setNext(notAuthenticatedFilter);


      return addSecurityFilter(getContext(), userBlackListFilter);
    }
    else {
      // Authentication is mandatory ? (optional - sinon fenetre login navigateur...)
      ChallengeAuthenticator authenticator = AuthenticatorFactory.getAuthenticator(restlet.getContext(), true,
          getSettings().getAuthenticationDOMAIN(), authenticationRealm);

      // attach a filter to block bad authentication
      NotAuthenticatedFilter notAuthenticatedFilter = new NotAuthenticatedFilter();
      // attach a filter to block bad authentication
      UserBlackListFilter userBlackListFilter = new UserBlackListFilter(getContext());

      authenticator.setNext(userBlackListFilter);

      userBlackListFilter.setNext(notAuthenticatedFilter);

      if("TRUE".equals(restlet.getContext().getAttributes().get(ContextAttributes.AUTHENTICATED_FILTER))){
        AuthenticatorFilter authenticatorFilter = new AuthenticatorFilter(getContext());
        // attach a filter to block bad authentication
        notAuthenticatedFilter.setNext(authenticatorFilter);
        authenticatorFilter.setNext(authorizer);
      }
      else {
        // attach a filter to block bad authentication
        notAuthenticatedFilter.setNext(authorizer);
      }
      
      authorizer.setNext(restlet);

      return addSecurityFilter(getContext(), authenticator);
    }
  }

  /**
   * Gets authorizer for an application.
   * 
   * @param application
   *          the application
   * @param context
   *          RESTlet application context
   * @param reference
   *          ConcurrentHashMap of Role objects to be used in the Authorizer.
   * @param userRequestAttribute
   *          user request attribute
   * @param methodsForPublic
   *          the List of methods allowed for public user
   * @return Authorizer
   */
  public final Authorizer getUserAuthorizer(Context context,
      ConcurrentHashMap<String, org.restlet.security.Role> reference, String userRequestAttribute,
      Application application, List<Method> methodsForPublic) {
    authorizer = null;

    Request reqGET = new Request(Method.GET, RIAPUtils.getRiapBase()
        + settings.getString(Consts.APP_AUTHORIZATIONS_URL) + "/" + this.wrapToResource().getId() + "/authorizer");
    ArrayList<Preference<MediaType>> objectMediaType = new ArrayList<Preference<MediaType>>();
    objectMediaType.add(new Preference<MediaType>(MediaType.APPLICATION_JAVA_OBJECT));
    reqGET.getClientInfo().setAcceptedMediaTypes(objectMediaType);
    org.restlet.Response response = context.getClientDispatcher().handle(reqGET);

    if (response == null || Status.isError(response.getStatus().getCode())) {
      return null;
    }

    @SuppressWarnings("unchecked")
    ObjectRepresentation<ResourceAuthorization> or = (ObjectRepresentation<ResourceAuthorization>) response.getEntity();
    try {
      ResourceAuthorization myObj = or.getObject();
      Authorizer result = myObj.wrap(reference, application);

      // Check
      ArrayList<Authorizer> userAuthorizers = new ArrayList<Authorizer>();

      // Authorization for User
      SitoolsUserAuthorizer sua = new SitoolsUserAuthorizer(userRequestAttribute, methodsForPublic);
      userAuthorizers.add(sua);

      // Authorization on UserStorageApplication by default
      userAuthorizers.add(result);

      // Result is a Or Authorizer
      authorizer = new SitoolsOrAuthorizer(userAuthorizers);
      return authorizer;
    }
    catch (IOException e) { // marshalling error
      throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
    }
  }

  @Override
  /**
   * logging starting application.
   */
  public synchronized void start() throws Exception {
    if (isStopped()) {
      getLogger().info("Starting " + this.getName() + " application...");
      super.start();
    }
    else {
      getLogger().fine("Application does not need to be started.");
    }
  }

  @Override
  /**
   * logging stopping application.
   */
  public synchronized void stop() throws Exception {
    if (isStarted()) {
      getLogger().info("Stopping " + this.getName() + " application...");
      super.stop();

    }
    else {
      getLogger().fine("Application does not need to be stopped.");
    }
  }

  /**
   * Gets the attachementRef value
   * 
   * @return the attachementRef
   */
  public final String getAttachementRef() {
    return attachementRef;
  }

  /**
   * Gets the publicBaseRef value
   * 
   * @param request
   *          the request sent
   * @return the publicBaseRef
   */
  public final String getPublicBaseRef(Request request) {
    return request.getAttributes().get(ContextAttributes.PUBLIC_HOST_NAME) + getAttachementRef();
  }

  /**
   * Resource Representation of the application.
   * 
   * @return Resource definition of the application
   */
  public final Resource wrapToResource() {
    Resource resource = new Resource();
    resource.setName(this.getName());
    resource.setDescription(this.getDescription());
    resource.setAuthor(this.getAuthor());
    resource.setOwner(this.getOwner());

    resource.setLastUpdate(new Date().toString());

    resource.setType(this.getType());
    resource.setCategory(this.getCategory());

    resource.setUrn("urn:uuid:" + this.getName() + ":type:" + this.getClass().getName());

    // relative url
    resource.setUrl(attachementRef);
    resource.setId((this.getId() == null) ? resource.getUrn() : this.getId());

    if (this.isStarted()) {
      resource.setStatus("ACTIVE");
    }
    else {
      resource.setStatus("INACTIVE");
    }
    return resource;
  }

  /**
   * Add Security filter in front of the Restlet
   * 
   * @param context
   *          Restlet context
   * @param restlet
   *          Restlet to be secured
   * @return a new instance of Security Filter securing the original Restlet
   */
  public final Restlet addSecurityFilter(Context context, Restlet restlet) {
    Filter filter;
    context.getAttributes().put("application", this);
    String classname = settings.getString("Security.filter.class");
    try {
      @SuppressWarnings("unchecked")
      Class<Filter> filterClass = (Class<Filter>) Class.forName(classname);
      Constructor<Filter> constructor = filterClass.getConstructor(Context.class);
      filter = constructor.newInstance(context);
      filter.setNext(restlet);
      return filter;
    }
    catch (ClassNotFoundException e) {
      getLogger().log(Level.WARNING,
          "Security filter not attached on application : " + this.getName() + " ClassNotFoundException :" + classname,
          e);

    }
    catch (InstantiationException e) {
      getLogger().log(Level.WARNING,
          "Security filter not attached on application : " + this.getName() + " InstantiationException :" + classname,
          e);
    }
    catch (IllegalAccessException e) {
      getLogger().log(Level.WARNING,
          "Security filter not attached on application : " + this.getName() + " IllegalAccessException :" + classname,
          e);
    }
    catch (SecurityException e) {
      getLogger().log(Level.WARNING,
          "Security filter not attached on application : " + this.getName() + " SecurityException :" + classname, e);
    }
    catch (NoSuchMethodException e) {
      getLogger()
          .log(
              Level.WARNING,
              "Security filter not attached on application : " + this.getName() + " NoSuchMethodException :"
                  + classname, e);
    }
    catch (IllegalArgumentException e) {
      getLogger()
          .log(
              Level.WARNING,
              "Security filter not attached on application : " + this.getName() + " IllegalArgumentException :"
                  + classname, e);
    }
    catch (InvocationTargetException e) {
      getLogger().log(
          Level.WARNING,
          "Security filter not attached on application : " + this.getName() + " InvocationTargetException :"
              + classname, e);
    }

    return restlet;

  }

  /**
   * Setting security.
   * 
   * @param application
   *          Application router to be attached with the authenticator/authorizer
   * @return Restlet
   */
  public final Restlet addSecurity(SitoolsApplication application) {
    authorizer = null;

    if (authenticationRealm == null && !isContextAuthorization()) {
      authorizationSecure = false;
      return addSecurityFilter(getContext(), application);
    }

    if (!application.isUserAuthenticationNeeded()) {
      authorizationSecure = false;
      return addSecurityFilter(getContext(), application);
    }

    Authorizer localAuthorizer = getAuthorizer(application);

    if ((localAuthorizer == null) || (localAuthorizer == Authorizer.ALWAYS)) {
      getLogger().warning("No security configuration for " + this.getName());
      authorizationSecure = false;

      // optional authenticator
      ChallengeAuthenticator auth = getChallengeAuthenticator(application);

      if (!Category.PUBLIC.equals(application.getCategory())) {
        // attach a filter to block bad authentication
        UserBlackListFilter userBlackListFilter = new UserBlackListFilter(getContext());
        auth.setNext(userBlackListFilter);


        // attach a filter to block bad authentication
        NotAuthenticatedFilter notAuthenticatedFilter = new NotAuthenticatedFilter();
        userBlackListFilter.setNext(notAuthenticatedFilter);

        if("TRUE".equals(application.getContext().getAttributes().get(ContextAttributes.AUTHENTICATED_FILTER))){
          AuthenticatorFilter authenticatorFilter = new AuthenticatorFilter(getContext());
          // attach a filter to block bad authentication
          notAuthenticatedFilter.setNext(authenticatorFilter);
          authenticatorFilter.setNext(application);
        }
        else {
          // attach a filter to block bad authentication
          notAuthenticatedFilter.setNext(application);
        }
      }
      else {
        auth.setNext(application);
      }

      return addSecurityFilter(getContext(), auth);

    }
    else {
      authorizationSecure = true;

      // optional authenticator
      ChallengeAuthenticator auth = getChallengeAuthenticator(application);

      if (Category.PUBLIC.equals(application.getCategory())) {
        auth.setNext(localAuthorizer);
      }
      else {
        // attach a filter to block bad authentication
        UserBlackListFilter userBlackListFilter = new UserBlackListFilter(getContext());
        auth.setNext(userBlackListFilter);

        // attach a filter to block bad authentication
        NotAuthenticatedFilter notAuthenticatedFilter = new NotAuthenticatedFilter();

        userBlackListFilter.setNext(notAuthenticatedFilter);
        
        if("TRUE".equals(application.getContext().getAttributes().get(ContextAttributes.AUTHENTICATED_FILTER))){
          AuthenticatorFilter authenticatorFilter = new AuthenticatorFilter(getContext());
          // attach a filter to block bad authentication
          notAuthenticatedFilter.setNext(authenticatorFilter);
          authenticatorFilter.setNext(localAuthorizer);
        }
        else {
          // attach a filter to block bad authentication
          notAuthenticatedFilter.setNext(localAuthorizer);
        }
      }

      localAuthorizer.setNext(application);

      authorizer = localAuthorizer;
      return addSecurityFilter(getContext(), auth);
    }

  }

  /**
   * Checks if there is custom authorization in the context.
   * 
   * @return true, if there is custom authorization in the context, false otherwise
   */
  private boolean isContextAuthorization() {

    return (getContext().getAttributes().containsKey(ContextAttributes.CUSTOM_AUTHORIZER) || getContext()
        .getAttributes().containsKey(ContextAttributes.CUSTOM_CHALLENGE_AUTHENTICATOR));

  }

  /**
   * Gets the authorizer of the given {@link SitoolsApplication} from its context or the default one
   * 
   * 
   * @param application
   *          the application
   * @return the authorizer for the application
   */
  private Authorizer getAuthorizer(SitoolsApplication application) {
    Authorizer localAuthorizer;
    if (getContext().getAttributes().containsKey(ContextAttributes.CUSTOM_AUTHORIZER)) {
      try {
        localAuthorizer = (Authorizer) getContext().getAttributes().get(ContextAttributes.CUSTOM_AUTHORIZER);
      }
      catch (Exception e) {
        localAuthorizer = getAuthorizer(application.getContext(), authenticationRealm.getReferenceRoles());
        getLogger().log(Level.WARNING, "Cannot cast ContextAttributes.CUSTOM_AUTHORIZER to Authorizer", e);
      }
    }
    else {
      localAuthorizer = getAuthorizer(application.getContext(), authenticationRealm.getReferenceRoles());
    }
    return localAuthorizer;
  }

  /**
   * Gets the ChallengeAuthenticator of the given {@link SitoolsApplication} from its context or the default one
   * 
   * 
   * @param application
   *          the application
   * @return the ChallengeAuthenticator for the application
   */
  private ChallengeAuthenticator getChallengeAuthenticator(SitoolsApplication application) {
    ChallengeAuthenticator auth;

    if (getContext().getAttributes().containsKey(ContextAttributes.CUSTOM_CHALLENGE_AUTHENTICATOR)) {
      try {
        auth = (ChallengeAuthenticator) getContext().getAttributes().get(
            ContextAttributes.CUSTOM_CHALLENGE_AUTHENTICATOR);
      }
      catch (Exception e) {
        auth = AuthenticatorFactory.getAuthenticator(application.getContext(), true,
            settings.getAuthenticationDOMAIN(), authenticationRealm);
        getLogger().log(Level.WARNING,
            "Cannot cast ContextAttributes.CUSTOM_CHALLENGE_AUTHENTICATOR to ChallengeAuthenticator", e);
      }
    }
    else {
      auth = AuthenticatorFactory.getAuthenticator(application.getContext(), true, settings.getAuthenticationDOMAIN(),
          authenticationRealm);
    }
    return auth;
  }

  /**
   * Get the secured application by default
   * 
   * @return secured application according to the defined security.Authorizations for the application.
   */
  public Restlet getSecure() {
    return addSecurity(this);
  }

  /**
   * Gets the authorizationSecure value.
   * 
   * @return the authorizationSecure
   */
  public final boolean isAuthorizationSecure() {
    return authorizationSecure;
  }

  /**
   * Gets the id value.
   * 
   * @return the id
   */
  public final String getId() {
    return id;
  }

  /**
   * Sets the value of id.
   * 
   * @param idd
   *          the id to set
   */
  public final void setId(String idd) {
    this.id = idd;
  }

  /**
   * Gets the authorizer value
   * 
   * @return the authorizer
   */
  public final Authorizer getAuthorizer() {
    return authorizer;
  }

  /**
   * Gets the type value
   * 
   * @return the type
   */
  public final String getType() {
    return type;
  }

  /**
   * Sets the value of type
   * 
   * @param type
   *          the type to set
   */
  public final void setType(String type) {
    this.type = type;
  }

  /**
   * Gets the category value
   * 
   * @return the category
   */
  public final Category getCategory() {
    return category;
  }

  /**
   * Sets the value of category
   * 
   * @param category
   *          the category to set
   */
  public final void setCategory(Category category) {
    this.category = category;
  }

  /**
   * Gets the settings value
   * 
   * @return the settings
   */
  public final SitoolsSettings getSettings() {
    return settings;
  }

  /**
   * Gets the sitoolsApplicationInfo value
   * 
   * @return the sitoolsApplicationInfo
   */
  public final SitoolsApplicationInfo getSitoolsApplicationInfo() {
    return sitoolsApplicationInfo;
  }

  /**
   * Set the fact that the application is auto registering
   * 
   * @param autoRegister
   *          true to set that the application is autoRegistering
   */
  public final void setAutoRegistration(boolean autoRegister) {
    this.autoRegistration = autoRegister;
  }

  /**
   * Get authentication Realm
   * 
   * @return the authentication realm
   */
  public final SitoolsRealm getAuthenticationRealm() {
    return this.authenticationRealm;
  }

  /**
   * Sets the value of sitoolsApplicationInfo
   * 
   * @param sitoolsApplicationInfo
   *          the sitoolsApplicationInfo to set
   */
  public final void setSitoolsApplicationInfo(SitoolsApplicationInfo sitoolsApplicationInfo) {
    this.sitoolsApplicationInfo = sitoolsApplicationInfo;
  }

  @Override
  public ApplicationInfo getApplicationInfo(Request request, Response response) {
    ApplicationInfo result = super.getApplicationInfo(request, response);
    result.getRepresentations().addAll(representationInfos.values());
    return result;
  }

  /**
   * Method to get the list of representation info references
   * 
   * @return the list of representation info references
   */
  public final ArrayList<String> getRepresentationInfoReferences() {
    return new ArrayList<String>(representationInfos.keySet());
  }

  /**
   * Gets the isUserAuthenticationNeeded value
   * 
   * @return the isUserAuthenticationNeeded
   */
  public boolean isUserAuthenticationNeeded() {
    return isUserAuthenticationNeeded;
  }

  /**
   * Sets the value of isUserAuthenticationNeeded
   * 
   * @param isUserAuthenticationNeeded
   *          the isUserAuthenticationNeeded to set
   */
  public void setUserAuthenticationNeeded(boolean isUserAuthenticationNeeded) {
    this.isUserAuthenticationNeeded = isUserAuthenticationNeeded;
  }
  // /**
  // * Create a new authorization for administrator on the given {@link SitoolsApplication}. If the application have
  // * already an application it does nothing.
  // *
  // * @param app
  // * the {@link SitoolsApplication}
  // *
  // */
  // public void createAuthorizationForAdministrator(SitoolsApplication app) {
  // // create a authorization by default if none exists
  // // DelegatedAuthorizer authorizer = app.getAuthorizer(dsa.getId());
  //
  // ResourceAuthorization resAuthApp = RIAPUtils.getObject(getSettings().getString(Consts.APP_AUTHORIZATIONS_URL) + "/"
  // + app.getId(), getContext());
  // if (resAuthApp == null) {
  //
  // // add a new authorization with default rights
  // ResourceAuthorization resAuth = new ResourceAuthorization();
  // resAuth.setId(app.getId());
  // resAuth.setName(app.getName());
  // resAuth.setDescription(app.getDescription());
  // resAuth.setUrl(app.getAttachementRef());
  // ArrayList<RoleAndMethodsAuthorization> authorizations = new ArrayList<RoleAndMethodsAuthorization>();
  // // role for adminstrator
  // RoleAndMethodsAuthorization authorizationAdmin = new RoleAndMethodsAuthorization();
  // String adminRole = getSettings().getString(Consts.ADMINSTRATOR_ROLE);
  // authorizationAdmin.setRole(adminRole);
  // // authorizationAdmin.setPutMethod(true);
  // // authorizationAdmin.setOptionsMethod(true);
  // // authorizationAdmin.setGetMethod(true);
  // setMethodsToAuthorization(authorizationAdmin, getSettings().getString(Consts.DYNAMIC_APPLICATION_ADMIN_RIGHTS));
  // authorizations.add(authorizationAdmin);
  //
  // // role for public
  // RoleAndMethodsAuthorization authorizationPublic = new RoleAndMethodsAuthorization();
  // String publicRole = SecurityUtil.PUBLIC_ROLE;
  // authorizationPublic.setRole(publicRole);
  // // authorizationPublic.setGetMethod(true);
  // setMethodsToAuthorization(authorizationPublic, getSettings().getString(Consts.DYNAMIC_APPLICATION_PUBLIC_RIGHTS));
  // authorizations.add(authorizationPublic);
  //
  // resAuth.setAuthorizations(authorizations);
  //
  // // POST dataset authorization
  // ResourceAuthorization appAuthorization = RIAPUtils.updateObject(resAuth,
  // getSettings().getString(Consts.APP_AUTHORIZATIONS_URL) + "/" + app.getId(), app.getContext());
  // if (appAuthorization == null) {
  // getLogger().warning("Impossible to add default administrator authorization for application " + app.getId());
  // }
  //
  // }
  //
  // }
  //
  // /**
  // * Sets the method authorized on a {@link RoleAndMethodsAuthorization} specified in the list of rights given
  // *
  // * @param auth
  // * the {@link RoleAndMethodsAuthorization}
  // * @param listOfRights
  // * the list of rights (a String slitted by |)
  // */
  // private void setMethodsToAuthorization(RoleAndMethodsAuthorization auth, String listOfRights) {
  // List<String> listOfRightsList = Arrays.asList(listOfRights.split("\\|"));
  // if (listOfRightsList.contains("GET")) {
  // auth.setGetMethod(true);
  // }
  // if (listOfRightsList.contains("PUT")) {
  // auth.setPutMethod(true);
  // }
  // if (listOfRightsList.contains("POST")) {
  // auth.setPostMethod(true);
  // }
  // if (listOfRightsList.contains("DELETE")) {
  // auth.setDeleteMethod(true);
  // }
  // if (listOfRightsList.contains("HEAD")) {
  // auth.setHeadMethod(true);
  // }
  // if (listOfRightsList.contains("OPTIONS")) {
  // auth.setOptionsMethod(true);
  // }
  //
  // }

}

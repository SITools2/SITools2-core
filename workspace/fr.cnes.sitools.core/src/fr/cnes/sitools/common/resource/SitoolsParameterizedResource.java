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
package fr.cnes.sitools.common.resource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.restlet.data.Disposition;
import org.restlet.data.Parameter;
import org.restlet.data.Status;
import org.restlet.engine.util.DateUtils;
import org.restlet.ext.wadl.MethodInfo;
import org.restlet.ext.wadl.ParameterInfo;
import org.restlet.ext.wadl.ParameterStyle;
import org.restlet.ext.wadl.ResponseInfo;
import org.restlet.representation.EmptyRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import fr.cnes.sitools.common.SitoolsResource;
import fr.cnes.sitools.common.SitoolsSettings;
import fr.cnes.sitools.common.application.ContextAttributes;
import fr.cnes.sitools.common.application.SitoolsParameterizedApplication;
import fr.cnes.sitools.common.model.IResource;
import fr.cnes.sitools.plugins.resources.model.ResourceModel;
import fr.cnes.sitools.plugins.resources.model.ResourceParameter;
import fr.cnes.sitools.plugins.resources.model.ResourceParameterType;

/**
 * Base class for Parameterized resources
 * 
 * Some variables can be used in resource url attachment : ${context_host_ref} ${context_app_ref}
 * ${context_resource_ref}
 * 
 * @author m.marseille (AKKA Technologies)
 */
@XStreamAlias("resourcePlugin")
public abstract class SitoolsParameterizedResource extends SitoolsResource implements IResource {

  /**
   * host url like http://localhost:8182
   */
  static final String HOST_REF = "${context_host_ref}";

  /**
   * application url like http://localhost:8182/sitools
   */
  static final String APPLICATION_REF = "${context_app_ref}";

  /**
   * resource url like http://localhost:8182/sitools/application/plugin
   */
  static final String RESOURCE_REF = "${context_resource_ref}";

  /**
   * path like C:/SITOOLS or "${context_root_dir}/"
   */
  static final String ROOT_DIR = "${context_root_dir}";

  /** The fileName to create */
  protected String fileName;

  /**
   * Model associated to the resource
   */
  private ResourceModel model;

  /**
   * To override model params with request or specific resource instance params.
   */
  private volatile Collection<ResourceParameter> overrideParams = new ArrayList<ResourceParameter>();

  /**
   * Identifier of the resource
   */
  private String id;

  /**
   * Parent of the resource (i.e. its application)
   */
  private String parent;

  @Override
  public void sitoolsDescribe() {
    setName("SitoolsParameterizedResource");
    setDescription("Base class for dynamic resource");
    setNegotiated(false);
  }

  /**
   * Initiate the resource
   * 
   * To initialize shared instance of objects between resources, get the component Context from the Settings and put the
   * object in the Context
   * ((SitoolsSettings)getContext().getAttributes().get(ContextAttributes.SETTINGS)).getComponent()
   * .getContext().getAttributes().put(SOME KEY, SOME OBJECT)
   * 
   */
  @Override
  public void doInit() {
    super.doInit();

    String ref = (String) getContext().getAttributes().get(ContextAttributes.RESOURCE_ATTACHMENT);
    if (ref == null) {
      getLogger().severe("Resource attachment not found in Context Attributes => NULL resource model.");
    }
    else {
      setModel(((SitoolsParameterizedApplication) getApplication()).getModel(ref));
    }
    if (this.model != null) {

      // get The fileName parameter.
      Parameter fileNameParam = getRequest().getResourceRef().getQueryAsForm().getFirst("fileName");
      if (fileNameParam == null) {
        // if it is not in the request parameters, let's get from the model
        ResourceParameter param = getModel().getParameterByName("fileName");
        if (param != null) {
          fileName = param.getValue();
        }
      }
      else {
        fileName = fileNameParam.getValue();
      }

      if (this.model.getApplicationClassName() != null && !this.model.getApplicationClassName().equals("")) {
        Class<?> appClassName = null;
        try {
          appClassName = Class.forName(this.model.getApplicationClassName());
        }
        catch (ClassNotFoundException e) {
          getLogger().warning("Application class : " + this.model.getApplicationClassName() + " not found");
        }

        if (!appClassName.isAssignableFrom(this.getApplication().getClass())) {
          throw new ResourceException(Status.SERVER_ERROR_SERVICE_UNAVAILABLE, "Application : "
              + model.getApplicationClassName() + " is not compatible with a " + getApplication().getClass().getName()
              + " application class");
        }
      }
    }

  }

  /**
   * Sets the value of model
   * 
   * @param model
   *          the model to set
   */
  public final void setModel(ResourceModel model) {
    this.model = model;
  }

  /**
   * Gets the model value
   * 
   * @return the model
   */
  public final ResourceModel getModel() {
    return model;
  }

  /**
   * Sets the value of id
   * 
   * @param id
   *          the id to set
   */
  public final void setId(String id) {
    this.id = id;
  }

  /**
   * Gets the id value
   * 
   * @return the id
   */
  public final String getId() {
    return id;
  }

  /**
   * Sets the value of parent
   * 
   * @param parent
   *          the parent to set
   */
  public final void setParent(String parent) {
    this.parent = parent;
  }

  /**
   * Gets the parent value
   * 
   * @return the parent
   */
  public final String getParent() {
    return parent;
  }

  /**
   * Method to get a parameter value using the map
   * 
   * @param key
   *          the key to get the parameter
   * @return the corresponding value
   */
  public final String getParameterValue(String key) {
    ResourceParameter param = this.getModel().getParametersMap().get(key);

    return applyRegExpReplacement(param);
  }

  /**
   * Method to get a parameter value using the map
   * 
   * @param key
   *          the key to get the parameter
   * @return the corresponding value
   */
  public final String getOverrideParameterValue(String key) {
    ResourceParameter param = this.getModel().getParametersMap().get(key);

    Collection<ResourceParameter> parameters = getOverrideParams();
    for (Iterator<ResourceParameter> iterator = parameters.iterator(); iterator.hasNext();) {
      ResourceParameter resourceParameter = (ResourceParameter) iterator.next();
      if (resourceParameter.getName().equals(key)) {
        param = resourceParameter;
        // getFirst > getLast overridden
        // break;
      }
    }
    if (param == null) {
      getLogger().warning("Param " + key + " not found");
      return null;
    }
    return applyRegExpReplacement(param);
  }

  /**
   * Apply the RegExpReplacement to the given parameter
   * 
   * @param param
   *          a {@link ResourceParameter}
   * @return the String with the RegExp applied
   */
  protected final String applyRegExpReplacement(ResourceParameter param) {
    if (null == param) {
      return null;
    }

    if ("xs:url".equals(param.getValueType())) {
      String url = param.getValue();
      if (param.getValue().contains(HOST_REF)) {
        url = this.replacePublicHostName(url);
        return url;
      }
      if (param.getValue().contains(APPLICATION_REF)) {
        url = this.replaceAppReference(url);
        return url;
      }
      if (param.getValue().contains(RESOURCE_REF)) {
        url = this.replaceResourceReference(url);
        return url;
      }
      // If not recognize, remove all "${}"
      return url.replaceAll("\\$\\{[a-zA-Z_]*\\}", "");
    }
    else if ("xs:path".equals(param.getValueType())) {
      String path = param.getValue();
      if (param.getValue().contains(ROOT_DIR)) {
        path = this.replacePath(path);
        return path;
      }
      // If not recognize, remove all "${}"
      return path.replaceAll("\\$\\{[a-zA-Z_]*\\}", "");
    }
    else if ("xs:path".equals(param.getValueType())) {
      String value = param.getValue();
      return this.replaceSettings(value);
    }
    else if ("xs:template".equals(param.getValueType())) {
      String value = param.getValue();
      if (value.contains("${date:")) {
        int beginTemplateIndex = value.indexOf("${date:");
        int endTemplateIndex = value.indexOf("}", beginTemplateIndex);
        String dateTemplate = value.substring(beginTemplateIndex + "${date:".length(), endTemplateIndex);
        Date date = new Date();
        // TODO voir une fois que les dates seront un peu plus claire
        String dateFormated = DateUtils.format(date, dateTemplate);
        return value.replace("${date:" + dateTemplate + "}", dateFormated);
      }
      return value;
    }
    else {
      return param.getValue();
    }
  }

  /**
   * Get the override parameter specified by the given key if present; if not, returns the original parameter in the
   * model.
   * 
   * @param key
   *          parameter identifier
   * @return Object parameter value
   */
  public final Object getOverrideParameterValueObject(String key) {
    ResourceParameter param = this.getModel().getParametersMap().get(key);

    Collection<ResourceParameter> parameters = getOverrideParams();
    for (Iterator<ResourceParameter> iterator = parameters.iterator(); iterator.hasNext();) {
      ResourceParameter resourceParameter = (ResourceParameter) iterator.next();
      if (resourceParameter.getName().equals(key)) {
        param = resourceParameter;
      }
    }
    if (param != null) {
      return param.getValueObject();
    }
    else {
      return null;
    }
  }

  /**
   * Method to get the public host domain
   * 
   * @param url
   *          the url to transforms
   * @return the transformed url
   */
  private String replacePublicHostName(String url) {
    String publicHostName = ((SitoolsSettings) this.getContext().getAttributes().get(ContextAttributes.SETTINGS))
        .getPublicHostDomain();
    url = url.replace(HOST_REF, publicHostName);
    return url;
  }

  /**
   * Method to put the application reference in a url
   * 
   * @param url
   *          the url to transform
   * @return the url transformed
   */
  private String replaceAppReference(String url) {
    String appRef = (String) this.getContext().getAttributes().get(ContextAttributes.APP_ATTACH_REF);
    url = url.replace(APPLICATION_REF, HOST_REF + appRef);
    url = this.replacePublicHostName(url);
    return url;
  }

  /**
   * Method to transform the url applying the resource reference URL
   * 
   * @param url
   *          the url to transform
   * @return the url transformed
   */
  private String replaceResourceReference(String url) {
    String sourceRef = (String) this.getContext().getAttributes().get(ContextAttributes.RESOURCE_ATTACHMENT);
    url = url.replace(RESOURCE_REF, APPLICATION_REF + sourceRef);
    url = this.replaceAppReference(url);
    return url;
  }

  /**
   * Method to transform the path
   * 
   * @param path
   *          a String path
   * @return the url transformed
   */
  private String replacePath(String path) {
    path = path.replace(ROOT_DIR, getSitoolsApplication().getSettings().getRootDirectory());
    path = this.replaceAppReference(path);
    return path;
  }

  /**
   * Replace variables like ${setting:xxxxxx} with the corresponding SitoolsSettings value if exist.
   * 
   * @param path
   *          property to expand
   * @return the replaced string
   */
  private String replaceSettings(String path) {
    String regexp = "\\$\\{setting:(.*)\\}";
    Pattern p = Pattern.compile(regexp);
    Matcher m = p.matcher(path);

    StringBuffer sb = new StringBuffer();
    while (m.find()) {
      String setting = path.substring(m.start() + 10, m.end() - 1);

      String replacedby = getSitoolsSetting(setting);
      if (replacedby != null) {
        m.appendReplacement(sb, replacedby);
      }
      else {
        m.appendReplacement(sb, "");
      }

    }
    m.appendTail(sb);

    return sb.toString();
  }

  /**
   * Add informations concerning user input parameters NOTE : Must be called at the end of the describe method !!
   * 
   * @param info
   *          the information to be processed
   */
  protected void addInfo(MethodInfo info) {
    info.setDocumentation("User input documentation");
    this.addStandardGetRequestInfo(info);
    if (this.model != null && this.model.getParametersMap() != null) {
      for (ResourceParameter param : this.model.getParametersMap().values()) {
        if (param.getType() == ResourceParameterType.PARAMETER_USER_INPUT) {
          ParameterInfo paramInfo = new ParameterInfo();
          paramInfo.setDefaultValue(param.getValue());
          paramInfo.setDocumentation(param.getDescription());
          paramInfo.setName(param.getName());
          paramInfo.setType(param.getValueType());
          paramInfo.setStyle(ParameterStyle.TEMPLATE);
          paramInfo.setRequired(false);
          info.getRequest().getParameters().add(paramInfo);
        }
      }
    }

  }

  /**
   * Configure the information to add a standard Sitools2 response
   * 
   * @param info
   *          the WADL method information
   */
  public void addStandardResponseInfo(MethodInfo info) {
    super.addStandardResponseInfo(info);

    ResponseInfo responseInfo503 = new ResponseInfo(
        "Request cannot be fulfilled because the the resource is not compatible the application");
    responseInfo503.getStatuses().add(Status.SERVER_ERROR_SERVICE_UNAVAILABLE);
    info.getResponses().add(responseInfo503);

  }

  /**
   * Get the specific parameterized resource parameters collection
   * 
   * @return Collection<ParameterizedResourcesParameter>
   */
  public Collection<ResourceParameter> getOverrideParams() {
    return overrideParams;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.restlet.resource.ServerResource#head(org.restlet.representation.Variant )
   */
  @Override
  protected Representation head(Variant variant) {
    Representation repr = new EmptyRepresentation();
    if (fileName != null && !"".equals(fileName)) {
      Disposition disp = new Disposition(Disposition.TYPE_ATTACHMENT);

      disp.setFilename(fileName);
      repr.setDisposition(disp);
    }
    return repr;
  }

  // @Override
  // protected void describeGet(MethodInfo info, String path) {
  // info.setDocumentation("User input documentation");
  // this.addStandardGetRequestInfo(info);
  // for (ParameterizedResourcesParameter param : this.model.getParameters()) {
  // if (param.getType() == ParameterizedResourcesParameterType.PARAMETER_USER_INPUT) {
  // ParameterInfo paramInfo = new ParameterInfo();
  // paramInfo.setDefaultValue(param.getValue());
  // paramInfo.setDocumentation(param.getDescription());
  // paramInfo.setName(param.getName());
  // paramInfo.setType("xs:string");
  // paramInfo.setStyle(ParameterStyle.TEMPLATE);
  // info.getRequest().getParameters().add(paramInfo);
  // }
  // }
  //
  // }

}

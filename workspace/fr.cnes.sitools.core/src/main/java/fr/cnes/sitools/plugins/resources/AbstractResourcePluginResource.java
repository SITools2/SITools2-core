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
package fr.cnes.sitools.plugins.resources;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

import org.restlet.data.MediaType;
import org.restlet.ext.jackson.JacksonRepresentation;
import org.restlet.ext.xstream.XstreamRepresentation;
import org.restlet.representation.ObjectRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;

import com.thoughtworks.xstream.XStream;

import fr.cnes.sitools.common.AbstractPluginResource;
import fr.cnes.sitools.common.XStreamFactory;
import fr.cnes.sitools.common.application.SitoolsApplication;
import fr.cnes.sitools.common.model.ExtensionModel;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.common.validator.ConstraintViolation;
import fr.cnes.sitools.common.validator.ConstraintViolationLevel;
import fr.cnes.sitools.notification.business.NotificationManager;
import fr.cnes.sitools.notification.model.RestletObserver;
import fr.cnes.sitools.plugins.resources.dto.ResourceModelDTO;
import fr.cnes.sitools.plugins.resources.model.ResourceModel;
import fr.cnes.sitools.plugins.resources.model.ResourceParameter;
import fr.cnes.sitools.plugins.resources.model.ResourceParameterType;
import fr.cnes.sitools.common.Consts;
import fr.cnes.sitools.util.RIAPUtils;

/**
 * Base class for resource plugin resources
 * 
 * @author m.marseille (AKKA Technologies)
 */
public class AbstractResourcePluginResource extends AbstractPluginResource {

  /** parent application */
  private SitoolsApplication application = null;

  /** Resource parent application identifier parameter */
  private String parentId = null;

  /** converterChained identifier parameter */
  private String resourcePluginId = null;

  @Override
  public void sitoolsDescribe() {
    setName("AbstractResourcePluginResource");
    setDescription("Base class for resource plugin resources");
    setNegotiated(false);
  }

  @Override
  public void doInit() {
    super.doInit();

    // // Declares the variants supported
    getVariants().add(new Variant(MediaType.APPLICATION_XML));
    getVariants().add(new Variant(MediaType.APPLICATION_JSON));
    getVariants().add(new Variant(MediaType.APPLICATION_JAVA_OBJECT));

    application = (SitoolsApplication) getApplication();

    parentId = (String) this.getRequest().getAttributes().get("parentId");

    resourcePluginId = ((String) this.getRequest().getAttributes().get("resourcePluginId"));

  }

  /**
   * Gets representation according to the specified MediaType.
   * 
   * @param response
   *          : The response to get the representation from
   * @param media
   *          : The MediaType asked
   * @return The Representation of the response with the selected mediaType
   */
  public final Representation getRepresentation(Response response, MediaType media) {
    
    if (media.isCompatible(MediaType.APPLICATION_JAVA_OBJECT)) {
      return new ObjectRepresentation<Response>(response);
    }

    XStream xstream = XStreamFactory.getInstance().getXStream(media, getContext());

    xstream.alias("resourcePlugin", ResourceModel.class);
    xstream.alias("resourceParameter", ResourceParameter.class);
    xstream.alias("response", Response.class);
    xstream.alias("item", Object.class, ResourceModel.class);
    xstream.alias("resourcePlugin", Object.class, ResourceModel.class);

    xstream.aliasField("resourcePlugin", Response.class, "item");

    xstream.omitField(Response.class, "itemName");
    xstream.omitField(Response.class, "itemClass");
    xstream.omitField(ExtensionModel.class, "parametersMap");

    xstream.setMode(XStream.NO_REFERENCES);

    XstreamRepresentation<Response> rep = new XstreamRepresentation<Response>(media, response);
    rep.setXstream(xstream);
    return rep;
  }

  /**
   * Get a FilterModelDTO from a Representation
   * 
   * @param representation
   *          the {@link Representation}
   * @return a FilterModelDTO
   * @throws IOException
   *           if there is an error while parsing the java object
   */
  public ResourceModelDTO getObject(Representation representation) throws IOException {
    ResourceModelDTO resourceInputDTO = null;
    if (representation.getMediaType().isCompatible(MediaType.APPLICATION_JAVA_OBJECT)) {
      @SuppressWarnings("unchecked")
      ObjectRepresentation<ResourceModelDTO> obj = (ObjectRepresentation<ResourceModelDTO>) representation;
      resourceInputDTO = obj.getObject();
    }
    else if (MediaType.APPLICATION_JSON.isCompatible(representation.getMediaType())) {
      // Parse the JSON representation to get the bean
      JacksonRepresentation<ResourceModelDTO> json = new JacksonRepresentation<ResourceModelDTO>(representation,
        ResourceModelDTO.class);
      resourceInputDTO = json.getObject();
    }
    return resourceInputDTO;
  }

  /**
   * Register as observer
   * 
   * @param input
   *          The ConverterChainedModel
   */
  public final void registerObserver(ResourceModel input) {
    NotificationManager notificationManager = application.getSettings().getNotificationManager();
    if (notificationManager == null) {
      getLogger().warning("NotificationManager is null");
      return;
    }

    RestletObserver observer = new RestletObserver();
    // passage RIAP
    String uriToNotify = RIAPUtils.getRiapBase() + getSitoolsSetting(Consts.APP_APPLICATIONS_URL) + "/"
      + input.getParent() + getSitoolsSetting(Consts.APP_RESOURCES_URL) + "/" + input.getId() + "/notify";
    observer.setUriToNotify(uriToNotify);
    observer.setMethodToNotify("PUT");
    observer.setUuid("ResourceModel." + input.getId());

    notificationManager.addObserver(input.getParent(), observer);

  }

  /**
   * Unregister as Observer
   * 
   * @param input
   *          ConverterChainedModel Objet
   */
  public final void unregisterObserver(ResourceModel input) {
    NotificationManager notificationManager = application.getSettings().getNotificationManager();
    if (notificationManager == null) {
      getLogger().warning("NotificationManager is null");
      return;
    }
    notificationManager.removeObserver(input.getParent(), "ResourceModel." + input.getId());
  }

  /**
   * Gets the parentId value
   * 
   * @return the parentId
   */
  public final String getParentId() {
    return parentId;
  }

  /**
   * Gets the resourcePluginId value
   * 
   * @return the resourcePluginId
   */
  public final String getResourcePluginId() {
    return resourcePluginId;
  }

  /**
   * Check the validaty of the given ConverterModel
   * 
   * @param input
   *          the ConverterModel to validate
   * @return a set of ConstraintViolation if the validation fail, null otherwise
   */
  @SuppressWarnings("unchecked")
  public final Set<ConstraintViolation> checkValidity(ResourceModel input) {
    Set<ConstraintViolation> constraints = new HashSet<ConstraintViolation>();
    try {
      // Apply the validation on the AbstractConverter
      Class<ResourceModel> classInput = (Class<ResourceModel>) Class.forName(input.getClassName());
      ResourceModel inputImpl = classInput.newInstance();
      inputImpl.setParametersMap(input.getParametersMap());

      int checkParameterAttachment = 0;
      for (ResourceParameter parameter : input.getParametersMap().values()) {
        if (parameter.getType().equals(ResourceParameterType.PARAMETER_ATTACHMENT)) {

          if (parameter.getValue() == null || parameter.getValue().equals("")) {
            ConstraintViolation constraint = new ConstraintViolation();
            constraint.setMessage("Resource attachment must be set.");
            constraint.setLevel(ConstraintViolationLevel.CRITICAL);
            constraint.setValueName(parameter.getName());
            constraints.add(constraint);
          }
          checkParameterAttachment++;
        }
      }
      if (checkParameterAttachment != 1) {
        ConstraintViolation constraint = new ConstraintViolation();
        constraint.setMessage("One resource attachment parameter is mandatory. Contact resource class developer.");
        constraint.setLevel(ConstraintViolationLevel.CRITICAL);
        constraints.add(constraint);
      }

      return super.checkValidity(inputImpl);
    }
    catch (ClassNotFoundException e) {
      getLogger().log(Level.SEVERE, "ClassNotFoundException", e);
      ConstraintViolation constraint = new ConstraintViolation();
      constraint.setMessage("ClassNotFoundException");
      constraint.setLevel(ConstraintViolationLevel.CRITICAL);
      constraints.add(constraint);
    }
    catch (InstantiationException e) {
      getLogger().log(Level.SEVERE, "InstantiationException", e);
      ConstraintViolation constraint = new ConstraintViolation();
      constraint.setMessage("InstantiationException");
      constraint.setLevel(ConstraintViolationLevel.CRITICAL);
      constraints.add(constraint);
    }
    catch (IllegalAccessException e) {
      getLogger().log(Level.SEVERE, "IllegalAccessException", e);
      ConstraintViolation constraint = new ConstraintViolation();
      constraint.setMessage("IllegalAccessException");
      constraint.setLevel(ConstraintViolationLevel.CRITICAL);
      constraints.add(constraint);
    }
    return constraints;
  }

  /**
   * Get a ResourceModelDTO from a ResourceModel
   * 
   * @param resource
   *          the ResourceModel
   * @return a ResourceModelDTO
   */
  public ResourceModelDTO getResourceModelDTO(ResourceModel resource) {
    return ResourceModelDTO.resourceModelToDTO(resource);
  }

  /**
   * Get a ResourceModel from a ResourceModelDTO
   * 
   * @param resource
   *          the ResourceModelDTO
   * @return a ResourceModel
   */
  protected ResourceModel getResourceModelFromDTO(ResourceModelDTO resource) {
    ResourceModel current = new ResourceModel();
    // common ExtensionModelDTO attributes
    current.setId(resource.getId());
    current.setName(resource.getName());
    current.setDescription(resource.getDescription());
    current.setClassAuthor(resource.getClassAuthor());
    current.setClassVersion(resource.getClassVersion());
    current.setClassName(resource.getClassName());
    current.setClassOwner(resource.getClassOwner());
    // current.setCurrentClassAuthor(resource.getCurrentClassVersion());
    // current.setCurrentClassVersion(resource.getCurrentClassVersion());
    // parametersMap
    int sequenceNumber = 1;
    for (ResourceParameter parameter : resource.getParameters()) {
      parameter.setSequence(sequenceNumber++);
    }
    current.setParametersMap(fromListToMap(resource.getParameters()));
    current.setDescriptionAction(resource.getDescriptionAction());
    // specific ResourceModelDTO attributes
    current.setApplicationClassName(resource.getApplicationClassName());
    current.setDataSetSelection(resource.getDataSetSelection());
    current.setParent(resource.getParent());
    current.setResourceClassName(resource.getResourceClassName());

    current.setDataSetSelection(resource.getDataSetSelection());
    current.setBehavior(resource.getBehavior());
    return current;

  }
  
  /**
   * getTraceParentType
   * @return a trace parent type
   */
  protected final String getTraceParentType() {
    Object obj = getContext().getAttributes().get("TRACE_PARENT_TYPE");
    if (obj != null) {
      return obj.toString();
    }
    else {
      return null;
    }
  }

}

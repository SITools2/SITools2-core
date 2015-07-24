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
package fr.cnes.sitools.plugins.filters;

import java.io.IOException;
import java.util.ArrayList;
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
import fr.cnes.sitools.common.SitoolsMediaType;
import fr.cnes.sitools.common.XStreamFactory;
import fr.cnes.sitools.common.application.SitoolsApplication;
import fr.cnes.sitools.common.model.ExtensionModel;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.common.validator.ConstraintViolation;
import fr.cnes.sitools.common.validator.ConstraintViolationLevel;
import fr.cnes.sitools.notification.business.NotificationManager;
import fr.cnes.sitools.notification.model.RestletObserver;
import fr.cnes.sitools.plugins.filters.dto.FilterModelDTO;
import fr.cnes.sitools.plugins.filters.model.FilterModel;
import fr.cnes.sitools.plugins.filters.model.FilterParameter;
import fr.cnes.sitools.common.Consts;
import fr.cnes.sitools.util.RIAPUtils;

/**
 * Base class for filter plugins resources
 * 
 * @author m.marseille (AKKA Technologies)
 */
public class AbstractFilterPluginResource extends AbstractPluginResource {

  /** parent application */
  private SitoolsApplication application = null;

  /** converterChained identifier parameter */
  private String filterPluginId = null;

  @Override
  public void sitoolsDescribe() {
    setName("AbstractFilterPluginResource");
    setDescription("Base class for filter plugins resources");
    setNegotiated(false);
  }

  @Override
  public void doInit() {
    super.doInit();

    // // Declares the variants supported
    addVariant(new Variant(MediaType.APPLICATION_XML));
    addVariant(new Variant(MediaType.APPLICATION_JSON));
    addVariant(new Variant(MediaType.APPLICATION_JAVA_OBJECT));

    application = (SitoolsApplication) getApplication();

    filterPluginId = ((String) this.getRequest().getAttributes().get("pluginId"));

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
    if (media.isCompatible(MediaType.APPLICATION_JAVA_OBJECT)
        || media.isCompatible(SitoolsMediaType.APPLICATION_JAVA_OBJECT_SITOOLS_MODEL)) {
      return new ObjectRepresentation<Response>(response);
    }

    XStream xstream = XStreamFactory.getInstance().getXStream(media, getContext());

    xstream.alias("filterPlugin", FilterModel.class);
    xstream.alias("filterParameter", FilterParameter.class);
    xstream.alias("response", Response.class);
    xstream.alias("item", Object.class, FilterModel.class);
    xstream.alias("filterPlugin", Object.class, FilterModel.class);

    xstream.aliasField("filterPlugin", Response.class, "item");

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
   */
  public FilterModelDTO getObject(Representation representation) {
    FilterModelDTO resourceInput = null;
    if (MediaType.APPLICATION_JSON.isCompatible(representation.getMediaType())) {
      // Parse the JSON representation to get the bean
      JacksonRepresentation<FilterModelDTO> json = new JacksonRepresentation<FilterModelDTO>(representation,
          FilterModelDTO.class);
      try {
        resourceInput = json.getObject();
      } catch (IOException e) {
        getContext().getLogger().severe(e.getMessage());
      }
    }
    return resourceInput;
  }

  /**
   * Register as observer
   * 
   * @param input
   *          The ConverterChainedModel
   */
  public final void registerObserver(FilterModel input) {
    NotificationManager notificationManager = application.getSettings().getNotificationManager();
    if (notificationManager == null) {
      getLogger().warning("NotificationManager is null");
      return;
    }

    RestletObserver observer = new RestletObserver();
    // passage RIAP
    String uriToNotify = RIAPUtils.getRiapBase() + getSitoolsSetting(Consts.APP_PLUGINS_FILTERS_INSTANCES_URL) + "/"
        + input.getId() + "/notify";
    observer.setUriToNotify(uriToNotify);
    observer.setMethodToNotify("PUT");
    observer.setUuid("CustomFilterModel." + input.getId());

    notificationManager.addObserver(input.getParent(), observer);

  }

  /**
   * Unregister as Observer
   * 
   * @param input
   *          ConverterChainedModel Objet
   */
  public final void unregisterObserver(FilterModel input) {
    NotificationManager notificationManager = application.getSettings().getNotificationManager();
    if (notificationManager == null) {
      getLogger().warning("NotificationManager is null");
      return;
    }
    notificationManager.removeObserver(input.getParent(), "ResourceModel." + input.getId());
  }

  /**
   * Gets the filterPluginId value
   * 
   * @return the filterPluginId
   */
  public final String getFilterPluginId() {
    return filterPluginId;
  }

  /**
   * Check the validaty of the given FilterModel
   * 
   * @param input
   *          the FilterModel to validate
   * @return a set of ConstraintViolation if the validation fail, null otherwise
   */
  @SuppressWarnings("unchecked")
  public final Set<ConstraintViolation> checkValidity(FilterModel input) {
    Set<ConstraintViolation> constraints = new HashSet<ConstraintViolation>();
    try {
      // Apply the validation on the AbstractConverter
      Class<FilterModel> classInput = (Class<FilterModel>) Class.forName(input.getClassName());
      FilterModel inputImpl = classInput.newInstance();
      inputImpl.setParametersMap(input.getParametersMap());
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
   * Get a FilterModelDTO from a FilterModel
   * 
   * @param filter
   *          the FilterModel
   * @return a FilterModelDTO
   */
  public FilterModelDTO getFilterModelDTO(FilterModel filter) {
    FilterModelDTO current = new FilterModelDTO();
    // common ExtensionModelDTO attributes
    current.setId(filter.getId());
    current.setName(filter.getName());
    current.setDescription(filter.getDescription());
    current.setClassAuthor(filter.getClassAuthor());
    current.setClassVersion(filter.getClassVersion());
    current.setClassOwner(filter.getClassOwner());
    current.setClassName(filter.getClassName());
    current.setParameters(new ArrayList<FilterParameter>(filter.getParametersMap().values()));
    current.setDescriptionAction(filter.getDescriptionAction());
    // FilterModel attributes
    current.setFilterClassName(filter.getFilterClassName());
    current.setParent(filter.getParent());

    return current;
  }

  /**
   * Get a FilterModel from a FilterModelDTO
   * 
   * @param filter
   *          the FilterModelDTO
   * @return a FilterModel
   */
  protected FilterModel getFilterModelFromDTO(FilterModelDTO filter) {
    FilterModel current = new FilterModel();
    // common ExtensionModelDTO attributes
    current.setId(filter.getId());
    current.setName(filter.getName());
    current.setDescription(filter.getDescription());
    current.setClassAuthor(filter.getClassAuthor());
    current.setClassVersion(filter.getClassVersion());
    current.setClassName(filter.getClassName());
    current.setClassOwner(filter.getClassOwner());
    // parametersMap
    current.setParametersMap(fromListToMap(filter.getParameters()));
    current.setDescriptionAction(filter.getDescriptionAction());
    // FilterModel attributes
    current.setFilterClassName(filter.getFilterClassName());
    current.setParent(filter.getParent());

    return current;
  }

}

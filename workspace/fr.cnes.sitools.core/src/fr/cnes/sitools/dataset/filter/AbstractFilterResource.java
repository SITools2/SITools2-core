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
package fr.cnes.sitools.dataset.filter;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
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
import fr.cnes.sitools.dataset.filter.business.AbstractFilter;
import fr.cnes.sitools.dataset.filter.dto.FilterChainedModelDTO;
import fr.cnes.sitools.dataset.filter.dto.FilterModelDTO;
import fr.cnes.sitools.dataset.filter.model.FilterChainedModel;
import fr.cnes.sitools.dataset.filter.model.FilterModel;
import fr.cnes.sitools.dataset.filter.model.FilterParameter;
import fr.cnes.sitools.notification.business.NotificationManager;
import fr.cnes.sitools.notification.model.RestletObserver;
import fr.cnes.sitools.server.Consts;
import fr.cnes.sitools.util.RIAPUtils;

/**
 * Abstract Resource class for Forms management
 * 
 * @author jp.boignard (AKKA Technologies)
 * 
 */
public abstract class AbstractFilterResource extends AbstractPluginResource {

  /** parent application */
  private SitoolsApplication application = null;

  /** DataSet identifier parameter */
  private String datasetId = null;

  /**
   * Default constructor
   */
  public AbstractFilterResource() {
    super();
  }

  @Override
  protected void doInit() {
    super.doInit();

    // // Declares the two variants supported
    getVariants().add(new Variant(MediaType.APPLICATION_XML));
    getVariants().add(new Variant(MediaType.APPLICATION_JSON));
    getVariants().add(new Variant(MediaType.APPLICATION_JAVA_OBJECT));

    application = (SitoolsApplication) getApplication();

    datasetId = (String) this.getRequest().getAttributes().get("datasetId");

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
  public Representation getRepresentation(Response response, MediaType media) {
    getLogger().info(media.toString());
    if (media.isCompatible(MediaType.APPLICATION_JAVA_OBJECT)
        || media.isCompatible(SitoolsMediaType.APPLICATION_JAVA_OBJECT_SITOOLS_MODEL)) {
      return new ObjectRepresentation<Response>(response);
    }

    XStream xstream = XStreamFactory.getInstance().getXStream(media, getContext());
    configure(xstream, response);
    xstream.alias("filterChainedModel", FilterChainedModel.class);
    xstream.alias("filterModel", FilterModel.class);
    xstream.alias("filterParameter", FilterParameter.class);
    xstream.omitField(ExtensionModel.class, "parametersMap");
    xstream.setMode(XStream.NO_REFERENCES);

    XstreamRepresentation<Response> rep = new XstreamRepresentation<Response>(media, response);
    rep.setXstream(xstream);
    return rep;
  }

  /**
   * Register as observer
   * 
   * @param input
   *          The FilterChainedModel
   */
  public void registerObserver(FilterChainedModel input) {
    NotificationManager notificationManager = application.getSettings().getNotificationManager();
    if (notificationManager == null) {
      getLogger().warning("NotificationManager is null");
      return;
    }

    RestletObserver observer = new RestletObserver();

    String uriToNotify = RIAPUtils.getRiapBase() + application.getSettings().getString(Consts.APP_DATASETS_URL) + "/"
        + datasetId + application.getSettings().getString(Consts.APP_DATASETS_FILTERS_URL) + "/notify";

    observer.setUriToNotify(uriToNotify);
    observer.setMethodToNotify("PUT");
    observer.setUuid("FilterChainedModel." + input.getId());

    notificationManager.addObserver(input.getParent(), observer);
  }

  /**
   * Unregister as Observer
   * 
   * @param input
   *          FilterChainedModel Object
   */
  public void unregisterObserver(FilterChainedModel input) {
    NotificationManager notificationManager = application.getSettings().getNotificationManager();
    if (notificationManager == null) {
      getLogger().warning("NotificationManager is null");
      return;
    }
    notificationManager.removeObserver(input.getParent(), "FilterChainedModel." + input.getId());
  }

  /**
   * Get a FilterModel from a FilterChainedModel
   * 
   * @param filterChainedModel
   *          the filterChainedModel
   * @param id
   *          the id of the filter
   * @return the FilterModel found
   */
  protected FilterModel getFilterModel(FilterChainedModel filterChainedModel, String id) {
    FilterModel filterRet = null;
    if (filterChainedModel != null && filterChainedModel.getFilters() != null && id != null) {
      for (Iterator<FilterModel> iterator = filterChainedModel.getFilters().iterator(); iterator.hasNext()
          && filterRet == null;) {
        FilterModel filter = iterator.next();
        if (filter.getId().equals(id)) {
          filterRet = filter;
        }
      }
    }
    return filterRet;
  }

  /**
   * Get the FilterModelDTO object from the representation sent
   * 
   * @param representation
   *          the representation sent (POST or PUT)
   * @return the corresponding FilterModelDTO
   * @throws IOException
   *           if there is an error while getting the FilterModelDTO
   */
  protected FilterModelDTO getObject(Representation representation) throws IOException {
    FilterModelDTO filterInputDTO = null;
    if (MediaType.APPLICATION_XML.isCompatible(representation.getMediaType())) {
      // Parse the XML representation to get the osearch bean
      filterInputDTO = new XstreamRepresentation<FilterModelDTO>(representation).getObject();
    }
    else if (MediaType.APPLICATION_JSON.isCompatible(representation.getMediaType())) {
      // Parse the JSON representation to get the bean
      filterInputDTO = new JacksonRepresentation<FilterModelDTO>(representation, FilterModelDTO.class).getObject();
    }
    else if (representation.getMediaType().isCompatible(MediaType.APPLICATION_JAVA_OBJECT)) {
      @SuppressWarnings("unchecked")
      ObjectRepresentation<FilterModelDTO> obj = (ObjectRepresentation<FilterModelDTO>) representation;
      filterInputDTO = obj.getObject();
    }
    return filterInputDTO;
  }

  /**
   * Get the identifier of the dataset
   * 
   * @return the identifier
   */
  public final String getDatasetId() {
    return this.datasetId;
  }

  // /**
  // * Add current Class description for comparison with stored application descriptions
  // *
  // * @param filtChListModel
  // * the app plugins
  // */
  // private void addCurrentClassDescription(List<FilterChainedModelDTO> filtChListModel) {
  // if (filtChListModel.size() > 0) {
  // for (FilterChainedModelDTO app : filtChListModel) {
  // addCurrentClassDescription(app);
  // }
  // }
  //
  // }

  /**
   * Add the current class descriptions of the filter in the chained model
   * 
   * @param filChModel
   *          the chained model
   */
  protected void addCurrentClassDescription(FilterChainedModelDTO filChModel) {
    if (filChModel != null && filChModel.getFilters() != null && filChModel.getFilters().size() > 0) {
      for (FilterModelDTO filt : filChModel.getFilters()) {
        try {
          @SuppressWarnings("unchecked")
          Class<AbstractFilter> filterClass = (Class<AbstractFilter>) Class.forName(filt.getClassName());
          Constructor<AbstractFilter> filterConstructor = filterClass.getDeclaredConstructor();
          AbstractFilter object = filterConstructor.newInstance();
          filt.setCurrentClassAuthor(object.getClassAuthor());
          filt.setCurrentClassVersion(object.getClassVersion());
        }
        catch (ClassNotFoundException e) {
          filt.setCurrentClassAuthor("CLASS_NOT_FOUND");
          filt.setCurrentClassVersion("CLASS_NOT_FOUND");
          getLogger().severe(e.getMessage());
        }
        catch (SecurityException e) {
          getLogger().severe(e.getMessage());
        }
        catch (NoSuchMethodException e) {
          getLogger().severe(e.getMessage());
        }
        catch (IllegalArgumentException e) {
          getLogger().severe(e.getMessage());
        }
        catch (InstantiationException e) {
          getLogger().severe(e.getMessage());
        }
        catch (IllegalAccessException e) {
          getLogger().severe(e.getMessage());
        }
        catch (InvocationTargetException e) {
          getLogger().severe(e.getMessage());
        }
      }
    }

  }

  /**
   * Check the validaty of the given FilterModel
   * 
   * @param input
   *          the FilterModel to validate
   * @return a set of ConstraintViolation if the validation fail, null otherwise
   */
  @SuppressWarnings("unchecked")
  public Set<ConstraintViolation> checkValidity(FilterModel input) {
    Set<ConstraintViolation> constraints = new HashSet<ConstraintViolation>();
    try {
      // Apply the validation on the AbstractFilter
      Class<AbstractFilter> classInput = (Class<AbstractFilter>) Class.forName(input.getClassName());
      AbstractFilter inputImpl = classInput.newInstance();
      inputImpl.setParametersMap(input.getParametersMap());
      inputImpl.setContext(getContext());
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
   * Configure the XStream
   * 
   * @param xstream
   *          the XStream to treat
   * @param response
   *          the response used
   */
  public void configure(XStream xstream, Response response) {
    super.configure(xstream, response);
    xstream.alias("constraintViolation", Object.class, ConstraintViolation.class);
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
    current.setClassName(filter.getClassName());
    current.setClassOwner(filter.getClassOwner());
    // current.setCurrentClassAuthor(filter.getCurrentClassVersion());
    // current.setCurrentClassVersion(filter.getCurrentClassVersion());
    current.setParameters(new ArrayList<FilterParameter>(filter.getParametersMap().values()));
    current.setDescriptionAction(filter.getDescriptionAction());

    // FilterModel attributes
    current.setStatus(filter.getStatus());

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
    // current.setCurrentClassAuthor(filter.getCurrentClassVersion());
    // current.setCurrentClassVersion(filter.getCurrentClassVersion());
    // parametersMap
    current.setParametersMap(fromListToMap(filter.getParameters()));
    current.setDescriptionAction(filter.getDescriptionAction());
    // FilterModel attributes
    current.setStatus(filter.getStatus());

    return current;

  }

  /**
   * Get a {@link FilterChainedModelDTO} from a {@link FilterChainedModel}
   * 
   * @param filterChModel
   *          the {@link FilterChainedModel}
   * @return a {@link FilterChainedModelDTO}
   */
  protected FilterChainedModelDTO getFilterChainedModelDTO(FilterChainedModel filterChModel) {
    if (filterChModel == null) {
      return null;
    }
    FilterChainedModelDTO filterChainedDTO = new FilterChainedModelDTO();
    filterChainedDTO.setDescription(filterChModel.getDescription());
    filterChainedDTO.setId(filterChModel.getId());
    filterChainedDTO.setName(filterChModel.getName());
    filterChainedDTO.setParent(filterChModel.getParent());
    for (FilterModel converter : filterChModel.getFilters()) {
      filterChainedDTO.getFilters().add(getFilterModelDTO(converter));
    }
    return filterChainedDTO;
  }

  /**
   * Get a FilterModelDTO from a FilterModel
   * 
   * @param filter
   *          the FilterModel
   * @return a FilterModelDTO
   */
  public FilterModelDTO getFilterModelDTO(AbstractFilter filter) {
    FilterModelDTO current = new FilterModelDTO();
    // common ExtensionModelDTO attributes
    current.setId(filter.getId());
    current.setName(filter.getName());
    current.setDescription(filter.getDescription());
    current.setClassAuthor(filter.getClassAuthor());
    current.setClassVersion(filter.getClassVersion());
    current.setClassName(filter.getClassName());
    current.setClassOwner(filter.getClassOwner());
    // current.setCurrentClassAuthor(filter.getCurrentClassVersion());
    // current.setCurrentClassVersion(filter.getCurrentClassVersion());
    current.setParameters(new ArrayList<FilterParameter>(filter.getParametersMap().values()));
    current.setDescriptionAction(filter.getDescriptionAction());
    return current;
  }

}

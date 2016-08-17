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
package fr.cnes.sitools.dataset.converter;

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
import fr.cnes.sitools.dataset.converter.business.AbstractConverter;
import fr.cnes.sitools.dataset.converter.dto.ConverterChainedModelDTO;
import fr.cnes.sitools.dataset.converter.dto.ConverterModelDTO;
import fr.cnes.sitools.dataset.converter.model.ConverterChainedModel;
import fr.cnes.sitools.dataset.converter.model.ConverterModel;
import fr.cnes.sitools.dataset.converter.model.ConverterParameter;
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
public abstract class AbstractConverterResource extends AbstractPluginResource {

  /** parent application */
  private SitoolsApplication application = null;

  /** DataSet identifier parameter */
  private String datasetId = null;

  @Override
  public void doInit() {
    super.doInit();

    // // Declares the variants supported
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
  public final Representation getRepresentation(Response response, MediaType media) {
    
    if (media.isCompatible(MediaType.APPLICATION_JAVA_OBJECT)
        || media.isCompatible(SitoolsMediaType.APPLICATION_JAVA_OBJECT_SITOOLS_MODEL)) {
      return new ObjectRepresentation<Response>(response);
    }

    XStream xstream = XStreamFactory.getInstance().getXStream(media, getContext());
    xstream.alias("converterChainedModel", ConverterChainedModel.class);
    xstream.alias("converterModel", ConverterModel.class);
    xstream.alias("converterParameter", ConverterParameter.class);
    xstream.omitField(ExtensionModel.class, "parametersMap");
    xstream.setMode(XStream.NO_REFERENCES);
    this.configure(xstream, response);

    XstreamRepresentation<Response> rep = new XstreamRepresentation<Response>(media, response);
    rep.setXstream(xstream);
    return rep;
  }

  /**
   * Register as observer
   * 
   * @param input
   *          The ConverterChainedModel
   */
  public final void registerObserver(ConverterChainedModel input) {
    NotificationManager notificationManager = application.getSettings().getNotificationManager();
    if (notificationManager == null) {
      getLogger().warning("NotificationManager is null");
      return;
    }

    RestletObserver observer = new RestletObserver();

    String uriToNotify = RIAPUtils.getRiapBase() + application.getSettings().getString(Consts.APP_DATASETS_URL) + "/"
        + datasetId + application.getSettings().getString(Consts.APP_DATASETS_CONVERTERS_URL) + "/notify";
    observer.setUriToNotify(uriToNotify);
    observer.setMethodToNotify("PUT");
    observer.setUuid("ConverterChainedModel." + input.getId());

    notificationManager.addObserver(input.getParent(), observer);

  }

  /**
   * Unregister as Observer
   * 
   * @param input
   *          ConverterChainedModel Objet
   */
  public final void unregisterObserver(ConverterChainedModel input) {
    NotificationManager notificationManager = application.getSettings().getNotificationManager();
    if (notificationManager == null) {
      getLogger().warning("NotificationManager is null");
      return;
    }
    notificationManager.removeObserver(input.getParent(), "ConverterChainedModel." + input.getId());
  }

  /**
   * Add the current class descriptions of the converter
   * 
   * @param conv
   *          the converterModel
   */
  protected void addCurrentClassDescriptions(ConverterModelDTO conv) {
    if (conv != null) {
      try {
        @SuppressWarnings("unchecked")
        Class<AbstractConverter> convClass = (Class<AbstractConverter>) Class.forName(conv.getClassName());
        Constructor<AbstractConverter> convConstructor = convClass.getDeclaredConstructor();
        AbstractConverter object = convConstructor.newInstance();
        conv.setCurrentClassAuthor(object.getClassAuthor());
        conv.setCurrentClassVersion(object.getClassVersion());
      }
      catch (ClassNotFoundException e) {
        conv.setCurrentClassAuthor("CLASS_NOT_FOUND");
        conv.setCurrentClassVersion("CLASS_NOT_FOUND");
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

  /**
   * Add the current class descriptions of the converters in the chained model
   * 
   * @param convChModel
   *          the chained model
   */
  public final void addCurrentClassDescriptions(ConverterChainedModelDTO convChModel) {
    if (convChModel != null && convChModel.getConverters() != null && convChModel.getConverters().size() > 0) {
      for (ConverterModelDTO conv : convChModel.getConverters()) {
        addCurrentClassDescriptions(conv);
      }
    }

  }

  /**
   * Get a converterModel from a converterChainedModel
   * 
   * @param convChainedModel
   *          the converterChainedModel
   * @param id
   *          the id of the converter
   * @return the converterModel
   */
  public final ConverterModel getConverterModel(ConverterChainedModel convChainedModel, String id) {
    ConverterModel convRet = null;
    if (convChainedModel != null && convChainedModel.getConverters() != null && id != null) {
      for (Iterator<ConverterModel> iterator = convChainedModel.getConverters().iterator(); iterator.hasNext()
          && convRet == null;) {
        ConverterModel conv = iterator.next();
        if (conv.getId().equals(id)) {
          convRet = conv;
        }
      }
    }
    return convRet;
  }

  /**
   * Get the ConverterModelDTO object from the representation sent
   * 
   * @param representation
   *          the representation sent (POST or PUT)
   * @return the corresponding ConverterModelDTO
   */
  protected ConverterModelDTO getObject(Representation representation) {
    ConverterModelDTO converterInputDTO = null;
    if (MediaType.APPLICATION_XML.isCompatible(representation.getMediaType())) {
      // Parse the XML representation to get the osearch bean
      converterInputDTO = new XstreamRepresentation<ConverterModelDTO>(representation).getObject();
    }
    else if (MediaType.APPLICATION_JSON.isCompatible(representation.getMediaType())) {
      // Parse the JSON representation to get the bean
      converterInputDTO = new JacksonRepresentation<ConverterModelDTO>(representation, ConverterModelDTO.class)
          .getObject();
    }
    return converterInputDTO;
  }

  /**
   * Get the identifier of the dataset
   * 
   * @return the identifier
   */
  public final String getDatasetId() {
    return this.datasetId;
  }

  /**
   * Check the validaty of the given ConverterModel
   * 
   * @param input
   *          the ConverterModel to validate
   * @return a set of ConstraintViolation if the validation fail, null otherwise
   */
  @SuppressWarnings("unchecked")
  public final Set<ConstraintViolation> checkValidity(ConverterModel input) {
    Set<ConstraintViolation> constraints = new HashSet<ConstraintViolation>();
    try {
      // Apply the validation on the AbstractConverter
      Class<AbstractConverter> classInput = (Class<AbstractConverter>) Class.forName(input.getClassName());
      AbstractConverter inputImpl = classInput.newInstance();
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
  public final void configure(XStream xstream, Response response) {
    super.configure(xstream, response);
    xstream.alias("constraintViolation", Object.class, ConstraintViolation.class);
  }

  /**
   * Get a ConverterModelDTO from a ConverterModel
   * 
   * @param converter
   *          the ConverterModel
   * @return a ConverterModelDTO
   */
  public ConverterModelDTO getConverterModelDTO(ConverterModel converter) {
    ConverterModelDTO current = new ConverterModelDTO();
    // common ExtensionModelDTO attributes
    current.setId(converter.getId());
    current.setName(converter.getName());
    current.setDescription(converter.getDescription());
    current.setClassAuthor(converter.getClassAuthor());
    current.setClassVersion(converter.getClassVersion());
    current.setClassName(converter.getClassName());
    current.setClassOwner(converter.getClassOwner());
    // current.setCurrentClassAuthor(converter.getCurrentClassVersion());
    // current.setCurrentClassVersion(converter.getCurrentClassVersion());
    current.setParameters(new ArrayList<ConverterParameter>(converter.getParametersMap().values()));
    current.setDescriptionAction(converter.getDescriptionAction());
    // ConverterModel attributes
    current.setStatus(converter.getStatus());

    return current;
  }

  /**
   * Get a ConverterModel from a ConverterModelDTO
   * 
   * @param converter
   *          the ConverterModelDTO
   * @return a ConverterModel
   */
  protected ConverterModel getConverterModelFromDTO(ConverterModelDTO converter) {
    ConverterModel current = new ConverterModel();
    // common ExtensionModelDTO attributes
    current.setId(converter.getId());
    current.setName(converter.getName());
    current.setDescription(converter.getDescription());
    current.setClassAuthor(converter.getClassAuthor());
    current.setClassVersion(converter.getClassVersion());
    current.setClassName(converter.getClassName());
    current.setClassOwner(converter.getClassOwner());
    // current.setCurrentClassAuthor(converter.getCurrentClassVersion());
    // current.setCurrentClassVersion(converter.getCurrentClassVersion());
    // parametersMap
    current.setParametersMap(fromListToMap(converter.getParameters()));
    current.setDescriptionAction(converter.getDescriptionAction());
    // ConverterModel attributes
    current.setStatus(converter.getStatus());

    return current;
  }

  /**
   * Get a {@link ConverterChainedModelDTO} from a {@link ConverterChainedModel}
   * 
   * @param convChModel
   *          the {@link ConverterChainedModel}
   * @return a {@link ConverterChainedModelDTO}
   */
  protected ConverterChainedModelDTO getConverterChainedModelDTO(ConverterChainedModel convChModel) {
    if (convChModel == null) {
      return null;
    }
    ConverterChainedModelDTO convChainedDTO = new ConverterChainedModelDTO();
    convChainedDTO.setDescription(convChModel.getDescription());
    convChainedDTO.setId(convChModel.getId());
    convChainedDTO.setName(convChModel.getName());
    convChainedDTO.setParent(convChModel.getParent());
    for (ConverterModel converter : convChModel.getConverters()) {
      convChainedDTO.getConverters().add(getConverterModelDTO(converter));
    }
    return convChainedDTO;
  }

  /**
   * Get a ConverterModelDTO from a ConverterModel
   * 
   * @param converter
   *          the ConverterModel
   * @return a ConverterModelDTO
   */
  protected ConverterModelDTO getConverterModelDTO(AbstractConverter converter) {
    ConverterModelDTO current = new ConverterModelDTO();
    // common ExtensionModelDTO attributes
    current.setId(converter.getId());
    current.setName(converter.getName());
    current.setDescription(converter.getDescription());
    current.setClassAuthor(converter.getClassAuthor());
    current.setClassVersion(converter.getClassVersion());
    current.setClassName(converter.getClassName());
    current.setClassOwner(converter.getClassOwner());
    // current.setCurrentClassAuthor(converter.getCurrentClassVersion());
    // current.setCurrentClassVersion(converter.getCurrentClassVersion());
    current.setParameters(new ArrayList<ConverterParameter>(converter.getParametersMap().values()));
    current.setDescriptionAction(converter.getDescriptionAction());
    return current;
  }

}

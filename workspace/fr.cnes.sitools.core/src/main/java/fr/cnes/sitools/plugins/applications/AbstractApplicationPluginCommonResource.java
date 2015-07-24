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
package fr.cnes.sitools.plugins.applications;

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
import fr.cnes.sitools.common.XStreamFactory;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.common.validator.ConstraintViolation;
import fr.cnes.sitools.common.validator.ConstraintViolationLevel;
import fr.cnes.sitools.plugins.applications.business.AbstractApplicationPlugin;
import fr.cnes.sitools.plugins.applications.dto.ApplicationPluginModelDTO;
import fr.cnes.sitools.plugins.applications.model.ApplicationPluginModel;
import fr.cnes.sitools.plugins.applications.model.ApplicationPluginParameter;

/**
 * SVA model resource for SVA application
 * 
 * @author m.marseille (AKKA Technologies) (AKKA)
 * 
 */
public abstract class AbstractApplicationPluginCommonResource extends AbstractPluginResource {

  @Override
  protected void doInit() {
    super.doInit();

    // Declares the two variants supported
    addVariant(new Variant(MediaType.APPLICATION_XML));
    addVariant(new Variant(MediaType.APPLICATION_JSON));

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
    
    if (media.isCompatible(MediaType.APPLICATION_JAVA_OBJECT)) {
      return new ObjectRepresentation<Response>(response);
    }

    XStream xstream = XStreamFactory.getInstance().getXStream(media, getContext());
    configure(xstream, response);
    xstream.alias("ApplicationPluginModel", ApplicationPluginModel.class);
    xstream.alias("parameters", ApplicationPluginParameter.class);

    XstreamRepresentation<Response> rep = new XstreamRepresentation<Response>(media, response);
    rep.setXstream(xstream);
    return rep;
  }

  /**
   * Get the SVA object from the representation sent
   * 
   * @param representation
   *          the representation sent (POST or PUT)
   * @return the corresponding SVA model
   */
  protected ApplicationPluginModelDTO getObject(Representation representation) {
    ApplicationPluginModelDTO appModel = null;
    if (MediaType.APPLICATION_JSON.isCompatible(representation.getMediaType())) {
      // Parse the JSON representation to get the bean
      try {
        appModel = new JacksonRepresentation<ApplicationPluginModelDTO>(representation, ApplicationPluginModelDTO.class).getObject();
      } catch (IOException e) {
        getContext().getLogger().severe(e.getMessage());
      }
    }
    return appModel;
  }

  /**
   * Check the validaty of the given SvaModel
   * 
   * @param input
   *          the SvaModel to validate
   * @return a set of ConstraintViolation if the validation fail, null otherwise
   */
  @SuppressWarnings("unchecked")
  public Set<ConstraintViolation> checkValidity(ApplicationPluginModel input) {
    Set<ConstraintViolation> constraints = new HashSet<ConstraintViolation>();
    try {
      // Apply the validation on the AbstractSva
      Class<AbstractApplicationPlugin> classInput = (Class<AbstractApplicationPlugin>) Class.forName(input
          .getClassName());
      AbstractApplicationPlugin inputImpl = classInput.newInstance();
      inputImpl.getModel().setParametersMap(input.getParametersMap());
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
   * Get a ApplicationPluginModelDTO from a ApplicationPluginModel
   * 
   * @param application
   *          the ApplicationPluginModel
   * @return a ApplicationPluginModelDTO
   */
  public ApplicationPluginModelDTO getApplicationModelDTO(ApplicationPluginModel application) {
    ApplicationPluginModelDTO current = new ApplicationPluginModelDTO();
    // common ExtensionModelDTO attributes
    current.setId(application.getId());
    current.setName(application.getName());
    current.setDescription(application.getDescription());
    current.setClassAuthor(application.getClassAuthor());
    current.setClassVersion(application.getClassVersion());
    current.setClassName(application.getClassName());
    current.setClassOwner(application.getClassOwner());
    current.setParameters(new ArrayList<ApplicationPluginParameter>(application.getParametersMap().values()));
    current.setDescriptionAction(application.getDescriptionAction());
    // ApplicationPluginModel attributes
    current.setCategory(application.getCategory());
    current.setLabel(application.getLabel());
    current.setUrlAttach(application.getUrlAttach());
    current.setStatus(application.getStatus());

    return current;
  }

  /**
   * Get a ApplicationPluginModel from a ApplicationPluginModelDTO
   * 
   * @param application
   *          the ApplicationPluginModelDTO
   * @return a ApplicationModel
   */
  protected ApplicationPluginModel getApplicationModelFromDTO(ApplicationPluginModelDTO application) {
    ApplicationPluginModel current = new ApplicationPluginModel();
    // common ExtensionModelDTO attributes
    current.setId(application.getId());
    current.setName(application.getName());
    current.setDescription(application.getDescription());
    current.setClassAuthor(application.getClassAuthor());
    current.setClassVersion(application.getClassVersion());
    current.setClassName(application.getClassName());
    current.setClassOwner(application.getClassOwner());
    // parametersMap
    current.setParametersMap(fromListToMap(application.getParameters()));
    current.setDescriptionAction(application.getDescriptionAction());
    // ApplicationPluginModel attributes
    current.setCategory(application.getCategory());
    current.setLabel(application.getLabel());
    current.setUrlAttach(application.getUrlAttach());
    current.setStatus(application.getStatus());

    return current;

  }

}

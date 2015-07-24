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
package fr.cnes.sitools.form.project;

import org.restlet.data.MediaType;
import org.restlet.ext.jackson.JacksonRepresentation;
import org.restlet.ext.xstream.XstreamRepresentation;
import org.restlet.representation.ObjectRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;

import com.thoughtworks.xstream.XStream;

import fr.cnes.sitools.common.SitoolsResource;
import fr.cnes.sitools.common.XStreamFactory;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.form.project.dto.FormProjectAdminDTO;
import fr.cnes.sitools.form.project.model.FormProject;

import java.io.IOException;

    /**
 * Base class for resource of management of form components
 * 
 * @author jp.boignard (AKKA Technologies)
 * 
 */
public abstract class AbstractFormProjectResource extends SitoolsResource {
  /** The name of the collection parameter */
  protected String collectionParamName = "collection";
  /** The name of the dictionary parameter */
  protected String dictionaryParamName = "dictionary";
  /** The name of the nbDatasetsMax parameter (only for search service) */
  protected String nbDatasetsMaxParamName = "nbDatasetsMax";

  /** parent application */
  private FormProjectApplication application = null;

  /** store */
  private FormProjectStoreInterface store = null;

  /** The projectId parameter */
  private String projectId = null;

  /** The id of the formProject */
  private String formProjectId = null;

  @Override
  public final void doInit() {
    super.doInit();

    // Declares the two variants supported
    addVariant(new Variant(MediaType.APPLICATION_XML));
    addVariant(new Variant(MediaType.APPLICATION_JSON));
    addVariant(new Variant(MediaType.APPLICATION_JAVA_OBJECT));

    application = (FormProjectApplication) getApplication();
    setStore(application.getStore());

    projectId = (String) this.getRequest().getAttributes().get("projectId");
    setFormProjectId((String) this.getRequest().getAttributes().get("formProjectId"));

  }

  /**
   * Response to Representation
   * 
   * @param response
   *          the server response
   * @param media
   *          the media used
   * @return Representation
   */
  public final Representation getRepresentation(Response response, MediaType media) {
    if (media.isCompatible(MediaType.APPLICATION_JAVA_OBJECT)) {
      return new ObjectRepresentation<Response>(response);
    }

    XStream xstream = XStreamFactory.getInstance().getXStream(media, getContext());
    configure(xstream, response);
    xstream.alias("formComponent", FormProject.class);
    xstream.setMode(XStream.NO_REFERENCES);

    XstreamRepresentation<Response> rep = new XstreamRepresentation<Response>(media, response);
    rep.setXstream(xstream);
    return rep;
  }

  /**
   * Get the object from representation
   * 
   * @param representation
   *          the representation used
   * @param variant
   *          the variant used
   * @return FormProject
   */
  public final FormProject getObject(Representation representation, Variant variant) {
    
    FormProject formProjectInput = null;
    
    FormProjectAdminDTO formProjectDTOInput = null;
    
    if (MediaType.APPLICATION_JSON.isCompatible(representation.getMediaType())) {
      // Parse the JSON representation to get the bean
      //formComponentInput = new JacksonRepresentation<FormProject>(representation, FormProject.class).getObject();
      try {
        formProjectDTOInput = new JacksonRepresentation<FormProjectAdminDTO>(representation, FormProjectAdminDTO.class).getObject();
      } catch (IOException e) {
        getContext().getLogger().severe(e.getMessage());
      }
    }
    
    formProjectInput = FormProjectAdminDTO.dtoToFormProject(formProjectDTOInput);

    return formProjectInput;
  }

  /**
   * Gets the projectId value
   * 
   * @return the projectId
   */
  public String getProjectId() {
    return projectId;
  }

  /**
   * Sets the value of projectId
   * 
   * @param projectId
   *          the projectId to set
   */
  public void setProjectId(String projectId) {
    this.projectId = projectId;
  }

  /**
   * Sets the value of store
   * 
   * @param store
   *          the store to set
   */
  public final void setStore(FormProjectStoreInterface store) {
    this.store = store;
  }

  /**
   * Gets the store value
   * 
   * @return the store
   */
  public final FormProjectStoreInterface getStore() {
    return store;
  }

  /**
   * Sets the value of formProjectId
   * 
   * @param formProjectId
   *          the formProjectId to set
   */
  public void setFormProjectId(String formProjectId) {
    this.formProjectId = formProjectId;
  }

  /**
   * Gets the formProjectId value
   * 
   * @return the formProjectId
   */
  public String getFormProjectId() {
    return formProjectId;
  }

}

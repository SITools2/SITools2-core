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
package fr.cnes.sitools.form.components;

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
import fr.cnes.sitools.common.store.SitoolsStore;
import fr.cnes.sitools.form.components.model.FormComponent;

/**
 * Base class for resource of management of form components
 * 
 * @author jp.boignard (AKKA Technologies)
 * 
 */
public abstract class AbstractFormComponentsResource extends SitoolsResource {

  /** parent application */
  private FormComponentsApplication application = null;
  
  /** store */
  private SitoolsStore<FormComponent> store = null;
  
  /** FormComponent identifier parameter */
  private String formComponentId = null;

  @Override
  public final void doInit() {
    super.doInit();
    
    // Declares the two variants supported
    getVariants().add(new Variant(MediaType.APPLICATION_XML));
    getVariants().add(new Variant(MediaType.APPLICATION_JSON));
    getVariants().add(new Variant(MediaType.APPLICATION_JAVA_OBJECT));

    application = (FormComponentsApplication) getApplication();
    setStore(application.getStore());

    setFormComponentId((String) this.getRequest().getAttributes().get("formComponentId"));
  }

  /**
   * Response to Representation
   * 
   * @param response the server response
   * @param media the media used
   * @return Representation
   */
  public final Representation getRepresentation(Response response, MediaType media) {
    getLogger().info(media.toString());
    if (media.isCompatible(MediaType.APPLICATION_JAVA_OBJECT)) {
      return new ObjectRepresentation<Response>(response);
    }

    XStream xstream = XStreamFactory.getInstance().getXStream(media, getContext());
    configure(xstream, response);
    xstream.alias("formComponent", FormComponent.class);

    XstreamRepresentation<Response> rep = new XstreamRepresentation<Response>(media, response);
    rep.setXstream(xstream);
    return rep;
  }

  /**
   * Get the object from representation
   * @param representation the representation used
   * @param variant the variant used
   * @return FormComponent
   */
  public final FormComponent getObject(Representation representation, Variant variant) {
    FormComponent formComponentInput = null;
    if (MediaType.APPLICATION_XML.isCompatible(representation.getMediaType())) {
      // Parse the XML representation to get the FormComponent bean
      XstreamRepresentation<FormComponent> repXML = new XstreamRepresentation<FormComponent>(representation);
      XStream xstream = XStreamFactory.getInstance().getXStreamReader(MediaType.APPLICATION_XML);
      xstream.autodetectAnnotations(false);
      xstream.alias("formComponent", FormComponent.class);
      repXML.setXstream(xstream);
      formComponentInput = repXML.getObject();
    }
    else if (MediaType.APPLICATION_JSON.isCompatible(representation.getMediaType())) {
      // Parse the JSON representation to get the bean
      formComponentInput = new JacksonRepresentation<FormComponent>(representation, FormComponent.class).getObject();
    }
    return formComponentInput;
  }

  /**
   * Sets the value of formComponentId
   * @param formComponentId the formComponentId to set
   */
  public final void setFormComponentId(String formComponentId) {
    this.formComponentId = formComponentId;
  }

  /**
   * Gets the formComponentId value
   * @return the formComponentId
   */
  public final String getFormComponentId() {
    return formComponentId;
  }

  /**
   * Sets the value of store
   * @param store the store to set
   */
  public final void setStore(SitoolsStore<FormComponent> store) {
    this.store = store;
  }

  /**
   * Gets the store value
   * @return the store
   */
  public final SitoolsStore<FormComponent> getStore() {
    return store;
  }

}

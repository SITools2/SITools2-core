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
package fr.cnes.sitools.form.dataset;

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
import fr.cnes.sitools.dataset.model.DataSet;
import fr.cnes.sitools.form.dataset.dto.FormDTO;
import fr.cnes.sitools.form.dataset.dto.ParameterDTO;
import fr.cnes.sitools.form.dataset.dto.ValueDTO;
import fr.cnes.sitools.form.dataset.dto.ZoneDTO;
import fr.cnes.sitools.form.dataset.model.Form;
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
public abstract class AbstractFormResource extends SitoolsResource {

  /** store */
  private SitoolsStore<Form> store = null;

  /** form identifier parameter */
  private String formId = null;

  /** filter with datasetId if present */
  private String datasetId = null;
  
  /** parent application */
  private FormApplication application = null;

  /**
   * Default constructor
   */
  public AbstractFormResource() {
    super();
  }

  @Override
  protected void doInit() {
    super.doInit();

    // Declares the two variants supported
    getVariants().add(new Variant(MediaType.APPLICATION_XML));
    getVariants().add(new Variant(MediaType.APPLICATION_JSON));
    getVariants().add(new Variant(MediaType.APPLICATION_JAVA_OBJECT));

    application = (FormApplication) getApplication();
    store = application.getStore();

    formId = (String) this.getRequest().getAttributes().get("formId");
    datasetId = (String) this.getRequest().getAttributes().get("datasetId");
  }

  /**
   * Response to Representation
   * 
   * @param response
   *          the response to treat
   * @param media
   *          the media to use
   * @return Representation
   */
  public final Representation getRepresentation(Response response, MediaType media) {
    
    if (media.isCompatible(MediaType.APPLICATION_JAVA_OBJECT)) {
      return new ObjectRepresentation<Response>(response);
    }

    XStream xstream = XStreamFactory.getInstance().getXStream(media, getContext());
    if (response == null) {
      xstream.alias("form", FormDTO.class);
      xstream.aliasField("parameters", FormDTO.class, "parameters");
      xstream.aliasField("zones", FormDTO.class, "zones");
      
      xstream.aliasField("params", ZoneDTO.class, "params");
      xstream.alias("zone", ZoneDTO.class);
      
      xstream.aliasField("values", ParameterDTO.class, "values");

      xstream.alias("parameters", ParameterDTO.class);
      xstream.alias("values", ValueDTO.class);
      
      xstream.alias("param", ParameterDTO.class);
    }
    else {
      configure(xstream, response);

      if ((response.getItem() instanceof FormDTO)) {
        xstream.alias("form", FormDTO.class);

        xstream.aliasField("parameters", FormDTO.class, "parameters");
        xstream.aliasField("zones", FormDTO.class, "zones");
        
        xstream.aliasField("values", ParameterDTO.class, "values");
        
        xstream.aliasField("params", ZoneDTO.class, "params");
        
        xstream.alias("zone", ZoneDTO.class);
        
        xstream.alias("parameters", ParameterDTO.class);
        xstream.alias("values", ValueDTO.class);
        xstream.alias("param", ParameterDTO.class);

      }
      else if (response.getItem() instanceof Form) {
        xstream.alias("form", Form.class);
      }

    }

    XstreamRepresentation<Response> rep = new XstreamRepresentation<Response>(media, response);
    rep.setXstream(xstream);
    return rep;
  }

  /**
   * Get the object from the representation
   * 
   * @param representation
   *          the representation to use
   * @param variant
   *          the variant to use
   * @return a project
   */
  public final FormDTO getObject(Representation representation, Variant variant) {
    FormDTO formDTOInput = null;
    if (MediaType.APPLICATION_XML.isCompatible(representation.getMediaType())) {
      // Parse the XML representation to get the bean
      formDTOInput = new XstreamRepresentation<FormDTO>(representation).getObject();
    }
    else if (MediaType.APPLICATION_JSON.isCompatible(representation.getMediaType())) {
      formDTOInput = new JacksonRepresentation<FormDTO>(representation, FormDTO.class).getObject();
    }
    return formDTOInput;
  }

  /**
   * Register as observer
   * 
   * @param input
   *          The Form object
   */
  public final void registerObserver(Form input) {
    NotificationManager notificationManager = application.getSettings().getNotificationManager();
    if (notificationManager == null) {
      getLogger().warning("NotificationManager is null");
      return;
    }

    RestletObserver observer = new RestletObserver();
    // passage en RIAP
    String uriToNotify = RIAPUtils.getRiapBase() + getSitoolsSetting(Consts.APP_DATASETS_URL) + "/"
        + input.getParent() + getSitoolsSetting(Consts.APP_FORMS_URL) + "/" + input.getId() + "/notify";

    observer.setUriToNotify(uriToNotify);
    observer.setMethodToNotify("PUT");
    observer.setUuid(input.getId());

    notificationManager.addObserver(input.getParent(), observer);

  }

  /**
   * Unregister as Observer
   * 
   * @param input
   *          Form Object
   */
  public final void unregisterObserver(Form input) {
    NotificationManager notificationManager = application.getSettings().getNotificationManager();
    if (notificationManager == null) {
      getLogger().warning("NotificationManager is null");
      return;
    }
    notificationManager.removeObserver(input.getParent(), input.getId());
  }

  /**
   * Gets the application value
   * 
   * @return the application
   */
  public final FormApplication getFormApplication() {
    return application;
  }

  /**
   * Gets the store value
   * 
   * @return the store
   */
  public final SitoolsStore<Form> getStore() {
    return store;
  }

  /**
   * Gets the formId value
   * 
   * @return the formId
   */
  public final String getFormId() {
    return formId;
  }

  /**
   * Gets the datasetId value
   * 
   * @return the datasetId
   */
  public final String getDatasetId() {
    return datasetId;
  }

  /**
   * Get the dataset object with the given id
   * 
   * @param id
   *          the id of the dataset
   * @return a dataset object corresponding to the given id
   */
  public final DataSet getDataset(String id) {
    return RIAPUtils.getObject(id, getSitoolsSetting(Consts.APP_DATASETS_URL), getContext());
  }

}

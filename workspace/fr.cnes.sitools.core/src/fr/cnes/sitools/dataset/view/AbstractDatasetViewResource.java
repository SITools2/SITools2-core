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
package fr.cnes.sitools.dataset.view;

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
import fr.cnes.sitools.dataset.view.model.DatasetView;

/**
 * Base class for resource of management of DatasetView
 * 
 * @author jp.boignard (AKKA Technologies)
 * 
 */
public abstract class AbstractDatasetViewResource extends SitoolsResource {

  /** parent application */
  private DatasetViewApplication application = null;
  
  /** store */
  private SitoolsStore<DatasetView> store = null;
  
  /** DatasetView identifier parameter */
  private String datasetViewId = null;

  @Override
  public final void doInit() {
    super.doInit();
    
    // Declares the two variants supported
    getVariants().add(new Variant(MediaType.APPLICATION_XML));
    getVariants().add(new Variant(MediaType.APPLICATION_JSON));
    getVariants().add(new Variant(MediaType.APPLICATION_JAVA_OBJECT));

    application = (DatasetViewApplication) getApplication();
    setStore(application.getStore());

    setDatasetViewId((String) this.getRequest().getAttributes().get("datasetViewId"));
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
    xstream.alias("datasetView", DatasetView.class);

    XstreamRepresentation<Response> rep = new XstreamRepresentation<Response>(media, response);
    rep.setXstream(xstream);
    return rep;
  }

  /**
   * Get the object from representation
   * @param representation the representation used
   * @param variant the variant used
   * @return DatasetView
   */
  public final DatasetView getObject(Representation representation, Variant variant) {
    DatasetView datasetViewInput = null;
    if (MediaType.APPLICATION_XML.isCompatible(representation.getMediaType())) {
      // Parse the XML representation to get the DatasetView bean
      XstreamRepresentation<DatasetView> repXML = new XstreamRepresentation<DatasetView>(representation);
      XStream xstream = XStreamFactory.getInstance().getXStreamReader(MediaType.APPLICATION_XML);
      xstream.autodetectAnnotations(false);
      xstream.alias("datasetView", DatasetView.class);
      repXML.setXstream(xstream);
      datasetViewInput = repXML.getObject();
    }
    else if (MediaType.APPLICATION_JSON.isCompatible(representation.getMediaType())) {
      // Parse the JSON representation to get the bean
      datasetViewInput = new JacksonRepresentation<DatasetView>(representation, DatasetView.class).getObject();
    }
    return datasetViewInput;
  }

  /**
   * Sets the value of datasetViewId
   * @param datasetViewId the datasetViewId to set
   */
  public final void setDatasetViewId(String datasetViewId) {
    this.datasetViewId = datasetViewId;
  }

  /**
   * Gets the datasetViewId value
   * @return the datasetViewId
   */
  public final String getDatasetViewId() {
    return datasetViewId;
  }

  /**
   * Sets the value of store
   * @param store the store to set
   */
  public final void setStore(SitoolsStore<DatasetView> store) {
    this.store = store;
  }

  /**
   * Gets the store value
   * @return the store
   */
  public final SitoolsStore<DatasetView> getStore() {
    return store;
  }

}

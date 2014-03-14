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
package fr.cnes.sitools.collections;

import org.restlet.data.MediaType;
import org.restlet.ext.jackson.JacksonRepresentation;
import org.restlet.ext.xstream.XstreamRepresentation;
import org.restlet.representation.ObjectRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;

import com.thoughtworks.xstream.XStream;

import fr.cnes.sitools.collections.model.Collection;
import fr.cnes.sitools.common.SitoolsResource;
import fr.cnes.sitools.common.SitoolsSettings;
import fr.cnes.sitools.common.XStreamFactory;
import fr.cnes.sitools.common.application.ContextAttributes;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.common.store.SitoolsStore;
import fr.cnes.sitools.dataset.model.DataSet;
import fr.cnes.sitools.server.Consts;
import fr.cnes.sitools.util.RIAPUtils;

/**
 * Base class for resource of management of collections
 * 
 * @author jp.boignard (AKKA Technologies)
 * 
 */
public abstract class AbstractCollectionsResource extends SitoolsResource {

  /** parent application */
  private CollectionsApplication application = null;

  /** store */
  private SitoolsStore<Collection> store = null;

  /** Collection identifier parameter */
  private String collectionId = null;

  @Override
  public void doInit() {
    super.doInit();

    // Declares the two variants supported
    getVariants().add(new Variant(MediaType.APPLICATION_XML));
    getVariants().add(new Variant(MediaType.APPLICATION_JSON));
    getVariants().add(new Variant(MediaType.APPLICATION_JAVA_OBJECT));

    application = (CollectionsApplication) getApplication();
    setStore(application.getStore());

    setCollectionId((String) this.getRequest().getAttributes().get("collectionId"));
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
    getLogger().info(media.toString());
    if (media.isCompatible(MediaType.APPLICATION_JAVA_OBJECT)) {
      return new ObjectRepresentation<Response>(response);
    }

    XStream xstream = XStreamFactory.getInstance().getXStream(media, getContext());
    configure(xstream, response);

    XstreamRepresentation<Response> rep = new XstreamRepresentation<Response>(media, response);
    rep.setXstream(xstream);
    return rep;
  }

  /**
   * Get the object collection representation
   * 
   * @param representation
   *          the representation used
   * @param variant
   *          the variant used
   * @return Collection
   */
  public final Collection getObject(Representation representation, Variant variant) {
    Collection collectionInput = null;
    if (MediaType.APPLICATION_XML.isCompatible(representation.getMediaType())) {
      // Parse the XML representation to get the Collection bean
      XstreamRepresentation<Collection> repXML = new XstreamRepresentation<Collection>(representation);
      XStream xstream = XStreamFactory.getInstance().getXStreamReader(MediaType.APPLICATION_XML);
      xstream.autodetectAnnotations(false);
      repXML.setXstream(xstream);
      collectionInput = repXML.getObject();
    }
    else if (MediaType.APPLICATION_JSON.isCompatible(representation.getMediaType())) {
      // Parse the JSON representation to get the bean
      collectionInput = new JacksonRepresentation<Collection>(representation, Collection.class).getObject();
    }
    return collectionInput;
  }

  /**
   * Sets the value of collectionId
   * 
   * @param collectionId
   *          the collectionId to set
   */
  public final void setCollectionId(String collectionId) {
    this.collectionId = collectionId;
  }

  /**
   * Gets the collectionId value
   * 
   * @return the collectionId
   */
  public final String getCollectionId() {
    return collectionId;
  }

  /**
   * Sets the value of store
   * 
   * @param store
   *          the store to set
   */
  public final void setStore(SitoolsStore<Collection> store) {
    this.store = store;
  }

  /**
   * Gets the store value
   * 
   * @return the store
   */
  public final SitoolsStore<Collection> getStore() {
    return store;
  }

  /**
   * Get DataSet object from its id
   * 
   * @param id
   *          the id of the DataSet
   * @return a DataSet object
   */
  public DataSet getDataset(String id) {
    SitoolsSettings settings = (SitoolsSettings) getContext().getAttributes().get(ContextAttributes.SETTINGS);
    return RIAPUtils.getObject(id, settings.getString(Consts.APP_DATASETS_URL), getContext());
  }

}

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
package fr.cnes.sitools.dataset;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.restlet.data.MediaType;
import org.restlet.ext.jackson.JacksonRepresentation;
import org.restlet.ext.xstream.XstreamRepresentation;
import org.restlet.representation.ObjectRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;

import com.thoughtworks.xstream.XStream;

import fr.cnes.sitools.common.SitoolsCommonDateConverter;
import fr.cnes.sitools.common.SitoolsResource;
import fr.cnes.sitools.common.XStreamFactory;
import fr.cnes.sitools.common.model.ExtensionModel;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.dataset.model.Column;
import fr.cnes.sitools.dataset.model.DataSet;
import fr.cnes.sitools.dataset.model.DictionaryMapping;
import fr.cnes.sitools.datasource.jdbc.model.Structure;
import fr.cnes.sitools.notification.business.NotificationManager;
import fr.cnes.sitools.notification.model.RestletObserver;
import fr.cnes.sitools.project.ProjectStoreInterface;
import fr.cnes.sitools.project.model.Project;
import fr.cnes.sitools.server.Consts;
import fr.cnes.sitools.util.RIAPUtils;

/**
 * Base class for DataSet management resources
 * 
 * @author jp.boignard (AKKA Technologies)
 */
public abstract class AbstractDataSetResource extends SitoolsResource {

  /** Parent Application */
  protected volatile AbstractDataSetApplication application = null;

  /** Store */
  protected volatile DataSetStoreInterface store = null;

  /** DataSet identifier parameter */
  protected volatile String datasetId = null;

  @Override
  protected void doInit() {
    super.doInit();

    // Declares the two variants supported
    getVariants().add(new Variant(MediaType.APPLICATION_XML));
    getVariants().add(new Variant(MediaType.APPLICATION_JSON));
    getVariants().add(new Variant(MediaType.TEXT_CSV));

    application = (AbstractDataSetApplication) getApplication();
    store = application.getStore();

    datasetId = (String) this.getRequest().getAttributes().get("datasetId");
  }

  /**
   * Encode a response into a Representation according to the given media type.
   * 
   * @param response
   *          Response
   * @param media
   *          Response
   * @return Representation
   */
  public Representation getRepresentation(Response response, MediaType media) {
    if (media.isCompatible(MediaType.APPLICATION_JAVA_OBJECT)) {
      return new ObjectRepresentation<Response>(response);
    }

    XStream xstream = XStreamFactory.getInstance().getXStream(media, getContext());
    configure(xstream, response);
    xstream.alias("dataset", DataSet.class);
    xstream.alias("column", Column.class);
    xstream.alias("structure", Structure.class);

    xstream.omitField(ExtensionModel.class, "parametersMap");
    xstream.registerConverter(new SitoolsCommonDateConverter());

    XstreamRepresentation<Response> rep = new XstreamRepresentation<Response>(media, response);
    rep.setXstream(xstream);
    return rep;
  }

  /**
   * Gets DataSet object from Representation
   * 
   * @param representation
   *          of a DataSet
   * @return DataSet
   * @throws IOException
   *           if there is an error while deserializing Java Object
   */
  public final DataSet getObject(Representation representation) throws IOException {
    DataSet object = null;
    if (representation.getMediaType().isCompatible(MediaType.APPLICATION_JAVA_OBJECT)) {
      @SuppressWarnings("unchecked")
      ObjectRepresentation<DataSet> obj = (ObjectRepresentation<DataSet>) representation;
      object = obj.getObject();
    }
    else if (MediaType.APPLICATION_XML.isCompatible(representation.getMediaType())) {
      // Parse the XML representation to get the dataset bean
      object = new XstreamRepresentation<DataSet>(representation).getObject();

    }
    else if (MediaType.APPLICATION_JSON.isCompatible(representation.getMediaType())) {
      // Parse the JSON representation to get the bean
      object = new JacksonRepresentation<DataSet>(representation, DataSet.class).getObject();
    }

    return object;
  }

  /**
   * Register an observer
   * 
   * @param input
   *          the project as input
   */
  public final void registerObserver(DataSet input) {
    NotificationManager notificationManager = application.getSettings().getNotificationManager();
    if (notificationManager == null) {
      getLogger().severe("NotificationManager is null");
      return;
    }

    List<DictionaryMapping> mappings = input.getDictionaryMappings();

    if (mappings != null) {
      RestletObserver observer = new RestletObserver();
      String uriToNotify = RIAPUtils.getRiapBase() + application.getSettings().getString(Consts.APP_DATASETS_URL) + "/"
          + input.getId() + "/notify";

      observer.setUriToNotify(uriToNotify);
      observer.setMethodToNotify("PUT");
      observer.setUuid(input.getId());

      for (DictionaryMapping mapping : mappings) {
        notificationManager.addObserver(mapping.getDictionaryId(), observer);
      }
    }
  }

  /**
   * Unregister an observer
   * 
   * @param input
   *          the project to unregister
   */
  public final void unregisterObserver(DataSet input) {
    NotificationManager notificationManager = application.getSettings().getNotificationManager();
    if (notificationManager == null) {
      getLogger().severe("NotificationManager is null");
      return;
    }

    List<DictionaryMapping> list = input.getDictionaryMappings();
    if (list != null) {
      for (DictionaryMapping mapping : list) {
        notificationManager.removeObserver(mapping.getDictionaryId(), input.getId());
      }
    }
  }

  /**
   * Gets the datasetId value
   * 
   * @return the datasetId
   */
  public String getDatasetId() {
    return datasetId;
  }
  
  /**
   * Get the store associated to the application
   * 
   * @return the store available for this resource
   */
  public final DataSetStoreInterface getStore() {
    return this.store;
  }
  
  /**
   * Check that the dataset name and the sitoolsUserAttachment are unique over the dataset collection includeDatasetInput is
   * used to specify whether or not to include datasetInput in the verification process ( for example when updating a
   * project )
   * 
   * @param datasetInput
   *          the dataset to check with the collection
   * @param includeDatasetInput
   *          true to include the dataset in the verification, false otherwise
   * @return a Response with the error message if the checking fail, null otherwise
   */
  public final Response checkUnicity(DataSet datasetInput, boolean includeDatasetInput) {
    Response response = null;
    List<DataSet> storedDatasets = getStore().getList();
    List<String> storedDatasetNames = new ArrayList<String>();
    List<String> storedDatasetUrlAttach = new ArrayList<String>();

    if (datasetInput.getName() == null || "".equals(datasetInput.getName())) {
      response = new Response(false, datasetInput, DataSet.class, "dataset");
      response.setMessage("dataset.name.mandatory");
      return response;
    }
    
    if (storedDatasets != null) {
      for (DataSet dataset : storedDatasets) {
        if (includeDatasetInput || (!includeDatasetInput && !datasetInput.getId().equals(dataset.getId()))) {
          storedDatasetNames.add(dataset.getName());
        }
        
        // if the attachment is empty it will be filled latter by a unique attachment
        // empty attachment must not be checked for unicity
        if (!dataset.getSitoolsAttachementForUsers().equals("")) {
          storedDatasetUrlAttach.add(dataset.getSitoolsAttachementForUsers());
        }
      }
      
      if (storedDatasetNames.contains(datasetInput.getName())) {
        response = new Response(false, datasetInput, DataSet.class, "dataset");
        response.setMessage("dataset.name.already.assigned");
        return response;
      }
      
      if (storedDatasetUrlAttach.contains(datasetInput.getSitoolsAttachementForUsers())) {
        response = new Response(false, datasetInput, DataSet.class, "dataset");
        response.setMessage("dataset.attachment.already.assigned");
        return response;
      }
    }
    return response;
  }

}

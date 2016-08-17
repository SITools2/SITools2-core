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
package fr.cnes.sitools.form.project.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.restlet.Request;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Preference;
import org.restlet.data.Status;
import org.restlet.ext.wadl.MethodInfo;
import org.restlet.representation.ObjectRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Get;
import org.restlet.resource.ResourceException;

import fr.cnes.sitools.collections.model.Collection;
import fr.cnes.sitools.common.SitoolsSettings;
import fr.cnes.sitools.common.application.ContextAttributes;
import fr.cnes.sitools.common.model.Resource;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.common.resource.SitoolsParameterizedResource;
import fr.cnes.sitools.server.Consts;
import fr.cnes.sitools.util.RIAPUtils;

/**
 * Multidataset properties search service
 * <p>
 * Calls the properties search on each dataset and return all the datasets corresponding to the properties
 * </p>
 * 
 * 
 * @author m.gond
 */
public class ServicePropertiesSearchResource extends SitoolsParameterizedResource {
  /** The identifier of the collection */
  private String collectionId = null;

  /**
   * Resource description
   */
  @Override
  public void sitoolsDescribe() {
    setName("ServicePropertiesSearchResource");
    setDescription("Get the list of dataset which corresponds to some properties");
  }

  @Override
  public void doInit() {
    super.doInit();
    collectionId = getOverrideParameterValue("collection");

  }

  /**
   * Process the properties search on each dataset of the collection
   * 
   * @param variant
   *          the Variant needed
   * @return a Representation in the given Variant of all the datasets corresponding to the properties
   */
  @Get
  public Representation processSearch(Variant variant) {
    String parameters = getRequest().getResourceRef().getQuery();

    List<Resource> datasetsOut = new ArrayList<Resource>();

    Collection collection = getCollection(collectionId);
    if (collection != null) {
      List<Resource> datasets = collection.getDataSets();
      for (Resource dataset : datasets) {
        if (isDatasetCandidate(dataset.getUrl() + "/checkProperties" + "?" + parameters)) {
          datasetsOut.add(dataset);
        }
      }
    }

    Collection collectionOut = new Collection();
    collectionOut.setDataSets(datasetsOut);

    Response response = new Response(true, collectionOut, Collection.class, "collection");
    return getRepresentation(response, variant);
  }

  /**
   * Get a Collection Object from its id
   * 
   * @param id
   *          the Collection identifier
   * @return the Collection object corresponding to the identifier or null if the Collection is not found
   */
  private Collection getCollection(String id) {
    SitoolsSettings settings = (SitoolsSettings) getContext().getAttributes().get(ContextAttributes.SETTINGS);
    return RIAPUtils.getObject(id, settings.getString(Consts.APP_COLLECTIONS_URL), getContext());
  }

  @Override
  public final void describeGet(MethodInfo info) {
    info.setDocumentation("Method to retrieve the list of dataset from a collection which corresponds to the given keywords");
    this.addStandardGetRequestInfo(info);
    this.addStandardObjectResponseInfo(info);
  }

  /**
   * Check if the dataset corresponds to the given properties (properties are included in the url)
   * 
   * @param url
   *          the url to call
   * @return true if the dataset corresponds to the properties, false otherwise
   */
  private boolean isDatasetCandidate(String url) {
    Request reqGET = new Request(Method.GET, RIAPUtils.getRiapBase() + url);
    ArrayList<Preference<MediaType>> objectMediaType = new ArrayList<Preference<MediaType>>();
    objectMediaType.add(new Preference<MediaType>(MediaType.APPLICATION_JAVA_OBJECT));
    reqGET.getClientInfo().setAcceptedMediaTypes(objectMediaType);
    org.restlet.Response response = null;

    response = getContext().getClientDispatcher().handle(reqGET);

    if (response == null || Status.isError(response.getStatus().getCode())) {
      RIAPUtils.exhaust(response);
      return false;
    }

    @SuppressWarnings("unchecked")
    ObjectRepresentation<Response> or = (ObjectRepresentation<Response>) response.getEntity();
    try {
      Response resp = or.getObject();
      return (resp.getSuccess() && resp.getItem() == null);
    }
    catch (IOException e) { // marshalling error
      throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
    }
  }

}

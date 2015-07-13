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
package fr.cnes.sitools.dataset.opensearch;

import java.util.logging.Level;

import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.ext.jackson.JacksonRepresentation;
import org.restlet.ext.xstream.XstreamRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ResourceException;

import fr.cnes.sitools.common.model.ResourceCollectionFilter;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.dataset.opensearch.model.Opensearch;

/**
 * Class Resource for managing Project Collection (GET, POST)
 * 
 * @author jp.boignard (AKKA Technologies)
 * 
 */
public class OpenSearchCollectionResource extends AbstractSearchResource {

  @Override
  public void sitoolsDescribe() {
    setName("OpensearchCollectionResource");
    setDescription("Resource for managing osearch collection");
  }

  /**
   * Update / Validate existing OpenSearch
   * 
   * @param representation
   *          OpenSearch configuration
   * @param variant
   *          client preferred media type
   * @return Representation
   */
  @Post
  public Representation newOpensearch(Representation representation, Variant variant) {
    if (representation == null) {
      throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "OPENSEARCH_REPRESENTATION_REQUIRED");
    }
    try {
      Opensearch osearchInput = null;
      if (MediaType.APPLICATION_XML.isCompatible(representation.getMediaType())) {
        // Parse the XML representation to get the bean
        osearchInput = new XstreamRepresentation<Opensearch>(representation).getObject();

      }
      else if (MediaType.APPLICATION_JSON.isCompatible(representation.getMediaType())) {
        // Parse the JSON representation to get the bean
        osearchInput = new JacksonRepresentation<Opensearch>(representation, Opensearch.class).getObject();
      }

      // Business service
      osearchInput.setStatus("INACTIVE");
      Opensearch osearchOutput = getStore().create(osearchInput);

      // Response
      Response response = new Response(true, osearchOutput, Opensearch.class, "opensearch");
      return getRepresentation(response, variant);

    }
    catch (ResourceException e) {
      getLogger().log(Level.INFO, null, e);
      throw e;
    }
    catch (Exception e) {
      getLogger().log(Level.SEVERE, null, e);
      throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
    }
  }

  /**
   * get all OpenSearch configuration
   * 
   * @param variant
   *          client preferred media type
   * @return Representation
   */
  @Get
  public Representation retrieve(Variant variant) {
    try {

      if (getDatasetId() != null) {
        Response response = null;
        Opensearch osearch = getStore().retrieve(getDatasetId());
        if ((getDatasetId() != null) && (!getDatasetId().equals(osearch.getParent()))) {
          response = new Response(false, "OPENSEARCH_NOT_BELONGS_TO_DATASET");
        }
        else {
          response = new Response(true, osearch, Opensearch.class, "opensearch");
        }

        return getRepresentation(response, variant);

      }
      else {
        ResourceCollectionFilter filter = new ResourceCollectionFilter(this.getRequest());
        if (getDatasetId() != null) {
          filter.setParent(getDatasetId());
        }
        Opensearch[] osearchs = getStore().getArray(filter);
        Response response = new Response(true, osearchs);
        return getRepresentation(response, variant);
      }
    }
    catch (ResourceException e) {
      getLogger().log(Level.INFO, null, e);
      throw e;
    }
    catch (Exception e) {
      getLogger().log(Level.SEVERE, null, e);
      throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
    }
  }

}

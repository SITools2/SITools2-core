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
package fr.cnes.sitools.collections;

import java.util.List;
import java.util.logging.Level;

import org.restlet.data.Status;
import org.restlet.ext.wadl.MethodInfo;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ResourceException;

import fr.cnes.sitools.collections.model.Collection;
import fr.cnes.sitools.common.model.ResourceCollectionFilter;
import fr.cnes.sitools.common.model.Response;

/**
 * Class Resource for managing Collection Collection (GET, POST)
 * 
 * @author jp.boignard (AKKA Technologies)
 * 
 */
public class CollectionsCollectionResource extends AbstractCollectionsResource {

  @Override
  public void sitoolsDescribe() {
    setName("CollectionsCollectionResource");
    setDescription("Resource for managing collections collection");
    setNegotiated(false);
  }

  /**
   * Update / Validate existing collection
   * 
   * @param representation
   *          Collection representation
   * @param variant
   *          client preferred media type
   * @return Representation
   */
  @Post
  public Representation newCollection(Representation representation, Variant variant) {
    if (representation == null) {
      trace(Level.INFO, "Cannot create new collection");
      throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "PROJECT_REPRESENTATION_REQUIRED");
    }
    try {
      // Parse object representation
      Collection collectionInput = getObject(representation, variant);

      // Business service
      Collection collectionOutput = getStore().create(collectionInput);

      trace(Level.INFO, "Create new collection " + collectionOutput.getName());
      // Response
      Response response = new Response(true, collectionOutput, Collection.class, "collection");
      return getRepresentation(response, variant);

    }
    catch (ResourceException e) {
      trace(Level.INFO, "Cannot create new collection");
      getLogger().log(Level.INFO, null, e);
      throw e;
    }
    catch (Exception e) {
      trace(Level.INFO, "Cannot create new collection");
      getLogger().log(Level.WARNING, null, e);
      throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
    }
  }

  @Override
  public final void describePost(MethodInfo info) {
    info.setDocumentation("Method to create a new form components sending its representation.");
    this.addStandardPostOrPutRequestInfo(info);
    this.addStandardResponseInfo(info);
    this.addStandardInternalServerErrorInfo(info);
  }

  /**
   * get all collections
   * 
   * @param variant
   *          client preferred media type
   * @return Representation
   */
  @Get
  public Representation retrieveCollection(Variant variant) {
    try {

      if (getCollectionId() != null) {
        Collection collection = getStore().retrieve(getCollectionId());
        Response response;
        if (collection != null) {
          trace(Level.FINE, "Edit collection information for the collection " + collection.getName());
          response = new Response(true, collection, Collection.class, "collection");
        }
        else {
          trace(Level.INFO, "Cannot edit collection information for the collection - id: " + getCollectionId());
          response = new Response(false, "collection.not.found");
        }
        return getRepresentation(response, variant);
      }
      else {
        ResourceCollectionFilter filter = new ResourceCollectionFilter(this.getRequest());
        List<Collection> collections = getStore().getList(filter);
        int total = collections.size();
        collections = getStore().getPage(filter, collections);
        trace(Level.FINE, "View available collections");
        Response response = new Response(true, collections, Collection.class, "collections");
        response.setTotal(total);
        return getRepresentation(response, variant);
      }
    }
    catch (ResourceException e) {
      trace(Level.INFO, "Cannot view available collections");
      getLogger().log(Level.INFO, null, e);
      throw e;
    }
    catch (Exception e) {
      trace(Level.INFO, "Cannot view available collections");
      getLogger().log(Level.WARNING, null, e);
      throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
    }
  }

  @Override
  public final void describeGet(MethodInfo info) {
    info.setDocumentation("Method to retrieve the list of form components available on the server.");
    this.addStandardGetRequestInfo(info);
    this.addStandardResponseInfo(info);
    this.addStandardInternalServerErrorInfo(info);
  }

}

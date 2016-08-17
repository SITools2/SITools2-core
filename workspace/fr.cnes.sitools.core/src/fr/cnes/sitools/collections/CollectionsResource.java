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
import org.restlet.ext.wadl.ParameterInfo;
import org.restlet.ext.wadl.ParameterStyle;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.Put;
import org.restlet.resource.ResourceException;

import fr.cnes.sitools.collections.model.Collection;
import fr.cnes.sitools.common.model.ResourceCollectionFilter;
import fr.cnes.sitools.common.model.Response;

/**
 * Class Resource for managing single Collection (GET UPDATE DELETE)
 * 
 * @author jp.boignard (AKKA Technologies)
 * 
 */
public class CollectionsResource extends AbstractCollectionsResource {

  @Override
  public void sitoolsDescribe() {
    setName("CollectionResource");
    setDescription("Resource for managing an identified collection");
    setNegotiated(false);
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
    info.setDocumentation("Method to retrieve a single form component by ID");
    this.addStandardGetRequestInfo(info);
    ParameterInfo param = new ParameterInfo("collectionId", true, "class", ParameterStyle.TEMPLATE,
        "Form component identifier");
    info.getRequest().getParameters().add(param);
    this.addStandardObjectResponseInfo(info);
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
  @Put
  public Representation updateCollection(Representation representation, Variant variant) {
    Collection collectionOutput = null;
    try {

      Collection collectionInput = null;
      if (representation != null) {
        // Parse object representation
        collectionInput = getObject(representation, variant);

        // Business service
        collectionOutput = getStore().update(collectionInput);
      }

      Response response;
      if (collectionOutput != null) {
        trace(Level.INFO, "Update collection " + collectionOutput.getName());
        response = new Response(true, collectionOutput, Collection.class, "collection");
      }
      else {
        trace(Level.INFO, "Cannot update collection - id: " + getCollectionId());
        response = new Response(false, "collection.update.failure");
      }
      return getRepresentation(response, variant);

    }
    catch (ResourceException e) {
      trace(Level.INFO, "Cannot update collection - id: " + getCollectionId());
      getLogger().log(Level.INFO, null, e);
      throw e;
    }
    catch (Exception e) {
      trace(Level.INFO, "Cannot update collection - id: " + getCollectionId());
      getLogger().log(Level.WARNING, null, e);
      throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
    }
  }

  @Override
  public final void describePut(MethodInfo info) {
    info.setDocumentation("Method to modify a single collection sending its new representation");
    this.addStandardPostOrPutRequestInfo(info);
    ParameterInfo param = new ParameterInfo("collectionId", true, "class", ParameterStyle.TEMPLATE,
        "Collection identifier");
    info.getRequest().getParameters().add(param);
    this.addStandardObjectResponseInfo(info);
    this.addStandardInternalServerErrorInfo(info);
  }

  /**
   * Delete collection
   * 
   * @param variant
   *          client preferred media type
   * @return Representation
   */
  @Delete
  public Representation deleteCollection(Variant variant) {
    try {
      Collection collection = getStore().retrieve(getCollectionId());
      Response response;
      if (collection != null) {
        // Business service
        getStore().delete(getCollectionId());

        trace(Level.INFO, "Delete collection " + collection.getName());
        // Response
        response = new Response(true, "collection.delete.success");

      }
      else {
        trace(Level.INFO, "Cannot delete collection " + getCollectionId());
        response = new Response(true, "collection.delete.failure");

      }
      return getRepresentation(response, variant);

    }
    catch (ResourceException e) {
      trace(Level.INFO, "Cannot delete collection " + getCollectionId());
      getLogger().log(Level.INFO, null, e);
      throw e;
    }
    catch (Exception e) {
      trace(Level.INFO, "Cannot delete collection " + getCollectionId());
      getLogger().log(Level.WARNING, null, e);
      throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
    }
  }

  @Override
  public final void describeDelete(MethodInfo info) {
    info.setDocumentation("Method to delete a single form component by ID");
    this.addStandardGetRequestInfo(info);
    ParameterInfo param = new ParameterInfo("collectionId", true, "class", ParameterStyle.TEMPLATE,
        "Form component identifier");
    info.getRequest().getParameters().add(param);
    this.addStandardSimpleResponseInfo(info);
    this.addStandardInternalServerErrorInfo(info);
  }

}

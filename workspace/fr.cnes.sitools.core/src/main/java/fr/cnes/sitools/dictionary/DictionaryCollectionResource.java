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
package fr.cnes.sitools.dictionary;

import java.util.List;
import java.util.logging.Level;

import org.restlet.data.Status;
import org.restlet.ext.wadl.MethodInfo;
import org.restlet.ext.wadl.ResponseInfo;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ResourceException;

import fr.cnes.sitools.common.model.ResourceCollectionFilter;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.dictionary.model.Dictionary;
import fr.cnes.sitools.notification.model.Notification;

/**
 * Class for dictionary collection management
 * 
 * @author jp.boignard (AKKA Technologies)
 * 
 */
public final class DictionaryCollectionResource extends AbstractDictionaryResource {

  @Override
  public void sitoolsDescribe() {
    setName("DictionaryCollectionResource");
    setDescription("Resource for managing a dictionary collection");
    this.setNegotiated(false);
  }

  /**
   * Create a new dictionary
   * 
   * @param representation
   *          input
   * @param variant
   *          client preferred media type
   * @return Representation
   */
  @Post
  public Representation newDictionary(Representation representation, Variant variant) {
    if (representation == null) {
      throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "DICTIONARY_REPRESENTATION_REQUIRED");
    }
    try {
      // Parse object representation
      Dictionary dictionaryInput = getObject(representation, variant);

      // Business service
      Dictionary dictionaryOutput = getStore().create(dictionaryInput);

      // Notify observers
      Notification notification = new Notification();
      notification.setObservable(dictionaryOutput.getId());
      notification.setEvent("DICTIONARY_CREATED");
      notification.setMessage("New dictionary created.");
      getResponse().getAttributes().put(Notification.ATTRIBUTE, notification);

      trace(Level.INFO, "Create the dictionary " + dictionaryOutput.getName());
      // Response
      Response response = new Response(true, dictionaryOutput, Dictionary.class, "dictionary");
      return getRepresentation(response, variant);
    }
    catch (ResourceException e) {
      trace(Level.INFO, "Cannot create the dictionary");
      getLogger().log(Level.INFO, null, e);
      throw e;
    }
    catch (Exception e) {
      trace(Level.INFO, "Cannot create the dictionary");
      getLogger().log(Level.WARNING, null, e);
      throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
    }
  }

  @Override
  protected void describePost(MethodInfo info) {

    // -> Global method info
    info.setIdentifier("create_dictionary");
    info.setDocumentation("To create a new dictionary.");

    // -> Response info

    // Requests
    this.addStandardPostOrPutRequestInfo(info);

    // Successful responses
    this.addStandardResponseInfo(info);

    // Failures
    ResponseInfo responseInfo = new ResponseInfo();
    responseInfo.getStatuses().add(Status.CLIENT_ERROR_BAD_REQUEST);
    responseInfo.setDocumentation("Dictionary representation required");
    info.getResponses().add(responseInfo);

    responseInfo = new ResponseInfo();
    responseInfo.getStatuses().add(Status.SERVER_ERROR_INTERNAL);
    responseInfo.setDocumentation("Server internal error occurred");
    info.getResponses().add(responseInfo);

  }

  /**
   * get all dictionary
   * 
   * @param variant
   *          client preferred media type
   * @return Representation
   */
  @Get
  public Representation retrieveDictionary(Variant variant) {
    try {
      if (getDictionaryId() != null) {
        Dictionary dictionary = getStore().retrieve(getDictionaryId());
        Response response;
        if (dictionary != null) {
          trace(Level.FINE, "Edit information for the dictionary " + dictionary.getName());
          response = new Response(true, dictionary, Dictionary.class, "dictionary");
        }
        else {
          trace(Level.FINE, "Edit information for the dictionary " + getDictionaryId());
          response = new Response(false, "cannot find dictionary");
        }
        return getRepresentation(response, variant);
      }
      else {
        ResourceCollectionFilter filter = new ResourceCollectionFilter(this.getRequest());
        List<Dictionary> dictionaries = getStore().getList(filter);
        int total = dictionaries.size();
        dictionaries = getStore().getPage(filter, dictionaries);
        trace(Level.FINE, "View available dictionaries");
        Response response = new Response(true, dictionaries, Dictionary.class, "dictionaries");
        response.setTotal(total);
        return getRepresentation(response, variant);
      }
    }
    catch (ResourceException e) {
      trace(Level.INFO, "Cannot view available dictionaries");
      getLogger().log(Level.INFO, null, e);
      throw e;
    }
    catch (Exception e) {
      trace(Level.INFO, "Cannot view available dictionaries");
      getLogger().log(Level.WARNING, null, e);
      throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
    }
  }

  @Override
  protected void describeGet(MethodInfo info) {

    // -> Global method info
    info.setIdentifier("get_dictionaries");
    info.setDocumentation("Get the list of dictionaries.");

    this.addStandardGetRequestInfo(info);
    this.addStandardResponseInfo(info);
    this.addStandardInternalServerErrorInfo(info);
    this.addStandardResourceCollectionFilterInfo(info);

  }

}

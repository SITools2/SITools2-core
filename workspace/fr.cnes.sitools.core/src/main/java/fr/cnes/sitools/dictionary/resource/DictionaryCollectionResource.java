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
package fr.cnes.sitools.dictionary.resource;

import java.util.List;
import java.util.logging.Level;

import fr.cnes.sitools.common.resource.ListCollection;
import org.restlet.data.Status;
import org.restlet.ext.wadl.MethodInfo;
import org.restlet.ext.wadl.ResponseInfo;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ResourceException;

import fr.cnes.sitools.common.model.ResourceCollectionFilter;
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
   */
  @Post
  public Dictionary newDictionary(Dictionary dictionary) throws ResourceException {
    if (dictionary == null) {
      throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "DICTIONARY_REPRESENTATION_REQUIRED");
    }
    try {
      // Business service
      Dictionary dictionaryOutput = getStore().create(dictionary);

      // Notify observers
      Notification notification = new Notification();
      notification.setObservable(dictionaryOutput.getId());
      notification.setEvent("DICTIONARY_CREATED");
      notification.setMessage("New dictionary created.");
      getResponse().getAttributes().put(Notification.ATTRIBUTE, notification);

      trace(Level.INFO, "Create the dictionary " + dictionaryOutput.getName());

      // Response
      return dictionaryOutput;
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
   */
  @Get
  public ListCollection<Dictionary> retrieveDictionary() throws ResourceException{
    try {
        ResourceCollectionFilter filter = new ResourceCollectionFilter(this.getRequest());
        List<Dictionary> dictionaries = getStore().getList(filter);
        int total = dictionaries.size();
        dictionaries = getStore().getPage(filter, dictionaries);
        trace(Level.FINE, "View available dictionaries");
        return new ListCollection<Dictionary>(dictionaries, total);
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

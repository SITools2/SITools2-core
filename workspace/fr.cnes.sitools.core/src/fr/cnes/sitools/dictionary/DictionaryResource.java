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
package fr.cnes.sitools.dictionary;

import java.util.HashMap;
import java.util.Map;
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

import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.dictionary.model.Dictionary;
import fr.cnes.sitools.notification.model.Notification;

/**
 * Class for dictionary management (GET, UPDATE, DELETE)
 * 
 * @author jp.boignard (AKKA Technologies)
 * 
 */
public final class DictionaryResource extends AbstractDictionaryResource {

  @Override
  public void sitoolsDescribe() {
    setName("DictionaryResource");
    setDescription("Resource for managing a dictionary - CRUD");
    setNegotiated(false);
  }

  /**
   * get dictionary
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
        Dictionary[] dictionary = getStore().getArray();
        trace(Level.FINE, "View available dictionaries");
        Response response = new Response(true, dictionary);
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
      getLogger().log(Level.SEVERE, null, e);
      throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
    }
  }

  @Override
  protected void describeGet(MethodInfo info) {

    // -> Global method info
    info.setIdentifier("get_dictionary");
    info.setDocumentation("Get a single dictionary from its ID");

    this.addStandardGetRequestInfo(info);
    ParameterInfo param = new ParameterInfo("dictionaryId", true, "xs:string", ParameterStyle.TEMPLATE,
        "Identifier of the dictionary to work with.");
    info.getRequest().getParameters().add(param);
    this.addStandardResponseInfo(info);
    this.addStandardInternalServerErrorInfo(info);

  }

  /**
   * Update existing dictionary
   * 
   * @param representation
   *          input
   * @param variant
   *          client preferred media type
   * @return Representation
   */
  @Put
  public Representation updateDictionary(Representation representation, Variant variant) {
    Dictionary dictionaryOutput = null;
    try {
      if (representation != null) {
        // Parse object representation
        Dictionary dictionaryInput = getObject(representation, variant);
        Dictionary oldDictionary = getStore().retrieve(dictionaryInput.getId());

        // Business service
        dictionaryOutput = getStore().update(dictionaryInput);

        Map<String, Dictionary> map = new HashMap<String, Dictionary>();
        map.put("oldDictionary", oldDictionary);
        map.put("newDictionary", dictionaryInput);

        Notification notification = new Notification();
        notification.setObservable(getDictionaryId());
        notification.setEvent("DICTIONARY_UPDATED");
        notification.setMessage("dictionary.delete.success");
        notification.setEventSource(map);

        getResponse().getAttributes().put(Notification.ATTRIBUTE, notification);
      }

      if (dictionaryOutput != null) {
        trace(Level.INFO, "Update information for the dictionary " + dictionaryOutput.getName());
        // Response
        Response response = new Response(true, dictionaryOutput, Dictionary.class, "dictionary");
        return getRepresentation(response, variant);

      }
      else {
        trace(Level.INFO, "Cannot update information for the dictionary - id: " + getDictionaryId());
        // Response
        Response response = new Response(false, "Can not validate dictionary");
        return getRepresentation(response, variant);

      }

    }
    catch (ResourceException e) {
      trace(Level.INFO, "Cannot update information for the dictionary - id: " + getDictionaryId());
      getLogger().log(Level.INFO, null, e);
      throw e;
    }
    catch (Exception e) {
      trace(Level.INFO, "Cannot update information for the dictionary - id: " + getDictionaryId());
      getLogger().log(Level.SEVERE, null, e);
      throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
    }
  }

  @Override
  protected void describePut(MethodInfo info) {

    // -> Global method info
    info.setIdentifier("modify_dictionary");
    info.setDocumentation("Method to modify an existing dictionary.");

    // -> Response info

    this.addStandardPostOrPutRequestInfo(info);
    ParameterInfo param = new ParameterInfo("dictionaryId", true, "xs:string", ParameterStyle.TEMPLATE,
        "Identifier of the dictionary to work with.");
    info.getRequest().getParameters().add(param);
    this.addStandardResponseInfo(info);

    // Failures
    this.addStandardInternalServerErrorInfo(info);

  }

  /**
   * Delete dictionary
   * 
   * @param variant
   *          client preferred media type
   * @return Representation
   */
  @Delete
  public Representation deleteDictionary(Variant variant) {
    try {
      Response response;
      Dictionary dictionaryToDelete = getStore().retrieve(getDictionaryId());
      if (dictionaryToDelete != null) {
        // Business service
        getStore().delete(getDictionaryId());

        // Response
        response = new Response(true, "dictionary.delete.success");

        // Notify observers
        Notification notification = new Notification();
        notification.setObservable(getDictionaryId());
        notification.setEvent("DICTIONARY_DELETED");
        notification.setMessage("dictionary.delete.success");
        notification.setStatus("DELETED");
        notification.setEventSource(dictionaryToDelete);
        getResponse().getAttributes().put(Notification.ATTRIBUTE, notification);

        trace(Level.INFO, "Delete the dictionary " + dictionaryToDelete.getName());

      }
      else {
        // Response
        response = new Response(false, "dictionary.delete.failure");
        trace(Level.INFO, "Delete the dictionary - id:" + getDictionaryId());
      }
      return getRepresentation(response, variant);

    }
    catch (ResourceException e) {
      trace(Level.INFO, "Delete the dictionary - id:" + getDictionaryId());
      getLogger().log(Level.INFO, null, e);
      throw e;
    }
    catch (Exception e) {
      trace(Level.INFO, "Delete the dictionary - id:" + getDictionaryId());
      getLogger().log(Level.SEVERE, null, e);
      throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
    }
  }

  @Override
  protected void describeDelete(MethodInfo info) {

    // -> Global method info
    info.setIdentifier("delete_dictionary");
    info.setDocumentation("Method to delete an existing dictionary.");

    this.addStandardGetRequestInfo(info);
    ParameterInfo param = new ParameterInfo("dictionaryId", true, "xs:string", ParameterStyle.TEMPLATE,
        "Identifier of the dictionary to work with.");
    info.getRequest().getParameters().add(param);
    this.addStandardSimpleResponseInfo(info);

    // Failures
    this.addStandardInternalServerErrorInfo(info);

  }

}

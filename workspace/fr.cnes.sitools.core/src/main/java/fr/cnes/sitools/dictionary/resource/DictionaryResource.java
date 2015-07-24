/*******************************************************************************
 * Copyright 2010-2014 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
 * <p/>
 * This file is part of SITools2.
 * <p/>
 * SITools2 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p/>
 * SITools2 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU General Public License
 * along with SITools2.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package fr.cnes.sitools.dictionary.resource;

import fr.cnes.sitools.dictionary.model.Dictionary;
import fr.cnes.sitools.notification.model.Notification;
import org.restlet.data.Status;
import org.restlet.ext.wadl.MethodInfo;
import org.restlet.ext.wadl.ParameterInfo;
import org.restlet.ext.wadl.ParameterStyle;
import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.Put;
import org.restlet.resource.ResourceException;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

/**
 * Class for dictionary management (GET, UPDATE, DELETE)
 *
 * @author jp.boignard (AKKA Technologies)
 */
public final class DictionaryResource extends AbstractDictionaryResource {

    /** Dictionary id in the request */
    private String dictionaryId = null;

    @Override
    public void doInit() {
        super.doInit();
        dictionaryId = getAttribute("dictionaryId");
    }

    @Override
    public void sitoolsDescribe() {
        setName("DictionaryResource");
        setDescription("Resource for managing a dictionary - CRUD");
    }

    /**
     * get dictionary
     */
    @Get
    public Dictionary retrieveDictionary() throws ResourceException {
        try {
            Dictionary dictionary = getStore().retrieve(dictionaryId);
            if (dictionary != null) {
                trace(Level.FINE, "Get information for the dictionary " + dictionary.getName());
            } else {
                trace(Level.FINE, "Get information for the dictionary " + dictionaryId);
                throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND, "Dictionary not found :" + dictionaryId);
            }
            return dictionary;
        } catch (ResourceException e) {
            trace(Level.INFO, "Cannot view available dictionaries");
            getLogger().log(Level.INFO, null, e);
            throw e;
        } catch (Exception e) {
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
     */
    @Put
    public Dictionary updateDictionary(Dictionary dictionary) throws ResourceException {
        Dictionary dictionaryOutput = null;
        try {
            if (dictionary != null) {
                // Parse object representation
                Dictionary oldDictionary = getStore().retrieve(dictionary.getId());

                // Business service
                dictionaryOutput = getStore().update(dictionary);

                Map<String, Dictionary> map = new HashMap<String, Dictionary>();
                map.put("oldDictionary", oldDictionary);
                map.put("newDictionary", dictionary);

                Notification notification = new Notification();
                notification.setObservable(dictionaryId);
                notification.setEvent("DICTIONARY_UPDATED");
                notification.setMessage("dictionary.delete.success");
                notification.setEventSource(map);

                getResponse().getAttributes().put(Notification.ATTRIBUTE, notification);
            }

            if (dictionaryOutput != null) {
                trace(Level.INFO, "Update information for the dictionary " + dictionaryOutput.getName());
                return dictionaryOutput;

            } else {
                trace(Level.INFO, "Cannot update information for the dictionary - id: " + dictionaryId);
                throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
            }

        } catch (ResourceException e) {
            trace(Level.INFO, "Cannot update information for the dictionary - id: " + dictionaryId);
            getLogger().log(Level.INFO, null, e);
            throw e;
        } catch (Exception e) {
            trace(Level.INFO, "Cannot update information for the dictionary - id: " + dictionaryId);
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
     */
    @Delete
    public void deleteDictionary() throws ResourceException {
        try {
            Dictionary dictionaryToDelete = getStore().retrieve(dictionaryId);
            if (dictionaryToDelete != null) {
                // Business service
                getStore().delete(dictionaryId);

                // Notify observers
                Notification notification = new Notification();
                notification.setObservable(dictionaryId);
                notification.setEvent("DICTIONARY_DELETED");
                notification.setMessage("dictionary.delete.success");
                notification.setStatus("DELETED");
                notification.setEventSource(dictionaryToDelete);
                getResponse().getAttributes().put(Notification.ATTRIBUTE, notification);

                trace(Level.INFO, "Delete the dictionary " + dictionaryToDelete.getName());
                getResponse().setStatus(Status.SUCCESS_NO_CONTENT);

            } else {
                String msg = "Error Delete the dictionary - not found id:" + dictionaryId;
                trace(Level.INFO, msg);
                throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND, msg);
            }

        } catch (ResourceException e) {
            trace(Level.INFO, "Delete the dictionary - id:" + dictionaryId);
            getLogger().log(Level.INFO, null, e);
            throw e;
        } catch (Exception e) {
            trace(Level.INFO, "Delete the dictionary - id:" + dictionaryId);
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
